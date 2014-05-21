package org.modifier.utils;

import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.parser.TerminalNode;

import java.util.ArrayList;

public class TreeGenerator
{
    private ArrayList<NonTerminalNode> list;
    private int position = 0;
    private String tree;

    public TreeGenerator(String tree)
    {
        list = new ArrayList<>();
        this.tree = tree;

        position = 0;
        generate(new NonTerminalNode("<CONTAINER>"));
    }

    public ArrayList<NonTerminalNode> getList ()
    {
        return list;
    }

    public NonTerminalNode get(int id)
    {
        return list.get(id);
    }

    private Node generate (NonTerminalNode container)
    {
        boolean addToArray = false;
        NonTerminalNode lastNT = null;
        while (position < tree.length())
        {
            if ('}' == tree.charAt(position))
            {
                return container;
            }
            else if ('{' == tree.charAt(position))
            {
                position++;
                generate(lastNT);
            }
            else if ('\'' == tree.charAt(position))
            {
                container.appendChild(new TerminalNode(matchLiteral()));
            }
            else if ('$' == tree.charAt(position))
            {
                addToArray = true;
            }
            else if (isAlphaChar(tree.charAt(position)))
            {
                NonTerminalNode node = new NonTerminalNode(matchIdent());
                lastNT = node;
                if (addToArray)
                {
                    list.add(node);
                    addToArray = false;
                }
                container.appendChild(node);
            }
            position++;
        }
        return container;
    }

    private boolean isAlphaChar (char symbol)
    {
        return symbol >= 'A' && symbol <= 'Z' || symbol >= 'a' && symbol <= 'z' || symbol >= '0' && symbol <= '9';
    }

    private String matchLiteral ()
    {
        position++;
        StringBuilder b = new StringBuilder();
        while ('\'' != tree.charAt(position))
        {
            b.append(tree.charAt(position++));
        }
        return b.toString();
    }

    private String matchIdent ()
    {
        position++;
        StringBuilder b = new StringBuilder();
        while (isAlphaChar(tree.charAt(position)))
        {
            b.append(tree.charAt(position++));
        }
        return b.toString();
    }
}
