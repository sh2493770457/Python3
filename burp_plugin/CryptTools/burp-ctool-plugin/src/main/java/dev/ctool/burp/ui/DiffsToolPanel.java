package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DiffsToolPanel extends JPanel {
    private JTextArea originalTextArea;
    private JTextArea modifiedTextArea;
    private JTextArea resultTextArea;
    private JButton compareButton;
    private JButton clearButton;
    private JLabel statusLabel;
    
    public DiffsToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        // 原始文本输入区域
        originalTextArea = new JTextArea(15, 40);
        originalTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        originalTextArea.setLineWrap(true);
        originalTextArea.setWrapStyleWord(true);
        
        // 修改后文本输入区域
        modifiedTextArea = new JTextArea(15, 40);
        modifiedTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        modifiedTextArea.setLineWrap(true);
        modifiedTextArea.setWrapStyleWord(true);
        
        // 结果显示区域
        resultTextArea = new JTextArea(15, 80);
        resultTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultTextArea.setEditable(false);
        resultTextArea.setBackground(new Color(248, 248, 248));
        
        // 按钮
        compareButton = new JButton("比较文本");
        clearButton = new JButton("清空");
        
        // 状态标签
        statusLabel = new JLabel("准备就绪");
        statusLabel.setForeground(Color.BLUE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // 原始文本面板
        JPanel originalPanel = new JPanel(new BorderLayout());
        originalPanel.setBorder(new TitledBorder("原始文本"));
        originalPanel.add(new JScrollPane(originalTextArea), BorderLayout.CENTER);
        
        // 修改后文本面板
        JPanel modifiedPanel = new JPanel(new BorderLayout());
        modifiedPanel.setBorder(new TitledBorder("修改后文本"));
        modifiedPanel.add(new JScrollPane(modifiedTextArea), BorderLayout.CENTER);
        
        inputPanel.add(originalPanel);
        inputPanel.add(modifiedPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(compareButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(statusLabel);
        
        // 结果面板
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("差异结果"));
        resultPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
        
        // 主面板布局
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        compareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareTexts();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }
    
    private void compareTexts() {
        try {
            String originalText = originalTextArea.getText();
            String modifiedText = modifiedTextArea.getText();
            
            if (originalText.isEmpty() && modifiedText.isEmpty()) {
                statusLabel.setText("请输入要比较的文本");
                statusLabel.setForeground(Color.RED);
                return;
            }
            
            // 执行文本差异比较
            String diffResult = performDiff(originalText, modifiedText);
            resultTextArea.setText(diffResult);
            
            statusLabel.setText("比较完成");
            statusLabel.setForeground(Color.GREEN);
            
        } catch (Exception ex) {
            statusLabel.setText("比较失败: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            resultTextArea.setText("错误: " + ex.getMessage());
        }
    }
    
    private String performDiff(String original, String modified) {
        // 简单的行级差异比较实现
        String[] originalLines = original.split("\\n");
        String[] modifiedLines = modified.split("\\n");
        
        StringBuilder result = new StringBuilder();
        result.append("=== 文本差异比较结果 ===\n\n");
        
        // 计算基本统计信息
        result.append("原始文本行数: ").append(originalLines.length).append("\n");
        result.append("修改后文本行数: ").append(modifiedLines.length).append("\n\n");
        
        // 执行简单的LCS（最长公共子序列）算法进行差异比较
        List<DiffLine> diffLines = computeDiff(originalLines, modifiedLines);
        
        // 格式化输出差异结果
        int lineNumber = 1;
        for (DiffLine diffLine : diffLines) {
            switch (diffLine.type) {
                case EQUAL:
                    result.append(String.format("%4d   %s\n", lineNumber++, diffLine.content));
                    break;
                case DELETE:
                    result.append(String.format("%4s - %s\n", "-", diffLine.content));
                    break;
                case INSERT:
                    result.append(String.format("%4s + %s\n", "+", diffLine.content));
                    lineNumber++;
                    break;
            }
        }
        
        return result.toString();
    }
    
    private List<DiffLine> computeDiff(String[] original, String[] modified) {
        List<DiffLine> result = new ArrayList<>();
        
        // 简化的差异算法：逐行比较
        int i = 0, j = 0;
        while (i < original.length || j < modified.length) {
            if (i >= original.length) {
                // 原始文本已结束，剩余的都是新增
                result.add(new DiffLine(DiffType.INSERT, modified[j]));
                j++;
            } else if (j >= modified.length) {
                // 修改后文本已结束，剩余的都是删除
                result.add(new DiffLine(DiffType.DELETE, original[i]));
                i++;
            } else if (original[i].equals(modified[j])) {
                // 相同行
                result.add(new DiffLine(DiffType.EQUAL, original[i]));
                i++;
                j++;
            } else {
                // 不同行，查找下一个匹配点
                boolean found = false;
                
                // 在接下来的几行中查找匹配
                for (int k = 1; k <= Math.min(5, Math.min(original.length - i, modified.length - j)); k++) {
                    if (i + k < original.length && original[i + k].equals(modified[j])) {
                        // 原始文本中的接下来几行被删除
                        for (int l = 0; l < k; l++) {
                            result.add(new DiffLine(DiffType.DELETE, original[i + l]));
                        }
                        i += k;
                        found = true;
                        break;
                    } else if (j + k < modified.length && original[i].equals(modified[j + k])) {
                        // 修改后文本中的接下来几行是新增的
                        for (int l = 0; l < k; l++) {
                            result.add(new DiffLine(DiffType.INSERT, modified[j + l]));
                        }
                        j += k;
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    // 没找到匹配，当作替换处理
                    result.add(new DiffLine(DiffType.DELETE, original[i]));
                    result.add(new DiffLine(DiffType.INSERT, modified[j]));
                    i++;
                    j++;
                }
            }
        }
        
        return result;
    }
    
    private void clearAll() {
        originalTextArea.setText("");
        modifiedTextArea.setText("");
        resultTextArea.setText("");
        statusLabel.setText("已清空");
        statusLabel.setForeground(Color.BLUE);
    }
    
    // 差异行类型枚举
    private enum DiffType {
        EQUAL, DELETE, INSERT
    }
    
    // 差异行数据类
    private static class DiffLine {
        DiffType type;
        String content;
        
        DiffLine(DiffType type, String content) {
            this.type = type;
            this.content = content;
        }
    }
}