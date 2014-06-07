package org.modifier.scanner;

import org.modifier.utils.NotImplementedException;

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
        return tokens.get(position++);
    }

    @Override
    public void remove()
    {
        throw new NotImplementedException();
    }
}
