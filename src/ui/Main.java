package ui;

import language.Parser;
import language.Token;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Parser parser;
        String code = Files.readString(Path.of(System.getProperty("user.dir"), "src", "codeFiles", "test.txt"), Charset.defaultCharset());
        parser = new Parser(code);
        printTokens(parser.parse());
    }

    private static void printTokens(List<Token> tokens) {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
