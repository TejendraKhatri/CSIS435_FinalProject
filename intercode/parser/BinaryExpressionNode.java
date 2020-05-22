package intercode.parser ;

import intercode.visitor.* ;
import intercode.lexer.*;

public class BinaryExpressionNode extends Node {
    public String tempStorage;
    public LiteralNode left  ;
    public LiteralNode right ;
    public char operation;
    public Token[] postfixExpression;

    public BinaryExpressionNode () {

    }
    
    public BinaryExpressionNode (LiteralNode left, LiteralNode right, char op) {
        this.left  = left  ;
        this.right = right ;
        this.operation = op;
    }

    public BinaryExpressionNode (String left, String right, char op) {
        this.left.literal = left  ;
        this.right.literal = right ;
        this.operation = op;
    }

    public void accept(ASTVisitor v) {
        v.visit(this);
    }
}
 