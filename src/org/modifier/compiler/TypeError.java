package org.modifier.compiler;

import org.modifier.scanner.Token;
import org.modifier.utils.PositionException;

public class TypeError extends PositionException
{
    private TypeError(String s, int line, int position)
    {
        super("[TypeError] " + s, line, position);
    }

    public static TypeError constantIsAlreadyDefined (Token ident)
    {
        return new TypeError("Constant " + ident.value + " is already defined in current scope", ident.getLine(), ident.getPosition());
    }

    public static TypeError constantCannotBeRedefined (Token ident)
    {
        return new TypeError("Constant " + ident.value + " cannot be redefined", ident.getLine(), ident.getPosition());
    }
}
