package org.modifier.compiler;

import org.modifier.ecmascript.SyntaxTable;
import org.modifier.parser.AbstractSyntaxTable;
import org.modifier.parser.Node;
import org.modifier.parser.Parser;
import org.modifier.parser.SyntaxError;
import org.modifier.scanner.Lexer;
import org.modifier.scanner.Scanner;
import org.modifier.scanner.ScannerException;
import org.modifier.utils.TerminalReader;
import org.modifier.utils.TerminalReaderException;

public class ESParser
{
    private static ESParser parser;
    private String grammar;

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

    public Node process(String stream) throws SyntaxError, TerminalReaderException, ScannerException
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

        return parser.getTree();
    }
}
