package org.modifier.parser;

import java.util.ArrayList;

public class NonTerminalNode extends Node
{
    private ArrayList<Node> children = new ArrayList<>();

    public NonTerminalNode(INodeClass className)
    {
        super(className);
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

    public ArrayList<Node> getChildren()
    {
        return children;
    }
}
