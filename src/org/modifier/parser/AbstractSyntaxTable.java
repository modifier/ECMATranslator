package org.modifier.parser;

import java.util.ArrayList;
import org.modifier.scanner.Token;

public abstract class AbstractSyntaxTable
{
    public ArrayList<Node> getRule(Token token, Node node) throws SyntaxError
    {
        if (node instanceof NonTerminalNode)
        {
            return getRule(token, (NonTerminalNode)node);
        }
        else
        {
            return getRule(token, (TerminalNode)node);
        }
    }

    abstract public ArrayList<Node> getRule(Token token, NonTerminalNode currentNode) throws SyntaxError;

    public ArrayList<Node> getRule(Token token, TerminalNode currentNode)
    {
        if (currentNode.fitsToken(token))
        {
            currentNode.setToken(token);
        }

        ArrayList<Node> result = new ArrayList<>();
        result.add(currentNode);

        return result;
    }

    abstract public Node getRoot();
}
