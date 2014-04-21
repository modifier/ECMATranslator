package org.modifier.ecmascript;

import org.modifier.lexer.AbstractLexer;
import org.modifier.scanner.Scanner;
import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;

public class Lexer extends AbstractLexer
{
    public Lexer(Scanner scanner)
    {
        super(scanner);
    }

    @Override
    protected Token convertToken(Token token)
    {
        if (token.classId != TokenClass.Other)
        {
            return token;
        }

        if (token.value.equals("<") || token.value.equals(">") || token.value.equals("+") ||
            token.value.equals("-") || token.value.equals("/") || token.value.equals("*") ||
            token.value.equals("==") || token.value.equals(">=") || token.value.equals("<=")
        ) {
            return new Token (token.value, TerminalClass.BinaryOperator);
        }

        if (token.value.equals("++") || token.value.equals("--"))
        {
            return new Token (token.value, TerminalClass.UnaryOperator);
        }

        return token;
    }
}
