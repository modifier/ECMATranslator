package org.modifier.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class LongestMatch
{
    private Set<String> underlying;
    private StringBuilder accumulator = new StringBuilder();
    private Stack<String> values = new Stack<>();

    public LongestMatch (ArrayList<String> set)
    {
        underlying = new HashSet<>(set);
    }

    public void append(char symbol)
    {
        accumulator.append(symbol);

        HashSet<String> newUnderlying = new HashSet<>();
        for (String key : underlying)
        {
            if (key.startsWith(accumulator.toString()))
            {
                newUnderlying.add(key);
            }
        }

        underlying = newUnderlying;

        // 1-symbol string is always resolvable
        if (accumulator.length() == 1)
        {
            values.add(accumulator.toString());
        }
        else if (underlying.contains(accumulator.toString()))
        {
            values.add(accumulator.toString());
        }
    }

    public boolean isSolvable()
    {
        return underlying.size() > 0;
    }

    public boolean hasSolution()
    {
        return underlying.size() == 1 && underlying.contains(accumulator.toString());
    }

    public String getSolution()
    {
        return values.pop();
    }
}
