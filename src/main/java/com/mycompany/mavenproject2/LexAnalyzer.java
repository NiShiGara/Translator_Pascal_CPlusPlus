package com.mycompany.mavenproject2;

import java.io.*;
import java.util.*;

// класс лексического анализатора Pascal
public class LexAnalyzer {

    private final String[] keyWord = {
        "begin", "end", "for", "to", "var", "downto", "do",
        "while", "repeat", "until", "if", "then", "else",
        "case", "break", "real", "char", "string",
        "boolean", "abs", "sqr", "sqrt", "exp", "write",
        "writeln", "readln", "true", "false", "integer"
    };

    private final String[] bracket = {
        "(", ")"
    };

    private final String[] num = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private final String[] compare = {
        ">", "<", "<=", ">=", "<>", "="
    };

    private final String[] charPascal = {
        "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
        "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M",
        "й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з", "х", "ъ", "ф", "ы", "в", "а", "п", "р", "о", "л", "д", "ж", "э", "я", "ч", "с", "м", "и", "т", "ь", "б", "ю",
        "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш", "Щ", "З", "Х", "Ъ", "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д", "Ж", "Э", "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю"
    };

    private final String[] separator = {
        ":", ".", ";", ","
    };

    private final String[] operator = {
        "+", "-", "*", "/"
    };

    private String input;
    private final ArrayList<Pair> output;

// конструктор создания класса с указанием названия считываемого файла
    private void setInput(String fileName) {
        try ( FileReader fr = new FileReader(fileName);  Scanner scan = new Scanner(fr)) {
            this.input = "";
            while (scan.hasNextLine()) {
                this.input += scan.nextLine();
                this.input += " $ ";
            }
        } catch (IOException e) {
            System.out.println("File err" + e);
        }
    }

    LexAnalyzer(String fileName) {
        this.output = new ArrayList();
        setInput(fileName);
    }

// метод формирования списка лексем из считываемого файла
    public void makeAnalysis() throws Exception {
        String lexema = new String();
        int i = 0;
        int numOfString = 1;
        while (i < this.input.length()) {
            lexema += this.input.charAt(i);
            if (lexema.charAt(0) == '\'') {
                i++;
                while (this.input.charAt(i) != '\'') {
                    lexema += this.input.charAt(i);
                    i++;
                }
                lexema += this.input.charAt(i);
                if (lexema.length() != 3) { // строка
                    Pair lex = new Pair("string", lexema, numOfString);
                    this.output.add(lex);
                } else { // символ
                    Pair lex = new Pair("char", lexema, numOfString);
                    this.output.add(lex);
                }
                lexema = "";
            }
            // скобка
            if (find(this.bracket, lexema)) {
                Pair lex = new Pair("bracket", lexema, numOfString);
                this.output.add(lex);
                lexema = "";
            }
            if (find(this.separator, lexema)) {
                //присваивание
                if (lexema.charAt(0) == ':' && this.input.charAt(i + 1) == '=') {
                    i++;
                    lexema += this.input.charAt(i);
                    Pair lex = new Pair("assignment", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                } else {
                    //разделитель
                    Pair lex = new Pair("separator", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                }
            }
            if (find(this.operator, lexema)) {
                //присваивание
                if (this.input.charAt(i + 1) == '=') {
                    i++;
                    lexema += this.input.charAt(i);
                    Pair lex = new Pair("assignment", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                } else {
                    // операция типа +
                    if (lexema.charAt(0) == '+' || lexema.charAt(0) == '-') {
                        Pair lex = new Pair("plus operator", lexema, numOfString);
                        this.output.add(lex);
                        lexema = "";
                    } else {
                        //операция типа *
                        Pair lex = new Pair("mult operator", lexema, numOfString);
                        this.output.add(lex);
                        lexema = "";
                    }
                }
            }
            //сравнение
            if (find(this.compare, lexema)) {
                if ((this.input.charAt(i + 1) == '=' && lexema.charAt(0) != '=') || (lexema.charAt(0) == '<' && this.input.charAt(i + 1) == '>')) {
                    i++;
                    lexema += this.input.charAt(i);
                    Pair lex = new Pair("compare", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                } else {
                    Pair lex = new Pair("compare", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                }
            }
            //числа
            if (find(this.num, lexema)) {
                String nextChar = new String();
                nextChar += this.input.charAt(i + 1);
                while (find(this.num, nextChar)) {
                    i++;
                    lexema += this.input.charAt(i);
                    nextChar = "";
                    nextChar += this.input.charAt(i + 1);
                }
                if (nextChar.charAt(0) == '.') {
                    i++;
                    lexema += this.input.charAt(i);
                    nextChar = "";
                    nextChar += this.input.charAt(i + 1);
                    while (find(this.num, nextChar)) {
                        i++;
                        lexema += this.input.charAt(i);
                        nextChar = "";
                        nextChar += this.input.charAt(i + 1);
                    }
                    Pair lex = new Pair("real", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                } else {
                    Pair lex = new Pair("integer", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                }
            }
            if (find(this.charPascal, lexema)) {
                String nextChar = new String();
                nextChar += this.input.charAt(i + 1);
                while (find(this.num, nextChar) || find(this.charPascal, nextChar) || nextChar.charAt(0) == '_') {
                    i++;
                    lexema += this.input.charAt(i);
                    nextChar = "";
                    nextChar += this.input.charAt(i + 1);
                }
                //ключевое слово языка
                if (find(this.keyWord, lexema)) {
                    Pair lex = new Pair("keyword", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                } else { //идентификатор
                    Pair lex = new Pair("id", lexema, numOfString);
                    this.output.add(lex);
                    lexema = "";
                }
            }
            if (lexema.length() == 1 && lexema.charAt(0) == ' ') {
                lexema = "";
            }
            if (lexema.length() == 1 && lexema.charAt(0) == '$') {
                numOfString++;
                lexema = "";
            }
            if (lexema.length() == 1) {
                throw new Exception("Недопустимый символ грамматики \'"
                        + lexema.charAt(0)
                        + "\' в строке "
                        + numOfString);
            }
            i++;
        }
    }

    private boolean find(String[] sourse, String sample) {
        boolean isFind = false;
        for (String atom : sourse) {
            if (sample.equals(atom)) {
                isFind = true;
            }
        }
        return isFind;
    }
// метод вывода списа лексем на экран
    public void print() throws UnsupportedEncodingException {

        PrintStream ps = new PrintStream(System.out, false, "utf-8");
        for (int i = 0; i < this.output.size(); i++) {
            this.output.get(i).print();
            ps.println();
        }
    }

// возвращает список лексем, полученый при анализе
    public ArrayList<Pair> getListLexem() {
        return this.output;
    }
}
