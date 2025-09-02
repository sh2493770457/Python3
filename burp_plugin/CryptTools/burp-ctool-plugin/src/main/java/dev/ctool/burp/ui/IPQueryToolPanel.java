package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * IP地址查询工具面板
 * 支持IP地址信息查询，包括地理位置、ISP等信息
 */
public class IPQueryToolPanel extends JPanel {
    
    private JTextField ipField;
    private JTextArea resultArea;
    private JButton queryButton;
    private JButton clearButton;
    private JButton getMyIPButton;
    
    // IP地址正则表达式
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    
    public IPQueryToolPanel() {
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建结果面板
        JPanel resultPanel = createResultPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("IP地址输入"));
        
        JLabel label = new JLabel("IP地址: ");
        ipField = new JTextField(20);
        ipField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        
        JPanel inputGroup = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputGroup.add(label);
        inputGroup.add(ipField);
        
        panel.add(inputGroup, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("查询结果"));
        
        resultArea = new JTextArea(15, 50);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        queryButton = new JButton("查询");
        clearButton = new JButton("清空");
        getMyIPButton = new JButton("获取本机IP");
        
        queryButton.setPreferredSize(new Dimension(100, 30));
        clearButton.setPreferredSize(new Dimension(100, 30));
        getMyIPButton.setPreferredSize(new Dimension(120, 30));
        
        panel.add(queryButton);
        panel.add(clearButton);
        panel.add(getMyIPButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryIP();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        getMyIPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getMyIP();
            }
        });
        
        // 回车键查询
        ipField.addActionListener(e -> queryIP());
    }
    
    private void queryIP() {
        String ip = ipField.getText().trim();
        if (ip.isEmpty()) {
            resultArea.setText("请输入IP地址");
            return;
        }
        
        if (!isValidIP(ip)) {
            resultArea.setText("无效的IP地址格式");
            return;
        }
        
        // 在新线程中执行查询，避免阻塞UI
        SwingUtilities.invokeLater(() -> {
            resultArea.setText("正在查询...");
            queryButton.setEnabled(false);
        });
        
        new Thread(() -> {
            try {
                String result = performIPQuery(ip);
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText(result);
                    queryButton.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText("查询失败: " + e.getMessage());
                    queryButton.setEnabled(true);
                });
            }
        }).start();
    }
    
    private String performIPQuery(String ip) {
        StringBuilder result = new StringBuilder();
        
        try {
            // 基本IP信息
            result.append("=== IP地址信息 ===").append("\n");
            result.append("IP地址: ").append(ip).append("\n");
            
            // 检查IP类型
            InetAddress addr = InetAddress.getByName(ip);
            result.append("IP类型: ");
            if (addr.isLoopbackAddress()) {
                result.append("回环地址");
            } else if (addr.isLinkLocalAddress()) {
                result.append("链路本地地址");
            } else if (addr.isSiteLocalAddress()) {
                result.append("站点本地地址(私有IP)");
            } else {
                result.append("公网地址");
            }
            result.append("\n");
            
            // 主机名解析
            try {
                String hostname = addr.getHostName();
                if (!hostname.equals(ip)) {
                    result.append("主机名: ").append(hostname).append("\n");
                }
            } catch (Exception e) {
                result.append("主机名: 无法解析\n");
            }
            
            // IP分类信息
            result.append("\n=== IP分类信息 ===").append("\n");
            String[] parts = ip.split("\\.");
            int firstOctet = Integer.parseInt(parts[0]);
            
            if (firstOctet >= 1 && firstOctet <= 126) {
                result.append("IP类别: A类地址\n");
                result.append("网络位: 8位\n");
                result.append("主机位: 24位\n");
                result.append("默认子网掩码: 255.0.0.0\n");
            } else if (firstOctet >= 128 && firstOctet <= 191) {
                result.append("IP类别: B类地址\n");
                result.append("网络位: 16位\n");
                result.append("主机位: 16位\n");
                result.append("默认子网掩码: 255.255.0.0\n");
            } else if (firstOctet >= 192 && firstOctet <= 223) {
                result.append("IP类别: C类地址\n");
                result.append("网络位: 24位\n");
                result.append("主机位: 8位\n");
                result.append("默认子网掩码: 255.255.255.0\n");
            } else if (firstOctet >= 224 && firstOctet <= 239) {
                result.append("IP类别: D类地址(组播)\n");
            } else if (firstOctet >= 240 && firstOctet <= 255) {
                result.append("IP类别: E类地址(保留)\n");
            }
            
            // 私有IP检查
            result.append("\n=== 特殊IP检查 ===").append("\n");
            if (isPrivateIP(ip)) {
                result.append("私有IP: 是\n");
                if (ip.startsWith("10.")) {
                    result.append("私有IP范围: 10.0.0.0/8\n");
                } else if (ip.startsWith("172.")) {
                    int second = Integer.parseInt(parts[1]);
                    if (second >= 16 && second <= 31) {
                        result.append("私有IP范围: 172.16.0.0/12\n");
                    }
                } else if (ip.startsWith("192.168.")) {
                    result.append("私有IP范围: 192.168.0.0/16\n");
                }
            } else {
                result.append("私有IP: 否\n");
            }
            
            // 二进制表示
            result.append("\n=== 二进制表示 ===").append("\n");
            for (int i = 0; i < parts.length; i++) {
                int octet = Integer.parseInt(parts[i]);
                String binary = String.format("%8s", Integer.toBinaryString(octet)).replace(' ', '0');
                result.append(parts[i]).append(" = ").append(binary);
                if (i < parts.length - 1) {
                    result.append(".\n");
                }
            }
            result.append("\n");
            
            // 十六进制表示
            result.append("\n=== 十六进制表示 ===").append("\n");
            for (int i = 0; i < parts.length; i++) {
                int octet = Integer.parseInt(parts[i]);
                String hex = String.format("%02X", octet);
                result.append(parts[i]).append(" = 0x").append(hex);
                if (i < parts.length - 1) {
                    result.append(".\n");
                }
            }
            result.append("\n");
            
        } catch (Exception e) {
            result.append("查询出错: ").append(e.getMessage()).append("\n");
        }
        
        return result.toString();
    }
    
    private void getMyIP() {
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText("正在获取本机IP...");
                    getMyIPButton.setEnabled(false);
                });
                
                StringBuilder result = new StringBuilder();
                result.append("=== 本机IP信息 ===").append("\n");
                
                // 获取本地IP
                InetAddress localHost = InetAddress.getLocalHost();
                result.append("本地IP: ").append(localHost.getHostAddress()).append("\n");
                result.append("主机名: ").append(localHost.getHostName()).append("\n");
                
                // 尝试获取公网IP（简单实现）
                try {
                    String publicIP = getPublicIP();
                    if (publicIP != null && !publicIP.isEmpty()) {
                        result.append("公网IP: ").append(publicIP).append("\n");
                    }
                } catch (Exception e) {
                    result.append("公网IP: 获取失败\n");
                }
                
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText(result.toString());
                    getMyIPButton.setEnabled(true);
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText("获取本机IP失败: " + e.getMessage());
                    getMyIPButton.setEnabled(true);
                });
            }
        }).start();
    }
    
    private String getPublicIP() throws Exception {
        // 使用简单的HTTP服务获取公网IP
        URL url = new URL("http://checkip.amazonaws.com/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.readLine().trim();
        }
    }
    
    private boolean isValidIP(String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }
    
    private boolean isPrivateIP(String ip) {
        String[] parts = ip.split("\\.");
        int first = Integer.parseInt(parts[0]);
        int second = Integer.parseInt(parts[1]);
        
        // 10.0.0.0/8
        if (first == 10) {
            return true;
        }
        
        // 172.16.0.0/12
        if (first == 172 && second >= 16 && second <= 31) {
            return true;
        }
        
        // 192.168.0.0/16
        if (first == 192 && second == 168) {
            return true;
        }
        
        return false;
    }
    
    private void clearAll() {
        ipField.setText("");
        resultArea.setText("");
    }
}