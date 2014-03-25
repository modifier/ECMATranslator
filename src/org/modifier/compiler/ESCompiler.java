package org.modifier.compiler;

import org.modifier.scanner.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class ESCompiler {
    public static void main (String[] args) throws ParseException, FileNotFoundException
    {
        Map<String, String> opts = parseOpts(args);
        InputStream stream = new FileInputStream(opts.get("i"));

        org.modifier.scanner.Scanner scanner = new org.modifier.scanner.Scanner(getStringFromInputStream(stream));
        ES5Keywords keywords = new ES5Keywords();
        keywords.reserveWords(scanner);

        for (Token token : scanner) {
            System.out.println(token.toString());
        }
    }

    public static HashMap<String, String> parseOpts (String[] args) throws ParseException
    {
        // TODO: use normal options parser instead
        HashMap<String, String> result = new HashMap<>();

        for (int i = 0; i < args.length; i++)
        {
            switch (args[i]) {
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
}
