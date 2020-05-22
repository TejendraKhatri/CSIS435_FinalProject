package intercode.parser ;
import java.util.*;

import intercode.parser.DeclarationNode;
import intercode.visitor.* ;

public class BlockNode extends Node {

    // public Vector<DeclarationNode> decls = new Vector<DeclarationNode>();
    // public Vector<StatementNode> stmts = new Vector<StatementNode>();

    public DeclarationNode[] decls;
    public StatementNode[] stmts;
    public Env symbolTable;

    public BlockNode () {
            decls = new DeclarationNode[10000];
            stmts = new StatementNode[10000];
    }

    public void accept(ASTVisitor v) {

        v.visit(this);
    }
}
