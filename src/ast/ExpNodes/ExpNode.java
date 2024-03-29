package ast.ExpNodes;

import ast.Node;
import ast.Types.TypeNode;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;
import util.TypeCheckException;

import java.util.ArrayList;

public class ExpNode implements Node {

    protected Node exp;
    protected int line;
    // Generic expression
    public ExpNode(Node exp, int line){
        this.exp = exp;
        this.line = line;
    }

    @Override
    public String toPrint(String indent) {
        return "\n"+indent+"ExpNode "+exp.toPrint(indent+" ");
    }

    @Override
    public TypeNode typeCheck(Environment env) throws TypeCheckException {
        return exp.typeCheck(env);
    }

    @Override
    public String codeGeneration(LabelGenerator labgen, Environment localenv) {
        return exp.codeGeneration(labgen, localenv);
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        return exp.checkSemantics(env);
    }

    @Override
    public void setupBreaks(ArrayList<Integer> breaks){
        return;
    }
}
