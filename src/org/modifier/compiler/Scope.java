package org.modifier.compiler;

import java.util.ArrayList;

public class Scope
{
    private ArrayList<String> vars = new ArrayList<>();
    private Scope parentScope = null;

    public Scope () { }

    public Scope (Scope parent)
    {
        parentScope = parent;
    }

    public void addIdent (String ident)
    {
        vars.add(ident);
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
        return vars.indexOf(ident) != -1;
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
