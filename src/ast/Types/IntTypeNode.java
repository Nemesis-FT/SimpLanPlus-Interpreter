package ast.Types;

import java.util.ArrayList;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;

public class IntTypeNode extends TypeNode {
    // The int return type
    public IntTypeNode () {
        super("int");
    }

    public String toPrint(String s) {
        return s+"IntType\n";
    }

    //non utilizzato
    public TypeNode typeCheck(Environment env) {
        return null;
    }

    //non utilizzato
    public String codeGeneration(LabelGenerator labgen, Environment localenv) {
        return "";
    }


    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {

        return new ArrayList<SemanticError>();
    }

}