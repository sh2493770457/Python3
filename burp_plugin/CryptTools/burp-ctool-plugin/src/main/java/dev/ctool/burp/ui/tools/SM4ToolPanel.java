package dev.ctool.burp.ui.tools;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * SM4加密解密工具面板
 * 支持SM4对称加密算法，包括ECB、CBC、CFB、OFB模式
 */
public class SM4ToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 加密面板组件
    private JTextArea encryptInputArea;
    private JTextArea encryptOutputArea;
    private JTextArea encryptKeyArea;
    private JTextArea encryptIvArea;
    private JButton encryptButton;
    private JButton generateKeyButton;
    private JButton generateIvButton;
    private JComboBox<String> encryptModeCombo;
    private JComboBox<String> encryptInputFormatCombo;
    private JComboBox<String> encryptOutputFormatCombo;
    private JComboBox<String> encryptKeyFormatCombo;
    private JComboBox<String> encryptIvFormatCombo;
    
    // 解密面板组件
    private JTextArea decryptInputArea;
    private JTextArea decryptOutputArea;
    private JTextArea decryptKeyArea;
    private JTextArea decryptIvArea;
    private JButton decryptButton;
    private JComboBox<String> decryptModeCombo;
    private JComboBox<String> decryptInputFormatCombo;
    private JComboBox<String> decryptOutputFormatCombo;
    private JComboBox<String> decryptKeyFormatCombo;
    private JComboBox<String> decryptIvFormatCombo;
    
    private static final String[] MODES = {"ECB", "CBC", "CFB", "OFB"};
    private static final String[] FORMATS = {"文本", "Base64", "十六进制"};
    private static final String[] KEY_FORMATS = {"十六进制", "Base64"};
    
    static {
        // 添加BouncyCastle提供者
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    public SM4ToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // 加密组件
        encryptInputArea = new JTextArea(8, 40);
        encryptInputArea.setLineWrap(true);
        encryptInputArea.setWrapStyleWord(true);
        encryptOutputArea = new JTextArea(8, 40);
        encryptOutputArea.setLineWrap(true);
        encryptOutputArea.setWrapStyleWord(true);
        encryptOutputArea.setEditable(false);
        encryptKeyArea = new JTextArea(3, 40);
        encryptKeyArea.setLineWrap(true);
        encryptKeyArea.setWrapStyleWord(true);
        encryptIvArea = new JTextArea(3, 40);
        encryptIvArea.setLineWrap(true);
        encryptIvArea.setWrapStyleWord(true);
        
        encryptButton = new JButton("加密");
        generateKeyButton = new JButton("生成密钥");
        generateIvButton = new JButton("生成IV");
        
        encryptModeCombo = new JComboBox<>(MODES);
        encryptInputFormatCombo = new JComboBox<>(FORMATS);
        encryptOutputFormatCombo = new JComboBox<>(FORMATS);
        encryptKeyFormatCombo = new JComboBox<>(KEY_FORMATS);
        encryptIvFormatCombo = new JComboBox<>(KEY_FORMATS);
        
        // 解密组件
        decryptInputArea = new JTextArea(8, 40);
        decryptInputArea.setLineWrap(true);
        decryptInputArea.setWrapStyleWord(true);
        decryptOutputArea = new JTextArea(8, 40);
        decryptOutputArea.setLineWrap(true);
        decryptOutputArea.setWrapStyleWord(true);
        decryptOutputArea.setEditable(false);
        decryptKeyArea = new JTextArea(3, 40);
        decryptKeyArea.setLineWrap(true);
        decryptKeyArea.setWrapStyleWord(true);
        decryptIvArea = new JTextArea(3, 40);
        decryptIvArea.setLineWrap(true);
        decryptIvArea.setWrapStyleWord(true);
        
        decryptButton = new JButton("解密");
        
        decryptModeCombo = new JComboBox<>(MODES);
        decryptInputFormatCombo = new JComboBox<>(FORMATS);
        decryptOutputFormatCombo = new JComboBox<>(FORMATS);
        decryptKeyFormatCombo = new JComboBox<>(KEY_FORMATS);
        decryptIvFormatCombo = new JComboBox<>(KEY_FORMATS);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建加密面板
        JPanel encryptPanel = createEncryptionPanel();
        tabbedPane.addTab("SM4加密", encryptPanel);
        
        // 创建解密面板
        JPanel decryptPanel = createDecryptionPanel();
        tabbedPane.addTab("SM4解密", decryptPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createEncryptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("加密配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("加密模式:"), gbc);
        gbc.gridx = 1;
        configPanel.add(encryptModeCombo, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("输入格式:"), gbc);
        gbc.gridx = 3;
        configPanel.add(encryptInputFormatCombo, gbc);
        
        gbc.gridx = 4;
        configPanel.add(new JLabel("输出格式:"), gbc);
        gbc.gridx = 5;
        configPanel.add(encryptOutputFormatCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("密钥格式:"), gbc);
        gbc.gridx = 1;
        configPanel.add(encryptKeyFormatCombo, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("IV格式:"), gbc);
        gbc.gridx = 3;
        configPanel.add(encryptIvFormatCombo, gbc);
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(new TitledBorder("明文数据"));
        dataPanel.add(new JScrollPane(encryptInputArea), BorderLayout.CENTER);
        
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(new TitledBorder("密钥 (128位/16字节)"));
        JPanel keyButtonPanel = new JPanel(new BorderLayout());
        keyButtonPanel.add(new JScrollPane(encryptKeyArea), BorderLayout.CENTER);
        JPanel keyBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        keyBtnPanel.add(generateKeyButton);
        keyButtonPanel.add(keyBtnPanel, BorderLayout.SOUTH);
        keyPanel.add(keyButtonPanel, BorderLayout.CENTER);
        
        JPanel ivPanel = new JPanel(new BorderLayout());
        ivPanel.setBorder(new TitledBorder("初始向量IV (128位/16字节)"));
        JPanel ivButtonPanel = new JPanel(new BorderLayout());
        ivButtonPanel.add(new JScrollPane(encryptIvArea), BorderLayout.CENTER);
        JPanel ivBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ivBtnPanel.add(generateIvButton);
        ivButtonPanel.add(ivBtnPanel, BorderLayout.SOUTH);
        ivPanel.add(ivButtonPanel, BorderLayout.CENTER);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("加密结果"));
        outputPanel.add(new JScrollPane(encryptOutputArea), BorderLayout.CENTER);
        
        inputPanel.add(dataPanel);
        inputPanel.add(keyPanel);
        inputPanel.add(ivPanel);
        inputPanel.add(outputPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(encryptButton);
        
        // 组装面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(configPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDecryptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("解密配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("解密模式:"), gbc);
        gbc.gridx = 1;
        configPanel.add(decryptModeCombo, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("输入格式:"), gbc);
        gbc.gridx = 3;
        configPanel.add(decryptInputFormatCombo, gbc);
        
        gbc.gridx = 4;
        configPanel.add(new JLabel("输出格式:"), gbc);
        gbc.gridx = 5;
        configPanel.add(decryptOutputFormatCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("密钥格式:"), gbc);
        gbc.gridx = 1;
        configPanel.add(decryptKeyFormatCombo, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("IV格式:"), gbc);
        gbc.gridx = 3;
        configPanel.add(decryptIvFormatCombo, gbc);
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(new TitledBorder("密文数据"));
        dataPanel.add(new JScrollPane(decryptInputArea), BorderLayout.CENTER);
        
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(new TitledBorder("密钥 (128位/16字节)"));
        keyPanel.add(new JScrollPane(decryptKeyArea), BorderLayout.CENTER);
        
        JPanel ivPanel = new JPanel(new BorderLayout());
        ivPanel.setBorder(new TitledBorder("初始向量IV (128位/16字节)"));
        ivPanel.add(new JScrollPane(decryptIvArea), BorderLayout.CENTER);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("解密结果"));
        outputPanel.add(new JScrollPane(decryptOutputArea), BorderLayout.CENTER);
        
        inputPanel.add(dataPanel);
        inputPanel.add(keyPanel);
        inputPanel.add(ivPanel);
        inputPanel.add(outputPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(decryptButton);
        
        // 组装面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(configPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSM4Encryption();
            }
        });
        
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSM4Decryption();
            }
        });
        
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSM4Key();
            }
        });
        
        generateIvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSM4IV();
            }
        });
        
        // 模式变化监听器
        encryptModeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateIVVisibility(encryptModeCombo, encryptIvArea, encryptIvFormatCombo, generateIvButton);
            }
        });
        
        decryptModeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateIVVisibility(decryptModeCombo, decryptIvArea, decryptIvFormatCombo, null);
            }
        });
        
        // 初始化IV可见性
        updateIVVisibility(encryptModeCombo, encryptIvArea, encryptIvFormatCombo, generateIvButton);
        updateIVVisibility(decryptModeCombo, decryptIvArea, decryptIvFormatCombo, null);
    }
    
    private void updateIVVisibility(JComboBox<String> modeCombo, JTextArea ivArea, 
                                   JComboBox<String> ivFormatCombo, JButton ivButton) {
        String mode = (String) modeCombo.getSelectedItem();
        boolean needsIV = !"ECB".equals(mode);
        
        ivArea.setEnabled(needsIV);
        ivFormatCombo.setEnabled(needsIV);
        if (ivButton != null) {
            ivButton.setEnabled(needsIV);
        }
        
        if (!needsIV) {
            ivArea.setText("");
        }
    }
    
    private void performSM4Encryption() {
        try {
            String input = encryptInputArea.getText().trim();
            String keyText = encryptKeyArea.getText().trim();
            String mode = (String) encryptModeCombo.getSelectedItem();
            
            if (input.isEmpty() || keyText.isEmpty()) {
                encryptOutputArea.setText("错误: 输入数据和密钥不能为空");
                return;
            }
            
            // 解析输入
            byte[] inputBytes = parseInput(input, encryptInputFormatCombo);
            byte[] keyBytes = parseKey(keyText, encryptKeyFormatCombo);
            
            if (keyBytes.length != 16) {
                encryptOutputArea.setText("错误: SM4密钥必须为128位(16字节)");
                return;
            }
            
            byte[] ivBytes = null;
            if (!"ECB".equals(mode)) {
                String ivText = encryptIvArea.getText().trim();
                if (ivText.isEmpty()) {
                    encryptOutputArea.setText("错误: " + mode + "模式需要提供IV");
                    return;
                }
                ivBytes = parseKey(ivText, encryptIvFormatCombo);
                if (ivBytes.length != 16) {
                    encryptOutputArea.setText("错误: SM4 IV必须为128位(16字节)");
                    return;
                }
            }
            
            // 执行SM4加密
            byte[] encrypted = sm4Encrypt(inputBytes, keyBytes, ivBytes, mode);
            
            // 格式化输出
            String output = formatOutput(encrypted, encryptOutputFormatCombo);
            encryptOutputArea.setText(output);
            
        } catch (Exception e) {
            encryptOutputArea.setText("加密失败: " + e.getMessage());
        }
    }
    
    private void performSM4Decryption() {
        try {
            String input = decryptInputArea.getText().trim();
            String keyText = decryptKeyArea.getText().trim();
            String mode = (String) decryptModeCombo.getSelectedItem();
            
            if (input.isEmpty() || keyText.isEmpty()) {
                decryptOutputArea.setText("错误: 输入数据和密钥不能为空");
                return;
            }
            
            // 解析输入
            byte[] inputBytes = parseInput(input, decryptInputFormatCombo);
            byte[] keyBytes = parseKey(keyText, decryptKeyFormatCombo);
            
            if (keyBytes.length != 16) {
                decryptOutputArea.setText("错误: SM4密钥必须为128位(16字节)");
                return;
            }
            
            byte[] ivBytes = null;
            if (!"ECB".equals(mode)) {
                String ivText = decryptIvArea.getText().trim();
                if (ivText.isEmpty()) {
                    decryptOutputArea.setText("错误: " + mode + "模式需要提供IV");
                    return;
                }
                ivBytes = parseKey(ivText, decryptIvFormatCombo);
                if (ivBytes.length != 16) {
                    decryptOutputArea.setText("错误: SM4 IV必须为128位(16字节)");
                    return;
                }
            }
            
            // 执行SM4解密
            byte[] decrypted = sm4Decrypt(inputBytes, keyBytes, ivBytes, mode);
            
            // 格式化输出
            String output = formatOutput(decrypted, decryptOutputFormatCombo);
            decryptOutputArea.setText(output);
            
        } catch (Exception e) {
            decryptOutputArea.setText("解密失败: " + e.getMessage());
        }
    }
    
    private byte[] sm4Encrypt(byte[] input, byte[] key, byte[] iv, String mode) throws Exception {
        BlockCipher engine = new SM4Engine();
        BlockCipher cipher;
        
        switch (mode) {
            case "ECB":
                cipher = engine;
                break;
            case "CBC":
                cipher = new CBCBlockCipher(engine);
                break;
            case "CFB":
                cipher = new CFBBlockCipher(engine, 128);
                break;
            case "OFB":
                cipher = new OFBBlockCipher(engine, 128);
                break;
            default:
                throw new IllegalArgumentException("不支持的加密模式: " + mode);
        }
        
        PaddedBufferedBlockCipher paddedCipher = new PaddedBufferedBlockCipher(cipher, new PKCS7Padding());
        
        KeyParameter keyParam = new KeyParameter(key);
        if (iv != null) {
            paddedCipher.init(true, new ParametersWithIV(keyParam, iv));
        } else {
            paddedCipher.init(true, keyParam);
        }
        
        byte[] output = new byte[paddedCipher.getOutputSize(input.length)];
        int len = paddedCipher.processBytes(input, 0, input.length, output, 0);
        len += paddedCipher.doFinal(output, len);
        
        byte[] result = new byte[len];
        System.arraycopy(output, 0, result, 0, len);
        return result;
    }
    
    private byte[] sm4Decrypt(byte[] input, byte[] key, byte[] iv, String mode) throws Exception {
        BlockCipher engine = new SM4Engine();
        BlockCipher cipher;
        
        switch (mode) {
            case "ECB":
                cipher = engine;
                break;
            case "CBC":
                cipher = new CBCBlockCipher(engine);
                break;
            case "CFB":
                cipher = new CFBBlockCipher(engine, 128);
                break;
            case "OFB":
                cipher = new OFBBlockCipher(engine, 128);
                break;
            default:
                throw new IllegalArgumentException("不支持的解密模式: " + mode);
        }
        
        PaddedBufferedBlockCipher paddedCipher = new PaddedBufferedBlockCipher(cipher, new PKCS7Padding());
        
        KeyParameter keyParam = new KeyParameter(key);
        if (iv != null) {
            paddedCipher.init(false, new ParametersWithIV(keyParam, iv));
        } else {
            paddedCipher.init(false, keyParam);
        }
        
        byte[] output = new byte[paddedCipher.getOutputSize(input.length)];
        int len = paddedCipher.processBytes(input, 0, input.length, output, 0);
        len += paddedCipher.doFinal(output, len);
        
        byte[] result = new byte[len];
        System.arraycopy(output, 0, result, 0, len);
        return result;
    }
    
    private void generateSM4Key() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] key = new byte[16]; // SM4密钥长度为128位(16字节)
            random.nextBytes(key);
            
            String keyFormat = (String) encryptKeyFormatCombo.getSelectedItem();
            String keyText;
            switch (keyFormat) {
                case "十六进制":
                    keyText = Hex.toHexString(key).toUpperCase();
                    break;
                case "Base64":
                    keyText = Base64.getEncoder().encodeToString(key);
                    break;
                default:
                    keyText = Hex.toHexString(key).toUpperCase();
            }
            
            encryptKeyArea.setText(keyText);
        } catch (Exception e) {
            encryptKeyArea.setText("密钥生成失败: " + e.getMessage());
        }
    }
    
    private void generateSM4IV() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[16]; // SM4 IV长度为128位(16字节)
            random.nextBytes(iv);
            
            String ivFormat = (String) encryptIvFormatCombo.getSelectedItem();
            String ivText;
            switch (ivFormat) {
                case "十六进制":
                    ivText = Hex.toHexString(iv).toUpperCase();
                    break;
                case "Base64":
                    ivText = Base64.getEncoder().encodeToString(iv);
                    break;
                default:
                    ivText = Hex.toHexString(iv).toUpperCase();
            }
            
            encryptIvArea.setText(ivText);
        } catch (Exception e) {
            encryptIvArea.setText("IV生成失败: " + e.getMessage());
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
    
    private byte[] parseKey(String keyText, JComboBox<String> formatCombo) throws Exception {
        String keyFormat = (String) formatCombo.getSelectedItem();
        switch (keyFormat) {
            case "十六进制":
                return Hex.decode(keyText.replaceAll("\\s+", ""));
            case "Base64":
                return Base64.getDecoder().decode(keyText);
            default:
                return Hex.decode(keyText.replaceAll("\\s+", ""));
        }
    }
    
    private String formatOutput(byte[] output, JComboBox<String> formatCombo) {
        String outputFormat = (String) formatCombo.getSelectedItem();
        switch (outputFormat) {
            case "文本":
                return new String(output, StandardCharsets.UTF_8);
            case "Base64":
                return Base64.getEncoder().encodeToString(output);
            case "十六进制":
                return Hex.toHexString(output).toUpperCase();
            default:
                return new String(output, StandardCharsets.UTF_8);
        }
    }
}