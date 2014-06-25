package org.modifier.compiler;

import org.modifier.parser.Node;
import org.modifier.parser.NonTerminalNode;
import org.modifier.parser.TerminalNode;
import org.modifier.scanner.ScanError;
import org.modifier.scanner.Token;
import org.modifier.scanner.TokenClass;
import org.modifier.utils.PositionException;
import org.modifier.utils.TerminalReaderException;
import org.modifier.utils.TreeGenerator;
import org.modifier.utils.Template;

import java.util.ArrayList;
import java.util.HashMap;

public class Translator
{
    protected NonTerminalNode root;
    private Polyfiller polyfiller;

    public Translator (NonTerminalNode node)
    {
        root = node;
    }

    public NonTerminalNode convert () throws PositionException, TerminalReaderException
    {
        explore(root);
        return root;
    }

    public void explore (NonTerminalNode root) throws PositionException, TerminalReaderException
    {
        check(root);

        for (Node node : root.getChildren())
        {
            if (node instanceof NonTerminalNode)
            {
                explore((NonTerminalNode)node);
            }
            else if (node.getNodeClass() == TokenClass.get("Ident"))
            {
                if (root.getNodeClass() == TokenClass.get("MemberExpressionPart"))
                {
                    polyfiller.reservePrototypeMethod(((TerminalNode)node).getToken().value);
                }
                else
                {
                    polyfiller.reserveClassName(((TerminalNode) node).getToken().value);
                }
            }
        }
    }

    public void check (NonTerminalNode node) throws PositionException, TerminalReaderException
    {
        if (
            node.getNodeClass() == TokenClass.get("FunctionBody")
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
        else if (node.getNodeClass() == TokenClass.get("LeftHandSideExpression"))
        {
            checkSuperInstruction(node);
        }
        else if (node.getNodeClass() == TokenClass.get("AssignmentExpression"))
        {
            checkConstInstruction(node);
        }
        else if (node.getNodeClass() == TokenClass.get("PrimaryExpression"))
        {
            checkQuasiliteral(node);
        }
        else if (node.getNodeClass() == TokenClass.get("SourceElement")
            && node.getChildren().get(0).getNodeClass() == TokenClass.get("ClassDeclaration"))
        {
            checkClassDeclaration(node);
        }

        if (node.getNodeClass() == TokenClass.get("VariableStatement"))
        {
            checkConstDeclaration(node);
        }
    }

    private void checkConstDeclaration(NonTerminalNode node)
    {
        Node kid = node.getChildren().get(0);
        if (kid instanceof TerminalNode && ((TerminalNode) kid).getToken().value.equals("const"))
        {
            Token token = ((TerminalNode) kid).getToken();
            Token clone = new Token("var", "Declarator");
            clone.setPosition(token.getLine(), token.getPosition());
            ((TerminalNode) kid).setToken(clone);
        }
    }

    private void checkSuperInstruction(NonTerminalNode node) throws TerminalReaderException, PositionException
    {
        if (!(node.getChildren().get(0) instanceof TerminalNode))
        {
            return;
        }

        NonTerminalNode tail = (NonTerminalNode)node.findNodeClass("LeftHandSideExpression_1");
        NonTerminalNode arguments = (NonTerminalNode)node.findNodeClass("LeftHandSideExpression_2");

        StringBuilder builder = new StringBuilder();
        builder.append("this.parent.prototype");
        if (tail.getChildren().size() == 0)
        {
            builder.append(".constructor");
        }
        else
        {
            builder.append(tail.toString());
        }
        Node expr = arguments.findNodeClass("Expression");
        String args = expr == null ? "" : expr.toString();

        builder.append(".call(this");
        if (!args.equals(""))
        {
            builder.append(",").append(args);
        }
        builder.append(")");

        node.clearChildren();
        NonTerminalNode tree = (NonTerminalNode)ESParser.get().processImmediate(builder.toString(), node);
    }

    private void checkClassDeclaration(NonTerminalNode sourceElement) throws TerminalReaderException, PositionException
    {
        NonTerminalNode node = (NonTerminalNode) sourceElement.findNodeClass("ClassDeclaration");
        Node siblings = sourceElement.findNodeClass("SourceElements");

        String className = ((TerminalNode) node.findNodeClass("Ident")).getToken().value;
        NonTerminalNode extender = (NonTerminalNode)node.findNodeClass("ClassDeclaration_1");
        String parentName;
        if (extender.findNodeClass("Ident") != null)
        {
            parentName = ((TerminalNode)extender.findNodeClass("Ident")).getToken().value;
        }
        else
        {
            parentName = "Object";
        }

        HashMap<String, Node> methods = new HashMap<>();
        NonTerminalNode classBody = (NonTerminalNode)node.findNodeClass("ClassBody");
        while (classBody.getChildren().size() != 0)
        {
            NonTerminalNode classElement = (NonTerminalNode)classBody.findNodeClass("ClassElement");

            String methodName = ((TerminalNode)classElement.findDeep("Ident")).getToken().value;
            methods.put(methodName, classElement.findDeep("FunctionBody"));

            classBody = (NonTerminalNode)classElement.findNodeClass("ClassBody");
        }

        // Function declaration
        StringBuilder builder = new StringBuilder();
        builder.append("var ").append(className).append(" = function ");
        if (methods.containsKey("constructor"))
        {
            builder.append(methods.get("constructor").toString());
        }
        else
        {
            builder.append("(){}");
        }
        builder.append(";");

        // Body
        builder.append(className).append(".prototype = Object.create(").append(parentName).append(".prototype);");
        for (String method : methods.keySet())
        {
            if (method.equals("constructor"))
            {
                continue;
            }

            builder.append(className).append(".prototype.").append(method).append(" = function").append(methods.get(method).toString()).append(";");
        }

        // Constructor and super
        builder.append(className).append(".prototype.constructor = ").append(className).append(";");
        builder.append(className).append(".prototype.parent = ").append(parentName).append(";");

        sourceElement.clearChildren();
        NonTerminalNode tree = (NonTerminalNode)ESParser.get().processImmediate(builder.toString(), sourceElement);
        sourceElement.appendChild(siblings);
    }

    private void checkQuasiliteralCall(NonTerminalNode node) throws PositionException, TerminalReaderException
    {
        TerminalNode foo = (TerminalNode) node.getChildren().get(0);
        NonTerminalNode kid = (NonTerminalNode)node.getChildren().get(1);
        if (kid.getChildren().size() == 0 || !(kid.getChildren().get(0) instanceof TerminalNode))
        {
            return;
        }

        Token quasi = ((TerminalNode) kid.getChildren().get(0)).getToken();

        String value = quasi.value;
        StringBuilder accumulator = new StringBuilder();
        ArrayList<String> pieces = new ArrayList<>();
        ArrayList<String> arguments = new ArrayList<>();
        boolean escaping = false;
        boolean isLiteral = true;
        for (int i = 1; i < value.length() - 1; i++)
        {
            char symbol = value.charAt(i);
            if ('\\' == symbol)
            {
                escaping = !escaping;
            }
            else
            {
                escaping = false;
            }

            if (isLiteral)
            {
                if ('$' == symbol && !escaping && '{' == value.charAt(i+1))
                {
                    ++i;
                    isLiteral = false;
                    if (accumulator.length() != 0)
                    {
                        pieces.add('"' + accumulator.toString() + '"');
                    }
                    accumulator = new StringBuilder();
                }
                else if ('"' == symbol)
                {
                    accumulator.append("\\\"");
                }
                else
                {
                    accumulator.append(symbol);
                }
            }
            else
            {
                if ('}' == symbol && !escaping)
                {
                    isLiteral = true;
                    arguments.add("(" + accumulator.toString() + ")");
                    accumulator = new StringBuilder();
                }
                else
                {
                    accumulator.append(symbol);
                }
            }
        }
        if (accumulator.length() != 0)
        {
            pieces.add('"' + accumulator.toString() + '"');
        }

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("(").append(foo.getToken().value).append("([").append(join(pieces, ", ")).append("]");
        if (arguments.size() != 0)
        {
            resultBuilder.append(", ").append(join(arguments, ", "));
        }
        resultBuilder.append("))");

        NonTerminalNode tree = (NonTerminalNode)ESParser.get().processImmediate(resultBuilder.toString(), node);
    }

    private String join (ArrayList<String> pieces, String glue)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pieces.size(); i++)
        {
            if (i != 0)
            {
                result.append(glue);
            }
            result.append(pieces.get(i));
        }
        return result.toString();
    }

    private void checkQuasiliteral(NonTerminalNode node) throws PositionException, TerminalReaderException
    {
        if (node.getChildren().size() == 0)
        {
            return;
        }

        Node kid = node.getChildren().get(0);
        if (!(kid instanceof TerminalNode))
        {
            return;
        }

        Token quasi = ((TerminalNode) kid).getToken();

        if (quasi.classId == TokenClass.get("Ident"))
        {
            checkQuasiliteralCall(node);
            return;
        }

        if (quasi.classId != TokenClass.get("Quasiliteral"))
        {
            return;
        }

        String value = ((TerminalNode) kid).getToken().value;
        StringBuilder accumulator = new StringBuilder();
        ArrayList<String> pieces = new ArrayList<>();
        boolean escaping = false;
        boolean isLiteral = true;
        for (int i = 1; i < value.length() - 1; i++)
        {
            char symbol = value.charAt(i);
            if ('\\' == symbol)
            {
                escaping = !escaping;
            }
            else
            {
                escaping = false;
            }

            if (isLiteral)
            {
                if ('$' == symbol && !escaping && '{' == value.charAt(i+1))
                {
                    ++i;
                    isLiteral = false;
                    if (accumulator.length() != 0)
                    {
                        pieces.add('"' + accumulator.toString() + '"');
                    }
                    accumulator = new StringBuilder();
                }
                else if ('"' == symbol)
                {
                    accumulator.append("\\\"");
                }
                else
                {
                    accumulator.append(symbol);
                }
            }
            else
            {
                if ('}' == symbol && !escaping)
                {
                    isLiteral = true;
                    pieces.add("(" + accumulator.toString() + ")");
                    accumulator = new StringBuilder();
                }
                else
                {
                    accumulator.append(symbol);
                }
            }
        }
        if (accumulator.length() != 0)
        {
            pieces.add('"' + accumulator.toString() + '"');
        }

        String result;
        if (pieces.size() == 0)
        {
             result = "\"\"";
        }
        else
        {
            result = "(" + join(pieces, " + ") + ")";
        }

        node.clearChildren();
        NonTerminalNode tree = (NonTerminalNode)ESParser.get().processImmediate(result, node);
    }

    private void checkConstInstruction (NonTerminalNode node) throws TypeError
    {
        NonTerminalNode rightSide = (NonTerminalNode)node.findNodeClass("BinaryExpression");
        if (rightSide == null) {
            return;
        }

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

        NonTerminalNode letBlock = node.closestVarBlock();

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

    private void convertFunction (NonTerminalNode node) throws TerminalReaderException, PositionException
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
            b.append("if(typeof " + key.getToken().value + " == \"undefined\"){" +
                key.getToken().value + " = " + assignees.get(key).toString() + ";}");
        }
        b.append(body.toString());

        body.clearChildren();
        NonTerminalNode parsed = (NonTerminalNode)ESParser.get().processImmediate(b.toString(), body);
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

    public void setPolyfiller(Polyfiller polyfiller) {
        this.polyfiller = polyfiller;
    }
}
