package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 随机字符生成工具面板
 * 支持生成各种类型的随机字符串
 */
public class RandomGeneratorToolPanel extends JPanel {
    
    private JSpinner lengthSpinner;
    private JSpinner countSpinner;
    private JCheckBox uppercaseCheckBox;
    private JCheckBox lowercaseCheckBox;
    private JCheckBox numbersCheckBox;
    private JCheckBox symbolsCheckBox;
    private JCheckBox customCheckBox;
    private JTextField customCharsField;
    private JTextArea resultArea;
    private JButton generateButton;
    private JButton clearButton;
    private JButton copyButton;
    
    // 字符集定义
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    private final SecureRandom random = new SecureRandom();
    
    public RandomGeneratorToolPanel() {
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建配置面板
        JPanel configPanel = createConfigPanel();
        
        // 创建结果面板
        JPanel resultPanel = createResultPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(configPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
    }
    
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("生成配置"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 长度设置
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("字符串长度:"), gbc);
        
        gbc.gridx = 1;
        lengthSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 1000, 1));
        lengthSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(lengthSpinner, gbc);
        
        // 生成数量
        gbc.gridx = 2;
        panel.add(new JLabel("生成数量:"), gbc);
        
        gbc.gridx = 3;
        countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        countSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(countSpinner, gbc);
        
        // 字符类型选择
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 4;
        JPanel charTypePanel = createCharTypePanel();
        panel.add(charTypePanel, gbc);
        
        // 自定义字符
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        customCheckBox = new JCheckBox("自定义字符:");
        panel.add(customCheckBox, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        customCharsField = new JTextField(20);
        customCharsField.setEnabled(false);
        customCharsField.setToolTipText("输入自定义字符集");
        panel.add(customCharsField, gbc);
        
        return panel;
    }
    
    private JPanel createCharTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("字符类型"));
        
        uppercaseCheckBox = new JCheckBox("大写字母 (A-Z)", true);
        lowercaseCheckBox = new JCheckBox("小写字母 (a-z)", true);
        numbersCheckBox = new JCheckBox("数字 (0-9)", true);
        symbolsCheckBox = new JCheckBox("符号", false);
        
        panel.add(uppercaseCheckBox);
        panel.add(lowercaseCheckBox);
        panel.add(numbersCheckBox);
        panel.add(symbolsCheckBox);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("生成结果"));
        
        resultArea = new JTextArea(15, 50);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        generateButton = new JButton("生成");
        clearButton = new JButton("清空");
        copyButton = new JButton("复制");
        
        generateButton.setPreferredSize(new Dimension(100, 30));
        clearButton.setPreferredSize(new Dimension(100, 30));
        copyButton.setPreferredSize(new Dimension(100, 30));
        
        panel.add(generateButton);
        panel.add(clearButton);
        panel.add(copyButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRandomStrings();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });
        
        // 自定义字符复选框事件
        customCheckBox.addActionListener(e -> {
            customCharsField.setEnabled(customCheckBox.isSelected());
            if (customCheckBox.isSelected()) {
                customCharsField.requestFocus();
            }
        });
        
        // 预设模板按钮
        addPresetButtons();
    }
    
    private void addPresetButtons() {
        // 可以在这里添加预设模板按钮
    }
    
    private void generateRandomStrings() {
        try {
            int length = (Integer) lengthSpinner.getValue();
            int count = (Integer) countSpinner.getValue();
            
            String charset = buildCharset();
            if (charset.isEmpty()) {
                resultArea.setText("请至少选择一种字符类型或输入自定义字符");
                return;
            }
            
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < count; i++) {
                String randomString = generateRandomString(charset, length);
                result.append(randomString);
                if (i < count - 1) {
                    result.append("\n");
                }
            }
            
            resultArea.setText(result.toString());
            
            // 显示统计信息
            String stats = String.format("\n\n=== 生成统计 ===\n" +
                    "字符集大小: %d\n" +
                    "字符串长度: %d\n" +
                    "生成数量: %d\n" +
                    "总字符数: %d",
                    charset.length(), length, count, length * count);
            
            resultArea.append(stats);
            
        } catch (Exception e) {
            resultArea.setText("生成失败: " + e.getMessage());
        }
    }
    
    private String buildCharset() {
        StringBuilder charset = new StringBuilder();
        
        if (uppercaseCheckBox.isSelected()) {
            charset.append(UPPERCASE);
        }
        
        if (lowercaseCheckBox.isSelected()) {
            charset.append(LOWERCASE);
        }
        
        if (numbersCheckBox.isSelected()) {
            charset.append(NUMBERS);
        }
        
        if (symbolsCheckBox.isSelected()) {
            charset.append(SYMBOLS);
        }
        
        if (customCheckBox.isSelected()) {
            String customChars = customCharsField.getText();
            if (!customChars.isEmpty()) {
                charset.append(customChars);
            }
        }
        
        // 去重
        List<Character> chars = new ArrayList<>();
        for (char c : charset.toString().toCharArray()) {
            if (!chars.contains(c)) {
                chars.add(c);
            }
        }
        
        StringBuilder result = new StringBuilder();
        for (char c : chars) {
            result.append(c);
        }
        
        return result.toString();
    }
    
    private String generateRandomString(String charset, int length) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            result.append(charset.charAt(index));
        }
        
        return result.toString();
    }
    
    private void copyToClipboard() {
        String text = resultArea.getText();
        if (!text.isEmpty()) {
            try {
                java.awt.datatransfer.StringSelection selection = 
                    new java.awt.datatransfer.StringSelection(text);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(selection, null);
                
                JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "复制失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearAll() {
        resultArea.setText("");
    }
    
    // 添加一些预设模板方法
    public void generatePassword() {
        uppercaseCheckBox.setSelected(true);
        lowercaseCheckBox.setSelected(true);
        numbersCheckBox.setSelected(true);
        symbolsCheckBox.setSelected(true);
        customCheckBox.setSelected(false);
        customCharsField.setEnabled(false);
        lengthSpinner.setValue(16);
        countSpinner.setValue(1);
        generateRandomStrings();
    }
    
    public void generateHexString() {
        uppercaseCheckBox.setSelected(false);
        lowercaseCheckBox.setSelected(false);
        numbersCheckBox.setSelected(true);
        symbolsCheckBox.setSelected(false);
        customCheckBox.setSelected(true);
        customCharsField.setText("ABCDEF");
        customCharsField.setEnabled(true);
        lengthSpinner.setValue(32);
        countSpinner.setValue(1);
        generateRandomStrings();
    }
    
    public void generateNumericCode() {
        uppercaseCheckBox.setSelected(false);
        lowercaseCheckBox.setSelected(false);
        numbersCheckBox.setSelected(true);
        symbolsCheckBox.setSelected(false);
        customCheckBox.setSelected(false);
        customCharsField.setEnabled(false);
        lengthSpinner.setValue(6);
        countSpinner.setValue(1);
        generateRandomStrings();
    }
}