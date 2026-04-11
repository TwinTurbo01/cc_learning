package com.calculator.ui;

import com.calculator.engine.CalculatorEngine;
import com.calculator.parser.ExpressionEvaluator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 科学计算器 GUI — 经典计算器布局，深色主题
 */
public class CalculatorGUI extends JFrame {

    // ======================== 颜色常量 ========================

    private static final Color BG_WINDOW = new Color(0x2D, 0x2D, 0x3F);
    private static final Color BG_DISPLAY = new Color(0x1E, 0x1E, 0x2E);
    private static final Color BG_NUMBER = new Color(0x3D, 0x3D, 0x5C);
    private static final Color BG_OPERATOR = new Color(0xFF, 0x95, 0x00);
    private static final Color BG_FUNCTION = new Color(0x50, 0x50, 0x70);
    private static final Color BG_EQUALS = new Color(0xFF, 0x6B, 0x00);
    private static final Color BG_CLEAR = new Color(0xCC, 0x33, 0x33);
    private static final Color BG_SPECIAL = new Color(0x63, 0x63, 0x80);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_RESULT = new Color(0xEE, 0xEE, 0xEE);
    private static final Color TEXT_EXPRESSION = new Color(0x88, 0x88, 0x99);
    private static final Color TEXT_ERROR = new Color(0xFF, 0x44, 0x44);
    private static final Color TEXT_DARK = new Color(0x22, 0x22, 0x22);

    // ======================== 状态枚举 ========================

    private enum CalcState {
        INPUT,   // 正在输入表达式
        RESULT,  // 已显示结果
        ERROR    // 出错
    }

    // ======================== 组件 ========================

    private JLabel expressionLabel;  // 上方：正在输入的表达式
    private JLabel resultLabel;      // 下方：计算结果
    private JLabel modeLabel;        // 左上角 DEG/RAD 指示
    private CalcState state = CalcState.INPUT;
    private StringBuilder expression = new StringBuilder();

    // ======================== 引擎 ========================

    private final ExpressionEvaluator evaluator;

    // ======================== 按钮定义 ========================

    /** 按钮布局：{显示文本, ButtonAction, 背景色, 前景色} */
    private static final Object[][] BUTTONS = {
        // 第1行：科学函数
        {"sin",  ButtonAction.SIN,       BG_FUNCTION, TEXT_LIGHT},
        {"cos",  ButtonAction.COS,       BG_FUNCTION, TEXT_LIGHT},
        {"tan",  ButtonAction.TAN,       BG_FUNCTION, TEXT_LIGHT},
        {"log",  ButtonAction.LOG,       BG_FUNCTION, TEXT_LIGHT},
        {"ln",   ButtonAction.LN,        BG_FUNCTION, TEXT_LIGHT},

        // 第2行：扩展函数
        {"x\u00B2",  ButtonAction.SQUARE,  BG_FUNCTION, TEXT_LIGHT},  // x²
        {"\u221A",   ButtonAction.SQRT,    BG_FUNCTION, TEXT_LIGHT},  // √
        {"\u03C0",   ButtonAction.PI,      BG_FUNCTION, TEXT_LIGHT},  // π
        {"e",    ButtonAction.E,         BG_FUNCTION, TEXT_LIGHT},
        {"mod",  ButtonAction.MODULO,    BG_FUNCTION, TEXT_LIGHT},

        // 第3行：括号、清除
        {"(",    ButtonAction.LEFT_PAREN,  BG_SPECIAL, TEXT_LIGHT},
        {")",    ButtonAction.RIGHT_PAREN, BG_SPECIAL, TEXT_LIGHT},
        {"%",    ButtonAction.PERCENT,     BG_SPECIAL, TEXT_LIGHT},
        {"C",    ButtonAction.CLEAR,       BG_CLEAR,   TEXT_LIGHT},
        {"\u232B",  ButtonAction.BACKSPACE,  BG_CLEAR,   TEXT_LIGHT},  // ⌫

        // 第4行：数字
        {"7",    ButtonAction.DIGIT_7,    BG_NUMBER,  TEXT_LIGHT},
        {"8",    ButtonAction.DIGIT_8,    BG_NUMBER,  TEXT_LIGHT},
        {"9",    ButtonAction.DIGIT_9,    BG_NUMBER,  TEXT_LIGHT},
        {"\u00F7", ButtonAction.DIVIDE,    BG_OPERATOR, TEXT_DARK},    // ÷
        {"+/-",  ButtonAction.PLUS_MINUS, BG_SPECIAL, TEXT_LIGHT},

        // 第5行：数字
        {"4",    ButtonAction.DIGIT_4,    BG_NUMBER,  TEXT_LIGHT},
        {"5",    ButtonAction.DIGIT_5,    BG_NUMBER,  TEXT_LIGHT},
        {"6",    ButtonAction.DIGIT_6,    BG_NUMBER,  TEXT_LIGHT},
        {"\u00D7", ButtonAction.MULTIPLY,  BG_OPERATOR, TEXT_DARK},    // ×
        {"DEG",  ButtonAction.DEG_RAD_TOGGLE, BG_SPECIAL, TEXT_LIGHT},

        // 第6行：数字
        {"1",    ButtonAction.DIGIT_1,    BG_NUMBER,  TEXT_LIGHT},
        {"2",    ButtonAction.DIGIT_2,    BG_NUMBER,  TEXT_LIGHT},
        {"3",    ButtonAction.DIGIT_3,    BG_NUMBER,  TEXT_LIGHT},
        {"\u2212", ButtonAction.SUBTRACT,  BG_OPERATOR, TEXT_DARK},    // −

        // 第7行：底部
        {"0",    ButtonAction.DIGIT_0,    BG_NUMBER,  TEXT_LIGHT},
        {".",    ButtonAction.DECIMAL_POINT, BG_NUMBER, TEXT_LIGHT},
        {"=",    ButtonAction.EQUALS,     BG_EQUALS,  TEXT_LIGHT},
        {"+",    ButtonAction.ADD,        BG_OPERATOR, TEXT_DARK},
    };

    // ======================== 构造函数 ========================

    public CalculatorGUI() {
        CalculatorEngine engine = new CalculatorEngine();
        evaluator = new ExpressionEvaluator(engine);

        initFrame();
        initComponents();
        initKeyboardBindings();
    }

    private void initFrame() {
        setTitle("科学计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 560);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_WINDOW);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));

        // ===== 显示面板 =====
        JPanel displayPanel = new JPanel(new BorderLayout(8, 4));
        displayPanel.setBackground(BG_DISPLAY);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // 模式指示标签（可点击）
        modeLabel = new JLabel("DEG");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        modeLabel.setForeground(TEXT_EXPRESSION);
        modeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        modeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                toggleDegRad();
            }
        });
        displayPanel.add(modeLabel, BorderLayout.LINE_START);

        // 表达式 + 结果
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG_DISPLAY);

        expressionLabel = new JLabel("");
        expressionLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        expressionLabel.setForeground(TEXT_EXPRESSION);
        expressionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        expressionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        resultLabel = new JLabel("0");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        resultLabel.setForeground(TEXT_RESULT);
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        resultLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        textPanel.add(expressionLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(resultLabel);

        displayPanel.add(textPanel, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.NORTH);

        // ===== 按钮面板 =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(BG_WINDOW);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 3, 3, 3);

        int col = 0, row = 0;
        for (Object[] btn : BUTTONS) {
            String text = (String) btn[0];
            ButtonAction action = (ButtonAction) btn[1];
            Color bg = (Color) btn[2];
            Color fg = (Color) btn[3];

            JButton button = createButton(text, action, bg, fg);
            gbc.gridx = col;
            gbc.gridy = row;

            // 0 按钮占两列
            if (action == ButtonAction.DIGIT_0) {
                gbc.gridwidth = 2;
            } else {
                gbc.gridwidth = 1;
            }

            // 最后一行特殊处理：0(2列) + . + = + + = 5列
            if (action == ButtonAction.DIGIT_0 || action == ButtonAction.DECIMAL_POINT
                    || action == ButtonAction.EQUALS || action == ButtonAction.ADD) {
                gbc.weightx = 1.0;
            } else {
                gbc.weightx = 1.0;
            }
            gbc.weighty = 1.0;

            buttonPanel.add(button, gbc);

            col += (action == ButtonAction.DIGIT_0) ? 2 : 1;
            if (col >= 5) {
                col = 0;
                row++;
            }
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * 创建按钮并设置样式和事件监听
     */
    private JButton createButton(String text, ButtonAction action, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 小字体按钮（科学函数）
        if (isSmallButton(action)) {
            button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        // 等号按钮大字体
        if (action == ButtonAction.EQUALS) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        }

        // 悬停和按下效果
        Color hoverBg = bg.brighter();
        Color pressedBg = bg.darker();

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bg);
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(pressedBg);
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
            }
        });

        // 点击事件
        button.addActionListener(e -> onButtonPress(action, text));

        return button;
    }

    private boolean isSmallButton(ButtonAction action) {
        return action == ButtonAction.SIN || action == ButtonAction.COS
                || action == ButtonAction.TAN || action == ButtonAction.LOG
                || action == ButtonAction.LN || action == ButtonAction.SQUARE
                || action == ButtonAction.SQRT || action == ButtonAction.DEG_RAD_TOGGLE;
    }

    // ======================== 按钮事件处理 ========================

    private void onButtonPress(ButtonAction action, String text) {
        switch (action) {
            // 数字
            case DIGIT_0: case DIGIT_1: case DIGIT_2: case DIGIT_3: case DIGIT_4:
            case DIGIT_5: case DIGIT_6: case DIGIT_7: case DIGIT_8: case DIGIT_9:
                inputDigit(text);
                break;

            case DECIMAL_POINT:
                inputDecimalPoint();
                break;

            // 运算符
            case ADD:
                inputOperator("+");
                break;
            case SUBTRACT:
                inputOperator("-");
                break;
            case MULTIPLY:
                inputOperator("*");
                break;
            case DIVIDE:
                inputOperator("/");
                break;
            case MODULO:
                inputOperator("%");
                break;
            case POWER:
                inputOperator("^");
                break;

            // 一元运算
            case SQUARE:
                inputFunction("square");
                break;
            case SQRT:
                inputFunction("sqrt");
                break;
            case SIN:
                inputFunction("sin");
                break;
            case COS:
                inputFunction("cos");
                break;
            case TAN:
                inputFunction("tan");
                break;
            case LOG:
                inputFunction("log");
                break;
            case LN:
                inputFunction("ln");
                break;

            // 常量
            case PI:
                inputConstant("pi");
                break;
            case E:
                inputConstant("e");
                break;

            // 括号
            case LEFT_PAREN:
                inputChar("(");
                break;
            case RIGHT_PAREN:
                inputChar(")");
                break;

            // 百分号（÷100）
            case PERCENT:
                appendToExpression("/100");
                updateDisplay();
                break;

            // 正负号
            case PLUS_MINUS:
                toggleSign();
                break;

            // 控制
            case EQUALS:
                evaluateExpression();
                break;
            case CLEAR:
                clearAll();
                break;
            case BACKSPACE:
                backspace();
                break;

            // 角度/弧度切换
            case DEG_RAD_TOGGLE:
                toggleDegRad();
                break;
        }
    }

    // ======================== 输入处理 ========================

    private void inputDigit(String digit) {
        if (state == CalcState.RESULT || state == CalcState.ERROR) {
            // 从结果/错误状态开始新表达式
            clearAll();
            state = CalcState.INPUT;
        }
        expression.append(digit);
        updateDisplay();
    }

    private void inputDecimalPoint() {
        if (state == CalcState.RESULT || state == CalcState.ERROR) {
            clearAll();
            expression.append("0");
            state = CalcState.INPUT;
        }
        // 防止重复小数点：检查当前数字是否已有小数点
        if (!hasCurrentNumberDecimalPoint()) {
            expression.append(".");
            updateDisplay();
        }
    }

    private boolean hasCurrentNumberDecimalPoint() {
        String expr = expression.toString();
        // 从末尾向前找，直到遇到运算符或开头
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == '.') return true;
            if ("+-*/%^( ".indexOf(c) >= 0) break;
            // 函数名结尾也中断
            if (i >= 2 && expr.substring(Math.max(0, i - 2), i + 1).matches("(sin|cos|tan|log|sqrt|ln)")) break;
        }
        return false;
    }

    private void inputOperator(String op) {
        if (state == CalcState.ERROR) return;

        if (state == CalcState.RESULT) {
            // 从结果继续运算
            String prevResult = resultLabel.getText();
            expression.setLength(0);
            expression.append(prevResult);
            state = CalcState.INPUT;
        }

        expression.append(op);
        updateDisplay();
    }

    private void inputFunction(String funcName) {
        if (state == CalcState.RESULT || state == CalcState.ERROR) {
            if (state == CalcState.RESULT && !expression.toString().isEmpty()) {
                // 如果之前有结果，在其上应用函数
                String prevResult = resultLabel.getText();
                expression.setLength(0);
                expression.append(funcName).append("(").append(prevResult).append(")");
                updateDisplay();
                evaluateExpression();
                return;
            }
            clearAll();
            state = CalcState.INPUT;
        }

        if ("square".equals(funcName)) {
            expression.append("^2");
        } else {
            expression.append(funcName).append("(");
        }
        updateDisplay();
    }

    private void inputConstant(String name) {
        if (state == CalcState.RESULT || state == CalcState.ERROR) {
            clearAll();
            state = CalcState.INPUT;
        }
        expression.append(name);
        updateDisplay();
    }

    private void inputChar(String ch) {
        if (state == CalcState.RESULT || state == CalcState.ERROR) {
            clearAll();
            state = CalcState.INPUT;
        }
        expression.append(ch);
        updateDisplay();
    }

    /**
     * 正负号切换：如果表达式以数字结尾，在该数字前加负号
     */
    private void toggleSign() {
        if (expression.length() == 0) {
            expression.append("-");
        } else {
            String expr = expression.toString();
            // 找到最后一个数字的起始位置
            int numStart = findLastNumberStart(expr);
            if (numStart >= 0 && expr.charAt(numStart) == '-') {
                // 已经有负号，去掉
                expression.deleteCharAt(numStart);
            } else if (numStart >= 0) {
                // 添加负号
                expression.insert(numStart, '-');
            } else {
                expression.insert(0, '-');
            }
        }
        updateDisplay();
    }

    private int findLastNumberStart(String expr) {
        int i = expr.length() - 1;
        while (i >= 0 && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
            i--;
        }
        if (i >= 0 && expr.charAt(i) == '-') {
            // 检查这是否是负号而不是减号
            if (i == 0 || "+-*/%^( ".indexOf(expr.charAt(i - 1)) >= 0) {
                return i;
            }
            i++; // 这是减号，不是负号
        } else {
            i++;
        }
        return (i < expr.length()) ? i : -1;
    }

    private void appendToExpression(String text) {
        if (state == CalcState.RESULT || state == CalcState.ERROR) {
            clearAll();
            state = CalcState.INPUT;
        }
        expression.append(text);
    }

    private void clearAll() {
        expression.setLength(0);
        resultLabel.setForeground(TEXT_RESULT);
        resultLabel.setText("0");
        expressionLabel.setText("");
        state = CalcState.INPUT;
    }

    private void backspace() {
        if (state != CalcState.INPUT) return;
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
        }
        updateDisplay();
    }

    // ======================== 计算 ========================

    private void evaluateExpression() {
        if (state == CalcState.ERROR) return;

        String expr = expression.toString().trim();
        if (expr.isEmpty()) return;

        expressionLabel.setText(expr);

        try {
            double result = evaluator.evaluate(expr);
            String resultText = formatResult(result);
            resultLabel.setForeground(TEXT_RESULT);
            resultLabel.setText(resultText);
            state = CalcState.RESULT;
        } catch (Exception e) {
            resultLabel.setForeground(TEXT_ERROR);
            resultLabel.setText("Error");
            state = CalcState.ERROR;
        }
    }

    private String formatResult(double value) {
        if (Double.isNaN(value)) return "NaN";
        if (Double.isInfinite(value)) return "∞";

        // 整数显示
        if (value == Math.floor(value) && !Double.isInfinite(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }

        // 非常大或非常小的数用科学计数法
        if (Math.abs(value) >= 1e15 || (Math.abs(value) < 1e-6 && value != 0)) {
            return String.format("%.6e", value);
        }

        // 正常小数
        String s = String.valueOf(value);
        // 去掉末尾多余的0
        if (s.contains(".")) {
            s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return s;
    }

    // ======================== 显示更新 ========================

    private void updateDisplay() {
        if (state == CalcState.INPUT) {
            String expr = expression.toString();
            expressionLabel.setText(expr);
            // 实时预览：尝试计算当前表达式
            if (!expr.isEmpty()) {
                try {
                    double preview = evaluator.evaluate(expr);
                    resultLabel.setForeground(TEXT_RESULT);
                    resultLabel.setText(formatResult(preview));
                } catch (Exception e) {
                    // 表达式不完整，不更新预览
                }
            } else {
                resultLabel.setForeground(TEXT_RESULT);
                resultLabel.setText("0");
            }
        }
    }

    // ======================== DEG/RAD 切换 ========================

    private void toggleDegRad() {
        boolean useDeg = !evaluator.isUseDegrees();
        evaluator.setUseDegrees(useDeg);
        String mode = useDeg ? "DEG" : "RAD";
        modeLabel.setText(mode);

        // 更新按钮文本
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                updateDegRadButtonText((Container) comp, mode);
            }
        }
    }

    private void updateDegRadButtonText(Container container, String mode) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if ("RAD".equals(btn.getText()) || "DEG".equals(btn.getText())) {
                    btn.setText(mode);
                    break;
                }
            }
            if (comp instanceof Container) {
                updateDegRadButtonText((Container) comp, mode);
            }
        }
    }

    // ======================== 键盘支持 ========================

    private void initKeyboardBindings() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        String[] keys = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            ".", "+", "-", "*", "/", "%", "^",
            "(", ")",
            "ENTER", "ESCAPE", "BACK_SPACE", "DELETE",
            "S", "C", "T", "L", "N", "P", "R"
        };
        ButtonAction[] actions = {
            ButtonAction.DIGIT_0, ButtonAction.DIGIT_1, ButtonAction.DIGIT_2,
            ButtonAction.DIGIT_3, ButtonAction.DIGIT_4, ButtonAction.DIGIT_5,
            ButtonAction.DIGIT_6, ButtonAction.DIGIT_7, ButtonAction.DIGIT_8,
            ButtonAction.DIGIT_9,
            ButtonAction.DECIMAL_POINT,
            ButtonAction.ADD, ButtonAction.SUBTRACT,
            ButtonAction.MULTIPLY, ButtonAction.DIVIDE,
            ButtonAction.PERCENT, ButtonAction.POWER,
            ButtonAction.LEFT_PAREN, ButtonAction.RIGHT_PAREN,
            ButtonAction.EQUALS, ButtonAction.CLEAR,
            ButtonAction.BACKSPACE, ButtonAction.CLEAR,
            ButtonAction.SIN, ButtonAction.COS, ButtonAction.TAN,
            ButtonAction.LOG, ButtonAction.LN,
            ButtonAction.PI, ButtonAction.SQRT
        };

        for (int i = 0; i < keys.length; i++) {
            String keyStroke = keys[i];
            ButtonAction action = actions[i];
            inputMap.put(KeyStroke.getKeyStroke(keyStroke), keyStroke);
            final ButtonAction fa = action;
            final String fk = keyStroke;
            actionMap.put(keyStroke, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 映射按键文本
                    String btnText = fk;
                    switch (fk) {
                        case "*": btnText = "\u00D7"; break;
                        case "/": btnText = "\u00F7"; break;
                        case "-": btnText = "\u2212"; break;
                        case "ENTER": btnText = "="; break;
                        case "ESCAPE": btnText = "C"; break;
                        case "BACK_SPACE": btnText = "\u232B"; break;
                        case "DELETE": btnText = "C"; break;
                    }
                    onButtonPress(fa, btnText);
                }
            });
        }
    }
}
