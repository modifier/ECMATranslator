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
            else if (token.classId == TokenClass.get("Quasiliteral"))
            {
                result.add(new TerminalNode(TokenClass.get("Quasiliteral")));
            }
            else if (token.classId == TokenClass.get("Regex"))
            {
                result.add(new TerminalNode(TokenClass.get("Regex")));
            }
            else if (token.classId == TokenClass.get("Const"))
            {
                result.add(new TerminalNode(TokenClass.get("Const")));
            }
            else if (token.classId == TokenClass.get("Ident"))
            {
                result.add(new TerminalNode(TokenClass.get("Ident")));
                result.add(new NonTerminalNode(TokenClass.get("PrimaryExpression_1")));
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
                result.add(new NonTerminalNode(TokenClass.get("PrimaryExpression_2")));
            }
            else
            {
                throw SyntaxError.unknownRule(nodeClass, token);
            }
        }
        else if (nodeClass == TokenClass.get("PrimaryExpression_1"))
        {
            if (token.classId == TokenClass.get("Quasiliteral"))
            {
                result.add(new TerminalNode(TokenClass.get("Quasiliteral")));
            }
        }
        else if (nodeClass == TokenClass.get("PrimaryExpression_2"))
        {
            if (!token.value.equals(")"))
            {
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
            }
            result.add(new TerminalNode(")"));
        }
        else if (nodeClass == TokenClass.get("ArrayLiteral"))
        {
            result.add(new TerminalNode("["));
            result.add(new NonTerminalNode(TokenClass.get("ArrayLiteral_1")));
        }
        else if (nodeClass == TokenClass.get("ArrayLiteral_1"))
        {
            if (!token.value.equals("]")) {
                result.add(new NonTerminalNode(TokenClass.get("ElementList")));
            }
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
            else
            {
                result.add(new TerminalNode(TokenClass.get("Literal")));
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
            if (token.classId == TokenClass.get("AdditiveOperator") || token.classId == TokenClass.get("UnaryOperator") || token.classId == TokenClass.get("PostfixOperator"))
            {
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
            }
            else
            {
                result.add(new NonTerminalNode(TokenClass.get("LeftHandSideExpression")));
            }
            result.add(new NonTerminalNode(TokenClass.get("BinaryExpression")));
        }
        else if (nodeClass == TokenClass.get("AssignmentExpressionNoIn"))
        {
            if (token.classId == TokenClass.get("AdditiveOperator") || token.classId == TokenClass.get("UnaryOperator") || token.classId == TokenClass.get("PostfixOperator"))
            {
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
            }
            else
            {
                result.add(new NonTerminalNode(TokenClass.get("LeftHandSideExpression")));
            }
            result.add(new NonTerminalNode(TokenClass.get("BinaryExpressionNoIn")));
        }
        else if (nodeClass == TokenClass.get("BinaryExpressionNoIn"))
        {
            if (token.classId == TokenClass.get("AdditiveOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("AdditiveOperator")));
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpressionNoIn")));
            }
            else if (token.classId == TokenClass.get("BinaryOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("BinaryOperator")));
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpressionNoIn")));
            }
            else if (token.classId == TokenClass.get("UnaryOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("UnaryOperator")));
            }
            else if (token.value.equals("="))
            {
                result.add(new TerminalNode("=", "AssignmentOperator"));
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpressionNoIn")));
            }
            else if (token.classId == TokenClass.get("AssignmentOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("AssignmentOperator")));
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpressionNoIn")));
            }
            else if (token.value.equals("?"))
            {
                result.add(new TerminalNode("?"));
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpressionNoIn")));
                result.add(new TerminalNode(":"));
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpressionNoIn")));
            }
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
            else if (token.value.equals("in"))
            {
                result.add(new TerminalNode("in"));
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpression")));
            }
            else if (token.classId == TokenClass.get("UnaryOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("UnaryOperator")));
            }
            else if (token.value.equals("="))
            {
                result.add(new TerminalNode("=", "AssignmentOperator"));
                result.add(new NonTerminalNode(TokenClass.get("UnaryExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpression")));
            }
            else if (token.classId == TokenClass.get("AssignmentOperator"))
            {
                result.add(new TerminalNode(TokenClass.get("AssignmentOperator")));
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
                result.add(new NonTerminalNode(TokenClass.get("BinaryExpression")));
            }
            else if (token.value.equals("?"))
            {
                result.add(new TerminalNode("?"));
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
                result.add(new TerminalNode(":"));
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
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
        }
        else if (nodeClass == TokenClass.get("LeftHandSideExpression"))
        {
            if (token.value.equals("super"))
            {
                result.add(new TerminalNode("super"));
                result.add(new NonTerminalNode(TokenClass.get("LeftHandSideExpression_1")));
                result.add(new NonTerminalNode(TokenClass.get("LeftHandSideExpression_2")));
            }
            else
            {
                result.add(new NonTerminalNode(TokenClass.get("MemberExpression")));
            }
        }
        else if (nodeClass == TokenClass.get("LeftHandSideExpression_1"))
        {
            if (token.value.equals("."))
            {
                result.add(new TerminalNode("."));
                result.add(new TerminalNode(TokenClass.get("Ident")));
            }
        }
        else if (nodeClass == TokenClass.get("LeftHandSideExpression_2"))
        {
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(TokenClass.get("LeftHandSideExpression_3")));
        }
        else if (nodeClass == TokenClass.get("LeftHandSideExpression_3"))
        {
            if (!token.value.equals(")"))
            {
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
            }
            result.add(new TerminalNode(")"));
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
                result.add(new NonTerminalNode(TokenClass.get("MemberExpressionPart_1")));
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
        else if (nodeClass == TokenClass.get("MemberExpressionPart_1"))
        {
            if (!token.value.equals(")"))
            {
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
            }
            result.add(new TerminalNode(")"));
        }
        else if (nodeClass == TokenClass.get("AllocationExpression"))
        {
            result.add(new TerminalNode("new"));
            result.add(new NonTerminalNode(TokenClass.get("MemberExpression")));
        }
        else if (nodeClass == TokenClass.get("FunctionExpression"))
        {
            result.add(new TerminalNode("function"));
            result.add(new NonTerminalNode(TokenClass.get("FunctionExpression_1")));
        }
        else if (nodeClass == TokenClass.get("FunctionExpression_1"))
        {
            if (token.classId == TokenClass.get("Ident"))
            {
                result.add(new TerminalNode(TokenClass.get("Ident")));
            }
            result.add(new NonTerminalNode(TokenClass.get("FunctionBody")));
        }
        else if (nodeClass == TokenClass.get("FunctionDeclaration"))
        {
            result.add(new TerminalNode("function"));
            result.add(new TerminalNode(TokenClass.get("Ident")));
            result.add(new NonTerminalNode(TokenClass.get("FunctionBody")));
        }
        else if (nodeClass == TokenClass.get("FunctionBody"))
        {
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(TokenClass.get("FunctionDeclaration_1")));
            result.add(new TerminalNode(")"));
            result.add(new TerminalNode("{"));
            result.add(new NonTerminalNode(TokenClass.get("SourceElements")));
            result.add(new TerminalNode("}"));
        }
        else if (nodeClass == TokenClass.get("ClassDeclaration"))
        {
            result.add(new TerminalNode("class"));
            result.add(new TerminalNode(TokenClass.get("Ident")));
            result.add(new NonTerminalNode(TokenClass.get("ClassDeclaration_1")));
            result.add(new TerminalNode("{"));
            result.add(new NonTerminalNode(TokenClass.get("ClassBody")));
            result.add(new TerminalNode("}"));
        }
        else if (nodeClass == TokenClass.get("ClassDeclaration_1"))
        {
            if (token.value.equals("extends"))
            {
                result.add(new TerminalNode("extends"));
                result.add(new TerminalNode(TokenClass.get("Ident")));
            }
        }
        else if (nodeClass == TokenClass.get("ClassBody"))
        {
            if (token.classId == TokenClass.get("<EOF>") || token.value.equals("}"))
            {
                return result;
            }
            result.add(new NonTerminalNode(TokenClass.get("ClassElement")));
        }
        else if (nodeClass == TokenClass.get("ClassElement"))
        {
            result.add(new NonTerminalNode(TokenClass.get("ClassMethod")));
            result.add(new NonTerminalNode(TokenClass.get("ClassBody")));
        }
        else if (nodeClass == TokenClass.get("ClassMethod"))
        {
            result.add(new TerminalNode(TokenClass.get("Ident")));
            result.add(new NonTerminalNode(TokenClass.get("FunctionBody")));
        }
        else if (nodeClass == TokenClass.get("FunctionDeclaration_1"))
        {
            if (!token.value.equals(")"))
            {
                result.add(new NonTerminalNode(TokenClass.get("VariableDeclarationList")));
            }
        }
        else if (nodeClass == TokenClass.get("SourceElements"))
        {
            if (token.classId == TokenClass.get("<EOF>") || token.value.equals("}"))
            {
                return result;
            }
            result.add(new NonTerminalNode(TokenClass.get("SourceElement")));
        }
        else if (nodeClass == TokenClass.get("SourceElement"))
        {
            if (token.value.equals("function"))
            {
                result.add(new NonTerminalNode(TokenClass.get("FunctionDeclaration")));
            }
            else if (token.value.equals("class"))
            {
                result.add(new NonTerminalNode(TokenClass.get("ClassDeclaration")));
            }
            else
            {
                result.add(new NonTerminalNode(TokenClass.get("Statement")));
            }
            result.add(new NonTerminalNode(TokenClass.get("SourceElements")));
        }
        else if (nodeClass == TokenClass.get("Program"))
        {
            result.add(new NonTerminalNode(TokenClass.get("SourceElements")));
            result.add(new TerminalNode(TokenClass.get("<EOF>")));
        }
        else if (nodeClass == TokenClass.get("StatementList"))
        {
            if (!token.value.equals("}") && !token.value.equals("case") && !token.value.equals("default"))
            {
                result.add(new NonTerminalNode(TokenClass.get("Statement")));
                result.add(new NonTerminalNode(TokenClass.get("StatementList")));
            }
        }
        else if (nodeClass == TokenClass.get("Block"))
        {
            result.add(new TerminalNode("{"));
            result.add(new NonTerminalNode(TokenClass.get("StatementList")));
            result.add(new TerminalNode("}"));
        }
        else if (nodeClass == TokenClass.get("Statement"))
        {
            if (token.value.equals("{"))
            {
                result.add(new NonTerminalNode(TokenClass.get("Block")));
            }
            else if (token.value.equals(";"))
            {
                result.add(new TerminalNode(";"));
            }
            else if (token.classId == TokenClass.get("Declarator"))
            {
                result.add(new NonTerminalNode(TokenClass.get("VariableStatement")));
                result.add(new TerminalNode(";"));
            }
            else if (token.value.equals("continue"))
            {
                result.add(new TerminalNode("continue"));
                result.add(new TerminalNode(";"));
            }
            else if (token.value.equals("break"))
            {
                result.add(new TerminalNode("break"));
                result.add(new TerminalNode(";"));
            }
            else if (token.value.equals("return"))
            {
                result.add(new TerminalNode("return"));
                result.add(new NonTerminalNode(TokenClass.get("ReturnStatement")));
                result.add(new TerminalNode(";"));
            }
            else if (token.value.equals("for"))
            {
                result.add(new NonTerminalNode(TokenClass.get("ForStatement")));
            }
            else if (token.value.equals("do"))
            {
                result.add(new NonTerminalNode(TokenClass.get("DoStatement")));
                result.add(new TerminalNode(";"));
            }
            else if (token.value.equals("while"))
            {
                result.add(new NonTerminalNode(TokenClass.get("WhileStatement")));
            }
            else if (token.value.equals("if"))
            {
                result.add(new NonTerminalNode(TokenClass.get("IfStatement")));
            }
            else if (token.value.equals("switch"))
            {
                result.add(new NonTerminalNode(TokenClass.get("SwitchStatement")));
            }
            else if (token.value.equals("throw"))
            {
                result.add(new NonTerminalNode(TokenClass.get("ThrowStatement")));
                result.add(new TerminalNode(";"));
            }
            else if (token.value.equals("try"))
            {
                result.add(new NonTerminalNode(TokenClass.get("TryStatement")));
            }
            else
            {
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
                result.add(new TerminalNode(";"));
            }
        }
        else if (nodeClass == TokenClass.get("ReturnStatement"))
        {
            if (!token.value.equals(";"))
            {
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
            }
        }
        else if (nodeClass == TokenClass.get("ThrowStatement"))
        {
            result.add(new TerminalNode("throw"));
            result.add(new NonTerminalNode(TokenClass.get("Expression")));
        }
        else if (nodeClass == TokenClass.get("WhileStatement"))
        {
            result.add(new TerminalNode("while"));
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(TokenClass.get("Expression")));
            result.add(new TerminalNode(")"));
            result.add(new NonTerminalNode(TokenClass.get("Statement")));
        }
        else if (nodeClass == TokenClass.get("DoStatement"))
        {
            result.add(new TerminalNode("do"));
            result.add(new NonTerminalNode(TokenClass.get("Statement")));
            result.add(new TerminalNode("while"));
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(TokenClass.get("Expression")));
            result.add(new TerminalNode(")"));
        }
        else if (nodeClass == TokenClass.get("ForStatement"))
        {
            result.add(new TerminalNode("for"));
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(TokenClass.get("ForStatement_1")));
            result.add(new NonTerminalNode(TokenClass.get("ForStatement_2")));
            result.add(new TerminalNode(")"));
            result.add(new NonTerminalNode(TokenClass.get("Statement")));
        }
        else if (nodeClass == TokenClass.get("ForStatement_1"))
        {
            if (token.classId == TokenClass.get("Declarator"))
            {
                result.add(new TerminalNode(TokenClass.get("Declarator")));
                result.add(new NonTerminalNode(TokenClass.get("VariableDeclarationList")));
            }
            else
            {
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpressionNoIn")));
            }
        }
        else if (nodeClass == TokenClass.get("ForStatement_2"))
        {
            if (token.value.equals("of"))
            {
                result.add(new TerminalNode("of"));
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
            }
            else if (token.value.equals("in"))
            {
                result.add(new TerminalNode("in"));
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
            }
            else
            {
                result.add(new TerminalNode(";"));
                result.add(new NonTerminalNode(TokenClass.get("OptionalExpression")));
                result.add(new TerminalNode(";"));
                result.add(new NonTerminalNode(TokenClass.get("OptionalExpression")));
            }
        }
        else if (nodeClass == TokenClass.get("OptionalExpression"))
        {
            if (!token.value.equals(";"))
            {
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
            }
        }
        else if (nodeClass == TokenClass.get("VariableDeclarationList"))
        {
            result.add(new NonTerminalNode(TokenClass.get("VariableDeclaration")));
            result.add(new NonTerminalNode(TokenClass.get("VariableDeclarationList_1")));
        }
        else if (nodeClass == TokenClass.get("VariableDeclarationList_1"))
        {
            if (token.value.equals(","))
            {
                result.add(new TerminalNode(","));
                result.add(new NonTerminalNode(TokenClass.get("VariableDeclaration")));
                result.add(new NonTerminalNode(TokenClass.get("VariableDeclarationList_1")));
            }
        }
        else if (nodeClass == TokenClass.get("VariableDeclaration"))
        {
            result.add(new TerminalNode(TokenClass.get("Ident")));
            result.add(new NonTerminalNode(TokenClass.get("VariableDeclaration_1")));
        }
        else if (nodeClass == TokenClass.get("VariableDeclaration_1"))
        {
            if (token.value.equals("="))
            {
                result.add(new TerminalNode("="));
                result.add(new NonTerminalNode(TokenClass.get("AssignmentExpression")));
            }
        }
        else if (nodeClass == TokenClass.get("VariableStatement"))
        {
            result.add(new TerminalNode(TokenClass.get("Declarator")));
            result.add(new NonTerminalNode(TokenClass.get("VariableDeclarationList")));
        }
        else if (nodeClass == TokenClass.get("IfStatement"))
        {
            result.add(new TerminalNode("if"));
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(TokenClass.get("Expression")));
            result.add(new TerminalNode(")"));
            result.add(new NonTerminalNode(TokenClass.get("Statement")));
            result.add(new NonTerminalNode(TokenClass.get("IfStatement_1")));
        }
        else if (nodeClass == TokenClass.get("IfStatement_1"))
        {
            if (token.value.equals("else"))
            {
                result.add(new TerminalNode("else"));
                result.add(new NonTerminalNode(TokenClass.get("Statement")));
            }
        }
        else if (nodeClass == TokenClass.get("SwitchStatement"))
        {
            result.add(new TerminalNode("switch"));
            result.add(new TerminalNode("("));
            result.add(new NonTerminalNode(TokenClass.get("Expression")));
            result.add(new TerminalNode(")"));
            result.add(new TerminalNode("{"));
            result.add(new NonTerminalNode(TokenClass.get("CaseClause")));
            result.add(new TerminalNode("}"));
        }
        else if (nodeClass == TokenClass.get("CaseClause"))
        {
            if (token.value.equals("case"))
            {
                result.add(new TerminalNode("case"));
                result.add(new NonTerminalNode(TokenClass.get("Expression")));
                result.add(new TerminalNode(":"));
            }
            else if (token.value.equals("default"))
            {
                result.add(new TerminalNode("default"));
                result.add(new TerminalNode(":"));
            }
            else
            {
                return result;
            }
            result.add(new NonTerminalNode(TokenClass.get("OptionalStatementList")));
        }
        else if (nodeClass == TokenClass.get("OptionalStatementList"))
        {
            if (token.value.equals("case") || token.value.equals("default"))
            {
                return result;
            }
            result.add(new NonTerminalNode(TokenClass.get("StatementList")));
            result.add(new NonTerminalNode(TokenClass.get("CaseClause")));
        }
        else if (nodeClass == TokenClass.get("TryStatement"))
        {
            result.add(new TerminalNode("try"));
            result.add(new NonTerminalNode(TokenClass.get("TryStatement_1")));
        }
        else if (nodeClass == TokenClass.get("TryStatement_1"))
        {
            if (token.value.equals("catch"))
            {
                result.add(new TerminalNode("catch"));
                result.add(new TerminalNode("("));
                result.add(new TerminalNode("Ident"));
                result.add(new TerminalNode(")"));
                result.add(new NonTerminalNode(TokenClass.get("Block")));
                result.add(new NonTerminalNode(TokenClass.get("TryStatement_2")));
            }
            else
            {
                result.add(new TerminalNode("finally"));
                result.add(new NonTerminalNode(TokenClass.get("Block")));
            }
        }
        else if (nodeClass == TokenClass.get("TryStatement_2"))
        {
            if (token.value.equals("finally"))
            {
                result.add(new TerminalNode("finally"));
                result.add(new NonTerminalNode(TokenClass.get("Block")));
            }
        }
        else
        {
            throw SyntaxError.unknownRule(nodeClass, token.getLine(), token.getPosition());
        }
        return result;
    }

    @Override
    public Node getRoot()
    {
        return new NonTerminalNode(TokenClass.get("Program"));
    }
}
