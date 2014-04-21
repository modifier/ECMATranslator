package org.modifier.ecmascript;


import org.modifier.parser.*;
import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;

import java.util.ArrayList;

public class SyntaxTable extends AbstractSyntaxTable
{
    @Override
    public ArrayList<Node> getRule(Token token, NonTerminalNode currentNode) throws SyntaxError
    {
        INodeClass nodeClass = currentNode.getNodeClass();
        ArrayList<Node> result = new ArrayList<>();

        if (nodeClass == NodeClass.Function)
        {
            result.add(new TerminalNode("function"));
            result.add(new TerminalNode(TokenClass.Ident));
            result.add(new TerminalNode("("));
            result.add(new TerminalNode(")"));
            result.add(new NonTerminalNode(NodeClass.Block));
        }
        else if (nodeClass == NodeClass.Block)
        {
            result.add(new TerminalNode("{"));
            result.add(new NonTerminalNode(NodeClass.Statements));
            result.add(new TerminalNode("}"));
        }
        else if (nodeClass == NodeClass.Statements)
        {
            if (token.value.equals("for"))
            {
                result.add(new NonTerminalNode(NodeClass.Loop));
            }
            else if (
                token.classId == TokenClass.Ident ||
                token.value.equals("var") ||
                token.value.equals("break")
            )
            {
                result.add(new NonTerminalNode(NodeClass.Statement));
                result.add(new TerminalNode(";"));
                result.add(new NonTerminalNode(NodeClass.Statements));
            }
        }
        else if (nodeClass == NodeClass.Statement)
        {
            if (token.value.equals("break"))
            {
                result.add(new TerminalNode("break"));
            }
            else if (token.value.equals("var") || token.classId == TokenClass.Ident || token.value.equals("++") || token.value.equals("--"))
            {
                result.add(new NonTerminalNode(NodeClass.Expression));
            }
        }
        else if (nodeClass == NodeClass.Loop)
        {
            result.add(new TerminalNode("for"));
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(NodeClass.Expression));
            result.add(new TerminalNode(";"));
            result.add(new NonTerminalNode(NodeClass.Expression));
            result.add(new TerminalNode(";"));
            result.add(new NonTerminalNode(NodeClass.Expression));
            result.add(new TerminalNode(")"));
            result.add(new NonTerminalNode(NodeClass.Block));
        }
        else if (nodeClass == NodeClass.Expression)
        {
            if (token.value.equals("var"))
            {
                result.add(new TerminalNode("var"));
                result.add(new TerminalNode(TokenClass.Ident));
                result.add(new TerminalNode("="));
                result.add(new NonTerminalNode(NodeClass.PrimaryExpression));
            }
            else if (token.classId == TerminalClass.UnaryOperator)
            {
                result.add(new TerminalNode(TerminalClass.UnaryOperator));
                result.add(new NonTerminalNode(NodeClass.PrimaryExpression));
            }
            else if (token.classId == TokenClass.Ident)
            {
                result.add(new TerminalNode(TokenClass.Ident));
                result.add(new NonTerminalNode(NodeClass.Expression_1));
            }
            else
            {
                throw new SyntaxError();
            }
        }
        else if (nodeClass == NodeClass.Expression_1)
        {
            if (token.classId == TerminalClass.BinaryOperator)
            {
                result.add(new TerminalNode(TerminalClass.BinaryOperator));
                result.add(new NonTerminalNode(NodeClass.PrimaryExpression));
            }
            else if (token.classId == TerminalClass.UnaryOperator)
            {
                result.add(new TerminalNode(TerminalClass.UnaryOperator));
            }
            else
            {
                throw new SyntaxError();
            }
        }
        else if (nodeClass == NodeClass.PrimaryExpression)
        {
            if (token.classId == TokenClass.Other)
            {
                throw new SyntaxError();
            }
            result.add(new TerminalNode(token));
        }
        else
        {
            throw new SyntaxError();
        }
        return result;
    }

    @Override
    public Node getRoot()
    {
        return new NonTerminalNode(NodeClass.Function);
    }
}
