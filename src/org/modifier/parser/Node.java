package org.modifier.parser;

public abstract class Node
{
    private Node parent;
    private INodeClass nodeClass;

    public Node (INodeClass nodeClass)
    {
        this.nodeClass = nodeClass;
    }

    public void setParent(Node node)
    {
        parent = node;
    }

    public Node getParent()
    {
        return parent;
    }
}
