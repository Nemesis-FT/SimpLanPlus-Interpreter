package ast.ExpNodes;

import ast.Node;
import util.Environment;
import util.SemanticError;

import java.util.ArrayList;

public class BinExpNode implements Node {

    private String op;
    private Node left;
    private Node right;

    public BinExpNode(String op, Node left, Node right){
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toPrint(String indent) {
        return null;
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
