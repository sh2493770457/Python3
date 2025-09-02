package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;

/**
 * 十六进制转换工具面板
 */
public class HexToolPanel extends JPanel {
    
    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JRadioButton encodeRadio;
    private JRadioButton decodeRadio;
    private JCheckBox upperCaseCheckBox;
    private JCheckBox spaceCheckBox;
    private JCheckBox prefixCheckBox;
    private JComboBox<String> charsetComboBox;
    
    public HexToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建选项面板
        JPanel optionPanel = createOptionPanel();
        
        // 创建输出面板
        JPanel outputPanel = createOutputPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(optionPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输入"));
        
        inputArea = new JTextArea(8, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("选项"));
        
        // 操作类型选择
        encodeRadio = new JRadioButton("字符串转十六进制", true);
        decodeRadio = new JRadioButton("十六进制转字符串");
        
        ButtonGroup operationGroup = new ButtonGroup();
        operationGroup.add(encodeRadio);
        operationGroup.add(decodeRadio);
        
        // 格式选项
        upperCaseCheckBox = new JCheckBox("大写输出", false);
        spaceCheckBox = new JCheckBox("添加空格分隔", true);
        prefixCheckBox = new JCheckBox("添加0x前缀", false);
        
        // 字符集选择
        String[] charsets = {"UTF-8", "GBK", "ISO-8859-1", "UTF-16", "ASCII"};
        charsetComboBox = new JComboBox<>(charsets);
        charsetComboBox.setSelectedItem("UTF-8");
        
        // 第一行
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row1.add(new JLabel("操作:"));
        row1.add(encodeRadio);
        row1.add(decodeRadio);
        
        // 第二行
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row2.add(new JLabel("格式:"));
        row2.add(upperCaseCheckBox);
        row2.add(spaceCheckBox);
        row2.add(prefixCheckBox);
        row2.add(Box.createHorizontalStrut(10));
        row2.add(new JLabel("字符集:"));
        row2.add(charsetComboBox);
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(row1);
        panel.add(row2);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输出"));
        
        outputArea = new JTextArea(8, 50);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton processButton = new JButton("转换");
        processButton.addActionListener(new ProcessActionListener());
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });
        
        JButton swapButton = new JButton("交换");
        swapButton.addActionListener(e -> {
            String input = inputArea.getText();
            String output = outputArea.getText();
            inputArea.setText(output);
            outputArea.setText(input);
            
            // 切换操作模式
            if (encodeRadio.isSelected()) {
                decodeRadio.setSelected(true);
            } else {
                encodeRadio.setSelected(true);
            }
        });
        
        JButton copyButton = new JButton("复制结果");
        copyButton.addActionListener(e -> {
            String result = outputArea.getText();
            if (!result.isEmpty()) {
                java.awt.datatransfer.StringSelection stringSelection = 
                    new java.awt.datatransfer.StringSelection(result);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(stringSelection, null);
                JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "提示", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        panel.add(processButton);
        panel.add(clearButton);
        panel.add(swapButton);
        panel.add(copyButton);
        
        return panel;
    }
    
    private class ProcessActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String input = inputArea.getText();
                if (input.isEmpty()) {
                    outputArea.setText("");
                    return;
                }
                
                String result;
                if (encodeRadio.isSelected()) {
                    result = stringToHex(input);
                } else {
                    result = hexToString(input);
                }
                
                outputArea.setText(result);
                
            } catch (Exception ex) {
                outputArea.setText("转换失败: " + ex.getMessage());
            }
        }
    }
    
    /**
     * 字符串转十六进制
     */
    private String stringToHex(String input) {
        try {
            String charset = (String) charsetComboBox.getSelectedItem();
            byte[] bytes = input.getBytes(charset);
            
            StringBuilder result = new StringBuilder();
            boolean upperCase = upperCaseCheckBox.isSelected();
            boolean addSpace = spaceCheckBox.isSelected();
            boolean addPrefix = prefixCheckBox.isSelected();
            
            for (int i = 0; i < bytes.length; i++) {
                if (i > 0 && addSpace) {
                    result.append(" ");
                }
                
                if (addPrefix) {
                    result.append("0x");
                }
                
                String hex = String.format("%02x", bytes[i] & 0xFF);
                if (upperCase) {
                    hex = hex.toUpperCase();
                }
                
                result.append(hex);
            }
            
            return result.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("字符串转换失败: " + e.getMessage());
        }
    }
    
    /**
     * 十六进制转字符串
     */
    private String hexToString(String input) {
        try {
            // 清理输入：移除空格、0x前缀等
            String cleanInput = input.replaceAll("\\s+", "")
                                   .replaceAll("0x", "")
                                   .replaceAll("0X", "");
            
            // 确保长度为偶数
            if (cleanInput.length() % 2 != 0) {
                throw new IllegalArgumentException("十六进制字符串长度必须为偶数");
            }
            
            byte[] bytes = new byte[cleanInput.length() / 2];
            
            for (int i = 0; i < cleanInput.length(); i += 2) {
                String hex = cleanInput.substring(i, i + 2);
                bytes[i / 2] = (byte) Integer.parseInt(hex, 16);
            }
            
            String charset = (String) charsetComboBox.getSelectedItem();
            return new String(bytes, charset);
            
        } catch (NumberFormatException e) {
            throw new RuntimeException("无效的十六进制字符: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("十六进制转换失败: " + e.getMessage());
        }
    }
}