package intercode.visitor ;

import java.util.Iterator;

import intercode.parser.* ;
import intercode.lexer.*;

public class ASTVisitor {

    public void visit (CompilationUnit n) {
        n.block.accept(this) ;
    }

    public void visit (BlockNode n) {
    }

    public void visit (DeclarationNode n) {
        n.type.accept(n.type.type.lexeme) ;
        n.id.accept(this) ;
    }

    public void visit (TypeNode n) {
        n.accept(n.type.lexeme);
    }


    public void visit (StatementNode n)
    {
        n.assign.accept(this);
    }

    public void visit (AssignmentNode n) {
        n.left.accept(this) ;
       // n.right.accept(this) ;
    }

    public void visit (BinaryExpressionNode n) {

    }

    public void visit (LogicalExpressionNode n) {

    }

    public void visit (LiteralNode n) {
       n.store(n.literal);
    }

    public void visit (IfNode n) {
    }

    public void visit (ForNode n) {
    }

    public void visit (ForUpdateExpressionNode n) {
    }

    public void visit (ExpressionNode n) {
    }

    public void visit (ValueNode n)
    {
        n.value.accept(this);
    }
}
