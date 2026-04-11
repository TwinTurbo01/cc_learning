# 科学计算器 Calculator-App

## 项目简介

一个基于 Java Swing 的可视化科学计算器，采用经典计算器布局（深色主题），支持点击按钮和键盘快捷键两种交互方式。内置表达式解析器（调度场算法），支持中缀表达式输入，实时预览计算结果。

## 已实现功能

### 基本运算
| 功能 | 按钮 | 键盘 |
|------|------|------|
| 加法 | `+` | `+` |
| 减法 | `−` | `-` |
| 乘法 | `×` | `*` |
| 除法 | `÷` | `/` |
| 取余 | `mod` | `%` |
| 幂运算 | — | `^` |

### 科学计算
| 功能 | 按钮 | 键盘 |
|------|------|------|
| 平方 | `x²` | — |
| 开方 | `√` | `R` |
| 正弦 | `sin` | `S` |
| 余弦 | `cos` | `C` |
| 正切 | `tan` | `T` |
| 常用对数 | `log` | `L` |
| 自然对数 | `ln` | `N` |
| 圆周率 | `π` | `P` |
| 自然常数 | `e` | — |
| 百分比 | `%` | `%` |
| 正负切换 | `+/-` | — |

### 其他功能
- **DEG/RAD 模式切换**：左上角点击或 DEG 按钮切换，影响三角函数参数单位
- **括号运算**：支持任意嵌套
- **实时预览**：输入表达式时即时显示结果
- **从结果继续**：计算结果上直接追加运算符继续计算
- **错误处理**：除以零、负数开方等错误友好显示

## 快捷键

| 按键 | 功能 | 按键 | 功能 |
|------|------|------|------|
| `0` - `9` | 输入数字 | `.` | 小数点 |
| `+` `-` `*` `/` | 四则运算 | `Enter` | 求值（=） |
| `^` | 幂运算 | `%` | 百分比 |
| `(` `)` | 括号 | `Esc` | 清除全部 |
| `Backspace` | 回退 | — | — |
| `S` | sin | `C` | cos |
| `T` | tan | `L` | log |
| `N` | ln | `P` | π |
| `R` | √ | — | — |

## 文件结构

```
calculator-app/
├── README.md                                    # 项目说明文档
└── src/
    └── main/
        └── java/
            └── com/
                └── calculator/
                    ├── Calculator.java          # 程序入口（启动 GUI）
                    ├── engine/
                    │   └── CalculatorEngine.java # 计算引擎（所有数学运算）
                    ├── parser/
                    │   └── ExpressionEvaluator.java # 中缀表达式求值（调度场算法）
                    └── ui/
                        ├── CalculatorGUI.java    # Swing 图形界面
                        └── ButtonAction.java     # 按钮动作枚举
```

### 模块说明

- **Calculator.java** — 主入口，在 EDT 中启动 GUI 窗口
- **CalculatorEngine.java** — 纯计算逻辑，封装所有数学运算
- **ExpressionEvaluator.java** — 词法分析 → 调度场算法转 RPN → 栈式求值，复用 CalculatorEngine
- **CalculatorGUI.java** — Swing 窗口、深色主题布局、按钮网格、键盘绑定、状态机
- **ButtonAction.java** — 定义所有按钮的动作类型

## 运行方式

### 运行环境

- **操作系统**：Windows / Linux / macOS
- **Java 版本**：JDK 8 或以上
- **GUI 依赖**：Java Swing（JDK 内置，无需额外安装）

### 一键编译并运行

```bash
cd calculator-app
javac -encoding UTF-8 -d out src/main/java/com/calculator/*.java src/main/java/com/calculator/engine/*.java src/main/java/com/calculator/parser/*.java src/main/java/com/calculator/ui/*.java && java -cp out com.calculator.Calculator
```

### 分步操作

**编译**

```bash
cd calculator-app
javac -encoding UTF-8 -d out src/main/java/com/calculator/*.java src/main/java/com/calculator/engine/*.java src/main/java/com/calculator/parser/*.java src/main/java/com/calculator/ui/*.java
```

### 运行

```bash
java -cp out com.calculator.Calculator
```

## 界面说明

```
┌─────────────────────────────────┐
│ [DEG]                           │ ← 模式指示（可点击切换）
│ sin(30) + 5                     │ ← 表达式行
│ 10                              │ ← 结果/预览行
├─────┬─────┬─────┬─────┬─────┤
│ sin │ cos │ tan │ log │ ln  │ ← 科学函数
│ x²  │  √  │  π  │  e  │ mod │ ← 扩展函数
│  (  │  )  │  %  │  C  │  ⌫  │ ← 括号/清除/回退
│  7  │  8  │  9  │  ÷  │ +/- │ ← 数字
│  4  │  5  │  6  │  ×  │ DEG │ ← 数字/运算符
│  1  │  2  │  3  │  −  │     │ ← 数字
│  0  │  .  │  =  │  +  │     │ ← 数字/等于
└─────┴─────┴─────┴─────┴─────┘
```
