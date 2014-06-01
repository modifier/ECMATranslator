package org.modifier.utils;

public class PositionException extends Exception
{
    protected int line;
    protected int position;

    public PositionException(String s, int line, int position)
    {
        super(s + " at " + line + ":" + position);
        this.line = line;
        this.position = position;
    }
}
