package org.modifier.parser;

import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;
import org.modifier.utils.PositionException;

public class SyntaxError extends PositionException
{
    private SyntaxError(String s, int line, int position)
    {
        super("[SyntaxError] " + s, line, position);
    }

    public static SyntaxError unexpectedToken(Token token, Token expected)
    {
        String expectedName;
        if (expected.classId == TokenClass.get("Other"))
        {
            expectedName = expected.value;
        }
        else
        {
            expectedName = expected.classId.toString();
        }

        return new SyntaxError("Unexpected token. Expected " + expectedName + ", got " + token.toString(), token.getLine(), token.getPosition());
    }

    public static SyntaxError unknownRule(TokenClass rule, int line, int position)
    {
        return new SyntaxError("Unknown rule encountered: '" + rule.toString() + "'", line, position);
    }
}
