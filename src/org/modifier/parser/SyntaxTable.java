package org.modifier.parser;

import java.util.ArrayList;
import org.modifier.scanner.Token;

public abstract class SyntaxTable
{
    abstract public ArrayList<Node> getRule(Token token, Node currentNode);

    abstract public Node getRoot();
}
