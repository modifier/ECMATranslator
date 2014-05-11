package org.modifier.scanner;

import java.util.*;

public class TokenClass
{
    private static HashMap<String, TokenClass> nonTerminals = new HashMap<>();

    private String name;
    private TokenClass(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public static TokenClass get (String name)
    {
        if (!nonTerminals.containsKey(name))
        {
            nonTerminals.put(name, new TokenClass(name));
        }
        return nonTerminals.get(name);
    }
}
