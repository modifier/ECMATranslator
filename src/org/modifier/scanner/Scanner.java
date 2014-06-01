package org.modifier.scanner;

import org.modifier.utils.LongestMatch;

import java.util.ArrayList;
import java.util.Iterator;

public class Scanner implements Iterable<Token>
{
    private final ArrayList<Token> result = new ArrayList<>();
    private final ArrayList<String> tokenList = new ArrayList<>();
    private final String stream;
    private int position;

    public Scanner(String stream)
    {
        this.stream = stream;
    }

    public ArrayList<Token> scan () throws ScannerException
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
                result.add(matchToken());
            }
        } while (stream.length() > position);

        return result;
    }

    private Token getToken(String key, TokenClass classId)
    {
        if (tokenList.contains(key))
        {
            classId = TokenClass.get("Other");
        }

        return new Token(key, classId);
    }

    private char getLookahead()
    {
        return stream.charAt(position);
    }

    //<editor-fold desc="Matchers">
    private Token matchIdent()
    {
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

    private Token continuousMatch(String tokenType) throws ScannerException
    {
        // TODO: there is no line wrapping in javascript
        final char startCharacter = stream.charAt(position++);
        StringBuilder accumulator = new StringBuilder();
        accumulator.append(startCharacter);
        char symbol = 0;
        boolean escaping = false;

        do
        {
            if ('\n' == symbol)
            {
                throw new ScannerException("Unexpected end of the line.");
            }
            else if ('\\' == symbol)
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

    private Token matchLiteral() throws ScannerException
    {
        return continuousMatch("Literal");
    }

    private Token matchToken()
    {
        int position = this.position;
        LongestMatch matcher = new LongestMatch(tokenList);

        do
        {
            char symbol = stream.charAt(position++);
            matcher.append(symbol);
        } while (matcher.isSolvable() && !matcher.hasSolution());

        String solution = matcher.getSolution();
        this.position += solution.length();

        return getToken(solution, TokenClass.get("Other"));
    }

    private Token matchRegex() throws ScannerException
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
        tokenList.add(keyword);
    }
}
