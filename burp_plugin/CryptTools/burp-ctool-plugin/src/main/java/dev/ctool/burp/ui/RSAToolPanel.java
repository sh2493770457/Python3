package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 密钥生成面板组件
    private JComboBox<String> keySizeComboBox;
    private JTextArea publicKeyArea;
    private JTextArea privateKeyArea;
    private JButton generateKeyButton;
    
    // 加密解密面板组件
    private JTextArea encryptInputArea;
    private JTextArea encryptOutputArea;
    private JTextArea encryptKeyArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private JComboBox<String> paddingComboBox;
    
    // 签名验证面板组件
    private JTextArea signInputArea;
    private JTextArea signatureArea;
    private JTextArea signPrivateKeyArea;
    private JTextArea verifyPublicKeyArea;
    private JButton signButton;
    private JButton verifyButton;
    private JComboBox<String> signAlgorithmComboBox;
    
    private static final String[] KEY_SIZES = {"1024", "2048", "3072", "4096"};
    private static final String[] PADDINGS = {"PKCS1Padding", "OAEPPadding", "NoPadding"};
    private static final String[] SIGN_ALGORITHMS = {"SHA1withRSA", "SHA256withRSA", "SHA384withRSA", "SHA512withRSA"};
    
    public RSAToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // 密钥生成组件
        keySizeComboBox = new JComboBox<>(KEY_SIZES);
        keySizeComboBox.setSelectedItem("2048");
        publicKeyArea = new JTextArea(10, 50);
        publicKeyArea.setLineWrap(true);
        publicKeyArea.setWrapStyleWord(true);
        publicKeyArea.setEditable(false);
        privateKeyArea = new JTextArea(10, 50);
        privateKeyArea.setLineWrap(true);
        privateKeyArea.setWrapStyleWord(true);
        privateKeyArea.setEditable(false);
        generateKeyButton = new JButton("生成密钥对");
        
        // 加密解密组件
        encryptInputArea = new JTextArea(8, 40);
        encryptInputArea.setLineWrap(true);
        encryptInputArea.setWrapStyleWord(true);
        encryptOutputArea = new JTextArea(8, 40);
        encryptOutputArea.setLineWrap(true);
        encryptOutputArea.setWrapStyleWord(true);
        encryptOutputArea.setEditable(false);
        encryptKeyArea = new JTextArea(6, 40);
        encryptKeyArea.setLineWrap(true);
        encryptKeyArea.setWrapStyleWord(true);
        encryptButton = new JButton("加密");
        decryptButton = new JButton("解密");
        paddingComboBox = new JComboBox<>(PADDINGS);
        
        // 签名验证组件
        signInputArea = new JTextArea(6, 40);
        signInputArea.setLineWrap(true);
        signInputArea.setWrapStyleWord(true);
        signatureArea = new JTextArea(4, 40);
        signatureArea.setLineWrap(true);
        signatureArea.setWrapStyleWord(true);
        signPrivateKeyArea = new JTextArea(6, 40);
        signPrivateKeyArea.setLineWrap(true);
        signPrivateKeyArea.setWrapStyleWord(true);
        verifyPublicKeyArea = new JTextArea(6, 40);
        verifyPublicKeyArea.setLineWrap(true);
        verifyPublicKeyArea.setWrapStyleWord(true);
        signButton = new JButton("签名");
        verifyButton = new JButton("验证签名");
        signAlgorithmComboBox = new JComboBox<>(SIGN_ALGORITHMS);
        signAlgorithmComboBox.setSelectedItem("SHA256withRSA");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 密钥生成面板
        JPanel keyGenPanel = createKeyGenerationPanel();
        tabbedPane.addTab("密钥生成", keyGenPanel);
        
        // 加密解密面板
        JPanel encryptPanel = createEncryptionPanel();
        tabbedPane.addTab("加密解密", encryptPanel);
        
        // 签名验证面板
        JPanel signPanel = createSignaturePanel();
        tabbedPane.addTab("签名验证", signPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createKeyGenerationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new FlowLayout());
        configPanel.add(new JLabel("密钥长度:"));
        configPanel.add(keySizeComboBox);
        configPanel.add(generateKeyButton);
        
        // 密钥显示面板
        JPanel keyPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JPanel publicKeyPanel = new JPanel(new BorderLayout());
        publicKeyPanel.setBorder(new TitledBorder("公钥 (Public Key)"));
        publicKeyPanel.add(new JScrollPane(publicKeyArea), BorderLayout.CENTER);
        
        JPanel privateKeyPanel = new JPanel(new BorderLayout());
        privateKeyPanel.setBorder(new TitledBorder("私钥 (Private Key)"));
        privateKeyPanel.add(new JScrollPane(privateKeyArea), BorderLayout.CENTER);
        
        keyPanel.add(publicKeyPanel);
        keyPanel.add(privateKeyPanel);
        
        panel.add(configPanel, BorderLayout.NORTH);
        panel.add(keyPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEncryptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new FlowLayout());
        configPanel.add(new JLabel("填充模式:"));
        configPanel.add(paddingComboBox);
        
        // 密钥面板
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(new TitledBorder("密钥 (加密用公钥，解密用私钥)"));
        keyPanel.add(new JScrollPane(encryptKeyArea), BorderLayout.CENTER);
        
        // 输入输出面板
        JPanel ioPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入文本"));
        inputPanel.add(new JScrollPane(encryptInputArea), BorderLayout.CENTER);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("输出结果"));
        outputPanel.add(new JScrollPane(encryptOutputArea), BorderLayout.CENTER);
        
        ioPanel.add(inputPanel);
        ioPanel.add(outputPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        
        panel.add(configPanel, BorderLayout.NORTH);
        panel.add(keyPanel, BorderLayout.CENTER);
        panel.add(ioPanel, BorderLayout.SOUTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createSignaturePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new FlowLayout());
        configPanel.add(new JLabel("签名算法:"));
        configPanel.add(signAlgorithmComboBox);
        
        // 主要内容面板
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        
        // 输入文本面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("待签名/验证文本"));
        inputPanel.add(new JScrollPane(signInputArea), BorderLayout.CENTER);
        
        // 签名结果面板
        JPanel signaturePanel = new JPanel(new BorderLayout());
        signaturePanel.setBorder(new TitledBorder("签名结果"));
        signaturePanel.add(new JScrollPane(signatureArea), BorderLayout.CENTER);
        
        // 私钥面板
        JPanel privateKeyPanel = new JPanel(new BorderLayout());
        privateKeyPanel.setBorder(new TitledBorder("私钥 (用于签名)"));
        privateKeyPanel.add(new JScrollPane(signPrivateKeyArea), BorderLayout.CENTER);
        
        // 公钥面板
        JPanel publicKeyPanel = new JPanel(new BorderLayout());
        publicKeyPanel.setBorder(new TitledBorder("公钥 (用于验证)"));
        publicKeyPanel.add(new JScrollPane(verifyPublicKeyArea), BorderLayout.CENTER);
        
        contentPanel.add(inputPanel);
        contentPanel.add(signaturePanel);
        contentPanel.add(privateKeyPanel);
        contentPanel.add(publicKeyPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(signButton);
        buttonPanel.add(verifyButton);
        
        panel.add(configPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateKeyPair();
            }
        });
        
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRSAEncryption();
            }
        });
        
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRSADecryption();
            }
        });
        
        signButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSignature();
            }
        });
        
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSignatureVerification();
            }
        });
    }
    
    private void generateKeyPair() {
        try {
            int keySize = Integer.parseInt((String) keySizeComboBox.getSelectedItem());
            
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(keySize);
            KeyPair keyPair = keyGen.generateKeyPair();
            
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            
            publicKeyArea.setText(formatKey(publicKeyString, "PUBLIC KEY"));
            privateKeyArea.setText(formatKey(privateKeyString, "PRIVATE KEY"));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "生成密钥对失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String formatKey(String key, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ").append(type).append("-----\n");
        
        // 每64个字符换行
        for (int i = 0; i < key.length(); i += 64) {
            int end = Math.min(i + 64, key.length());
            sb.append(key.substring(i, end)).append("\n");
        }
        
        sb.append("-----END ").append(type).append("-----");
        return sb.toString();
    }
    
    private void performRSAEncryption() {
        try {
            String input = encryptInputArea.getText().trim();
            String keyText = encryptKeyArea.getText().trim();
            String padding = (String) paddingComboBox.getSelectedItem();
            
            if (input.isEmpty() || keyText.isEmpty()) {
                encryptOutputArea.setText("错误: 输入文本和密钥不能为空");
                return;
            }
            
            // 解析公钥
            PublicKey publicKey = parsePublicKey(keyText);
            
            // 执行加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/" + padding);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            String result = Base64.getEncoder().encodeToString(encrypted);
            encryptOutputArea.setText(result);
            
        } catch (Exception e) {
            encryptOutputArea.setText("加密错误: " + e.getMessage());
        }
    }
    
    private void performRSADecryption() {
        try {
            String input = encryptInputArea.getText().trim();
            String keyText = encryptKeyArea.getText().trim();
            String padding = (String) paddingComboBox.getSelectedItem();
            
            if (input.isEmpty() || keyText.isEmpty()) {
                encryptOutputArea.setText("错误: 输入文本和密钥不能为空");
                return;
            }
            
            // 解析私钥
            PrivateKey privateKey = parsePrivateKey(keyText);
            
            // 执行解密
            Cipher cipher = Cipher.getInstance("RSA/ECB/" + padding);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte[] encrypted = Base64.getDecoder().decode(input);
            byte[] decrypted = cipher.doFinal(encrypted);
            String result = new String(decrypted, StandardCharsets.UTF_8);
            encryptOutputArea.setText(result);
            
        } catch (Exception e) {
            encryptOutputArea.setText("解密错误: " + e.getMessage());
        }
    }
    
    private void performSignature() {
        try {
            String input = signInputArea.getText().trim();
            String keyText = signPrivateKeyArea.getText().trim();
            String algorithm = (String) signAlgorithmComboBox.getSelectedItem();
            
            if (input.isEmpty() || keyText.isEmpty()) {
                signatureArea.setText("错误: 输入文本和私钥不能为空");
                return;
            }
            
            // 解析私钥
            PrivateKey privateKey = parsePrivateKey(keyText);
            
            // 执行签名
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(input.getBytes(StandardCharsets.UTF_8));
            
            byte[] signatureBytes = signature.sign();
            String result = Base64.getEncoder().encodeToString(signatureBytes);
            signatureArea.setText(result);
            
        } catch (Exception e) {
            signatureArea.setText("签名错误: " + e.getMessage());
        }
    }
    
    private void performSignatureVerification() {
        try {
            String input = signInputArea.getText().trim();
            String signatureText = signatureArea.getText().trim();
            String keyText = verifyPublicKeyArea.getText().trim();
            String algorithm = (String) signAlgorithmComboBox.getSelectedItem();
            
            if (input.isEmpty() || signatureText.isEmpty() || keyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "输入文本、签名和公钥不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 解析公钥
            PublicKey publicKey = parsePublicKey(keyText);
            
            // 执行验证
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(input.getBytes(StandardCharsets.UTF_8));
            
            byte[] signatureBytes = Base64.getDecoder().decode(signatureText);
            boolean isValid = signature.verify(signatureBytes);
            
            String result = isValid ? "签名验证成功" : "签名验证失败";
            JOptionPane.showMessageDialog(this, result, "验证结果", 
                isValid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "验证错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private PublicKey parsePublicKey(String keyText) throws Exception {
        // 移除PEM格式的头尾标记
        String cleanKey = keyText.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                                 .replaceAll("-----END PUBLIC KEY-----", "")
                                 .replaceAll("\\s+", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    
    private PrivateKey parsePrivateKey(String keyText) throws Exception {
        // 移除PEM格式的头尾标记
        String cleanKey = keyText.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                                 .replaceAll("-----END PRIVATE KEY-----", "")
                                 .replaceAll("\\s+", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}