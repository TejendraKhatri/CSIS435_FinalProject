package intercode.typechecker;
import intercode.parser.*;
import intercode.visitor.* ;
import intercode.lexer.* ;
import java.io.* ;


public class TypeChecker extends ASTVisitor{
    public Parser parser = null;
    public Lexer lexer = null ;  
    public CompilationUnit cu = null;
    public Env top = null;
    public Type rhsExp = null;
    //////////////////
    public boolean hasbeenInitialized = false;
    public String[] initilizedTokens = new String[1000];
    /////////////////
    public TypeChecker(Parser parser)
    {
            this.parser = parser;
            cu = parser.cu;
            visit(cu);
    }

    void error(String s)
    {
        throw new Error(s);
    }

    boolean isDigit(final String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    boolean isFloat(final String str) {
        int numDec = 0;
        for (char c : str.toCharArray()) {
            if(c=='.')
                numDec+=1;
            else if (!Character.isDigit(c) && numDec==0 || numDec>1) {
                return false;
            }  
        }
        return true;
    }

    public void getType(LiteralNode a){
        String x = a.literal;
        if(isDigit(x))
        {
            rhsExp =  Type.Int;
            hasbeenInitialized = true; /////////////////////////////////////
        }
        else if(isFloat(x))
        {
            rhsExp =  Type.Float;
            hasbeenInitialized = true;/////////////////////////////////////
        }
        else if(x.equals("true") || x.equals("false"))
        {
            rhsExp = Type.Boolean;
        }
        else if(top.table.get(a.literal) == null)
        {
            error(  a.literal + " variable not declared!");
        } 
        else
        {
            rhsExp = top.table.get(x);
        }
    }

    public boolean checkOperator(String x)
    {
        if(x.equals("+") || x.equals("-") || x.equals("*") || x.equals("/") || x.equals("(") || x.equals(")"))
        {return true;}
        return false;
    }

    public void visit (CompilationUnit n) {
        top = new Env();
        top.table = n.symbolTable.table;
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

    public void initializeLeft(String s)
    {
        int tokenArraySize=0;
        for (int i = 0; i < initilizedTokens.length; i ++)
                if (initilizedTokens[i] != null)
                    tokenArraySize ++;
        initilizedTokens[tokenArraySize] = s;
    }

    public boolean checkInitialization(String s)
    {
        String temp = s;
        if(!(isDigit(temp) || isFloat(temp) || temp == "false" || temp == "true"))
        {
            for (int i = 0; i < initilizedTokens.length; i ++){
                if (initilizedTokens[i] != null)
                {
                    if(initilizedTokens[i] == s)
                        return true;
                }
            }
        }
        else{
            return true;
        }
        return false;
    }

    public void visit(AssignmentNode a){
        Type left;
        if(!(top.table.containsKey(a.left.literal)))
        {
            error("Variable " + a.left.literal +" has not been declared.");
        }
        left = top.table.get(a.left.literal);
        visit(a.expressionNode);
        /////////////////////////////////////
        if( hasbeenInitialized )
        {
            initializeLeft(a.left.literal);
            hasbeenInitialized = false;
        }
        /////////////////////////////////////
        if(left==Type.Int && rhsExp == Type.Float)
        {
            top.table.put(a.left.literal,Type.Int);
        }
        else if(left==Type.Boolean && rhsExp != Type.Boolean)
        {
            error("Incompatible types assignment: "+left.toString()+" & " +rhsExp.toString());
        }
        else if((left==Type.Int || left ==Type.Float) && rhsExp == Type.Boolean)
        {
            error("Incompatible types assignment: "+left.toString()+" & " +rhsExp.toString());
        }
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
            visit(e.valNode);
        }
        if(size >= 3)
        {
           visit(e.expRight);
        }
    }

    public void visit(ValueNode a){
        ////////////////////
        String temp = a.value.literal;
        if(!checkInitialization(temp))
        {
            error( temp + " has not been initialized.");
        }
        /////////////////
        getType(a.value);
    } 

    public void visit(BinaryExpressionNode a){
        Type left,right;
        if(a.postfixExpression == null)
        {
            getType(a.left);
            ////////////////
            if(!checkInitialization(a.left.literal))
            {
                error( a.left.literal + " has not been initialized.");
            }
            ///////////////////
            left = rhsExp;
            getType(a.right);
            ////////////////
            if(!checkInitialization(a.right.literal))
            {
                error( a.right.literal + " has not been initialized.");
            }
            ///////////////////
            right = rhsExp;
            if(left == Type.Boolean || right == Type.Boolean)
            {
                error(left.toString() +" "+ a.operation +" "+ right.toString() +" is not allowed." );
            }
            else if(left == Type.Int && right == Type.Float)
            {
                rhsExp = Type.Float;
            }
            else if(left == Type.Float && right == Type.Int)
            {
                rhsExp = Type.Float;
            }
            else if(left == Type.Float && right == Type.Float)
            {
                rhsExp = Type.Float;
            }
            else if(left == Type.Int && right == Type.Int)
            {
                rhsExp = Type.Int;
            }
        }
        else
        {
            boolean presenceOfFloat = false;
            for(int i=0;i<(a.postfixExpression).length;i++)
            {
                if(a.postfixExpression[i]!=null)
                {
                    String dummy = a.postfixExpression[i].toString();
                    if(!checkOperator(dummy))
                    {
                        ////////////////
                        if(!checkInitialization(dummy))
                        {
                            error( dummy + " has not been initialized.");
                        }
                        ///////////////////
                        
                        if(dummy.equals("true") || dummy.equals("false"))
                        {
                            error("+ - * / cannot be done on boolean:: "+ dummy);
                        }
                        if(!(isFloat(dummy) || isDigit(dummy)))
                        {
                            if(!(top.table.containsKey(dummy)))
                            {
                                error("Variable " + dummy +" has not been declared.");
                            }
                        }
                        if(top.table.get(dummy) == Type.Boolean)
                        {
                            error("+ - * / cannot be done on boolean: "+ dummy);
                        }
                        if(top.table.get(dummy) == Type.Float)
                        {
                            presenceOfFloat = true;
                        }
                        
                    }

                }
            }
            if(presenceOfFloat) rhsExp = Type.Float;
            else rhsExp = Type.Int;
        }
    }

    public void visit(IfNode i)
    {
        visit(i.logicalExpr);
        visit(i.ifBlock);
        if(i.elseBlock != null)
        {
            visit(i.elseBlock);
        }
    }

    public void visit(LogicalExpressionNode l)
    {
        Type left,right;
        getType(l.left); left = rhsExp;
        ////////////////
        if(!checkInitialization(l.left.literal))
        {
            error(l.left.literal + " has not been initialized.");
        }
        ///////////////////
        getType(l.right); right = rhsExp;
         ////////////////
         if(!checkInitialization(l.right.literal))
         {
             error(l.right.literal + " has not been initialized.");
         }
         ///////////////////
        if(left == Type.Boolean || right == Type.Boolean)
        {
            error("relational operators don't work on boolean operands: " + l.left.literal +" and "+l.right.literal);
        }
    }   

    public void visit(ForNode f)
    {
        if(f.initializer!=null)
        {
            visit(f.initializer); 
            visit(f.logicalExpr);
            visit(f.updater);
        }
        visit(f.forBlock); 
    }

    public void visit(ForUpdateExpressionNode a)
    {
        Type aleft,left,right,expType;
        expType = null;
        if(!(top.table.containsKey(a.left.literal)))
        {
            error("Variable " + a.left.literal +" has not been declared.");
        }
        getType(a.left); aleft = rhsExp;
        getType(a.expRight.left); left = rhsExp;
        ////////////////
        if(!checkInitialization(a.expRight.left.literal))
        {
            error(a.expRight.left.literal + " has not been initialized.");
        }
        ///////////////////
        getType(a.expRight.right); right = rhsExp;
        ////////////////
        if(!checkInitialization(a.expRight.right.literal))
        {
            error( a.expRight.right.literal + " has not been initialized.");
        }
        ///////////////////


        if(left == Type.Boolean || right == Type.Boolean)
        {
            error(left.toString() +" "+ a.expRight.operation +" "+ right.toString() +" is not allowed." );
        }
        else if(left == Type.Int && right == Type.Float)
        {
            expType = Type.Float;
        }
        else if(left == Type.Float && right == Type.Int)
        {
            expType = Type.Float;
        }
        else if(left == Type.Float && right == Type.Float)
        {
            expType = Type.Float;
        }
        else if(left == Type.Int && right == Type.Int)
        {
            expType = Type.Int;
        }


        if(aleft==Type.Int && expType == Type.Float)
        {
            top.table.put(a.left.literal,Type.Int);
        }
        else if(aleft==Type.Boolean && expType != Type.Boolean)
        {
            error("Incompatible types assignment: "+left.toString()+" & " +expType.toString());
        }
        else if((left==Type.Int || left ==Type.Float) && expType == Type.Boolean)
        {
            error("Incompatible types assignment: "+left.toString()+" & " +expType.toString());
        }
    }

}
