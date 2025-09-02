package dev.ctool.burp.ui.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class BinaryToolPanel extends JPanel {
    private JTextArea inputTextArea;
    private JTextArea trueFormTextArea;
    private JTextArea inverseTextArea;
    private JTextArea complementTextArea;
    private JComboBox<String> lengthComboBox;
    
    // 支持的位长度
    private static final List<Integer> LENGTH_OPTIONS = Arrays.asList(8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096);
    
    public BinaryToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        // 输入区域
        inputTextArea = new JTextArea(8, 40);
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        
        // 输出区域
        trueFormTextArea = new JTextArea(6, 40);
        trueFormTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        trueFormTextArea.setEditable(false);
        trueFormTextArea.setBackground(new Color(248, 248, 248));
        trueFormTextArea.setLineWrap(true);
        trueFormTextArea.setWrapStyleWord(true);
        
        inverseTextArea = new JTextArea(6, 40);
        inverseTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inverseTextArea.setEditable(false);
        inverseTextArea.setBackground(new Color(248, 248, 248));
        inverseTextArea.setLineWrap(true);
        inverseTextArea.setWrapStyleWord(true);
        
        complementTextArea = new JTextArea(6, 40);
        complementTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        complementTextArea.setEditable(false);
        complementTextArea.setBackground(new Color(248, 248, 248));
        complementTextArea.setLineWrap(true);
        complementTextArea.setWrapStyleWord(true);
        
        // 位长度选择
        String[] lengthOptions = LENGTH_OPTIONS.stream()
                .map(length -> length + " 位")
                .toArray(String[]::new);
        lengthComboBox = new JComboBox<>(lengthOptions);
        lengthComboBox.setSelectedIndex(0); // 默认选择8位
        
        // 按钮
        JButton convertButton = new JButton("转换");
        convertButton.addActionListener(e -> performConversion());
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> clearAll());
        
        JButton copyTrueFormButton = new JButton("复制原码");
        copyTrueFormButton.addActionListener(e -> copyToClipboard(trueFormTextArea.getText()));
        
        JButton copyInverseButton = new JButton("复制反码");
        copyInverseButton.addActionListener(e -> copyToClipboard(inverseTextArea.getText()));
        
        JButton copyComplementButton = new JButton("复制补码");
        copyComplementButton.addActionListener(e -> copyToClipboard(complementTextArea.getText()));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部面板 - 输入区域
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("输入数字（支持正负整数，每行一个）"));
        
        JPanel inputControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inputControlPanel.add(new JLabel("位长度:"));
        inputControlPanel.add(lengthComboBox);
        
        JButton convertButton = new JButton("转换");
        convertButton.addActionListener(e -> performConversion());
        inputControlPanel.add(convertButton);
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> clearAll());
        inputControlPanel.add(clearButton);
        
        topPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);
        topPanel.add(inputControlPanel, BorderLayout.SOUTH);
        
        // 中间面板 - 输出区域
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // 原码面板
        JPanel trueFormPanel = new JPanel(new BorderLayout());
        trueFormPanel.setBorder(BorderFactory.createTitledBorder("原码"));
        JPanel trueFormButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyTrueFormButton = new JButton("复制");
        copyTrueFormButton.addActionListener(e -> copyToClipboard(trueFormTextArea.getText()));
        trueFormButtonPanel.add(copyTrueFormButton);
        trueFormPanel.add(new JScrollPane(trueFormTextArea), BorderLayout.CENTER);
        trueFormPanel.add(trueFormButtonPanel, BorderLayout.SOUTH);
        
        // 反码面板
        JPanel inversePanel = new JPanel(new BorderLayout());
        inversePanel.setBorder(BorderFactory.createTitledBorder("反码"));
        JPanel inverseButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyInverseButton = new JButton("复制");
        copyInverseButton.addActionListener(e -> copyToClipboard(inverseTextArea.getText()));
        inverseButtonPanel.add(copyInverseButton);
        inversePanel.add(new JScrollPane(inverseTextArea), BorderLayout.CENTER);
        inversePanel.add(inverseButtonPanel, BorderLayout.SOUTH);
        
        // 补码面板
        JPanel complementPanel = new JPanel(new BorderLayout());
        complementPanel.setBorder(BorderFactory.createTitledBorder("补码"));
        JPanel complementButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyComplementButton = new JButton("复制");
        copyComplementButton.addActionListener(e -> copyToClipboard(complementTextArea.getText()));
        complementButtonPanel.add(copyComplementButton);
        complementPanel.add(new JScrollPane(complementTextArea), BorderLayout.CENTER);
        complementPanel.add(complementButtonPanel, BorderLayout.SOUTH);
        
        centerPanel.add(trueFormPanel);
        centerPanel.add(inversePanel);
        centerPanel.add(complementPanel);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        // 实时转换
        inputTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> performConversion());
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> performConversion());
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> performConversion());
            }
        });
        
        // 位长度变化时重新转换
        lengthComboBox.addActionListener(e -> performConversion());
    }
    
    private void performConversion() {
        String input = inputTextArea.getText().trim();
        if (input.isEmpty()) {
            clearOutputs();
            return;
        }
        
        int selectedLength = LENGTH_OPTIONS.get(lengthComboBox.getSelectedIndex());
        
        StringBuilder trueFormResult = new StringBuilder();
        StringBuilder inverseResult = new StringBuilder();
        StringBuilder complementResult = new StringBuilder();
        
        String[] lines = input.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            try {
                BinaryTransform transform = new BinaryTransform(line, selectedLength);
                
                if (trueFormResult.length() > 0) {
                    trueFormResult.append("\n");
                    inverseResult.append("\n");
                    complementResult.append("\n");
                }
                
                trueFormResult.append(transform.getTrueForm());
                inverseResult.append(transform.getInverse());
                complementResult.append(transform.getComplement());
                
            } catch (Exception e) {
                if (trueFormResult.length() > 0) {
                    trueFormResult.append("\n");
                    inverseResult.append("\n");
                    complementResult.append("\n");
                }
                
                String errorMsg = "错误: " + e.getMessage();
                trueFormResult.append(errorMsg);
                inverseResult.append(errorMsg);
                complementResult.append(errorMsg);
            }
        }
        
        trueFormTextArea.setText(trueFormResult.toString());
        inverseTextArea.setText(inverseResult.toString());
        complementTextArea.setText(complementResult.toString());
    }
    
    private void clearAll() {
        inputTextArea.setText("");
        clearOutputs();
    }
    
    private void clearOutputs() {
        trueFormTextArea.setText("");
        inverseTextArea.setText("");
        complementTextArea.setText("");
    }
    
    private void copyToClipboard(String text) {
        if (text != null && !text.trim().isEmpty()) {
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(text);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // 二进制转换核心类
    private static class BinaryTransform {
        private final String input;
        private final int length;
        private final BigInteger number;
        private final boolean isNegative;
        
        public BinaryTransform(String input, int length) throws Exception {
            if (!input.matches("^[0-9+-]+$")) {
                throw new Exception("输入格式错误，只支持数字和正负号");
            }
            
            if (!LENGTH_OPTIONS.contains(length)) {
                throw new Exception("不支持的位长度");
            }
            
            this.input = input;
            this.length = length;
            this.isNegative = input.startsWith("-");
            
            String numberStr = input.startsWith("+") || input.startsWith("-") ? input.substring(1) : input;
            this.number = new BigInteger((isNegative ? "-" : "") + numberStr);
            
            checkRange();
        }
        
        private void checkRange() throws Exception {
            if (number.equals(BigInteger.ZERO)) {
                return;
            }
            
            BigInteger max = BigInteger.valueOf(2).pow(length - 1).subtract(BigInteger.ONE);
            BigInteger min = BigInteger.valueOf(2).pow(length - 1).negate();
            
            if (number.compareTo(max) > 0 || number.compareTo(min) < 0) {
                throw new Exception(String.format("超出%d位范围: %s ~ %s", length, min, max));
            }
        }
        
        public String getTrueForm() {
            BigInteger absNumber = number.abs();
            String binary = absNumber.toString(2);
            
            if (!isNegative) {
                return padBinary(binary, length, '0');
            } else {
                return padBinary(binary, length, '1');
            }
        }
        
        public String getInverse() {
            String trueForm = getTrueForm();
            if (!isNegative) {
                return trueForm;
            }
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < length; i++) {
                char bit = trueForm.charAt(i);
                if (i == 0) {
                    result.append(bit); // 符号位保持不变
                } else {
                    result.append(bit == '0' ? '1' : '0'); // 其他位取反
                }
            }
            return result.toString();
        }
        
        public String getComplement() {
            String trueForm = getTrueForm();
            if (!isNegative) {
                return trueForm;
            }
            
            String inverse = getInverse();
            BigInteger inverseValue = new BigInteger(inverse, 2);
            BigInteger complement = inverseValue.add(BigInteger.ONE);
            
            String result = complement.toString(2);
            return padBinary(result, length, '1');
        }
        
        private String padBinary(String binary, int length, char padChar) {
            if (binary.length() >= length) {
                return binary.substring(binary.length() - length);
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(padChar);
            for (int i = 0; i < length - 1 - binary.length(); i++) {
                sb.append('0');
            }
            sb.append(binary);
            return sb.toString();
        }
    }
}