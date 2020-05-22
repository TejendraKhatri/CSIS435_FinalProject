package intercode ;

import java.io.IOException;

import intercode.lexer.* ;
import intercode.parser.* ;
import intercode.unparser.*;
import intercode.typechecker.*;
import intercode.intercodegen.*;
    
public class Main {

    public static void main (String[] args) {
        Lexer lexer = new Lexer() ;
        Parser parser = new Parser(lexer) ;
        TypeChecker typeCheck = new TypeChecker(parser);
        Intercode intercodeASTGenerator = new Intercode(parser);
        Unparser unpretty = new Unparser(parser);  
    }
}
