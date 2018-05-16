package calc;

import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;

public class Main {

    public static void main(String[] args) {
        if (args == null || args.length != 1)
            System.out.println("Application must have one start argument");

        try {
            new Calc().calculate(args[0].trim());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }
}

class Calc {

    void calculate(String input) throws Exception {

        if (!isCorrect(input)) throw new Exception("Expression contain illegal symbol(s)");

        Deque<String> deque      = split(input);

        List<String> expression  = transform(deque);

        Double answer            = calculateAnswer(expression);

        System.out.println(answer);
    }


    private boolean isCorrect(String exp) {
        //input string must contain only numeric and operators
        return Pattern.compile("([-+()*/.\\d]+)").matcher(exp).matches();
    }


    private ArrayDeque<String> split(String exp) {
        //add whitespace before and after operators and split
        return new ArrayDeque<>(Arrays.asList(exp.split("(?<=[-+()*/])|(?=[-+()*/])")));
    }


    private List<String> transform(Deque<String> expression) {
        //transform expression into Reverse Polish Notation

        Deque<String> output = new ArrayDeque<>();
        Stack<String> stack = new Stack<>();

        for (String s : expression) {
            /*need for debug*/
            System.out.println(stack);
            System.out.println(output);

            if (isNumber(s)) {
                output.add(s);
                continue;
            }

            if (stack.size() == 0) {
                stack.push(s);
                continue;
            }

            if (priority(s) <= priority(stack.peek())) {
                if (!s.equals("(")) pushStack(stack, output);
            }

            stack.push(s);
        }
        pushStack(stack, output);
        return new LinkedList<>(output);
    }


    private boolean isNumber(String s) {
        return s.chars().allMatch(this::isNumber);
    }


    private boolean isNumber(int c) {
        return Character.isDigit(c) || c=='.';
    }


    private int priority(String s) {

        switch (s){
            case "+": return 1;
            case "-": return 1;
            case "*": return 2;
            case "/": return 2;
            case "(": return 0;
            case ")": return 3;
        }
        return -1;
    }


    private void pushStack(Stack<String> stack, Deque<String> output) {

        Collections.reverse(stack);
        Iterator<String> iterator = stack.iterator();
        while (iterator.hasNext()) {
            String s1 = iterator.next();

            if (!(s1.equals("(")||s1.equals(")"))) {
                output.add(s1);
            }
            iterator.remove();
        }
    }


    private Double calculateAnswer(List<String> expression) throws Exception {
        Double a, b, result;

        if (expression.size() == 1) return parseDouble(expression.get(0));

        for (int i = 2; i < expression.size(); i++) {

            //need for debug
            System.out.println(expression);

            if (!isNumber(expression.get(i))) {

                try {
                    a = parseDouble(expression.get(i-2));
                    b = parseDouble(expression.get(i-1));

                    result = operation(a, b, expression.get(i));

                    expression.subList(i-2, i+1).clear();
                    expression.add(i-2, String.valueOf(result));

                    i-=2;
                }
                catch (IndexOutOfBoundsException e) {
                    throw new Exception("Operator in wrong position!", e);
                }
            }
        }
        return parseDouble(expression.get(0));
    }


    private Double operation(Double a, Double b, String operation) {

        switch (operation) {
            case "+": return a+b;
            case "-": return a-b;
            case "*": return a*b;
            case "/": return a/b;
        }
        return null;
    }
}
