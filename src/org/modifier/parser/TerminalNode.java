package org.modifier.parser;

import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;

public class TerminalNode extends Node
{
    private Token token;
    private TokenClass nodeClass;

    public TerminalNode(String value)
    {
        this.nodeClass = TokenClass.get("Other");
        token = new Token(value);
    }

    public TerminalNode(TokenClass nodeClass)
    {
        this.nodeClass = nodeClass;
    }

    public TokenClass getNodeClass()
    {
        return nodeClass;
    }

    @Override
    public String toString()
    {
        if (null == token)
        {
            return nodeClass.toString();
        }
        return token.toString();
    }

    public TerminalNode(Token token)
    {
        nodeClass = token.classId;
        this.token = token;
    }

    public void setToken(Token token)
    {
        this.token = token;
    }

    public Token getToken()
    {
        return token;
    }

    public boolean fitsToken(Token token)
    {
        if (token.classId == TokenClass.get("Other") && nodeClass == TokenClass.get("Other"))
        {
            return token.value.equals(this.token.value);
        }

        if (nodeClass == TokenClass.get("Other") && token.value.equals(this.token.value))
        {
            return true;
        }

        return token.classId == nodeClass;
    }
}
