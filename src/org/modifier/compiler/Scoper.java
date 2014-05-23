package org.modifier.compiler;

import org.modifier.parser.NonTerminalNode;

public class Scoper
{
    private NonTerminalNode root;

    public Scoper (NonTerminalNode root)
    {
        this.root = root;
    }

    public NonTerminalNode process ()
    {
        return root;
    }
}
