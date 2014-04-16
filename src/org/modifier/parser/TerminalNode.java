package org.modifier.parser;

import org.modifier.scanner.Token;

public class TerminalNode extends Node
{
    private Token token;

    public TerminalNode(Token token)
    {
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
        // TODO: add checking
        return true;
    }
}
