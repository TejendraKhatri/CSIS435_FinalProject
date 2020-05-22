package intercode.parser;

import intercode.parser.StatementNode;
import intercode.visitor.* ;

public class ForNode extends Node {
    public BlockNode forBlock;
    public LogicalExpressionNode logicalExpr;
    public AssignmentNode initializer;
    public ForUpdateExpressionNode updater;
    public ForNode () { 
        
    }

    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}
