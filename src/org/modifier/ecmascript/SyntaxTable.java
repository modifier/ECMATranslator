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
        TokenClass nodeClass = currentNode.getTokenClass();
        ArrayList<Node> result = new ArrayList<>();

        if (nodeClass == TokenClass.get("Function"))
        {
            result.add(new TerminalNode("function"));
            result.add(new TerminalNode(TokenClass.get("Ident")));
            result.add(new TerminalNode("("));
            result.add(new TerminalNode(")"));
            result.add(new NonTerminalNode(TokenClass.get("Block")));
        }
        else if (nodeClass == TokenClass.get("Block"))
        {
            result.add(new TerminalNode("{"));
            result.add(new NonTerminalNode(TokenClass.get("Statements")));
            result.add(new TerminalNode("}"));
        }
        else if (nodeClass == TokenClass.get("Statements"))
        {
            if (token.value.equals("for"))
            {
                result.add(new NonTerminalNode(TokenClass.get("Loop")));
            }
            else if (
                token.classId == TokenClass.get("Ident") ||
                token.value.equals("var") ||
                token.value.equals("break")
            )
            {
                result.add(new NonTerminalNode(TokenClass.get("Statement")));
                result.add(new TerminalNode(";"));
                result.add(new NonTerminalNode(TokenClass.get("Statements")));
            }
        }
        else if (nodeClass == TokenClass.get("Statement"))
        {
            if (token.value.equals("break"))
            {
                result.add(new TerminalNode("break"));
            }
            else if (token.value.equals("var") || token.classId == TokenClass.get("Ident") || token.value.equals("++") || token.value.equals("--"))
            {
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
            }
        }
        else if (nodeClass == TokenClass.get("Loop"))
        {
            result.add(new TerminalNode("for"));
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(TokenClass.get("Expression")));
            result.add(new TerminalNode(";"));
            result.add(new NonTerminalNode(TokenClass.get("Expression")));
            result.add(new TerminalNode(";"));
            result.add(new NonTerminalNode(TokenClass.get("Expression")));
            result.add(new TerminalNode(")"));
            result.add(new NonTerminalNode(TokenClass.get("Block")));
        }
        else if (nodeClass == TokenClass.get("Expression"))
        {
            if (token.value.equals("var"))
            {
                result.add(new TerminalNode("var"));
                result.add(new TerminalNode(TokenClass.get("Ident")));
                result.add(new TerminalNode("="));
                result.add(new NonTerminalNode(TokenClass.get("PrimaryExpression")));
            }
            else if (token.classId == TokenClass.get("UnaryOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("UnaryOperator")));
                result.add(new NonTerminalNode(TokenClass.get("PrimaryExpression")));
            }
            else if (token.classId == TokenClass.get("Ident"))
            {
                result.add(new TerminalNode(TokenClass.get("Ident")));
                result.add(new NonTerminalNode(TokenClass.get("Expression_1")));
            }
            else
            {
                throw new SyntaxError();
            }
        }
        else if (nodeClass == TokenClass.get("Expression_1"))
        {
            if (token.classId == TokenClass.get("BinaryOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("BinaryOperator")));
                result.add(new NonTerminalNode(TokenClass.get("PrimaryExpression")));
            }
            else if (token.classId == TokenClass.get("UnaryOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("UnaryOperator")));
            }
            else
            {
                throw new SyntaxError();
            }
        }
        else if (nodeClass == TokenClass.get("PrimaryExpression"))
        {
            if (token.classId == TokenClass.get("Other"))
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
        return new NonTerminalNode(TokenClass.get("Function"));
    }
}
