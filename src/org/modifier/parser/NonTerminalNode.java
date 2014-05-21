package org.modifier.parser;

import org.modifier.scanner.TokenClass;
import org.modifier.utils.Tuple;

import java.util.ArrayList;

public class NonTerminalNode extends Node
{
    private ArrayList<Node> children = new ArrayList<>();

    public NonTerminalNode(String className)
    {
        tokenClass = TokenClass.get(className);
    }

    public NonTerminalNode(TokenClass className)
    {
        tokenClass = className;
    }

    public void update(NonTerminalNode anotherNode)
    {
        this.children = anotherNode.children;
        this.tokenClass = anotherNode.tokenClass;
    }

    public void setChildren(ArrayList<Node> children)
    {
        this.children = children;
    }

    public ArrayList<Node> getChildren()
    {
        return children;
    }

    public void appendChild (Node child)
    {
        children.add(child);
    }

    public void clearChildren ()
    {
        children = new ArrayList<>();
    }

    public Node findNodeClass(String classType)
    {
        for (Node node : getChildren())
        {
            if (node.getTokenClass() == TokenClass.get(classType))
            {
                return node;
            }
        }

        return null;
    }

    @Override
    public String toString()
    {
        Tuple<String, Boolean> tuple = toString(false);
        return tuple.x;
    }

    private boolean isAlphaString (String str)
    {
        if (str.charAt(0) >= '0' && str.charAt(0) <= '9')
        {
            return false;
        }

        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if (c == '$' || c == '_' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')
            {
                continue;
            }
            return false;
        }
        return true;
    }

    private Tuple<String, Boolean> toString(boolean prevWord)
    {
        StringBuilder builder = new StringBuilder();

        boolean isPrevWord = prevWord;
        for (Node child : getChildren())
        {
            if (child instanceof TerminalNode)
            {
                if (child.getNodeClass() == TokenClass.get("<EOF>"))
                {
                    continue;
                }
                boolean isNowWord = isAlphaString(child.toString());
                if (isNowWord == isPrevWord && isNowWord)
                {
                    builder.append(' ');
                }
                isPrevWord = isNowWord;
                builder.append(child.toString());
            }
            else
            {
                Tuple<String, Boolean> result = ((NonTerminalNode) child).toString(isPrevWord);
                builder.append(result.x);
                isPrevWord = result.y;
            }
        }

        return new Tuple<>(builder.toString(), isPrevWord);
    }
}
