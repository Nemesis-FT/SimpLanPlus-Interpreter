package ast;

import util.Environment;
import util.SemanticError;

import java.util.ArrayList;

public class BlockNode implements Node{

    /*
    Nella sintassi di SLP, le dichiarazioni delle variabili / funzioni devono venire fatte prima
    degli statement.
     */
    private ArrayList<Node> declarations;
    private ArrayList<Node> statements;

    public BlockNode(ArrayList<Node> declarations, ArrayList<Node> statements){
        this.declarations = declarations;
        this.statements = statements;
    }

    @Override
    public String toPrint(String indent) {
        String res = "";
        for(Node dec:declarations){
            res += dec.toPrint(indent+" ");
        }
        for(Node dec:statements){
            res += dec.toPrint(indent+" ");
        }
        return indent+"Block\n"+res;
    }

    @Override
    public Node typeCheck() {
        return null;
    }

    @Override
    public String codeGeneration() {
        return null;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        return null;
    }
}