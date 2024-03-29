package ast;

import ast.Types.TypeNode;
import util.Effect;
import util.Environment;
import util.LabelGenerator;
import util.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;

public class ArgNode implements Node{

    private TypeNode type;
    private IdNode id;
    private boolean isVar = false;
    private int line;
    // Argument node, keeps track if it's used as var.
    public ArgNode(TypeNode type, IdNode id, boolean isVar, int line){
        this.type = type;
        this.id = id;
        this.isVar = isVar;
        this.line = line;
    }

    @Override
    public String toPrint(String indent) {
        return "\n"+indent+"Arg"+this.type.toPrint(indent+" ")+this.id.toPrint(indent+" ");
    }

    public IdNode getId() {
        return id;
    }

    @Override
    public TypeNode typeCheck(Environment env) {
        return null;
    }

    public TypeNode getType(){
        return type;
    }

    public boolean isVar() {
        return isVar;
    }

    @Override
    public String codeGeneration(LabelGenerator labgen, Environment localenv) {
        return null;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        HashMap<String, STentry> st = env.getSymbolTableManager().getLevel(env.getNestingLevel());

        int offset = (type.getType().equals("int")? 4 : 1);

        if(this.isVar){
            offset += 4;
        }

        if(st.put(this.id.getId(), new STentry(env.getNestingLevel(), type, env.decOffset(offset), new Effect(true), false)) != null){
            // There can't be another argument with the same name as another.
            res.add(new SemanticError("[!] Argument id "+this.id.getId()+" already defined for the function at line "+ line +"."));
        }

        return res;
    }

    @Override
    public void setupBreaks(ArrayList<Integer> breaks){
        return;
    }
}
