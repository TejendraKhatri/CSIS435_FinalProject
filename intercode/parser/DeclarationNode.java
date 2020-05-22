package intercode.parser;
import intercode.visitor.*;

public class DeclarationNode extends Node{

    public LiteralNode id;
    public TypeNode type;

    public DeclarationNode(){

    }

    public DeclarationNode(TypeNode type, LiteralNode id) {

        this.type = type ;
        this.id = id;
    }

    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}
