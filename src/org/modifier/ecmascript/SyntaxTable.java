package org.modifier.ecmascript;


import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.scanner.Token;
import org.modifier.parser.AbstractSyntaxTable;
import java.util.ArrayList;

public class SyntaxTable extends AbstractSyntaxTable
{
    @Override
    public ArrayList<Node> getRule(Token token, NonTerminalNode currentNode)
    {
        return null;
    }

    @Override
    public Node getRoot()
    {
        return new NonTerminalNode(NodeClass.Function);
    }
}
