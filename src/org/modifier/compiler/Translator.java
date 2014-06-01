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

    public NonTerminalNode convert () throws TypeError
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

    public void check (NonTerminalNode node) throws TypeError
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
            convertForOf(node);
        }
        else if (
            node.getNodeClass() == TokenClass.get("VariableStatement")
            || node.getNodeClass() == TokenClass.get("ForStatement_1")
        )
        {
            convertLetInstruction(node);
        }
        else if (node.getNodeClass() == TokenClass.get("AssignmentExpression"))
        {
            checkConstInstruction(node);
        }
    }

    private void checkConstInstruction (NonTerminalNode node) throws TypeError
    {
        NonTerminalNode rightSide = (NonTerminalNode)node.findNodeClass("BinaryExpression");
        boolean hasAssignment = rightSide.findNodeClass("AssignmentOperator") != null;

        if (!hasAssignment)
        {
            return;
        }

        Scope closestScope = node.closestVarBlock().getScope();
        NonTerminalNode leftSide = (NonTerminalNode)node.findDeep("MemberExpression");
        Node firstKid = leftSide.getChildren().get(0);
        if (firstKid.getNodeClass() != TokenClass.get("PrimaryExpression"))
        {
            return;
        }

        Node subKid = ((NonTerminalNode)firstKid).getChildren().get(0);
        if (subKid.getNodeClass() != TokenClass.get("Ident"))
        {
            return;
        }

        Token ident = ((TerminalNode)subKid).getToken();
        if (closestScope.hasConstIdent(ident.value))
        {
            throw TypeError.constantCannotBeRedefined(ident);
        }
    }

    private void convertLetInstruction (NonTerminalNode node)
    {
        Node first = node.getChildren().get(0);
        if (
            first.getNodeClass() != TokenClass.get("Declarator")
            || !((TerminalNode)first).getToken().value.equals("let")
        )
        {
            return;
        }

        ((TerminalNode)first).setToken(new Token("var"));

        NonTerminalNode letBlock = node.closestLetBlock();

        if (letBlock.isVarBlock())
        {
            return;
        }

        HashMap<String, String> renamed = new HashMap<>();

        NonTerminalNode list = (NonTerminalNode)node.findNodeClass("VariableDeclarationList");
        while (list.getChildren().size() != 0)
        {
            NonTerminalNode declaration = (NonTerminalNode)list.findNodeClass("VariableDeclaration");
            TerminalNode ident = (TerminalNode)declaration.findNodeClass("Ident");

            String identValue = ident.getToken().value;
            String newValue;
            int i = 0;
            do
            {
                newValue = identValue + "_" + (++i);
            } while (letBlock.getScope().hasIdent(newValue));
            renamed.put(identValue, newValue);

            ident.setToken(new Token(newValue, "Ident"));
            letBlock.getScope().replace(identValue, newValue);

            list = (NonTerminalNode)list.findNodeClass("VariableDeclarationList_1");
        }

        exploreLet(letBlock, renamed);
    }

    private void exploreLet (NonTerminalNode root, HashMap<String, String> renamed)
    {
        for (Node kid : root.getChildren())
        {
            if (kid.getNodeClass() == TokenClass.get("Ident"))
            {
                TerminalNode tekid = (TerminalNode)kid;
                for (String initial : renamed.keySet())
                {
                    if (tekid.getToken().value.equals(initial))
                    {
                        tekid.setToken(new Token(renamed.get(initial), "Ident"));
                        break;
                    }
                }
                continue;
            }

            if (kid instanceof TerminalNode)
            {
                continue;
            }

            NonTerminalNode nokid = (NonTerminalNode)kid;

            HashMap<String, String> rebuilt = renamed;
            if (nokid.getScope() != null)
            {
                rebuilt = new HashMap<>();
                for (String initial : renamed.keySet())
                {
                    if (!nokid.getScope().hasOwnIdent(initial))
                    {
                        rebuilt.put(initial, renamed.get(initial));
                    }
                }
            }

            if (rebuilt.size() == renamed.size())
            {
                rebuilt = renamed;
            }

            if (rebuilt.size() == 0)
            {
                continue;
            }

            exploreLet(nokid, rebuilt);
        }
    }

    private void convertFunction (NonTerminalNode node)
    {
        // Find necessary kids first
        NonTerminalNode functionDeclaration = (NonTerminalNode)node.findNodeClass("FunctionDeclaration_1");
        NonTerminalNode body = (NonTerminalNode)node.findNodeClass("SourceElements");

        NonTerminalNode expression = (NonTerminalNode)functionDeclaration.findNodeClass("VariableDeclarationList");

        if (expression == null)
        {
            return;
        }

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

        StringBuilder b = new StringBuilder();
        for (TerminalNode key : assignees.keySet())
        {
            b.append("if(" + key.getToken().value + " == null){" +
                key.getToken().value + " = " + assignees.get(key).toString() + ";}");
        }
        b.append(body.toString());

        NonTerminalNode parsed = (NonTerminalNode)ESParser.get().processImmediate(b.toString(), TokenClass.get("SourceElement"));

        body.clearChildren();
        body.update(parsed);
    }

    private void convertForOf(NonTerminalNode node)
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
