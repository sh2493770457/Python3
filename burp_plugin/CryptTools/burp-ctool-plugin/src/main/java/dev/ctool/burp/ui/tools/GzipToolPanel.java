package dev.ctool.burp.ui.tools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipToolPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JRadioButton compressRadio;
    private JRadioButton decompressRadio;
    private JRadioButton textModeRadio;
    private JRadioButton base64ModeRadio;
    private JLabel statusLabel;

    public GzipToolPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("GZIP 压缩/解压工具"));

        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入数据"));
        
        inputArea = new JTextArea(8, 40);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 选项面板
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1));
        
        // 操作选项
        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        operationPanel.setBorder(new TitledBorder("操作"));
        
        ButtonGroup operationGroup = new ButtonGroup();
        compressRadio = new JRadioButton("压缩", true);
        decompressRadio = new JRadioButton("解压");
        operationGroup.add(compressRadio);
        operationGroup.add(decompressRadio);
        
        operationPanel.add(compressRadio);
        operationPanel.add(decompressRadio);
        
        // 格式选项
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.setBorder(new TitledBorder("输出格式"));
        
        ButtonGroup formatGroup = new ButtonGroup();
        textModeRadio = new JRadioButton("文本模式", true);
        base64ModeRadio = new JRadioButton("Base64编码");
        formatGroup.add(textModeRadio);
        formatGroup.add(base64ModeRadio);
        
        formatPanel.add(textModeRadio);
        formatPanel.add(base64ModeRadio);
        
        optionsPanel.add(operationPanel);
        optionsPanel.add(formatPanel);
        
        inputPanel.add(optionsPanel, BorderLayout.SOUTH);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("输出结果"));
        
        outputArea = new JTextArea(8, 40);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // 状态标签
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        outputPanel.add(statusLabel, BorderLayout.NORTH);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton processButton = new JButton("处理");
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processData();
            }
        });
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        
        JButton swapButton = new JButton("交换输入输出");
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapInputOutput();
            }
        });
        
        JButton copyButton = new JButton("复制结果");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyResult();
            }
        });
        
        buttonPanel.add(processButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(copyButton);
        
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(inputPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
    }

    private void processData() {
        try {
            String input = inputArea.getText();
            if (input.isEmpty()) {
                outputArea.setText("请输入要处理的数据");
                statusLabel.setText(" ");
                return;
            }
            
            String result;
            if (compressRadio.isSelected()) {
                result = compressData(input);
            } else {
                result = decompressData(input);
            }
            
            outputArea.setText(result);
            
        } catch (Exception e) {
            outputArea.setText("处理失败: " + e.getMessage());
            statusLabel.setText("错误: " + e.getClass().getSimpleName());
        }
    }

    private String compressData(String input) throws IOException {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(baos)) {
            gzipOut.write(inputBytes);
        }
        
        byte[] compressedBytes = baos.toByteArray();
        
        // 更新状态信息
        double compressionRatio = (double) compressedBytes.length / inputBytes.length * 100;
        statusLabel.setText(String.format("压缩完成 - 原始: %d 字节, 压缩后: %d 字节, 压缩率: %.1f%%", 
                inputBytes.length, compressedBytes.length, compressionRatio));
        
        if (base64ModeRadio.isSelected()) {
            return Base64.getEncoder().encodeToString(compressedBytes);
        } else {
            // 文本模式：尝试转换为字符串，如果包含不可打印字符则使用Base64
            try {
                String result = new String(compressedBytes, StandardCharsets.UTF_8);
                // 检查是否包含控制字符
                if (result.chars().anyMatch(c -> c < 32 && c != 9 && c != 10 && c != 13)) {
                    statusLabel.setText(statusLabel.getText() + " (自动转换为Base64格式)");
                    return Base64.getEncoder().encodeToString(compressedBytes);
                }
                return result;
            } catch (Exception e) {
                statusLabel.setText(statusLabel.getText() + " (自动转换为Base64格式)");
                return Base64.getEncoder().encodeToString(compressedBytes);
            }
        }
    }

    private String decompressData(String input) throws IOException {
        byte[] inputBytes;
        
        // 尝试解析输入数据
        if (isBase64(input)) {
            try {
                inputBytes = Base64.getDecoder().decode(input);
            } catch (Exception e) {
                throw new IOException("无效的Base64格式");
            }
        } else {
            inputBytes = input.getBytes(StandardCharsets.UTF_8);
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (GZIPInputStream gzipIn = new GZIPInputStream(bais)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIn.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
        }
        
        byte[] decompressedBytes = baos.toByteArray();
        String result = new String(decompressedBytes, StandardCharsets.UTF_8);
        
        // 更新状态信息
        statusLabel.setText(String.format("解压完成 - 压缩数据: %d 字节, 解压后: %d 字节", 
                inputBytes.length, decompressedBytes.length));
        
        return result;
    }

    private boolean isBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        
        // 简单的Base64格式检查
        return str.matches("^[A-Za-z0-9+/]*={0,2}$") && str.length() % 4 == 0;
    }

    private void clearFields() {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText(" ");
        compressRadio.setSelected(true);
        textModeRadio.setSelected(true);
    }

    private void swapInputOutput() {
        String input = inputArea.getText();
        String output = outputArea.getText();
        inputArea.setText(output);
        outputArea.setText(input);
        
        // 切换操作模式
        if (compressRadio.isSelected()) {
            decompressRadio.setSelected(true);
        } else {
            compressRadio.setSelected(true);
        }
        
        statusLabel.setText(" ");
    }

    private void copyResult() {
        String result = outputArea.getText();
        if (!result.isEmpty()) {
            StringSelection selection = new StringSelection(result);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}