package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import dev.ctool.burp.utils.CryptoUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * AES加密工具面板
 */
public class AESToolPanel extends JPanel {
    
    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextField keyField;
    private JTextField ivField;
    private JComboBox<String> modeComboBox;
    private JComboBox<String> paddingComboBox;
    private JComboBox<String> keySizeComboBox;
    private JRadioButton encryptRadio;
    private JRadioButton decryptRadio;
    
    public AESToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建配置面板
        JPanel configPanel = createConfigPanel();
        
        // 创建输出面板
        JPanel outputPanel = createOutputPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(configPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输入内容"));
        
        inputArea = new JTextArea(6, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("AES配置"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 操作类型
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("操作:"), gbc);
        
        encryptRadio = new JRadioButton("加密", true);
        decryptRadio = new JRadioButton("解密", false);
        ButtonGroup operationGroup = new ButtonGroup();
        operationGroup.add(encryptRadio);
        operationGroup.add(decryptRadio);
        
        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        operationPanel.add(encryptRadio);
        operationPanel.add(decryptRadio);
        
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(operationPanel, gbc);
        
        // 密钥长度
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("密钥长度:"), gbc);
        
        keySizeComboBox = new JComboBox<>(new String[]{"128", "192", "256"});
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(keySizeComboBox, gbc);
        
        // 加密模式
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("模式:"), gbc);
        
        modeComboBox = new JComboBox<>(new String[]{"CBC", "ECB", "CFB", "OFB", "CTR"});
        gbc.gridx = 3; gbc.gridy = 1;
        panel.add(modeComboBox, gbc);
        
        // 填充方式
        gbc.gridx = 4; gbc.gridy = 1;
        panel.add(new JLabel("填充:"), gbc);
        
        paddingComboBox = new JComboBox<>(new String[]{"PKCS5Padding", "NoPadding"});
        gbc.gridx = 5; gbc.gridy = 1;
        panel.add(paddingComboBox, gbc);
        
        // 密钥
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("密钥:"), gbc);
        
        keyField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(keyField, gbc);
        
        JButton generateKeyButton = new JButton("生成密钥");
        generateKeyButton.addActionListener(e -> {
            int keySize = Integer.parseInt((String) keySizeComboBox.getSelectedItem());
            String randomKey = CryptoUtils.generateRandomKey(keySize / 8);
            keyField.setText(randomKey);
        });
        gbc.gridx = 4; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(generateKeyButton, gbc);
        
        // IV向量
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("IV向量:"), gbc);
        
        ivField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3;
        panel.add(ivField, gbc);
        
        JButton generateIVButton = new JButton("生成IV");
        generateIVButton.addActionListener(e -> {
            String randomIV = CryptoUtils.generateRandomIV(16);
            ivField.setText(randomIV);
        });
        gbc.gridx = 4; gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(generateIVButton, gbc);
        
        // 模式变化监听
        modeComboBox.addActionListener(e -> {
            boolean needIV = !"ECB".equals(modeComboBox.getSelectedItem());
            ivField.setEnabled(needIV);
            generateIVButton.setEnabled(needIV);
        });
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输出结果"));
        
        outputArea = new JTextArea(8, 50);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 添加复制按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyButton = new JButton("复制结果");
        copyButton.addActionListener(e -> {
            if (!outputArea.getText().trim().isEmpty()) {
                outputArea.selectAll();
                outputArea.copy();
                JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(copyButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton processButton = new JButton("执行");
        processButton.addActionListener(new ProcessActionListener());
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });
        
        panel.add(processButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private class ProcessActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputArea.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(AESToolPanel.this, "请输入要处理的内容", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String key = keyField.getText().trim();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(AESToolPanel.this, "请输入密钥", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String mode = (String) modeComboBox.getSelectedItem();
            String padding = (String) paddingComboBox.getSelectedItem();
            String iv = ivField.getText().trim();
            
            // 检查IV
            if (!"ECB".equals(mode) && iv.isEmpty()) {
                JOptionPane.showMessageDialog(AESToolPanel.this, "当前模式需要IV向量", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // 调整密钥长度
                int keySize = Integer.parseInt((String) keySizeComboBox.getSelectedItem());
                String adjustedKey = CryptoUtils.adjustKeyLength(key, keySize / 8);
                
                String result;
                if (encryptRadio.isSelected()) {
                    result = CryptoUtils.aesEncrypt(input, adjustedKey, mode, padding, iv);
                } else {
                    result = CryptoUtils.aesDecrypt(input, adjustedKey, mode, padding, iv);
                }
                
                outputArea.setText(result);
                
            } catch (Exception ex) {
                String operation = encryptRadio.isSelected() ? "加密" : "解密";
                JOptionPane.showMessageDialog(AESToolPanel.this, 
                    "AES" + operation + "时发生错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
                outputArea.setText("错误: " + ex.getMessage());
            }
        }
    }
}