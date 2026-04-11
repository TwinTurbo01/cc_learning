package com.calculator.parser;

import com.calculator.engine.CalculatorEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 表达式求值器：中缀表达式 → 逆波兰表示 → 求值
 * 使用调度场算法（Shunting-Yard），复用 CalculatorEngine 执行运算。
 */
public class ExpressionEvaluator {

    private final CalculatorEngine engine;
    private boolean useDegrees = true; // 默认使用角度制

    public ExpressionEvaluator(CalculatorEngine engine) {
        this.engine = engine;
    }

    public boolean isUseDegrees() { return useDegrees; }
    public void setUseDegrees(boolean useDegrees) { this.useDegrees = useDegrees; }

    // ======================== 对外接口 ========================

    /**
     * 求值表达式字符串
     * @param expression 如 "2+3*4", "sin(30)", "(1+2)^3"
     * @return 计算结果
     */
    public double evaluate(String expression) {
        List<Token> tokens = tokenize(expression);
        List<Token> rpn = toRPN(tokens);
        return evalRPN(rpn);
    }

    // ======================== 词法分析 ========================

    private static final String[] FUNCTIONS = {"sin", "cos", "tan", "sqrt", "log", "ln"};
    private static final String[] CONSTANTS = {"pi", "e"};

    private List<Token> tokenize(String expr) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);

            // 跳过空白
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // 数字（含小数）
            if (Character.isDigit(c) || c == '.') {
                int start = i;
                boolean hasDot = (c == '.');
                i++;
                while (i < expr.length()) {
                    char cc = expr.charAt(i);
                    if (Character.isDigit(cc)) {
                        i++;
                    } else if (cc == '.' && !hasDot) {
                        hasDot = true;
                        i++;
                    } else {
                        break;
                    }
                }
                tokens.add(new Token(TokenType.NUMBER, Double.parseDouble(expr.substring(start, i))));
                continue;
            }

            // 字母（函数名、常量名）
            if (Character.isLetter(c) || c == '_') {
                int start = i;
                while (i < expr.length() && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) {
                    i++;
                }
                String name = expr.substring(start, i);
                String lower = name.toLowerCase();

                if (isFunction(lower)) {
                    tokens.add(new Token(TokenType.FUNCTION, lower));
                } else if ("pi".equals(lower)) {
                    tokens.add(new Token(TokenType.CONSTANT, Math.PI));
                } else if ("e".equals(lower)) {
                    tokens.add(new Token(TokenType.CONSTANT, Math.E));
                } else {
                    throw new IllegalArgumentException("未知标识符: " + name);
                }
                continue;
            }

            // 运算符和括号
            switch (c) {
                case '+':
                    tokens.add(new Token(TokenType.OPERATOR, "+"));
                    break;
                case '-':
                    tokens.add(new Token(TokenType.OPERATOR, "-"));
                    break;
                case '*':
                case '\u00D7': // ×
                    tokens.add(new Token(TokenType.OPERATOR, "*"));
                    break;
                case '/':
                case '\u00F7': // ÷
                    tokens.add(new Token(TokenType.OPERATOR, "/"));
                    break;
                case '%':
                    tokens.add(new Token(TokenType.OPERATOR, "%"));
                    break;
                case '^':
                    tokens.add(new Token(TokenType.OPERATOR, "^"));
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                    break;
                default:
                    throw new IllegalArgumentException("非法字符: " + c);
            }
            i++;
        }
        return tokens;
    }

    private boolean isFunction(String name) {
        for (String f : FUNCTIONS) {
            if (f.equals(name)) return true;
        }
        return false;
    }

    // ======================== 调度场算法 ========================

    private int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "%":
                return 2;
            case "^":
                return 4;
            default:
                return 0;
        }
    }

    private boolean isRightAssociative(String op) {
        return "^".equals(op);
    }

    private List<Token> toRPN(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Stack<Token> opStack = new Stack<>();

        for (Token token : tokens) {
            switch (token.type) {
                case NUMBER:
                case CONSTANT:
                    output.add(token);
                    break;

                case FUNCTION:
                    opStack.push(token);
                    break;

                case OPERATOR:
                    while (!opStack.isEmpty()) {
                        Token top = opStack.peek();
                        if (top.type == TokenType.OPERATOR) {
                            int p1 = precedence(token.value);
                            int p2 = precedence(top.value);
                            if ((p2 > p1) || (p2 == p1 && !isRightAssociative(token.value))) {
                                output.add(opStack.pop());
                                continue;
                            }
                        }
                        break;
                    }
                    opStack.push(token);
                    break;

                case LEFT_PAREN:
                    opStack.push(token);
                    break;

                case RIGHT_PAREN:
                    while (!opStack.isEmpty() && opStack.peek().type != TokenType.LEFT_PAREN) {
                        output.add(opStack.pop());
                    }
                    if (opStack.isEmpty()) {
                        throw new IllegalArgumentException("括号不匹配");
                    }
                    opStack.pop(); // 弹出左括号
                    if (!opStack.isEmpty() && opStack.peek().type == TokenType.FUNCTION) {
                        output.add(opStack.pop());
                    }
                    break;
            }
        }

        while (!opStack.isEmpty()) {
            Token top = opStack.pop();
            if (top.type == TokenType.LEFT_PAREN) {
                throw new IllegalArgumentException("括号不匹配");
            }
            output.add(top);
        }

        return output;
    }

    // ======================== RPN 求值 ========================

    private double evalRPN(List<Token> rpn) {
        Stack<Double> stack = new Stack<>();

        for (Token token : rpn) {
            switch (token.type) {
                case NUMBER:
                case CONSTANT:
                    stack.push(token.valueNum);
                    break;

                case OPERATOR: {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("运算符 " + token.value + " 参数不足");
                    }
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(applyOperator(a, b, token.value));
                    break;
                }

                case FUNCTION: {
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("函数 " + token.value + " 参数不足");
                    }
                    double a = stack.pop();
                    stack.push(applyFunction(a, token.value));
                    break;
                }
            }
        }

        if (stack.isEmpty()) {
            return 0;
        }
        if (stack.size() > 1) {
            throw new IllegalArgumentException("表达式不完整");
        }
        return stack.pop();
    }

    private double applyOperator(double a, double b, String op) {
        switch (op) {
            case "+": return engine.add(a, b);
            case "-": return engine.subtract(a, b);
            case "*": return engine.multiply(a, b);
            case "/": return engine.divide(a, b);
            case "%": return engine.modulo(a, b);
            case "^": return engine.power(a, b);
            default:
                throw new IllegalArgumentException("未知运算符: " + op);
        }
    }

    private double applyFunction(double a, String name) {
        switch (name) {
            case "sin":
                return engine.sin(useDegrees ? engine.toRadians(a) : a);
            case "cos":
                return engine.cos(useDegrees ? engine.toRadians(a) : a);
            case "tan":
                return engine.tan(useDegrees ? engine.toRadians(a) : a);
            case "sqrt":
                return engine.sqrt(a);
            case "log":
                return engine.log(a);
            case "ln":
                return engine.ln(a);
            default:
                throw new IllegalArgumentException("未知函数: " + name);
        }
    }

    // ======================== Token 类型 ========================

    private enum TokenType {
        NUMBER, CONSTANT, OPERATOR, FUNCTION, LEFT_PAREN, RIGHT_PAREN
    }

    private static class Token {
        final TokenType type;
        final String value; // 用于 OPERATOR 和 FUNCTION
        final double valueNum; // 用于 NUMBER 和 CONSTANT（alias for value field）

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
            this.valueNum = 0;
        }

        Token(TokenType type, double value) {
            this.type = type;
            this.value = null;
            this.valueNum = value;
        }

        // Convenience accessors to handle the dual nature
        // For NUMBER/CONSTANT tokens, use .valueNum directly
    }
}
