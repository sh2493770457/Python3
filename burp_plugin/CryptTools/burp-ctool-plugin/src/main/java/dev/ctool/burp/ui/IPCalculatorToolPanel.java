package dev.ctool.burp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class IPCalculatorToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // IPv4计算面板组件
    private JTextField ipv4Field;
    private JTextField subnetMaskField;
    private JTextArea ipv4ResultArea;
    private JButton calculateIPv4Button;
    private JButton clearIPv4Button;
    
    // IPv6计算面板组件
    private JTextField ipv6Field;
    private JTextField prefixLengthField;
    private JTextArea ipv6ResultArea;
    private JButton calculateIPv6Button;
    private JButton clearIPv6Button;
    
    // IP转换面板组件
    private JTextField ipAddressField;
    private JTextField binaryField;
    private JTextField hexField;
    private JTextField decimalField;
    private JButton convertButton;
    private JButton clearConvertButton;
    
    public IPCalculatorToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // IPv4计算组件
        ipv4Field = new JTextField(20);
        ipv4Field.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ipv4Field.setText("192.168.1.100");
        
        subnetMaskField = new JTextField(20);
        subnetMaskField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        subnetMaskField.setText("255.255.255.0");
        
        ipv4ResultArea = new JTextArea(15, 40);
        ipv4ResultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ipv4ResultArea.setEditable(false);
        ipv4ResultArea.setBackground(new Color(248, 248, 248));
        
        calculateIPv4Button = new JButton("计算IPv4网络");
        clearIPv4Button = new JButton("清空");
        
        // IPv6计算组件
        ipv6Field = new JTextField(30);
        ipv6Field.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ipv6Field.setText("2001:db8:85a3::8a2e:370:7334");
        
        prefixLengthField = new JTextField(10);
        prefixLengthField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        prefixLengthField.setText("64");
        
        ipv6ResultArea = new JTextArea(15, 40);
        ipv6ResultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ipv6ResultArea.setEditable(false);
        ipv6ResultArea.setBackground(new Color(248, 248, 248));
        
        calculateIPv6Button = new JButton("计算IPv6网络");
        clearIPv6Button = new JButton("清空");
        
        // IP转换组件
        ipAddressField = new JTextField(20);
        ipAddressField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ipAddressField.setText("192.168.1.1");
        
        binaryField = new JTextField(35);
        binaryField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        binaryField.setEditable(false);
        binaryField.setBackground(new Color(248, 248, 248));
        
        hexField = new JTextField(20);
        hexField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        hexField.setEditable(false);
        hexField.setBackground(new Color(248, 248, 248));
        
        decimalField = new JTextField(20);
        decimalField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        decimalField.setEditable(false);
        decimalField.setBackground(new Color(248, 248, 248));
        
        convertButton = new JButton("转换");
        clearConvertButton = new JButton("清空");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // IPv4计算面板
        JPanel ipv4Panel = createIPv4Panel();
        tabbedPane.addTab("IPv4网络计算", ipv4Panel);
        
        // IPv6计算面板
        JPanel ipv6Panel = createIPv6Panel();
        tabbedPane.addTab("IPv6网络计算", ipv6Panel);
        
        // IP转换面板
        JPanel convertPanel = createConvertPanel();
        tabbedPane.addTab("IP地址转换", convertPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createIPv4Panel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("IPv4网络计算"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("IP地址:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(ipv4Field, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("子网掩码:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(subnetMaskField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(calculateIPv4Button);
        buttonPanel.add(clearIPv4Button);
        
        // 结果面板
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("计算结果"));
        resultPanel.add(new JScrollPane(ipv4ResultArea), BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(resultPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createIPv6Panel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("IPv6网络计算"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("IPv6地址:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(ipv6Field, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("前缀长度:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(prefixLengthField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(calculateIPv6Button);
        buttonPanel.add(clearIPv6Button);
        
        // 结果面板
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("计算结果"));
        resultPanel.add(new JScrollPane(ipv6ResultArea), BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(resultPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createConvertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("IP地址转换"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("IP地址:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(ipAddressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("二进制:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(binaryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("十六进制:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(hexField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("十进制:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(decimalField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(convertButton);
        buttonPanel.add(clearConvertButton);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // IPv4计算事件
        calculateIPv4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateIPv4Network();
            }
        });
        
        clearIPv4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearIPv4();
            }
        });
        
        // IPv6计算事件
        calculateIPv6Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateIPv6Network();
            }
        });
        
        clearIPv6Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearIPv6();
            }
        });
        
        // IP转换事件
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertIPAddress();
            }
        });
        
        clearConvertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearConvert();
            }
        });
    }
    
    private void calculateIPv4Network() {
        try {
            String ipStr = ipv4Field.getText().trim();
            String maskStr = subnetMaskField.getText().trim();
            
            if (ipStr.isEmpty() || maskStr.isEmpty()) {
                ipv4ResultArea.setText("请输入IP地址和子网掩码");
                return;
            }
            
            // 解析IP地址和子网掩码
            long ip = parseIPv4(ipStr);
            long mask = parseIPv4(maskStr);
            
            // 计算网络信息
            long network = ip & mask;
            long broadcast = network | (~mask & 0xFFFFFFFFL);
            long hostMin = network + 1;
            long hostMax = broadcast - 1;
            long hostCount = hostMax - hostMin + 1;
            
            // 计算CIDR前缀长度
            int cidr = Long.bitCount(mask);
            
            // 格式化结果
            StringBuilder result = new StringBuilder();
            result.append("网络信息计算结果:\n");
            result.append("================================\n");
            result.append("IP地址: ").append(ipStr).append("\n");
            result.append("子网掩码: ").append(maskStr).append("\n");
            result.append("CIDR表示: ").append(ipStr).append("/").append(cidr).append("\n");
            result.append("\n网络地址: ").append(formatIPv4(network)).append("\n");
            result.append("广播地址: ").append(formatIPv4(broadcast)).append("\n");
            result.append("主机范围: ").append(formatIPv4(hostMin)).append(" - ").append(formatIPv4(hostMax)).append("\n");
            result.append("可用主机数: ").append(hostCount).append("\n");
            result.append("\n二进制表示:\n");
            result.append("IP地址:   ").append(toBinary32(ip)).append("\n");
            result.append("子网掩码: ").append(toBinary32(mask)).append("\n");
            result.append("网络地址: ").append(toBinary32(network)).append("\n");
            result.append("广播地址: ").append(toBinary32(broadcast)).append("\n");
            
            ipv4ResultArea.setText(result.toString());
            
        } catch (Exception e) {
            ipv4ResultArea.setText("计算失败: " + e.getMessage());
        }
    }
    
    private void calculateIPv6Network() {
        try {
            String ipv6Str = ipv6Field.getText().trim();
            String prefixStr = prefixLengthField.getText().trim();
            
            if (ipv6Str.isEmpty() || prefixStr.isEmpty()) {
                ipv6ResultArea.setText("请输入IPv6地址和前缀长度");
                return;
            }
            
            int prefixLength = Integer.parseInt(prefixStr);
            if (prefixLength < 0 || prefixLength > 128) {
                ipv6ResultArea.setText("前缀长度必须在0-128之间");
                return;
            }
            
            // 解析IPv6地址
            InetAddress addr = InetAddress.getByName(ipv6Str);
            byte[] ipBytes = addr.getAddress();
            
            // 计算网络前缀
            byte[] networkBytes = new byte[16];
            int fullBytes = prefixLength / 8;
            int remainingBits = prefixLength % 8;
            
            // 复制完整字节
            System.arraycopy(ipBytes, 0, networkBytes, 0, fullBytes);
            
            // 处理部分字节
            if (remainingBits > 0 && fullBytes < 16) {
                int mask = (0xFF << (8 - remainingBits)) & 0xFF;
                networkBytes[fullBytes] = (byte) (ipBytes[fullBytes] & mask);
            }
            
            // 格式化结果
            StringBuilder result = new StringBuilder();
            result.append("IPv6网络信息计算结果:\n");
            result.append("================================\n");
            result.append("IPv6地址: ").append(ipv6Str).append("\n");
            result.append("前缀长度: /").append(prefixLength).append("\n");
            result.append("网络前缀: ").append(formatIPv6(networkBytes)).append("/").append(prefixLength).append("\n");
            result.append("\n地址类型分析:\n");
            
            // 分析地址类型
            if (isIPv6Loopback(ipBytes)) {
                result.append("- 回环地址 (::1)\n");
            } else if (isIPv6LinkLocal(ipBytes)) {
                result.append("- 链路本地地址 (fe80::/10)\n");
            } else if (isIPv6UniqueLocal(ipBytes)) {
                result.append("- 唯一本地地址 (fc00::/7)\n");
            } else if (isIPv6Multicast(ipBytes)) {
                result.append("- 组播地址 (ff00::/8)\n");
            } else if (isIPv6Global(ipBytes)) {
                result.append("- 全球单播地址\n");
            }
            
            result.append("\n十六进制表示:\n");
            result.append(formatIPv6Hex(ipBytes));
            
            ipv6ResultArea.setText(result.toString());
            
        } catch (NumberFormatException e) {
            ipv6ResultArea.setText("前缀长度格式错误");
        } catch (UnknownHostException e) {
            ipv6ResultArea.setText("IPv6地址格式错误");
        } catch (Exception e) {
            ipv6ResultArea.setText("计算失败: " + e.getMessage());
        }
    }
    
    private void convertIPAddress() {
        try {
            String ipStr = ipAddressField.getText().trim();
            if (ipStr.isEmpty()) {
                binaryField.setText("请输入IP地址");
                return;
            }
            
            // 解析IPv4地址
            long ip = parseIPv4(ipStr);
            
            // 转换为各种格式
            String binary = toBinary32(ip);
            String hex = String.format("0x%08X", ip);
            String decimal = String.valueOf(ip);
            
            binaryField.setText(binary);
            hexField.setText(hex);
            decimalField.setText(decimal);
            
        } catch (Exception e) {
            binaryField.setText("转换失败: " + e.getMessage());
            hexField.setText("");
            decimalField.setText("");
        }
    }
    
    // 辅助方法
    private long parseIPv4(String ip) throws Exception {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new Exception("IPv4地址格式错误");
        }
        
        long result = 0;
        for (int i = 0; i < 4; i++) {
            int octet = Integer.parseInt(parts[i]);
            if (octet < 0 || octet > 255) {
                throw new Exception("IPv4地址范围错误");
            }
            result = (result << 8) | octet;
        }
        return result;
    }
    
    private String formatIPv4(long ip) {
        return String.format("%d.%d.%d.%d",
            (ip >> 24) & 0xFF,
            (ip >> 16) & 0xFF,
            (ip >> 8) & 0xFF,
            ip & 0xFF);
    }
    
    private String toBinary32(long value) {
        String binary = String.format("%32s", Long.toBinaryString(value)).replace(' ', '0');
        return binary.substring(0, 8) + "." + binary.substring(8, 16) + "." + 
               binary.substring(16, 24) + "." + binary.substring(24, 32);
    }
    
    private String formatIPv6(byte[] bytes) {
        try {
            InetAddress addr = InetAddress.getByAddress(bytes);
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            return "格式化失败";
        }
    }
    
    private String formatIPv6Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i += 2) {
            if (i > 0) sb.append(":");
            sb.append(String.format("%02x%02x", bytes[i] & 0xFF, bytes[i + 1] & 0xFF));
        }
        return sb.toString();
    }
    
    private boolean isIPv6Loopback(byte[] bytes) {
        for (int i = 0; i < 15; i++) {
            if (bytes[i] != 0) return false;
        }
        return bytes[15] == 1;
    }
    
    private boolean isIPv6LinkLocal(byte[] bytes) {
        return (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xC0) == 0x80;
    }
    
    private boolean isIPv6UniqueLocal(byte[] bytes) {
        return (bytes[0] & 0xFE) == 0xFC;
    }
    
    private boolean isIPv6Multicast(byte[] bytes) {
        return (bytes[0] & 0xFF) == 0xFF;
    }
    
    private boolean isIPv6Global(byte[] bytes) {
        return !isIPv6Loopback(bytes) && !isIPv6LinkLocal(bytes) && 
               !isIPv6UniqueLocal(bytes) && !isIPv6Multicast(bytes);
    }
    
    private void clearIPv4() {
        ipv4Field.setText("192.168.1.100");
        subnetMaskField.setText("255.255.255.0");
        ipv4ResultArea.setText("");
    }
    
    private void clearIPv6() {
        ipv6Field.setText("2001:db8:85a3::8a2e:370:7334");
        prefixLengthField.setText("64");
        ipv6ResultArea.setText("");
    }
    
    private void clearConvert() {
        ipAddressField.setText("192.168.1.1");
        binaryField.setText("");
        hexField.setText("");
        decimalField.setText("");
    }
}