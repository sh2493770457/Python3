package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class URLParserToolPanel extends JPanel {
    private JTextArea urlArea;
    private JTextField schemeField;
    private JTextField hostField;
    private JTextField portField;
    private JTextField pathField;
    private JTextField queryField;
    private JTextField fragmentField;
    private JTextArea parametersArea;
    private JButton parseButton;
    private JButton buildButton;
    private JButton clearButton;
    private JButton encodeButton;
    private JButton decodeButton;
    
    public URLParserToolPanel() {
        initComponents();
        layoutComponents();
        addEventListeners();
    }
    
    private void initComponents() {
        // URL输入区域
        urlArea = new JTextArea(3, 50);
        urlArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        urlArea.setText("https://www.example.com:8080/path/to/resource?param1=value1&param2=value2&中文=测试#section1");
        urlArea.setLineWrap(true);
        urlArea.setWrapStyleWord(true);
        
        // URL组件字段
        schemeField = new JTextField(20);
        hostField = new JTextField(20);
        portField = new JTextField(10);
        pathField = new JTextField(30);
        queryField = new JTextField(30);
        fragmentField = new JTextField(20);
        
        // 参数解析区域
        parametersArea = new JTextArea(8, 50);
        parametersArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        parametersArea.setEditable(false);
        parametersArea.setBackground(new Color(248, 248, 248));
        
        // 按钮
        parseButton = new JButton("解析URL");
        buildButton = new JButton("构建URL");
        clearButton = new JButton("清空");
        encodeButton = new JButton("URL编码");
        decodeButton = new JButton("URL解码");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // URL输入面板
        JPanel urlPanel = new JPanel(new BorderLayout());
        urlPanel.setBorder(new TitledBorder("URL输入"));
        
        JPanel urlButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urlButtonPanel.add(parseButton);
        urlButtonPanel.add(buildButton);
        urlButtonPanel.add(clearButton);
        urlButtonPanel.add(encodeButton);
        urlButtonPanel.add(decodeButton);
        
        urlPanel.add(new JScrollPane(urlArea), BorderLayout.CENTER);
        urlPanel.add(urlButtonPanel, BorderLayout.SOUTH);
        
        // URL组件面板
        JPanel componentsPanel = new JPanel(new GridBagLayout());
        componentsPanel.setBorder(new TitledBorder("URL组件"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 第一行：协议和主机
        gbc.gridx = 0; gbc.gridy = 0;
        componentsPanel.add(new JLabel("协议:"), gbc);
        gbc.gridx = 1;
        componentsPanel.add(schemeField, gbc);
        gbc.gridx = 2;
        componentsPanel.add(new JLabel("主机:"), gbc);
        gbc.gridx = 3;
        componentsPanel.add(hostField, gbc);
        gbc.gridx = 4;
        componentsPanel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 5;
        componentsPanel.add(portField, gbc);
        
        // 第二行：路径
        gbc.gridx = 0; gbc.gridy = 1;
        componentsPanel.add(new JLabel("路径:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        componentsPanel.add(pathField, gbc);
        
        // 第三行：查询字符串
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        componentsPanel.add(new JLabel("查询:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        componentsPanel.add(queryField, gbc);
        
        // 第四行：片段
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        componentsPanel.add(new JLabel("片段:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        componentsPanel.add(fragmentField, gbc);
        
        // 参数解析面板
        JPanel parametersPanel = new JPanel(new BorderLayout());
        parametersPanel.setBorder(new TitledBorder("查询参数解析"));
        parametersPanel.add(new JScrollPane(parametersArea), BorderLayout.CENTER);
        
        // 主布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(urlPanel, BorderLayout.NORTH);
        topPanel.add(componentsPanel, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, parametersPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.7);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void addEventListeners() {
        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseURL();
            }
        });
        
        buildButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildURL();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encodeURL();
            }
        });
        
        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decodeURL();
            }
        });
    }
    
    private void parseURL() {
        String url = urlArea.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入URL", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            URI uri = URI.create(url);
            
            // 填充组件字段
            schemeField.setText(uri.getScheme() != null ? uri.getScheme() : "");
            hostField.setText(uri.getHost() != null ? uri.getHost() : "");
            portField.setText(uri.getPort() != -1 ? String.valueOf(uri.getPort()) : "");
            pathField.setText(uri.getPath() != null ? uri.getPath() : "");
            queryField.setText(uri.getQuery() != null ? uri.getQuery() : "");
            fragmentField.setText(uri.getFragment() != null ? uri.getFragment() : "");
            
            // 解析查询参数
            parseQueryParameters(uri.getQuery());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "URL解析失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void parseQueryParameters(String query) {
        StringBuilder sb = new StringBuilder();
        sb.append("查询参数解析结果:\n");
        sb.append("===================\n\n");
        
        if (query == null || query.isEmpty()) {
            sb.append("无查询参数\n");
        } else {
            try {
                Map<String, String> params = new HashMap<>();
                String[] pairs = query.split("&");
                
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                    String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name()) : "";
                    params.put(key, value);
                }
                
                sb.append("参数数量: ").append(params.size()).append("\n\n");
                
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append("参数名: ").append(entry.getKey()).append("\n");
                    sb.append("参数值: ").append(entry.getValue()).append("\n");
                    sb.append("编码后: ").append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
                      .append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name())).append("\n");
                    sb.append("-------------------\n");
                }
                
            } catch (Exception e) {
                sb.append("参数解析失败: ").append(e.getMessage()).append("\n");
            }
        }
        
        parametersArea.setText(sb.toString());
    }
    
    private void buildURL() {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            
            // 协议
            String scheme = schemeField.getText().trim();
            if (!scheme.isEmpty()) {
                urlBuilder.append(scheme).append("://");
            }
            
            // 主机
            String host = hostField.getText().trim();
            if (!host.isEmpty()) {
                urlBuilder.append(host);
            }
            
            // 端口
            String port = portField.getText().trim();
            if (!port.isEmpty()) {
                urlBuilder.append(":").append(port);
            }
            
            // 路径
            String path = pathField.getText().trim();
            if (!path.isEmpty()) {
                if (!path.startsWith("/")) {
                    urlBuilder.append("/");
                }
                urlBuilder.append(path);
            }
            
            // 查询字符串
            String query = queryField.getText().trim();
            if (!query.isEmpty()) {
                urlBuilder.append("?").append(query);
            }
            
            // 片段
            String fragment = fragmentField.getText().trim();
            if (!fragment.isEmpty()) {
                urlBuilder.append("#").append(fragment);
            }
            
            urlArea.setText(urlBuilder.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "URL构建失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void encodeURL() {
        String url = urlArea.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入URL", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.name());
            urlArea.setText(encoded);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "URL编码失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void decodeURL() {
        String url = urlArea.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入URL", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String decoded = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
            urlArea.setText(decoded);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "URL解码失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearAll() {
        urlArea.setText("");
        schemeField.setText("");
        hostField.setText("");
        portField.setText("");
        pathField.setText("");
        queryField.setText("");
        fragmentField.setText("");
        parametersArea.setText("");
    }
}