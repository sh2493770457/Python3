package dev.ctool.burp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

public class RadixConverterToolPanel extends JPanel {
    private JTextField inputField;
    private JComboBox<String> fromRadixCombo;
    private JComboBox<String> toRadixCombo;
    private JTextArea outputArea;
    private JButton convertButton;
    private JButton clearButton;
    private JButton swapButton;
    
    public RadixConverterToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        // 输入组件
        inputField = new JTextField(20);
        inputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // 进制选择组件
        String[] radixOptions = new String[35];
        for (int i = 2; i <= 36; i++) {
            radixOptions[i-2] = String.valueOf(i);
        }
        
        fromRadixCombo = new JComboBox<>(radixOptions);
        fromRadixCombo.setSelectedItem("10"); // 默认十进制
        
        toRadixCombo = new JComboBox<>(radixOptions);
        toRadixCombo.setSelectedItem("16"); // 默认转换为十六进制
        
        // 输出组件
        outputArea = new JTextArea(10, 40);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        
        // 按钮组件
        convertButton = new JButton("转换");
        clearButton = new JButton("清空");
        swapButton = new JButton("交换进制");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("数值:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(inputField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("源进制:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(fromRadixCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("目标进制:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(toRadixCombo, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(convertButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(clearButton);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("转换结果"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        // 主布局
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(outputPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapRadix();
            }
        });
        
        // 回车键触发转换
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });
    }
    
    private void performConversion() {
        try {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                outputArea.setText("请输入要转换的数值");
                return;
            }
            
            int fromRadix = Integer.parseInt((String) fromRadixCombo.getSelectedItem());
            int toRadix = Integer.parseInt((String) toRadixCombo.getSelectedItem());
            
            // 验证输入是否符合源进制
            if (!isValidForRadix(input, fromRadix)) {
                outputArea.setText("输入的数值不符合 " + fromRadix + " 进制格式");
                return;
            }
            
            // 转换为十进制
            BigInteger decimal = new BigInteger(input.toUpperCase(), fromRadix);
            
            // 转换为目标进制
            String result = decimal.toString(toRadix).toUpperCase();
            
            // 显示详细结果
            StringBuilder output = new StringBuilder();
            output.append("转换结果:\n");
            output.append("源数值: ").append(input.toUpperCase()).append(" (").append(fromRadix).append("进制)\n");
            output.append("目标数值: ").append(result).append(" (").append(toRadix).append("进制)\n");
            output.append("十进制值: ").append(decimal.toString()).append("\n\n");
            
            // 显示常用进制转换
            output.append("常用进制转换:\n");
            output.append("二进制: ").append(decimal.toString(2)).append("\n");
            output.append("八进制: ").append(decimal.toString(8)).append("\n");
            output.append("十进制: ").append(decimal.toString(10)).append("\n");
            output.append("十六进制: ").append(decimal.toString(16).toUpperCase()).append("\n");
            
            outputArea.setText(output.toString());
            
        } catch (NumberFormatException e) {
            outputArea.setText("转换失败: 输入格式错误\n" + e.getMessage());
        } catch (Exception e) {
            outputArea.setText("转换失败: " + e.getMessage());
        }
    }
    
    private boolean isValidForRadix(String input, int radix) {
        try {
            new BigInteger(input.toUpperCase(), radix);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void swapRadix() {
        String fromRadix = (String) fromRadixCombo.getSelectedItem();
        String toRadix = (String) toRadixCombo.getSelectedItem();
        
        fromRadixCombo.setSelectedItem(toRadix);
        toRadixCombo.setSelectedItem(fromRadix);
        
        // 如果有输入，自动转换
        if (!inputField.getText().trim().isEmpty()) {
            performConversion();
        }
    }
    
    private void clearAll() {
        inputField.setText("");
        outputArea.setText("");
        fromRadixCombo.setSelectedItem("10");
        toRadixCombo.setSelectedItem("16");
    }
}