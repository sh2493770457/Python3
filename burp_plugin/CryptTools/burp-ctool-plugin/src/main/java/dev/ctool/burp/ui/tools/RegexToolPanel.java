package dev.ctool.burp.ui.tools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 正则匹配选项卡
    private JTextField regexField;
    private JTextArea testTextArea;
    private JTable matchTable;
    private DefaultTableModel matchTableModel;
    private JCheckBox globalCheckBox;
    private JCheckBox ignoreCaseCheckBox;
    private JCheckBox multilineCheckBox;
    private JCheckBox dotallCheckBox;
    
    // 正则替换选项卡
    private JTextField replaceRegexField;
    private JTextArea replaceInputArea;
    private JTextField replacementField;
    private JTextArea replaceOutputArea;
    private JCheckBox replaceGlobalCheckBox;
    private JCheckBox replaceIgnoreCaseCheckBox;
    private JCheckBox replaceMultilineCheckBox;
    private JCheckBox replaceDotallCheckBox;
    
    // 正则测试选项卡
    private JTextField testRegexField;
    private JTextField testStringField;
    private JLabel testResultLabel;
    private JTextArea testDetailsArea;
    private JCheckBox testIgnoreCaseCheckBox;
    private JCheckBox testMultilineCheckBox;
    private JCheckBox testDotallCheckBox;

    public RegexToolPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("正则表达式工具"));

        tabbedPane = new JTabbedPane();
        
        // 正则匹配选项卡
        JPanel matchPanel = createMatchPanel();
        tabbedPane.addTab("正则匹配", matchPanel);
        
        // 正则替换选项卡
        JPanel replacePanel = createReplacePanel();
        tabbedPane.addTab("正则替换", replacePanel);
        
        // 正则测试选项卡
        JPanel testPanel = createTestPanel();
        tabbedPane.addTab("正则测试", testPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMatchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        
        // 正则表达式输入
        JPanel regexPanel = new JPanel(new BorderLayout());
        regexPanel.setBorder(new TitledBorder("正则表达式"));
        
        regexField = new JTextField();
        regexField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        regexField.setText("\\b\\w+@\\w+\\.\\w+\\b"); // 示例：邮箱匹配
        regexPanel.add(regexField, BorderLayout.CENTER);
        
        // 选项面板
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.setBorder(new TitledBorder("匹配选项"));
        
        globalCheckBox = new JCheckBox("全局匹配", true);
        ignoreCaseCheckBox = new JCheckBox("忽略大小写");
        multilineCheckBox = new JCheckBox("多行模式");
        dotallCheckBox = new JCheckBox("单行模式");
        
        optionsPanel.add(globalCheckBox);
        optionsPanel.add(ignoreCaseCheckBox);
        optionsPanel.add(multilineCheckBox);
        optionsPanel.add(dotallCheckBox);
        
        // 测试文本输入
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(new TitledBorder("测试文本"));
        
        testTextArea = new JTextArea(6, 40);
        testTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        testTextArea.setText("联系我们：admin@example.com 或 support@test.org\n技术支持：tech@company.net");
        testTextArea.setLineWrap(true);
        testTextArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(testTextArea);
        textPanel.add(textScrollPane, BorderLayout.CENTER);
        
        inputPanel.add(regexPanel, BorderLayout.NORTH);
        inputPanel.add(optionsPanel, BorderLayout.CENTER);
        inputPanel.add(textPanel, BorderLayout.SOUTH);
        
        // 结果区域
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("匹配结果"));
        
        // 创建表格
        String[] columnNames = {"序号", "匹配内容", "起始位置", "结束位置", "长度"};
        matchTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        matchTable = new JTable(matchTableModel);
        matchTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        matchTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane tableScrollPane = new JScrollPane(matchTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 200));
        resultPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton matchButton = new JButton("执行匹配");
        matchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performMatch();
            }
        });
        
        JButton clearMatchButton = new JButton("清空结果");
        clearMatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMatchResults();
            }
        });
        
        JButton copyMatchButton = new JButton("复制匹配项");
        copyMatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedMatch();
            }
        });
        
        buttonPanel.add(matchButton);
        buttonPanel.add(clearMatchButton);
        buttonPanel.add(copyMatchButton);
        
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createReplacePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        
        // 正则表达式
        JPanel regexPanel = new JPanel(new BorderLayout());
        regexPanel.setBorder(new TitledBorder("正则表达式"));
        
        replaceRegexField = new JTextField();
        replaceRegexField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        replaceRegexField.setText("\\b(\\w+)@(\\w+)\\.(\\w+)\\b");
        regexPanel.add(replaceRegexField, BorderLayout.CENTER);
        
        // 替换字符串
        JPanel replacementPanel = new JPanel(new BorderLayout());
        replacementPanel.setBorder(new TitledBorder("替换为"));
        
        replacementField = new JTextField();
        replacementField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        replacementField.setText("[$1] at [$2] dot [$3]");
        replacementPanel.add(replacementField, BorderLayout.CENTER);
        
        // 选项面板
        JPanel replaceOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        replaceOptionsPanel.setBorder(new TitledBorder("替换选项"));
        
        replaceGlobalCheckBox = new JCheckBox("全局替换", true);
        replaceIgnoreCaseCheckBox = new JCheckBox("忽略大小写");
        replaceMultilineCheckBox = new JCheckBox("多行模式");
        replaceDotallCheckBox = new JCheckBox("单行模式");
        
        replaceOptionsPanel.add(replaceGlobalCheckBox);
        replaceOptionsPanel.add(replaceIgnoreCaseCheckBox);
        replaceOptionsPanel.add(replaceMultilineCheckBox);
        replaceOptionsPanel.add(replaceDotallCheckBox);
        
        inputPanel.add(regexPanel);
        inputPanel.add(replacementPanel);
        inputPanel.add(replaceOptionsPanel);
        
        // 文本区域
        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        
        // 输入文本
        JPanel inputTextPanel = new JPanel(new BorderLayout());
        inputTextPanel.setBorder(new TitledBorder("输入文本"));
        
        replaceInputArea = new JTextArea(10, 20);
        replaceInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        replaceInputArea.setText("联系我们：admin@example.com\n技术支持：support@test.org\n销售咨询：sales@company.net");
        replaceInputArea.setLineWrap(true);
        replaceInputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(replaceInputArea);
        inputTextPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出文本
        JPanel outputTextPanel = new JPanel(new BorderLayout());
        outputTextPanel.setBorder(new TitledBorder("替换结果"));
        
        replaceOutputArea = new JTextArea(10, 20);
        replaceOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        replaceOutputArea.setEditable(false);
        replaceOutputArea.setLineWrap(true);
        replaceOutputArea.setWrapStyleWord(true);
        JScrollPane outputScrollPane = new JScrollPane(replaceOutputArea);
        outputTextPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        textPanel.add(inputTextPanel);
        textPanel.add(outputTextPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton replaceButton = new JButton("执行替换");
        replaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performReplace();
            }
        });
        
        JButton clearReplaceButton = new JButton("清空");
        clearReplaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearReplaceFields();
            }
        });
        
        JButton copyReplaceButton = new JButton("复制结果");
        copyReplaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyReplaceResult();
            }
        });
        
        JButton swapButton = new JButton("交换输入输出");
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapReplaceText();
            }
        });
        
        buttonPanel.add(replaceButton);
        buttonPanel.add(clearReplaceButton);
        buttonPanel.add(copyReplaceButton);
        buttonPanel.add(swapButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        
        // 正则表达式
        JPanel regexPanel = new JPanel(new BorderLayout());
        regexPanel.setBorder(new TitledBorder("正则表达式"));
        
        testRegexField = new JTextField();
        testRegexField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        testRegexField.setText("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        regexPanel.add(testRegexField, BorderLayout.CENTER);
        
        // 测试字符串
        JPanel stringPanel = new JPanel(new BorderLayout());
        stringPanel.setBorder(new TitledBorder("测试字符串"));
        
        testStringField = new JTextField();
        testStringField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        testStringField.setText("test@example.com");
        stringPanel.add(testStringField, BorderLayout.CENTER);
        
        // 选项面板
        JPanel testOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        testOptionsPanel.setBorder(new TitledBorder("测试选项"));
        
        testIgnoreCaseCheckBox = new JCheckBox("忽略大小写");
        testMultilineCheckBox = new JCheckBox("多行模式");
        testDotallCheckBox = new JCheckBox("单行模式");
        
        testOptionsPanel.add(testIgnoreCaseCheckBox);
        testOptionsPanel.add(testMultilineCheckBox);
        testOptionsPanel.add(testDotallCheckBox);
        
        inputPanel.add(regexPanel);
        inputPanel.add(stringPanel);
        inputPanel.add(testOptionsPanel);
        
        // 结果区域
        JPanel resultPanel = new JPanel(new BorderLayout());
        
        // 测试结果
        testResultLabel = new JLabel(" ");
        testResultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        testResultLabel.setBorder(BorderFactory.createTitledBorder("匹配结果"));
        testResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultPanel.add(testResultLabel, BorderLayout.NORTH);
        
        // 详细信息
        testDetailsArea = new JTextArea(8, 40);
        testDetailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        testDetailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(testDetailsArea);
        detailsScrollPane.setBorder(new TitledBorder("详细信息"));
        resultPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton testButton = new JButton("执行测试");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performTest();
            }
        });
        
        JButton clearTestButton = new JButton("清空");
        clearTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTestFields();
            }
        });
        
        buttonPanel.add(testButton);
        buttonPanel.add(clearTestButton);
        
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void performMatch() {
        try {
            String regex = regexField.getText().trim();
            String text = testTextArea.getText();
            
            if (regex.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入正则表达式", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 构建Pattern标志
            int flags = 0;
            if (ignoreCaseCheckBox.isSelected()) flags |= Pattern.CASE_INSENSITIVE;
            if (multilineCheckBox.isSelected()) flags |= Pattern.MULTILINE;
            if (dotallCheckBox.isSelected()) flags |= Pattern.DOTALL;
            
            Pattern pattern = Pattern.compile(regex, flags);
            Matcher matcher = pattern.matcher(text);
            
            // 清空之前的结果
            matchTableModel.setRowCount(0);
            
            int matchCount = 0;
            if (globalCheckBox.isSelected()) {
                // 全局匹配
                while (matcher.find()) {
                    matchCount++;
                    String matchText = matcher.group();
                    int start = matcher.start();
                    int end = matcher.end();
                    int length = matchText.length();
                    
                    matchTableModel.addRow(new Object[]{matchCount, matchText, start, end, length});
                }
            } else {
                // 只匹配第一个
                if (matcher.find()) {
                    matchCount = 1;
                    String matchText = matcher.group();
                    int start = matcher.start();
                    int end = matcher.end();
                    int length = matchText.length();
                    
                    matchTableModel.addRow(new Object[]{matchCount, matchText, start, end, length});
                }
            }
            
            if (matchCount == 0) {
                JOptionPane.showMessageDialog(this, "没有找到匹配项", "匹配结果", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "找到 " + matchCount + " 个匹配项", "匹配结果", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (PatternSyntaxException e) {
            JOptionPane.showMessageDialog(this, "正则表达式语法错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "匹配失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performReplace() {
        try {
            String regex = replaceRegexField.getText().trim();
            String replacement = replacementField.getText();
            String text = replaceInputArea.getText();
            
            if (regex.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入正则表达式", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 构建Pattern标志
            int flags = 0;
            if (replaceIgnoreCaseCheckBox.isSelected()) flags |= Pattern.CASE_INSENSITIVE;
            if (replaceMultilineCheckBox.isSelected()) flags |= Pattern.MULTILINE;
            if (replaceDotallCheckBox.isSelected()) flags |= Pattern.DOTALL;
            
            Pattern pattern = Pattern.compile(regex, flags);
            Matcher matcher = pattern.matcher(text);
            
            String result;
            if (replaceGlobalCheckBox.isSelected()) {
                result = matcher.replaceAll(replacement);
            } else {
                result = matcher.replaceFirst(replacement);
            }
            
            replaceOutputArea.setText(result);
            
        } catch (PatternSyntaxException e) {
            JOptionPane.showMessageDialog(this, "正则表达式语法错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "替换失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performTest() {
        try {
            String regex = testRegexField.getText().trim();
            String testString = testStringField.getText();
            
            if (regex.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入正则表达式", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 构建Pattern标志
            int flags = 0;
            if (testIgnoreCaseCheckBox.isSelected()) flags |= Pattern.CASE_INSENSITIVE;
            if (testMultilineCheckBox.isSelected()) flags |= Pattern.MULTILINE;
            if (testDotallCheckBox.isSelected()) flags |= Pattern.DOTALL;
            
            Pattern pattern = Pattern.compile(regex, flags);
            Matcher matcher = pattern.matcher(testString);
            
            boolean matches = matcher.matches();
            
            if (matches) {
                testResultLabel.setText("✓ 匹配成功");
                testResultLabel.setForeground(new Color(0, 128, 0));
            } else {
                testResultLabel.setText("✗ 匹配失败");
                testResultLabel.setForeground(Color.RED);
            }
            
            // 显示详细信息
            StringBuilder details = new StringBuilder();
            details.append("正则表达式: ").append(regex).append("\n");
            details.append("测试字符串: ").append(testString).append("\n");
            details.append("匹配结果: ").append(matches ? "成功" : "失败").append("\n\n");
            
            // 显示所有匹配的组
            matcher.reset();
            if (matcher.find()) {
                details.append("匹配组信息:\n");
                details.append("完整匹配: ").append(matcher.group()).append("\n");
                
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String group = matcher.group(i);
                    details.append("组 ").append(i).append(": ").append(group != null ? group : "(null)").append("\n");
                }
            }
            
            testDetailsArea.setText(details.toString());
            
        } catch (PatternSyntaxException e) {
            testResultLabel.setText("✗ 语法错误");
            testResultLabel.setForeground(Color.RED);
            testDetailsArea.setText("正则表达式语法错误：\n" + e.getMessage());
        } catch (Exception e) {
            testResultLabel.setText("✗ 测试失败");
            testResultLabel.setForeground(Color.RED);
            testDetailsArea.setText("测试失败：\n" + e.getMessage());
        }
    }

    private void clearMatchResults() {
        matchTableModel.setRowCount(0);
    }

    private void clearReplaceFields() {
        replaceInputArea.setText("");
        replaceOutputArea.setText("");
    }

    private void clearTestFields() {
        testResultLabel.setText(" ");
        testResultLabel.setForeground(Color.BLACK);
        testDetailsArea.setText("");
    }

    private void copySelectedMatch() {
        int selectedRow = matchTable.getSelectedRow();
        if (selectedRow >= 0) {
            String matchText = (String) matchTableModel.getValueAt(selectedRow, 1);
            StringSelection selection = new StringSelection(matchText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "匹配项已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "请先选择一个匹配项", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void copyReplaceResult() {
        String result = replaceOutputArea.getText();
        if (!result.isEmpty()) {
            StringSelection selection = new StringSelection(result);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void swapReplaceText() {
        String input = replaceInputArea.getText();
        String output = replaceOutputArea.getText();
        
        replaceInputArea.setText(output);
        replaceOutputArea.setText(input);
    }
}