package ast;

import ast.ExpNodes.DerExpNode;
import ast.Types.FunctionTypeNode;
import ast.Types.TypeNode;
import ast.Types.VoidTypeNode;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;
import util.TypeCheckException;

import java.util.ArrayList;
import java.util.Objects;

public class CallNode implements Node {

    private IdNode id;
    private ArrayList<Node> exp;
    private boolean isExp;
    private int line;
    // Callnode statement, used when function is called.
    public CallNode(IdNode id, ArrayList<Node> exp, int line) {
        this.id = id;
        this.exp = exp;
        this.isExp = false;
        this.line = line;
    }

    public CallNode(IdNode id, int line) {
        this.id = id;
        this.isExp = false;
        this.line = line;
    }

    @Override
    public String toPrint(String indent) {
        String res = "\n" + indent + "Call" + id.toPrint(indent);
        if (this.exp != null) {
            for (Node e : exp) {
                res += e.toPrint(indent + " ");
            }
        }
        return res;
    }

    @Override
    public TypeNode typeCheck(Environment env) throws TypeCheckException {

        STentry entry = env.getSymbolTableManager().getLastEntry(id.getId(), 0); // Functions are only defined at level 0

        if (!(env.getSymbolTableManager().getLastEntry(id.getId(), 0).getType() instanceof FunctionTypeNode)) {
            // Check if the symbol can be called.
            throw new TypeCheckException("[!] Trying to call non-function symbol " + this.id.getId()+ " at line "+line+".");
        }

        FunctionTypeNode t = (FunctionTypeNode) entry.getType();
        if (t.getArgs().size() > 0) {
            if(this.exp == null){
                // Check for parameter number
                throw new TypeCheckException("[!] Number of parameters for " + this.id.getId() + " is not correct. Expecting " + t.getArgs().size() + ", got 0 at line "+line+".");
            }
            if (t.getArgs().size() != this.exp.size()) {
                // Check for parameter number
                throw new TypeCheckException("[!] Number of parameters for " + this.id.getId() + " is not correct. Expecting " + t.getArgs().size() + ", got " + this.exp.size()+ " at line "+line+".");
            }
            for (int i = 0; i < exp.size(); i++) {
                // Check for Types and Var mismatches.
                if (!Objects.equals(exp.get(i).typeCheck(env).getType(), t.getArgs().get(i).getType())) {
                    throw new TypeCheckException("[!] Parameters type mismatch for " + this.id.getId() + ": expecting " + t.getArgs().get(i).getType() + ", got " + exp.get(i).typeCheck(env).getType()+ " for parameter "+(i+1)+ " at line "+line+".");
                }

                if (t.getArgs().get(i).isVar()) {
                    if (!exp.get(i).getClass().equals(DerExpNode.class)) {
                        throw new TypeCheckException("[!] Expecting variable in call of symbol " + this.id.getId() + " for parameter "+(i+1)+ " at line "+line+".");
                    }
                }
            }
        } else if (this.exp != null) {
            throw new TypeCheckException("[!] Number of parameters for " + this.id.getId() + " is not correct. Expecting " + t.getArgs().size() + ", got " + this.exp.size()+ " at line "+line+".");
        }
        // Set function as used
        entry.getEffect().setUsed();
        if (!isExp)
            return new VoidTypeNode();
        else
            return env.getSymbolTableManager().getLastEntry(id.getId(), 0).getType();

    }

    public void setIsExp(boolean isExp) {
        this.isExp = isExp;
    }

    @Override
    public String codeGeneration(LabelGenerator labgen, Environment localenv) {
        String asm = ";Function Call\n";

        STentry entry = localenv.getSymbolTableManager().getLastEntry(id.getId(), 0);
        FunctionTypeNode t = (FunctionTypeNode) entry.getType();

        if(this.exp != null) {
            // Load args
            for (int i = (exp.size() - 1); i >= 0; i--) {
                asm += ";Loading arg " + i + "\n";
                // Var loading
                if (t.getArgs().get(i).isVar()) {
                    asm += "mov $t1 $fp\n";
                    DerExpNode idName = (DerExpNode) exp.get(i);
                    for (int j = 0; j < (localenv.getNestingLevel() - localenv.getSymbolTableManager().getLastEntry(idName.getId().getId(), localenv.getNestingLevel()).getNestinglevel()); j++) {
                        asm += "lw $t1 0($t1)\n";
                    }
                    asm += "addi $t1 $t1 " + localenv.getSymbolTableManager().getLastEntry(idName.getId().getId(), localenv.getNestingLevel()).getOffset() + "\n";
                    asm += "push $t1\n";
                }
                // Generate asm for expression
                asm += exp.get(i).codeGeneration(labgen, localenv);
                // Push element on stack.
                if (t.getArgs().get(i).getType().equals("int")) {
                    asm += "push $a0\n";
                } else if (t.getArgs().get(i).getType().equals("bool")) {
                    asm += "subi $sp $sp 1\n";
                    asm += "sb $a0 0($sp)\n";
                }
            }
        }
        asm += "jal " + id.getId() + "\n";
        return asm;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();

        if (env.getSymbolTableManager().getLastEntry(id.getId(), env.getNestingLevel()) == null) {
            res.add(new SemanticError("[!] Function " + this.id.getId() + " not declared at line "+ line +"."));
            return res;
        }

        if (this.exp != null) {
            for (Node arg : exp)
                res.addAll(arg.checkSemantics(env));
        }
        return res;
    }

    @Override
    public void setupBreaks(ArrayList<Integer> breaks){
        return;
    }
}
