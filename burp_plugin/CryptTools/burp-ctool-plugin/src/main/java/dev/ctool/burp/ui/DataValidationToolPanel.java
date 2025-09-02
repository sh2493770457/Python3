package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class DataValidationToolPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea bccHexArea, bccDecArea, bccOctArea, bccBinArea;
    private JTextArea crcHexArea, crcDecArea, crcOctArea, crcBinArea;
    private JTextArea lrcHexArea, lrcDecArea, lrcOctArea, lrcBinArea;
    private JComboBox<String> inputFormatComboBox;
    
    public DataValidationToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // 输入区域
        inputArea = new JTextArea(5, 50);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setBorder(BorderFactory.createTitledBorder("输入数据"));
        
        // 输入格式选择
        inputFormatComboBox = new JComboBox<>(new String[]{"Hex", "Text", "Base64"});
        inputFormatComboBox.setSelectedItem("Hex");
        
        // BCC结果区域
        bccHexArea = createResultArea("BCC - Hex");
        bccDecArea = createResultArea("BCC - Dec");
        bccOctArea = createResultArea("BCC - Oct");
        bccBinArea = createResultArea("BCC - Bin");
        
        // CRC结果区域
        crcHexArea = createResultArea("CRC32 - Hex");
        crcDecArea = createResultArea("CRC32 - Dec");
        crcOctArea = createResultArea("CRC32 - Oct");
        crcBinArea = createResultArea("CRC32 - Bin");
        
        // LRC结果区域
        lrcHexArea = createResultArea("LRC - Hex");
        lrcDecArea = createResultArea("LRC - Dec");
        lrcOctArea = createResultArea("LRC - Oct");
        lrcBinArea = createResultArea("LRC - Bin");
    }
    
    private JTextArea createResultArea(String title) {
        JTextArea area = new JTextArea(2, 20);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setBorder(BorderFactory.createTitledBorder(title));
        area.setBackground(getBackground());
        return area;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部输入区域
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        
        JPanel inputControlPanel = new JPanel(new FlowLayout());
        inputControlPanel.add(new JLabel("输入格式:"));
        inputControlPanel.add(inputFormatComboBox);
        
        JButton calculateButton = new JButton("计算校验值");
        JButton clearButton = new JButton("清空");
        inputControlPanel.add(calculateButton);
        inputControlPanel.add(clearButton);
        
        topPanel.add(inputControlPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        
        // 中间结果区域
        JPanel resultPanel = new JPanel(new GridLayout(3, 4, 5, 5));
        resultPanel.setBorder(BorderFactory.createTitledBorder("校验结果"));
        
        // BCC行
        resultPanel.add(bccHexArea);
        resultPanel.add(bccDecArea);
        resultPanel.add(bccOctArea);
        resultPanel.add(bccBinArea);
        
        // CRC行
        resultPanel.add(crcHexArea);
        resultPanel.add(crcDecArea);
        resultPanel.add(crcOctArea);
        resultPanel.add(crcBinArea);
        
        // LRC行
        resultPanel.add(lrcHexArea);
        resultPanel.add(lrcDecArea);
        resultPanel.add(lrcOctArea);
        resultPanel.add(lrcBinArea);
        
        add(resultPanel, BorderLayout.CENTER);
        
        // 底部说明
        JTextArea infoArea = new JTextArea(4, 50);
        infoArea.setEditable(false);
        infoArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        infoArea.setText("校验算法说明:\n" +
            "BCC (Block Check Character): 异或校验，对所有字节进行异或运算\n" +
            "CRC32 (Cyclic Redundancy Check): 循环冗余校验，使用CRC32算法\n" +
            "LRC (Longitudinal Redundancy Check): 纵向冗余校验，字节累加后取补码");
        infoArea.setBorder(BorderFactory.createTitledBorder("说明"));
        add(new JScrollPane(infoArea), BorderLayout.SOUTH);
        
        // 设置事件处理
        calculateButton.addActionListener(e -> calculateChecksums());
        clearButton.addActionListener(e -> clearAll());
    }
    
    private void setupEventHandlers() {
        // 输入变化时自动计算
        inputArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateChecksums(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateChecksums(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateChecksums(); }
        });
        
        inputFormatComboBox.addActionListener(e -> calculateChecksums());
    }
    
    private void calculateChecksums() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            clearResults();
            return;
        }
        
        try {
            byte[] data = parseInput(input);
            
            // 计算BCC
            int bcc = calculateBCC(data);
            displayResult(bcc, bccHexArea, bccDecArea, bccOctArea, bccBinArea);
            
            // 计算CRC32
            long crc32 = calculateCRC32(data);
            displayResult((int)crc32, crcHexArea, crcDecArea, crcOctArea, crcBinArea);
            
            // 计算LRC
            int lrc = calculateLRC(data);
            displayResult(lrc, lrcHexArea, lrcDecArea, lrcOctArea, lrcBinArea);
            
        } catch (Exception e) {
            showError("输入数据格式错误: " + e.getMessage());
        }
    }
    
    private byte[] parseInput(String input) throws Exception {
        String format = (String) inputFormatComboBox.getSelectedItem();
        
        switch (format) {
            case "Hex":
                return parseHexString(input);
            case "Text":
                return input.getBytes("UTF-8");
            case "Base64":
                return java.util.Base64.getDecoder().decode(input);
            default:
                throw new IllegalArgumentException("不支持的输入格式: " + format);
        }
    }
    
    private byte[] parseHexString(String hex) throws Exception {
        // 移除空格和其他分隔符
        hex = hex.replaceAll("[^0-9A-Fa-f]", "");
        
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("十六进制字符串长度必须是偶数");
        }
        
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            bytes[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), 16);
        }
        
        return bytes;
    }
    
    private int calculateBCC(byte[] data) {
        int result = 0;
        for (byte b : data) {
            result ^= (b & 0xFF);
        }
        return result;
    }
    
    private long calculateCRC32(byte[] data) {
        Checksum crc32 = new CRC32();
        crc32.update(data, 0, data.length);
        return crc32.getValue();
    }
    
    private int calculateLRC(byte[] data) {
        int sum = 0;
        for (byte b : data) {
            sum += (b & 0xFF);
        }
        return 256 - (sum % 256);
    }
    
    private void displayResult(int value, JTextArea hexArea, JTextArea decArea, 
                              JTextArea octArea, JTextArea binArea) {
        // Hex格式
        String hex = Integer.toHexString(value).toUpperCase();
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        hexArea.setText(hex);
        
        // Dec格式
        decArea.setText(String.valueOf(value));
        
        // Oct格式
        octArea.setText(Integer.toOctalString(value));
        
        // Bin格式
        String bin = Integer.toBinaryString(value);
        // 补齐到8位的倍数
        int padding = 8 - (bin.length() % 8);
        if (padding != 8) {
            bin = "0".repeat(padding) + bin;
        }
        binArea.setText(bin);
    }
    
    private void clearResults() {
        bccHexArea.setText("");
        bccDecArea.setText("");
        bccOctArea.setText("");
        bccBinArea.setText("");
        
        crcHexArea.setText("");
        crcDecArea.setText("");
        crcOctArea.setText("");
        crcBinArea.setText("");
        
        lrcHexArea.setText("");
        lrcDecArea.setText("");
        lrcOctArea.setText("");
        lrcBinArea.setText("");
    }
    
    private void showError(String message) {
        clearResults();
        bccHexArea.setText("错误");
        bccDecArea.setText(message);
    }
    
    private void clearAll() {
        inputArea.setText("");
        clearResults();
    }
}