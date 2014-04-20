package org.modifier.scanner;

import org.modifier.scanner.TokenClass;

public class Token {
    public final String value;
    public final TokenClass classId;

    public Token (String value)
    {
        this.value = value;
        this.classId = TokenClass.Other;
    }

    public Token (String value, TokenClass classId) {
        this.value = value;
        this.classId = classId;
    }

    @Override
    public String toString() {
        return classId.toString() + " " + value;
    }
}
