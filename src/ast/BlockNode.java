package ast;

import util.Environment;
import util.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockNode implements Node {

    /*
    Nella sintassi di SLP, le dichiarazioni delle variabili / funzioni devono venire fatte prima
    degli statement.
     */
    private ArrayList<Node> declarations;
    private ArrayList<Node> statements;

    public BlockNode(ArrayList<Node> declarations, ArrayList<Node> statements) {
        this.declarations = declarations;
        this.statements = statements;
    }

    @Override
    public String toPrint(String indent) {
        String res = "";
        if (this.declarations != null) {
            for (Node dec : declarations) {
                res += dec.toPrint(indent + " ");
            }
        }
        if (this.statements != null) {
            for (Node dec : statements) {
                res += dec.toPrint(indent + " ");
            }
        }
        return "\n"+indent + "Block" + res;
    }

    @Override
    public Node typeCheck() {
        if(this.declarations!=null){
            // DECs rule
            for(Node dec: declarations){
                dec.typeCheck();
            }
        }
        Node T = null;
        if(this.statements!=null){
            // STMs rule
            for(Node stm: statements){
                // QUI CI VA MAX*
                T = stm.typeCheck();
            }
        }
        return T;
    }

    @Override
    public String codeGeneration() {
        return null;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        env.incNestingLevel(1);
        HashMap<String, STentry> level = new HashMap<String, STentry>();
        env.getSymbolTableManager().addLevel(level);

        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        if(this.declarations!=null && this.declarations.size()>0){
            for(Node n: this.declarations){
                res.addAll(n.checkSemantics(env));
            }
        }
        if(this.statements!=null && this.statements.size()>0){
            for(Node n: this.statements){
                res.addAll(n.checkSemantics(env));
            }
        }
        env.getSymbolTableManager().removeLevel(env.decNestingLevel(1));

        return res;
    }
}
