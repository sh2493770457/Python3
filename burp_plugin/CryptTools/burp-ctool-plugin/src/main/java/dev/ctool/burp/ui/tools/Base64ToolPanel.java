package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64编码解码工具面板
 */
public class Base64ToolPanel extends JPanel {
    
    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JRadioButton encodeRadio;
    private JRadioButton decodeRadio;
    private JCheckBox urlSafeCheckBox;
    
    public Base64ToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
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
        panel.setBorder(new TitledBorder("输入内容"));
        
        inputArea = new JTextArea(8, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("选项"));
        
        // 操作类型
        encodeRadio = new JRadioButton("编码", true);
        decodeRadio = new JRadioButton("解码", false);
        ButtonGroup operationGroup = new ButtonGroup();
        operationGroup.add(encodeRadio);
        operationGroup.add(decodeRadio);
        
        // URL安全选项
        urlSafeCheckBox = new JCheckBox("URL安全编码", false);
        
        panel.add(new JLabel("操作:"));
        panel.add(encodeRadio);
        panel.add(decodeRadio);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(urlSafeCheckBox);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输出结果"));
        
        outputArea = new JTextArea(8, 50);
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
            outputArea.setText(input);
            
            // 切换操作类型
            if (encodeRadio.isSelected()) {
                decodeRadio.setSelected(true);
            } else {
                encodeRadio.setSelected(true);
            }
        });
        
        panel.add(processButton);
        panel.add(clearButton);
        panel.add(swapButton);
        
        return panel;
    }
    
    private class ProcessActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputArea.getText();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(Base64ToolPanel.this, "请输入要处理的内容", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                String result;
                
                if (encodeRadio.isSelected()) {
                    // Base64编码
                    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                    if (urlSafeCheckBox.isSelected()) {
                        result = Base64.getUrlEncoder().encodeToString(inputBytes);
                    } else {
                        result = Base64.getEncoder().encodeToString(inputBytes);
                    }
                } else {
                    // Base64解码
                    byte[] decodedBytes;
                    if (urlSafeCheckBox.isSelected()) {
                        decodedBytes = Base64.getUrlDecoder().decode(input);
                    } else {
                        decodedBytes = Base64.getDecoder().decode(input);
                    }
                    result = new String(decodedBytes, StandardCharsets.UTF_8);
                }
                
                outputArea.setText(result);
                
            } catch (Exception ex) {
                String operation = encodeRadio.isSelected() ? "编码" : "解码";
                JOptionPane.showMessageDialog(Base64ToolPanel.this, 
                    "Base64" + operation + "时发生错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
                outputArea.setText("错误: " + ex.getMessage());
            }
        }
    }
}