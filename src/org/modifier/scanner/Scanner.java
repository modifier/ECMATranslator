package org.modifier.scanner;

import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;
import org.modifier.scanner.TokenGenerator;
import org.modifier.utils.FilteredSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Scanner implements Iterable<Token> {
    private final HashMap<String, Token> tokenList = new HashMap<>();
    private final String stream;

    public Scanner(String stream)
    {
        this.stream = stream;
    }

    private ArrayList<Token> scan ()
    {
        ArrayList<Token> result = new ArrayList<>();

        do
        {
            char symbol = stream.charAt(position);

            if (isWhitespace(symbol))
            {
                position++;
                continue;
            }
            if (isAlpha(symbol))
            {
                result.add(matchIdent());
            }
            else if (isDigit(symbol))
            {
                result.add(matchConst());
            }
            else if (isLiteral(symbol))
            {
                result.add(matchLiteral());
            }
            else if (isRegex(symbol))
            {
                result.add(matchRegex());
            }
            else if (isComment(symbol))
            {
                skipComment();
            }
            else
            {
                result.addAll(matchToken());
            }
        } while (stream.length() > position);

        return result;
    }

    private Token getToken(String key, TokenClass classId)
    {
        if (tokenList.get(key) == null)
        {
            tokenList.put(key, new Token(key, classId));
        }

        return tokenList.get(key);
    }

    private char getLookahead()
    {
        return stream.charAt(position);
    }

    //<editor-fold desc="Matchers">
    private Token matchIdent()
    {
        // TODO: idents may start with _
        char symbol;

        StringBuilder accumulator = new StringBuilder();
        do
        {
            symbol = stream.charAt(position++);
            accumulator.append(symbol);
        }
        while (isAlpha(getLookahead()) || isDigit(getLookahead()));

        String finalString = accumulator.toString();

        return getToken(finalString, TokenClass.Ident);
    }

    private Token matchConst()
    {
        // TODO: add scientific notation and notation starting with point
        char symbol;

        StringBuilder accumulator = new StringBuilder();
        Boolean wasPoint = false;
        do
        {
            symbol = stream.charAt(position++);
            accumulator.append(symbol);
            if ('.' == symbol) {
                wasPoint = true;
            }
        }
        while (isDigit(getLookahead()) || ('.' == getLookahead() && !wasPoint));

        String finalString = accumulator.toString();

        return getToken(finalString, TokenClass.Const);
    }

    private Token matchLiteral()
    {
        // TODO: there is no line wrapping in javascript
        // TODO: check quotation escaping
        final char quoteType = stream.charAt(position++);
        StringBuilder accumulator = new StringBuilder();
        accumulator.append(quoteType);
        char symbol;

        do
        {
            symbol = stream.charAt(position++);
            accumulator.append(symbol);
        }
        while (quoteType != symbol);

        String finalString = accumulator.toString();

        return getToken(finalString, TokenClass.Literal);
    }

    private ArrayList<Token> matchToken()
    {
        ArrayList<Token> result = new ArrayList<>();

        StringBuilder accumulator = new StringBuilder();
        char symbol;

        FilteredSet keys = new FilteredSet(tokenList.keySet());

        ArrayList<String> tokenStrings = new ArrayList<>();

        do
        {
            symbol = stream.charAt(position++);
            accumulator.append(symbol);

            if (!keys.containsPartialKey(accumulator.toString()))
            {
                String tokenString = accumulator.toString();
                for (int i = 0; i < tokenString.length(); i++)
                {
                    tokenStrings.add(String.valueOf(tokenString.charAt(i)));
                }
            }

            if (1 == keys.size() && keys.contains(accumulator.toString())) {
                tokenStrings.add(accumulator.toString());
                break;
            }
        }
        while (!isWhitespace(getLookahead()) && !isAlpha(getLookahead()) && !isDigit(getLookahead()) && (0 != keys.size()));

        for (String tokenString : tokenStrings) {
            result.add(getToken(tokenString, TokenClass.Other));
        }

        return result;
    }

    private Token matchRegex()
    {
        return null;
    }

    private void skipComment()
    {
        Boolean isBlockComment = stream.charAt(++position) == '*';

        do
        {
            char symbol = stream.charAt(position++);

            if ('\n' == symbol && !isBlockComment)
            {
                return;
            }

            if ('*' == symbol && '/' == getLookahead() && isBlockComment)
            {
                position++;
                return;
            }
        } while (position < stream.length());
    }
    //</editor-fold>

    //<editor-fold desc="Checkers">
    private boolean isWhitespace (char symbol)
    {
        return ' ' == symbol || '\n' == symbol;
    }

    private boolean isAlpha (char symbol)
    {
        return (symbol >= 'A' && symbol <= 'Z') || (symbol >= 'a' && symbol <= 'z');
    }

    private boolean isDigit (char symbol)
    {
        return symbol >= '0' && symbol <= '9';
    }

    private boolean isLiteral (char symbol)
    {
        return '"' == symbol || '\n' == symbol;
    }

    private boolean isRegex (char symbol)
    {
        // TODO: implement regex literal
        return false;
    }

    private boolean isComment (char symbol)
    {
        char nextSymbol = stream.charAt(position + 1);
        return ('/' == symbol) && ('/' == nextSymbol) || ('*' == nextSymbol);
    }
    //</editor-fold>

    private int position;

    @Override
    public Iterator<Token> iterator() {
        return new TokenGenerator(scan());
    }

    public void reserve (String keyword)
    {
        tokenList.put(keyword, new Token(keyword, TokenClass.Other));
    }
}
