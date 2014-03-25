package org.modifier.scanner;

import org.modifier.scanner.TokenClass;

public class Token {
    public String value;
    public TokenClass classId;

    public Token (String value, TokenClass classId) {
        this.value = value;
        this.classId = classId;
    }

    @Override
    public String toString() {
        return classId.toString() + " " + value;
    }
}
