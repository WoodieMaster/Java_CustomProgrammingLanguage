package language;

import java.text.ParseException;
import java.util.*;

public class Parser {
    private int index;
    private char currentChar;
    private final String code;

    public Map<String, Type> types = new HashMap<>();

    private char advance() {
        try {
            return (currentChar = code.charAt(index++));
        }catch (Exception e) {
            return 0;
        }
    }

    private List<Token> evaluate() throws ParseException {
        List<Token> tokens = new ArrayList<>();

        while (advance() != 0) {
            //whitespace
            if(Character.isWhitespace(currentChar)) {
                while (Character.isWhitespace(advance()));
                index--;
                tokens.add(new Token(Token.Type.WHITESPACE, null));
                continue;
            }

            //number
            if(currentChar == '.' || Character.isDigit(currentChar)) {
                boolean isFloat = currentChar == '.';
                StringBuilder value = new StringBuilder();
                value.append(currentChar);

                while(Character.isDigit(advance()) || (currentChar == '.' && !isFloat)) {
                    if(currentChar == '.') isFloat = true;

                    value.append(currentChar);
                }
                index--;

                if(isFloat) tokens.add(new Token(Token.Type.VALUE, Float.parseFloat(value.toString())));
                else tokens.add(new Token(Token.Type.VALUE, Integer.parseInt(value.toString())));

                continue;
            }

            //string
            if(currentChar == '"') {
                StringBuilder value = new StringBuilder();

                while (advance() != '"') {
                    if(currentChar == 0 || currentChar == '\n') throw new ParseException("String has no defined end", index);

                    if(currentChar == '\\') {
                        switch (advance()) {
                            case '\n', 0 -> throw new ParseException("String has no defined end", index);
                            case 'n' -> value.append('\n');
                            case 't' -> value.append('\t');
                            default -> value.append(currentChar);
                        }
                        continue;
                    }

                    value.append(currentChar);
                }
                tokens.add(new Token(Token.Type.VALUE, value.toString()));
                continue;
            }

            //Group Start
            if(currentChar == '{' || currentChar == '(' || currentChar == '[') {
                char endChar;
                Token.Type tokenType;
                switch (currentChar) {
                    case '{' -> {
                        endChar = '}';
                        tokenType = Token.Type.ACTION_GROUP;
                    }
                    case '(' -> {
                        endChar = ')';
                        tokenType = Token.Type.OPERATION_GROUP;
                    }
                    default -> {
                        endChar = ']';
                        tokenType = Token.Type.INDEXING_GROUP;
                    }
                }
                tokens.add(new Token(tokenType, evaluate()));
                if(currentChar == 0) throw new ParseException("Group has no defined ending", index);

                if(currentChar != endChar) throw new ParseException("Group has no defined start at " + index, index);

                continue;
            }

            //Group End
            if(currentChar == ')' || currentChar == '}' || currentChar == ']') {
                break;
            }

            //terminate
            if(currentChar == ';') {
                tokens.add(new Token(Token.Type.TERMINATE, null));
                continue;
            }

            //operator
            if(Token.operatorIdx(currentChar,0) >= 0) {
                int idx = -1;
                boolean found;

                do {
                    found = true;

                    idx = Token.operatorIdx(currentChar, idx+1);
                    if(idx == -1) {
                        found = false;
                        break;
                    }

                    String operator = Token.OPERATORS[idx];

                    for(int i = 1; i < operator.length(); i++) {
                        if(code.charAt(index+i-1) != operator.charAt(i)) {
                            found = false;
                            break;
                        }
                    }
                }while (!found);

                if(found) {
                    tokens.add(new Token(Token.Type.OPERATOR, idx));
                    index += Token.OPERATORS[idx].length();
                    continue;
                }
            }

            if(Character.isLetter(currentChar)) {
                StringBuilder value = new StringBuilder().append(currentChar);

                while(Character.isLetter(advance())) {
                    value.append(currentChar);
                }
                index--;
                tokens.add(new Token(Token.Type.KEY, value.toString()));
                continue;
            }
        }

        return tokens;
    }

    public Parser(String code) {
        this.code = code;
        types.put("string", new Type(new Variable("string", null)));
    }

    public List<Token> parse() throws ParseException {
        index = 0;
        return evaluate();
    }
}
