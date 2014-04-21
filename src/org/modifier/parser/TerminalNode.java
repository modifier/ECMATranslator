package org.modifier.parser;

import org.modifier.scanner.ITokenClass;
import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;

public class TerminalNode extends Node
{
    private Token token;
    private ITokenClass nodeClass;

    public TerminalNode(String value)
    {
        this.nodeClass = TokenClass.Other;
        token = new Token(value);
    }

    public TerminalNode(ITokenClass nodeClass)
    {
        this.nodeClass = nodeClass;
    }

    public ITokenClass getNodeClass()
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
        if (token.classId == TokenClass.Other && nodeClass == TokenClass.Other)
        {
            return token.value.equals(this.token.value);
        }

        return token.classId == nodeClass;
    }
}
