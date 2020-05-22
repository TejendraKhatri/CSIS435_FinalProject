package intercode.parser;

import intercode.visitor.* ;
import intercode.lexer.*;

public class ExpressionNode extends Node {
    public BinaryExpressionNode expRight;
    public ValueNode valNode;
    public LogicalExpressionNode logicExpression;
    public Token[] tokenArray;
    
    public ExpressionNode () {
        
    }

    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}
