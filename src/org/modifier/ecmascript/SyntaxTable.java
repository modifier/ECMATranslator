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

        if (nodeClass == TokenClass.get("PrimaryExpression"))
        {
            if (token.value.equals("this"))
            {
                result.add(new TerminalNode("this"));
            }
            else if (token.classId == TokenClass.get("Literal"))
            {
                result.add(new TerminalNode(TokenClass.get("Literal")));
            }
            else if (token.value.equals("["))
            {
                result.add(new NonTerminalNode(TokenClass.get("ArrayLiteral")));
            }
            else if (token.value.equals("{"))
            {
                result.add(new NonTerminalNode(TokenClass.get("ObjectLiteral")));
            }
        }
        else if (nodeClass == TokenClass.get("ArrayLiteral"))
        {
            result.add(new TerminalNode("["));
            result.add(new NonTerminalNode(TokenClass.get("ElementList")));
            result.add(new TerminalNode("]"));
        }
        else if (nodeClass == TokenClass.get("ElementList"))
        {
            boolean fits;
            try
            {
                fits = TokenClass.get("AssignmentExpression").fits(token);
            }
            catch (Exception e)
            {
                throw new SyntaxError();
            }
            if (token.value.equals(","))
            {
                result.add(new TerminalNode(","));
                result.add(new NonTerminalNode(TokenClass.get("ElementList")));
            }
            else if (fits)
            {
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
                result.add(new NonTerminalNode(TokenClass.get("ElementList")));
            }
        }
        else if (nodeClass == TokenClass.get("ObjectLiteral"))
        {
            result.add(new TerminalNode("{"));
            result.add(new NonTerminalNode(TokenClass.get("PropertyNameAndValueList")));
            result.add(new TerminalNode("}"));
        }
        else if (nodeClass == TokenClass.get("PropertyNameAndValueList"))
        {
            result.add(new NonTerminalNode(TokenClass.get("PropertyName")));
            result.add(new TerminalNode(":"));
            result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
            result.add(new NonTerminalNode(TokenClass.get("PropertyNameAndValueList_1")));
        }
        else if (nodeClass == TokenClass.get("PropertyNameAndValueList_1"))
        {
            if (token.value.equals(","))
            {
                result.add(new TerminalNode(","));
                result.add(new NonTerminalNode(TokenClass.get("PropertyNameAndValueList")));
            }
        }
        else if (nodeClass == TokenClass.get("PropertyName"))
        {
            if (token.classId == TokenClass.get("Ident"))
            {
                result.add(new TerminalNode(TokenClass.get("Ident")));
            }
            else if (token.classId == TokenClass.get("Literal"))
            {
                result.add(new TerminalNode(TokenClass.get("Literal")));
            }
            else
            {
                throw new SyntaxError();
            }
        }
        else if (nodeClass == TokenClass.get("AssignmentExpression"))
        {
            // TODO: temp
            result.add(new TerminalNode(TokenClass.get("Ident")));
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
        return new NonTerminalNode(TokenClass.get("PrimaryExpression"));
    }
}
