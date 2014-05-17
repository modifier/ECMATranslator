package org.modifier.parser;

import org.modifier.scanner.TokenClass;

public abstract class Node
{
    private Node parent;
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
}
