package intercode.parser ;
import intercode.visitor.* ;
import intercode.lexer.*;

public class TypeNode extends Node {

    public Type type;

    public TypeNode () {

    }
    
    public TypeNode (Type type) {

        this.type = type;
    }

    public void accept(String v) {

        type.lexeme = v;
    }
}
