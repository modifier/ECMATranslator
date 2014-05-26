package org.modifier.compiler;

public class VariableException extends Exception
{
    private final String message;

    public VariableException (String name)
    {
        message = "Constant " + name + " is already defined in current scope.";
    }

    public String getMessage ()
    {
        return message;
    }
}
