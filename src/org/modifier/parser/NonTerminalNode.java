package org.modifier.parser;

import org.modifier.compiler.Scope;
import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;
import org.modifier.utils.Tuple;

import java.util.ArrayList;

public class NonTerminalNode extends Node
{
    private ArrayList<Node> children = new ArrayList<>();
    private Scope scope;

    public NonTerminalNode(String className)
    {
        tokenClass = TokenClass.get(className);
    }

    public NonTerminalNode(TokenClass className)
    {
        tokenClass = className;
    }

    public Scope getScope()
    {
        if (!isLetBlock())
        {
            return null;
        }

        if (scope == null)
        {
            scope = new Scope();
            NonTerminalNode letBlock = closestLetBlock();
            if (letBlock != null)
            {
                scope.setParent(letBlock.getScope());
            }
        }
        return scope;
    }

    public void update(NonTerminalNode anotherNode)
    {
        this.children = anotherNode.children;
        this.tokenClass = anotherNode.tokenClass;
        for (Node kid : children)
        {
            kid.setParent(this);
        }
    }

    public NonTerminalNode clone ()
    {
        NonTerminalNode clone = new NonTerminalNode(tokenClass);
        clone.children = children;
        return clone;
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

    public Node findDeep(String classType)
    {
        for (Node node : getChildren())
        {
            if (node.getTokenClass() == TokenClass.get(classType))
            {
                return node;
            }

            if (node instanceof NonTerminalNode)
            {
                Node result = ((NonTerminalNode) node).findDeep(classType);
                if (result != null)
                {
                    return result;
                }
            }
        }

        return null;
    }

    public NonTerminalNode closestLetBlock()
    {
        ArrayList<String> letScope = new ArrayList<>();
        letScope.add("Block");
        letScope.add("ForStatement");
        letScope.add("FunctionDeclaration");
        letScope.add("FunctionExpression");
        letScope.add("Program");
        return (NonTerminalNode)closest(letScope);
    }

    public boolean isLetBlock()
    {
        return tokenClass == TokenClass.get("Block")
            || tokenClass == TokenClass.get("ForStatement")
            || isVarBlock();
    }

    public boolean isVarBlock()
    {
        return tokenClass == TokenClass.get("FunctionDeclaration")
            || tokenClass == TokenClass.get("FunctionExpression")
            || tokenClass == TokenClass.get("Program");
    }

    public NonTerminalNode closestVarBlock()
    {
        ArrayList<String> varScope = new ArrayList<>();
        varScope.add("FunctionDeclaration");
        varScope.add("FunctionExpression");
        varScope.add("Program");
        return (NonTerminalNode)closest(varScope);
    }

    public Node closest(ArrayList<String> list)
    {
        if (parent == null)
        {
            return null;
        }

        for (String type : list)
        {
            if (parent.getNodeClass() == TokenClass.get(type))
            {
                return parent;
            }
        }

        return parent.closest(list);
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
            if (c == '$' || c == '_' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9')
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
