package org.modifier.scanner;

public class Token {
    public final String value;
    public final TokenClass classId;

    public Token (String value)
    {
        this.value = value;
        this.classId = TokenClass.get("Other");
    }

    public Token (String value, TokenClass classId) {
        this.value = value;
        this.classId = classId;
    }

    public Token (String value, String classId) {
        this.value = value;
        this.classId = TokenClass.get(classId);
    }

    @Override
    public String toString() {
        return classId.toString() + " " + value;
    }
}
