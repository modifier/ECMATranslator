package org.modifier.parser;

public abstract class Node
{
    private Node parent;
    protected INodeClass nodeClass;

    public Node () { }

    public Node (INodeClass nodeClass)
    {
        this.nodeClass = nodeClass;
    }

    public INodeClass getNodeClass()
    {
        return nodeClass;
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
