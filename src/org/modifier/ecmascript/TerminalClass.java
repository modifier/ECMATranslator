package org.modifier.ecmascript;

import org.modifier.parser.INodeClass;
import org.modifier.scanner.ITokenClass;

public enum TerminalClass implements ITokenClass, INodeClass
{
    Ident, Const, Literal, Regex, Other, BinaryOperator, UnaryOperator
}
