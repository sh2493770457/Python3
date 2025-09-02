package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ARMToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // ARM到十六进制面板组件
    private JTextArea armInputArea;
    private JTextArea arm64OutputArea;
    private JTextArea armOutputArea;
    private JTextArea thumbOutputArea;
    private JTextField offsetField;
    private JCheckBox prefixCheckBox;
    private JCheckBox swapEndianCheckBox;
    private JButton armToHexButton;
    
    // 十六进制到ARM面板组件
    private JTextArea hexInputArea;
    private JTextArea hexArm64OutputArea;
    private JTextArea hexArmOutputArea;
    private JTextArea hexArmBeOutputArea;
    private JTextArea hexThumbOutputArea;
    private JTextArea hexThumbBeOutputArea;
    private JTextField hexOffsetField;
    private JButton hexToArmButton;
    
    // 基础ARM指令映射表
    private static final Map<String, String> ARM_INSTRUCTIONS = new HashMap<>();
    private static final Map<String, String> THUMB_INSTRUCTIONS = new HashMap<>();
    
    static {
        // 初始化基础ARM指令映射
        ARM_INSTRUCTIONS.put("NOP", "E1A00000");
        ARM_INSTRUCTIONS.put("MOV R0, R0", "E1A00000");
        ARM_INSTRUCTIONS.put("MOV R0, #0", "E3A00000");
        ARM_INSTRUCTIONS.put("MOV R1, #1", "E3A01001");
        ARM_INSTRUCTIONS.put("ADD R0, R0, #1", "E2800001");
        ARM_INSTRUCTIONS.put("SUB R0, R0, #1", "E2400001");
        ARM_INSTRUCTIONS.put("LDR R0, [R1]", "E5910000");
        ARM_INSTRUCTIONS.put("STR R0, [R1]", "E5810000");
        ARM_INSTRUCTIONS.put("B #0", "EA000000");
        ARM_INSTRUCTIONS.put("BL #0", "EB000000");
        ARM_INSTRUCTIONS.put("BX LR", "E12FFF1E");
        ARM_INSTRUCTIONS.put("PUSH {LR}", "E52DE004");
        ARM_INSTRUCTIONS.put("POP {PC}", "E49DF004");
        
        // 初始化基础THUMB指令映射
        THUMB_INSTRUCTIONS.put("NOP", "46C0");
        THUMB_INSTRUCTIONS.put("MOV R0, R0", "4600");
        THUMB_INSTRUCTIONS.put("MOV R0, #0", "2000");
        THUMB_INSTRUCTIONS.put("MOV R1, #1", "2101");
        THUMB_INSTRUCTIONS.put("ADD R0, #1", "3001");
        THUMB_INSTRUCTIONS.put("SUB R0, #1", "3801");
        THUMB_INSTRUCTIONS.put("LDR R0, [R1]", "6808");
        THUMB_INSTRUCTIONS.put("STR R0, [R1]", "6008");
        THUMB_INSTRUCTIONS.put("B #0", "E000");
        THUMB_INSTRUCTIONS.put("BL #0", "F000F800");
        THUMB_INSTRUCTIONS.put("BX LR", "4770");
        THUMB_INSTRUCTIONS.put("PUSH {LR}", "B500");
        THUMB_INSTRUCTIONS.put("POP {PC}", "BD00");
    }
    
    public ARMToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // ARM到十六进制组件
        armInputArea = new JTextArea(15, 40);
        armInputArea.setLineWrap(true);
        armInputArea.setWrapStyleWord(true);
        
        arm64OutputArea = new JTextArea(5, 40);
        arm64OutputArea.setLineWrap(true);
        arm64OutputArea.setWrapStyleWord(true);
        arm64OutputArea.setEditable(false);
        
        armOutputArea = new JTextArea(5, 40);
        armOutputArea.setLineWrap(true);
        armOutputArea.setWrapStyleWord(true);
        armOutputArea.setEditable(false);
        
        thumbOutputArea = new JTextArea(5, 40);
        thumbOutputArea.setLineWrap(true);
        thumbOutputArea.setWrapStyleWord(true);
        thumbOutputArea.setEditable(false);
        
        offsetField = new JTextField("0", 10);
        prefixCheckBox = new JCheckBox("0x前缀", false);
        swapEndianCheckBox = new JCheckBox("GDB/LLDB字节序", false);
        armToHexButton = new JButton("转换为十六进制");
        
        // 十六进制到ARM组件
        hexInputArea = new JTextArea(15, 40);
        hexInputArea.setLineWrap(true);
        hexInputArea.setWrapStyleWord(true);
        
        hexArm64OutputArea = new JTextArea(3, 40);
        hexArm64OutputArea.setLineWrap(true);
        hexArm64OutputArea.setWrapStyleWord(true);
        hexArm64OutputArea.setEditable(false);
        
        hexArmOutputArea = new JTextArea(3, 40);
        hexArmOutputArea.setLineWrap(true);
        hexArmOutputArea.setWrapStyleWord(true);
        hexArmOutputArea.setEditable(false);
        
        hexArmBeOutputArea = new JTextArea(3, 40);
        hexArmBeOutputArea.setLineWrap(true);
        hexArmBeOutputArea.setWrapStyleWord(true);
        hexArmBeOutputArea.setEditable(false);
        
        hexThumbOutputArea = new JTextArea(3, 40);
        hexThumbOutputArea.setLineWrap(true);
        hexThumbOutputArea.setWrapStyleWord(true);
        hexThumbOutputArea.setEditable(false);
        
        hexThumbBeOutputArea = new JTextArea(3, 40);
        hexThumbBeOutputArea.setLineWrap(true);
        hexThumbBeOutputArea.setWrapStyleWord(true);
        hexThumbBeOutputArea.setEditable(false);
        
        hexOffsetField = new JTextField("0", 10);
        hexToArmButton = new JButton("转换为汇编");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // ARM到十六进制面板
        JPanel armToHexPanel = createArmToHexPanel();
        tabbedPane.addTab("ARM → 十六进制", armToHexPanel);
        
        // 十六进制到ARM面板
        JPanel hexToArmPanel = createHexToArmPanel();
        tabbedPane.addTab("十六进制 → ARM", hexToArmPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createArmToHexPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        configPanel.add(new JLabel("偏移量 (hex):"));
        configPanel.add(offsetField);
        configPanel.add(prefixCheckBox);
        configPanel.add(swapEndianCheckBox);
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("ARM汇编代码"));
        JTextArea placeholderArea = new JTextArea("输入ARM汇编代码示例:\nNOP\nMOV R0, #0\nADD R0, R0, #1\nBX LR");
        placeholderArea.setEditable(false);
        placeholderArea.setBackground(getBackground());
        placeholderArea.setForeground(Color.GRAY);
        
        JSplitPane inputSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
            new JScrollPane(armInputArea), 
            new JScrollPane(placeholderArea));
        inputSplit.setDividerLocation(200);
        inputPanel.add(inputSplit, BorderLayout.CENTER);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        JPanel arm64Panel = new JPanel(new BorderLayout());
        arm64Panel.setBorder(new TitledBorder("ARM64"));
        arm64Panel.add(new JScrollPane(arm64OutputArea), BorderLayout.CENTER);
        
        JPanel armPanel = new JPanel(new BorderLayout());
        armPanel.setBorder(new TitledBorder("ARM"));
        armPanel.add(new JScrollPane(armOutputArea), BorderLayout.CENTER);
        
        JPanel thumbPanel = new JPanel(new BorderLayout());
        thumbPanel.setBorder(new TitledBorder("THUMB"));
        thumbPanel.add(new JScrollPane(thumbOutputArea), BorderLayout.CENTER);
        
        outputPanel.add(arm64Panel);
        outputPanel.add(armPanel);
        outputPanel.add(thumbPanel);
        
        // 主要内容面板
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, outputPanel);
        mainSplit.setDividerLocation(400);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(armToHexButton);
        
        panel.add(configPanel, BorderLayout.NORTH);
        panel.add(mainSplit, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createHexToArmPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 配置面板
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        configPanel.add(new JLabel("偏移量 (hex):"));
        configPanel.add(hexOffsetField);
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("十六进制代码"));
        JTextArea hexPlaceholderArea = new JTextArea("输入十六进制代码示例:\nE1A00000\nE3A00000\nE2800001\nE12FFF1E");
        hexPlaceholderArea.setEditable(false);
        hexPlaceholderArea.setBackground(getBackground());
        hexPlaceholderArea.setForeground(Color.GRAY);
        
        JSplitPane hexInputSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
            new JScrollPane(hexInputArea), 
            new JScrollPane(hexPlaceholderArea));
        hexInputSplit.setDividerLocation(200);
        inputPanel.add(hexInputSplit, BorderLayout.CENTER);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new GridLayout(5, 1, 2, 2));
        
        JPanel hexArm64Panel = new JPanel(new BorderLayout());
        hexArm64Panel.setBorder(new TitledBorder("ARM64"));
        hexArm64Panel.add(new JScrollPane(hexArm64OutputArea), BorderLayout.CENTER);
        
        JPanel hexArmPanel = new JPanel(new BorderLayout());
        hexArmPanel.setBorder(new TitledBorder("ARM"));
        hexArmPanel.add(new JScrollPane(hexArmOutputArea), BorderLayout.CENTER);
        
        JPanel hexArmBePanel = new JPanel(new BorderLayout());
        hexArmBePanel.setBorder(new TitledBorder("ARM Big Endian"));
        hexArmBePanel.add(new JScrollPane(hexArmBeOutputArea), BorderLayout.CENTER);
        
        JPanel hexThumbPanel = new JPanel(new BorderLayout());
        hexThumbPanel.setBorder(new TitledBorder("THUMB"));
        hexThumbPanel.add(new JScrollPane(hexThumbOutputArea), BorderLayout.CENTER);
        
        JPanel hexThumbBePanel = new JPanel(new BorderLayout());
        hexThumbBePanel.setBorder(new TitledBorder("THUMB Big Endian"));
        hexThumbBePanel.add(new JScrollPane(hexThumbBeOutputArea), BorderLayout.CENTER);
        
        outputPanel.add(hexArm64Panel);
        outputPanel.add(hexArmPanel);
        outputPanel.add(hexArmBePanel);
        outputPanel.add(hexThumbPanel);
        outputPanel.add(hexThumbBePanel);
        
        // 主要内容面板
        JSplitPane hexMainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, outputPanel);
        hexMainSplit.setDividerLocation(400);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(hexToArmButton);
        
        panel.add(configPanel, BorderLayout.NORTH);
        panel.add(hexMainSplit, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        armToHexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertArmToHex();
            }
        });
        
        hexToArmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertHexToArm();
            }
        });
    }
    
    private void convertArmToHex() {
        String input = armInputArea.getText().trim();
        if (input.isEmpty()) {
            arm64OutputArea.setText("");
            armOutputArea.setText("");
            thumbOutputArea.setText("");
            return;
        }
        
        try {
            String[] lines = input.split("\n");
            StringBuilder arm64Result = new StringBuilder();
            StringBuilder armResult = new StringBuilder();
            StringBuilder thumbResult = new StringBuilder();
            
            for (String line : lines) {
                line = line.trim().toUpperCase();
                if (line.isEmpty()) continue;
                
                // 处理ARM64指令（简化版本）
                String arm64Hex = convertToArm64Hex(line);
                if (arm64Hex != null) {
                    arm64Result.append(formatHex(arm64Hex)).append("\n");
                } else {
                    arm64Result.append("# 未知指令: ").append(line).append("\n");
                }
                
                // 处理ARM指令
                String armHex = ARM_INSTRUCTIONS.get(line);
                if (armHex != null) {
                    armResult.append(formatHex(armHex)).append("\n");
                } else {
                    armResult.append("# 未知指令: ").append(line).append("\n");
                }
                
                // 处理THUMB指令
                String thumbHex = THUMB_INSTRUCTIONS.get(line);
                if (thumbHex != null) {
                    thumbResult.append(formatHex(thumbHex)).append("\n");
                } else {
                    thumbResult.append("# 未知指令: ").append(line).append("\n");
                }
            }
            
            arm64OutputArea.setText(arm64Result.toString());
            armOutputArea.setText(armResult.toString());
            thumbOutputArea.setText(thumbResult.toString());
            
        } catch (Exception e) {
            arm64OutputArea.setText("转换错误: " + e.getMessage());
            armOutputArea.setText("转换错误: " + e.getMessage());
            thumbOutputArea.setText("转换错误: " + e.getMessage());
        }
    }
    
    private void convertHexToArm() {
        String input = hexInputArea.getText().trim();
        if (input.isEmpty()) {
            hexArm64OutputArea.setText("");
            hexArmOutputArea.setText("");
            hexArmBeOutputArea.setText("");
            hexThumbOutputArea.setText("");
            hexThumbBeOutputArea.setText("");
            return;
        }
        
        try {
            String[] lines = input.split("\n");
            StringBuilder arm64Result = new StringBuilder();
            StringBuilder armResult = new StringBuilder();
            StringBuilder armBeResult = new StringBuilder();
            StringBuilder thumbResult = new StringBuilder();
            StringBuilder thumbBeResult = new StringBuilder();
            
            for (String line : lines) {
                line = line.trim().toUpperCase().replaceAll("0X", "").replaceAll("\\s+", "");
                if (line.isEmpty()) continue;
                
                // 查找对应的汇编指令
                String armInstruction = findArmInstruction(line);
                String thumbInstruction = findThumbInstruction(line);
                
                // ARM64结果（简化处理）
                if (armInstruction != null) {
                    arm64Result.append(convertToArm64Instruction(armInstruction)).append("\n");
                } else {
                    arm64Result.append("# 未知十六进制: ").append(line).append("\n");
                }
                
                // ARM结果
                if (armInstruction != null) {
                    armResult.append(armInstruction).append("\n");
                } else {
                    armResult.append("# 未知十六进制: ").append(line).append("\n");
                }
                
                // ARM Big Endian结果
                String swappedLine = swapEndian(line, 4);
                String armBeInstruction = findArmInstruction(swappedLine);
                if (armBeInstruction != null) {
                    armBeResult.append(armBeInstruction).append("\n");
                } else {
                    armBeResult.append("# 未知十六进制: ").append(line).append("\n");
                }
                
                // THUMB结果
                if (thumbInstruction != null) {
                    thumbResult.append(thumbInstruction).append("\n");
                } else {
                    thumbResult.append("# 未知十六进制: ").append(line).append("\n");
                }
                
                // THUMB Big Endian结果
                String thumbSwappedLine = swapEndian(line, 2);
                String thumbBeInstruction = findThumbInstruction(thumbSwappedLine);
                if (thumbBeInstruction != null) {
                    thumbBeResult.append(thumbBeInstruction).append("\n");
                } else {
                    thumbBeResult.append("# 未知十六进制: ").append(line).append("\n");
                }
            }
            
            hexArm64OutputArea.setText(arm64Result.toString());
            hexArmOutputArea.setText(armResult.toString());
            hexArmBeOutputArea.setText(armBeResult.toString());
            hexThumbOutputArea.setText(thumbResult.toString());
            hexThumbBeOutputArea.setText(thumbBeResult.toString());
            
        } catch (Exception e) {
            String errorMsg = "转换错误: " + e.getMessage();
            hexArm64OutputArea.setText(errorMsg);
            hexArmOutputArea.setText(errorMsg);
            hexArmBeOutputArea.setText(errorMsg);
            hexThumbOutputArea.setText(errorMsg);
            hexThumbBeOutputArea.setText(errorMsg);
        }
    }
    
    private String convertToArm64Hex(String instruction) {
        // 简化的ARM64指令转换
        switch (instruction) {
            case "NOP":
                return "1F2003D5";
            case "RET":
                return "C0035FD6";
            default:
                // 尝试从ARM指令映射
                String armHex = ARM_INSTRUCTIONS.get(instruction);
                if (armHex != null) {
                    // 简单的ARM到ARM64转换（实际情况更复杂）
                    return armHex;
                }
                return null;
        }
    }
    
    private String convertToArm64Instruction(String armInstruction) {
        // 简化的ARM到ARM64指令转换
        switch (armInstruction) {
            case "MOV R0, R0":
                return "MOV X0, X0";
            case "MOV R0, #0":
                return "MOV X0, #0";
            case "BX LR":
                return "RET";
            default:
                return armInstruction.replace("R", "X"); // 简单替换
        }
    }
    
    private String findArmInstruction(String hex) {
        for (Map.Entry<String, String> entry : ARM_INSTRUCTIONS.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(hex)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private String findThumbInstruction(String hex) {
        for (Map.Entry<String, String> entry : THUMB_INSTRUCTIONS.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(hex)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private String formatHex(String hex) {
        if (prefixCheckBox.isSelected()) {
            hex = "0x" + hex;
        }
        
        if (swapEndianCheckBox.isSelected()) {
            hex = swapEndian(hex.replace("0x", ""), 4);
            if (prefixCheckBox.isSelected()) {
                hex = "0x" + hex;
            }
        }
        
        return hex;
    }
    
    private String swapEndian(String hex, int byteSize) {
        if (hex.length() < byteSize * 2) {
            // 补齐到指定字节数
            while (hex.length() < byteSize * 2) {
                hex = "0" + hex;
            }
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = hex.length(); i > 0; i -= 2) {
            result.append(hex.substring(Math.max(0, i - 2), i));
        }
        return result.toString();
    }
}