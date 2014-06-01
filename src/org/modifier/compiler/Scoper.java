package org.modifier.compiler;

import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.parser.TerminalNode;
import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;

import java.util.HashMap;

public class Scoper
{
    private NonTerminalNode root;

    public Scoper (NonTerminalNode root)
    {
        this.root = root;
    }

    public NonTerminalNode process () throws TypeError
    {
        explore(root);
        return root;
    }

    public void explore (NonTerminalNode root) throws TypeError
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

    private void check (NonTerminalNode node) throws TypeError
    {
        HashMap<Token, Node> valueMap = new HashMap<>();
        Token functionName = null;
        boolean isVarScope = true;
        boolean isConst = false;
        if (node.getNodeClass() == TokenClass.get("FunctionDeclaration"))
        {
            functionName = ((TerminalNode)(node).findNodeClass("Ident")).getToken();
        }
        else if (node.getNodeClass() == TokenClass.get("FunctionExpression_1"))
        {
            Node first = node.getChildren().get(0);
            if (first.getNodeClass() != TokenClass.get("Ident"))
            {
                return;
            }
            functionName = ((TerminalNode) first).getToken();
        }
        else if (node.getNodeClass() == TokenClass.get("ForStatement_1") || node.getNodeClass() == TokenClass.get("VariableStatement"))
        {
            Node first = node.getChildren().get(0);
            if (first.getNodeClass() != TokenClass.get("Declarator"))
            {
                return;
            }
            isConst = ((TerminalNode)first).getToken().value.equals("const");
            isVarScope = isConst || ((TerminalNode)first).getToken().value.equals("var");

            NonTerminalNode currentList = (NonTerminalNode)(node).findNodeClass("VariableDeclarationList");
            do
            {
                NonTerminalNode declaration = (NonTerminalNode)(currentList.findNodeClass("VariableDeclaration"));

                TerminalNode ident = (TerminalNode)declaration.findNodeClass("Ident");
                Node evaluation = ((NonTerminalNode)(declaration.findNodeClass("VariableDeclaration_1"))).findNodeClass("AssignmentExpression");
                valueMap.put(ident.getToken(), evaluation);

                currentList = (NonTerminalNode)currentList.findNodeClass("VariableDeclarationList_1");
            } while (currentList.getChildren().size() != 0);
        }
        else
        {
            return;
        }

        NonTerminalNode scope = isVarScope ? node.closestVarBlock() : node.closestLetBlock();
        for (Token ident : valueMap.keySet())
        {
            if (isConst)
            {
                scope.getScope().addConst(ident, valueMap.get(ident));
            }
            else
            {
                scope.getScope().addIdent(ident);
            }
        }

        if (functionName != null)
        {
            scope.getScope().addIdent(functionName);
        }
    }
}
