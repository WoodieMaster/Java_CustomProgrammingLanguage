package language;

import java.util.List;

public class Token {
    enum Type {
        VALUE,
        KEY,
        ACTION_GROUP,
        OPERATION_GROUP,
        INDEXING_GROUP,
        OPERATOR,
        VARIABLE,
        TERMINATE,
        WHITESPACE
    }

    public static final String[] OPERATORS = {
        "typeof", "==", "<=", ">=", "+=", "-=", "*=", "/=", "%=", ":=", "++", "--", "=", "+", "-", "*", "/", "%", ":", "!", "<", ">", "&", "|"
    };

    public static int operatorIdx(char ch, int start) {
        for(int i = start; i < OPERATORS.length; i++) {
            if(OPERATORS[i].charAt(0) == ch) {
                return i;
            }
        }
        return -1;
    }

    Type type;
    Object value;

    public Token(Type type, Object value){
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();

        switch (type) {
            case VALUE -> {
                if(value.getClass() == String.class) {
                    content = new StringBuilder(String.format("\"%s\"", value));
                    content = new StringBuilder(content.toString().replace("\\", "\\\\").replace("\n", "\\n"));
                    break;
                }

                content.append(value);
            }
            case OPERATOR -> content = new StringBuilder(String.format("%s (%d)", OPERATORS[(int) value], (int) value));
            case OPERATION_GROUP -> {
                content = new StringBuilder();
                content.append('(');
                for(Token token : (List<Token>) value) {
                    content.append("\n\t").append(token.toString().replace("\n", "\n\t"));
                }
                if(((List<Token>) value).size() > 0) content.append("\n");
                content.append(')');
            }
            case ACTION_GROUP -> {
                content = new StringBuilder();
                content.append('{');
                for(Token token : (List<Token>) value) {
                    content.append("\n\t").append(token.toString().replace("\n", "\n\t"));
                }
                if(((List<Token>) value).size() > 0) content.append("\n");
                content.append('}');
            }
            case INDEXING_GROUP -> {
                content = new StringBuilder();
                content.append('[');
                for(Token token : (List<Token>) value) {
                    content.append("\n\t").append(token.toString().replace("\n", "\n\t"));
                }
                if(((List<Token>) value).size() > 0) content.append("\n");
                content.append(']');
            }
            default -> {
                if(value == null) return type.toString();
                content.append(value);
            }
        }

        return String.format("%s: %s", type, content);
    }
}
