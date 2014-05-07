package org.modifier.utils;

import java.util.*;

public class TerminalReader
{
    private String input;
    private ArrayList<String> keywords = new ArrayList<>();
    private HashMap<String, ArrayList<String>> correspondence = new HashMap<>();
    public TerminalReader (String input) throws Exception
    {
        this.input = input;
        this.process();
    }

    private void process() throws Exception
    {
        String[] rows = input.split("\n");
        for (String row : rows)
        {
            if (row.trim().startsWith("#"))
            {
                continue;
            }

            if (!row.contains("::="))
            {
                int start = row.indexOf("\"");
                int end = row.lastIndexOf("\"");
                keywords.add(row.substring(start + 1, end));
            }
            else
            {
                String[] keyValue = row.split("::=");
                ArrayList<String> values = this.splitLiterals(keyValue[1], '|');
                ArrayList<String> parsedValues = new ArrayList<>();
                for (String value : values)
                {
                    int start = value.indexOf("\"");
                    int end = value.lastIndexOf("\"");
                    value = value.substring(start + 1, end);

                    if (value.equals(""))
                    {
                        throw new Exception("Empty terminal");
                    }

                    if (value.length() > 1)
                    {
                        keywords.add(value);
                    }
                    parsedValues.add(value);
                }
                correspondence.put(keyValue[0].trim(), parsedValues);
            }
        }
    }

    private ArrayList<String> splitLiterals(String input, char delimiter)
    {
        int position = 0;
        ArrayList<String> result = new ArrayList<>();
        boolean isQuoted = false;
        StringBuilder builder = new StringBuilder();
        do
        {
            if (input.charAt(position) == '"' || input.charAt(position) == '\'')
            {
                isQuoted = !isQuoted;
            }

            if (input.charAt(position) == delimiter && !isQuoted)
            {
                result.add(builder.toString());
                builder = new StringBuilder();
            }
            else
            {
                builder.append(input.charAt(position));
            }
        } while (++position < input.length());
        result.add(builder.toString());

        return result;
    }

    public ArrayList<String> getKeywords()
    {
        return keywords;
    }

    public HashMap<String, ArrayList<String>> getCorrespondence()
    {
        return correspondence;
    }
}
