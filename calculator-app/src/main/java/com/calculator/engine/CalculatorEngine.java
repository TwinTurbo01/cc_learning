package com.calculator.engine;

/**
 * 计算器核心引擎：负责所有运算逻辑
 */
public class CalculatorEngine {

    /**
     * 加法
     */
    public double add(double a, double b) {
        return a + b;
    }

    /**
     * 减法
     */
    public double subtract(double a, double b) {
        return a - b;
    }

    /**
     * 乘法
     */
    public double multiply(double a, double b) {
        return a * b;
    }

    /**
     * 除法
     */
    public double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("除数不能为零");
        }
        return a / b;
    }

    /**
     * 取余
     */
    public double modulo(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("取余时除数不能为零");
        }
        return a % b;
    }

    /**
     * 幂运算
     */
    public double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    /**
     * 开方
     */
    public double sqrt(double value) {
        if (value < 0) {
            throw new ArithmeticException("负数不能开方");
        }
        return Math.sqrt(value);
    }

    /**
     * 正弦
     */
    public double sin(double radians) {
        return Math.sin(radians);
    }

    /**
     * 余弦
     */
    public double cos(double radians) {
        return Math.cos(radians);
    }

    /**
     * 正切
     */
    public double tan(double radians) {
        return Math.tan(radians);
    }

    /**
     * 常用对数（以10为底）
     */
    public double log(double value) {
        if (value <= 0) {
            throw new ArithmeticException("对数的参数必须大于零");
        }
        return Math.log10(value);
    }

    /**
     * 自然对数（以e为底）
     */
    public double ln(double value) {
        if (value <= 0) {
            throw new ArithmeticException("对数的参数必须大于零");
        }
        return Math.log(value);
    }

    /**
     * 弧度制转换：角度转弧度
     */
    public double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }
}
