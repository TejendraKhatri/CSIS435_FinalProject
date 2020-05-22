package intercode.lexer;

import intercode.visitor.ASTVisitor;

public class Type extends Word{
    public int width = 0;

    public static final Type
      Int   = new Type( "int",   Tag.BASIC, 4 ),
      Float = new Type( "float", Tag.BASIC, 8 ),
      Char  = new Type( "char",  Tag.BASIC, 1 ),
      Boolean  = new Type( "boolean",  Tag.BASIC, 1 );

    public Type(String s, int tag,int w)
    {
        super(s,tag);width = w;
    }
}
