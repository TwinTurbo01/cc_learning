package com.calculator.ui;

/**
 * 按钮动作枚举：定义计算器上每个按钮的行为类型
 */
public enum ButtonAction {
    // 数字
    DIGIT_0, DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4,
    DIGIT_5, DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9,

    // 基本运算
    ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,

    // 科学运算
    POWER, SQUARE, SQRT,
    SIN, COS, TAN, LOG, LN,

    // 辅助
    LEFT_PAREN, RIGHT_PAREN,
    PI, E,
    PERCENT, PLUS_MINUS,
    DECIMAL_POINT,

    // 控制
    EQUALS, CLEAR, BACKSPACE,
    DEG_RAD_TOGGLE
}
