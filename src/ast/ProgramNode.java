package ast;

import ast.Types.TypeNode;
import ast.Types.VoidTypeNode;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;
import util.TypeCheckException;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgramNode implements Node {

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
    public TypeNode typeCheck(Environment env) throws TypeCheckException {
        // Typecheck the declarations
        if (this.declarations != null) {
            for (Node declaration : this.declarations) {
                declaration.typeCheck(this.localenv);
            }
        }
        // Typecheck the statements
        if (this.statements != null) {
            for (Node statement : this.statements) {
                statement.typeCheck(this.localenv);
            }
        }
        // Locate unused symbols
        for(String id: localenv.getSymbolTableManager().getLevel(localenv.getNestingLevel()).keySet()){
            if(!localenv.getSymbolTableManager().getLevel(localenv.getNestingLevel()).get(id).getEffect().isUsed()){
                System.out.println("[W] Symbol "+id+" is unused in program.");
            }
        }
        return new VoidTypeNode();
    }

    @Override
    public String codeGeneration(LabelGenerator labgen, Environment localenv2) {
        String asm = ";Program\n";
        // Declarations setup
        if (this.declarations != null && this.declarations.size() > 0) {
            asm += ";Variable Declaration\n";
            //li $t1 " + localenv.getDecSpace() + "\n";
            asm += "subi $sp $sp " + localenv.getDecSpace() + "\n";
            asm += "jal main_end\n";
            asm += "label main_start :\n";
            asm += "push $ra\n";
            asm += "push $fp\n";
            asm += "mov $fp $sp\n";
            for (Node declaration : this.declarations) {
                asm += declaration.codeGeneration(labgen, this.localenv);
            }
        }else {
            asm += "mov $t1 $ra\n";
            asm += "jal main_end\n";
            asm += "label main_start :\n";
            asm += "push $ra\n";
            asm += "push $fp\n";
            asm += "mov $fp $sp\n";
        }
        if (this.statements != null) {
            for (Node statement : this.statements) {
                // Statements ASM codegen
                asm += statement.codeGeneration(labgen, this.localenv);
            }
        }
        // Program closeup
        asm += "lw $t1 4($fp)\n";
        asm += "jr $t1\n";
        asm += "label main_end :\n";
        asm += "jal main_start\n";
        asm += "pop $fp\n";
        //asm += "li $t1 " + localenv.getDecSpace() + "\n";
        asm += "addi $sp $sp " + localenv.getDecSpace() + "\n";
        return asm;
    }

    @Override
    public void setupBreaks(ArrayList<Integer> breaks){
        if (this.statements != null && this.statements.size() > 0) {
            for(Node n: this.declarations){
                n.setupBreaks(breaks);
            }
            for (Node n : this.statements) {
                n.setupBreaks(breaks);
            }
        }
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        env.incNestingLevel(1);
        HashMap<String, STentry> st = new HashMap<String, STentry>();
        env.getSymbolTableManager().addLevel(st);
        env.setOffset(8);

        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        // Check the semantics of declarations
        if (this.declarations != null && this.declarations.size() > 0) {
            for (Node n : this.declarations) {
                res.addAll(n.checkSemantics(env));
            }
        }
        // Check the semantics of statements
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
