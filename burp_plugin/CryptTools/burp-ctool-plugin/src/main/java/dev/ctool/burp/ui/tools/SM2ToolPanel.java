package dev.ctool.burp.ui.tools;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
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
 * SM2国密算法工具面板
 * 支持SM2加密解密功能
 */
public class SM2ToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 密钥生成面板组件
    private JTextArea publicKeyArea;
    private JTextArea privateKeyArea;
    private JButton generateKeyButton;
    
    // 加密解密面板组件
    private JTextArea encryptInputArea;
    private JTextArea encryptOutputArea;
    private JTextArea encryptKeyArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private JComboBox<String> inputFormatCombo;
    private JComboBox<String> outputFormatCombo;
    private JComboBox<String> keyFormatCombo;
    
    private static final String[] FORMATS = {"文本", "Base64", "十六进制"};
    private static final String[] KEY_FORMATS = {"十六进制", "Base64"};
    
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
    
    public SM2ToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // 密钥生成组件
        publicKeyArea = new JTextArea(8, 50);
        publicKeyArea.setLineWrap(true);
        publicKeyArea.setWrapStyleWord(true);
        publicKeyArea.setEditable(false);
        privateKeyArea = new JTextArea(8, 50);
        privateKeyArea.setLineWrap(true);
        privateKeyArea.setWrapStyleWord(true);
        privateKeyArea.setEditable(false);
        generateKeyButton = new JButton("生成SM2密钥对");
        
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
        
        inputFormatCombo = new JComboBox<>(FORMATS);
        outputFormatCombo = new JComboBox<>(FORMATS);
        outputFormatCombo.setSelectedIndex(1); // 默认Base64输出
        keyFormatCombo = new JComboBox<>(KEY_FORMATS);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建密钥生成面板
        JPanel keyGenPanel = createKeyGenerationPanel();
        tabbedPane.addTab("密钥生成", keyGenPanel);
        
        // 创建加密解密面板
        JPanel encryptPanel = createEncryptionPanel();
        tabbedPane.addTab("加密解密", encryptPanel);
        
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
    
    private JPanel createEncryptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("输入格式:"), gbc);
        gbc.gridx = 1;
        configPanel.add(inputFormatCombo, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("输出格式:"), gbc);
        gbc.gridx = 3;
        configPanel.add(outputFormatCombo, gbc);
        
        gbc.gridx = 4;
        configPanel.add(new JLabel("密钥格式:"), gbc);
        gbc.gridx = 5;
        configPanel.add(keyFormatCombo, gbc);
        
        // 密钥面板
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(new TitledBorder("密钥 (公钥用于加密，私钥用于解密)"));
        keyPanel.add(new JScrollPane(encryptKeyArea), BorderLayout.CENTER);
        
        // 输入输出面板
        JPanel ioPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入"));
        inputPanel.add(new JScrollPane(encryptInputArea), BorderLayout.CENTER);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("输出"));
        outputPanel.add(new JScrollPane(encryptOutputArea), BorderLayout.CENTER);
        
        ioPanel.add(inputPanel);
        ioPanel.add(outputPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        
        // 组装面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(configPanel, BorderLayout.NORTH);
        topPanel.add(keyPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(ioPanel, BorderLayout.CENTER);
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
        
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSM2Encryption();
            }
        });
        
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSM2Decryption();
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
            String publicKeyHex = "04" + 
                    point.getAffineXCoord().toBigInteger().toString(16).toUpperCase() +
                    point.getAffineYCoord().toBigInteger().toString(16).toUpperCase();
            
            publicKeyArea.setText(publicKeyHex);
            privateKeyArea.setText(privateKeyHex);
            
        } catch (Exception e) {
            publicKeyArea.setText("密钥生成失败: " + e.getMessage());
            privateKeyArea.setText("");
        }
    }
    
    private void performSM2Encryption() {
        try {
            String input = encryptInputArea.getText().trim();
            String keyText = encryptKeyArea.getText().trim();
            
            if (input.isEmpty() || keyText.isEmpty()) {
                encryptOutputArea.setText("错误: 输入内容和公钥不能为空");
                return;
            }
            
            // 解析输入
            byte[] inputBytes = parseInput(input);
            
            // 解析公钥
            ECPublicKeyParameters publicKey = parsePublicKey(keyText);
            
            // 执行SM2加密
            SM2Engine engine = new SM2Engine();
            engine.init(true, new ParametersWithRandom(publicKey, new SecureRandom()));
            
            byte[] encrypted = engine.processBlock(inputBytes, 0, inputBytes.length);
            
            // 格式化输出
            String output = formatOutput(encrypted);
            encryptOutputArea.setText(output);
            
        } catch (Exception e) {
            encryptOutputArea.setText("加密失败: " + e.getMessage());
        }
    }
    
    private void performSM2Decryption() {
        try {
            String input = encryptInputArea.getText().trim();
            String keyText = encryptKeyArea.getText().trim();
            
            if (input.isEmpty() || keyText.isEmpty()) {
                encryptOutputArea.setText("错误: 输入内容和私钥不能为空");
                return;
            }
            
            // 解析输入
            byte[] inputBytes = parseInput(input);
            
            // 解析私钥
            ECPrivateKeyParameters privateKey = parsePrivateKey(keyText);
            
            // 执行SM2解密
            SM2Engine engine = new SM2Engine();
            engine.init(false, privateKey);
            
            byte[] decrypted = engine.processBlock(inputBytes, 0, inputBytes.length);
            
            // 格式化输出
            String output = formatOutput(decrypted);
            encryptOutputArea.setText(output);
            
        } catch (Exception e) {
            encryptOutputArea.setText("解密失败: " + e.getMessage());
        }
    }
    
    private byte[] parseInput(String input) throws Exception {
        String inputFormat = (String) inputFormatCombo.getSelectedItem();
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
    
    private String formatOutput(byte[] data) {
        String outputFormat = (String) outputFormatCombo.getSelectedItem();
        switch (outputFormat) {
            case "文本":
                return new String(data, StandardCharsets.UTF_8);
            case "Base64":
                return Base64.getEncoder().encodeToString(data);
            case "十六进制":
                return Hex.toHexString(data).toUpperCase();
            default:
                return Base64.getEncoder().encodeToString(data);
        }
    }
    
    private ECPublicKeyParameters parsePublicKey(String keyText) throws Exception {
        byte[] keyBytes;
        String keyFormat = (String) keyFormatCombo.getSelectedItem();
        
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
    
    private ECPrivateKeyParameters parsePrivateKey(String keyText) throws Exception {
        byte[] keyBytes;
        String keyFormat = (String) keyFormatCombo.getSelectedItem();
        
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