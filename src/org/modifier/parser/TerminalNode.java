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
        if (token.classId == TokenClass.Other && nodeClass == TerminalNodeClass.Other)
        {
            return token.value.equals(this.token.value);
        }
        else if (token.classId == TokenClass.Ident && nodeClass == TerminalNodeClass.Identifier)
        {
            return true;
        }
        else if (token.classId == TokenClass.Const && nodeClass == TerminalNodeClass.Const)
        {
            return true;
        }
        else if (token.classId == TokenClass.Literal && nodeClass == TerminalNodeClass.Literal)
        {
            return true;
        }
        else if (token.classId == TokenClass.Regex && nodeClass == TerminalNodeClass.RegEx)
        {
            return true;
        }

        return false;
    }
}
