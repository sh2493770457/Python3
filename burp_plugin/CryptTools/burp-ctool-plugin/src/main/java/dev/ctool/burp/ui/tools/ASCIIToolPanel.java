package dev.ctool.burp.ui.tools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.util.HashMap;
import java.util.Map;

/**
 * ASCII工具面板
 * 支持ASCII码在字符串、十进制、十六进制、八进制、二进制之间的相互转换
 * 包含ASCII参考表功能
 */
public class ASCIIToolPanel extends JPanel {
    
    private JTextArea strTextArea;
    private JTextArea decTextArea;
    private JTextArea hexTextArea;
    private JTextArea octTextArea;
    private JTextArea binTextArea;
    private JTable referenceTable;
    private JDialog referenceDialog;
    
    // 文档监听器映射
    private Map<JTextArea, javax.swing.event.DocumentListener> listenerMap = new HashMap<>();
    private boolean isUpdating = false;
    
    // ASCII映射表
    private static final String[] ASCII_MAP = {
        "NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "HT", "LF", "VT", "FF", "CR", "SO", "SI",
        "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB", "CAN", "EM", "SUB", "ESC", "FS", "GS", "RS", "US",
        "SPACE", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E", "F",
        "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[",
        "\\", "]", "^", "_", "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
        "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|", "}", "~", "DEL"
    };
    
    // ASCII不可显示字符说明
    private static final Map<String, String> ASCII_HIDDEN = new HashMap<>();
    static {
        ASCII_HIDDEN.put("NUL", "空字符");
        ASCII_HIDDEN.put("SOH", "标题开始");
        ASCII_HIDDEN.put("STX", "正文开始");
        ASCII_HIDDEN.put("ETX", "正文结束");
        ASCII_HIDDEN.put("EOT", "传输结束");
        ASCII_HIDDEN.put("ENQ", "请求");
        ASCII_HIDDEN.put("ACK", "确认");
        ASCII_HIDDEN.put("BEL", "响铃");
        ASCII_HIDDEN.put("BS", "退格");
        ASCII_HIDDEN.put("HT", "水平制表符");
        ASCII_HIDDEN.put("LF", "换行");
        ASCII_HIDDEN.put("VT", "垂直制表符");
        ASCII_HIDDEN.put("FF", "换页");
        ASCII_HIDDEN.put("CR", "回车");
        ASCII_HIDDEN.put("SO", "移出");
        ASCII_HIDDEN.put("SI", "移入");
        ASCII_HIDDEN.put("DLE", "数据链路转义");
        ASCII_HIDDEN.put("DC1", "设备控制1");
        ASCII_HIDDEN.put("DC2", "设备控制2");
        ASCII_HIDDEN.put("DC3", "设备控制3");
        ASCII_HIDDEN.put("DC4", "设备控制4");
        ASCII_HIDDEN.put("NAK", "否定确认");
        ASCII_HIDDEN.put("SYN", "同步空闲");
        ASCII_HIDDEN.put("ETB", "传输块结束");
        ASCII_HIDDEN.put("CAN", "取消");
        ASCII_HIDDEN.put("EM", "媒介结束");
        ASCII_HIDDEN.put("SUB", "替换");
        ASCII_HIDDEN.put("ESC", "转义");
        ASCII_HIDDEN.put("FS", "文件分隔符");
        ASCII_HIDDEN.put("GS", "组分隔符");
        ASCII_HIDDEN.put("RS", "记录分隔符");
        ASCII_HIDDEN.put("US", "单元分隔符");
        ASCII_HIDDEN.put("DEL", "删除");
        ASCII_HIDDEN.put("SPACE", "空格");
    }
    
    public ASCIIToolPanel() {
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 字符串输入区域
        mainPanel.add(createInputPanel("字符串 (String)", "请输入字符串", 
            strTextArea = new JTextArea(3, 50), "str", true));
        
        // 十进制输入区域
        mainPanel.add(createInputPanel("十进制 (Decimal)", "请输入十进制ASCII码，用空格分隔", 
            decTextArea = new JTextArea(3, 50), "dec", false));
        
        // 十六进制输入区域
        mainPanel.add(createInputPanel("十六进制 (Hexadecimal)", "请输入十六进制ASCII码，用空格分隔", 
            hexTextArea = new JTextArea(3, 50), "hex", false));
        
        // 八进制输入区域
        mainPanel.add(createInputPanel("八进制 (Octal)", "请输入八进制ASCII码，用空格分隔", 
            octTextArea = new JTextArea(3, 50), "oct", false));
        
        // 二进制输入区域
        mainPanel.add(createInputPanel("二进制 (Binary)", "请输入二进制ASCII码，用空格分隔", 
            binTextArea = new JTextArea(3, 50), "bin", false));
        
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        
        // 创建ASCII参考表对话框
        createReferenceDialog();
    }
    
    private JPanel createInputPanel(String title, String placeholder, JTextArea textArea, String type, boolean showReference) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(title));
        
        // 设置文本区域
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // 添加文档监听器
        javax.swing.event.DocumentListener listener = new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                convertFromType(type);
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                convertFromType(type);
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                convertFromType(type);
            }
        };
        textArea.getDocument().addDocumentListener(listener);
        listenerMap.put(textArea, listener);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        if (showReference) {
            JButton referenceButton = new JButton("参考表");
            referenceButton.addActionListener(e -> showReferenceDialog());
            buttonPanel.add(referenceButton);
        }
        
        JButton copyButton = new JButton("复制");
        copyButton.addActionListener(e -> copyToClipboard(textArea.getText()));
        buttonPanel.add(copyButton);
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> textArea.setText(""));
        buttonPanel.add(clearButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void convertFromType(String sourceType) {
        if (isUpdating) return; // 防止循环更新
        
        try {
            String input = "";
            switch (sourceType) {
                case "str":
                    input = strTextArea.getText();
                    break;
                case "dec":
                    input = decTextArea.getText();
                    break;
                case "hex":
                    input = hexTextArea.getText();
                    break;
                case "oct":
                    input = octTextArea.getText();
                    break;
                case "bin":
                    input = binTextArea.getText();
                    break;
            }
            
            if (!input.trim().isEmpty()) {
                updateOtherFields(input, sourceType);
            }
        } catch (Exception e) {
            // 转换错误时不更新其他字段
        }
    }
    
    private void updateOtherFields(String input, String sourceType) {
        try {
            isUpdating = true; // 设置更新标志
            
            String result;
            
            if (!sourceType.equals("str")) {
                result = convertToString(input, sourceType);
                strTextArea.setText(result);
            }
            
            if (!sourceType.equals("dec")) {
                result = convertToDecimal(input, sourceType);
                decTextArea.setText(result);
            }
            
            if (!sourceType.equals("hex")) {
                result = convertToHexadecimal(input, sourceType);
                hexTextArea.setText(result);
            }
            
            if (!sourceType.equals("oct")) {
                result = convertToOctal(input, sourceType);
                octTextArea.setText(result);
            }
            
            if (!sourceType.equals("bin")) {
                result = convertToBinary(input, sourceType);
                binTextArea.setText(result);
            }
            
        } catch (Exception e) {
            // 转换失败时不更新
        } finally {
            isUpdating = false; // 重置更新标志
        }
    }
    
    private String convertToString(String input, String sourceType) {
        if (input.trim().isEmpty()) return "";
        
        StringBuilder result = new StringBuilder();
        String[] parts = input.trim().split("\\s+");
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            
            int ascii;
            switch (sourceType) {
                case "dec":
                    ascii = Integer.parseInt(part);
                    break;
                case "hex":
                    ascii = Integer.parseInt(part, 16);
                    break;
                case "oct":
                    ascii = Integer.parseInt(part, 8);
                    break;
                case "bin":
                    ascii = Integer.parseInt(part, 2);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown source type: " + sourceType);
            }
            
            if (ascii < 0 || ascii > 127) {
                throw new IllegalArgumentException("ASCII value out of range: " + ascii);
            }
            
            String asciiStr = ASCII_MAP[ascii];
            
            // 处理特殊字符
            switch (asciiStr) {
                case "SPACE":
                    result.append(" ");
                    break;
                case "HT":
                    result.append("\t");
                    break;
                case "LF":
                    result.append("\n");
                    break;
                case "CR":
                    result.append("\r");
                    break;
                default:
                    if (ASCII_HIDDEN.containsKey(asciiStr)) {
                        result.append("[").append(asciiStr).append("]");
                    } else {
                        result.append(asciiStr);
                    }
                    break;
            }
        }
        
        return result.toString();
    }
    
    private String convertToDecimal(String input, String sourceType) {
        if (input.trim().isEmpty()) return "";
        
        StringBuilder result = new StringBuilder();
        
        if (sourceType.equals("str")) {
            // 处理转义字符
            String processedInput = input;
            for (String key : ASCII_HIDDEN.keySet()) {
                processedInput = processedInput.replace("[" + key + "]", String.valueOf((char) getAsciiIndex(key)));
            }
            
            for (char c : processedInput.toCharArray()) {
                if (result.length() > 0) result.append(" ");
                result.append((int) c);
            }
        } else {
            String[] parts = input.trim().split("\\s+");
            for (String part : parts) {
                if (part.isEmpty()) continue;
                
                int ascii;
                switch (sourceType) {
                    case "hex":
                        ascii = Integer.parseInt(part, 16);
                        break;
                    case "oct":
                        ascii = Integer.parseInt(part, 8);
                        break;
                    case "bin":
                        ascii = Integer.parseInt(part, 2);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown source type: " + sourceType);
                }
                
                if (result.length() > 0) result.append(" ");
                result.append(ascii);
            }
        }
        
        return result.toString();
    }
    
    private String convertToHexadecimal(String input, String sourceType) {
        String decimal = sourceType.equals("dec") ? input : convertToDecimal(input, sourceType);
        if (decimal.trim().isEmpty()) return "";
        
        StringBuilder result = new StringBuilder();
        String[] parts = decimal.trim().split("\\s+");
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            int ascii = Integer.parseInt(part);
            if (result.length() > 0) result.append(" ");
            result.append(Integer.toHexString(ascii).toUpperCase());
        }
        
        return result.toString();
    }
    
    private String convertToOctal(String input, String sourceType) {
        String decimal = sourceType.equals("dec") ? input : convertToDecimal(input, sourceType);
        if (decimal.trim().isEmpty()) return "";
        
        StringBuilder result = new StringBuilder();
        String[] parts = decimal.trim().split("\\s+");
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            int ascii = Integer.parseInt(part);
            if (result.length() > 0) result.append(" ");
            result.append(Integer.toOctalString(ascii));
        }
        
        return result.toString();
    }
    
    private String convertToBinary(String input, String sourceType) {
        String decimal = sourceType.equals("dec") ? input : convertToDecimal(input, sourceType);
        if (decimal.trim().isEmpty()) return "";
        
        StringBuilder result = new StringBuilder();
        String[] parts = decimal.trim().split("\\s+");
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            int ascii = Integer.parseInt(part);
            if (result.length() > 0) result.append(" ");
            String binary = Integer.toBinaryString(ascii);
            // 补齐到8位
            while (binary.length() < 8) {
                binary = "0" + binary;
            }
            result.append(binary);
        }
        
        return result.toString();
    }
    
    private int getAsciiIndex(String asciiStr) {
        for (int i = 0; i < ASCII_MAP.length; i++) {
            if (ASCII_MAP[i].equals(asciiStr)) {
                return i;
            }
        }
        return -1;
    }
    
    private void createReferenceDialog() {
        referenceDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "ASCII参考表", true);
        referenceDialog.setSize(800, 600);
        referenceDialog.setLocationRelativeTo(this);
        
        // 创建表格数据
        String[] columnNames = {"十进制", "十六进制", "八进制", "二进制", "字符", "可显示", "说明"};
        Object[][] data = new Object[128][7];
        
        for (int i = 0; i < 128; i++) {
            String asciiStr = ASCII_MAP[i];
            boolean isVisible = !ASCII_HIDDEN.containsKey(asciiStr);
            
            data[i][0] = String.valueOf(i);
            data[i][1] = Integer.toHexString(i).toUpperCase();
            data[i][2] = Integer.toOctalString(i);
            data[i][3] = String.format("%8s", Integer.toBinaryString(i)).replace(' ', '0');
            data[i][4] = asciiStr;
            data[i][5] = isVisible ? "是" : "否";
            data[i][6] = isVisible ? "" : ASCII_HIDDEN.get(asciiStr);
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        referenceTable = new JTable(model);
        referenceTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        referenceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(referenceTable);
        referenceDialog.add(scrollPane, BorderLayout.CENTER);
        
        // 添加关闭按钮
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> referenceDialog.setVisible(false));
        buttonPanel.add(closeButton);
        
        referenceDialog.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void showReferenceDialog() {
        referenceDialog.setVisible(true);
    }
    
    private void copyToClipboard(String text) {
        if (text != null && !text.trim().isEmpty()) {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            
            JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}