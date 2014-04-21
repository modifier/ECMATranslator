package org.modifier.ecmascript;

import org.modifier.scanner.KeywordInjector;
import org.modifier.scanner.Scanner;

public class ES5Keywords implements KeywordInjector
{
    private String[] keywords = new String[] {
        "break",
        "function",
        "for",
        "++",
        "var"
    };

    @Override
    public void reserveWords(Scanner scanner)
    {
        for (String keyword : keywords)
        {
            scanner.reserve(keyword);
        }
    }
}
