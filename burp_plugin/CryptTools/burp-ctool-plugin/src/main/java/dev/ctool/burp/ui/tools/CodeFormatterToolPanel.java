package dev.ctool.burp.ui.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import java.io.StringReader;
import java.io.StringWriter;
import org.xml.sax.InputSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

public class CodeFormatterToolPanel extends JPanel {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JComboBox<String> languageComboBox;
    private JComboBox<String> indentComboBox;
    private JButton beautifyButton;
    private JButton compressButton;
    private JButton clearButton;
    private JButton copyButton;
    
    // 支持的语言类型
    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList(
        "JSON", "XML", "HTML", "CSS", "JavaScript", "SQL"
    );
    
    // 缩进选项
    private static final List<String> INDENT_OPTIONS = Arrays.asList(
        "无缩进", "2空格", "4空格", "6空格", "8空格"
    );
    
    private static final List<Integer> INDENT_VALUES = Arrays.asList(0, 2, 4, 6, 8);
    
    public CodeFormatterToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        // 输入区域
        inputTextArea = new JTextArea(15, 50);
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(false);
        inputTextArea.setTabSize(4);
        
        // 输出区域
        outputTextArea = new JTextArea(15, 50);
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputTextArea.setEditable(false);
        outputTextArea.setBackground(new Color(248, 248, 248));
        outputTextArea.setLineWrap(false);
        outputTextArea.setTabSize(4);
        
        // 语言选择
        languageComboBox = new JComboBox<>(SUPPORTED_LANGUAGES.toArray(new String[0]));
        languageComboBox.setSelectedIndex(0); // 默认选择JSON
        
        // 缩进选择
        indentComboBox = new JComboBox<>(INDENT_OPTIONS.toArray(new String[0]));
        indentComboBox.setSelectedIndex(2); // 默认选择4空格
        
        // 按钮
        beautifyButton = new JButton("格式化");
        compressButton = new JButton("压缩");
        clearButton = new JButton("清空");
        copyButton = new JButton("复制结果");
        
        // 设置按钮样式
        beautifyButton.setBackground(new Color(0, 123, 255));
        beautifyButton.setForeground(Color.WHITE);
        beautifyButton.setFocusPainted(false);
        
        compressButton.setBackground(new Color(40, 167, 69));
        compressButton.setForeground(Color.WHITE);
        compressButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("语言:"));
        controlPanel.add(languageComboBox);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(new JLabel("缩进:"));
        controlPanel.add(indentComboBox);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(beautifyButton);
        controlPanel.add(compressButton);
        controlPanel.add(clearButton);
        controlPanel.add(copyButton);
        
        // 主要内容区域
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入代码"));
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("格式化结果"));
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(inputPanel);
        mainPanel.add(outputPanel);
        
        add(controlPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        beautifyButton.addActionListener(e -> performFormat("beautify"));
        compressButton.addActionListener(e -> performFormat("compress"));
        clearButton.addActionListener(e -> clearAll());
        copyButton.addActionListener(e -> copyToClipboard());
        
        // 语言变化时更新按钮状态
        languageComboBox.addActionListener(e -> updateButtonStates());
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        
        // 根据语言类型启用/禁用压缩按钮
        switch (selectedLanguage) {
            case "JSON":
            case "XML":
            case "HTML":
            case "CSS":
                compressButton.setEnabled(true);
                break;
            case "JavaScript":
            case "SQL":
                compressButton.setEnabled(false);
                break;
            default:
                compressButton.setEnabled(true);
        }
    }
    
    private void performFormat(String type) {
        String input = inputTextArea.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要格式化的代码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        int indentSize = INDENT_VALUES.get(indentComboBox.getSelectedIndex());
        
        try {
            String result = formatCode(input, selectedLanguage, type, indentSize);
            outputTextArea.setText(result);
        } catch (Exception e) {
            outputTextArea.setText("格式化失败: " + e.getMessage());
        }
    }
    
    private String formatCode(String input, String language, String type, int indentSize) throws Exception {
        switch (language) {
            case "JSON":
                return formatJson(input, type, indentSize);
            case "XML":
                return formatXml(input, type, indentSize);
            case "HTML":
                return formatHtml(input, type, indentSize);
            case "CSS":
                return formatCss(input, type, indentSize);
            case "JavaScript":
                return formatJavaScript(input, type, indentSize);
            case "SQL":
                return formatSql(input, type, indentSize);
            default:
                throw new Exception("不支持的语言类型: " + language);
        }
    }
    
    private String formatJson(String input, String type, int indentSize) throws Exception {
        try {
            JsonElement jsonElement = JsonParser.parseString(input);
            
            if ("compress".equals(type)) {
                Gson gson = new Gson();
                return gson.toJson(jsonElement);
            } else {
                GsonBuilder gsonBuilder = new GsonBuilder();
                if (indentSize > 0) {
                    gsonBuilder.setPrettyPrinting();
                    // 注意：Gson的setPrettyPrinting()使用固定的2空格缩进
                }
                Gson gson = gsonBuilder.create();
                String result = gson.toJson(jsonElement);
                
                // 如果需要自定义缩进，手动调整
                if (indentSize != 2 && indentSize > 0) {
                    result = adjustIndentation(result, 2, indentSize);
                }
                
                return result;
            }
        } catch (Exception e) {
            throw new Exception("JSON格式错误: " + e.getMessage());
        }
    }
    
    private String formatXml(String input, String type, int indentSize) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(input)));
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            if ("compress".equals(type)) {
                transformer.setOutputProperty(OutputKeys.INDENT, "no");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            } else {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                if (indentSize > 0) {
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentSize));
                }
            }
            
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            
            return writer.toString();
        } catch (Exception e) {
            throw new Exception("XML格式错误: " + e.getMessage());
        }
    }
    
    private String formatHtml(String input, String type, int indentSize) throws Exception {
        if ("compress".equals(type)) {
            // 简单的HTML压缩：移除多余空白
            return input.replaceAll(">\\s+<", "><")
                       .replaceAll("\\s+", " ")
                       .trim();
        } else {
            // 简单的HTML格式化
            return formatHtmlBasic(input, indentSize);
        }
    }
    
    private String formatHtmlBasic(String input, int indentSize) {
        StringBuilder result = new StringBuilder();
        String indent = " ".repeat(indentSize);
        int level = 0;
        
        // 简单的HTML标签匹配和缩进
        Pattern tagPattern = Pattern.compile("<(/?)([^>]+)>");
        Matcher matcher = tagPattern.matcher(input);
        
        int lastEnd = 0;
        while (matcher.find()) {
            String beforeTag = input.substring(lastEnd, matcher.start()).trim();
            if (!beforeTag.isEmpty()) {
                result.append(indent.repeat(level)).append(beforeTag).append("\n");
            }
            
            String isClosing = matcher.group(1);
            String tagName = matcher.group(2);
            
            if ("/".equals(isClosing)) {
                level = Math.max(0, level - 1);
            }
            
            result.append(indent.repeat(level)).append(matcher.group()).append("\n");
            
            if (!"/".equals(isClosing) && !tagName.matches(".*[/\\s]$") && !isSelfClosingTag(tagName)) {
                level++;
            }
            
            lastEnd = matcher.end();
        }
        
        String remaining = input.substring(lastEnd).trim();
        if (!remaining.isEmpty()) {
            result.append(indent.repeat(level)).append(remaining);
        }
        
        return result.toString();
    }
    
    private boolean isSelfClosingTag(String tagName) {
        String[] selfClosingTags = {"br", "hr", "img", "input", "meta", "link", "area", "base", "col", "embed", "source", "track", "wbr"};
        String tag = tagName.toLowerCase().split("\\s")[0];
        return Arrays.asList(selfClosingTags).contains(tag);
    }
    
    private String formatCss(String input, String type, int indentSize) {
        if ("compress".equals(type)) {
            return input.replaceAll("\\s+", " ")
                       .replaceAll(";\\s*}", "}")
                       .replaceAll("\\s*{\\s*", "{")
                       .replaceAll("\\s*;\\s*", ";")
                       .replaceAll("}\\s*", "}")
                       .trim();
        } else {
            return formatCssBasic(input, indentSize);
        }
    }
    
    private String formatCssBasic(String input, int indentSize) {
        StringBuilder result = new StringBuilder();
        String indent = " ".repeat(indentSize);
        
        String[] lines = input.split("\n");
        boolean inRule = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            if (line.contains("{")) {
                result.append(line).append("\n");
                inRule = true;
            } else if (line.contains("}")) {
                result.append(line).append("\n\n");
                inRule = false;
            } else if (inRule) {
                result.append(indent).append(line).append("\n");
            } else {
                result.append(line).append("\n");
            }
        }
        
        return result.toString();
    }
    
    private String formatJavaScript(String input, String type, int indentSize) {
        // 简单的JavaScript格式化
        if ("beautify".equals(type)) {
            return formatJavaScriptBasic(input, indentSize);
        }
        return input; // JavaScript压缩需要复杂的解析器，这里暂不实现
    }
    
    private String formatJavaScriptBasic(String input, int indentSize) {
        StringBuilder result = new StringBuilder();
        String indent = " ".repeat(indentSize);
        int level = 0;
        
        String[] lines = input.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            if (line.contains("}")) {
                level = Math.max(0, level - 1);
            }
            
            result.append(indent.repeat(level)).append(line).append("\n");
            
            if (line.contains("{")) {
                level++;
            }
        }
        
        return result.toString();
    }
    
    private String formatSql(String input, String type, int indentSize) {
        if ("compress".equals(type)) {
            return input.replaceAll("\\s+", " ").trim();
        } else {
            return formatSqlBasic(input, indentSize);
        }
    }
    
    private String formatSqlBasic(String input, int indentSize) {
        StringBuilder result = new StringBuilder();
        String indent = " ".repeat(indentSize);
        
        String[] keywords = {"SELECT", "FROM", "WHERE", "JOIN", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", 
                           "GROUP BY", "ORDER BY", "HAVING", "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", "DROP"};
        
        String[] lines = input.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            boolean isKeyword = false;
            for (String keyword : keywords) {
                if (line.toUpperCase().startsWith(keyword)) {
                    isKeyword = true;
                    break;
                }
            }
            
            if (isKeyword) {
                result.append(line.toUpperCase()).append("\n");
            } else {
                result.append(indent).append(line).append("\n");
            }
        }
        
        return result.toString();
    }
    
    private String adjustIndentation(String input, int fromIndent, int toIndent) {
        if (fromIndent == toIndent) return input;
        
        String fromIndentStr = " ".repeat(fromIndent);
        String toIndentStr = " ".repeat(toIndent);
        
        return input.replaceAll("(?m)^" + fromIndentStr, toIndentStr);
    }
    
    private void clearAll() {
        inputTextArea.setText("");
        outputTextArea.setText("");
    }
    
    private void copyToClipboard() {
        String text = outputTextArea.getText();
        if (text != null && !text.trim().isEmpty()) {
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(text);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "没有内容可复制", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
}