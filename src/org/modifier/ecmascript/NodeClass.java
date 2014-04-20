package org.modifier.ecmascript;

import org.modifier.parser.INodeClass;

public enum NodeClass implements INodeClass
{
    Function, Block, Statements, Statement, Loop, Condition, Else, Expression, Declaration, BinaryExpression,
    UnaryExpression, PrimaryExpression, BinaryOperator, UnaryOperator, Identifier, Const, RegEx, Literal
}
