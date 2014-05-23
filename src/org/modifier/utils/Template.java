package org.modifier.utils;

import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.parser.TerminalNode;
import java.util.ArrayList;

public class Template
{
    public static TreeGenerator expression (Node node)
    {
        String variable;
        if (node instanceof TerminalNode)
        {
            variable = "(" + ((TerminalNode) node).getToken().classId + "," + ((TerminalNode) node).getToken().value + ")";
        }
        else
        {
            variable = "$Temporary";
        }
        TreeGenerator result = new TreeGenerator("$AssignmentExpression { LeftHandSideExpression { MemberExpression { PrimaryExpression { " + variable + " } }  } }");

        if (node instanceof NonTerminalNode)
        {
            result.get(1).update((NonTerminalNode)node);
        }

        return result;
    }

    public static TreeGenerator accessor (Node array, Node key)
    {
        String variable;
        if (array instanceof TerminalNode)
        {
            variable = "(" + ((TerminalNode) array).getToken().classId + "," + ((TerminalNode) array).getToken().value + ")";
        }
        else
        {
            variable = "$Placeholder";
        }
        TreeGenerator result = new TreeGenerator("$AssignmentExpression { LeftHandSideExpression { MemberExpression { PrimaryExpression { " + variable + " } } MemberExpressionPart { '[' $Expression ']' } } }");

        NonTerminalNode keyNode;
        if (array instanceof NonTerminalNode)
        {
            result.get(1).update((NonTerminalNode)array);
            keyNode = result.get(2);
        }
        else
        {
            keyNode = result.get(1);
        }

        keyNode.appendChild(key);

        return result;
    }

    public static TreeGenerator block (int count)
    {
        StringBuilder b = new StringBuilder();
        b.append("$Block { '{' StatementList { ");
        for (int i = 0; i < count; i++)
        {
            b.append("$Statement ");
        }
        b.append("} '}' }");

        return new TreeGenerator(b.toString());
    }

    public static TreeGenerator declaration (String name)
    {
        ArrayList<String> result = new ArrayList<>();
        result.add(name);
        return declaration(result);
    }

    public static TreeGenerator declaration (ArrayList<String> names)
    {
        StringBuilder b = new StringBuilder();
        b.append("$Statement { 'var' VariableDeclarationList {");
        for (int i = 0; i < names.size(); i++)
        {
            if (i != 0)
            {
                b.append("',' ");
            }
            b.append("VariableDeclaration { (Ident,");
            b.append(names.get(i));
            b.append(") VariableDeclaration_1 { '=' $AssignmentExpression } }");
        }
        b.append("';'} }");
        return new TreeGenerator(b.toString());
    }

    public static TreeGenerator ifStatement (Node condition, Node body)
    {
        TreeGenerator result = new TreeGenerator("$Statement { IfStatement { 'if' '(' $Placeholder ')' Block { '{' $Placeholder '}' } } }");
        result.get(1).appendChild(condition);
        result.get(2).appendChild(body);
        return result;
    }
}
