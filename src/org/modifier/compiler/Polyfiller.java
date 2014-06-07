package org.modifier.compiler;

import java.util.*;

public class Polyfiller
{
    private HashMap<String, String> list = new HashMap<>();

    private ArrayList<String> usedClasses = new ArrayList<>();
    private ArrayList<String> usedMethods = new ArrayList<>();

    public Polyfiller(String file)
    {
        processFile(file);
    }

    public void reservePrototypeMethod(String word)
    {
        if (!usedMethods.contains(word))
        {
            usedMethods.add(word);
        }
    }

    public void reserveClassName(String word)
    {
        if (!usedClasses.contains(word))
        {
            usedClasses.add(word);
        }
    }

    public String getShims()
    {
        StringBuilder shim = new StringBuilder();
        for (String key : list.keySet())
        {
            String[] parts = key.split("\\.");
            if (parts.length > 1 && usedMethods.contains(parts[parts.length-1]))
            {
                shim.append(list.get(key));
            }
            else if (parts.length == 1 && usedClasses.contains(parts[0]))
            {
                shim.append(list.get(key));
            }
        }
        return shim.toString();
    }

    private void processFile(String file)
    {
        String[] lines = file.split("\n");
        String signature = null;
        StringBuilder body = new StringBuilder();
        for (String line : lines)
        {
            if (line.startsWith("#"))
            {
                continue;
            }

            if (line.equals("==="))
            {
                list.put(signature, body.toString());
                signature = null;
                body = new StringBuilder();
                continue;
            }

            if (signature == null)
            {
                signature = line;
            }
            else
            {
                body.append(line).append("\n");
            }
        }
    }
}
