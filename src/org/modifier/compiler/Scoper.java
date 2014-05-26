package org.modifier.compiler;

import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.parser.TerminalNode;
import org.modifier.scanner.TokenClass;

import java.util.ArrayList;

public class Scoper
{
    private NonTerminalNode root;

    public Scoper (NonTerminalNode root)
    {
        this.root = root;
    }

    public NonTerminalNode process ()
    {
        explore(root);
        return root;
    }

    public void explore (NonTerminalNode root)
    {
        check(root);

        for (Node node : root.getChildren())
        {
            if (node instanceof NonTerminalNode)
            {
                explore((NonTerminalNode)node);
            }
        }
    }

    private void check (NonTerminalNode node)
    {
        ArrayList<String> names = new ArrayList<>();
        boolean isVarScope = true;
        if (node.getNodeClass() == TokenClass.get("FunctionDeclaration"))
        {
            names.add(((TerminalNode)(node).findNodeClass("Ident")).getToken().value);
        }
        else if (node.getNodeClass() == TokenClass.get("FunctionExpression_1"))
        {
            Node first = node.getChildren().get(0);
            if (first.getNodeClass() != TokenClass.get("Ident"))
            {
                return;
            }
            names.add(((TerminalNode)first).getToken().value);
        }
        else if (node.getNodeClass() == TokenClass.get("ForStatement_1") || node.getNodeClass() == TokenClass.get("VariableStatement"))
        {
            Node first = node.getChildren().get(0);
            if (first.getNodeClass() != TokenClass.get("Declarator"))
            {
                return;
            }
            isVarScope = ((TerminalNode)first).getToken().value.equals("var");

            NonTerminalNode currentList = (NonTerminalNode)(node).findNodeClass("VariableDeclarationList");
            do
            {
                TerminalNode declaration = (TerminalNode)(((NonTerminalNode)(currentList.findNodeClass("VariableDeclaration"))).findNodeClass("Ident"));
                names.add(declaration.getToken().value);
                currentList = (NonTerminalNode)currentList.findNodeClass("VariableDeclarationList_1");
            } while (currentList.getChildren().size() != 0);
        }
        else
        {
            return;
        }

        NonTerminalNode scope = isVarScope ? node.closestVarBlock() : node.closestLetBlock();
        for (String ident : names)
        {
            scope.getScope().addIdent(ident);
        }
    }
}
