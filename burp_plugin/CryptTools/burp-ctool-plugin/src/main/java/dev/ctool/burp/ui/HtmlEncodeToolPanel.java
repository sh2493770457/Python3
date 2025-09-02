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

/**
 * HTML编码工具面板
 * 支持HTML实体编码和解码
 */
public class HtmlEncodeToolPanel extends JPanel {
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JButton encodeButton;
    private JButton decodeButton;
    private JButton clearButton;
    private JButton swapButton;
    private JCheckBox namedEntitiesCheckBox;
    private JCheckBox numericEntitiesCheckBox;
    private JCheckBox hexEntitiesCheckBox;
    
    // HTML实体映射表
    private static final Map<String, String> HTML_ENTITIES = new HashMap<>();
    private static final Map<String, String> HTML_ENTITIES_REVERSE = new HashMap<>();
    
    static {
        initializeHtmlEntities();
    }
    
    public HtmlEncodeToolPanel() {
        initializeUI();
        setupEventListeners();
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
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(outputPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输入文本"));
        
        inputArea = new JTextArea(10, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("编码选项"));
        
        namedEntitiesCheckBox = new JCheckBox("命名实体 (&lt; &gt; &amp;)", true);
        numericEntitiesCheckBox = new JCheckBox("数字实体 (&#60; &#62;)", false);
        hexEntitiesCheckBox = new JCheckBox("十六进制实体 (&#x3C; &#x3E;)", false);
        
        panel.add(namedEntitiesCheckBox);
        panel.add(numericEntitiesCheckBox);
        panel.add(hexEntitiesCheckBox);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输出结果"));
        
        outputArea = new JTextArea(10, 50);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        encodeButton = new JButton("编码");
        decodeButton = new JButton("解码");
        swapButton = new JButton("交换");
        clearButton = new JButton("清空");
        
        encodeButton.setPreferredSize(new Dimension(100, 30));
        decodeButton.setPreferredSize(new Dimension(100, 30));
        swapButton.setPreferredSize(new Dimension(100, 30));
        clearButton.setPreferredSize(new Dimension(100, 30));
        
        panel.add(encodeButton);
        panel.add(decodeButton);
        panel.add(swapButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encodeHtml();
            }
        });
        
        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decodeHtml();
            }
        });
        
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapInputOutput();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }
    
    private void encodeHtml() {
        String input = inputArea.getText();
        if (input.isEmpty()) {
            outputArea.setText("");
            return;
        }
        
        try {
            String result = input;
            
            if (namedEntitiesCheckBox.isSelected()) {
                result = encodeWithNamedEntities(result);
            } else if (numericEntitiesCheckBox.isSelected()) {
                result = encodeWithNumericEntities(result);
            } else if (hexEntitiesCheckBox.isSelected()) {
                result = encodeWithHexEntities(result);
            } else {
                // 默认使用命名实体
                result = encodeWithNamedEntities(result);
            }
            
            outputArea.setText(result);
        } catch (Exception e) {
            outputArea.setText("编码出错: " + e.getMessage());
        }
    }
    
    private void decodeHtml() {
        String input = inputArea.getText();
        if (input.isEmpty()) {
            outputArea.setText("");
            return;
        }
        
        try {
            String result = decodeHtmlEntities(input);
            outputArea.setText(result);
        } catch (Exception e) {
            outputArea.setText("解码出错: " + e.getMessage());
        }
    }
    
    private String encodeWithNamedEntities(String input) {
        StringBuilder result = new StringBuilder();
        
        for (char c : input.toCharArray()) {
            String entity = getNamedEntity(c);
            if (entity != null) {
                result.append(entity);
            } else if (c > 127) {
                // 对于非ASCII字符，使用数字实体
                result.append("&#").append((int) c).append(";");
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    private String encodeWithNumericEntities(String input) {
        StringBuilder result = new StringBuilder();
        
        for (char c : input.toCharArray()) {
            if (needsEncoding(c)) {
                result.append("&#").append((int) c).append(";");
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    private String encodeWithHexEntities(String input) {
        StringBuilder result = new StringBuilder();
        
        for (char c : input.toCharArray()) {
            if (needsEncoding(c)) {
                result.append("&#x").append(Integer.toHexString(c).toUpperCase()).append(";");
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    private String decodeHtmlEntities(String input) {
        String result = input;
        
        // 解码命名实体
        for (Map.Entry<String, String> entry : HTML_ENTITIES_REVERSE.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        
        // 解码数字实体 &#123;
        Pattern numericPattern = Pattern.compile("&#(\\d+);");
        Matcher numericMatcher = numericPattern.matcher(result);
        StringBuffer sb = new StringBuffer();
        while (numericMatcher.find()) {
            int code = Integer.parseInt(numericMatcher.group(1));
            numericMatcher.appendReplacement(sb, String.valueOf((char) code));
        }
        numericMatcher.appendTail(sb);
        result = sb.toString();
        
        // 解码十六进制实体 &#x7B;
        Pattern hexPattern = Pattern.compile("&#x([0-9A-Fa-f]+);");
        Matcher hexMatcher = hexPattern.matcher(result);
        sb = new StringBuffer();
        while (hexMatcher.find()) {
            int code = Integer.parseInt(hexMatcher.group(1), 16);
            hexMatcher.appendReplacement(sb, String.valueOf((char) code));
        }
        hexMatcher.appendTail(sb);
        result = sb.toString();
        
        return result;
    }
    
    private String getNamedEntity(char c) {
        return HTML_ENTITIES.get(String.valueOf(c));
    }
    
    private boolean needsEncoding(char c) {
        return c == '<' || c == '>' || c == '&' || c == '"' || c == '\'' || c > 127;
    }
    
    private void swapInputOutput() {
        String input = inputArea.getText();
        String output = outputArea.getText();
        
        inputArea.setText(output);
        outputArea.setText(input);
    }
    
    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
    }
    
    private static void initializeHtmlEntities() {
        // 基本HTML实体
        HTML_ENTITIES.put("<", "&lt;");
        HTML_ENTITIES.put(">", "&gt;");
        HTML_ENTITIES.put("&", "&amp;");
        HTML_ENTITIES.put("\"", "&quot;");
        HTML_ENTITIES.put("'", "&#39;");
        HTML_ENTITIES.put(" ", "&nbsp;");
        
        // 常用符号
        HTML_ENTITIES.put("©", "&copy;");
        HTML_ENTITIES.put("®", "&reg;");
        HTML_ENTITIES.put("™", "&trade;");
        HTML_ENTITIES.put("€", "&euro;");
        HTML_ENTITIES.put("£", "&pound;");
        HTML_ENTITIES.put("¥", "&yen;");
        HTML_ENTITIES.put("¢", "&cent;");
        HTML_ENTITIES.put("§", "&sect;");
        HTML_ENTITIES.put("¶", "&para;");
        HTML_ENTITIES.put("•", "&bull;");
        HTML_ENTITIES.put("…", "&hellip;");
        HTML_ENTITIES.put("–", "&ndash;");
        HTML_ENTITIES.put("—", "&mdash;");
        HTML_ENTITIES.put("'", "&lsquo;");
        HTML_ENTITIES.put("'", "&rsquo;");
        HTML_ENTITIES.put("\"", "&ldquo;");
        HTML_ENTITIES.put("\"", "&rdquo;");
        
        // 数学符号
        HTML_ENTITIES.put("±", "&plusmn;");
        HTML_ENTITIES.put("×", "&times;");
        HTML_ENTITIES.put("÷", "&divide;");
        HTML_ENTITIES.put("≠", "&ne;");
        HTML_ENTITIES.put("≤", "&le;");
        HTML_ENTITIES.put("≥", "&ge;");
        HTML_ENTITIES.put("∞", "&infin;");
        HTML_ENTITIES.put("∑", "&sum;");
        HTML_ENTITIES.put("∏", "&prod;");
        HTML_ENTITIES.put("√", "&radic;");
        HTML_ENTITIES.put("∫", "&int;");
        HTML_ENTITIES.put("∂", "&part;");
        HTML_ENTITIES.put("∆", "&Delta;");
        HTML_ENTITIES.put("Ω", "&Omega;");
        HTML_ENTITIES.put("α", "&alpha;");
        HTML_ENTITIES.put("β", "&beta;");
        HTML_ENTITIES.put("γ", "&gamma;");
        HTML_ENTITIES.put("δ", "&delta;");
        HTML_ENTITIES.put("π", "&pi;");
        HTML_ENTITIES.put("σ", "&sigma;");
        HTML_ENTITIES.put("τ", "&tau;");
        HTML_ENTITIES.put("φ", "&phi;");
        HTML_ENTITIES.put("ψ", "&psi;");
        HTML_ENTITIES.put("ω", "&omega;");
        
        // 箭头符号
        HTML_ENTITIES.put("←", "&larr;");
        HTML_ENTITIES.put("→", "&rarr;");
        HTML_ENTITIES.put("↑", "&uarr;");
        HTML_ENTITIES.put("↓", "&darr;");
        HTML_ENTITIES.put("↔", "&harr;");
        HTML_ENTITIES.put("⇐", "&lArr;");
        HTML_ENTITIES.put("⇒", "&rArr;");
        HTML_ENTITIES.put("⇑", "&uArr;");
        HTML_ENTITIES.put("⇓", "&dArr;");
        HTML_ENTITIES.put("⇔", "&hArr;");
        
        // 创建反向映射
        for (Map.Entry<String, String> entry : HTML_ENTITIES.entrySet()) {
            HTML_ENTITIES_REVERSE.put(entry.getValue(), entry.getKey());
        }
    }
}