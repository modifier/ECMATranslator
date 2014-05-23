package org.modifier.compiler;

import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.parser.TerminalNode;
import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;
import org.modifier.utils.TreeGenerator;
import org.modifier.utils.Template;

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
            leftConditionPart.appendChild((Template.expression(new TerminalNode("null")).get(0)));

            NonTerminalNode condition = Template.expression(key).get(0);
            condition.appendChild(leftConditionPart);

            // And then body
            NonTerminalNode leftAssignmentPart = new NonTerminalNode("BinaryExpression");
            leftAssignmentPart.appendChild(new TerminalNode("="));
            leftAssignmentPart.appendChild(assignees.get(key));
            NonTerminalNode assignment = Template.expression(key).get(0);
            assignment.appendChild(leftAssignmentPart);
            assignment.appendChild(new TerminalNode(";"));
            NonTerminalNode innerExpression = new NonTerminalNode("Expression");
            innerExpression.appendChild(assignment);
            NonTerminalNode statement = new NonTerminalNode("Statement");
            statement.appendChild(innerExpression);
            NonTerminalNode slist = new NonTerminalNode("StatementList");
            slist.appendChild(statement);

            NonTerminalNode result = Template.ifStatement(condition, slist).get(0);
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
        TreeGenerator innerBlock = Template.block(2);
        TreeGenerator outerAssigment = Template.declaration(name);
        TreeGenerator innerAssignment = Template.accessor(new TerminalNode("__collection__", "Ident"), Template.expression(new TerminalNode("__key__", "Ident")).get(0));

        outerAssigment.get(1).update(innerAssignment.get(0));
        innerBlock.get(1).update(outerAssigment.get(0));
        innerBlock.get(2).update(body.clone());
        body.clearChildren();
        body.appendChild(innerBlock.get(0));

        // At last, we wrap initial loop into block
        TreeGenerator wrapper = Template.block(2);
        TreeGenerator preCalculation = Template.declaration(collectionName);
        TreeGenerator accessor = Template.accessor(new TerminalNode(collectionName, "Ident"), collectionExpression);

        preCalculation.get(1).update(collectionExpression);
        wrapper.get(2).update(node.clone());
        node.update(wrapper.get(0));
        wrapper.get(1).update(preCalculation.get(0));
    }
}
