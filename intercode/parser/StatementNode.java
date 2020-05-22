package intercode.parser;
import intercode.visitor.*;

public class StatementNode extends Node{

    public AssignmentNode assign;
    public IfNode ifCondition;
    public ForNode forStmt;

    public StatementNode(){

    }

    public StatementNode(AssignmentNode assign,IfNode f,ForNode y)
    {
        this.assign = assign ;
        this.ifCondition =f;
        this.forStmt =y;
    }

    public void accept(ASTVisitor v) {
        v.visit(this);
    }
}
