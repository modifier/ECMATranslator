package org.modifier.utils;

import java.util.HashSet;
import java.util.Set;

public class FilteredSet
{
    private Set<String> underlying;

    public FilteredSet (Set<String> set)
    {
        underlying = set;
    }

    public boolean contains(String key)
    {
        return underlying.contains(key);
    }


    public boolean containsPartialKey(String partialKey)
    {
        Set<String> newUnderlying = new HashSet<>(underlying);
        for (String key : underlying)
        {
            if (!key.startsWith(partialKey))
            {
                newUnderlying.remove(key);
            }
        }

        underlying = newUnderlying;

        return underlying.size() != 0;
    }

    public int size()
    {
        return underlying.size();
    }
}
