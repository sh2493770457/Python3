package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import com.google.gson.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * JSON工具面板
 */
public class JsonToolPanel extends JPanel {
    
    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JRadioButton formatRadio;
    private JRadioButton compressRadio;
    private JRadioButton validateRadio;
    private JSpinner indentSpinner;
    
    public JsonToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建选项面板
        JPanel optionPanel = createOptionPanel();
        
        // 创建输出面板
        JPanel outputPanel = createOutputPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(optionPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("JSON输入"));
        
        inputArea = new JTextArea(10, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("操作选项"));
        
        // 操作类型
        formatRadio = new JRadioButton("格式化", true);
        compressRadio = new JRadioButton("压缩", false);
        validateRadio = new JRadioButton("验证", false);
        
        ButtonGroup operationGroup = new ButtonGroup();
        operationGroup.add(formatRadio);
        operationGroup.add(compressRadio);
        operationGroup.add(validateRadio);
        
        // 缩进设置
        JLabel indentLabel = new JLabel("缩进空格数:");
        indentSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 8, 1));
        indentSpinner.setPreferredSize(new Dimension(60, 25));
        
        panel.add(new JLabel("操作:"));
        panel.add(formatRadio);
        panel.add(compressRadio);
        panel.add(validateRadio);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(indentLabel);
        panel.add(indentSpinner);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输出结果"));
        
        outputArea = new JTextArea(10, 50);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 添加复制按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyButton = new JButton("复制结果");
        copyButton.addActionListener(e -> {
            if (!outputArea.getText().trim().isEmpty()) {
                outputArea.selectAll();
                outputArea.copy();
                JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(copyButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton processButton = new JButton("执行");
        processButton.addActionListener(new ProcessActionListener());
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });
        
        JButton swapButton = new JButton("交换输入输出");
        swapButton.addActionListener(e -> {
            String input = inputArea.getText();
            String output = outputArea.getText();
            inputArea.setText(output);
            outputArea.setText("");
        });
        
        panel.add(processButton);
        panel.add(clearButton);
        panel.add(swapButton);
        
        return panel;
    }
    
    private class ProcessActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputArea.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(JsonToolPanel.this, "请输入JSON内容", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                
                if (validateRadio.isSelected()) {
                    // JSON验证
                    try {
                        JsonElement element = parser.parse(input);
                        outputArea.setText("✓ JSON格式正确\n\n" + 
                                          "类型: " + getJsonType(element) + "\n" +
                                          "大小: " + input.length() + " 字符");
                    } catch (JsonSyntaxException ex) {
                        outputArea.setText("✗ JSON格式错误\n\n" + 
                                          "错误信息: " + ex.getMessage());
                    }
                } else {
                    // 解析JSON
                    JsonElement element = parser.parse(input);
                    
                    if (formatRadio.isSelected()) {
                        // 格式化JSON
                        int indent = (Integer) indentSpinner.getValue();
                        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
                        
                        // 设置缩进
                        String indentString = " ".repeat(indent);
                        Gson prettyGson = builder.create();
                        String formatted = prettyGson.toJson(element);
                        
                        // 如果缩进不是2个空格，需要手动调整
                        if (indent != 2) {
                            formatted = adjustIndentation(formatted, indent);
                        }
                        
                        outputArea.setText(formatted);
                    } else if (compressRadio.isSelected()) {
                        // 压缩JSON
                        String compressed = gson.toJson(element);
                        outputArea.setText(compressed);
                    }
                }
                
            } catch (JsonSyntaxException ex) {
                JOptionPane.showMessageDialog(JsonToolPanel.this, 
                    "JSON解析错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
                outputArea.setText("JSON解析错误: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(JsonToolPanel.this, 
                    "处理JSON时发生错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
                outputArea.setText("错误: " + ex.getMessage());
            }
        }
    }
    
    private String getJsonType(JsonElement element) {
        if (element.isJsonObject()) {
            return "对象 (Object)";
        } else if (element.isJsonArray()) {
            return "数组 (Array)";
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return "字符串 (String)";
            } else if (primitive.isNumber()) {
                return "数字 (Number)";
            } else if (primitive.isBoolean()) {
                return "布尔值 (Boolean)";
            }
        } else if (element.isJsonNull()) {
            return "空值 (Null)";
        }
        return "未知类型";
    }
    
    private String adjustIndentation(String json, int indent) {
        String[] lines = json.split("\n");
        StringBuilder result = new StringBuilder();
        String indentString = " ".repeat(indent);
        
        for (String line : lines) {
            // 计算原始缩进级别
            int originalIndent = 0;
            for (char c : line.toCharArray()) {
                if (c == ' ') {
                    originalIndent++;
                } else {
                    break;
                }
            }
            
            // 计算新的缩进级别
            int level = originalIndent / 2;
            String newIndent = indentString.repeat(level);
            String content = line.trim();
            
            if (!content.isEmpty()) {
                result.append(newIndent).append(content).append("\n");
            } else {
                result.append("\n");
            }
        }
        
        return result.toString().trim();
    }
}