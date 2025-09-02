package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ComplementCodeToolPanel extends JPanel {
    private JTextField decimalField;
    private JTextField binaryField;
    private JTextField sourceCodeField;
    private JTextField inverseCodeField;
    private JTextField complementCodeField;
    private JComboBox<String> bitWidthCombo;
    private JButton convertButton;
    private JButton clearButton;
    private JTextArea explanationArea;
    
    public ComplementCodeToolPanel() {
        initComponents();
        layoutComponents();
        addEventListeners();
    }
    
    private void initComponents() {
        // 输入字段
        decimalField = new JTextField(20);
        binaryField = new JTextField(20);
        sourceCodeField = new JTextField(20);
        inverseCodeField = new JTextField(20);
        complementCodeField = new JTextField(20);
        
        // 位宽选择
        String[] bitWidths = {"8位", "16位", "32位"};
        bitWidthCombo = new JComboBox<>(bitWidths);
        bitWidthCombo.setSelectedIndex(0); // 默认8位
        
        // 按钮
        convertButton = new JButton("转换");
        clearButton = new JButton("清空");
        
        // 说明区域
        explanationArea = new JTextArea(10, 50);
        explanationArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        explanationArea.setEditable(false);
        explanationArea.setBackground(new Color(248, 248, 248));
        explanationArea.setText("源码反码补码说明:\n" +
                "\n" +
                "1. 源码（原码）：直接将数值转换为二进制表示\n" +
                "   - 正数：最高位为0，其余位表示数值\n" +
                "   - 负数：最高位为1，其余位表示数值的绝对值\n" +
                "\n" +
                "2. 反码（一补数）：\n" +
                "   - 正数：与源码相同\n" +
                "   - 负数：符号位不变，其余位按位取反\n" +
                "\n" +
                "3. 补码（二补数）：\n" +
                "   - 正数：与源码相同\n" +
                "   - 负数：反码加1\n" +
                "\n" +
                "计算机内部使用补码进行运算，可以统一加减法运算。");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("数值输入"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 十进制输入
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("十进制:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(decimalField, gbc);
        
        // 二进制输入
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("二进制:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(binaryField, gbc);
        
        // 位宽选择
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("位宽:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(bitWidthCombo, gbc);
        
        // 按钮
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(convertButton);
        buttonPanel.add(clearButton);
        inputPanel.add(buttonPanel, gbc);
        
        // 结果面板
        JPanel resultPanel = new JPanel(new GridBagLayout());
        resultPanel.setBorder(new TitledBorder("转换结果"));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 源码
        gbc.gridx = 0; gbc.gridy = 0;
        resultPanel.add(new JLabel("源码:"), gbc);
        gbc.gridx = 1;
        sourceCodeField.setEditable(false);
        sourceCodeField.setBackground(new Color(248, 248, 248));
        resultPanel.add(sourceCodeField, gbc);
        
        // 反码
        gbc.gridx = 0; gbc.gridy = 1;
        resultPanel.add(new JLabel("反码:"), gbc);
        gbc.gridx = 1;
        inverseCodeField.setEditable(false);
        inverseCodeField.setBackground(new Color(248, 248, 248));
        resultPanel.add(inverseCodeField, gbc);
        
        // 补码
        gbc.gridx = 0; gbc.gridy = 2;
        resultPanel.add(new JLabel("补码:"), gbc);
        gbc.gridx = 1;
        complementCodeField.setEditable(false);
        complementCodeField.setBackground(new Color(248, 248, 248));
        resultPanel.add(complementCodeField, gbc);
        
        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.WEST);
        topPanel.add(resultPanel, BorderLayout.CENTER);
        
        // 说明面板
        JPanel explanationPanel = new JPanel(new BorderLayout());
        explanationPanel.setBorder(new TitledBorder("说明"));
        explanationPanel.add(new JScrollPane(explanationArea), BorderLayout.CENTER);
        
        // 主分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, explanationPanel);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.4);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void addEventListeners() {
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convert();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        // 十进制输入框监听
        decimalField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convert();
            }
        });
        
        // 二进制输入框监听
        binaryField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convert();
            }
        });
    }
    
    private void convert() {
        try {
            int bitWidth = getBitWidth();
            int value = 0;
            boolean hasInput = false;
            
            // 从十进制输入获取值
            String decimalText = decimalField.getText().trim();
            if (!decimalText.isEmpty()) {
                value = Integer.parseInt(decimalText);
                hasInput = true;
                // 更新二进制显示
                binaryField.setText(Integer.toBinaryString(value));
            }
            // 从二进制输入获取值
            else {
                String binaryText = binaryField.getText().trim();
                if (!binaryText.isEmpty()) {
                    // 移除可能的空格和前缀
                    binaryText = binaryText.replaceAll("\\s+", "").replace("0b", "");
                    value = Integer.parseInt(binaryText, 2);
                    hasInput = true;
                    // 更新十进制显示
                    decimalField.setText(String.valueOf(value));
                }
            }
            
            if (!hasInput) {
                JOptionPane.showMessageDialog(this, "请输入十进制数值或二进制数值", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 检查数值范围
            int maxValue = (1 << (bitWidth - 1)) - 1;
            int minValue = -(1 << (bitWidth - 1));
            
            if (value > maxValue || value < minValue) {
                JOptionPane.showMessageDialog(this, 
                    String.format("数值超出%d位范围 [%d, %d]", bitWidth, minValue, maxValue), 
                    "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 计算源码、反码、补码
            String sourceCode = calculateSourceCode(value, bitWidth);
            String inverseCode = calculateInverseCode(value, bitWidth);
            String complementCode = calculateComplementCode(value, bitWidth);
            
            // 显示结果
            sourceCodeField.setText(sourceCode);
            inverseCodeField.setText(inverseCode);
            complementCodeField.setText(complementCode);
            
            // 更新说明
            updateExplanation(value, bitWidth, sourceCode, inverseCode, complementCode);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "输入格式错误，请输入有效的数值", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "转换失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getBitWidth() {
        String selected = (String) bitWidthCombo.getSelectedItem();
        switch (selected) {
            case "8位": return 8;
            case "16位": return 16;
            case "32位": return 32;
            default: return 8;
        }
    }
    
    private String calculateSourceCode(int value, int bitWidth) {
        if (value >= 0) {
            // 正数：直接转换为二进制，补足位宽
            String binary = Integer.toBinaryString(value);
            return padBinary(binary, bitWidth);
        } else {
            // 负数：符号位为1，其余位表示绝对值
            String binary = Integer.toBinaryString(Math.abs(value));
            binary = padBinary(binary, bitWidth - 1);
            return "1" + binary;
        }
    }
    
    private String calculateInverseCode(int value, int bitWidth) {
        if (value >= 0) {
            // 正数：与源码相同
            return calculateSourceCode(value, bitWidth);
        } else {
            // 负数：符号位不变，其余位按位取反
            String sourceCode = calculateSourceCode(value, bitWidth);
            StringBuilder inverse = new StringBuilder();
            inverse.append('1'); // 符号位保持1
            
            for (int i = 1; i < sourceCode.length(); i++) {
                char bit = sourceCode.charAt(i);
                inverse.append(bit == '0' ? '1' : '0');
            }
            return inverse.toString();
        }
    }
    
    private String calculateComplementCode(int value, int bitWidth) {
        if (value >= 0) {
            // 正数：与源码相同
            return calculateSourceCode(value, bitWidth);
        } else {
            // 负数：反码加1
            String inverseCode = calculateInverseCode(value, bitWidth);
            
            // 二进制加1
            StringBuilder complement = new StringBuilder();
            int carry = 1;
            
            for (int i = inverseCode.length() - 1; i >= 0; i--) {
                int bit = Character.getNumericValue(inverseCode.charAt(i));
                int sum = bit + carry;
                complement.insert(0, sum % 2);
                carry = sum / 2;
            }
            
            return complement.toString();
        }
    }
    
    private String padBinary(String binary, int width) {
        StringBuilder sb = new StringBuilder();
        for (int i = binary.length(); i < width; i++) {
            sb.append('0');
        }
        sb.append(binary);
        return sb.toString();
    }
    
    private void updateExplanation(int value, int bitWidth, String sourceCode, String inverseCode, String complementCode) {
        StringBuilder explanation = new StringBuilder();
        explanation.append("转换过程详解:\n");
        explanation.append("================\n\n");
        
        explanation.append("输入数值: ").append(value).append("\n");
        explanation.append("位宽: ").append(bitWidth).append("位\n\n");
        
        if (value >= 0) {
            explanation.append("正数转换:\n");
            explanation.append("1. 源码 = 反码 = 补码\n");
            explanation.append("2. 直接将十进制转换为二进制，最高位为0\n");
            explanation.append("3. 结果: ").append(sourceCode).append("\n\n");
        } else {
            explanation.append("负数转换:\n");
            explanation.append("1. 源码: 符号位为1，其余位表示绝对值\n");
            explanation.append("   绝对值: ").append(Math.abs(value)).append(" -> ").append(Integer.toBinaryString(Math.abs(value))).append("\n");
            explanation.append("   源码: ").append(sourceCode).append("\n\n");
            
            explanation.append("2. 反码: 符号位不变，其余位按位取反\n");
            explanation.append("   反码: ").append(inverseCode).append("\n\n");
            
            explanation.append("3. 补码: 反码加1\n");
            explanation.append("   补码: ").append(complementCode).append("\n\n");
        }
        
        explanation.append("验证:\n");
        explanation.append("补码转十进制: ");
        int complementValue = Integer.parseInt(complementCode, 2);
        if (complementCode.charAt(0) == '1' && value < 0) {
            // 负数补码转换
            int mask = (1 << bitWidth) - 1;
            complementValue = complementValue - (1 << bitWidth);
        }
        explanation.append(complementValue).append("\n");
        
        explanationArea.setText(explanation.toString());
    }
    
    private void clearAll() {
        decimalField.setText("");
        binaryField.setText("");
        sourceCodeField.setText("");
        inverseCodeField.setText("");
        complementCodeField.setText("");
        
        explanationArea.setText("源码反码补码说明:\n" +
                "\n" +
                "1. 源码（原码）：直接将数值转换为二进制表示\n" +
                "   - 正数：最高位为0，其余位表示数值\n" +
                "   - 负数：最高位为1，其余位表示数值的绝对值\n" +
                "\n" +
                "2. 反码（一补数）：\n" +
                "   - 正数：与源码相同\n" +
                "   - 负数：符号位不变，其余位按位取反\n" +
                "\n" +
                "3. 补码（二补数）：\n" +
                "   - 正数：与源码相同\n" +
                "   - 负数：反码加1\n" +
                "\n" +
                "计算机内部使用补码进行运算，可以统一加减法运算。");
    }
}