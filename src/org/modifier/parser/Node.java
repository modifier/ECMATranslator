package org.modifier.parser;

import org.modifier.scanner.TokenClass;

public abstract class Node
{
    protected NonTerminalNode parent;
    protected TokenClass tokenClass;

    public TokenClass getNodeClass()
    {
        return tokenClass;
    }

    public TokenClass getTokenClass()
    {
        return tokenClass;
    }

    public Node () { }

    abstract public String toString();

    public void setParent (NonTerminalNode parent)
    {
        this.parent = parent;
    }
}
