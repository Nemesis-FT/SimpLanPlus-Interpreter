package ast;

import ast.Types.TypeNode;
import ast.Types.VoidTypeNode;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgramNode implements Node {

    /*
    Nella sintassi di SLP, le dichiarazioni delle variabili / funzioni devono venire fatte prima
    degli statement.
     */
    private ArrayList<Node> declarations;
    private ArrayList<Node> statements;
    private Environment localenv;

    public ProgramNode(ArrayList<Node> declarations, ArrayList<Node> statements) {
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
        return "\n" + indent + "Program" + res;
    }

    @Override
    public TypeNode typeCheck(Environment env) {
        if (this.declarations != null) {
            for (Node declaration : this.declarations) {
                declaration.typeCheck(this.localenv);
            }
        }
        if (this.statements != null) {
            for (Node statement : this.statements) {
                statement.typeCheck(this.localenv);
            }
        }
        for(String id: localenv.getSymbolTableManager().getLevel(localenv.getNestingLevel()).keySet()){
            if(!localenv.getSymbolTableManager().getLevel(localenv.getNestingLevel()).get(id).getEffect().isUsed()){
                System.out.println("Warning: symbol "+id+" is unused.");
            }
        }
        return new VoidTypeNode();
    }

    @Override
    public String codeGeneration(LabelGenerator labgen, Environment localenv2) {
        String asm = ";Program\n"; // li $sp MEM_TOP\nli $fp MEM_TOP causano problemi con il parser

        if (this.declarations != null) {
            asm += ";Variable Declaration\nli $t1 " + localenv.getOffset() + "\n";
            asm += "sub $fp $fp $t1\n";
            for (Node declaration : this.declarations) {
                asm += declaration.codeGeneration(labgen, this.localenv);
            }
        }
        if (this.statements != null) {
            for (Node statement : this.statements) {
                asm += statement.codeGeneration(labgen, this.localenv);
            }
        }
        return asm;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        env.incNestingLevel(1);
        HashMap<String, STentry> st = new HashMap<String, STentry>();
        env.getSymbolTableManager().addLevel(st);

        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        if (this.declarations != null && this.declarations.size() > 0) {
            for (Node n : this.declarations) {
                res.addAll(n.checkSemantics(env));
            }
        }
        if (this.statements != null && this.statements.size() > 0) {
            for (Node n : this.statements) {
                res.addAll(n.checkSemantics(env));
            }
        }
        localenv = new Environment(env);
        env.getSymbolTableManager().removeLevel(env.decNestingLevel(1));
        return res;
    }

}
