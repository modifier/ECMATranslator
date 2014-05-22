package org.modifier.compiler;

import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.parser.TerminalNode;
import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;
import org.modifier.utils.TreeGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class Translator
{
    protected NonTerminalNode root;
    public Translator (NonTerminalNode node)
    {
        root = node;
    }

    public NonTerminalNode convert ()
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

    public void check (NonTerminalNode node)
    {
        if (
            node.getNodeClass() == TokenClass.get("FunctionExpression_1")
            || node.getNodeClass() == TokenClass.get("FunctionDeclaration")
        )
        {
            convertFunction(node);
        }
        else if (node.getNodeClass() == TokenClass.get("ForStatement"))
        {
            convertForLoop(node);
        }
    }

    public NonTerminalNode createIfStatement (NonTerminalNode innerExpression, NonTerminalNode body)
    {
        TreeGenerator generator = new TreeGenerator("$Statement { IfStatement { 'if' '(' $Placeholder ')' Block { '{' $Placeholder '}' } } }");

        NonTerminalNode statement = generator.get(0);
        NonTerminalNode condition = generator.get(1);
        NonTerminalNode innerBody = generator.get(2);

        condition.update(innerExpression);
        innerBody.update(body);

        return statement;
    }

    public NonTerminalNode createVariable (Node name)
    {
        NonTerminalNode primary = new NonTerminalNode("PrimaryExpression");
        primary.appendChild(name);
        NonTerminalNode member = new NonTerminalNode("MemberExpression");
        member.appendChild(primary);
        NonTerminalNode leftHand = new NonTerminalNode("LeftHandSideExpression");
        leftHand.appendChild(member);
        NonTerminalNode assignment = new NonTerminalNode("AssignmentExpression");
        assignment.appendChild(leftHand);

        return assignment;
    }

    public void convertFunction (NonTerminalNode node)
    {
        // Find necessary kids first
        NonTerminalNode expression = (NonTerminalNode)node.findNodeClass("VariableDeclarationList");
        NonTerminalNode body = (NonTerminalNode)node.findNodeClass("SourceElements");

        // Now find assigned arguments
        ArrayList<NonTerminalNode> declarations = new ArrayList<>();
        NonTerminalNode currentList = expression;
        while (currentList != null)
        {
            declarations.add((NonTerminalNode)currentList.findNodeClass("VariableDeclaration"));
            currentList = (NonTerminalNode)currentList.findNodeClass("VariableDeclarationList_1");
        }

        // Disassemble declarations, find out which variables are assignable
        HashMap<TerminalNode, NonTerminalNode> assignees = new HashMap<>();
        for (NonTerminalNode declaration : declarations)
        {
            if (declaration == null)
            {
                continue;
            }

            TerminalNode ident = (TerminalNode)declaration.findNodeClass("Ident");
            NonTerminalNode tail = (NonTerminalNode)declaration.findNodeClass("VariableDeclaration_1");
            NonTerminalNode assignee = (NonTerminalNode)tail.findNodeClass("AssignmentExpression");

            if (assignee != null)
            {
                assignees.put(ident, assignee);

                // Meanwhile remove declarations with assignees
                declaration.clearChildren();
                declaration.appendChild(ident);
            }
        }

        NonTerminalNode lastSource = (NonTerminalNode)body.findNodeClass("SourceElement");
        // And put them into the body
        for (TerminalNode key : assignees.keySet())
        {
            // TODO: Refactor using TreeGenerator
            // Create condition
            NonTerminalNode leftConditionPart = new NonTerminalNode("BinaryExpression");
            leftConditionPart.appendChild(new TerminalNode("=="));
            leftConditionPart.appendChild((createVariable(new TerminalNode("null"))));

            NonTerminalNode condition = createVariable(key);
            condition.appendChild(leftConditionPart);

            // And then body
            NonTerminalNode leftAssignmentPart = new NonTerminalNode("BinaryExpression");
            leftAssignmentPart.appendChild(new TerminalNode("="));
            leftAssignmentPart.appendChild(assignees.get(key));
            NonTerminalNode assignment = createVariable(key);
            assignment.appendChild(leftAssignmentPart);
            assignment.appendChild(new TerminalNode(";"));
            NonTerminalNode innerExpression = new NonTerminalNode("Expression");
            innerExpression.appendChild(assignment);
            NonTerminalNode statement = new NonTerminalNode("Statement");
            statement.appendChild(innerExpression);
            NonTerminalNode slist = new NonTerminalNode("StatementList");
            slist.appendChild(statement);

            NonTerminalNode result = createIfStatement(condition, slist);
            NonTerminalNode sourceElement = new NonTerminalNode("SourceElement");
            sourceElement.appendChild(result);
            sourceElement.appendChild(lastSource);

            NonTerminalNode sourceElements = new NonTerminalNode("SourceElements");
            sourceElements.appendChild(sourceElement);

            lastSource = sourceElements;
        }

        body.clearChildren();
        body.appendChild(lastSource.getChildren().get(0));
    }

    public void convertForLoop(NonTerminalNode node)
    {
        NonTerminalNode expressionLeft = (NonTerminalNode)node.findNodeClass("ForStatement_1");
        NonTerminalNode expressionRight = (NonTerminalNode)node.findNodeClass("ForStatement_2");
        NonTerminalNode body = (NonTerminalNode)node.findNodeClass("Statement");

        TerminalNode operatorName = (TerminalNode)(expressionRight.getChildren().get(0));

        if (!operatorName.getToken().value.equals("of"))
        {
            return;
        }

        // TODO: unhardcode variable's name
        String keyName = "__key__";
        String collectionName = "__collection__";

        // Find out value's variable name and fetch collection expression
        TerminalNode nameToken = (TerminalNode)expressionLeft.findDeep("Ident");
        String name = nameToken.getToken().value;

        NonTerminalNode collectionExpression = (NonTerminalNode)expressionRight.getChildren().get(1);

        // Replace all this stuff with new ones
        nameToken.setToken(new Token(keyName, TokenClass.get("Ident")));

        expressionRight.clearChildren();
        expressionRight.appendChild(new TerminalNode(new Token("in")));
        expressionRight.appendChild(new TerminalNode(new Token(collectionName, TokenClass.get("Ident"))));

        // Append calculation to the loop body
        TreeGenerator innerBlock = new TreeGenerator("$Block { '{' StatementList { $Statement StatementList { $Statement } } '}' }");
        TreeGenerator outerAssigment = new TreeGenerator("$Statement { 'var' VariableDeclarationList { VariableDeclaration { (Ident," + name + ") VariableDeclaration_1 { '=' '(' $AssignmentExpression ')' } } } ';' }");
        TreeGenerator innerAssignment = new TreeGenerator("$AssignmentExpression { LeftHandSideExpression { MemberExpression { PrimaryExpression { (Ident,__collection__) } } MemberExpressionPart { '[' (Ident,__key__) ']' } } }");

        outerAssigment.get(1).update(innerAssignment.get(0));
        innerBlock.get(1).update(outerAssigment.get(0));
        innerBlock.get(2).update(body.clone());
        body.clearChildren();
        body.appendChild(innerBlock.get(0));

        // At last, we wrap initial loop into block
        TreeGenerator wrapper = new TreeGenerator("$Block { '{' StatementList { $Statement StatementList { $Statement } } '}' }");
        TreeGenerator preCalculation = new TreeGenerator("$Statement { 'var' VariableDeclarationList { VariableDeclaration { (Ident," +  collectionName + ") VariableDeclaration_1 { '=' '(' $AssignmentExpression ')' } } } ';' }");

        wrapper.get(2).update(node.clone());
        node.update(wrapper.get(0));
        wrapper.get(1).update(preCalculation.get(0));
        preCalculation.get(1).update(collectionExpression);
    }
}
