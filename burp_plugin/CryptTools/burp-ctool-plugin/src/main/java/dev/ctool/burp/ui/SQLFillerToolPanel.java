package dev.ctool.burp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLFillerToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // SQL参数填充面板组件
    private JTextArea sqlTemplateArea;
    private JTextArea parametersArea;
    private JTextArea resultArea;
    private JComboBox<String> parameterTypeCombo;
    private JButton fillButton;
    private JButton clearButton;
    private JButton formatButton;
    
    // SQL格式化面板组件
    private JTextArea rawSqlArea;
    private JTextArea formattedSqlArea;
    private JButton formatSqlButton;
    private JButton clearFormatButton;
    private JCheckBox uppercaseKeywordsCheck;
    private JCheckBox addIndentationCheck;
    
    public SQLFillerToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // SQL参数填充组件
        sqlTemplateArea = new JTextArea(8, 50);
        sqlTemplateArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sqlTemplateArea.setText("SELECT * FROM users WHERE id = ? AND name = ? AND age > ?");
        sqlTemplateArea.setLineWrap(true);
        sqlTemplateArea.setWrapStyleWord(true);
        
        parametersArea = new JTextArea(6, 50);
        parametersArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        parametersArea.setText("1\n'张三'\n25");
        parametersArea.setLineWrap(true);
        parametersArea.setWrapStyleWord(true);
        
        resultArea = new JTextArea(8, 50);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(248, 248, 248));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        
        String[] parameterTypes = {"按行分割", "逗号分割", "分号分割", "制表符分割"};
        parameterTypeCombo = new JComboBox<>(parameterTypes);
        
        fillButton = new JButton("填充参数");
        clearButton = new JButton("清空");
        formatButton = new JButton("格式化SQL");
        
        // SQL格式化组件
        rawSqlArea = new JTextArea(10, 50);
        rawSqlArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        rawSqlArea.setText("select u.id,u.name,u.email from users u inner join orders o on u.id=o.user_id where u.status=1 and o.amount>100 order by o.created_at desc");
        rawSqlArea.setLineWrap(true);
        rawSqlArea.setWrapStyleWord(true);
        
        formattedSqlArea = new JTextArea(15, 50);
        formattedSqlArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        formattedSqlArea.setEditable(false);
        formattedSqlArea.setBackground(new Color(248, 248, 248));
        formattedSqlArea.setLineWrap(true);
        formattedSqlArea.setWrapStyleWord(true);
        
        formatSqlButton = new JButton("格式化");
        clearFormatButton = new JButton("清空");
        uppercaseKeywordsCheck = new JCheckBox("关键字大写", true);
        addIndentationCheck = new JCheckBox("添加缩进", true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // SQL参数填充面板
        JPanel fillerPanel = createFillerPanel();
        tabbedPane.addTab("SQL参数填充", fillerPanel);
        
        // SQL格式化面板
        JPanel formatterPanel = createFormatterPanel();
        tabbedPane.addTab("SQL格式化", formatterPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createFillerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("SQL参数填充"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // SQL模板
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        inputPanel.add(new JLabel("SQL模板 (使用?作为占位符):"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.4;
        inputPanel.add(new JScrollPane(sqlTemplateArea), gbc);
        
        // 参数设置
        JPanel paramPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paramPanel.add(new JLabel("参数分割方式:"));
        paramPanel.add(parameterTypeCombo);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        inputPanel.add(paramPanel, gbc);
        
        // 参数值
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHWEST;
        inputPanel.add(new JLabel("参数值 (每行一个参数或按分割方式输入):"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        inputPanel.add(new JScrollPane(parametersArea), gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(fillButton);
        buttonPanel.add(formatButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        inputPanel.add(buttonPanel, gbc);
        
        // 结果面板
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("填充结果"));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        inputPanel.add(resultPanel, gbc);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormatterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("原始SQL"));
        inputPanel.add(new JScrollPane(rawSqlArea), BorderLayout.CENTER);
        
        // 选项面板
        JPanel optionPanel = new JPanel(new FlowLayout());
        optionPanel.add(uppercaseKeywordsCheck);
        optionPanel.add(addIndentationCheck);
        optionPanel.add(formatSqlButton);
        optionPanel.add(clearFormatButton);
        
        // 结果面板
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("格式化结果"));
        resultPanel.add(new JScrollPane(formattedSqlArea), BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(optionPanel, BorderLayout.CENTER);
        panel.add(resultPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // SQL参数填充事件
        fillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillSQLParameters();
            }
        });
        
        formatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatSQL();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFiller();
            }
        });
        
        // SQL格式化事件
        formatSqlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatRawSQL();
            }
        });
        
        clearFormatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFormatter();
            }
        });
    }
    
    private void fillSQLParameters() {
        try {
            String sqlTemplate = sqlTemplateArea.getText().trim();
            String parametersText = parametersArea.getText().trim();
            
            if (sqlTemplate.isEmpty()) {
                resultArea.setText("请输入SQL模板");
                return;
            }
            
            if (parametersText.isEmpty()) {
                resultArea.setText("请输入参数值");
                return;
            }
            
            // 解析参数
            List<String> parameters = parseParameters(parametersText);
            
            // 计算占位符数量
            int placeholderCount = countPlaceholders(sqlTemplate);
            
            if (parameters.size() != placeholderCount) {
                resultArea.setText(String.format("参数数量不匹配：SQL模板需要%d个参数，但提供了%d个参数", 
                    placeholderCount, parameters.size()));
                return;
            }
            
            // 填充参数
            String result = fillParameters(sqlTemplate, parameters);
            resultArea.setText(result);
            
        } catch (Exception e) {
            resultArea.setText("填充失败: " + e.getMessage());
        }
    }
    
    private void formatSQL() {
        try {
            String result = resultArea.getText().trim();
            if (result.isEmpty()) {
                resultArea.setText("请先填充SQL参数");
                return;
            }
            
            String formatted = formatSQLString(result, true, true);
            resultArea.setText(formatted);
            
        } catch (Exception e) {
            resultArea.setText("格式化失败: " + e.getMessage());
        }
    }
    
    private void formatRawSQL() {
        try {
            String rawSQL = rawSqlArea.getText().trim();
            if (rawSQL.isEmpty()) {
                formattedSqlArea.setText("请输入SQL语句");
                return;
            }
            
            boolean uppercase = uppercaseKeywordsCheck.isSelected();
            boolean indent = addIndentationCheck.isSelected();
            
            String formatted = formatSQLString(rawSQL, uppercase, indent);
            formattedSqlArea.setText(formatted);
            
        } catch (Exception e) {
            formattedSqlArea.setText("格式化失败: " + e.getMessage());
        }
    }
    
    private List<String> parseParameters(String parametersText) {
        List<String> parameters = new ArrayList<>();
        String separator = (String) parameterTypeCombo.getSelectedItem();
        
        switch (separator) {
            case "按行分割":
                String[] lines = parametersText.split("\\r?\\n");
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        parameters.add(line.trim());
                    }
                }
                break;
            case "逗号分割":
                String[] commaParts = parametersText.split(",");
                for (String part : commaParts) {
                    parameters.add(part.trim());
                }
                break;
            case "分号分割":
                String[] semicolonParts = parametersText.split(";");
                for (String part : semicolonParts) {
                    parameters.add(part.trim());
                }
                break;
            case "制表符分割":
                String[] tabParts = parametersText.split("\\t");
                for (String part : tabParts) {
                    parameters.add(part.trim());
                }
                break;
        }
        
        return parameters;
    }
    
    private int countPlaceholders(String sql) {
        int count = 0;
        for (int i = 0; i < sql.length(); i++) {
            if (sql.charAt(i) == '?') {
                count++;
            }
        }
        return count;
    }
    
    private String fillParameters(String sqlTemplate, List<String> parameters) {
        String result = sqlTemplate;
        for (String parameter : parameters) {
            int index = result.indexOf('?');
            if (index != -1) {
                result = result.substring(0, index) + parameter + result.substring(index + 1);
            }
        }
        return result;
    }
    
    private String formatSQLString(String sql, boolean uppercaseKeywords, boolean addIndentation) {
        String result = sql;
        
        // 移除多余的空白字符
        result = result.replaceAll("\\s+", " ").trim();
        
        if (uppercaseKeywords) {
            // 将SQL关键字转换为大写
            String[] keywords = {
                "SELECT", "FROM", "WHERE", "AND", "OR", "NOT", "IN", "EXISTS",
                "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE",
                "CREATE", "TABLE", "ALTER", "DROP", "INDEX", "VIEW",
                "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER", "ON",
                "GROUP", "BY", "HAVING", "ORDER", "ASC", "DESC",
                "UNION", "ALL", "DISTINCT", "AS", "CASE", "WHEN", "THEN", "ELSE", "END",
                "NULL", "IS", "LIKE", "BETWEEN", "LIMIT", "OFFSET"
            };
            
            for (String keyword : keywords) {
                Pattern pattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
                result = pattern.matcher(result).replaceAll(keyword);
            }
        }
        
        if (addIndentation) {
            // 添加换行和缩进
            result = result.replaceAll("\\bSELECT\\b", "\nSELECT\n    ");
            result = result.replaceAll("\\bFROM\\b", "\nFROM\n    ");
            result = result.replaceAll("\\bWHERE\\b", "\nWHERE\n    ");
            result = result.replaceAll("\\bAND\\b", "\n    AND ");
            result = result.replaceAll("\\bOR\\b", "\n    OR ");
            result = result.replaceAll("\\bJOIN\\b", "\nJOIN\n    ");
            result = result.replaceAll("\\bINNER JOIN\\b", "\nINNER JOIN\n    ");
            result = result.replaceAll("\\bLEFT JOIN\\b", "\nLEFT JOIN\n    ");
            result = result.replaceAll("\\bRIGHT JOIN\\b", "\nRIGHT JOIN\n    ");
            result = result.replaceAll("\\bON\\b", "\n    ON ");
            result = result.replaceAll("\\bGROUP BY\\b", "\nGROUP BY\n    ");
            result = result.replaceAll("\\bHAVING\\b", "\nHAVING\n    ");
            result = result.replaceAll("\\bORDER BY\\b", "\nORDER BY\n    ");
            result = result.replaceAll("\\bUNION\\b", "\nUNION\n");
            result = result.replaceAll("\\bUNION ALL\\b", "\nUNION ALL\n");
            
            // 清理多余的换行
            result = result.replaceAll("\\n+", "\n").trim();
        }
        
        return result;
    }
    
    private void clearFiller() {
        sqlTemplateArea.setText("SELECT * FROM users WHERE id = ? AND name = ? AND age > ?");
        parametersArea.setText("1\n'张三'\n25");
        resultArea.setText("");
        parameterTypeCombo.setSelectedIndex(0);
    }
    
    private void clearFormatter() {
        rawSqlArea.setText("select u.id,u.name,u.email from users u inner join orders o on u.id=o.user_id where u.status=1 and o.amount>100 order by o.created_at desc");
        formattedSqlArea.setText("");
        uppercaseKeywordsCheck.setSelected(true);
        addIndentationCheck.setSelected(true);
    }
}