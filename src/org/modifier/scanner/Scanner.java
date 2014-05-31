package org.modifier.scanner;

import org.modifier.utils.FilteredSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Scanner implements Iterable<Token>
{
    private final ArrayList<Token> result = new ArrayList<>();
    private final HashMap<String, Token> tokenList = new HashMap<>();
    private final String stream;
    private int position;

    public Scanner(String stream)
    {
        this.stream = stream;
    }

    public ArrayList<Token> scan ()
    {
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

        return getToken(finalString, TokenClass.get("Ident"));
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

        return getToken(finalString, TokenClass.get("Const"));
    }

    private Token continuousMatch(String tokenType)
    {
        // TODO: there is no line wrapping in javascript
        final char startCharacter = stream.charAt(position++);
        StringBuilder accumulator = new StringBuilder();
        accumulator.append(startCharacter);
        char symbol = 0;
        boolean escaping = false;

        do
        {
            if ('\\' == symbol)
            {
                escaping = !escaping;
            }
            else
            {
                escaping = false;
            }

            symbol = stream.charAt(position++);
            accumulator.append(symbol);
        }
        while (!(startCharacter == symbol && !escaping));

        String finalString = accumulator.toString();

        return getToken(finalString, TokenClass.get(tokenType));
    }

    private Token matchLiteral()
    {
        return continuousMatch("Literal");
    }

    private ArrayList<Token> matchToken()
    {
        ArrayList<Token> result = new ArrayList<>();

        StringBuilder accumulator = new StringBuilder();
        char symbol;

        FilteredSet keys = new FilteredSet(tokenList.keySet());

        ArrayList<String> tokenStrings = new ArrayList<>();
        boolean added = false;

        do
        {
            symbol = stream.charAt(position++);
            accumulator.append(symbol);

            // TODO: make correct fallback to values longer than 1 symbol
            if (!keys.containsPartialKey(accumulator.toString()))
            {
                String tokenString = accumulator.toString();
                for (int i = 0; i < tokenString.length(); i++)
                {
                    tokenStrings.add(String.valueOf(tokenString.charAt(i)));
                }
                added = true;
            }

            if (1 == keys.size() && keys.contains(accumulator.toString())) {
                tokenStrings.add(accumulator.toString());
                added = true;
                break;
            }
        }
        while (!isWhitespace(getLookahead()) && !isAlpha(getLookahead()) && !isDigit(getLookahead()) && (0 != keys.size()));

        if (keys.size() >= 1 && !added)
        {
            tokenStrings.add(accumulator.toString());
        }

        for (String tokenString : tokenStrings) {
            result.add(getToken(tokenString, TokenClass.get("Other")));
        }

        return result;
    }

    private Token matchRegex()
    {
        return continuousMatch("Regex");
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
        return (symbol >= 'A' && symbol <= 'Z') || (symbol >= 'a' && symbol <= 'z') || (symbol == '_') || (symbol == '$');
    }

    private boolean isDigit (char symbol)
    {
        return symbol >= '0' && symbol <= '9';
    }

    private boolean isLiteral (char symbol)
    {
        return '"' == symbol || '\'' == symbol;
    }

    private boolean isRegex (char symbol)
    {
        return '/' == symbol;
    }

    private boolean isComment (char symbol)
    {
        char nextSymbol = stream.charAt(position + 1);
        return ('/' == symbol) && ('/' == nextSymbol) || ('*' == nextSymbol);
    }
    //</editor-fold>

    @Override
    public Iterator<Token> iterator() {
        return new TokenGenerator(result);
    }

    public void reserve (String keyword)
    {
        tokenList.put(keyword, new Token(keyword, TokenClass.get("Other")));
    }
}
