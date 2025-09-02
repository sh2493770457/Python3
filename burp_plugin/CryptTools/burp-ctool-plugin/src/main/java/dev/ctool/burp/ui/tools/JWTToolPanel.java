package dev.ctool.burp.ui.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

public class JWTToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // JWT解析选项卡
    private JTextArea parseInputArea;
    private JTextArea parseOutputArea;
    
    // JWT生成选项卡
    private JTextArea headerArea;
    private JTextArea payloadArea;
    private JTextField secretField;
    private JComboBox<String> algorithmComboBox;
    private JTextArea generateOutputArea;
    
    // JWT验证选项卡
    private JTextArea verifyTokenArea;
    private JTextField verifySecretField;
    private JLabel verifyResultLabel;
    private JTextArea verifyDetailsArea;
    
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JWTToolPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("JWT 工具"));

        tabbedPane = new JTabbedPane();
        
        // JWT解析选项卡
        JPanel parsePanel = createParsePanel();
        tabbedPane.addTab("解析JWT", parsePanel);
        
        // JWT生成选项卡
        JPanel generatePanel = createGeneratePanel();
        tabbedPane.addTab("生成JWT", generatePanel);
        
        // JWT验证选项卡
        JPanel verifyPanel = createVerifyPanel();
        tabbedPane.addTab("验证JWT", verifyPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createParsePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("JWT Token"));
        
        parseInputArea = new JTextArea(4, 40);
        parseInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        parseInputArea.setLineWrap(true);
        parseInputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(parseInputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("解析结果"));
        
        parseOutputArea = new JTextArea(12, 40);
        parseOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        parseOutputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(parseOutputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton parseButton = new JButton("解析JWT");
        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseJWT();
            }
        });
        
        JButton clearParseButton = new JButton("清空");
        clearParseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearParseFields();
            }
        });
        
        JButton copyParseButton = new JButton("复制结果");
        copyParseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyParseResult();
            }
        });
        
        buttonPanel.add(parseButton);
        buttonPanel.add(clearParseButton);
        buttonPanel.add(copyParseButton);
        
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(outputPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createGeneratePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        
        // Header区域
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new TitledBorder("Header (JSON)"));
        
        headerArea = new JTextArea(4, 40);
        headerArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        headerArea.setText("{\n  \"alg\": \"HS256\",\n  \"typ\": \"JWT\"\n}");
        JScrollPane headerScrollPane = new JScrollPane(headerArea);
        headerPanel.add(headerScrollPane, BorderLayout.CENTER);
        
        // Payload区域
        JPanel payloadPanel = new JPanel(new BorderLayout());
        payloadPanel.setBorder(new TitledBorder("Payload (JSON)"));
        
        payloadArea = new JTextArea(4, 40);
        payloadArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        payloadArea.setText("{\n  \"sub\": \"1234567890\",\n  \"name\": \"John Doe\",\n  \"iat\": " + (System.currentTimeMillis() / 1000) + "\n}");
        JScrollPane payloadScrollPane = new JScrollPane(payloadArea);
        payloadPanel.add(payloadScrollPane, BorderLayout.CENTER);
        
        inputPanel.add(headerPanel);
        inputPanel.add(payloadPanel);
        
        // 配置区域
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        configPanel.setBorder(new TitledBorder("签名配置"));
        
        configPanel.add(new JLabel("算法:"));
        algorithmComboBox = new JComboBox<>(new String[]{"HS256", "HS384", "HS512"});
        configPanel.add(algorithmComboBox);
        
        configPanel.add(Box.createHorizontalStrut(20));
        configPanel.add(new JLabel("密钥:"));
        secretField = new JTextField(20);
        secretField.setText("your-256-bit-secret");
        configPanel.add(secretField);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("生成的JWT"));
        
        generateOutputArea = new JTextArea(4, 40);
        generateOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        generateOutputArea.setLineWrap(true);
        generateOutputArea.setWrapStyleWord(true);
        generateOutputArea.setEditable(false);
        JScrollPane generateOutputScrollPane = new JScrollPane(generateOutputArea);
        outputPanel.add(generateOutputScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton generateButton = new JButton("生成JWT");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateJWT();
            }
        });
        
        JButton clearGenerateButton = new JButton("清空");
        clearGenerateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearGenerateFields();
            }
        });
        
        JButton copyGenerateButton = new JButton("复制JWT");
        copyGenerateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyGenerateResult();
            }
        });
        
        buttonPanel.add(generateButton);
        buttonPanel.add(clearGenerateButton);
        buttonPanel.add(copyGenerateButton);
        
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(configPanel, BorderLayout.CENTER);
        panel.add(outputPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createVerifyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        
        // JWT输入
        JPanel tokenPanel = new JPanel(new BorderLayout());
        tokenPanel.setBorder(new TitledBorder("JWT Token"));
        
        verifyTokenArea = new JTextArea(4, 40);
        verifyTokenArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        verifyTokenArea.setLineWrap(true);
        verifyTokenArea.setWrapStyleWord(true);
        JScrollPane tokenScrollPane = new JScrollPane(verifyTokenArea);
        tokenPanel.add(tokenScrollPane, BorderLayout.CENTER);
        
        // 密钥输入
        JPanel secretPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secretPanel.add(new JLabel("验证密钥:"));
        verifySecretField = new JTextField(30);
        verifySecretField.setText("your-256-bit-secret");
        secretPanel.add(verifySecretField);
        
        inputPanel.add(tokenPanel, BorderLayout.CENTER);
        inputPanel.add(secretPanel, BorderLayout.SOUTH);
        
        // 验证结果
        JPanel resultPanel = new JPanel(new BorderLayout());
        
        verifyResultLabel = new JLabel(" ");
        verifyResultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        verifyResultLabel.setBorder(BorderFactory.createTitledBorder("验证结果"));
        resultPanel.add(verifyResultLabel, BorderLayout.NORTH);
        
        // 详细信息
        verifyDetailsArea = new JTextArea(8, 40);
        verifyDetailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        verifyDetailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(verifyDetailsArea);
        detailsScrollPane.setBorder(new TitledBorder("Token详细信息"));
        resultPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton verifyButton = new JButton("验证JWT");
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyJWT();
            }
        });
        
        JButton clearVerifyButton = new JButton("清空");
        clearVerifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearVerifyFields();
            }
        });
        
        buttonPanel.add(verifyButton);
        buttonPanel.add(clearVerifyButton);
        
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void parseJWT() {
        try {
            String token = parseInputArea.getText().trim();
            if (token.isEmpty()) {
                parseOutputArea.setText("请输入JWT Token");
                return;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                parseOutputArea.setText("无效的JWT格式，JWT应该包含3个部分（header.payload.signature）");
                return;
            }
            
            StringBuilder result = new StringBuilder();
            result.append("JWT解析结果:\n");
            result.append("=============\n\n");
            
            // 解析Header
            try {
                String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
                JsonObject headerObj = JsonParser.parseString(headerJson).getAsJsonObject();
                result.append("Header:\n");
                result.append(gson.toJson(headerObj)).append("\n\n");
            } catch (Exception e) {
                result.append("Header解析失败: ").append(e.getMessage()).append("\n\n");
            }
            
            // 解析Payload
            try {
                String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                JsonObject payloadObj = JsonParser.parseString(payloadJson).getAsJsonObject();
                result.append("Payload:\n");
                result.append(gson.toJson(payloadObj)).append("\n\n");
                
                // 解析时间戳
                if (payloadObj.has("iat")) {
                    long iat = payloadObj.get("iat").getAsLong();
                    result.append("签发时间 (iat): ").append(new Date(iat * 1000)).append("\n");
                }
                if (payloadObj.has("exp")) {
                    long exp = payloadObj.get("exp").getAsLong();
                    result.append("过期时间 (exp): ").append(new Date(exp * 1000));
                    if (exp * 1000 < System.currentTimeMillis()) {
                        result.append(" [已过期]");
                    }
                    result.append("\n");
                }
                if (payloadObj.has("nbf")) {
                    long nbf = payloadObj.get("nbf").getAsLong();
                    result.append("生效时间 (nbf): ").append(new Date(nbf * 1000)).append("\n");
                }
                result.append("\n");
                
            } catch (Exception e) {
                result.append("Payload解析失败: ").append(e.getMessage()).append("\n\n");
            }
            
            // 签名信息
            result.append("Signature (Base64URL): ").append(parts[2]).append("\n");
            result.append("\n注意: 此工具仅解析JWT结构，不验证签名有效性。");
            
            parseOutputArea.setText(result.toString());
            
        } catch (Exception e) {
            parseOutputArea.setText("解析失败: " + e.getMessage());
        }
    }

    private void generateJWT() {
        try {
            String headerJson = headerArea.getText().trim();
            String payloadJson = payloadArea.getText().trim();
            String secret = secretField.getText();
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            
            if (headerJson.isEmpty() || payloadJson.isEmpty() || secret.isEmpty()) {
                generateOutputArea.setText("请填写Header、Payload和密钥");
                return;
            }
            
            // 验证JSON格式
            JsonParser.parseString(headerJson);
            JsonParser.parseString(payloadJson);
            
            // Base64URL编码
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            
            // 创建签名数据
            String signatureData = encodedHeader + "." + encodedPayload;
            
            // 生成签名
            String signature = createSignature(signatureData, secret, algorithm);
            
            // 组装JWT
            String jwt = signatureData + "." + signature;
            
            generateOutputArea.setText(jwt);
            
        } catch (Exception e) {
            generateOutputArea.setText("生成失败: " + e.getMessage());
        }
    }

    private void verifyJWT() {
        try {
            String token = verifyTokenArea.getText().trim();
            String secret = verifySecretField.getText().trim();
            
            if (token.isEmpty() || secret.isEmpty()) {
                verifyResultLabel.setText("请输入JWT Token和验证密钥");
                verifyResultLabel.setForeground(Color.RED);
                return;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                verifyResultLabel.setText("✗ 无效的JWT格式");
                verifyResultLabel.setForeground(Color.RED);
                return;
            }
            
            // 解析Header获取算法
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            JsonObject headerObj = JsonParser.parseString(headerJson).getAsJsonObject();
            String algorithm = headerObj.get("alg").getAsString();
            
            // 重新计算签名
            String signatureData = parts[0] + "." + parts[1];
            String expectedSignature = createSignature(signatureData, secret, algorithm);
            
            boolean isValid = expectedSignature.equals(parts[2]);
            
            if (isValid) {
                verifyResultLabel.setText("✓ JWT签名验证成功");
                verifyResultLabel.setForeground(new Color(0, 128, 0));
            } else {
                verifyResultLabel.setText("✗ JWT签名验证失败");
                verifyResultLabel.setForeground(Color.RED);
            }
            
            // 显示详细信息
            StringBuilder details = new StringBuilder();
            details.append("Header:\n");
            details.append(gson.toJson(headerObj)).append("\n\n");
            
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonObject payloadObj = JsonParser.parseString(payloadJson).getAsJsonObject();
            details.append("Payload:\n");
            details.append(gson.toJson(payloadObj)).append("\n\n");
            
            details.append("算法: ").append(algorithm).append("\n");
            details.append("期望签名: ").append(expectedSignature).append("\n");
            details.append("实际签名: ").append(parts[2]).append("\n");
            
            verifyDetailsArea.setText(details.toString());
            
        } catch (Exception e) {
            verifyResultLabel.setText("验证失败: " + e.getMessage());
            verifyResultLabel.setForeground(Color.RED);
            verifyDetailsArea.setText("");
        }
    }

    private String createSignature(String data, String secret, String algorithm) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        String hmacAlgorithm;
        switch (algorithm) {
            case "HS256":
                hmacAlgorithm = "HmacSHA256";
                break;
            case "HS384":
                hmacAlgorithm = "HmacSHA384";
                break;
            case "HS512":
                hmacAlgorithm = "HmacSHA512";
                break;
            default:
                throw new IllegalArgumentException("不支持的算法: " + algorithm);
        }
        
        Mac mac = Mac.getInstance(hmacAlgorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), hmacAlgorithm);
        mac.init(secretKeySpec);
        
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }

    private void clearParseFields() {
        parseInputArea.setText("");
        parseOutputArea.setText("");
    }

    private void clearGenerateFields() {
        headerArea.setText("{\n  \"alg\": \"HS256\",\n  \"typ\": \"JWT\"\n}");
        payloadArea.setText("{\n  \"sub\": \"1234567890\",\n  \"name\": \"John Doe\",\n  \"iat\": " + (System.currentTimeMillis() / 1000) + "\n}");
        secretField.setText("your-256-bit-secret");
        algorithmComboBox.setSelectedIndex(0);
        generateOutputArea.setText("");
    }

    private void clearVerifyFields() {
        verifyTokenArea.setText("");
        verifySecretField.setText("your-256-bit-secret");
        verifyResultLabel.setText(" ");
        verifyResultLabel.setForeground(Color.BLACK);
        verifyDetailsArea.setText("");
    }

    private void copyParseResult() {
        String result = parseOutputArea.getText();
        if (!result.isEmpty()) {
            StringSelection selection = new StringSelection(result);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void copyGenerateResult() {
        String result = generateOutputArea.getText();
        if (!result.isEmpty()) {
            StringSelection selection = new StringSelection(result);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "JWT已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}