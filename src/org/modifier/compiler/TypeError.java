package org.modifier.compiler;

public class TypeError extends Exception
{
    private final String message;

    private TypeError(String message)
    {
        this.message = "[TypeError] " + message;
    }

    public static TypeError constantIsAlreadyDefined (String name)
    {
        return new TypeError("Constant " + name + " is already defined in current scope.");
    }

    public static TypeError constantCannotBeRedefined (String name)
    {
        return new TypeError("Constant " + name + " cannot be redefined.");
    }

    public String getMessage ()
    {
        return message;
    }
}
