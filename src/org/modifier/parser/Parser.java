package org.modifier.parser;

import java.util.ArrayList;
import org.modifier.scanner.Scanner;
import org.modifier.scanner.Token;

public class Parser
{
    private Scanner scanner;
    private AbstractSyntaxTable table;
    private ArrayList<Node> linearTree = new ArrayList<>();
    private Node root;

    public Parser(Scanner scanner, AbstractSyntaxTable table)
    {
        this.scanner = scanner;
        this.table = table;
        this.root = table.getRoot();
        this.linearTree.add(root);
    }

    public void getTree() throws SyntaxError
    {
        int pointer = 0;
        Node currentRule = linearTree.get(pointer);
        for(Token currentToken : scanner)
        {
            do {
                ArrayList<Node> rule = table.getRule(currentToken, currentRule);
                linearTree.addAll(pointer, rule);
                currentRule = linearTree.get(pointer);
            } while (currentRule instanceof NonTerminalNode);

            if (!((TerminalNode)currentRule).fitsToken(currentToken))
            {
                throw new SyntaxError();
            }

            ((TerminalNode) currentRule).setToken(currentToken);
            pointer++;
        }
    }
}
