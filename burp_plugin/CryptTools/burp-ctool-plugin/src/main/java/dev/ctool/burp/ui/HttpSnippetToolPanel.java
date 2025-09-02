package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP请求代码生成工具面板
 * 支持将HTTP请求转换为多种编程语言的代码
 */
public class HttpSnippetToolPanel extends JPanel {
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> languageComboBox;
    private JComboBox<String> libraryComboBox;
    private JButton generateButton;
    private JButton clearButton;
    private JButton copyButton;
    private JRadioButton curlRadio;
    private JRadioButton httpRadio;
    
    // 支持的语言和库映射
    private static final Map<String, String[]> LANGUAGE_LIBRARIES = new HashMap<>();
    
    static {
        LANGUAGE_LIBRARIES.put("JavaScript", new String[]{"fetch", "axios", "jQuery", "XMLHttpRequest"});
        LANGUAGE_LIBRARIES.put("Python", new String[]{"requests", "urllib", "http.client"});
        LANGUAGE_LIBRARIES.put("Java", new String[]{"OkHttp", "HttpURLConnection", "Apache HttpClient"});
        LANGUAGE_LIBRARIES.put("PHP", new String[]{"cURL", "Guzzle", "file_get_contents"});
        LANGUAGE_LIBRARIES.put("C#", new String[]{"HttpClient", "RestSharp", "WebClient"});
        LANGUAGE_LIBRARIES.put("Go", new String[]{"net/http", "resty"});
        LANGUAGE_LIBRARIES.put("Ruby", new String[]{"Net::HTTP", "HTTParty", "Faraday"});
        LANGUAGE_LIBRARIES.put("Shell", new String[]{"cURL", "wget"});
    }
    
    public HttpSnippetToolPanel() {
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
        
        // 添加使用说明
        add(createInstructionPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输入HTTP请求"));
        
        inputArea = new JTextArea(12, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setText("curl -X GET 'https://api.example.com/users' \\\n" +
                         "  -H 'Authorization: Bearer token123' \\\n" +
                         "  -H 'Content-Type: application/json'");
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 输入格式选择
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup formatGroup = new ButtonGroup();
        
        curlRadio = new JRadioButton("cURL命令", true);
        httpRadio = new JRadioButton("HTTP原始请求");
        
        formatGroup.add(curlRadio);
        formatGroup.add(httpRadio);
        
        formatPanel.add(new JLabel("输入格式:"));
        formatPanel.add(curlRadio);
        formatPanel.add(httpRadio);
        
        panel.add(formatPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("代码生成选项"));
        
        // 语言选择
        panel.add(new JLabel("语言:"));
        languageComboBox = new JComboBox<>(LANGUAGE_LIBRARIES.keySet().toArray(new String[0]));
        languageComboBox.setSelectedItem("JavaScript");
        panel.add(languageComboBox);
        
        // 库选择
        panel.add(new JLabel("库/框架:"));
        libraryComboBox = new JComboBox<>();
        updateLibraryOptions();
        panel.add(libraryComboBox);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("生成的代码"));
        
        outputArea = new JTextArea(15, 50);
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
        
        generateButton = new JButton("生成代码");
        clearButton = new JButton("清空");
        copyButton = new JButton("复制代码");
        
        panel.add(generateButton);
        panel.add(clearButton);
        panel.add(copyButton);
        
        return panel;
    }
    
    private JPanel createInstructionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("使用说明"));
        
        JTextArea instructionArea = new JTextArea(3, 50);
        instructionArea.setEditable(false);
        instructionArea.setBackground(getBackground());
        instructionArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        instructionArea.setText(
            "1. 在输入框中粘贴cURL命令或HTTP原始请求\n" +
            "2. 选择目标编程语言和库/框架\n" +
            "3. 点击'生成代码'按钮获取对应的代码实现"
        );
        
        panel.add(instructionArea, BorderLayout.CENTER);
        return panel;
    }
    
    private void setupEventListeners() {
        // 语言选择变化时更新库选项
        languageComboBox.addActionListener(e -> updateLibraryOptions());
        
        // 生成代码按钮
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCode();
            }
        });
        
        // 清空按钮
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        
        // 复制按钮
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });
    }
    
    private void updateLibraryOptions() {
        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        if (selectedLanguage != null) {
            String[] libraries = LANGUAGE_LIBRARIES.get(selectedLanguage);
            libraryComboBox.removeAllItems();
            for (String library : libraries) {
                libraryComboBox.addItem(library);
            }
        }
    }
    
    private void generateCode() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            outputArea.setText("请输入HTTP请求内容");
            return;
        }
        
        String language = (String) languageComboBox.getSelectedItem();
        String library = (String) libraryComboBox.getSelectedItem();
        boolean isCurl = curlRadio.isSelected();
        
        try {
            String code = generateCodeForLanguage(input, language, library, isCurl);
            outputArea.setText(code);
        } catch (Exception e) {
            outputArea.setText("代码生成失败: " + e.getMessage());
        }
    }
    
    private void clearFields() {
        inputArea.setText("");
        outputArea.setText("");
    }
    
    private void copyToClipboard() {
        String text = outputArea.getText();
        if (!text.isEmpty()) {
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "代码已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private String generateCodeForLanguage(String input, String language, String library, boolean isCurl) {
        // 解析HTTP请求
        HttpRequest request = isCurl ? parseCurlCommand(input) : parseHttpRequest(input);
        
        // 根据语言和库生成代码
        switch (language) {
            case "JavaScript":
                return generateJavaScriptCode(request, library);
            case "Python":
                return generatePythonCode(request, library);
            case "Java":
                return generateJavaCode(request, library);
            case "PHP":
                return generatePHPCode(request, library);
            case "C#":
                return generateCSharpCode(request, library);
            case "Go":
                return generateGoCode(request, library);
            case "Ruby":
                return generateRubyCode(request, library);
            case "Shell":
                return generateShellCode(request, library);
            default:
                return "暂不支持该语言: " + language;
        }
    }
    
    private HttpRequest parseCurlCommand(String curl) {
        HttpRequest request = new HttpRequest();
        
        // 提取URL
        Pattern urlPattern = Pattern.compile("curl\\s+(?:-X\\s+\\w+\\s+)?['\"]?([^'\"\\s]+)['\"]?");
        Matcher urlMatcher = urlPattern.matcher(curl);
        if (urlMatcher.find()) {
            request.url = urlMatcher.group(1);
        }
        
        // 提取方法
        Pattern methodPattern = Pattern.compile("-X\\s+(\\w+)");
        Matcher methodMatcher = methodPattern.matcher(curl);
        if (methodMatcher.find()) {
            request.method = methodMatcher.group(1);
        } else {
            request.method = "GET";
        }
        
        // 提取头部
        Pattern headerPattern = Pattern.compile("-H\\s+['\"]([^'\"]+)['\"]?");
        Matcher headerMatcher = headerPattern.matcher(curl);
        while (headerMatcher.find()) {
            String header = headerMatcher.group(1);
            String[] parts = header.split(":\\s*", 2);
            if (parts.length == 2) {
                request.headers.put(parts[0], parts[1]);
            }
        }
        
        // 提取数据
        Pattern dataPattern = Pattern.compile("-d\\s+['\"]([^'\"]+)['\"]?");
        Matcher dataMatcher = dataPattern.matcher(curl);
        if (dataMatcher.find()) {
            request.body = dataMatcher.group(1);
        }
        
        return request;
    }
    
    private HttpRequest parseHttpRequest(String http) {
        HttpRequest request = new HttpRequest();
        String[] lines = http.split("\\n");
        
        if (lines.length > 0) {
            // 解析请求行
            String[] requestLine = lines[0].split("\\s+");
            if (requestLine.length >= 2) {
                request.method = requestLine[0];
                request.url = requestLine[1];
            }
            
            // 解析头部
            int bodyStart = -1;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    bodyStart = i + 1;
                    break;
                }
                String[] parts = line.split(":\\s*", 2);
                if (parts.length == 2) {
                    request.headers.put(parts[0], parts[1]);
                }
            }
            
            // 解析请求体
            if (bodyStart > 0 && bodyStart < lines.length) {
                StringBuilder body = new StringBuilder();
                for (int i = bodyStart; i < lines.length; i++) {
                    body.append(lines[i]).append("\\n");
                }
                request.body = body.toString().trim();
            }
        }
        
        return request;
    }
    
    private String generateJavaScriptCode(HttpRequest request, String library) {
        switch (library) {
            case "fetch":
                return generateFetchCode(request);
            case "axios":
                return generateAxiosCode(request);
            case "jQuery":
                return generateJQueryCode(request);
            case "XMLHttpRequest":
                return generateXHRCode(request);
            default:
                return generateFetchCode(request);
        }
    }
    
    private String generateFetchCode(HttpRequest request) {
        StringBuilder code = new StringBuilder();
        code.append("fetch('").append(request.url).append("', {\n");
        code.append("  method: '").append(request.method).append("',\n");
        
        if (!request.headers.isEmpty()) {
            code.append("  headers: {\n");
            for (Map.Entry<String, String> header : request.headers.entrySet()) {
                code.append("    '").append(header.getKey()).append("': '").append(header.getValue()).append("',\n");
            }
            code.append("  },\n");
        }
        
        if (request.body != null && !request.body.isEmpty()) {
            code.append("  body: '").append(request.body.replace("'", "\\'")).append("'\n");
        }
        
        code.append("})\n");
        code.append(".then(response => response.json())\n");
        code.append(".then(data => console.log(data))\n");
        code.append(".catch(error => console.error('Error:', error));");
        
        return code.toString();
    }
    
    private String generateAxiosCode(HttpRequest request) {
        StringBuilder code = new StringBuilder();
        code.append("axios({");
        code.append("\n  method: '").append(request.method.toLowerCase()).append("',");
        code.append("\n  url: '").append(request.url).append("',");
        
        if (!request.headers.isEmpty()) {
            code.append("\n  headers: {");
            for (Map.Entry<String, String> header : request.headers.entrySet()) {
                code.append("\n    '").append(header.getKey()).append("': '").append(header.getValue()).append("',");
            }
            code.append("\n  },");
        }
        
        if (request.body != null && !request.body.isEmpty()) {
            code.append("\n  data: '").append(request.body.replace("'", "\\'")).append("'");
        }
        
        code.append("\n})\n");
        code.append(".then(response => console.log(response.data))\n");
        code.append(".catch(error => console.error('Error:', error));");
        
        return code.toString();
    }
    
    private String generateJQueryCode(HttpRequest request) {
        StringBuilder code = new StringBuilder();
        code.append("$.ajax({\n");
        code.append("  url: '").append(request.url).append("',\n");
        code.append("  method: '").append(request.method).append("',\n");
        
        if (!request.headers.isEmpty()) {
            code.append("  headers: {\n");
            for (Map.Entry<String, String> header : request.headers.entrySet()) {
                code.append("    '").append(header.getKey()).append("': '").append(header.getValue()).append("',\n");
            }
            code.append("  },\n");
        }
        
        if (request.body != null && !request.body.isEmpty()) {
            code.append("  data: '").append(request.body.replace("'", "\\'")).append("',\n");
        }
        
        code.append("  success: function(data) {\n");
        code.append("    console.log(data);\n");
        code.append("  },\n");
        code.append("  error: function(xhr, status, error) {\n");
        code.append("    console.error('Error:', error);\n");
        code.append("  }\n");
        code.append("});");
        
        return code.toString();
    }
    
    private String generateXHRCode(HttpRequest request) {
        StringBuilder code = new StringBuilder();
        code.append("const xhr = new XMLHttpRequest();\n");
        code.append("xhr.open('").append(request.method).append("', '").append(request.url).append("');\n");
        
        for (Map.Entry<String, String> header : request.headers.entrySet()) {
            code.append("xhr.setRequestHeader('").append(header.getKey()).append("', '").append(header.getValue()).append("');\n");
        }
        
        code.append("\nxhr.onreadystatechange = function() {\n");
        code.append("  if (xhr.readyState === 4 && xhr.status === 200) {\n");
        code.append("    console.log(xhr.responseText);\n");
        code.append("  }\n");
        code.append("};\n\n");
        
        if (request.body != null && !request.body.isEmpty()) {
            code.append("xhr.send('").append(request.body.replace("'", "\\'")).append("');");
        } else {
            code.append("xhr.send();");
        }
        
        return code.toString();
    }
    
    private String generatePythonCode(HttpRequest request, String library) {
        switch (library) {
            case "requests":
                return generatePythonRequestsCode(request);
            case "urllib":
                return generatePythonUrllibCode(request);
            case "http.client":
                return generatePythonHttpClientCode(request);
            default:
                return generatePythonRequestsCode(request);
        }
    }
    
    private String generatePythonRequestsCode(HttpRequest request) {
        StringBuilder code = new StringBuilder();
        code.append("import requests\n\n");
        
        code.append("url = '").append(request.url).append("'\n");
        
        if (!request.headers.isEmpty()) {
            code.append("headers = {\n");
            for (Map.Entry<String, String> header : request.headers.entrySet()) {
                code.append("    '").append(header.getKey()).append("': '").append(header.getValue()).append("',\n");
            }
            code.append("}\n");
        }
        
        if (request.body != null && !request.body.isEmpty()) {
            code.append("data = '").append(request.body.replace("'", "\\'")).append("'\n");
        }
        
        code.append("\nresponse = requests.").append(request.method.toLowerCase()).append("(url");
        
        if (!request.headers.isEmpty()) {
            code.append(", headers=headers");
        }
        
        if (request.body != null && !request.body.isEmpty()) {
            code.append(", data=data");
        }
        
        code.append(")\n");
        code.append("print(response.text)");
        
        return code.toString();
    }
    
    private String generatePythonUrllibCode(HttpRequest request) {
        return "Python urllib implementation";
    }
    
    private String generatePythonHttpClientCode(HttpRequest request) {
        return "Python http.client implementation";
    }
    
    private String generateJavaCode(HttpRequest request, String library) {
        return "Java " + library + " implementation";
    }
    
    private String generatePHPCode(HttpRequest request, String library) {
        return "PHP " + library + " implementation";
    }
    
    private String generateCSharpCode(HttpRequest request, String library) {
        return "C# " + library + " implementation";
    }
    
    private String generateGoCode(HttpRequest request, String library) {
        return "Go " + library + " implementation";
    }
    
    private String generateRubyCode(HttpRequest request, String library) {
        return "Ruby " + library + " implementation";
    }
    
    private String generateShellCode(HttpRequest request, String library) {
        return "Shell " + library + " implementation";
    }
    
    // HTTP请求数据类
    private static class HttpRequest {
        String url = "";
        String method = "GET";
        Map<String, String> headers = new HashMap<>();
        String body = "";
    }
}