package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class HMACToolPanel extends JPanel {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    
    private JTextArea inputArea;
    private JTextArea keyArea;
    private JTextArea outputArea;
    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> inputFormatComboBox;
    private JComboBox<String> keyFormatComboBox;
    private JCheckBox uppercaseCheckBox;
    
    public HMACToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
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
        
        // 算法选择
        String[] algorithms = {"HmacMD5", "HmacSHA1", "HmacSHA256", "HmacSHA384", "HmacSHA512"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setSelectedItem("HmacSHA256");
        
        // 输入格式
        String[] formats = {"文本", "十六进制", "Base64"};
        inputFormatComboBox = new JComboBox<>(formats);
        keyFormatComboBox = new JComboBox<>(formats);
        
        // 选项复选框
        uppercaseCheckBox = new JCheckBox("大写输出", false);
        
        panel.add(new JLabel("算法:"));
        panel.add(algorithmComboBox);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JLabel("输入格式:"));
        panel.add(inputFormatComboBox);
        panel.add(new JLabel("密钥格式:"));
        panel.add(keyFormatComboBox);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(uppercaseCheckBox);
        
        return panel;
    }
    
    private JPanel createIOPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入数据"));
        inputArea = new JTextArea(5, 50);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        
        // 密钥面板
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(BorderFactory.createTitledBorder("密钥"));
        keyArea = new JTextArea(3, 50);
        keyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        keyArea.setLineWrap(true);
        keyArea.setWrapStyleWord(true);
        JScrollPane keyScroll = new JScrollPane(keyArea);
        keyPanel.add(keyScroll, BorderLayout.CENTER);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("HMAC结果"));
        outputArea = new JTextArea(5, 50);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputPanel.add(outputScroll, BorderLayout.CENTER);
        
        panel.add(inputPanel);
        panel.add(keyPanel);
        panel.add(outputPanel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton calculateButton = new JButton("计算HMAC");
        JButton clearButton = new JButton("清空");
        JButton copyButton = new JButton("复制结果");
        JButton verifyButton = new JButton("验证HMAC");
        JButton generateKeyButton = new JButton("生成随机密钥");
        
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateHMAC();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                keyArea.setText("");
                outputArea.setText("");
            }
        });
        
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(outputArea.getText());
            }
        });
        
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyHMAC();
            }
        });
        
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRandomKey();
            }
        });
        
        panel.add(calculateButton);
        panel.add(verifyButton);
        panel.add(generateKeyButton);
        panel.add(clearButton);
        panel.add(copyButton);
        
        return panel;
    }
    
    private void calculateHMAC() {
        String input = inputArea.getText();
        String key = keyArea.getText();
        
        if (input.isEmpty() || key.isEmpty()) {
            outputArea.setText("请输入数据和密钥");
            return;
        }
        
        try {
            // 解析输入数据
            byte[] inputBytes = parseInput(input, (String) inputFormatComboBox.getSelectedItem());
            
            // 解析密钥
            byte[] keyBytes = parseInput(key, (String) keyFormatComboBox.getSelectedItem());
            
            // 计算HMAC
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, algorithm);
            mac.init(secretKey);
            
            byte[] hmacBytes = mac.doFinal(inputBytes);
            
            // 格式化输出
            String result = formatOutput(hmacBytes);
            outputArea.setText(result);
            
        } catch (Exception e) {
            outputArea.setText("错误: " + e.getMessage());
        }
    }
    
    private void verifyHMAC() {
        String expectedHmac = JOptionPane.showInputDialog(this, "请输入期望的HMAC值:", "验证HMAC", JOptionPane.QUESTION_MESSAGE);
        if (expectedHmac == null || expectedHmac.trim().isEmpty()) {
            return;
        }
        
        calculateHMAC();
        String actualHmac = outputArea.getText();
        
        if (actualHmac.startsWith("错误:")) {
            JOptionPane.showMessageDialog(this, "计算HMAC时出错，无法验证", "验证失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean matches = actualHmac.equalsIgnoreCase(expectedHmac.trim());
        String message = matches ? "HMAC验证成功！" : "HMAC验证失败！";
        int messageType = matches ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
        
        JOptionPane.showMessageDialog(this, message, "验证结果", messageType);
    }
    
    private void generateRandomKey() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] keyBytes = new byte[32]; // 256位密钥
            random.nextBytes(keyBytes);
            
            String keyFormat = (String) keyFormatComboBox.getSelectedItem();
            String key;
            
            switch (keyFormat) {
                case "十六进制":
                    StringBuilder hex = new StringBuilder();
                    for (byte b : keyBytes) {
                        hex.append(String.format("%02x", b & 0xFF));
                    }
                    key = hex.toString();
                    break;
                case "Base64":
                    key = Base64.getEncoder().encodeToString(keyBytes);
                    break;
                default: // 文本
                    key = Base64.getEncoder().encodeToString(keyBytes);
                    break;
            }
            
            keyArea.setText(key);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "生成密钥失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private byte[] parseInput(String input, String format) throws Exception {
        switch (format) {
            case "十六进制":
                String cleanHex = input.replaceAll("\\s+", "").replaceAll("0x", "");
                if (cleanHex.length() % 2 != 0) {
                    throw new IllegalArgumentException("十六进制字符串长度必须为偶数");
                }
                byte[] hexBytes = new byte[cleanHex.length() / 2];
                for (int i = 0; i < cleanHex.length(); i += 2) {
                    hexBytes[i / 2] = (byte) Integer.parseInt(cleanHex.substring(i, i + 2), 16);
                }
                return hexBytes;
            case "Base64":
                return Base64.getDecoder().decode(input);
            default: // 文本
                return input.getBytes("UTF-8");
        }
    }
    
    private String formatOutput(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02x", b & 0xFF);
            if (uppercaseCheckBox.isSelected()) {
                hex = hex.toUpperCase();
            }
            result.append(hex);
        }
        return result.toString();
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