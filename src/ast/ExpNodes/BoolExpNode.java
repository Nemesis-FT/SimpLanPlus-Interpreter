package ast.ExpNodes;

import ast.Node;
import util.Environment;
import util.SemanticError;

import java.util.ArrayList;

public class BoolExpNode implements Node {

    private boolean bool;

    public BoolExpNode(boolean bool){
        this.bool = bool;
    }

    @Override
    public String toPrint(String indent) {
        return "\n"+indent + "Bool " + bool;
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
        return new ArrayList<SemanticError>();
    }
}
