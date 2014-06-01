package org.modifier.compiler;

import org.modifier.ecmascript.SyntaxTable;
import org.modifier.parser.*;
import org.modifier.scanner.Lexer;
import org.modifier.scanner.ScanError;
import org.modifier.scanner.Scanner;
import org.modifier.scanner.TokenClass;
import org.modifier.utils.TerminalReader;
import org.modifier.utils.TerminalReaderException;

public class ESParser
{
    private static ESParser parser;
    private String grammar;
    private Node root = null;

    private ESParser() {}

    public ESParser setGrammar(String grammar)
    {
        this.grammar = grammar;
        return this;
    }

    public static ESParser get()
    {
        if (parser == null)
        {
            parser = new ESParser();
        }

        return parser;
    }

    public Node processImmediate(String stream, TokenClass root)
    {
        setRoot(new NonTerminalNode(root));
        try
        {
            return process(stream);
        }
        catch(Exception e)
        {
            throw new RuntimeException();
        }
        finally
        {
            setRoot(null);
        }
    }

    public Node process(String stream) throws SyntaxError, TerminalReaderException, ScanError, TypeError
    {
        TerminalReader reader = new TerminalReader(grammar);

        Scanner scanner = new Scanner(stream);
        for (String word : reader.getKeywords())
        {
            scanner.reserve(word);
        }
        scanner.scan();

        Lexer lexer = new Lexer(scanner);
        lexer.setCorrespondence(reader.getCorrespondence());

        AbstractSyntaxTable table = new SyntaxTable();
        Parser parser = new Parser(lexer, table);

        if (root != null)
        {
            parser.setRoot(root);
        }

        Node tree = parser.getTree();

        Scoper scoper = new Scoper((NonTerminalNode)tree);
        scoper.process();

        return tree;
    }

    public void setRoot(Node root)
    {
        this.root = root;
    }
}
