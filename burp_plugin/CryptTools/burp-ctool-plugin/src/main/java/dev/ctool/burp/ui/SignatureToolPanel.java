package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 签名验签工具面板
 * 支持数字签名和验证
 */
public class SignatureToolPanel extends JPanel {
    
    private JTextArea dataArea;
    private JTextArea keyArea;
    private JTextArea signatureArea;
    private JTextArea resultArea;
    private JComboBox<String> algorithmCombo;
    private JComboBox<String> operationCombo;
    private JButton executeButton;
    private JButton generateKeyButton;
    private JButton clearButton;
    private JCheckBox base64CheckBox;
    
    // 签名算法
    private static final String[] ALGORITHMS = {
        "RSA-SHA256",
        "RSA-SHA1",
        "RSA-MD5",
        "DSA-SHA1",
        "ECDSA-SHA256",
        "HMAC-SHA256",
        "HMAC-SHA1",
        "HMAC-MD5"
    };
    
    // 操作类型
    private static final String[] OPERATIONS = {
        "签名",
        "验签",
        "生成密钥对"
    };
    
    public SignatureToolPanel() {
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建选项面板
        JPanel optionPanel = createOptionPanel();
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建输出面板
        JPanel outputPanel = createOutputPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(optionPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("签名选项"));
        
        panel.add(new JLabel("算法:"));
        algorithmCombo = new JComboBox<>(ALGORITHMS);
        algorithmCombo.setSelectedIndex(0); // 默认RSA-SHA256
        panel.add(algorithmCombo);
        
        panel.add(Box.createHorizontalStrut(20));
        
        panel.add(new JLabel("操作:"));
        operationCombo = new JComboBox<>(OPERATIONS);
        operationCombo.setSelectedIndex(0); // 默认签名
        panel.add(operationCombo);
        
        panel.add(Box.createHorizontalStrut(20));
        
        base64CheckBox = new JCheckBox("Base64编码", true);
        panel.add(base64CheckBox);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 5, 0));
        
        // 数据输入
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(new TitledBorder("待签名数据"));
        dataArea = new JTextArea(8, 20);
        dataArea.setLineWrap(true);
        dataArea.setWrapStyleWord(true);
        dataArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        dataArea.setText("Hello, World!");
        dataPanel.add(new JScrollPane(dataArea), BorderLayout.CENTER);
        
        // 密钥输入
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(new TitledBorder("密钥"));
        keyArea = new JTextArea(8, 20);
        keyArea.setLineWrap(true);
        keyArea.setWrapStyleWord(true);
        keyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        keyPanel.add(new JScrollPane(keyArea), BorderLayout.CENTER);
        
        // 签名输入（用于验签）
        JPanel signaturePanel = new JPanel(new BorderLayout());
        signaturePanel.setBorder(new TitledBorder("签名值（验签时使用）"));
        signatureArea = new JTextArea(8, 20);
        signatureArea.setLineWrap(true);
        signatureArea.setWrapStyleWord(true);
        signatureArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        signaturePanel.add(new JScrollPane(signatureArea), BorderLayout.CENTER);
        
        panel.add(dataPanel);
        panel.add(keyPanel);
        panel.add(signaturePanel);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("结果"));
        
        resultArea = new JTextArea(8, 50);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        executeButton = new JButton("执行");
        generateKeyButton = new JButton("生成密钥对");
        clearButton = new JButton("清空");
        
        executeButton.setPreferredSize(new Dimension(100, 30));
        generateKeyButton.setPreferredSize(new Dimension(120, 30));
        clearButton.setPreferredSize(new Dimension(100, 30));
        
        panel.add(executeButton);
        panel.add(generateKeyButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeOperation();
            }
        });
        
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateKeyPair();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        operationCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateButtonText();
            }
        });
        
        updateButtonText();
    }
    
    private void updateButtonText() {
        int operation = operationCombo.getSelectedIndex();
        switch (operation) {
            case 0:
                executeButton.setText("签名");
                break;
            case 1:
                executeButton.setText("验签");
                break;
            case 2:
                executeButton.setText("生成密钥");
                break;
        }
    }
    
    private void executeOperation() {
        int operation = operationCombo.getSelectedIndex();
        
        try {
            switch (operation) {
                case 0:
                    performSign();
                    break;
                case 1:
                    performVerify();
                    break;
                case 2:
                    generateKeyPair();
                    break;
            }
        } catch (Exception e) {
            resultArea.setText("操作失败: " + e.getMessage());
        }
    }
    
    private void performSign() throws Exception {
        String data = dataArea.getText();
        String keyText = keyArea.getText().trim();
        String algorithm = (String) algorithmCombo.getSelectedItem();
        
        if (data.isEmpty() || keyText.isEmpty()) {
            throw new Exception("数据和密钥不能为空");
        }
        
        byte[] signature;
        
        if (algorithm.startsWith("HMAC")) {
            signature = performHmacSign(data, keyText, algorithm);
        } else {
            signature = performAsymmetricSign(data, keyText, algorithm);
        }
        
        String result;
        if (base64CheckBox.isSelected()) {
            result = Base64.getEncoder().encodeToString(signature);
        } else {
            result = bytesToHex(signature);
        }
        
        resultArea.setText("签名结果:\n" + result);
    }
    
    private void performVerify() throws Exception {
        String data = dataArea.getText();
        String keyText = keyArea.getText().trim();
        String signatureText = signatureArea.getText().trim();
        String algorithm = (String) algorithmCombo.getSelectedItem();
        
        if (data.isEmpty() || keyText.isEmpty() || signatureText.isEmpty()) {
            throw new Exception("数据、密钥和签名值不能为空");
        }
        
        byte[] signature;
        if (base64CheckBox.isSelected()) {
            signature = Base64.getDecoder().decode(signatureText);
        } else {
            signature = hexToBytes(signatureText);
        }
        
        boolean isValid;
        
        if (algorithm.startsWith("HMAC")) {
            isValid = performHmacVerify(data, keyText, signature, algorithm);
        } else {
            isValid = performAsymmetricVerify(data, keyText, signature, algorithm);
        }
        
        resultArea.setText("验签结果: " + (isValid ? "验证成功" : "验证失败"));
    }
    
    private byte[] performHmacSign(String data, String key, String algorithm) throws Exception {
        String macAlgorithm = algorithm.replace("-", "");
        Mac mac = Mac.getInstance(macAlgorithm);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), macAlgorithm);
        mac.init(secretKey);
        return mac.doFinal(data.getBytes("UTF-8"));
    }
    
    private boolean performHmacVerify(String data, String key, byte[] signature, String algorithm) throws Exception {
        byte[] expectedSignature = performHmacSign(data, key, algorithm);
        return MessageDigest.isEqual(signature, expectedSignature);
    }
    
    private byte[] performAsymmetricSign(String data, String keyText, String algorithm) throws Exception {
        // 解析算法
        String[] parts = algorithm.split("-");
        String keyAlgorithm = parts[0];
        String hashAlgorithm = parts[1];
        
        String signatureAlgorithm = hashAlgorithm + "with" + keyAlgorithm;
        
        // 解析私钥
        PrivateKey privateKey = parsePrivateKey(keyText, keyAlgorithm);
        
        // 执行签名
        Signature sig = Signature.getInstance(signatureAlgorithm);
        sig.initSign(privateKey);
        sig.update(data.getBytes("UTF-8"));
        
        return sig.sign();
    }
    
    private boolean performAsymmetricVerify(String data, String keyText, byte[] signature, String algorithm) throws Exception {
        // 解析算法
        String[] parts = algorithm.split("-");
        String keyAlgorithm = parts[0];
        String hashAlgorithm = parts[1];
        
        String signatureAlgorithm = hashAlgorithm + "with" + keyAlgorithm;
        
        // 解析公钥
        PublicKey publicKey = parsePublicKey(keyText, keyAlgorithm);
        
        // 执行验签
        Signature sig = Signature.getInstance(signatureAlgorithm);
        sig.initVerify(publicKey);
        sig.update(data.getBytes("UTF-8"));
        
        return sig.verify(signature);
    }
    
    private PrivateKey parsePrivateKey(String keyText, String algorithm) throws Exception {
        // 移除PEM格式的头尾
        String cleanKey = keyText.replaceAll("-----[^-]+-----", "")
                                .replaceAll("\\s+", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        
        return keyFactory.generatePrivate(keySpec);
    }
    
    private PublicKey parsePublicKey(String keyText, String algorithm) throws Exception {
        // 移除PEM格式的头尾
        String cleanKey = keyText.replaceAll("-----[^-]+-----", "")
                                .replaceAll("\\s+", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        
        return keyFactory.generatePublic(keySpec);
    }
    
    private void generateKeyPair() {
        try {
            String algorithm = (String) algorithmCombo.getSelectedItem();
            
            if (algorithm.startsWith("HMAC")) {
                // 生成HMAC密钥
                generateHmacKey();
            } else {
                // 生成非对称密钥对
                String keyAlgorithm = algorithm.split("-")[0];
                generateAsymmetricKeyPair(keyAlgorithm);
            }
        } catch (Exception e) {
            resultArea.setText("生成密钥失败: " + e.getMessage());
        }
    }
    
    private void generateHmacKey() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256位密钥
        random.nextBytes(keyBytes);
        
        String key = Base64.getEncoder().encodeToString(keyBytes);
        keyArea.setText(key);
        resultArea.setText("HMAC密钥已生成并填入密钥框");
    }
    
    private void generateAsymmetricKeyPair(String algorithm) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
        
        // 设置密钥长度
        int keySize = 2048;
        if ("DSA".equals(algorithm)) {
            keySize = 1024;
        } else if ("EC".equals(algorithm)) {
            keySize = 256;
        }
        
        keyGen.initialize(keySize);
        KeyPair keyPair = keyGen.generateKeyPair();
        
        // 格式化私钥
        String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
                           Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()) +
                           "\n-----END PRIVATE KEY-----";
        
        // 格式化公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                          Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()) +
                          "\n-----END PUBLIC KEY-----";
        
        keyArea.setText(privateKey);
        resultArea.setText("密钥对已生成:\n\n私钥（已填入密钥框）:\n" + privateKey + 
                          "\n\n公钥：\n" + publicKey);
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    private byte[] hexToBytes(String hex) {
        hex = hex.replaceAll("\\s+", "");
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
    private void clearAll() {
        dataArea.setText("");
        keyArea.setText("");
        signatureArea.setText("");
        resultArea.setText("");
    }
}