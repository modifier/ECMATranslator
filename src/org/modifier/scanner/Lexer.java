package org.modifier.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Lexer implements Iterable<Token>
{
    private Iterable<Token> scanner;
    private HashMap<String, ArrayList<String>> correspondence = new HashMap<>();

    public Lexer(Iterable<Token> scanner)
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

    protected Token convertToken(Token token)
    {
        if (token.classId != TokenClass.get("Other"))
        {
            return token;
        }

        for (String key : correspondence.keySet())
        {
            ArrayList<String> values = correspondence.get(key);
            for (String value : values)
            {
                if (value.equals(token.value))
                {
                    return new Token(value, TokenClass.get(key));
                }
            }
        }

        return token;
    }

    public void setCorrespondence(HashMap<String, ArrayList<String>> correspondence)
    {
        this.correspondence = correspondence;
    }

    @Override
    public Iterator<Token> iterator() {
        return new TokenGenerator(convert());
    }
}
