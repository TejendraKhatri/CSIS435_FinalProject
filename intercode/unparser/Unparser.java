package intercode.unparser;
import intercode.parser.*;
import intercode.visitor.* ;
import intercode.lexer.* ;
import java.io.* ;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;



public class Unparser extends ASTVisitor{
    public Parser parser = null;
    public CompilationUnit cu = null;
    public BufferedWriter out;
    public int lNum=1;
    public int tNum=1;
    public String leftVal = "";
    public Token[] tempExpression;
    public String l,op,r;
    public int posOfOp;
   

    public Unparser(Parser parser)
    {
        try
        {
            out = new BufferedWriter(new FileWriter("output.txt"));
            this.parser = parser;
            cu = parser.cu;
            visit(cu);
            out.write("L"+lNum+": \n");
            out.close();
            String temp = "";
            temp = new String(Files.readAllBytes(Paths.get("output.txt")));
            System.out.print(temp);
        }
            catch(IOException e){ System.out.println("Error while writing to file");}
    }

    public void visit (CompilationUnit n) {

        visit(n.block);
    }

    public void visit(BlockNode d){
        for (int i = 0; i < d.stmts.length;  i ++)
            if (d.stmts[i] != null)
            {visit(d.stmts[i]);}
    }
 
    public void visit(StatementNode d){
        if(d.assign!=null)
        {
            visit(d.assign);
        }
        else if(d.ifCondition!=null)
        {
            visit(d.ifCondition);
        }
        else if(d.forStmt!=null)
        {
            visit(d.forStmt);
        }
    }

    public void adjustText()
    {
        try {
            out.close();
            String temp = "";
            temp = new String(Files.readAllBytes(Paths.get("output.txt")));
            temp = temp.replaceFirst("~", Integer.toString(lNum));
            out = new BufferedWriter(new FileWriter("output.txt"));
            out.write(temp);
        } catch(IOException e){ System.out.println("Error while replacing line num");}
    
    }

    public void visit(IfNode i)
    {
        try
        {
            out.write("L"+lNum + ": ");
            out.write("ifFalse ");
            visit(i.logicalExpr);
            out.write(" goto L"+ "~\n");
            lNum++;
            visit(i.ifBlock);
            adjustText();
            if(i.gotoBlock != null)
            {
                visit(i.gotoBlock);
            }
        }
        catch(IOException e){ System.out.println("Error while writing to file in IfNode");}
    }

    public void visit(LogicalExpressionNode l)
    {
        try
        {
            visit(l.left);
            out.write(" "+l.operation+" ");
            visit(l.right);
        }
        catch(IOException e){ System.out.println("Error while writing to file in LogicalExpressionNode");}
    }

    public void visit(AssignmentNode a){
        try {
            out.write("L"+lNum + ": ");
            leftVal = a.left.literal;
            visit(a.expressionNode);
            lNum++;
            out.write("\n");
        } catch(IOException e)
        { System.out.println("Error while writing to file in AssignmentNode");}
    }

    public void visit(ForNode f)
    {
        int curLnum = lNum;
        try{
            if(f.initializer!=null)
            {
                visit(f.initializer);
                out.write("L"+lNum + ": ");
                curLnum = lNum;
                out.write("ifFalse ");
                visit(f.logicalExpr);
                out.write(" goto L"+ "~\n");
                lNum++;
                visit(f.forBlock);
                out.write("L"+lNum + ": ");
                lNum++;
                visit(f.updater);
            }
            else
            {
                visit(f.forBlock);
            }
            out.write("L"+lNum + ": ");
            out.write("goto L"+curLnum+"\n");
            lNum++;
            adjustText();
        }
        catch(IOException e){ System.out.println("Error while writing to file in ForNode");}
    }

    public void visit(ForUpdateExpressionNode a)
    {
       try {
            out.write("t"+tNum+" = ");
            visit(a.expRight.left);
            out.write(" "+a.expRight.operation+" ");
            visit(a.expRight.right);
            out.write("\n");
            tNum++;
            out.write("    ");
            visit(a.left);
            out.write(" = t"+ (tNum-1)+"\n");
       } catch (IOException e){ System.out.println("Error while writing to file in ForUpdateExpressionNode");}
    }


    public void visit(ExpressionNode e)
    {
        int size = 0;
        for(int i=0;i<e.tokenArray.length;i++)
        {
            if(e.tokenArray[i]!=null)
            {
                size++;
            }
        }
        if(size== 1)
        {   
            try{
                out.write(leftVal + " = ");
                visit(e.valNode);
            }
            catch(IOException ef)
            { System.out.println("Error while writing to file.");}
            
        }
        if(size >= 3)
        {
            try{
                visit(e.expRight);
                out.write("    "+leftVal+" = t"+ (tNum-1));
            }
           catch(IOException ef)
           { System.out.println("Error while writing to file.");}
        }
    }

    public void visit(BinaryExpressionNode a){
        if(a.postfixExpression == null)
        {
            try {
                out.write("t"+tNum+" = ");
                visit(a.left);
                out.write(" "+ a.operation+ " ");
                visit(a.right);
                out.write("\n");
                tNum++;
            
            } catch(IOException e)
            { System.out.println("Error while writing to file in BinaryExpressionNode");}
        }
        else
        {
            try{
                tempExpression = a.postfixExpression;
                int opNums = countOperators();
                for(int i=1;i<=opNums;i++)
                {
                    getOperands();
                    out.write("    t"+tNum+" = ");
                    out.write(l+" "+op + " "+r+"\n");
                    tNum++;
                }
            }catch(IOException e)
            { System.out.println("Error while writing to file in BinaryExpressionNode2");}
        }
    }
    
    public int countOperators()
    {
        int ct = 0;
        for(int i=0;i<tempExpression.length;i++)
        {
            if(tempExpression[i]!= null){
                char op = (tempExpression[i].toString()).charAt(0);
                if(checkOperator(op))
                {
                    ct++;
                }
            }
        }
        return ct;
    }

    public void getOperands()
    {
        int y = tempExpression.length;
        for(int i=0;i<y;i++)
        {
            if(tempExpression[i]!=null)
            {
                char opI = (tempExpression[i].toString()).charAt(0);
                if(checkOperator(opI))
                {
                    posOfOp = i;
                    l = tempExpression[i-2].toString();
                    r = tempExpression[i-1].toString();
                    op = tempExpression[i].toString();
                    clean();
                    break;
                }
            }
        }
    }

    public void clean()
    {
        Token[] temp = new Token[100];
        if(posOfOp == 2)
        {
            int ct = 1;
            for(int i = 3;i<tempExpression.length;i++)
            {
                if(tempExpression[i]!=null)
                {
                    temp[ct] = tempExpression[i];
                    ct++;
                }
            }
            Word x = new Word("t"+tNum, Tag.ID);
            temp[0] = x;
        }
        else
        {
            int ct = posOfOp-2;
            for(int i = 0;i<(posOfOp-2);i++)
            {
                temp[i] = tempExpression[i];
            }
            for(int i = posOfOp;i<tempExpression.length;i++)
            {
                if(tempExpression[i]!=null)
                {
                    temp[ct] = tempExpression[i];
                    ct++;
                }
            }
            Word x = new Word("t"+tNum, Tag.ID);
            temp[posOfOp-2] = x;
        }
       
        tempExpression = new Token[100];
        for(int i = 0;i<temp.length;i++)
        {
            if(temp[i]!=null)
            {
                tempExpression[i] = temp[i];
            }
        }
    }

    public boolean checkOperator(char x)
    {
        if(x == '+' || x == '-' || x == '/' || x == '*' || x == '(' || x == ')')
        {return true;}
        return false;
    }
 
    public void visit(ValueNode a){
        visit(a.value);
    }

    public void visit(LiteralNode l){
        try {
            out.write(l.literal);
        } catch(IOException e)
      { System.out.println("Error while writing to file in LiteralNode");}
      
    }
}
