package org.modifier.parser;

import java.util.ArrayList;

public class NonTerminalNode extends Node
{
    private ArrayList<Node> children = new ArrayList<>();
    private INodeClass nodeClass;

    public NonTerminalNode(INodeClass className)
    {
        nodeClass = className;
    }

    public INodeClass getNodeClass()
    {
        return nodeClass;
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
        return nodeClass.toString();
    }
}
