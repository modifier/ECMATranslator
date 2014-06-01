package org.modifier.compiler;

import org.modifier.parser.Node;
import org.modifier.scanner.Token;

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

    public void addIdent (Token ident) throws TypeError
    {
        if (hasConstIdent(ident.value))
        {
            throw TypeError.constantIsAlreadyDefined(ident);
        }
        vars.add(ident.value);
    }

    public void addConst (Token ident, Node value) throws TypeError
    {
        if (hasOwnIdent(ident.value))
        {
            throw TypeError.constantIsAlreadyDefined(ident);
        }
        consts.put(ident.value, value);
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
