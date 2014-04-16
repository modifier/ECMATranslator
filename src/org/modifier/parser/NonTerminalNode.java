package org.modifier.parser;

import java.util.ArrayList;

public class NonTerminalNode extends Node
{
    private ArrayList<Node> children = new ArrayList<>();

    public NonTerminalNode() {}

    public NonTerminalNode(ArrayList<Node> children)
    {
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
