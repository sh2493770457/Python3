package dev.ctool.burp.ui.tools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 文本转换选项卡
    private JTextArea convertInputArea;
    private JTextArea convertOutputArea;
    private JComboBox<String> convertTypeComboBox;
    
    // 文本处理选项卡
    private JTextArea processInputArea;
    private JTextArea processOutputArea;
    private JComboBox<String> processTypeComboBox;
    private JCheckBox preserveOrderCheckBox;
    private JCheckBox caseSensitiveCheckBox;
    
    // 文本统计选项卡
    private JTextArea statsInputArea;
    private JTextArea statsOutputArea;
    
    // 文本分割合并选项卡
    private JTextArea splitInputArea;
    private JTextField separatorField;
    private JTextArea splitOutputArea;
    private JComboBox<String> splitModeComboBox;

    public TextToolPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("文本处理工具"));

        tabbedPane = new JTabbedPane();
        
        // 文本转换选项卡
        JPanel convertPanel = createConvertPanel();
        tabbedPane.addTab("文本转换", convertPanel);
        
        // 文本处理选项卡
        JPanel processPanel = createProcessPanel();
        tabbedPane.addTab("文本处理", processPanel);
        
        // 文本统计选项卡
        JPanel statsPanel = createStatsPanel();
        tabbedPane.addTab("文本统计", statsPanel);
        
        // 文本分割合并选项卡
        JPanel splitPanel = createSplitPanel();
        tabbedPane.addTab("分割合并", splitPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createConvertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(new TitledBorder("转换类型"));
        
        convertTypeComboBox = new JComboBox<>(new String[]{
            "转为大写", "转为小写", "首字母大写", "单词首字母大写", 
            "反转大小写", "驼峰转下划线", "下划线转驼峰", "反转字符串"
        });
        controlPanel.add(new JLabel("转换类型:"));
        controlPanel.add(convertTypeComboBox);
        
        // 文本区域
        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入文本"));
        
        convertInputArea = new JTextArea(15, 25);
        convertInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        convertInputArea.setText("Hello World!\nthis_is_a_test\ncamelCaseExample");
        convertInputArea.setLineWrap(true);
        convertInputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(convertInputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("转换结果"));
        
        convertOutputArea = new JTextArea(15, 25);
        convertOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        convertOutputArea.setEditable(false);
        convertOutputArea.setLineWrap(true);
        convertOutputArea.setWrapStyleWord(true);
        JScrollPane outputScrollPane = new JScrollPane(convertOutputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        textPanel.add(inputPanel);
        textPanel.add(outputPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton convertButton = new JButton("执行转换");
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConvert();
            }
        });
        
        JButton clearConvertButton = new JButton("清空");
        clearConvertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearConvertFields();
            }
        });
        
        JButton copyConvertButton = new JButton("复制结果");
        copyConvertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyConvertResult();
            }
        });
        
        JButton swapConvertButton = new JButton("交换输入输出");
        swapConvertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapConvertText();
            }
        });
        
        buttonPanel.add(convertButton);
        buttonPanel.add(clearConvertButton);
        buttonPanel.add(copyConvertButton);
        buttonPanel.add(swapConvertButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createProcessPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(new TitledBorder("处理选项"));
        
        processTypeComboBox = new JComboBox<>(new String[]{
            "去除重复行", "排序(升序)", "排序(降序)", "随机排序", 
            "去除空行", "去除首尾空格", "添加行号", "反转行顺序"
        });
        controlPanel.add(new JLabel("处理类型:"));
        controlPanel.add(processTypeComboBox);
        
        preserveOrderCheckBox = new JCheckBox("保持原顺序");
        caseSensitiveCheckBox = new JCheckBox("区分大小写", true);
        
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(preserveOrderCheckBox);
        controlPanel.add(caseSensitiveCheckBox);
        
        // 文本区域
        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入文本"));
        
        processInputArea = new JTextArea(15, 25);
        processInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        processInputArea.setText("apple\nbanana\napple\ncherry\n\nbanana\ndate");
        processInputArea.setLineWrap(true);
        processInputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(processInputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("处理结果"));
        
        processOutputArea = new JTextArea(15, 25);
        processOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        processOutputArea.setEditable(false);
        processOutputArea.setLineWrap(true);
        processOutputArea.setWrapStyleWord(true);
        JScrollPane outputScrollPane = new JScrollPane(processOutputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        textPanel.add(inputPanel);
        textPanel.add(outputPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton processButton = new JButton("执行处理");
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performProcess();
            }
        });
        
        JButton clearProcessButton = new JButton("清空");
        clearProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearProcessFields();
            }
        });
        
        JButton copyProcessButton = new JButton("复制结果");
        copyProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyProcessResult();
            }
        });
        
        JButton swapProcessButton = new JButton("交换输入输出");
        swapProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapProcessText();
            }
        });
        
        buttonPanel.add(processButton);
        buttonPanel.add(clearProcessButton);
        buttonPanel.add(copyProcessButton);
        buttonPanel.add(swapProcessButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入文本"));
        
        statsInputArea = new JTextArea(8, 40);
        statsInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        statsInputArea.setText("Hello World!\nThis is a sample text for statistics.\n统计测试文本。");
        statsInputArea.setLineWrap(true);
        statsInputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(statsInputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("统计结果"));
        
        statsOutputArea = new JTextArea(12, 40);
        statsOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        statsOutputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(statsOutputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton statsButton = new JButton("统计分析");
        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performStats();
            }
        });
        
        JButton clearStatsButton = new JButton("清空");
        clearStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearStatsFields();
            }
        });
        
        JButton copyStatsButton = new JButton("复制结果");
        copyStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyStatsResult();
            }
        });
        
        buttonPanel.add(statsButton);
        buttonPanel.add(clearStatsButton);
        buttonPanel.add(copyStatsButton);
        
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(outputPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createSplitPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(new TitledBorder("分割合并选项"));
        
        splitModeComboBox = new JComboBox<>(new String[]{
            "按分隔符分割", "按行合并", "按逗号合并", "按空格合并", "按自定义分隔符合并"
        });
        controlPanel.add(new JLabel("模式:"));
        controlPanel.add(splitModeComboBox);
        
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(new JLabel("分隔符:"));
        separatorField = new JTextField(10);
        separatorField.setText(",");
        controlPanel.add(separatorField);
        
        // 文本区域
        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入文本"));
        
        splitInputArea = new JTextArea(15, 25);
        splitInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        splitInputArea.setText("apple,banana,cherry,date");
        splitInputArea.setLineWrap(true);
        splitInputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(splitInputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("处理结果"));
        
        splitOutputArea = new JTextArea(15, 25);
        splitOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        splitOutputArea.setEditable(false);
        splitOutputArea.setLineWrap(true);
        splitOutputArea.setWrapStyleWord(true);
        JScrollPane outputScrollPane = new JScrollPane(splitOutputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        textPanel.add(inputPanel);
        textPanel.add(outputPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton splitButton = new JButton("执行处理");
        splitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSplit();
            }
        });
        
        JButton clearSplitButton = new JButton("清空");
        clearSplitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSplitFields();
            }
        });
        
        JButton copySplitButton = new JButton("复制结果");
        copySplitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySplitResult();
            }
        });
        
        JButton swapSplitButton = new JButton("交换输入输出");
        swapSplitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapSplitText();
            }
        });
        
        buttonPanel.add(splitButton);
        buttonPanel.add(clearSplitButton);
        buttonPanel.add(copySplitButton);
        buttonPanel.add(swapSplitButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void performConvert() {
        try {
            String input = convertInputArea.getText();
            String convertType = (String) convertTypeComboBox.getSelectedItem();
            String result = "";
            
            switch (convertType) {
                case "转为大写":
                    result = input.toUpperCase();
                    break;
                case "转为小写":
                    result = input.toLowerCase();
                    break;
                case "首字母大写":
                    result = input.isEmpty() ? "" : input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
                    break;
                case "单词首字母大写":
                    result = capitalizeWords(input);
                    break;
                case "反转大小写":
                    result = toggleCase(input);
                    break;
                case "驼峰转下划线":
                    result = camelToUnderscore(input);
                    break;
                case "下划线转驼峰":
                    result = underscoreToCamel(input);
                    break;
                case "反转字符串":
                    result = new StringBuilder(input).reverse().toString();
                    break;
            }
            
            convertOutputArea.setText(result);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "转换失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performProcess() {
        try {
            String input = processInputArea.getText();
            String processType = (String) processTypeComboBox.getSelectedItem();
            String[] lines = input.split("\n");
            List<String> lineList = new ArrayList<>(Arrays.asList(lines));
            
            switch (processType) {
                case "去除重复行":
                    if (preserveOrderCheckBox.isSelected()) {
                        lineList = removeDuplicatesPreserveOrder(lineList, caseSensitiveCheckBox.isSelected());
                    } else {
                        Set<String> uniqueLines = caseSensitiveCheckBox.isSelected() ? 
                            new LinkedHashSet<>(lineList) : 
                            lineList.stream().collect(Collectors.toCollection(
                                () -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)
                            ));
                        lineList = new ArrayList<>(uniqueLines);
                    }
                    break;
                case "排序(升序)":
                    if (caseSensitiveCheckBox.isSelected()) {
                        Collections.sort(lineList);
                    } else {
                        lineList.sort(String.CASE_INSENSITIVE_ORDER);
                    }
                    break;
                case "排序(降序)":
                    if (caseSensitiveCheckBox.isSelected()) {
                        lineList.sort(Collections.reverseOrder());
                    } else {
                        lineList.sort(String.CASE_INSENSITIVE_ORDER.reversed());
                    }
                    break;
                case "随机排序":
                    Collections.shuffle(lineList);
                    break;
                case "去除空行":
                    lineList = lineList.stream().filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());
                    break;
                case "去除首尾空格":
                    lineList = lineList.stream().map(String::trim).collect(Collectors.toList());
                    break;
                case "添加行号":
                    for (int i = 0; i < lineList.size(); i++) {
                        lineList.set(i, (i + 1) + ". " + lineList.get(i));
                    }
                    break;
                case "反转行顺序":
                    Collections.reverse(lineList);
                    break;
            }
            
            processOutputArea.setText(String.join("\n", lineList));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "处理失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performStats() {
        try {
            String input = statsInputArea.getText();
            
            // 基本统计
            int totalChars = input.length();
            int totalCharsNoSpaces = input.replaceAll("\\s", "").length();
            int totalLines = input.split("\n").length;
            int totalWords = input.trim().isEmpty() ? 0 : input.trim().split("\\s+").length;
            int totalParagraphs = input.split("\n\\s*\n").length;
            
            // 字符统计
            Map<Character, Integer> charCount = new HashMap<>();
            for (char c : input.toCharArray()) {
                charCount.put(c, charCount.getOrDefault(c, 0) + 1);
            }
            
            // 单词统计
            Map<String, Integer> wordCount = new HashMap<>();
            if (!input.trim().isEmpty()) {
                String[] words = input.toLowerCase().replaceAll("[^\\w\\s]", "").split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                    }
                }
            }
            
            StringBuilder result = new StringBuilder();
            result.append("文本统计结果\n");
            result.append("=============\n\n");
            
            result.append("基本统计:\n");
            result.append("总字符数: ").append(totalChars).append("\n");
            result.append("字符数(不含空格): ").append(totalCharsNoSpaces).append("\n");
            result.append("总行数: ").append(totalLines).append("\n");
            result.append("总单词数: ").append(totalWords).append("\n");
            result.append("总段落数: ").append(totalParagraphs).append("\n\n");
            
            // 最常用字符（前10个）
            result.append("最常用字符 (前10个):\n");
            charCount.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    char c = entry.getKey();
                    String charDisplay = c == '\n' ? "\\n" : c == '\t' ? "\\t" : c == ' ' ? "空格" : String.valueOf(c);
                    result.append("  ").append(charDisplay).append(": ").append(entry.getValue()).append("\n");
                });
            
            result.append("\n");
            
            // 最常用单词（前10个）
            if (!wordCount.isEmpty()) {
                result.append("最常用单词 (前10个):\n");
                wordCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> result.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
            }
            
            statsOutputArea.setText(result.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "统计失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSplit() {
        try {
            String input = splitInputArea.getText();
            String mode = (String) splitModeComboBox.getSelectedItem();
            String separator = separatorField.getText();
            String result = "";
            
            switch (mode) {
                case "按分隔符分割":
                    if (separator.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "请输入分隔符", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String[] parts = input.split(Pattern.quote(separator));
                    result = String.join("\n", parts);
                    break;
                case "按行合并":
                    String[] lines = input.split("\n");
                    result = String.join("", lines);
                    break;
                case "按逗号合并":
                    lines = input.split("\n");
                    result = String.join(",", lines);
                    break;
                case "按空格合并":
                    lines = input.split("\n");
                    result = String.join(" ", lines);
                    break;
                case "按自定义分隔符合并":
                    if (separator.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "请输入分隔符", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    lines = input.split("\n");
                    result = String.join(separator, lines);
                    break;
            }
            
            splitOutputArea.setText(result);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "处理失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 辅助方法
    private String capitalizeWords(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }

    private String toggleCase(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                result.append(Character.toUpperCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String camelToUnderscore(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    private String underscoreToCamel(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : input.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }

    private List<String> removeDuplicatesPreserveOrder(List<String> list, boolean caseSensitive) {
        Set<String> seen = caseSensitive ? new HashSet<>() : new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        List<String> result = new ArrayList<>();
        
        for (String item : list) {
            if (seen.add(item)) {
                result.add(item);
            }
        }
        
        return result;
    }

    // 清空和复制方法
    private void clearConvertFields() {
        convertInputArea.setText("");
        convertOutputArea.setText("");
    }

    private void clearProcessFields() {
        processInputArea.setText("");
        processOutputArea.setText("");
    }

    private void clearStatsFields() {
        statsInputArea.setText("");
        statsOutputArea.setText("");
    }

    private void clearSplitFields() {
        splitInputArea.setText("");
        splitOutputArea.setText("");
    }

    private void copyConvertResult() {
        copyToClipboard(convertOutputArea.getText());
    }

    private void copyProcessResult() {
        copyToClipboard(processOutputArea.getText());
    }

    private void copyStatsResult() {
        copyToClipboard(statsOutputArea.getText());
    }

    private void copySplitResult() {
        copyToClipboard(splitOutputArea.getText());
    }

    private void swapConvertText() {
        String input = convertInputArea.getText();
        String output = convertOutputArea.getText();
        convertInputArea.setText(output);
        convertOutputArea.setText(input);
    }

    private void swapProcessText() {
        String input = processInputArea.getText();
        String output = processOutputArea.getText();
        processInputArea.setText(output);
        processOutputArea.setText(input);
    }

    private void swapSplitText() {
        String input = splitInputArea.getText();
        String output = splitOutputArea.getText();
        splitInputArea.setText(output);
        splitOutputArea.setText(input);
    }

    private void copyToClipboard(String text) {
        if (!text.isEmpty()) {
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}