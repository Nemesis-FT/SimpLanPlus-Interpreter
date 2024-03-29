package ast;

import ast.Types.BoolTypeNode;
import ast.Types.TypeNode;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;
import util.TypeCheckException;

import java.util.ArrayList;
import java.util.HashMap;

public class IteNode implements Node {

    private Node exp;
    private Node then_statement;
    private Node else_statement;
    private ArrayList<HashMap<String, STentry>> localSymbolTable;
    private int line;
    // ITE Node with or without else branch
    public IteNode(Node exp, Node then_statement, Node else_statement, int line) {
        this.exp = exp;
        this.then_statement = then_statement;
        this.else_statement = else_statement;
        this.line = line;
    }

    public IteNode(Node exp, Node then_statement, int line) {
        this.exp = exp;
        this.then_statement = then_statement;
        this.line = line;
    }

    @Override
    public String toPrint(String indent) {
        String res = "\n" + indent + "ITE ";
        res += "\n" + indent + " Condition " + exp.toPrint(indent + "  ");
        res += "\n" + indent + " Then " + then_statement.toPrint(indent + "  ");

        if (else_statement != null) {
            res += "\n" + indent + " Else " + else_statement.toPrint(indent + "  ");
        }

        return res;
    }


    @Override
    public TypeNode typeCheck(Environment env) throws TypeCheckException {
        // Check if exp is boolean
        if (!exp.typeCheck(env).getClass().equals(BoolTypeNode.class)) {
            throw new TypeCheckException("[!] Condition of if statement not boolean at line "+line+".");
        }

        Environment envOld = new Environment(env, true);
        // Typecheck then_node
        TypeNode then_node = then_statement.typeCheck(env);
        // If there's an else statement...
        if (else_statement != null) {
            TypeNode else_node = else_statement.typeCheck(envOld);
            // If there's a mismatch in types...
            if (!then_node.getType().equals(else_node.getType())) {
                throw new TypeCheckException("[!] Then and else have different types in structure at line "+line+".");
            }
        }
        // Warn the user if the return type is not void, a non void return was found and the else branch is not present.
        if (else_statement==null && !then_node.getType().equals("void")){
            System.out.println("[W] Else not specified in structure at line "+line+" that contains a non-void return.\n" +
                    "    Please be sure that there's another exit point outside of the ite.");
            //System.exit(0);
        }
        // EffectMax function between else and then branches
        for (int i = 0; i < env.getSymbolTableManager().getSymbolTable().size(); i++) {
            HashMap<String, STentry> tmp = env.getSymbolTableManager().getSymbolTable().get(i);
            HashMap<String, STentry> tmp1 = envOld.getSymbolTableManager().getSymbolTable().get(i);
            for (String key : tmp.keySet()) {
                tmp.get(key).getEffect().join(tmp1.get(key).getEffect().getStatus());
            }

        }

        return then_node;
    }

    public boolean has_else(){
        return this.else_statement!=null;
    }

    @Override
    public void setupBreaks(ArrayList<Integer> breaks){
        if(this.then_statement!=null){
            this.then_statement.setupBreaks(breaks);
        }
        if(this.else_statement!=null){
            this.else_statement.setupBreaks(breaks);
        }
    }

    @Override
    public String codeGeneration(LabelGenerator labgen, Environment localenv) {
        String asm = ";Ite\n"+exp.codeGeneration(labgen, localenv);
        String then_label = labgen.new_label("ITE_THEN");
        String exit_label = labgen.new_label("ITE_EXIT");
        asm += "li $t1 1\nbeq $a0 $t1 " + then_label + "\n" +
                (else_statement != null ? else_statement.codeGeneration(labgen, localenv) : "") + "jal " + exit_label + "\n" +
                "label " + then_label + ":\n" + then_statement.codeGeneration(labgen, localenv) + "label " + exit_label + ":\n";

//        asm += "li $t1 1\nbeq $a0 $t1 " + then_label + "\n" +
//                (else_statement != null ? else_statement.codeGeneration(labgen, localenv) : "") + "jal " + exit_label + "\n" +
//                then_label + ":\n" + then_statement.codeGeneration(labgen, localenv) + exit_label + ":\n";
        return asm;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        if (this.exp != null) {
            res.addAll(this.exp.checkSemantics(env));
        }
        if (this.then_statement != null) {
            res.addAll(this.then_statement.checkSemantics(env));
        }
        if (this.else_statement != null) {
            res.addAll(this.else_statement.checkSemantics(env));
        }
        this.localSymbolTable = env.getSymbolTableManager().getSymbolTable();
        return res;
    }
}
