package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间戳工具面板
 */
public class TimestampToolPanel extends JPanel {
    
    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    
    private JTextField timestampField;
    private JTextField datetimeField;
    private JComboBox<String> formatComboBox;
    private JComboBox<String> timezoneComboBox;
    private JComboBox<String> unitComboBox;
    private JTextArea resultArea;
    
    private final String[] dateFormats = {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy年MM月dd日 HH:mm:ss",
        "MM/dd/yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "EEE MMM dd HH:mm:ss yyyy"
    };
    
    private final String[] timezones = {
        "Asia/Shanghai",
        "UTC",
        "America/New_York",
        "Europe/London",
        "Asia/Tokyo",
        "Australia/Sydney"
    };
    
    private final String[] units = {
        "秒 (s)",
        "毫秒 (ms)",
        "微秒 (μs)",
        "纳秒 (ns)"
    };
    
    public TimestampToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        
        initializeUI();
        updateCurrentTime();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建选项面板
        JPanel optionPanel = createOptionPanel();
        
        // 创建结果面板
        JPanel resultPanel = createResultPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(optionPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("时间转换"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 时间戳输入
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("时间戳:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        timestampField = new JTextField(20);
        panel.add(timestampField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton toDateButton = new JButton("转为日期");
        toDateButton.addActionListener(e -> convertTimestampToDate());
        panel.add(toDateButton, gbc);
        
        // 日期时间输入
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("日期时间:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        datetimeField = new JTextField(20);
        panel.add(datetimeField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton toTimestampButton = new JButton("转为时间戳");
        toTimestampButton.addActionListener(e -> convertDateToTimestamp());
        panel.add(toTimestampButton, gbc);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("选项设置"));
        
        // 日期格式
        panel.add(new JLabel("日期格式:"));
        formatComboBox = new JComboBox<>(dateFormats);
        formatComboBox.setPreferredSize(new Dimension(200, 25));
        panel.add(formatComboBox);
        
        panel.add(Box.createHorizontalStrut(10));
        
        // 时区
        panel.add(new JLabel("时区:"));
        timezoneComboBox = new JComboBox<>(timezones);
        timezoneComboBox.setPreferredSize(new Dimension(150, 25));
        panel.add(timezoneComboBox);
        
        panel.add(Box.createHorizontalStrut(10));
        
        // 时间戳单位
        panel.add(new JLabel("时间戳单位:"));
        unitComboBox = new JComboBox<>(units);
        unitComboBox.setPreferredSize(new Dimension(100, 25));
        panel.add(unitComboBox);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("转换结果"));
        
        resultArea = new JTextArea(8, 50);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton currentTimeButton = new JButton("获取当前时间");
        currentTimeButton.addActionListener(e -> updateCurrentTime());
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> {
            timestampField.setText("");
            datetimeField.setText("");
            resultArea.setText("");
        });
        
        JButton copyButton = new JButton("复制结果");
        copyButton.addActionListener(e -> {
            if (!resultArea.getText().trim().isEmpty()) {
                resultArea.selectAll();
                resultArea.copy();
                JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        panel.add(currentTimeButton);
        panel.add(clearButton);
        panel.add(copyButton);
        
        return panel;
    }
    
    private void convertTimestampToDate() {
        String timestampStr = timestampField.getText().trim();
        if (timestampStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入时间戳", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            long timestamp = Long.parseLong(timestampStr);
            String unit = (String) unitComboBox.getSelectedItem();
            String timezone = (String) timezoneComboBox.getSelectedItem();
            String format = (String) formatComboBox.getSelectedItem();
            
            // 根据单位转换为毫秒
            long milliseconds;
            if (unit.contains("秒 (s)")) {
                milliseconds = timestamp * 1000;
            } else if (unit.contains("毫秒 (ms)")) {
                milliseconds = timestamp;
            } else if (unit.contains("微秒 (μs)")) {
                milliseconds = timestamp / 1000;
            } else { // 纳秒
                milliseconds = timestamp / 1000000;
            }
            
            // 转换为指定时区的日期时间
            Instant instant = Instant.ofEpochMilli(milliseconds);
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime zonedDateTime = instant.atZone(zoneId);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            String formattedDate = zonedDateTime.format(formatter);
            
            datetimeField.setText(formattedDate);
            
            // 显示详细信息
            StringBuilder result = new StringBuilder();
            result.append("时间戳转换结果:\n");
            result.append("原始时间戳: ").append(timestampStr).append(" (").append(unit).append(")\n");
            result.append("转换后日期: ").append(formattedDate).append("\n");
            result.append("时区: ").append(timezone).append("\n");
            result.append("格式: ").append(format).append("\n\n");
            
            // 显示其他格式
            result.append("其他常用格式:\n");
            for (String fmt : dateFormats) {
                if (!fmt.equals(format)) {
                    try {
                        DateTimeFormatter otherFormatter = DateTimeFormatter.ofPattern(fmt);
                        String otherFormatted = zonedDateTime.format(otherFormatter);
                        result.append(fmt).append(": ").append(otherFormatted).append("\n");
                    } catch (Exception ignored) {
                        // 忽略格式化错误
                    }
                }
            }
            
            resultArea.setText(result.toString());
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "时间戳格式错误，请输入数字", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "转换时发生错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void convertDateToTimestamp() {
        String dateStr = datetimeField.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入日期时间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String format = (String) formatComboBox.getSelectedItem();
            String timezone = (String) timezoneComboBox.getSelectedItem();
            String unit = (String) unitComboBox.getSelectedItem();
            
            // 解析日期时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            
            long milliseconds = zonedDateTime.toInstant().toEpochMilli();
            
            // 根据单位转换时间戳
            long timestamp;
            if (unit.contains("秒 (s)")) {
                timestamp = milliseconds / 1000;
            } else if (unit.contains("毫秒 (ms)")) {
                timestamp = milliseconds;
            } else if (unit.contains("微秒 (μs)")) {
                timestamp = milliseconds * 1000;
            } else { // 纳秒
                timestamp = milliseconds * 1000000;
            }
            
            timestampField.setText(String.valueOf(timestamp));
            
            // 显示详细信息
            StringBuilder result = new StringBuilder();
            result.append("日期转换结果:\n");
            result.append("原始日期: ").append(dateStr).append("\n");
            result.append("转换后时间戳: ").append(timestamp).append(" (").append(unit).append(")\n");
            result.append("时区: ").append(timezone).append("\n");
            result.append("格式: ").append(format).append("\n\n");
            
            // 显示其他单位的时间戳
            result.append("其他单位时间戳:\n");
            result.append("秒: ").append(milliseconds / 1000).append("\n");
            result.append("毫秒: ").append(milliseconds).append("\n");
            result.append("微秒: ").append(milliseconds * 1000).append("\n");
            result.append("纳秒: ").append(milliseconds * 1000000).append("\n");
            
            resultArea.setText(result.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期解析错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCurrentTime() {
        try {
            String timezone = (String) timezoneComboBox.getSelectedItem();
            String format = (String) formatComboBox.getSelectedItem();
            String unit = (String) unitComboBox.getSelectedItem();
            
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timezone));
            long milliseconds = now.toInstant().toEpochMilli();
            
            // 设置当前时间戳
            long timestamp;
            if (unit.contains("秒 (s)")) {
                timestamp = milliseconds / 1000;
            } else if (unit.contains("毫秒 (ms)")) {
                timestamp = milliseconds;
            } else if (unit.contains("微秒 (μs)")) {
                timestamp = milliseconds * 1000;
            } else { // 纳秒
                timestamp = milliseconds * 1000000;
            }
            
            timestampField.setText(String.valueOf(timestamp));
            
            // 设置当前日期时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            String formattedDate = now.format(formatter);
            datetimeField.setText(formattedDate);
            
            // 显示当前时间信息
            StringBuilder result = new StringBuilder();
            result.append("当前时间信息:\n");
            result.append("时间戳: ").append(timestamp).append(" (").append(unit).append(")\n");
            result.append("日期时间: ").append(formattedDate).append("\n");
            result.append("时区: ").append(timezone).append("\n");
            result.append("更新时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
            
            resultArea.setText(result.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "获取当前时间时发生错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}