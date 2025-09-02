package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 变量名工具面板
 * 支持各种命名风格的转换
 */
public class VariableNameToolPanel extends JPanel {
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> fromStyleCombo;
    private JComboBox<String> toStyleCombo;
    private JButton convertButton;
    private JButton clearButton;
    private JButton swapButton;
    private JCheckBox batchModeCheckBox;
    
    // 命名风格
    private static final String[] NAMING_STYLES = {
        "camelCase (驼峰命名)",
        "PascalCase (帕斯卡命名)", 
        "snake_case (下划线命名)",
        "kebab-case (短横线命名)",
        "UPPER_SNAKE_CASE (大写下划线)",
        "lower case (小写空格)",
        "UPPER CASE (大写空格)",
        "Title Case (标题格式)",
        "dot.case (点分命名)",
        "path/case (路径命名)"
    };
    
    public VariableNameToolPanel() {
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建选项面板
        JPanel optionPanel = createOptionPanel();
        
        // 创建输出面板
        JPanel outputPanel = createOutputPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(optionPanel, BorderLayout.SOUTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(outputPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输入变量名"));
        
        inputArea = new JTextArea(8, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setText("myVariableName\nuser_name\nUserProfile\nmy-component\nMAX_SIZE");
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("转换选项"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 源格式
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("源格式:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        fromStyleCombo = new JComboBox<>(NAMING_STYLES);
        fromStyleCombo.setSelectedIndex(0); // 默认camelCase
        panel.add(fromStyleCombo, gbc);
        
        // 目标格式
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("目标格式:"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 0;
        toStyleCombo = new JComboBox<>(NAMING_STYLES);
        toStyleCombo.setSelectedIndex(2); // 默认snake_case
        panel.add(toStyleCombo, gbc);
        
        // 批量模式
        gbc.gridx = 4; gbc.gridy = 0;
        batchModeCheckBox = new JCheckBox("批量模式", true);
        panel.add(batchModeCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("转换结果"));
        
        outputArea = new JTextArea(8, 50);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        convertButton = new JButton("转换");
        swapButton = new JButton("交换格式");
        clearButton = new JButton("清空");
        
        convertButton.setPreferredSize(new Dimension(100, 30));
        swapButton.setPreferredSize(new Dimension(100, 30));
        clearButton.setPreferredSize(new Dimension(100, 30));
        
        panel.add(convertButton);
        panel.add(swapButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertVariableNames();
            }
        });
        
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapStyles();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }
    
    private void convertVariableNames() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            outputArea.setText("");
            return;
        }
        
        try {
            int fromIndex = fromStyleCombo.getSelectedIndex();
            int toIndex = toStyleCombo.getSelectedIndex();
            
            if (batchModeCheckBox.isSelected()) {
                // 批量模式：每行一个变量名
                String[] lines = input.split("\\n");
                StringBuilder result = new StringBuilder();
                
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        String converted = convertNamingStyle(line, fromIndex, toIndex);
                        result.append(converted).append("\n");
                    }
                }
                
                outputArea.setText(result.toString().trim());
            } else {
                // 单个模式
                String result = convertNamingStyle(input, fromIndex, toIndex);
                outputArea.setText(result);
            }
        } catch (Exception e) {
            outputArea.setText("转换出错: " + e.getMessage());
        }
    }
    
    private String convertNamingStyle(String input, int fromStyle, int toStyle) {
        // 首先将输入转换为单词列表
        List<String> words = parseWords(input, fromStyle);
        
        if (words.isEmpty()) {
            return input;
        }
        
        // 然后根据目标格式组合单词
        return formatWords(words, toStyle);
    }
    
    private List<String> parseWords(String input, int style) {
        List<String> words = new ArrayList<>();
        
        switch (style) {
            case 0: // camelCase
            case 1: // PascalCase
                words = parseCamelCase(input);
                break;
            case 2: // snake_case
            case 4: // UPPER_SNAKE_CASE
                words = Arrays.asList(input.split("_"));
                break;
            case 3: // kebab-case
                words = Arrays.asList(input.split("-"));
                break;
            case 5: // lower case
            case 6: // UPPER CASE
            case 7: // Title Case
                words = Arrays.asList(input.split("\\s+"));
                break;
            case 8: // dot.case
                words = Arrays.asList(input.split("\\."));
                break;
            case 9: // path/case
                words = Arrays.asList(input.split("/"));
                break;
            default:
                words.add(input);
        }
        
        // 清理空字符串和转换为小写
        List<String> cleanWords = new ArrayList<>();
        for (String word : words) {
            word = word.trim().toLowerCase();
            if (!word.isEmpty()) {
                cleanWords.add(word);
            }
        }
        
        return cleanWords;
    }
    
    private List<String> parseCamelCase(String input) {
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (Character.isUpperCase(c) && currentWord.length() > 0) {
                words.add(currentWord.toString());
                currentWord = new StringBuilder();
            }
            
            currentWord.append(Character.toLowerCase(c));
        }
        
        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
        
        return words;
    }
    
    private String formatWords(List<String> words, int style) {
        if (words.isEmpty()) {
            return "";
        }
        
        switch (style) {
            case 0: // camelCase
                return formatCamelCase(words, false);
            case 1: // PascalCase
                return formatCamelCase(words, true);
            case 2: // snake_case
                return String.join("_", words);
            case 3: // kebab-case
                return String.join("-", words);
            case 4: // UPPER_SNAKE_CASE
                return String.join("_", words).toUpperCase();
            case 5: // lower case
                return String.join(" ", words);
            case 6: // UPPER CASE
                return String.join(" ", words).toUpperCase();
            case 7: // Title Case
                return formatTitleCase(words);
            case 8: // dot.case
                return String.join(".", words);
            case 9: // path/case
                return String.join("/", words);
            default:
                return String.join("_", words);
        }
    }
    
    private String formatCamelCase(List<String> words, boolean pascalCase) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (i == 0 && !pascalCase) {
                result.append(word.toLowerCase());
            } else {
                result.append(capitalize(word));
            }
        }
        
        return result.toString();
    }
    
    private String formatTitleCase(List<String> words) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.size(); i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(capitalize(words.get(i)));
        }
        
        return result.toString();
    }
    
    private String capitalize(String word) {
        if (word.isEmpty()) {
            return word;
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
    }
    
    private void swapStyles() {
        int fromIndex = fromStyleCombo.getSelectedIndex();
        int toIndex = toStyleCombo.getSelectedIndex();
        
        fromStyleCombo.setSelectedIndex(toIndex);
        toStyleCombo.setSelectedIndex(fromIndex);
        
        // 自动转换
        convertVariableNames();
    }
    
    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
    }
}