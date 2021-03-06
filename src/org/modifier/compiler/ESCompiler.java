package org.modifier.compiler;
import org.modifier.parser.*;
import org.modifier.scanner.TokenClass;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class ESCompiler
{
    public static void main (String[] args) throws ParseException, FileNotFoundException
    {
        try
        {
            if (args.length == 0)
            {
                System.out.println("Usage: es6translate -i input_file [-o output_file]");
                return;
            }
            Map<String, String> opts = parseOpts(args);
            if (!opts.containsKey("i"))
            {
                System.out.println("Missing required parameter -i (input file).");
                return;
            }

            InputStream stream = new FileInputStream(opts.get("i"));

            InputStream grammar = System.class.getResourceAsStream("/terminals.res");
            InputStream polyfill = System.class.getResourceAsStream("/polyfill.res");

            PrintStream output = null;
            if (opts.containsKey("o"))
            {
                output = new PrintStream(new FileOutputStream(opts.get("o")));
            }

            ESParser parser = ESParser.get().setGrammar(getStringFromInputStream(grammar));

            Node tree = parser.process(getStringFromInputStream(stream));

            Polyfiller polyfiller = new Polyfiller(getStringFromInputStream(polyfill));

            Translator translator = new Translator((NonTerminalNode)tree);
            translator.setPolyfiller(polyfiller);
            translator.convert();

            if (output == null)
            {
                System.out.print(polyfiller.getShims());
                System.out.print(tree.toString());
                System.out.println();
            }
            else
            {
                output.append(polyfiller.getShims());
                output.append(tree.toString());
            }
        }
        catch (Exception e)
        {
            if (e.getMessage() == null)
            {
                e.printStackTrace();
            }
            else
            {
                System.out.println(e.getMessage());
            }
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

        System.out.print(result.getTokenClass().toString());
        if (result instanceof NonTerminalNode || result.getNodeClass() == TokenClass.get("<EOF>"))
        {
            System.out.println();
        }
        else
        {
            System.out.println(' ' + result.toString());
        }

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
