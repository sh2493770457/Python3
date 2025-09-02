package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class UnicodeToolPanel extends JPanel {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JCheckBox uppercaseCheckBox;
    private JCheckBox prefixCheckBox;
    private JRadioButton encodeRadio;
    private JRadioButton decodeRadio;
    
    public UnicodeToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 创建选项面板
        JPanel optionsPanel = createOptionsPanel();
        mainPanel.add(optionsPanel, BorderLayout.NORTH);
        
        // 创建输入输出面板
        JPanel ioPanel = createIOPanel();
        mainPanel.add(ioPanel, BorderLayout.CENTER);
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("选项"));
        
        // 编码/解码选择
        encodeRadio = new JRadioButton("编码", true);
        decodeRadio = new JRadioButton("解码");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(encodeRadio);
        modeGroup.add(decodeRadio);
        
        // 选项复选框
        uppercaseCheckBox = new JCheckBox("大写输出", true);
        prefixCheckBox = new JCheckBox("添加\\u前缀", true);
        
        panel.add(encodeRadio);
        panel.add(decodeRadio);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(uppercaseCheckBox);
        panel.add(prefixCheckBox);
        
        return panel;
    }
    
    private JPanel createIOPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入"));
        inputArea = new JTextArea(8, 50);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("输出"));
        outputArea = new JTextArea(8, 50);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputPanel.add(outputScroll, BorderLayout.CENTER);
        
        panel.add(inputPanel);
        panel.add(outputPanel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton processButton = new JButton("处理");
        JButton clearButton = new JButton("清空");
        JButton swapButton = new JButton("交换");
        JButton copyButton = new JButton("复制结果");
        
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processText();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                outputArea.setText("");
            }
        });
        
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = inputArea.getText();
                inputArea.setText(outputArea.getText());
                outputArea.setText(temp);
            }
        });
        
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(outputArea.getText());
            }
        });
        
        panel.add(processButton);
        panel.add(clearButton);
        panel.add(swapButton);
        panel.add(copyButton);
        
        return panel;
    }
    
    private void processText() {
        String input = inputArea.getText();
        if (input.isEmpty()) {
            outputArea.setText("");
            return;
        }
        
        try {
            String result;
            if (encodeRadio.isSelected()) {
                result = encodeToUnicode(input);
            } else {
                result = decodeFromUnicode(input);
            }
            outputArea.setText(result);
        } catch (Exception e) {
            outputArea.setText("错误: " + e.getMessage());
        }
    }
    
    private String encodeToUnicode(String input) {
        StringBuilder result = new StringBuilder();
        boolean uppercase = uppercaseCheckBox.isSelected();
        boolean addPrefix = prefixCheckBox.isSelected();
        
        for (char c : input.toCharArray()) {
            if (c < 128) {
                // ASCII字符直接输出
                result.append(c);
            } else {
                // 非ASCII字符转换为Unicode
                String hex = Integer.toHexString(c);
                if (uppercase) {
                    hex = hex.toUpperCase();
                }
                
                if (addPrefix) {
                    result.append("\\u");
                }
                
                // 补齐4位
                while (hex.length() < 4) {
                    hex = "0" + hex;
                }
                result.append(hex);
            }
        }
        
        return result.toString();
    }
    
    private String decodeFromUnicode(String input) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        
        while (i < input.length()) {
            if (i < input.length() - 5 && input.substring(i, i + 2).equals("\\u")) {
                // 找到Unicode转义序列
                try {
                    String hex = input.substring(i + 2, i + 6);
                    int codePoint = Integer.parseInt(hex, 16);
                    result.append((char) codePoint);
                    i += 6;
                } catch (NumberFormatException e) {
                    // 不是有效的Unicode转义，直接添加字符
                    result.append(input.charAt(i));
                    i++;
                }
            } else if (i < input.length() - 3 && isHexDigit(input.charAt(i))) {
                // 尝试解析没有\\u前缀的4位十六进制
                try {
                    String hex = input.substring(i, i + 4);
                    if (hex.matches("[0-9a-fA-F]{4}")) {
                        int codePoint = Integer.parseInt(hex, 16);
                        if (codePoint > 127) { // 只转换非ASCII字符
                            result.append((char) codePoint);
                            i += 4;
                        } else {
                            result.append(input.charAt(i));
                            i++;
                        }
                    } else {
                        result.append(input.charAt(i));
                        i++;
                    }
                } catch (Exception e) {
                    result.append(input.charAt(i));
                    i++;
                }
            } else {
                result.append(input.charAt(i));
                i++;
            }
        }
        
        return result.toString();
    }
    
    private boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
    
    private void copyToClipboard(String text) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, null);
            JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "复制失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}