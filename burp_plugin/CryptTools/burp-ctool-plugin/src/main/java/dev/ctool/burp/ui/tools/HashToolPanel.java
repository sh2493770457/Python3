package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import dev.ctool.burp.utils.CryptoUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希工具面板 - 支持MD5、SHA1、SHA256、SHA512等哈希算法
 */
public class HashToolPanel extends JPanel {
    
    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    
    private JTextArea inputArea;
    private JTextArea md5Result;
    private JTextArea sha1Result;
    private JTextArea sha256Result;
    private JTextArea sha512Result;
    private JCheckBox uppercaseCheckBox;
    private JCheckBox saltCheckBox;
    private JTextField saltField;
    
    public HashToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
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
        
        // 创建结果面板
        JPanel resultPanel = createResultPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(optionPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输入内容"));
        
        inputArea = new JTextArea(6, 50);
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
        
        uppercaseCheckBox = new JCheckBox("大写输出", false);
        saltCheckBox = new JCheckBox("使用盐值", false);
        saltField = new JTextField(20);
        saltField.setEnabled(false);
        
        // 盐值选项监听
        saltCheckBox.addActionListener(e -> {
            saltField.setEnabled(saltCheckBox.isSelected());
            if (!saltCheckBox.isSelected()) {
                saltField.setText("");
            }
        });
        
        panel.add(uppercaseCheckBox);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(saltCheckBox);
        panel.add(new JLabel("盐值:"));
        panel.add(saltField);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(new TitledBorder("哈希结果"));
        
        // MD5结果
        JPanel md5Panel = createResultItemPanel("MD5:");
        md5Result = new JTextArea(2, 50);
        md5Result.setEditable(false);
        md5Result.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        md5Result.setBackground(getBackground());
        md5Panel.add(new JScrollPane(md5Result), BorderLayout.CENTER);
        md5Panel.add(createCopyButton(md5Result), BorderLayout.EAST);
        
        // SHA1结果
        JPanel sha1Panel = createResultItemPanel("SHA1:");
        sha1Result = new JTextArea(2, 50);
        sha1Result.setEditable(false);
        sha1Result.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sha1Result.setBackground(getBackground());
        sha1Panel.add(new JScrollPane(sha1Result), BorderLayout.CENTER);
        sha1Panel.add(createCopyButton(sha1Result), BorderLayout.EAST);
        
        // SHA256结果
        JPanel sha256Panel = createResultItemPanel("SHA256:");
        sha256Result = new JTextArea(2, 50);
        sha256Result.setEditable(false);
        sha256Result.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sha256Result.setBackground(getBackground());
        sha256Panel.add(new JScrollPane(sha256Result), BorderLayout.CENTER);
        sha256Panel.add(createCopyButton(sha256Result), BorderLayout.EAST);
        
        // SHA512结果
        JPanel sha512Panel = createResultItemPanel("SHA512:");
        sha512Result = new JTextArea(3, 50);
        sha512Result.setEditable(false);
        sha512Result.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sha512Result.setBackground(getBackground());
        sha512Panel.add(new JScrollPane(sha512Result), BorderLayout.CENTER);
        sha512Panel.add(createCopyButton(sha512Result), BorderLayout.EAST);
        
        panel.add(md5Panel);
        panel.add(sha1Panel);
        panel.add(sha256Panel);
        panel.add(sha512Panel);
        
        return panel;
    }
    
    private JPanel createResultItemPanel(String label) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setPreferredSize(new Dimension(80, 0));
        panel.add(labelComponent, BorderLayout.WEST);
        
        return panel;
    }
    
    private JButton createCopyButton(JTextArea textArea) {
        JButton copyButton = new JButton("复制");
        copyButton.setPreferredSize(new Dimension(60, 25));
        copyButton.addActionListener(e -> {
            if (!textArea.getText().trim().isEmpty()) {
                textArea.selectAll();
                textArea.copy();
                JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return copyButton;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton calculateButton = new JButton("计算哈希");
        calculateButton.addActionListener(new CalculateActionListener());
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> {
            inputArea.setText("");
            md5Result.setText("");
            sha1Result.setText("");
            sha256Result.setText("");
            sha512Result.setText("");
        });
        
        panel.add(calculateButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private class CalculateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputArea.getText();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(HashToolPanel.this, "请输入要计算哈希的内容", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // 处理盐值
                String dataToHash = input;
                if (saltCheckBox.isSelected() && !saltField.getText().trim().isEmpty()) {
                    dataToHash = input + saltField.getText().trim();
                }
                
                byte[] data = dataToHash.getBytes(StandardCharsets.UTF_8);
                
                // 计算各种哈希
                String md5Hash = CryptoUtils.calculateHash(data, "MD5");
                String sha1Hash = CryptoUtils.calculateHash(data, "SHA-1");
                String sha256Hash = CryptoUtils.calculateHash(data, "SHA-256");
                String sha512Hash = CryptoUtils.calculateHash(data, "SHA-512");
                
                // 根据选项决定大小写
                if (uppercaseCheckBox.isSelected()) {
                    md5Hash = md5Hash.toUpperCase();
                    sha1Hash = sha1Hash.toUpperCase();
                    sha256Hash = sha256Hash.toUpperCase();
                    sha512Hash = sha512Hash.toUpperCase();
                }
                
                // 显示结果
                md5Result.setText(md5Hash);
                sha1Result.setText(sha1Hash);
                sha256Result.setText(sha256Hash);
                sha512Result.setText(sha512Hash);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(HashToolPanel.this, 
                    "计算哈希时发生错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}