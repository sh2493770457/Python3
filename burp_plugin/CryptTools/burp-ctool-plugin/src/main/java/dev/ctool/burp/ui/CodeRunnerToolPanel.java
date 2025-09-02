package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringWriter;
import java.io.PrintWriter;

public class CodeRunnerToolPanel extends JPanel {
    private JTextArea codeArea;
    private JTextArea outputArea;
    private JComboBox<String> languageCombo;
    private JButton runButton;
    private JButton clearButton;
    private JButton clearOutputButton;
    
    public CodeRunnerToolPanel() {
        initComponents();
        layoutComponents();
        addEventListeners();
    }
    
    private void initComponents() {
        // 语言选择
        String[] languages = {"JavaScript", "Groovy", "Python (Jython)"};
        languageCombo = new JComboBox<>(languages);
        
        // 代码输入区域
        codeArea = new JTextArea(15, 50);
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.setText("// JavaScript示例\nconsole.log('Hello, World!');\n\n// 计算示例\nvar result = 1 + 2 * 3;\nconsole.log('1 + 2 * 3 = ' + result);\n\n// 数组操作\nvar arr = [1, 2, 3, 4, 5];\nvar sum = arr.reduce(function(a, b) { return a + b; }, 0);\nconsole.log('数组和: ' + sum);");
        
        // 输出区域
        outputArea = new JTextArea(10, 50);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        
        // 按钮
        runButton = new JButton("运行代码");
        runButton.setEnabled(false); // 暂时禁用运行功能
        clearButton = new JButton("清空代码");
        clearOutputButton = new JButton("清空输出");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // 顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("语言:"));
        topPanel.add(languageCombo);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(runButton);
        topPanel.add(clearButton);
        topPanel.add(clearOutputButton);
        
        // 代码输入面板
        JPanel codePanel = new JPanel(new BorderLayout());
        codePanel.setBorder(new TitledBorder("代码输入"));
        codePanel.add(new JScrollPane(codeArea), BorderLayout.CENTER);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("运行结果"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        // 主分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codePanel, outputPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.6);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void addEventListeners() {
        // 暂时注释掉运行按钮功能，因为相关功能有问题
        /*
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCode();
            }
        });
        */
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setText("");
            }
        });
        
        clearOutputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputArea.setText("");
            }
        });
        
        languageCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCodeTemplate();
            }
        });
    }
    
    private void updateCodeTemplate() {
        String language = (String) languageCombo.getSelectedItem();
        String template = "";
        
        switch (language) {
            case "JavaScript":
                template = "// JavaScript示例\nconsole.log('Hello, World!');\n\n// 计算示例\nvar result = 1 + 2 * 3;\nconsole.log('1 + 2 * 3 = ' + result);\n\n// 数组操作\nvar arr = [1, 2, 3, 4, 5];\nvar sum = arr.reduce(function(a, b) { return a + b; }, 0);\nconsole.log('数组和: ' + sum);";
                break;
            case "Groovy":
                template = "// Groovy示例\nprintln 'Hello, World!'\n\n// 计算示例\ndef result = 1 + 2 * 3\nprintln \"1 + 2 * 3 = $result\"\n\n// 列表操作\ndef list = [1, 2, 3, 4, 5]\ndef sum = list.sum()\nprintln \"列表和: $sum\"";
                break;
            case "Python (Jython)":
                template = "# Python示例\nprint('Hello, World!')\n\n# 计算示例\nresult = 1 + 2 * 3\nprint('1 + 2 * 3 =', result)\n\n# 列表操作\narr = [1, 2, 3, 4, 5]\nsum_val = sum(arr)\nprint('列表和:', sum_val)";
                break;
        }
        
        if (codeArea.getText().trim().isEmpty()) {
            codeArea.setText(template);
        }
    }
    
    // 暂时注释掉运行代码功能，因为相关功能有问题
    /*
    private void runCode() {
        String language = (String) languageCombo.getSelectedItem();
        String code = codeArea.getText();
        
        if (code.trim().isEmpty()) {
            outputArea.setText("错误: 请输入要执行的代码");
            return;
        }
        
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = null;
            
            switch (language) {
                case "JavaScript":
                    engine = manager.getEngineByName("JavaScript");
                    if (engine == null) {
                        engine = manager.getEngineByName("nashorn");
                    }
                    if (engine == null) {
                        engine = manager.getEngineByName("rhino");
                    }
                    break;
                case "Groovy":
                    engine = manager.getEngineByName("groovy");
                    break;
                case "Python (Jython)":
                    engine = manager.getEngineByName("python");
                    break;
            }
            
            if (engine == null) {
                outputArea.setText("错误: 不支持的脚本语言 " + language + "\n\n可用的脚本引擎:\n");
                StringBuilder sb = new StringBuilder();
                for (var factory : manager.getEngineFactories()) {
                    sb.append("- ").append(factory.getEngineName())
                      .append(" (").append(String.join(", ", factory.getNames())).append(")\n");
                }
                outputArea.append(sb.toString());
                return;
            }
            
            // 重定向输出
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            
            // 为JavaScript添加console.log支持
            if (language.equals("JavaScript")) {
                engine.put("out", printWriter);
                String consoleScript = "var console = { log: function(msg) { out.println(msg); } };";
                engine.eval(consoleScript);
            } else {
                engine.getContext().setWriter(printWriter);
            }
            
            // 执行代码
            Object result = engine.eval(code);
            
            // 获取输出
            String output = writer.toString();
            if (output.isEmpty() && result != null) {
                output = "返回值: " + result.toString();
            }
            
            if (output.isEmpty()) {
                output = "代码执行完成，无输出";
            }
            
            outputArea.setText(output);
            
        } catch (ScriptException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            outputArea.setText("脚本执行错误:\n" + e.getMessage() + "\n\n详细信息:\n" + sw.toString());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            outputArea.setText("执行错误:\n" + e.getMessage() + "\n\n详细信息:\n" + sw.toString());
        }
    }
    */
}