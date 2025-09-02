package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ASN1ToolPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> inputFormatCombo;
    private JButton decodeButton;
    private JButton clearButton;
    
    // ASN.1标签类型映射
    private static final Map<Integer, String> TAG_NAMES = new HashMap<>();
    
    static {
        TAG_NAMES.put(0x01, "BOOLEAN");
        TAG_NAMES.put(0x02, "INTEGER");
        TAG_NAMES.put(0x03, "BIT STRING");
        TAG_NAMES.put(0x04, "OCTET STRING");
        TAG_NAMES.put(0x05, "NULL");
        TAG_NAMES.put(0x06, "OBJECT IDENTIFIER");
        TAG_NAMES.put(0x07, "ObjectDescriptor");
        TAG_NAMES.put(0x08, "EXTERNAL");
        TAG_NAMES.put(0x09, "REAL");
        TAG_NAMES.put(0x0A, "ENUMERATED");
        TAG_NAMES.put(0x0B, "EMBEDDED PDV");
        TAG_NAMES.put(0x0C, "UTF8String");
        TAG_NAMES.put(0x0D, "RELATIVE-OID");
        TAG_NAMES.put(0x10, "SEQUENCE");
        TAG_NAMES.put(0x11, "SET");
        TAG_NAMES.put(0x12, "NumericString");
        TAG_NAMES.put(0x13, "PrintableString");
        TAG_NAMES.put(0x14, "T61String");
        TAG_NAMES.put(0x15, "VideotexString");
        TAG_NAMES.put(0x16, "IA5String");
        TAG_NAMES.put(0x17, "UTCTime");
        TAG_NAMES.put(0x18, "GeneralizedTime");
        TAG_NAMES.put(0x19, "GraphicString");
        TAG_NAMES.put(0x1A, "VisibleString");
        TAG_NAMES.put(0x1B, "GeneralString");
        TAG_NAMES.put(0x1C, "UniversalString");
        TAG_NAMES.put(0x1E, "BMPString");
    }
    
    public ASN1ToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        inputArea = new JTextArea(15, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        outputArea = new JTextArea(15, 50);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        inputFormatCombo = new JComboBox<>(new String[]{"十六进制", "Base64"});
        decodeButton = new JButton("解码");
        clearButton = new JButton("清空");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("输入格式:"));
        controlPanel.add(inputFormatCombo);
        controlPanel.add(decodeButton);
        controlPanel.add(clearButton);
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("ASN.1 DER编码数据"));
        
        JTextArea placeholderArea = new JTextArea("输入示例 (十六进制):\n308201033081b60201003027310b30090603550406130244453118301606035504030c0f7777772e6578616d706c652e636f6d302a300506032b657003210072596b7105e845c8c189d1da142dee367859b4e24f34f42cf07de77c7680f9fba05c305a06092a864886f70d01090e314d304b300b0603551d0f04040302043030130603551d25040c300a06082b0601050507030130270603551d110420301e820f7777772e6578616d706c652e636f6d820b6578616d706c652e636f6d300506032b657003410087cf352637d1e0053c64390f69878eeff35e5c22ca4c3caeaec29e01b300be96a6f18fa7f41acf7ca598d1915bab80b30bd2687d596debacde94a69c5c2fa90e\n\n或 Base64:\nMIIBA6CBtgIBADAn...");
        placeholderArea.setEditable(false);
        placeholderArea.setBackground(getBackground());
        placeholderArea.setForeground(Color.GRAY);
        placeholderArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        
        JSplitPane inputSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
            new JScrollPane(inputArea), 
            new JScrollPane(placeholderArea));
        inputSplit.setDividerLocation(200);
        inputPanel.add(inputSplit, BorderLayout.CENTER);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("解码结果 (XML格式)"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        // 主分割面板
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, outputPanel);
        mainSplit.setDividerLocation(500);
        
        add(controlPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decodeASN1();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                outputArea.setText("");
            }
        });
    }
    
    private void decodeASN1() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            outputArea.setText("");
            return;
        }
        
        try {
            byte[] data;
            String format = (String) inputFormatCombo.getSelectedItem();
            
            if ("Base64".equals(format)) {
                data = Base64.getDecoder().decode(input.replaceAll("\\s+", ""));
            } else {
                // 十六进制
                String hex = input.replaceAll("\\s+", "").replaceAll("0x", "");
                data = hexStringToByteArray(hex);
            }
            
            StringBuilder result = new StringBuilder();
            result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            result.append("<asn1>\n");
            
            parseASN1(data, 0, result, 1);
            
            result.append("</asn1>");
            outputArea.setText(result.toString());
            
        } catch (Exception e) {
            outputArea.setText("解码错误: " + e.getMessage() + "\n\n请检查输入数据格式是否正确。");
        }
    }
    
    private int parseASN1(byte[] data, int offset, StringBuilder result, int depth) throws Exception {
        if (offset >= data.length) {
            return offset;
        }
        
        String indent = "  ".repeat(depth);
        
        // 读取标签
        int tag = data[offset] & 0xFF;
        offset++;
        
        // 读取长度
        int length;
        int lengthBytes = 1;
        if ((data[offset] & 0x80) == 0) {
            // 短格式
            length = data[offset] & 0x7F;
            offset++;
        } else {
            // 长格式
            int lengthOfLength = data[offset] & 0x7F;
            offset++;
            lengthBytes += lengthOfLength;
            
            if (lengthOfLength == 0) {
                throw new Exception("不定长度格式不支持");
            }
            
            length = 0;
            for (int i = 0; i < lengthOfLength; i++) {
                length = (length << 8) | (data[offset] & 0xFF);
                offset++;
            }
        }
        
        // 获取标签名称
        String tagName = TAG_NAMES.getOrDefault(tag & 0x1F, "UNKNOWN");
        boolean isConstructed = (tag & 0x20) != 0;
        
        result.append(indent).append("<").append(tagName.toLowerCase().replace(" ", "_"));
        result.append(" tag=\"0x").append(String.format("%02X", tag)).append("\"");
        result.append(" length=\"").append(length).append("\"");
        
        if (isConstructed || (tag & 0x1F) == 0x10 || (tag & 0x1F) == 0x11) {
            // 构造类型 (SEQUENCE, SET等)
            result.append(">\n");
            
            int endOffset = offset + length;
            while (offset < endOffset) {
                offset = parseASN1(data, offset, result, depth + 1);
            }
            
            result.append(indent).append("</").append(tagName.toLowerCase().replace(" ", "_")).append(">\n");
        } else {
            // 原始类型
            result.append(">");
            
            String content = parseContent(data, offset, length, tag & 0x1F);
            result.append(content);
            
            result.append("</").append(tagName.toLowerCase().replace(" ", "_")).append(">\n");
            offset += length;
        }
        
        return offset;
    }
    
    private String parseContent(byte[] data, int offset, int length, int tagNumber) {
        if (length == 0) {
            return "";
        }
        
        switch (tagNumber) {
            case 0x01: // BOOLEAN
                return (data[offset] == 0) ? "false" : "true";
                
            case 0x02: // INTEGER
                if (length <= 8) {
                    long value = 0;
                    for (int i = 0; i < length; i++) {
                        value = (value << 8) | (data[offset + i] & 0xFF);
                    }
                    return String.valueOf(value);
                } else {
                    return "0x" + bytesToHex(data, offset, length);
                }
                
            case 0x03: // BIT STRING
            case 0x04: // OCTET STRING
                return "0x" + bytesToHex(data, offset, length);
                
            case 0x05: // NULL
                return "";
                
            case 0x06: // OBJECT IDENTIFIER
                return parseOID(data, offset, length);
                
            case 0x0A: // ENUMERATED
                long enumValue = 0;
                for (int i = 0; i < length; i++) {
                    enumValue = (enumValue << 8) | (data[offset + i] & 0xFF);
                }
                return String.valueOf(enumValue);
                
            case 0x0C: // UTF8String
                return new String(data, offset, length, StandardCharsets.UTF_8);
                
            case 0x13: // PrintableString
            case 0x16: // IA5String
            case 0x1A: // VisibleString
                return new String(data, offset, length, StandardCharsets.US_ASCII);
                
            case 0x17: // UTCTime
            case 0x18: // GeneralizedTime
                return new String(data, offset, length, StandardCharsets.US_ASCII);
                
            default:
                return "0x" + bytesToHex(data, offset, length);
        }
    }
    
    private String parseOID(byte[] data, int offset, int length) {
        if (length == 0) {
            return "";
        }
        
        StringBuilder oid = new StringBuilder();
        
        // 第一个字节编码前两个子标识符
        int firstByte = data[offset] & 0xFF;
        oid.append(firstByte / 40).append(".").append(firstByte % 40);
        
        int i = offset + 1;
        while (i < offset + length) {
            long value = 0;
            while (i < offset + length && (data[i] & 0x80) != 0) {
                value = (value << 7) | (data[i] & 0x7F);
                i++;
            }
            if (i < offset + length) {
                value = (value << 7) | (data[i] & 0x7F);
                i++;
            }
            oid.append(".").append(value);
        }
        
        return oid.toString();
    }
    
    private String bytesToHex(byte[] data, int offset, int length) {
        StringBuilder hex = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            hex.append(String.format("%02X", data[i] & 0xFF));
        }
        return hex.toString();
    }
    
    private byte[] hexStringToByteArray(String hex) {
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; i++) {
            int index = i * 2;
            result[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), 16);
        }
        return result;
    }
}