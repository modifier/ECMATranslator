package org.modifier.parser;

import org.modifier.scanner.TokenClass;

import java.util.ArrayList;

public class NonTerminalNode extends Node
{
    private ArrayList<Node> children = new ArrayList<>();
    private TokenClass tokenClass;

    public NonTerminalNode(TokenClass className)
    {
        tokenClass = className;
    }

    public TokenClass getTokenClass()
    {
        return tokenClass;
    }

    public void setChildren(ArrayList<Node> children)
    {
        this.children = children;
    }

    public ArrayList<Node> getChildren()
    {
        return children;
    }

    @Override
    public String toString()
    {
        return tokenClass.toString();
    }
}
