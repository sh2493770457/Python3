package dev.ctool.burp.ui.tools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class URLToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // URL编码/解码选项卡
    private JTextArea encodeInputArea;
    private JTextArea encodeOutputArea;
    private JRadioButton encodeRadio;
    private JRadioButton decodeRadio;
    private JComboBox<String> charsetComboBox;
    
    // URL解析选项卡
    private JTextArea parseInputArea;
    private JTextArea parseOutputArea;
    private JCheckBox prettyFormatCheckBox;

    public URLToolPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("URL 工具"));

        tabbedPane = new JTabbedPane();
        
        // URL编码/解码选项卡
        JPanel encodePanel = createEncodeDecodePanel();
        tabbedPane.addTab("URL编码/解码", encodePanel);
        
        // URL解析选项卡
        JPanel parsePanel = createParsePanel();
        tabbedPane.addTab("URL解析", parsePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createEncodeDecodePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入"));
        
        encodeInputArea = new JTextArea(6, 40);
        encodeInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        encodeInputArea.setLineWrap(true);
        encodeInputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(encodeInputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 选项面板
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // 操作选项
        ButtonGroup operationGroup = new ButtonGroup();
        encodeRadio = new JRadioButton("编码", true);
        decodeRadio = new JRadioButton("解码");
        operationGroup.add(encodeRadio);
        operationGroup.add(decodeRadio);
        
        optionsPanel.add(encodeRadio);
        optionsPanel.add(decodeRadio);
        optionsPanel.add(Box.createHorizontalStrut(20));
        
        // 字符集选项
        optionsPanel.add(new JLabel("字符集:"));
        charsetComboBox = new JComboBox<>(new String[]{"UTF-8", "GBK", "ISO-8859-1", "US-ASCII"});
        optionsPanel.add(charsetComboBox);
        
        inputPanel.add(optionsPanel, BorderLayout.SOUTH);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("输出"));
        
        encodeOutputArea = new JTextArea(6, 40);
        encodeOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        encodeOutputArea.setLineWrap(true);
        encodeOutputArea.setWrapStyleWord(true);
        encodeOutputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(encodeOutputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton processButton = new JButton("处理");
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processEncodeDecode();
            }
        });
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEncodeFields();
            }
        });
        
        JButton swapButton = new JButton("交换输入输出");
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapEncodeInputOutput();
            }
        });
        
        JButton copyButton = new JButton("复制结果");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyEncodeResult();
            }
        });
        
        buttonPanel.add(processButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(copyButton);
        
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(outputPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createParsePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入URL"));
        
        parseInputArea = new JTextArea(4, 40);
        parseInputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        parseInputArea.setLineWrap(true);
        parseInputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(parseInputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 选项面板
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prettyFormatCheckBox = new JCheckBox("格式化输出", true);
        optionsPanel.add(prettyFormatCheckBox);
        inputPanel.add(optionsPanel, BorderLayout.SOUTH);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("解析结果"));
        
        parseOutputArea = new JTextArea(12, 40);
        parseOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        parseOutputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(parseOutputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton parseButton = new JButton("解析URL");
        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseURL();
            }
        });
        
        JButton clearParseButton = new JButton("清空");
        clearParseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearParseFields();
            }
        });
        
        JButton copyParseButton = new JButton("复制结果");
        copyParseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyParseResult();
            }
        });
        
        buttonPanel.add(parseButton);
        buttonPanel.add(clearParseButton);
        buttonPanel.add(copyParseButton);
        
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(outputPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void processEncodeDecode() {
        try {
            String input = encodeInputArea.getText().trim();
            if (input.isEmpty()) {
                encodeOutputArea.setText("请输入要处理的文本");
                return;
            }
            
            String charset = (String) charsetComboBox.getSelectedItem();
            String result;
            
            if (encodeRadio.isSelected()) {
                result = URLEncoder.encode(input, charset);
            } else {
                result = URLDecoder.decode(input, charset);
            }
            
            encodeOutputArea.setText(result);
            
        } catch (UnsupportedEncodingException e) {
            encodeOutputArea.setText("不支持的字符编码: " + e.getMessage());
        } catch (Exception e) {
            encodeOutputArea.setText("处理失败: " + e.getMessage());
        }
    }

    private void parseURL() {
        try {
            String input = parseInputArea.getText().trim();
            if (input.isEmpty()) {
                parseOutputArea.setText("请输入要解析的URL");
                return;
            }
            
            URL url = new URL(input);
            StringBuilder result = new StringBuilder();
            
            if (prettyFormatCheckBox.isSelected()) {
                result.append("URL解析结果:\n");
                result.append("================\n\n");
                
                result.append("完整URL: ").append(url.toString()).append("\n\n");
                
                result.append("协议 (Protocol): ").append(url.getProtocol()).append("\n");
                
                if (url.getUserInfo() != null) {
                    result.append("用户信息 (UserInfo): ").append(url.getUserInfo()).append("\n");
                }
                
                result.append("主机 (Host): ").append(url.getHost()).append("\n");
                
                if (url.getPort() != -1) {
                    result.append("端口 (Port): ").append(url.getPort()).append("\n");
                } else {
                    result.append("端口 (Port): ").append(url.getDefaultPort()).append(" (默认)\n");
                }
                
                if (url.getPath() != null && !url.getPath().isEmpty()) {
                    result.append("路径 (Path): ").append(url.getPath()).append("\n");
                }
                
                if (url.getQuery() != null) {
                    result.append("查询参数 (Query): ").append(url.getQuery()).append("\n\n");
                    
                    // 解析查询参数
                    Map<String, String> params = parseQueryParams(url.getQuery());
                    if (!params.isEmpty()) {
                        result.append("查询参数详细:\n");
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            result.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
                        }
                        result.append("\n");
                    }
                }
                
                if (url.getRef() != null) {
                    result.append("锚点 (Fragment): ").append(url.getRef()).append("\n");
                }
                
            } else {
                // 简洁格式
                result.append("Protocol: ").append(url.getProtocol()).append("\n");
                if (url.getUserInfo() != null) {
                    result.append("UserInfo: ").append(url.getUserInfo()).append("\n");
                }
                result.append("Host: ").append(url.getHost()).append("\n");
                result.append("Port: ").append(url.getPort() != -1 ? url.getPort() : url.getDefaultPort()).append("\n");
                if (url.getPath() != null) {
                    result.append("Path: ").append(url.getPath()).append("\n");
                }
                if (url.getQuery() != null) {
                    result.append("Query: ").append(url.getQuery()).append("\n");
                }
                if (url.getRef() != null) {
                    result.append("Fragment: ").append(url.getRef()).append("\n");
                }
            }
            
            parseOutputArea.setText(result.toString());
            
        } catch (MalformedURLException e) {
            parseOutputArea.setText("URL格式错误: " + e.getMessage());
        } catch (Exception e) {
            parseOutputArea.setText("解析失败: " + e.getMessage());
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new LinkedHashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            try {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name()) : "";
                params.put(key, value);
            } catch (UnsupportedEncodingException e) {
                params.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
            }
        }
        
        return params;
    }

    private void clearEncodeFields() {
        encodeInputArea.setText("");
        encodeOutputArea.setText("");
        encodeRadio.setSelected(true);
        charsetComboBox.setSelectedIndex(0);
    }

    private void clearParseFields() {
        parseInputArea.setText("");
        parseOutputArea.setText("");
    }

    private void swapEncodeInputOutput() {
        String input = encodeInputArea.getText();
        String output = encodeOutputArea.getText();
        encodeInputArea.setText(output);
        encodeOutputArea.setText(input);
        
        // 切换操作模式
        if (encodeRadio.isSelected()) {
            decodeRadio.setSelected(true);
        } else {
            encodeRadio.setSelected(true);
        }
    }

    private void copyEncodeResult() {
        String result = encodeOutputArea.getText();
        if (!result.isEmpty()) {
            StringSelection selection = new StringSelection(result);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void copyParseResult() {
        String result = parseOutputArea.getText();
        if (!result.isEmpty()) {
            StringSelection selection = new StringSelection(result);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}