package org.modifier.parser;

public abstract class Node
{
    private Node parent;

    public Node () { }

    public void setParent(Node node)
    {
        parent = node;
    }

    public Node getParent()
    {
        return parent;
    }

    abstract public String toString();
}
