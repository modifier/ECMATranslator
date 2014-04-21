package org.modifier.scanner;

public class Token {
    public final String value;
    public final ITokenClass classId;

    public Token (String value)
    {
        this.value = value;
        this.classId = TokenClass.Other;
    }

    public Token (String value, ITokenClass classId) {
        this.value = value;
        this.classId = classId;
    }

    @Override
    public String toString() {
        return classId.toString() + " " + value;
    }
}
