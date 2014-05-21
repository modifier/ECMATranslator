package org.modifier.compiler;

import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.parser.TerminalNode;
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
}
