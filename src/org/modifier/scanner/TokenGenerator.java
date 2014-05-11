package org.modifier.scanner;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.List;

public class TokenGenerator implements Iterator<Token> {
    private List<Token> tokens;
    private int position = 0;

    public TokenGenerator (List<Token> tokens)
    {
        this.tokens = tokens;
    }

    @Override
    public boolean hasNext()
    {
        return position < tokens.size();
    }

    @Override
    public Token next()
    {
        if (position < tokens.size())
        {
            return tokens.get(position++);
        }
        else
        {
            position++;
            return new Token("<EOF>", TokenClass.get("Other"));
        }
    }

    @Override
    public void remove()
    {
        throw new NotImplementedException();
    }
}
