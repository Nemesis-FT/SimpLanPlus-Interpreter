package ast;

import ast.Types.TypeNode;
import ast.Types.VoidTypeNode;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;
import util.TypeCheckException;

import java.util.ArrayList;

public class ReturnNode implements Node {

    private Node exp;
    private int line;
    // Return instruction
    public ReturnNode(Node exp, int line) {
        this.exp = exp;
        this.line = line;
    }

    @Override
    public String toPrint(String indent) {
        String res = "\n" + indent + "Return";
        if (this.exp != null)
            return res + exp.toPrint(indent);
        else
            return res;
    }

    @Override
    public TypeNode typeCheck(Environment env) throws TypeCheckException {
        // If instruction is return;
        if (this.exp == null) {
            return new VoidTypeNode();
        }
        // else
        return exp.typeCheck(env);
    }

    public Node getExp() {
        return exp;
    }

    public int getLine(){
        return line;
    }

    @Override
    public String codeGeneration(LabelGenerator labgen, Environment localenv) {
        String asm = ";Return\n";
        if(this.exp!=null){
            asm += this.exp.codeGeneration(labgen, localenv);
        }
        asm += "mov $t1 $fp\n";
        for(int i = 0; i < localenv.getNestingLevel(); i++ ){
            asm += "lw $t1 0($t1)\n";
        }
        asm += "subi $sp $t1 4\n";
        asm += "lw $t1 4($t1)\n";
        asm += "jr $t1\n";
        return asm;
    }

    @Override
    public void setupBreaks(ArrayList<Integer> breaks){
        return;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        if(exp != null) {
            return exp.checkSemantics(env);
        }
        return new ArrayList<SemanticError>();
    }
}
