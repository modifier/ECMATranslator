package org.modifier.lexer;

import org.modifier.scanner.Token;
import org.modifier.scanner.TokenGenerator;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractLexer implements Iterable<Token>
{
    private Iterable<Token> scanner;

    public AbstractLexer(Iterable<Token> scanner)
    {
        this.scanner = scanner;
    }

    private ArrayList<Token> convert()
    {
        ArrayList<Token> result = new ArrayList<>();
        for (Token token : scanner)
        {
            result.add(convertToken(token));
        }
        return result;
    }

    protected abstract Token convertToken(Token token);

    @Override
    public Iterator<Token> iterator() {
        return new TokenGenerator(convert());
    }
}
