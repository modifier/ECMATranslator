package org.modifier.parser;

import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;

public class TerminalNode extends Node
{
    private Token token;

    public TerminalNode(String value)
    {
        this.tokenClass = TokenClass.get("Other");
        token = new Token(value);
    }

    public TerminalNode(String value, String classId)
    {
        this.tokenClass = TokenClass.get(classId);
        token = new Token(value, classId);
    }

    public TerminalNode(TokenClass nodeClass)
    {
        this.tokenClass = nodeClass;
    }

    @Override
    public String toString()
    {
        return token.value;
    }

    public TerminalNode(Token token)
    {
        tokenClass = token.classId;
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
        if (token.classId == TokenClass.get("Other") && tokenClass == TokenClass.get("Other"))
        {
            return token.value.equals(this.token.value);
        }

        if (tokenClass == TokenClass.get("Other") && token.value.equals(this.token.value))
        {
            return true;
        }

        return token.classId == tokenClass;
    }
}
