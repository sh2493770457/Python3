package dev.ctool.burp.ui;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import dev.ctool.burp.ui.tools.HashToolPanel;
import dev.ctool.burp.ui.tools.AESToolPanel;
import dev.ctool.burp.ui.tools.Base64ToolPanel;
import dev.ctool.burp.ui.tools.JsonToolPanel;
import dev.ctool.burp.ui.tools.TimestampToolPanel;
import dev.ctool.burp.ui.tools.QRCodeToolPanel;
import dev.ctool.burp.ui.tools.UnicodeToolPanel;
import dev.ctool.burp.ui.tools.HexToolPanel;
import dev.ctool.burp.ui.tools.HMACToolPanel;
import dev.ctool.burp.ui.tools.BcryptToolPanel;
import dev.ctool.burp.ui.tools.GzipToolPanel;
import dev.ctool.burp.ui.tools.URLToolPanel;
import dev.ctool.burp.ui.tools.JWTToolPanel;
import dev.ctool.burp.ui.tools.RegexToolPanel;
import dev.ctool.burp.ui.tools.TextToolPanel;
import dev.ctool.burp.ui.tools.ColorToolPanel;
import dev.ctool.burp.ui.tools.ASCIIToolPanel;
import dev.ctool.burp.ui.tools.BinaryToolPanel;
import dev.ctool.burp.ui.tools.CodeFormatterToolPanel;
import dev.ctool.burp.ui.EncryptToolPanel;
import dev.ctool.burp.ui.RSAToolPanel;
import dev.ctool.burp.ui.ARMToolPanel;
import dev.ctool.burp.ui.ASN1ToolPanel;
import dev.ctool.burp.ui.BarcodeToolPanel;
import dev.ctool.burp.ui.CrontabToolPanel;
import dev.ctool.burp.ui.DataValidationToolPanel;
import dev.ctool.burp.ui.DiffsToolPanel;
import dev.ctool.burp.ui.PinyinToolPanel;
import dev.ctool.burp.ui.IPQueryToolPanel;
import dev.ctool.burp.ui.RandomGeneratorToolPanel;
import dev.ctool.burp.ui.HtmlEncodeToolPanel;
import dev.ctool.burp.ui.VariableNameToolPanel;
import dev.ctool.burp.ui.UuidToolPanel;
import dev.ctool.burp.ui.SignatureToolPanel;
import dev.ctool.burp.ui.RadixConverterToolPanel;
import dev.ctool.burp.ui.SerializationToolPanel;
import dev.ctool.burp.ui.UnitConverterToolPanel;
import dev.ctool.burp.ui.TimeCalculatorToolPanel;
import dev.ctool.burp.ui.IPCalculatorToolPanel;
import dev.ctool.burp.ui.SQLFillerToolPanel;
import dev.ctool.burp.ui.CodeRunnerToolPanel;
import dev.ctool.burp.ui.WebSocketToolPanel;
import dev.ctool.burp.ui.URLParserToolPanel;
import dev.ctool.burp.ui.ComplementCodeToolPanel;
import dev.ctool.burp.ui.HttpSnippetToolPanel;
import dev.ctool.burp.ui.tools.RC4ToolPanel;
import dev.ctool.burp.ui.tools.RabbitToolPanel;
import dev.ctool.burp.ui.tools.SM2ToolPanel;
import dev.ctool.burp.ui.tools.SM2SignatureToolPanel;
import dev.ctool.burp.ui.tools.SM4ToolPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Ctool主面板 - 提供工具选择和功能展示
 */
public class CtoolMainPanel extends JPanel {
    
    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    private final PrintWriter stdout;
    private final PrintWriter stderr;
    
    private JList<ToolItem> toolList;
    private JPanel toolPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> toolPanels;
    
    public CtoolMainPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers, 
                         PrintWriter stdout, PrintWriter stderr) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        this.stdout = stdout;
        this.stderr = stderr;
        
        initializeUI();
        initializeTools();
        
        // 在所有初始化完成后设置默认选择
        SwingUtilities.invokeLater(() -> {
            if (toolList != null && toolList.getModel().getSize() > 0) {
                toolList.setSelectedIndex(0);
            }
        });
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建工具列表
        createToolList();
        
        // 创建工具面板区域
        createToolPanel();
        
        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createToolListPanel());
        splitPane.setRightComponent(toolPanel);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.0);
        
        add(splitPane, BorderLayout.CENTER);
        
        // 添加顶部信息面板
        add(createHeaderPanel(), BorderLayout.NORTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel titleLabel = new JLabel("CryptoTools");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        JLabel versionLabel = new JLabel("v1.1.0");
        versionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        versionLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalStrut(10));
        headerPanel.add(versionLabel);
        
        return headerPanel;
    }
    
    private void createToolList() {
        DefaultListModel<ToolItem> listModel = new DefaultListModel<>();
        
        // 添加工具项
        listModel.addElement(new ToolItem("hash", "哈希(Hash)", "MD5, SHA1, SHA256, SHA512, SM3"));
        listModel.addElement(new ToolItem("aes", "AES加密/解密", "AES加密解密工具"));
        listModel.addElement(new ToolItem("base64", "Base64编码/解码", "Base64编码解码工具"));
        listModel.addElement(new ToolItem("json", "JSON工具", "JSON格式化、压缩、验证"));
        listModel.addElement(new ToolItem("timestamp", "时间戳转换", "时间戳与日期互转"));
        listModel.addElement(new ToolItem("qrcode", "二维码生成/解析", "二维码生成和解析工具"));
        listModel.addElement(new ToolItem("unicode", "Unicode编码/解码", "Unicode编码解码"));
        listModel.addElement(new ToolItem("hex", "十六进制转换", "十六进制与字符串互转"));
        listModel.addElement(new ToolItem("hmac", "HMAC签名", "HMAC消息认证码"));
        listModel.addElement(new ToolItem("bcrypt", "Bcrypt密码哈希", "Bcrypt密码加密和验证"));
        listModel.addElement(new ToolItem("gzip", "GZIP压缩/解压", "GZIP数据压缩和解压缩"));
        listModel.addElement(new ToolItem("url", "URL工具", "URL编码/解码和解析"));
        listModel.addElement(new ToolItem("jwt", "JWT工具", "JWT令牌生成、解析和验证"));
        listModel.addElement(new ToolItem("regex", "正则表达式", "正则匹配和测试工具"));
        listModel.addElement(new ToolItem("text", "文本处理", "文本转换、处理和统计"));
        listModel.addElement(new ToolItem("color", "颜色工具", "颜色格式转换和选择器"));
        listModel.addElement(new ToolItem("ascii", "ASCII工具", "ASCII码转换和参考表"));
        listModel.addElement(new ToolItem("binary", "二进制工具", "二进制转换和处理工具"));
        listModel.addElement(new ToolItem("code", "代码格式化", "多种编程语言代码格式化工具"));
        listModel.addElement(new ToolItem("encrypt", "加密工具", "多种加密算法工具"));
        listModel.addElement(new ToolItem("rsa", "RSA工具", "RSA密钥生成、加密解密和数字签名工具"));
        listModel.addElement(new ToolItem("arm", "ARM汇编工具", "ARM汇编代码转换和分析工具"));
        listModel.addElement(new ToolItem("asn1", "ASN.1解码工具", "ASN.1 DER格式解码和XML格式化工具"));
        listModel.addElement(new ToolItem("barcode", "条形码工具", "条形码生成和解析工具"));
        listModel.addElement(new ToolItem("crontab", "Crontab工具", "Cron表达式生成和解析工具"));
        listModel.addElement(new ToolItem("dataValidation", "数据校验工具", "BCC/CRC/LRC校验计算工具"));
        listModel.addElement(new ToolItem("diffs", "文本对比工具", "文本差异对比和合并工具"));
        listModel.addElement(new ToolItem("pinyin", "汉字转拼音", "汉字转拼音工具，支持声调和首字母提取"));
        listModel.addElement(new ToolItem("ipquery", "IP地址查询", "IP地址信息查询工具，支持地理位置和网络信息查询"));
        listModel.addElement(new ToolItem("randomgen", "随机字符生成", "随机字符串生成工具，支持多种字符类型和自定义字符集"));
        listModel.addElement(new ToolItem("htmlencode", "HTML编码", "HTML实体编码和解码工具"));
        listModel.addElement(new ToolItem("variablename", "变量名转换", "变量名格式转换工具，支持驼峰、下划线等格式互转"));
        listModel.addElement(new ToolItem("uuid", "UUID生成", "UUID生成工具，支持多种UUID版本"));
        listModel.addElement(new ToolItem("signature", "签名验签", "数字签名生成和验证工具"));
        listModel.addElement(new ToolItem("radix", "进制转换", "二进制、八进制、十进制、十六进制互转工具"));
         listModel.addElement(new ToolItem("serialization", "序列化转换", "JSON、XML、YAML等格式互转工具"));
          listModel.addElement(new ToolItem("unit", "单位换算", "长度、重量、温度等单位转换工具"));
        listModel.addElement(new ToolItem("timecalc", "时间计算器", "时间计算、时区转换、时间戳转换工具"));
        listModel.addElement(new ToolItem("ipcalc", "IP网络计算器", "IPv4和IPv6网络计算、IP地址转换工具"));
        listModel.addElement(new ToolItem("sqlfiller", "SQL参数填充", "SQL语句参数替换和格式化工具"));
        // 暂时注释掉代码运行工具，因为相关功能有问题
        // listModel.addElement(new ToolItem("coderunner", "代码运行", "JavaScript、Groovy等代码在线执行工具"));
        listModel.addElement(new ToolItem("websocket", "WebSocket", "WebSocket连接测试和消息收发工具"));
        listModel.addElement(new ToolItem("urlparser", "URL解析", "URL组件解析和构建工具"));
        listModel.addElement(new ToolItem("complement", "源码反码补码", "二进制数的源码、反码、补码转换工具"));
        listModel.addElement(new ToolItem("httpsnippet", "HTTP请求代码", "HTTP请求代码生成工具，支持多种编程语言"));
        listModel.addElement(new ToolItem("rc4", "RC4流密码", "RC4流密码加密解密工具"));
        listModel.addElement(new ToolItem("rabbit", "Rabbit流密码", "Rabbit流密码加密解密工具"));
        listModel.addElement(new ToolItem("sm2", "SM2国密算法", "SM2椭圆曲线公钥密码算法"));
        listModel.addElement(new ToolItem("sm2signature", "SM2数字签名", "SM2数字签名和验证工具"));
        listModel.addElement(new ToolItem("sm4", "SM4国密算法", "SM4分组密码算法"));
        
        toolList = new JList<>(listModel);
        toolList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toolList.setCellRenderer(new ToolItemRenderer());
        
        // 添加选择监听器
        toolList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ToolItem selectedTool = toolList.getSelectedValue();
                if (selectedTool != null && cardLayout != null) {
                    showTool(selectedTool.getId());
                }
            }
        });
    }
    
    private JPanel createToolListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(new TitledBorder("工具列表"));
        
        JScrollPane scrollPane = new JScrollPane(toolList);
        scrollPane.setPreferredSize(new Dimension(250, 0));
        
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        return listPanel;
    }
    
    private void createToolPanel() {
        cardLayout = new CardLayout();
        toolPanel = new JPanel(cardLayout);
        toolPanel.setBorder(new TitledBorder("工具面板"));
        
        toolPanels = new HashMap<>();
    }
    
    private void initializeTools() {
        // 初始化各个工具面板
        addToolPanel("hash", new HashToolPanel(callbacks, helpers));
        addToolPanel("aes", new AESToolPanel(callbacks, helpers));
        addToolPanel("base64", new Base64ToolPanel(callbacks, helpers));
        addToolPanel("json", new JsonToolPanel(callbacks, helpers));
        addToolPanel("timestamp", new TimestampToolPanel(callbacks, helpers));
        addToolPanel("qrcode", new QRCodeToolPanel(callbacks, helpers));
        addToolPanel("unicode", new UnicodeToolPanel(callbacks, helpers));
        addToolPanel("hex", new HexToolPanel(callbacks, helpers));
        addToolPanel("hmac", new HMACToolPanel(callbacks, helpers));
        addToolPanel("bcrypt", new BcryptToolPanel());
        addToolPanel("gzip", new GzipToolPanel());
        addToolPanel("url", new URLToolPanel());
        addToolPanel("jwt", new JWTToolPanel());
        addToolPanel("regex", new RegexToolPanel());
        addToolPanel("text", new TextToolPanel());
        addToolPanel("color", new ColorToolPanel());
        addToolPanel("ascii", new ASCIIToolPanel());
        addToolPanel("binary", new BinaryToolPanel());
        addToolPanel("code", new CodeFormatterToolPanel());
        addToolPanel("encrypt", new EncryptToolPanel());
        addToolPanel("rsa", new RSAToolPanel());
        addToolPanel("arm", new ARMToolPanel());
        addToolPanel("asn1", new ASN1ToolPanel());
        addToolPanel("barcode", new BarcodeToolPanel());
        addToolPanel("crontab", new CrontabToolPanel());
        addToolPanel("dataValidation", new DataValidationToolPanel());
        addToolPanel("diffs", new DiffsToolPanel());
        addToolPanel("pinyin", new PinyinToolPanel());
        addToolPanel("ipquery", new IPQueryToolPanel());
        addToolPanel("randomgen", new RandomGeneratorToolPanel());
        addToolPanel("htmlencode", new HtmlEncodeToolPanel());
        addToolPanel("variablename", new VariableNameToolPanel());
        addToolPanel("uuid", new UuidToolPanel());
        addToolPanel("signature", new SignatureToolPanel());
        addToolPanel("radix", new RadixConverterToolPanel());
         addToolPanel("serialization", new SerializationToolPanel());
          addToolPanel("unit", new UnitConverterToolPanel());
        addToolPanel("timecalc", new TimeCalculatorToolPanel());
        addToolPanel("ipcalc", new IPCalculatorToolPanel());
        addToolPanel("sqlfiller", new SQLFillerToolPanel());
        // 暂时注释掉代码运行工具面板，因为相关功能有问题
        // addToolPanel("coderunner", new CodeRunnerToolPanel());
        addToolPanel("websocket", new WebSocketToolPanel());
        addToolPanel("urlparser", new URLParserToolPanel());
        addToolPanel("complement", new ComplementCodeToolPanel());
        addToolPanel("httpsnippet", new HttpSnippetToolPanel());
        addToolPanel("rc4", new RC4ToolPanel());
        addToolPanel("rabbit", new RabbitToolPanel());
        addToolPanel("sm2", new SM2ToolPanel());
        addToolPanel("sm2signature", new SM2SignatureToolPanel());
        addToolPanel("sm4", new SM4ToolPanel());
        
        // 显示第一个工具
        showTool("hash");
    }
    
    private void addToolPanel(String id, JPanel panel) {
        toolPanels.put(id, panel);
        toolPanel.add(panel, id);
    }
    
    private void showTool(String toolId) {
        cardLayout.show(toolPanel, toolId);
    }
    
    /**
     * 工具项数据类
     */
    private static class ToolItem {
        private final String id;
        private final String name;
        private final String description;
        
        public ToolItem(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**
     * 工具项渲染器
     */
    private static class ToolItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ToolItem) {
                ToolItem item = (ToolItem) value;
                setText(item.getName());
                setToolTipText(item.getDescription());
            }
            
            return this;
        }
    }
}