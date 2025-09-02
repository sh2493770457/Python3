package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * UUID生成工具面板
 * 支持各种版本的UUID生成
 */
public class UuidToolPanel extends JPanel {
    
    private JTextArea outputArea;
    private JComboBox<String> versionCombo;
    private JSpinner countSpinner;
    private JCheckBox upperCaseCheckBox;
    private JCheckBox removeDashCheckBox;
    private JCheckBox addBracesCheckBox;
    private JButton generateButton;
    private JButton clearButton;
    private JButton copyButton;
    private JTextField namespaceField;
    private JTextField nameField;
    
    // UUID版本
    private static final String[] UUID_VERSIONS = {
        "Version 1 (时间戳 + MAC地址)",
        "Version 3 (MD5哈希)",
        "Version 4 (随机)",
        "Version 5 (SHA-1哈希)",
        "Nil UUID (全零)",
        "Max UUID (全1)"
    };
    
    // 预定义命名空间
    private static final UUID NAMESPACE_DNS = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
    private static final UUID NAMESPACE_URL = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
    private static final UUID NAMESPACE_OID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8");
    private static final UUID NAMESPACE_X500 = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8");
    
    public UuidToolPanel() {
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建选项面板
        JPanel optionPanel = createOptionPanel();
        
        // 创建输出面板
        JPanel outputPanel = createOutputPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        add(optionPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("生成选项"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // UUID版本
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("UUID版本:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        versionCombo = new JComboBox<>(UUID_VERSIONS);
        versionCombo.setSelectedIndex(2); // 默认Version 4
        panel.add(versionCombo, gbc);
        
        // 生成数量
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("数量:"), gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        countSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(countSpinner, gbc);
        
        // 命名空间（用于Version 3和5）
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("命名空间:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        namespaceField = new JTextField(NAMESPACE_DNS.toString(), 20);
        panel.add(namespaceField, gbc);
        
        // 名称（用于Version 3和5）
        gbc.gridx = 3; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("名称:"), gbc);
        
        gbc.gridx = 4; gbc.gridy = 1;
        nameField = new JTextField("example", 15);
        panel.add(nameField, gbc);
        
        // 格式选项
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 5;
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        upperCaseCheckBox = new JCheckBox("大写", false);
        removeDashCheckBox = new JCheckBox("移除连字符", false);
        addBracesCheckBox = new JCheckBox("添加大括号", false);
        
        formatPanel.add(upperCaseCheckBox);
        formatPanel.add(Box.createHorizontalStrut(10));
        formatPanel.add(removeDashCheckBox);
        formatPanel.add(Box.createHorizontalStrut(10));
        formatPanel.add(addBracesCheckBox);
        
        panel.add(formatPanel, gbc);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("生成的UUID"));
        
        outputArea = new JTextArea(15, 50);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        generateButton = new JButton("生成UUID");
        copyButton = new JButton("复制");
        clearButton = new JButton("清空");
        
        generateButton.setPreferredSize(new Dimension(100, 30));
        copyButton.setPreferredSize(new Dimension(100, 30));
        clearButton.setPreferredSize(new Dimension(100, 30));
        
        panel.add(generateButton);
        panel.add(copyButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateUuids();
            }
        });
        
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearOutput();
            }
        });
        
        versionCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUIForVersion();
            }
        });
        
        // 初始化UI状态
        updateUIForVersion();
    }
    
    private void updateUIForVersion() {
        int version = versionCombo.getSelectedIndex();
        boolean needsNamespace = (version == 1 || version == 3); // Version 3 or 5
        
        namespaceField.setEnabled(needsNamespace);
        nameField.setEnabled(needsNamespace);
    }
    
    private void generateUuids() {
        try {
            int count = (Integer) countSpinner.getValue();
            int version = versionCombo.getSelectedIndex();
            
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < count; i++) {
                String uuid = generateUuid(version);
                uuid = formatUuid(uuid);
                result.append(uuid);
                
                if (i < count - 1) {
                    result.append("\n");
                }
            }
            
            outputArea.setText(result.toString());
        } catch (Exception e) {
            outputArea.setText("生成UUID出错: " + e.getMessage());
        }
    }
    
    private String generateUuid(int version) throws Exception {
        switch (version) {
            case 0: // Version 1 (时间戳)
                return generateVersion1Uuid();
            case 1: // Version 3 (MD5)
                return generateVersion3Uuid();
            case 2: // Version 4 (随机)
                return UUID.randomUUID().toString();
            case 3: // Version 5 (SHA-1)
                return generateVersion5Uuid();
            case 4: // Nil UUID
                return "00000000-0000-0000-0000-000000000000";
            case 5: // Max UUID
                return "ffffffff-ffff-ffff-ffff-ffffffffffff";
            default:
                return UUID.randomUUID().toString();
        }
    }
    
    private String generateVersion1Uuid() {
        // 简化的Version 1实现
        long timestamp = System.currentTimeMillis();
        Random random = new SecureRandom();
        
        // 时间戳部分（60位）
        long time = timestamp * 10000 + 0x01B21DD213814000L;
        
        // 时钟序列（14位）
        int clockSeq = random.nextInt(0x4000);
        
        // 节点ID（48位，这里用随机数模拟）
        long node = random.nextLong() & 0xFFFFFFFFFFFFL;
        
        // 构造UUID
        long mostSigBits = (time & 0xFFFFFFFFL) << 32;
        mostSigBits |= ((time >>> 32) & 0xFFFFL) << 16;
        mostSigBits |= 0x1000 | ((time >>> 48) & 0x0FFFL);
        
        long leastSigBits = (clockSeq & 0x3FFFL) | 0x8000L;
        leastSigBits <<= 48;
        leastSigBits |= node;
        
        return new UUID(mostSigBits, leastSigBits).toString();
    }
    
    private String generateVersion3Uuid() throws Exception {
        String namespace = namespaceField.getText().trim();
        String name = nameField.getText().trim();
        
        if (namespace.isEmpty() || name.isEmpty()) {
            throw new Exception("命名空间和名称不能为空");
        }
        
        UUID namespaceUuid = UUID.fromString(namespace);
        return generateNameBasedUuid(namespaceUuid, name, "MD5", 3);
    }
    
    private String generateVersion5Uuid() throws Exception {
        String namespace = namespaceField.getText().trim();
        String name = nameField.getText().trim();
        
        if (namespace.isEmpty() || name.isEmpty()) {
            throw new Exception("命名空间和名称不能为空");
        }
        
        UUID namespaceUuid = UUID.fromString(namespace);
        return generateNameBasedUuid(namespaceUuid, name, "SHA-1", 5);
    }
    
    private String generateNameBasedUuid(UUID namespace, String name, String algorithm, int version) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        
        // 添加命名空间UUID的字节
        md.update(uuidToBytes(namespace));
        
        // 添加名称的字节
        md.update(name.getBytes("UTF-8"));
        
        byte[] hash = md.digest();
        
        // 设置版本和变体位
        hash[6] &= 0x0f;
        hash[6] |= (version << 4);
        hash[8] &= 0x3f;
        hash[8] |= 0x80;
        
        // 构造UUID
        long mostSigBits = 0;
        long leastSigBits = 0;
        
        for (int i = 0; i < 8; i++) {
            mostSigBits = (mostSigBits << 8) | (hash[i] & 0xff);
        }
        
        for (int i = 8; i < 16; i++) {
            leastSigBits = (leastSigBits << 8) | (hash[i] & 0xff);
        }
        
        return new UUID(mostSigBits, leastSigBits).toString();
    }
    
    private byte[] uuidToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (mostSigBits >>> (8 * (7 - i)));
        }
        
        for (int i = 8; i < 16; i++) {
            bytes[i] = (byte) (leastSigBits >>> (8 * (7 - (i - 8))));
        }
        
        return bytes;
    }
    
    private String formatUuid(String uuid) {
        String result = uuid;
        
        if (upperCaseCheckBox.isSelected()) {
            result = result.toUpperCase();
        }
        
        if (removeDashCheckBox.isSelected()) {
            result = result.replace("-", "");
        }
        
        if (addBracesCheckBox.isSelected()) {
            result = "{" + result + "}";
        }
        
        return result;
    }
    
    private void copyToClipboard() {
        String text = outputArea.getText();
        if (!text.isEmpty()) {
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(text), null);
                JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "复制失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearOutput() {
        outputArea.setText("");
    }
}