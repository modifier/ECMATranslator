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
    private int line = 1;
    private int linePos = 0;

    public Scanner(String stream)
    {
        this.stream = stream;
    }

    private int incPos()
    {
        if (stream.charAt(position) == '\n')
        {
            line++;
            linePos = 0;
        }
        linePos++;
        return position++;
    }

    private int incPos(int count)
    {
        for (int i = 0; i < count; i++)
        {
            incPos();
        }

        return position;
    }

    public ArrayList<Token> scan () throws ScanError
    {
        do
        {
            char symbol = stream.charAt(position);
            int line = this.line, linePos = this.linePos;
            Token token;

            if (isWhitespace(symbol))
            {
                incPos();
                continue;
            }
            if (isAlpha(symbol))
            {
                token = matchIdent();
            }
            else if (isDigit(symbol))
            {
                token = matchConst();
            }
            else if (isLiteral(symbol))
            {
                token = matchLiteral();
            }
            else if (isRegex(symbol))
            {
                token = matchRegex();
            }
            else if (isComment(symbol))
            {
                skipComment();
                continue;
            }
            else
            {
                token = matchToken();
            }

            token.setPosition(line, linePos);
            result.add(token);
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
            symbol = stream.charAt(incPos());
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
            symbol = stream.charAt(incPos());
            accumulator.append(symbol);
            if ('.' == symbol) {
                wasPoint = true;
            }
        }
        while (isDigit(getLookahead()) || ('.' == getLookahead() && !wasPoint));

        String finalString = accumulator.toString();

        return getToken(finalString, TokenClass.get("Const"));
    }

    private Token continuousMatch(String tokenType) throws ScanError
    {
        final char startCharacter = stream.charAt(incPos());
        StringBuilder accumulator = new StringBuilder();
        accumulator.append(startCharacter);
        char symbol = 0;
        boolean escaping = false;

        do
        {
            if ('\n' == getLookahead())
            {
                throw ScanError.unexpectedEOL(line, linePos);
            }

            if ('\\' == symbol)
            {
                escaping = !escaping;
            }
            else
            {
                escaping = false;
            }

            symbol = stream.charAt(incPos());
            accumulator.append(symbol);
        }
        while (!(startCharacter == symbol && !escaping));

        String finalString = accumulator.toString();

        return getToken(finalString, TokenClass.get(tokenType));
    }

    private Token matchLiteral() throws ScanError
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
        } while (matcher.isSolvable() && !matcher.hasSolution() && position < stream.length() - 1);

        String solution = matcher.getSolution();
        incPos(solution.length());

        return getToken(solution, TokenClass.get("Other"));
    }

    private Token matchRegex() throws ScanError
    {
        return continuousMatch("Regex");
    }

    private void skipComment()
    {
        Boolean isBlockComment = stream.charAt(incPos() + 1) == '*';

        do
        {
            char symbol = stream.charAt(position++);

            if ('\n' == symbol && !isBlockComment)
            {
                return;
            }

            if ('*' == symbol && '/' == getLookahead() && isBlockComment)
            {
                incPos();
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
        // TODO: check whether line is a regex or a division symbol
        return '/' == symbol;
    }

    private boolean isComment (char symbol)
    {
        if (position == stream.length() - 1)
        {
            return false;
        }
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
