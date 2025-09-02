package dev.ctool.burp.ui.tools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * RC4流密码工具面板
 * 支持RC4加密解密功能
 */
public class RC4ToolPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextField keyField;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearButton;
    private JComboBox<String> inputFormatCombo;
    private JComboBox<String> outputFormatCombo;
    
    private static final String[] FORMATS = {"文本", "Base64", "十六进制"};
    
    public RC4ToolPanel() {
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建配置面板
        JPanel configPanel = createConfigPanel();
        
        // 创建输入输出面板
        JPanel ioPanel = createIOPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        add(configPanel, BorderLayout.NORTH);
        add(ioPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("RC4配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("密钥:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        keyField = new JTextField(30);
        panel.add(keyField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("输入格式:"), gbc);
        
        gbc.gridx = 1;
        inputFormatCombo = new JComboBox<>(FORMATS);
        panel.add(inputFormatCombo, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("输出格式:"), gbc);
        
        gbc.gridx = 3;
        outputFormatCombo = new JComboBox<>(FORMATS);
        outputFormatCombo.setSelectedIndex(1); // 默认Base64输出
        panel.add(outputFormatCombo, gbc);
        
        return panel;
    }
    
    private JPanel createIOPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入"));
        inputArea = new JTextArea(15, 30);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("输出"));
        outputArea = new JTextArea(15, 30);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        panel.add(inputPanel);
        panel.add(outputPanel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        encryptButton = new JButton("加密");
        decryptButton = new JButton("解密");
        clearButton = new JButton("清空");
        
        panel.add(encryptButton);
        panel.add(decryptButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRC4Operation(true);
            }
        });
        
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRC4Operation(false);
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }
    
    private void performRC4Operation(boolean encrypt) {
        try {
            String key = keyField.getText().trim();
            String input = inputArea.getText().trim();
            
            if (key.isEmpty() || input.isEmpty()) {
                outputArea.setText("错误: 密钥和输入内容不能为空");
                return;
            }
            
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] inputBytes;
            
            // 解析输入格式
            String inputFormat = (String) inputFormatCombo.getSelectedItem();
            switch (inputFormat) {
                case "文本":
                    inputBytes = input.getBytes(StandardCharsets.UTF_8);
                    break;
                case "Base64":
                    inputBytes = Base64.getDecoder().decode(input);
                    break;
                case "十六进制":
                    inputBytes = hexToBytes(input);
                    break;
                default:
                    inputBytes = input.getBytes(StandardCharsets.UTF_8);
            }
            
            // 执行RC4加密/解密（RC4是对称的）
            byte[] result = rc4(inputBytes, keyBytes);
            
            // 格式化输出
            String output;
            String outputFormat = (String) outputFormatCombo.getSelectedItem();
            switch (outputFormat) {
                case "文本":
                    output = new String(result, StandardCharsets.UTF_8);
                    break;
                case "Base64":
                    output = Base64.getEncoder().encodeToString(result);
                    break;
                case "十六进制":
                    output = bytesToHex(result);
                    break;
                default:
                    output = Base64.getEncoder().encodeToString(result);
            }
            
            outputArea.setText(output);
            
        } catch (Exception e) {
            outputArea.setText("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * RC4算法实现
     */
    private byte[] rc4(byte[] data, byte[] key) {
        int[] s = new int[256];
        int[] k = new int[256];
        
        // 初始化S盒和K盒
        for (int i = 0; i < 256; i++) {
            s[i] = i;
            k[i] = key[i % key.length] & 0xFF;
        }
        
        // 初始置换
        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + s[i] + k[i]) % 256;
            int temp = s[i];
            s[i] = s[j];
            s[j] = temp;
        }
        
        // 生成密钥流并加密
        byte[] result = new byte[data.length];
        int i = 0;
        j = 0;
        
        for (int n = 0; n < data.length; n++) {
            i = (i + 1) % 256;
            j = (j + s[i]) % 256;
            
            int temp = s[i];
            s[i] = s[j];
            s[j] = temp;
            
            int keyByte = s[(s[i] + s[j]) % 256];
            result[n] = (byte) ((data[n] & 0xFF) ^ keyByte);
        }
        
        return result;
    }
    
    private byte[] hexToBytes(String hex) {
        hex = hex.replaceAll("\\s+", "").toUpperCase();
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("十六进制字符串长度必须为偶数");
        }
        
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
    
    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
        keyField.setText("");
    }
}