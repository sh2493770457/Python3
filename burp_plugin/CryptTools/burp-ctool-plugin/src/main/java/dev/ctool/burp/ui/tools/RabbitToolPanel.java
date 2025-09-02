package dev.ctool.burp.ui.tools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Rabbit流密码工具面板
 * 支持Rabbit加密解密功能
 */
public class RabbitToolPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextField keyField;
    private JTextField ivField;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearButton;
    private JButton generateKeyButton;
    private JComboBox<String> inputFormatCombo;
    private JComboBox<String> outputFormatCombo;
    private JCheckBox useIvCheckBox;
    
    private static final String[] FORMATS = {"文本", "Base64", "十六进制"};
    
    public RabbitToolPanel() {
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
        panel.setBorder(new TitledBorder("Rabbit配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("密钥(128位):"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        keyField = new JTextField(30);
        panel.add(keyField, gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        generateKeyButton = new JButton("生成密钥");
        panel.add(generateKeyButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        useIvCheckBox = new JCheckBox("使用IV(64位):", false);
        panel.add(useIvCheckBox, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        ivField = new JTextField(30);
        ivField.setEnabled(false);
        panel.add(ivField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
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
                performRabbitOperation(true);
            }
        });
        
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRabbitOperation(false);
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRandomKey();
            }
        });
        
        useIvCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ivField.setEnabled(useIvCheckBox.isSelected());
            }
        });
    }
    
    private void performRabbitOperation(boolean encrypt) {
        try {
            String key = keyField.getText().trim();
            String input = inputArea.getText().trim();
            
            if (key.isEmpty() || input.isEmpty()) {
                outputArea.setText("错误: 密钥和输入内容不能为空");
                return;
            }
            
            byte[] keyBytes = parseKey(key);
            byte[] ivBytes = null;
            
            if (useIvCheckBox.isSelected()) {
                String iv = ivField.getText().trim();
                if (!iv.isEmpty()) {
                    ivBytes = parseIV(iv);
                }
            }
            
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
            
            // 执行Rabbit加密/解密（Rabbit是对称的）
            byte[] result = rabbit(inputBytes, keyBytes, ivBytes);
            
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
     * Rabbit算法实现
     */
    private byte[] rabbit(byte[] data, byte[] key, byte[] iv) {
        RabbitCipher cipher = new RabbitCipher();
        cipher.setupKey(key);
        
        if (iv != null) {
            cipher.setupIV(iv);
        }
        
        return cipher.encrypt(data);
    }
    
    /**
     * Rabbit密码算法实现类
     */
    private static class RabbitCipher {
        private int[] x = new int[8];
        private int[] c = new int[8];
        private int carry = 0;
        
        private static final int[] A = {
            0x4D34D34D, 0xD34D34D3, 0x34D34D34, 0x4D34D34D,
            0xD34D34D3, 0x34D34D34, 0x4D34D34D, 0xD34D34D3
        };
        
        public void setupKey(byte[] key) {
            if (key.length != 16) {
                throw new IllegalArgumentException("Rabbit密钥必须是128位(16字节)");
            }
            
            int[] subkeys = new int[4];
            for (int i = 0; i < 4; i++) {
                subkeys[i] = bytesToInt(key, i * 4);
            }
            
            // 初始化状态变量
            x[0] = subkeys[0];
            x[2] = subkeys[1];
            x[4] = subkeys[2];
            x[6] = subkeys[3];
            x[1] = (subkeys[3] << 16) | (subkeys[2] >>> 16);
            x[3] = (subkeys[0] << 16) | (subkeys[3] >>> 16);
            x[5] = (subkeys[1] << 16) | (subkeys[0] >>> 16);
            x[7] = (subkeys[2] << 16) | (subkeys[1] >>> 16);
            
            // 初始化计数器
            for (int i = 0; i < 8; i++) {
                c[i] = A[i];
            }
            
            carry = 0;
            
            // 执行4轮迭代
            for (int i = 0; i < 4; i++) {
                nextState();
            }
            
            // 重新初始化计数器
            for (int i = 0; i < 8; i++) {
                c[i] ^= x[(i + 4) % 8];
            }
        }
        
        public void setupIV(byte[] iv) {
            if (iv.length != 8) {
                throw new IllegalArgumentException("Rabbit IV必须是64位(8字节)");
            }
            
            int i0 = bytesToInt(iv, 0);
            int i2 = bytesToInt(iv, 4);
            int i1 = (i0 >>> 16) | (i2 & 0xFFFF0000);
            int i3 = (i2 << 16) | (i0 & 0x0000FFFF);
            
            c[0] ^= i0;
            c[1] ^= i1;
            c[2] ^= i2;
            c[3] ^= i3;
            c[4] ^= i0;
            c[5] ^= i1;
            c[6] ^= i2;
            c[7] ^= i3;
        }
        
        public byte[] encrypt(byte[] data) {
            byte[] result = new byte[data.length];
            int pos = 0;
            
            while (pos < data.length) {
                nextState();
                
                int[] s = new int[4];
                s[0] = x[0] ^ (x[5] >>> 16) ^ (x[3] << 16);
                s[1] = x[2] ^ (x[7] >>> 16) ^ (x[5] << 16);
                s[2] = x[4] ^ (x[1] >>> 16) ^ (x[7] << 16);
                s[3] = x[6] ^ (x[3] >>> 16) ^ (x[1] << 16);
                
                for (int i = 0; i < 4 && pos < data.length; i++) {
                    for (int j = 0; j < 4 && pos < data.length; j++) {
                        result[pos] = (byte) (data[pos] ^ ((s[i] >>> (j * 8)) & 0xFF));
                        pos++;
                    }
                }
            }
            
            return result;
        }
        
        private void nextState() {
            int[] g = new int[8];
            
            // 计数器系统
            int oldCarry = carry;
            carry = 0;
            
            for (int i = 0; i < 8; i++) {
                long temp = (long) c[i] + A[i] + oldCarry;
                carry = (int) (temp >>> 32);
                c[i] = (int) temp;
                oldCarry = carry;
            }
            
            // 下一状态函数
            for (int i = 0; i < 8; i++) {
                long temp = (long) x[i] + c[i];
                temp = temp * temp;
                g[i] = (int) (temp ^ (temp >>> 32));
            }
            
            x[0] = g[0] + rotateLeft(g[7], 16) + rotateLeft(g[6], 16);
            x[1] = g[1] + rotateLeft(g[0], 8) + g[7];
            x[2] = g[2] + rotateLeft(g[1], 16) + rotateLeft(g[0], 16);
            x[3] = g[3] + rotateLeft(g[2], 8) + g[1];
            x[4] = g[4] + rotateLeft(g[3], 16) + rotateLeft(g[2], 16);
            x[5] = g[5] + rotateLeft(g[4], 8) + g[3];
            x[6] = g[6] + rotateLeft(g[5], 16) + rotateLeft(g[4], 16);
            x[7] = g[7] + rotateLeft(g[6], 8) + g[5];
        }
        
        private int rotateLeft(int value, int bits) {
            return (value << bits) | (value >>> (32 - bits));
        }
        
        private int bytesToInt(byte[] bytes, int offset) {
            return ((bytes[offset] & 0xFF)) |
                   ((bytes[offset + 1] & 0xFF) << 8) |
                   ((bytes[offset + 2] & 0xFF) << 16) |
                   ((bytes[offset + 3] & 0xFF) << 24);
        }
    }
    
    private byte[] parseKey(String key) {
        if (key.length() == 32) {
            // 十六进制格式
            return hexToBytes(key);
        } else {
            // 文本格式，截取或填充到16字节
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] result = new byte[16];
            System.arraycopy(keyBytes, 0, result, 0, Math.min(keyBytes.length, 16));
            return result;
        }
    }
    
    private byte[] parseIV(String iv) {
        if (iv.length() == 16) {
            // 十六进制格式
            return hexToBytes(iv);
        } else {
            // 文本格式，截取或填充到8字节
            byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
            byte[] result = new byte[8];
            System.arraycopy(ivBytes, 0, result, 0, Math.min(ivBytes.length, 8));
            return result;
        }
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
    
    private void generateRandomKey() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        keyField.setText(bytesToHex(key));
    }
    
    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
        keyField.setText("");
        ivField.setText("");
    }
}