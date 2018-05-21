package postaurant.context;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("singleton")
    public class KeyboardList {
        private static ArrayList<String> qwerty = new ArrayList<>();

        public KeyboardList() {
            qwerty.add("1");
            qwerty.add("2");
            qwerty.add("3");
            qwerty.add("4");
            qwerty.add("5");
            qwerty.add("6");
            qwerty.add("7");
            qwerty.add("8");
            qwerty.add("9");
            qwerty.add("0");

            qwerty.add("q");
            qwerty.add("w");
            qwerty.add("e");
            qwerty.add("r");
            qwerty.add("t");
            qwerty.add("y");
            qwerty.add("u");
            qwerty.add("i");
            qwerty.add("o");
            qwerty.add("p");
            qwerty.add("a");
            qwerty.add("s");
            qwerty.add("d");
            qwerty.add("f");
            qwerty.add("g");
            qwerty.add("h");
            qwerty.add("j");
            qwerty.add("k");
            qwerty.add("l");
            qwerty.add("<--");
            qwerty.add("");
            qwerty.add("z");
            qwerty.add("x");
            qwerty.add("c");
            qwerty.add("v");
            qwerty.add("b");
            qwerty.add("n");
            qwerty.add("m");
            qwerty.add("/");
            qwerty.add(".");

            qwerty.add("Q");
            qwerty.add("W");
            qwerty.add("E");
            qwerty.add("R");
            qwerty.add("T");
            qwerty.add("Y");
            qwerty.add("U");
            qwerty.add("I");
            qwerty.add("O");
            qwerty.add("P");
            qwerty.add("A");
            qwerty.add("S");
            qwerty.add("D");
            qwerty.add("F");
            qwerty.add("G");
            qwerty.add("H");
            qwerty.add("J");
            qwerty.add("K");
            qwerty.add("L");
            qwerty.add("<--");
            qwerty.add("");
            qwerty.add("Z");
            qwerty.add("X");
            qwerty.add("C");
            qwerty.add("V");
            qwerty.add("B");
            qwerty.add("N");
            qwerty.add("M");
            qwerty.add("/");
            qwerty.add(".");
        }

        public ArrayList<String> getQwerty() {
            return qwerty;
        }

    }

