package dev.ctool.burp.ui.tools;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * SM2签名验签工具面板
 * 支持SM2数字签名和验证功能
 */
public class SM2SignatureToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 密钥生成面板组件
    private JTextArea publicKeyArea;
    private JTextArea privateKeyArea;
    private JButton generateKeyButton;
    
    // 签名面板组件
    private JTextArea signInputArea;
    private JTextArea signatureArea;
    private JTextArea signPrivateKeyArea;
    private JButton signButton;
    private JComboBox<String> signInputFormatCombo;
    private JComboBox<String> signatureFormatCombo;
    private JComboBox<String> signKeyFormatCombo;
    private JTextField userIdField;
    
    // 验签面板组件
    private JTextArea verifyInputArea;
    private JTextArea verifySignatureArea;
    private JTextArea verifyPublicKeyArea;
    private JTextArea verifyResultArea;
    private JButton verifyButton;
    private JComboBox<String> verifyInputFormatCombo;
    private JComboBox<String> verifySignatureFormatCombo;
    private JComboBox<String> verifyKeyFormatCombo;
    private JTextField verifyUserIdField;
    
    private static final String[] FORMATS = {"文本", "Base64", "十六进制"};
    private static final String[] KEY_FORMATS = {"十六进制", "Base64"};
    private static final String DEFAULT_USER_ID = "1234567812345678"; // 默认用户ID
    
    // SM2曲线参数
    private static final X9ECParameters SM2_CURVE_PARAMS = GMNamedCurves.getByName("sm2p256v1");
    private static final ECDomainParameters SM2_DOMAIN_PARAMS = new ECDomainParameters(
            SM2_CURVE_PARAMS.getCurve(),
            SM2_CURVE_PARAMS.getG(),
            SM2_CURVE_PARAMS.getN(),
            SM2_CURVE_PARAMS.getH()
    );
    
    static {
        // 添加BouncyCastle提供者
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    public SM2SignatureToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // 密钥生成组件
        publicKeyArea = new JTextArea(6, 50);
        publicKeyArea.setLineWrap(true);
        publicKeyArea.setWrapStyleWord(true);
        publicKeyArea.setEditable(false);
        privateKeyArea = new JTextArea(6, 50);
        privateKeyArea.setLineWrap(true);
        privateKeyArea.setWrapStyleWord(true);
        privateKeyArea.setEditable(false);
        generateKeyButton = new JButton("生成SM2密钥对");
        
        // 签名组件
        signInputArea = new JTextArea(8, 40);
        signInputArea.setLineWrap(true);
        signInputArea.setWrapStyleWord(true);
        signatureArea = new JTextArea(4, 40);
        signatureArea.setLineWrap(true);
        signatureArea.setWrapStyleWord(true);
        signatureArea.setEditable(false);
        signPrivateKeyArea = new JTextArea(4, 40);
        signPrivateKeyArea.setLineWrap(true);
        signPrivateKeyArea.setWrapStyleWord(true);
        signButton = new JButton("签名");
        
        signInputFormatCombo = new JComboBox<>(FORMATS);
        signatureFormatCombo = new JComboBox<>(KEY_FORMATS);
        signKeyFormatCombo = new JComboBox<>(KEY_FORMATS);
        userIdField = new JTextField(DEFAULT_USER_ID, 20);
        
        // 验签组件
        verifyInputArea = new JTextArea(6, 40);
        verifyInputArea.setLineWrap(true);
        verifyInputArea.setWrapStyleWord(true);
        verifySignatureArea = new JTextArea(4, 40);
        verifySignatureArea.setLineWrap(true);
        verifySignatureArea.setWrapStyleWord(true);
        verifyPublicKeyArea = new JTextArea(4, 40);
        verifyPublicKeyArea.setLineWrap(true);
        verifyPublicKeyArea.setWrapStyleWord(true);
        verifyResultArea = new JTextArea(2, 40);
        verifyResultArea.setLineWrap(true);
        verifyResultArea.setWrapStyleWord(true);
        verifyResultArea.setEditable(false);
        verifyButton = new JButton("验证签名");
        
        verifyInputFormatCombo = new JComboBox<>(FORMATS);
        verifySignatureFormatCombo = new JComboBox<>(KEY_FORMATS);
        verifyKeyFormatCombo = new JComboBox<>(KEY_FORMATS);
        verifyUserIdField = new JTextField(DEFAULT_USER_ID, 20);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建密钥生成面板
        JPanel keyGenPanel = createKeyGenerationPanel();
        tabbedPane.addTab("密钥生成", keyGenPanel);
        
        // 创建签名面板
        JPanel signPanel = createSignaturePanel();
        tabbedPane.addTab("数字签名", signPanel);
        
        // 创建验签面板
        JPanel verifyPanel = createVerificationPanel();
        tabbedPane.addTab("签名验证", verifyPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createKeyGenerationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateKeyButton);
        
        // 密钥显示面板
        JPanel keyPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JPanel publicKeyPanel = new JPanel(new BorderLayout());
        publicKeyPanel.setBorder(new TitledBorder("公钥 (十六进制)"));
        publicKeyPanel.add(new JScrollPane(publicKeyArea), BorderLayout.CENTER);
        
        JPanel privateKeyPanel = new JPanel(new BorderLayout());
        privateKeyPanel.setBorder(new TitledBorder("私钥 (十六进制)"));
        privateKeyPanel.add(new JScrollPane(privateKeyArea), BorderLayout.CENTER);
        
        keyPanel.add(publicKeyPanel);
        keyPanel.add(privateKeyPanel);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(keyPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSignaturePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("签名配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("输入格式:"), gbc);
        gbc.gridx = 1;
        configPanel.add(signInputFormatCombo, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("签名格式:"), gbc);
        gbc.gridx = 3;
        configPanel.add(signatureFormatCombo, gbc);
        
        gbc.gridx = 4;
        configPanel.add(new JLabel("密钥格式:"), gbc);
        gbc.gridx = 5;
        configPanel.add(signKeyFormatCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("用户ID:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        configPanel.add(userIdField, gbc);
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(new TitledBorder("待签名数据"));
        dataPanel.add(new JScrollPane(signInputArea), BorderLayout.CENTER);
        
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(new TitledBorder("私钥"));
        keyPanel.add(new JScrollPane(signPrivateKeyArea), BorderLayout.CENTER);
        
        inputPanel.add(dataPanel);
        inputPanel.add(keyPanel);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("签名结果"));
        outputPanel.add(new JScrollPane(signatureArea), BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(signButton);
        
        // 组装面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(configPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(outputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createVerificationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("验签配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("输入格式:"), gbc);
        gbc.gridx = 1;
        configPanel.add(verifyInputFormatCombo, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("签名格式:"), gbc);
        gbc.gridx = 3;
        configPanel.add(verifySignatureFormatCombo, gbc);
        
        gbc.gridx = 4;
        configPanel.add(new JLabel("密钥格式:"), gbc);
        gbc.gridx = 5;
        configPanel.add(verifyKeyFormatCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("用户ID:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        configPanel.add(verifyUserIdField, gbc);
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(new TitledBorder("原始数据"));
        dataPanel.add(new JScrollPane(verifyInputArea), BorderLayout.CENTER);
        
        JPanel sigPanel = new JPanel(new BorderLayout());
        sigPanel.setBorder(new TitledBorder("签名"));
        sigPanel.add(new JScrollPane(verifySignatureArea), BorderLayout.CENTER);
        
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(new TitledBorder("公钥"));
        keyPanel.add(new JScrollPane(verifyPublicKeyArea), BorderLayout.CENTER);
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("验证结果"));
        resultPanel.add(new JScrollPane(verifyResultArea), BorderLayout.CENTER);
        
        inputPanel.add(dataPanel);
        inputPanel.add(sigPanel);
        inputPanel.add(keyPanel);
        inputPanel.add(resultPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(verifyButton);
        
        // 组装面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(configPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSM2KeyPair();
            }
        });
        
        signButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSM2Signature();
            }
        });
        
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSM2Verification();
            }
        });
    }
    
    private void generateSM2KeyPair() {
        try {
            ECKeyPairGenerator keyGen = new ECKeyPairGenerator();
            keyGen.init(new ECKeyGenerationParameters(SM2_DOMAIN_PARAMS, new SecureRandom()));
            
            AsymmetricCipherKeyPair keyPair = keyGen.generateKeyPair();
            
            ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();
            ECPublicKeyParameters publicKey = (ECPublicKeyParameters) keyPair.getPublic();
            
            // 格式化私钥（32字节）
            String privateKeyHex = privateKey.getD().toString(16).toUpperCase();
            while (privateKeyHex.length() < 64) {
                privateKeyHex = "0" + privateKeyHex;
            }
            
            // 格式化公钥（未压缩格式，65字节：04 + 32字节x + 32字节y）
            ECPoint point = publicKey.getQ();
            String xHex = point.getAffineXCoord().toBigInteger().toString(16).toUpperCase();
            String yHex = point.getAffineYCoord().toBigInteger().toString(16).toUpperCase();
            while (xHex.length() < 64) xHex = "0" + xHex;
            while (yHex.length() < 64) yHex = "0" + yHex;
            String publicKeyHex = "04" + xHex + yHex;
            
            publicKeyArea.setText(publicKeyHex);
            privateKeyArea.setText(privateKeyHex);
            
        } catch (Exception e) {
            publicKeyArea.setText("密钥生成失败: " + e.getMessage());
            privateKeyArea.setText("");
        }
    }
    
    private void performSM2Signature() {
        try {
            String input = signInputArea.getText().trim();
            String keyText = signPrivateKeyArea.getText().trim();
            String userId = userIdField.getText().trim();
            
            if (input.isEmpty() || keyText.isEmpty()) {
                signatureArea.setText("错误: 输入数据和私钥不能为空");
                return;
            }
            
            // 解析输入
            byte[] inputBytes = parseInput(input, signInputFormatCombo);
            
            // 解析私钥
            ECPrivateKeyParameters privateKey = parsePrivateKey(keyText, signKeyFormatCombo);
            
            // 执行SM2签名
            SM2Signer signer = new SM2Signer();
            signer.init(true, new ParametersWithID(new ParametersWithRandom(privateKey, new SecureRandom()), 
                    userId.getBytes(StandardCharsets.UTF_8)));
            
            signer.update(inputBytes, 0, inputBytes.length);
            byte[] signature = signer.generateSignature();
            
            // 格式化输出
            String output = formatSignature(signature, signatureFormatCombo);
            signatureArea.setText(output);
            
        } catch (Exception e) {
            signatureArea.setText("签名失败: " + e.getMessage());
        }
    }
    
    private void performSM2Verification() {
        try {
            String input = verifyInputArea.getText().trim();
            String signatureText = verifySignatureArea.getText().trim();
            String keyText = verifyPublicKeyArea.getText().trim();
            String userId = verifyUserIdField.getText().trim();
            
            if (input.isEmpty() || signatureText.isEmpty() || keyText.isEmpty()) {
                verifyResultArea.setText("错误: 输入数据、签名和公钥不能为空");
                return;
            }
            
            // 解析输入
            byte[] inputBytes = parseInput(input, verifyInputFormatCombo);
            byte[] signatureBytes = parseSignature(signatureText, verifySignatureFormatCombo);
            
            // 解析公钥
            ECPublicKeyParameters publicKey = parsePublicKey(keyText, verifyKeyFormatCombo);
            
            // 执行SM2验签
            SM2Signer signer = new SM2Signer();
            signer.init(false, new ParametersWithID(publicKey, userId.getBytes(StandardCharsets.UTF_8)));
            
            signer.update(inputBytes, 0, inputBytes.length);
            boolean isValid = signer.verifySignature(signatureBytes);
            
            verifyResultArea.setText(isValid ? "✓ 签名验证成功" : "✗ 签名验证失败");
            verifyResultArea.setForeground(isValid ? Color.GREEN.darker() : Color.RED);
            
        } catch (Exception e) {
            verifyResultArea.setText("验签失败: " + e.getMessage());
            verifyResultArea.setForeground(Color.RED);
        }
    }
    
    private byte[] parseInput(String input, JComboBox<String> formatCombo) throws Exception {
        String inputFormat = (String) formatCombo.getSelectedItem();
        switch (inputFormat) {
            case "文本":
                return input.getBytes(StandardCharsets.UTF_8);
            case "Base64":
                return Base64.getDecoder().decode(input);
            case "十六进制":
                return Hex.decode(input.replaceAll("\\s+", ""));
            default:
                return input.getBytes(StandardCharsets.UTF_8);
        }
    }
    
    private String formatSignature(byte[] signature, JComboBox<String> formatCombo) {
        String signatureFormat = (String) formatCombo.getSelectedItem();
        switch (signatureFormat) {
            case "十六进制":
                return Hex.toHexString(signature).toUpperCase();
            case "Base64":
                return Base64.getEncoder().encodeToString(signature);
            default:
                return Hex.toHexString(signature).toUpperCase();
        }
    }
    
    private byte[] parseSignature(String signatureText, JComboBox<String> formatCombo) throws Exception {
        String signatureFormat = (String) formatCombo.getSelectedItem();
        switch (signatureFormat) {
            case "十六进制":
                return Hex.decode(signatureText.replaceAll("\\s+", ""));
            case "Base64":
                return Base64.getDecoder().decode(signatureText);
            default:
                return Hex.decode(signatureText.replaceAll("\\s+", ""));
        }
    }
    
    private ECPublicKeyParameters parsePublicKey(String keyText, JComboBox<String> formatCombo) throws Exception {
        byte[] keyBytes;
        String keyFormat = (String) formatCombo.getSelectedItem();
        
        switch (keyFormat) {
            case "十六进制":
                keyBytes = Hex.decode(keyText.replaceAll("\\s+", ""));
                break;
            case "Base64":
                keyBytes = Base64.getDecoder().decode(keyText);
                break;
            default:
                keyBytes = Hex.decode(keyText.replaceAll("\\s+", ""));
        }
        
        if (keyBytes.length != 65 || keyBytes[0] != 0x04) {
            throw new IllegalArgumentException("无效的SM2公钥格式，应为65字节未压缩格式");
        }
        
        byte[] xBytes = new byte[32];
        byte[] yBytes = new byte[32];
        System.arraycopy(keyBytes, 1, xBytes, 0, 32);
        System.arraycopy(keyBytes, 33, yBytes, 0, 32);
        
        BigInteger x = new BigInteger(1, xBytes);
        BigInteger y = new BigInteger(1, yBytes);
        
        ECPoint point = SM2_CURVE_PARAMS.getCurve().createPoint(x, y);
        return new ECPublicKeyParameters(point, SM2_DOMAIN_PARAMS);
    }
    
    private ECPrivateKeyParameters parsePrivateKey(String keyText, JComboBox<String> formatCombo) throws Exception {
        byte[] keyBytes;
        String keyFormat = (String) formatCombo.getSelectedItem();
        
        switch (keyFormat) {
            case "十六进制":
                keyBytes = Hex.decode(keyText.replaceAll("\\s+", ""));
                break;
            case "Base64":
                keyBytes = Base64.getDecoder().decode(keyText);
                break;
            default:
                keyBytes = Hex.decode(keyText.replaceAll("\\s+", ""));
        }
        
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("无效的SM2私钥格式，应为32字节");
        }
        
        BigInteger d = new BigInteger(1, keyBytes);
        return new ECPrivateKeyParameters(d, SM2_DOMAIN_PARAMS);
    }
}