package intercode.lexer ;

public class Word extends Token {
    
    public String lexeme = "" ;
   
    public static final Word True  = new Word("true",  Tag.TRUE) ;
    public static final Word False = new Word("false", Tag.FALSE) ;

    public Word (String s, int tag) { 
        
        super(tag) ; 
        lexeme = s ;
    }

    public String toString() { 
        return lexeme ; 
    }
}
