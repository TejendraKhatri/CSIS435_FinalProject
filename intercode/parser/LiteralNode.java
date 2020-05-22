package intercode.parser ;

import intercode.lexer.*;
import intercode.visitor.* ;

public class LiteralNode extends Node {

    public String literal ;

    public LiteralNode () {

    }
    
    public LiteralNode (String literal) {

        this.literal = literal ;
    }

    public LiteralNode (Word w) {

        this.literal = w.lexeme;
    }

    public void accept(ASTVisitor v) {
       // System.out.println("OUUUTin accept of litN");
        v.visit(this);
    }

    public void store(String n) {

        this.literal = n;
    }

    void printNode () {

        System.out.println("LiteralNode: " + literal) ;
    }
}
