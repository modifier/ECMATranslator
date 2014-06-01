package org.modifier.parser;

import java.util.ArrayList;
import org.modifier.scanner.Token;

public class Parser
{
    private Iterable<Token> scanner;
    private AbstractSyntaxTable table;
    private ArrayList<Node> linearTree;
    private Node root;

    public Parser(Iterable<Token> scanner, AbstractSyntaxTable table)
    {
        this.scanner = scanner;
        this.table = table;
        this.root = table.getRoot();
    }

    public void setRoot(Node root)
    {
        this.root = root;
    }

    public Node getTree() throws SyntaxError
    {
        linearTree =  new ArrayList<>();
        linearTree.add(root);
        int pointer = 0;
        for(Token currentToken : scanner)
        {
            Node currentRule = linearTree.get(pointer);
            while (currentRule instanceof NonTerminalNode)
            {
                ArrayList<Node> rule = table.getRule(currentToken, currentRule);
                for (Node subrule : rule)
                {
                    subrule.setParent((NonTerminalNode)currentRule);
                }
                ((NonTerminalNode) currentRule).setChildren(rule);
                linearTree.remove(pointer);
                linearTree.addAll(pointer, rule);
                currentRule = linearTree.get(pointer);
            }

            if (!((TerminalNode)currentRule).fitsToken(currentToken))
            {
                throw SyntaxError.unexpectedToken(currentToken, ((TerminalNode) currentRule).getToken());
            }

            ((TerminalNode) currentRule).setToken(currentToken);
            pointer++;
        }
        return this.root;
    }
}
