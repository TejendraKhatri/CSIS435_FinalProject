package intercode.parser ;

import intercode.visitor.* ;

public class IfNode extends Node {
    public BlockNode ifBlock;
    public LogicalExpressionNode logicalExpr;
    public BlockNode elseBlock;
    public BlockNode gotoBlock;
    public IfNode () {
        
    }

    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}
