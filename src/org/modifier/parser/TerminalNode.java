package org.modifier.parser;

import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;

public class TerminalNode extends Node
{
    private Token token;

    public TerminalNode(String value)
    {
        super(TerminalNodeClass.Other);
        token = new Token(value);
    }

    public TerminalNode(INodeClass nodeClass)
    {
        super(nodeClass);
    }

    public TerminalNode(Token token)
    {
        if (token.classId == TokenClass.Ident)
        {
            nodeClass = TerminalNodeClass.Identifier;
        }
        else if (token.classId == TokenClass.Const)
        {
            nodeClass = TerminalNodeClass.Const;
        }
        else if (token.classId == TokenClass.Literal)
        {
            nodeClass = TerminalNodeClass.Literal;
        }
        else if (token.classId == TokenClass.Regex)
        {
            nodeClass = TerminalNodeClass.RegEx;
        }
        else
        {
            nodeClass = TerminalNodeClass.Other;
        }
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
