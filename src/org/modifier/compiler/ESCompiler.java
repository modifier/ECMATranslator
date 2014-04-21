package org.modifier.compiler;

import com.sun.java_cup.internal.runtime.lr_parser;
import org.modifier.ecmascript.SyntaxTable;
import org.modifier.parser.*;
import org.modifier.scanner.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class ESCompiler
{
    public static void main (String[] args) throws ParseException, FileNotFoundException
    {
        Map<String, String> opts = parseOpts(args);
        InputStream stream = new FileInputStream(opts.get("i"));

        org.modifier.scanner.Scanner scanner = new org.modifier.scanner.Scanner(getStringFromInputStream(stream));
        ES5Keywords keywords = new ES5Keywords();
        keywords.reserveWords(scanner);

        AbstractSyntaxTable table = new SyntaxTable();
        Parser parser = new Parser(scanner, table);

        Node result;

        try
        {
            result = parser.getTree();

            printResult(result);
        }
        catch (SyntaxError syntaxError)
        {
            syntaxError.printStackTrace();
        }
    }

    public static HashMap<String, String> parseOpts (String[] args) throws ParseException
    {
        // TODO: use normal options parser instead
        HashMap<String, String> result = new HashMap<>();

        for (int i = 0; i < args.length; i++)
        {
            switch (args[i])
            {
                case "-i":
                    result.put("i", args[++i]);
                    break;

                case "-o":
                    result.put("o", args[++i]);
                    break;

                default:
                    throw new ParseException("Incorrect options", 1);
            }
        }

        return result;
    }

    private static String getStringFromInputStream (InputStream is)
    {
        // Copy-pasted from http://www.mkyong.com/java/how-to-convert-inputstream-to-string-in-java/
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try
        {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append('\n');
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    private static void printResult(Node result)
    {
        printResult(result, 0, false);
    }

    private static void printResult(Node result, int offset, boolean isLast)
    {
        System.out.print(repeatSymbol(" ", offset - 1));
        if (offset != 0)
        {
            System.out.print(isLast ? "└ " : "├ ");
        }
        System.out.println(result.toString());

        if (result instanceof TerminalNode)
        {
            return;
        }

        ArrayList<Node> children = ((NonTerminalNode)result).getChildren();

        for (int i = 0; i < children.size(); i++)
        {
            printResult(children.get(i), offset + 2, i == children.size() - 1);
        }
    }

    private static String repeatSymbol(String symbol, int times)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++)
        {
            builder.append(symbol);
        }
        return builder.toString();
    }
}
