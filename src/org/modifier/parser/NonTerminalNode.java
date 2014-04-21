package org.modifier.parser;

import java.util.ArrayList;

public class NonTerminalNode extends Node
{
    private ArrayList<Node> children = new ArrayList<>();

    public NonTerminalNode(INodeClass className)
    {
        super(className);
    }

    @Override
    public String toString()
    {
        return nodeClass.toString();
    }

    public NonTerminalNode(INodeClass className, ArrayList<Node> children)
    {
        super(className);
        this.children = children;
    }

    public void appendChild(Node child)
    {
        children.add(child);
    }

    public void setChildren(ArrayList<Node> children)
    {
        this.children = children;
    }

    public ArrayList<Node> getChildren()
    {
        return children;
    }
}
