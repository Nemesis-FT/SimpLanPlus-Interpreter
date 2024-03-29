package ast;

import ast.Types.TypeNode;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;
import util.TypeCheckException;

import java.util.ArrayList;

public class StatementNode implements Node {

    private Node statement;
    private int line;
    private boolean isBreak = false;
    // Generic statement node, handles breakpoints.
    public StatementNode(Node statement, int line) {
        this.statement = statement;
        this.line = line;
    }

    public Node getStatement() {
        return statement;
    }

    @Override
    public String toPrint(String indent) {
        return "\n" + indent + "Statement" + statement.toPrint(indent + " ");
    }

    @Override
    public TypeNode typeCheck(Environment env) throws TypeCheckException {
        return statement.typeCheck(env);
    }

    @Override
    public String codeGeneration(LabelGenerator labgen, Environment localenv) {
        String asm = "";
        // If this statement is a breakpoint, stop.
        if(isBreak){
            asm = ";Breakpoint\n"+"halt\n";
        }
        asm += statement.codeGeneration(labgen, localenv);

        return asm;
    }

    @Override
    public void setupBreaks(ArrayList<Integer> breaks){
        if(breaks.contains(this.line)){
            System.out.println("[L] Breakpoint enabled for line "+line);
            this.isBreak = true;
        }
        if(this.statement!=null){
            statement.setupBreaks(breaks);
        }
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        if (statement != null) {
            res.addAll(this.statement.checkSemantics(env));
        }
        return res;
    }
}
