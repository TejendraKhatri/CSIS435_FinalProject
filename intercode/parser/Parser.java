package intercode.parser ;

import intercode.visitor.* ;
import intercode.lexer.* ;

import java.io.* ;

import javax.lang.model.util.ElementScanner6;

public class Parser extends ASTVisitor {

    public CompilationUnit cu = null ;
    public Lexer lexer        = null ;    
    public Token lookAhead    = null;
    public LiteralNode x,y,z;
    private char op;
    public String origExpression= "";
    public int tokenArraySize;
    public Token[] tempArray;
    public Env top = null;

    public Parser (Lexer lexer) { 

        this.lexer = lexer ;
        readLexem();
        cu = new CompilationUnit() ;
        visit(cu) ;
    }

    void error(String s)
    {
        throw new Error("near line  "+lexer.line+": "+ s);
    }

    void match(int t)
    {
        if(lookAhead.tag==t)
        {
            readLexem();
        }
        else
            error("syntax error");
    }

    void readLexem () {
        try {
            
            lookAhead = lexer.scan() ;
             if((lookAhead.tag == 13))
            {
                readLexem();
            } 
        }
        catch (IOException e) {

            System.out.println("IO Error") ;
        }
    }

    public void visit (CompilationUnit n) {
        n.block = new BlockNode() ;
        top = new Env();
        n.block.accept(this);
        n.symbolTable = top;
    }

    public void visit(BlockNode n){
       match('{');
       while(lookAhead.tag == Tag.BASIC){
           DeclarationNode decl = new DeclarationNode();
           decl.accept(this);
           if(top.table.containsKey(decl.id.literal))
           {
               error("variable name has already been used.");
           }
           top.put(decl.id.literal,decl.type.type);
           
           int counter = 0;
           for (int i = 0; i < n.decls.length; i ++)
               if (n.decls[i] != null)
                   counter ++;
           n.decls[counter] = decl;
       }
       while(lookAhead.tag == Tag.ID || lookAhead.tag == Tag.IF || lookAhead.tag == Tag.FOR){
            StatementNode st = new StatementNode();
            st.accept(this);
            int counter = 0;
            for (int i = 0; i < n.stmts.length; i ++)
                if (n.stmts[i] != null)
                    counter ++;
            n.stmts[counter] = st;
       }
       match('}');
    }

    public void visit(DeclarationNode d){
        d.type = new TypeNode((Type) lookAhead);
        match(Tag.BASIC);
        d.type.accept(d.type.type.lexeme);
        d.id = new LiteralNode();
        x = new LiteralNode(lookAhead.toString());
        match(Tag.ID);
        d.id.store(x.literal);
        match(';');
    }

    public void visit(StatementNode s){
        if(lookAhead.tag == Tag.ID)
        {
            s.assign = new AssignmentNode();
            s.assign.accept(this);
        }
        else if(lookAhead.tag == Tag.IF)
        {
            s.ifCondition = new IfNode();
            s.ifCondition.accept(this);
        }
        else if(lookAhead.tag == Tag.FOR)
        {
            s.forStmt = new ForNode();
            s.forStmt.accept(this);
        }
    }


    public boolean isNumeric(String s) {
		if (s == null || s.equals("")) {
			return false;
		}

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
    }

    public boolean isFloat(final String str) {
        int numDec = 0;
        for (char c : str.toCharArray()) {
            if(c=='.')
                numDec+=1;
            else if (!Character.isDigit(c) && numDec==0 || numDec>1) {
                return false;
            }  
        }
        return true;
    }
    
    public void visit(AssignmentNode a){
        a.left = new LiteralNode();
        x = new LiteralNode(lookAhead.toString());
        match(Tag.ID);
        a.left.store(x.literal);
        Token t = new Token('=');
        match(t.tag);
        a.expressionNode = new ExpressionNode();
        a.expressionNode.accept(this);
    }

    public void visit(ExpressionNode a)
    {   
        tokenArraySize = 0 ;
        a.tokenArray = new Token[100];
        tempArray = new Token[100];
        for (int i = 0; i < tempArray.length; i ++)
                if (tempArray[i] != null)
                    tokenArraySize ++;
        origExpression ="";
        tmpPrecedence();
        for (int i = 0; i < tempArray.length; i ++)
            if (tempArray[i] != null)
            {
                a.tokenArray[i] = tempArray[i];
            }
        if(tokenArraySize == 1)
        {
                LiteralNode l = new LiteralNode((a.tokenArray[0]).toString());
                a.valNode = new ValueNode(l);
        }
        else if(tokenArraySize == 3)
        {
            LiteralNode x = new LiteralNode((a.tokenArray[0]).toString());
            LiteralNode y = new LiteralNode((a.tokenArray[1]).toString());
            char z = ((a.tokenArray[2]).toString()).charAt(0);
            a.expRight = new BinaryExpressionNode(x,y,z);

        }
        else if(tokenArraySize > 3)
        {
            a.expRight = new BinaryExpressionNode();
            a.expRight.postfixExpression = new Token[100];
            for (int i = 0; i < tempArray.length; i ++)
            if (tempArray[i] != null)
            {
                a.expRight.postfixExpression[i] = tempArray[i];
                a.expRight.tempStorage = origExpression;
            }
        }
        else error("syntax error");
        match(';');
    }

    public void visit(IfNode i)
    {
        match(Tag.IF);
        match('(');
        i.logicalExpr = new LogicalExpressionNode();
        i.logicalExpr.accept(this);
        match(')');
        i.ifBlock = new BlockNode();
        i.ifBlock.accept(this);
        if(lookAhead.tag == Tag.ELSE)
        {
            match(Tag.ELSE);
            i.elseBlock = new BlockNode();
            i.elseBlock.accept(this);
        }
    }

    public void visit(ForNode f)
    {
        match(Tag.FOR);
        match('(');
        if(lookAhead.toString().charAt(0) == ';' )
        {
            match(';');
            match(';');
        }
        else
        {
            f.initializer = new AssignmentNode();
            f.initializer.accept(this);
            f.logicalExpr = new LogicalExpressionNode();
            f.logicalExpr.accept(this);
            match(';');
            f.updater =new ForUpdateExpressionNode();
            f.updater.accept(this);
        }
        match(')');
        f.forBlock = new BlockNode();
        f.forBlock.accept(this);
    }

    public void visit(ValueNode n) {
        n.value = new LiteralNode() ;
        n.value.store(x.literal);
    }

    public void visit (LogicalExpressionNode n) {
        x = new LiteralNode(lookAhead.toString());
        if(isNumeric(lookAhead.toString())){
            match(Tag.NUM);
        }
        else if(isFloat(lookAhead.toString())){
            match(Tag.REAL);
        }
        else match(Tag.ID);
        n.left = new LiteralNode() ;
        n.left.store(x.literal);
        n.operation = lookAhead.toString();
        match(Tag.RELATIONAL);
        n.right = new LiteralNode();
        x = new LiteralNode(lookAhead.toString());
        if(isNumeric(lookAhead.toString())){
            match(Tag.NUM);
        }
        else if(isFloat(lookAhead.toString())){
            match(Tag.REAL);
        }
        else match(Tag.ID);
        n.right.store(x.literal) ;
    }
    
    public void visit (BinaryExpressionNode n) {
        match(op);
        z = new LiteralNode(lookAhead.toString());
        if(isNumeric(lookAhead.toString())){
            match(Tag.NUM);
        }
        else if(isFloat(lookAhead.toString())){
            match(Tag.REAL);
        }
        else match(Tag.ID);
        char op2 = lookAhead.toString().charAt(0);
        if(op2 == '+' || op2 == '-' || op2 == '*' || op2 == '/')
        {
            tmpPrecedence();
        }
        else
        {
            n.left = new LiteralNode() ;
            n.left.store(x.literal);
            n.operation = op;
            n.right = new LiteralNode();
            x = new LiteralNode(z.literal);
            n.right.store(x.literal);
        }
        
    }

    public void visit (ForUpdateExpressionNode a){
        a.left = new LiteralNode();
        x = new LiteralNode(lookAhead.toString());
        match(Tag.ID);
        a.left.store(x.literal);
        Token t = new Token('=');
        match(t.tag);
        a.expRight = new BinaryExpressionNode();
        x = new LiteralNode(lookAhead.toString());
        if(isNumeric(lookAhead.toString())){
            match(Tag.NUM);
        }
        else if(isFloat(lookAhead.toString())){
            match(Tag.REAL);
        }
        else match(Tag.ID);
        a.expRight.left = new LiteralNode();
        a.expRight.left.store(x.literal);
        a.expRight.operation = lookAhead.toString().charAt(0);
        readLexem();
        a.expRight.right = new LiteralNode();
        x = new LiteralNode(lookAhead.toString());
        if(isNumeric(lookAhead.toString())){
            match(Tag.NUM);
        }
        else if(isFloat(lookAhead.toString())){
            match(Tag.REAL);
        }
        else match(Tag.ID);
        a.expRight.right.store(x.literal) ;
    }

    public void visit (LiteralNode n) {
        n.accept(this);
    }

    void tmpPrecedence() {
        tmpPrecedence1() ;
        while(true) {
            if (lookAhead.toString().charAt(0) == '+') {
                origExpression+=lookAhead.toString();
                match('+') ;
                tmpPrecedence1() ;
                tempArray[tokenArraySize++] = new Token('+');
            } else if (lookAhead.toString().charAt(0) == '-') {
                origExpression+=lookAhead.toString();
                match('-') ;
                tmpPrecedence1() ;
                tempArray[tokenArraySize++] = new Token('-');
            } else {
                return ;
            }
        }
    }

    void tmpPrecedence1(){
        tmpPrecedence2() ;
        while(true) {
            if (lookAhead.toString().charAt(0) == '*') {
                origExpression+=lookAhead.toString();
                match('*') ;
                tmpPrecedence2() ;
                tempArray[tokenArraySize++] = new Token('*');
            } else if (lookAhead.toString().charAt(0) == '/') {
                origExpression+=lookAhead.toString();
                match('/') ;
                tmpPrecedence2() ;
                tempArray[tokenArraySize++] = new Token('/');
            } else {
                return ;
            }
        }
    }

    void tmpPrecedence2(){
        if (lookAhead.tag == Tag.NUM) {
            origExpression+=lookAhead.toString();
            tempArray[tokenArraySize++] = lookAhead;
            match(Tag.NUM);
        }
        else if (lookAhead.tag == Tag.ID) {
            origExpression+=lookAhead.toString();
            tempArray[tokenArraySize++] = lookAhead;
            match(Tag.ID);
        }
        else if (lookAhead.tag == Tag.REAL) {
            origExpression+=lookAhead.toString();
            tempArray[tokenArraySize++] = lookAhead;
            match(Tag.REAL);
        }
        else if(lookAhead.toString().charAt(0) == '(')
        {
            origExpression += '(';
            match('(');
            tmpPrecedence();
            origExpression += ')';
            match(')');
        }

    }
}
