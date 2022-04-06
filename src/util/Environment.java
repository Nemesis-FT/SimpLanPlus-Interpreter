package util;

import java.util.ArrayList;
import java.util.HashMap;

import ast.STentry;

public class Environment {
    public ArrayList<HashMap<String, STentry>> symTable = new ArrayList<HashMap<String, STentry>>();
    public int nestingLevel = -1;
    public int offset = 0;
    public boolean insideFunction=false;
}