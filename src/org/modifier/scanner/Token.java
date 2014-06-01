package org.modifier.scanner;

public class Token {
    public final String value;
    public final TokenClass classId;
    private int line;
    private int position;

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

    public void setPosition(int line, int position)
    {
        this.line = line;
        this.position = position;
    }

    public int getPosition()
    {
        return position;
    }

    public int getLine()
    {
        return line;
    }
}
