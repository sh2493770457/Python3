package dev.ctool.burp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

public class TimeCalculatorToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 时间计算面板组件
    private JTextField dateTimeField1;
    private JTextField dateTimeField2;
    private JComboBox<String> operationCombo;
    private JTextField resultField;
    private JButton calculateButton;
    private JButton clearButton;
    
    // 时区转换面板组件
    private JTextField sourceTimeField;
    private JComboBox<String> sourceTimezoneCombo;
    private JComboBox<String> targetTimezoneCombo;
    private JTextField convertedTimeField;
    private JButton convertButton;
    private JButton clearTimezoneButton;
    
    // 时间戳转换面板组件
    private JTextField timestampField;
    private JTextField dateTimeField;
    private JButton toDateTimeButton;
    private JButton toTimestampButton;
    private JButton clearTimestampButton;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public TimeCalculatorToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // 时间计算组件
        dateTimeField1 = new JTextField(20);
        dateTimeField1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        dateTimeField1.setText(LocalDateTime.now().format(formatter));
        
        dateTimeField2 = new JTextField(20);
        dateTimeField2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        dateTimeField2.setText(LocalDateTime.now().plusDays(1).format(formatter));
        
        String[] operations = {"时间差计算", "时间加法", "时间减法"};
        operationCombo = new JComboBox<>(operations);
        
        resultField = new JTextField(30);
        resultField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultField.setEditable(false);
        resultField.setBackground(new Color(248, 248, 248));
        
        calculateButton = new JButton("计算");
        clearButton = new JButton("清空");
        
        // 时区转换组件
        sourceTimeField = new JTextField(20);
        sourceTimeField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sourceTimeField.setText(LocalDateTime.now().format(formatter));
        
        String[] timezones = {
            "Asia/Shanghai", "UTC", "America/New_York", "America/Los_Angeles",
            "Europe/London", "Europe/Paris", "Asia/Tokyo", "Asia/Seoul",
            "Australia/Sydney", "America/Chicago", "Asia/Kolkata", "Europe/Berlin"
        };
        
        sourceTimezoneCombo = new JComboBox<>(timezones);
        targetTimezoneCombo = new JComboBox<>(timezones);
        targetTimezoneCombo.setSelectedItem("UTC");
        
        convertedTimeField = new JTextField(20);
        convertedTimeField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        convertedTimeField.setEditable(false);
        convertedTimeField.setBackground(new Color(248, 248, 248));
        
        convertButton = new JButton("转换");
        clearTimezoneButton = new JButton("清空");
        
        // 时间戳转换组件
        timestampField = new JTextField(20);
        timestampField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        timestampField.setText(String.valueOf(System.currentTimeMillis()));
        
        dateTimeField = new JTextField(20);
        dateTimeField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        dateTimeField.setText(LocalDateTime.now().format(formatter));
        
        toDateTimeButton = new JButton("时间戳→日期时间");
        toTimestampButton = new JButton("日期时间→时间戳");
        clearTimestampButton = new JButton("清空");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 时间计算面板
        JPanel calculatorPanel = createCalculatorPanel();
        tabbedPane.addTab("时间计算", calculatorPanel);
        
        // 时区转换面板
        JPanel timezonePanel = createTimezonePanel();
        tabbedPane.addTab("时区转换", timezonePanel);
        
        // 时间戳转换面板
        JPanel timestampPanel = createTimestampPanel();
        tabbedPane.addTab("时间戳转换", timestampPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createCalculatorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("时间计算"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("时间1:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(dateTimeField1, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("操作:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(operationCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("时间2:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(dateTimeField2, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("结果:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(resultField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTimezonePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("时区转换"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("源时间:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(sourceTimeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("源时区:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(sourceTimezoneCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("目标时区:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(targetTimezoneCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("转换结果:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(convertedTimeField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(convertButton);
        buttonPanel.add(clearTimezoneButton);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTimestampPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("时间戳转换"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("时间戳(毫秒):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(timestampField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("日期时间:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(dateTimeField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(toDateTimeButton);
        buttonPanel.add(toTimestampButton);
        buttonPanel.add(clearTimestampButton);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // 时间计算事件
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performTimeCalculation();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCalculator();
            }
        });
        
        // 时区转换事件
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performTimezoneConversion();
            }
        });
        
        clearTimezoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTimezone();
            }
        });
        
        // 时间戳转换事件
        toDateTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timestampToDateTime();
            }
        });
        
        toTimestampButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateTimeToTimestamp();
            }
        });
        
        clearTimestampButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTimestamp();
            }
        });
    }
    
    private void performTimeCalculation() {
        try {
            String dateTime1Str = dateTimeField1.getText().trim();
            String dateTime2Str = dateTimeField2.getText().trim();
            String operation = (String) operationCombo.getSelectedItem();
            
            if (dateTime1Str.isEmpty() || dateTime2Str.isEmpty()) {
                resultField.setText("请输入时间");
                return;
            }
            
            LocalDateTime dateTime1 = LocalDateTime.parse(dateTime1Str, formatter);
            LocalDateTime dateTime2 = LocalDateTime.parse(dateTime2Str, formatter);
            
            String result;
            switch (operation) {
                case "时间差计算":
                    result = calculateTimeDifference(dateTime1, dateTime2);
                    break;
                case "时间加法":
                    result = "请在时间2字段输入要增加的时间量（如：1天2小时30分钟）";
                    break;
                case "时间减法":
                    result = "请在时间2字段输入要减少的时间量（如：1天2小时30分钟）";
                    break;
                default:
                    result = "不支持的操作";
            }
            
            resultField.setText(result);
            
        } catch (DateTimeParseException e) {
            resultField.setText("时间格式错误，请使用格式：yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            resultField.setText("计算失败: " + e.getMessage());
        }
    }
    
    private String calculateTimeDifference(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        Duration duration = Duration.between(dateTime1, dateTime2);
        
        long totalSeconds = Math.abs(duration.getSeconds());
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        StringBuilder result = new StringBuilder();
        if (duration.isNegative()) {
            result.append("时间1晚于时间2，相差：");
        } else {
            result.append("时间2晚于时间1，相差：");
        }
        
        if (days > 0) result.append(days).append("天");
        if (hours > 0) result.append(hours).append("小时");
        if (minutes > 0) result.append(minutes).append("分钟");
        if (seconds > 0) result.append(seconds).append("秒");
        
        result.append("\n总计：").append(Math.abs(duration.toDays())).append("天")
              .append(" 或 ").append(Math.abs(duration.toHours())).append("小时")
              .append(" 或 ").append(Math.abs(duration.toMinutes())).append("分钟")
              .append(" 或 ").append(Math.abs(duration.getSeconds())).append("秒");
        
        return result.toString();
    }
    
    private void performTimezoneConversion() {
        try {
            String sourceTimeStr = sourceTimeField.getText().trim();
            if (sourceTimeStr.isEmpty()) {
                convertedTimeField.setText("请输入源时间");
                return;
            }
            
            String sourceTimezone = (String) sourceTimezoneCombo.getSelectedItem();
            String targetTimezone = (String) targetTimezoneCombo.getSelectedItem();
            
            LocalDateTime localDateTime = LocalDateTime.parse(sourceTimeStr, formatter);
            ZonedDateTime sourceZonedDateTime = localDateTime.atZone(ZoneId.of(sourceTimezone));
            ZonedDateTime targetZonedDateTime = sourceZonedDateTime.withZoneSameInstant(ZoneId.of(targetTimezone));
            
            String result = targetZonedDateTime.format(formatter) + " (" + targetTimezone + ")";
            convertedTimeField.setText(result);
            
        } catch (DateTimeParseException e) {
            convertedTimeField.setText("时间格式错误，请使用格式：yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            convertedTimeField.setText("转换失败: " + e.getMessage());
        }
    }
    
    private void timestampToDateTime() {
        try {
            String timestampStr = timestampField.getText().trim();
            if (timestampStr.isEmpty()) {
                dateTimeField.setText("请输入时间戳");
                return;
            }
            
            long timestamp = Long.parseLong(timestampStr);
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            
            dateTimeField.setText(dateTime.format(formatter));
            
        } catch (NumberFormatException e) {
            dateTimeField.setText("时间戳格式错误");
        } catch (Exception e) {
            dateTimeField.setText("转换失败: " + e.getMessage());
        }
    }
    
    private void dateTimeToTimestamp() {
        try {
            String dateTimeStr = dateTimeField.getText().trim();
            if (dateTimeStr.isEmpty()) {
                timestampField.setText("请输入日期时间");
                return;
            }
            
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
            long timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            
            timestampField.setText(String.valueOf(timestamp));
            
        } catch (DateTimeParseException e) {
            timestampField.setText("时间格式错误，请使用格式：yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            timestampField.setText("转换失败: " + e.getMessage());
        }
    }
    
    private void clearCalculator() {
        dateTimeField1.setText(LocalDateTime.now().format(formatter));
        dateTimeField2.setText(LocalDateTime.now().plusDays(1).format(formatter));
        resultField.setText("");
        operationCombo.setSelectedIndex(0);
    }
    
    private void clearTimezone() {
        sourceTimeField.setText(LocalDateTime.now().format(formatter));
        convertedTimeField.setText("");
        sourceTimezoneCombo.setSelectedIndex(0);
        targetTimezoneCombo.setSelectedItem("UTC");
    }
    
    private void clearTimestamp() {
        timestampField.setText(String.valueOf(System.currentTimeMillis()));
        dateTimeField.setText(LocalDateTime.now().format(formatter));
    }
}