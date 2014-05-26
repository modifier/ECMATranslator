package org.modifier.compiler;

import org.modifier.parser.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class Scope
{
    private ArrayList<String> vars = new ArrayList<>();
    private HashMap<String, Node> consts = new HashMap<>();
    private Scope parentScope = null;

    public Scope () { }

    public Scope (Scope parent)
    {
        parentScope = parent;
    }

    public void addIdent (String ident) throws TypeError
    {
        if (hasConstIdent(ident))
        {
            throw TypeError.constantIsAlreadyDefined(ident);
        }
        vars.add(ident);
    }

    public void addConst (String ident, Node value) throws TypeError
    {
        if (hasOwnIdent(ident))
        {
            throw TypeError.constantIsAlreadyDefined(ident);
        }
        consts.put(ident, value);
    }

    public boolean hasIdent (String ident)
    {
        boolean hasHere = hasOwnIdent(ident);
        if (!hasHere && parentScope != null)
        {
            return parentScope.hasIdent(ident);
        }
        return hasHere;
    }

    public boolean hasOwnIdent (String ident)
    {
        return vars.indexOf(ident) != -1 || hasConstIdent(ident);
    }

    public boolean hasConstIdent (String ident)
    {
        return consts.containsKey(ident);
    }

    public void replace (String oldIdent, String newIdent)
    {
        vars.remove(oldIdent);
        vars.add(newIdent);
    }

    public void setParent (Scope parent)
    {
        parentScope = parent;
    }
}
