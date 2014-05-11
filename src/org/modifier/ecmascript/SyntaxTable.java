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
            else if (token.classId == TokenClass.get("Const"))
            {
                result.add(new TerminalNode(TokenClass.get("Const")));
            }
            else if (token.classId == TokenClass.get("Ident"))
            {
                result.add(new TerminalNode(TokenClass.get("Ident")));
            }
            else if (token.value.equals("["))
            {
                result.add(new NonTerminalNode(TokenClass.get("ArrayLiteral")));
            }
            else if (token.value.equals("{"))
            {
                result.add(new NonTerminalNode(TokenClass.get("ObjectLiteral")));
            }
            else if (token.value.equals("("))
            {
                result.add(new TerminalNode("("));
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
                result.add(new TerminalNode(")"));
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
            result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
            result.add(new NonTerminalNode(TokenClass.get("ElementList_1")));
        }
        else if (nodeClass == TokenClass.get("ElementList_1"))
        {
            if (token.value.equals(","))
            {
                result.add(new TerminalNode(","));
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
            if (token.value.equals("}"))
            {
                return result;
            }

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
        else if (nodeClass == TokenClass.get("Expression"))
        {
            result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
            result.add(new NonTerminalNode(TokenClass.get("Expression_1")));
        }
        else if (nodeClass == TokenClass.get("Expression_1"))
        {
            if (token.value.equals(","))
            {
                result.add(new TerminalNode(","));
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
            }
        }
        else if (nodeClass == TokenClass.get("AssignmentExpression"))
        {
            if (token.classId == TokenClass.get("AdditiveOperator") || token.classId == TokenClass.get("UnaryOperator"))
            {
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
            }
            else
            {
                result.add(new NonTerminalNode(TokenClass.get("LeftHandSideExpression")));
            }
            result.add(new NonTerminalNode(TokenClass.get("BinaryExpression")));
        }
        else if (nodeClass == TokenClass.get("BinaryExpression"))
        {
            if (token.classId == TokenClass.get("AdditiveOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("AdditiveOperator")));
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpression")));
            }
            else if (token.classId == TokenClass.get("BinaryOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("BinaryOperator")));
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpression")));
            }
            else if (token.classId == TokenClass.get("AssignmentOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("AssignmentOperator")));
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpression")));
            }
        }
        else if (nodeClass == TokenClass.get("UnaryExpression"))
        {
            if (token.classId == TokenClass.get("AdditiveOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("AdditiveOperator")));
            }
            else if (token.classId == TokenClass.get("UnaryOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("UnaryOperator")));
            }
            result.add(new NonTerminalNode(TokenClass.get("LeftHandSideExpression")));
            result.add(new NonTerminalNode(TokenClass.get("PostfixExpression")));
        }
        else if (nodeClass == TokenClass.get("PostfixExpression"))
        {
            if (token.classId == TokenClass.get("PostfixOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("PostfixOperator")));
            }
        }
        else if (nodeClass == TokenClass.get("LeftHandSideExpression"))
        {
            result.add(new NonTerminalNode(TokenClass.get("MemberExpression")));
            result.add(new NonTerminalNode(TokenClass.get("LeftHandSideExpression_1")));
        }
        else if (nodeClass == TokenClass.get("LeftHandSideExpression_1"))
        {

        }
        else if (nodeClass == TokenClass.get("MemberExpression"))
        {
            if (token.value.equals("new"))
            {
                result.add(new NonTerminalNode(TokenClass.get("AllocationExpression")));
            }
            else if (token.value.equals("function"))
            {
                result.add(new NonTerminalNode(TokenClass.get("FunctionExpression")));
            }
            else
            {
                result.add(new NonTerminalNode(TokenClass.get("PrimaryExpression")));
            }
            result.add(new NonTerminalNode(TokenClass.get("MemberExpressionPart")));
        }
        else if (nodeClass == TokenClass.get("MemberExpressionPart"))
        {
            if (token.value.equals("("))
            {
                result.add(new TerminalNode("("));
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
                result.add(new TerminalNode(")"));
                result.add(new NonTerminalNode(TokenClass.get("MemberExpressionPart")));
            }
            else if (token.value.equals("["))
            {
                result.add(new TerminalNode("["));
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
                result.add(new TerminalNode("]"));
                result.add(new NonTerminalNode(TokenClass.get("MemberExpressionPart")));
            }
            else if (token.value.equals("."))
            {
                result.add(new TerminalNode("."));
                result.add(new TerminalNode(TokenClass.get("Ident")));
                result.add(new NonTerminalNode(TokenClass.get("MemberExpressionPart")));
            }
        }
        else if (nodeClass == TokenClass.get("AllocationExpression"))
        {
            result.add(new TerminalNode("new"));
            result.add(new NonTerminalNode(TokenClass.get("MemberExpression")));
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
