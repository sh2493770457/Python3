package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.SecretKeyFactory;

public class EncryptToolPanel extends JPanel {
    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> modeComboBox;
    private JComboBox<String> paddingComboBox;
    private JTextField keyField;
    private JTextField ivField;
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton generateKeyButton;
    private JCheckBox autoFillIvCheckBox;
    
    private static final String[] ALGORITHMS = {"DES", "3DES", "AES"};
    private static final String[] MODES = {"ECB", "CBC", "CFB", "OFB"};
    private static final String[] PADDINGS = {"PKCS5Padding", "NoPadding"};
    
    public EncryptToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        algorithmComboBox = new JComboBox<>(ALGORITHMS);
        modeComboBox = new JComboBox<>(MODES);
        paddingComboBox = new JComboBox<>(PADDINGS);
        
        keyField = new JTextField(20);
        ivField = new JTextField(20);
        
        inputArea = new JTextArea(8, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        
        outputArea = new JTextArea(8, 40);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        
        encryptButton = new JButton("加密");
        decryptButton = new JButton("解密");
        generateKeyButton = new JButton("生成密钥");
        
        autoFillIvCheckBox = new JCheckBox("自动填充IV", true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("加密配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("算法:"), gbc);
        gbc.gridx = 1;
        configPanel.add(algorithmComboBox, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("模式:"), gbc);
        gbc.gridx = 3;
        configPanel.add(modeComboBox, gbc);
        
        gbc.gridx = 4;
        configPanel.add(new JLabel("填充:"), gbc);
        gbc.gridx = 5;
        configPanel.add(paddingComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("密钥:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        configPanel.add(keyField, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        configPanel.add(generateKeyButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        configPanel.add(new JLabel("IV:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        configPanel.add(ivField, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        configPanel.add(autoFillIvCheckBox, gbc);
        
        // 输入输出面板
        JPanel ioPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入文本"));
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("输出结果"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        ioPanel.add(inputPanel);
        ioPanel.add(outputPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        
        add(configPanel, BorderLayout.NORTH);
        add(ioPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        algorithmComboBox.addActionListener(e -> updateUIBasedOnAlgorithm());
        modeComboBox.addActionListener(e -> updateUIBasedOnMode());
        
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performEncryption();
            }
        });
        
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDecryption();
            }
        });
        
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRandomKey();
            }
        });
        
        updateUIBasedOnAlgorithm();
        updateUIBasedOnMode();
    }
    
    private void updateUIBasedOnAlgorithm() {
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        // 根据算法更新UI状态
    }
    
    private void updateUIBasedOnMode() {
        String mode = (String) modeComboBox.getSelectedItem();
        boolean needsIV = !"ECB".equals(mode);
        ivField.setEnabled(needsIV);
        autoFillIvCheckBox.setEnabled(needsIV);
    }
    
    private void performEncryption() {
        try {
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            String mode = (String) modeComboBox.getSelectedItem();
            String padding = (String) paddingComboBox.getSelectedItem();
            String transformation = algorithm + "/" + mode + "/" + padding;
            
            String keyText = keyField.getText().trim();
            String ivText = ivField.getText().trim();
            String input = inputArea.getText();
            
            if (keyText.isEmpty() || input.isEmpty()) {
                outputArea.setText("错误: 密钥和输入文本不能为空");
                return;
            }
            
            SecretKey secretKey = createSecretKey(algorithm, keyText);
            Cipher cipher = Cipher.getInstance(transformation);
            
            if ("ECB".equals(mode)) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } else {
                IvParameterSpec ivSpec = createIvParameterSpec(algorithm, ivText);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            }
            
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            String result = Base64.getEncoder().encodeToString(encrypted);
            outputArea.setText(result);
            
        } catch (Exception e) {
            outputArea.setText("加密错误: " + e.getMessage());
        }
    }
    
    private void performDecryption() {
        try {
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            String mode = (String) modeComboBox.getSelectedItem();
            String padding = (String) paddingComboBox.getSelectedItem();
            String transformation = algorithm + "/" + mode + "/" + padding;
            
            String keyText = keyField.getText().trim();
            String ivText = ivField.getText().trim();
            String input = inputArea.getText().trim();
            
            if (keyText.isEmpty() || input.isEmpty()) {
                outputArea.setText("错误: 密钥和输入文本不能为空");
                return;
            }
            
            SecretKey secretKey = createSecretKey(algorithm, keyText);
            Cipher cipher = Cipher.getInstance(transformation);
            
            if ("ECB".equals(mode)) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            } else {
                IvParameterSpec ivSpec = createIvParameterSpec(algorithm, ivText);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            }
            
            byte[] encrypted = Base64.getDecoder().decode(input);
            byte[] decrypted = cipher.doFinal(encrypted);
            String result = new String(decrypted, StandardCharsets.UTF_8);
            outputArea.setText(result);
            
        } catch (Exception e) {
            outputArea.setText("解密错误: " + e.getMessage());
        }
    }
    
    private SecretKey createSecretKey(String algorithm, String keyText) throws Exception {
        byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);
        
        switch (algorithm) {
            case "DES":
                // DES密钥必须是8字节
                byte[] desKey = new byte[8];
                System.arraycopy(keyBytes, 0, desKey, 0, Math.min(keyBytes.length, 8));
                DESKeySpec desKeySpec = new DESKeySpec(desKey);
                SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
                return desFactory.generateSecret(desKeySpec);
                
            case "3DES":
                // 3DES密钥可以是16或24字节
                byte[] tripleDesKey = new byte[24];
                System.arraycopy(keyBytes, 0, tripleDesKey, 0, Math.min(keyBytes.length, 24));
                DESedeKeySpec tripleDesKeySpec = new DESedeKeySpec(tripleDesKey);
                SecretKeyFactory tripleDesFactory = SecretKeyFactory.getInstance("DESede");
                return tripleDesFactory.generateSecret(tripleDesKeySpec);
                
            case "AES":
                // AES密钥可以是16、24或32字节
                byte[] aesKey = new byte[16]; // 默认128位
                System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, 16));
                return new SecretKeySpec(aesKey, "AES");
                
            default:
                throw new IllegalArgumentException("不支持的算法: " + algorithm);
        }
    }
    
    private IvParameterSpec createIvParameterSpec(String algorithm, String ivText) {
        int ivLength = getIvLength(algorithm);
        byte[] iv = new byte[ivLength];
        
        if (ivText.isEmpty() && autoFillIvCheckBox.isSelected()) {
            // 自动生成IV
            new SecureRandom().nextBytes(iv);
            ivField.setText(Base64.getEncoder().encodeToString(iv));
        } else {
            byte[] ivBytes = ivText.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(ivBytes, 0, iv, 0, Math.min(ivBytes.length, ivLength));
        }
        
        return new IvParameterSpec(iv);
    }
    
    private int getIvLength(String algorithm) {
        switch (algorithm) {
            case "DES":
            case "3DES":
                return 8;
            case "AES":
                return 16;
            default:
                return 16;
        }
    }
    
    private void generateRandomKey() {
        try {
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            KeyGenerator keyGen;
            int keySize;
            
            switch (algorithm) {
                case "DES":
                    keyGen = KeyGenerator.getInstance("DES");
                    keySize = 56; // DES实际密钥长度
                    break;
                case "3DES":
                    keyGen = KeyGenerator.getInstance("DESede");
                    keySize = 168; // 3DES实际密钥长度
                    break;
                case "AES":
                    keyGen = KeyGenerator.getInstance("AES");
                    keySize = 128; // AES默认密钥长度
                    break;
                default:
                    throw new IllegalArgumentException("不支持的算法: " + algorithm);
            }
            
            keyGen.init(keySize);
            SecretKey secretKey = keyGen.generateKey();
            String keyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            keyField.setText(keyString);
            
        } catch (Exception e) {
            outputArea.setText("生成密钥错误: " + e.getMessage());
        }
    }
}