package intercode.intercodegen;
import intercode.parser.*;
import intercode.visitor.* ;
import intercode.lexer.* ;
import java.io.* ;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class Intercode extends ASTVisitor{
    public Parser parser = null;
    public CompilationUnit cu = null;

    public Intercode(Parser parser)
    {
        this.parser = parser;
        cu = parser.cu;
        visit(cu);
    }
    
    public void visit (CompilationUnit n) {
        visit(n.block);
    }

    public void visit(BlockNode d){
        for (int i = 0; i < d.stmts.length;  i ++)
            if (d.stmts[i] != null)
            {visit(d.stmts[i]);}      
    }

    public void visit(DeclarationNode d){
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
        visit(a.left);
        visit(a.expRight);  
    }

    public void visit(IfNode i)
    {
        visit(i.logicalExpr);
        visit(i.ifBlock);
        if(i.elseBlock != null)
        {
            i.gotoBlock = new BlockNode();
            i.gotoBlock = i.elseBlock;
            visit(i.gotoBlock);
        }
    }

    public void visit(LogicalExpressionNode l)
    {
        visit(l.left);
         visit(l.right);
    }

    public void visit(AssignmentNode a){
        visit(a.left);
        visit(a.expressionNode);
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

    public void visit(BinaryExpressionNode a){
    }

    public void visit(ValueNode a){
    }

    public void visit(TypeNode x){
    }

    public void visit(LiteralNode l){
    }
}