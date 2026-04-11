package com.calculator;

import com.calculator.ui.CalculatorGUI;

import javax.swing.SwingUtilities;

/**
 * 科学计算器主入口 — 启动 GUI 界面
 */
public class Calculator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorGUI gui = new CalculatorGUI();
            gui.setVisible(true);
        });
    }
}
