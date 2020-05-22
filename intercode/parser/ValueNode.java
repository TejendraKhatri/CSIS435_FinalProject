package intercode.parser ;
import intercode.visitor.* ;
import intercode.lexer.*;

public class ValueNode extends Node {

    public LiteralNode value  ;

    public ValueNode () {

    }
    
    public ValueNode (LiteralNode value) {

        this.value  = value  ;
    }

    public ValueNode (String valxue) {

        this.value.literal  = valxue  ;
    }

    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}
