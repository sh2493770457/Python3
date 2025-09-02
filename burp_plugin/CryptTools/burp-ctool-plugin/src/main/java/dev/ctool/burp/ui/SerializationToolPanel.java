package dev.ctool.burp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SerializationToolPanel extends JPanel {
    private JComboBox<String> fromFormatCombo;
    private JComboBox<String> toFormatCombo;
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JButton convertButton;
    private JButton clearButton;
    private JButton swapButton;
    private JButton formatButton;
    
    private final String[] formats = {"JSON", "XML", "YAML", "Properties"};
    
    public SerializationToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        // 格式选择组件
        fromFormatCombo = new JComboBox<>(formats);
        fromFormatCombo.setSelectedItem("JSON");
        
        toFormatCombo = new JComboBox<>(formats);
        toFormatCombo.setSelectedItem("XML");
        
        // 文本区域
        inputArea = new JTextArea(12, 40);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        
        outputArea = new JTextArea(12, 40);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        
        // 按钮组件
        convertButton = new JButton("转换");
        clearButton = new JButton("清空");
        swapButton = new JButton("交换格式");
        formatButton = new JButton("格式化");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 控制面板
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("格式转换"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("源格式:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        controlPanel.add(fromFormatCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        controlPanel.add(new JLabel("目标格式:"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 0;
        controlPanel.add(toFormatCombo, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(convertButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(formatButton);
        buttonPanel.add(clearButton);
        
        // 输入输出面板
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入"));
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("输出"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        contentPanel.add(inputPanel);
        contentPanel.add(outputPanel);
        
        // 主布局
        add(controlPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapFormats();
            }
        });
        
        formatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatInput();
            }
        });
    }
    
    private void performConversion() {
        try {
            String input = inputArea.getText().trim();
            if (input.isEmpty()) {
                outputArea.setText("请输入要转换的内容");
                return;
            }
            
            String fromFormat = (String) fromFormatCombo.getSelectedItem();
            String toFormat = (String) toFormatCombo.getSelectedItem();
            
            if (fromFormat.equals(toFormat)) {
                outputArea.setText("源格式和目标格式相同，无需转换");
                return;
            }
            
            String result = convertFormat(input, fromFormat, toFormat);
            outputArea.setText(result);
            
        } catch (Exception e) {
            outputArea.setText("转换失败: " + e.getMessage());
        }
    }
    
    private String convertFormat(String input, String fromFormat, String toFormat) throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        YAMLMapper yamlMapper = new YAMLMapper();
        
        // 首先将输入转换为通用的JsonNode对象
        JsonNode jsonNode;
        
        switch (fromFormat) {
            case "JSON":
                jsonNode = jsonMapper.readTree(input);
                break;
            case "YAML":
                jsonNode = yamlMapper.readTree(input);
                break;
            case "XML":
                // XML转JSON需要特殊处理
                jsonNode = xmlToJson(input);
                break;
            case "Properties":
                jsonNode = propertiesToJson(input);
                break;
            default:
                throw new IllegalArgumentException("不支持的源格式: " + fromFormat);
        }
        
        // 然后将JsonNode转换为目标格式
        switch (toFormat) {
            case "JSON":
                return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            case "YAML":
                return yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            case "XML":
                return jsonToXml(jsonNode);
            case "Properties":
                return jsonToProperties(jsonNode);
            default:
                throw new IllegalArgumentException("不支持的目标格式: " + toFormat);
        }
    }
    
    private JsonNode xmlToJson(String xml) throws Exception {
        // 简化的XML到JSON转换
        // 实际项目中可能需要更复杂的转换逻辑
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode().put("xml_content", xml);
    }
    
    private String jsonToXml(JsonNode jsonNode) throws Exception {
        // 简化的JSON到XML转换
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<root>\n");
        xml.append(jsonNode.toPrettyString());
        xml.append("\n</root>");
        return xml.toString();
    }
    
    private JsonNode propertiesToJson(String properties) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode objectNode = mapper.createObjectNode();
        
        String[] lines = properties.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    objectNode.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        
        return objectNode;
    }
    
    private String jsonToProperties(JsonNode jsonNode) throws Exception {
        StringBuilder properties = new StringBuilder();
        
        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    properties.append(key).append("=").append(value.asText()).append("\n");
                } else {
                    properties.append(key).append("=").append(value.toString()).append("\n");
                }
            });
        }
        
        return properties.toString();
    }
    
    private void formatInput() {
        try {
            String input = inputArea.getText().trim();
            if (input.isEmpty()) {
                return;
            }
            
            String format = (String) fromFormatCombo.getSelectedItem();
            String formatted = formatString(input, format);
            inputArea.setText(formatted);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "格式化失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String formatString(String input, String format) throws Exception {
        switch (format) {
            case "JSON":
                ObjectMapper jsonMapper = new ObjectMapper();
                JsonNode jsonNode = jsonMapper.readTree(input);
                return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            case "YAML":
                YAMLMapper yamlMapper = new YAMLMapper();
                JsonNode yamlNode = yamlMapper.readTree(input);
                return yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(yamlNode);
            case "XML":
                return formatXml(input);
            case "Properties":
                return input; // Properties格式通常不需要特殊格式化
            default:
                return input;
        }
    }
    
    private String formatXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        
        return writer.toString();
    }
    
    private void swapFormats() {
        String fromFormat = (String) fromFormatCombo.getSelectedItem();
        String toFormat = (String) toFormatCombo.getSelectedItem();
        
        fromFormatCombo.setSelectedItem(toFormat);
        toFormatCombo.setSelectedItem(fromFormat);
        
        // 如果有输入，自动转换
        if (!inputArea.getText().trim().isEmpty()) {
            performConversion();
        }
    }
    
    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
        fromFormatCombo.setSelectedItem("JSON");
        toFormatCombo.setSelectedItem("XML");
    }
}