package intercode.parser ;

import intercode.visitor.* ;

public class LogicalExpressionNode extends Node {
    public LiteralNode left  ;
    public LiteralNode right ;
    public String operation;

    public LogicalExpressionNode () {

    }
    
    public LogicalExpressionNode (LiteralNode left, LiteralNode right, String op) {
        this.left  = left  ;
        this.right = right ;
        this.operation = op;
    }

    public void accept(ASTVisitor v) {
        v.visit(this);
    }
}
