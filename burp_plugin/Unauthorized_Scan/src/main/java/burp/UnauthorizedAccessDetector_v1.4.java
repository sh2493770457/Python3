package burp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.RowSorter;
import javax.swing.table.TableRowSorter;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;

/**
 * 未授权访问检测Burp扩展
 * 通过删除指定认证请求头并比较响应来检测未授权访问漏洞
 */
public class UnauthorizedAccessDetector implements IBurpExtender, IScannerCheck, ITab, IContextMenuFactory, IProxyListener, IHttpListener {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private PrintWriter stdout;
    private PrintWriter stderr;
    
    // UI组件
    private JPanel mainPanel;
    private JTable resultsTable;
    private IssuesTableModel issuesModel;
    private JTextArea headersTextArea;
    private JCheckBox activeCheckBox;
    private JCheckBox passiveScanCheckBox;
    private JCheckBox enabledCheckBox;  // 新增：是否启用插件的全局开关
    private JPanel requestResponseViewer;  // 改为：
    private IMessageEditor originalRequestViewer;  // 新增：原始请求查看器
    private IMessageEditor originalResponseViewer;  // 新增：原始响应查看器
    private IMessageEditor unauthorizedRequestViewer;  // 新增：未授权请求查看器
    private IMessageEditor unauthorizedResponseViewer;  // 新增：未授权响应查看器
    private JButton exportButton;  // 新增：导出按钮
    
    // 新增：检测阈值设置
    private JTextField thresholdField;
    private int responseLengthThreshold = 50; // 默认50字节的差异阈值
    
    // 流量表格和模型
    private JTable trafficTable;
    private TrafficTableModel trafficTableModel;
    
    // 过滤状态标签
    private JLabel filterStatusLabel;
    
    // 存储检测到的问题
    private List<ScanIssue> issues = new ArrayList<>();
    
    // 新增：存储所有经过的数据包及其未授权版本
    private List<IHttpRequestResponse> allTraffic = new ArrayList<>();
    private List<IHttpRequestResponse> unauthorizedTraffic = new ArrayList<>();
    private List<Boolean> isVulnerableList = new ArrayList<>();  // 新增：标记是否存在漏洞
    
    // 需要删除的认证头列表
    private List<String> headersToRemove = new ArrayList<>();
    
    // 资源过滤相关
    private boolean filterEnabled = true; // 是否启用资源过滤
    private boolean filterImages = true; // 过滤图片
    private boolean filterCSS = true; // 过滤CSS
    private boolean filterJS = true; // 过滤JavaScript
    private boolean filterFonts = true; // 过滤字体文件
    private boolean filterStatic = true; // 过滤静态资源
    private String customFilterPattern = ""; // 自定义过滤正则表达式
    private Pattern compiledCustomPattern; // 编译后的正则表达式
    
    // 用于过滤的UI元素
    private JCheckBox filterEnabledCheckBox;
    private JCheckBox filterImagesCheckBox;
    private JCheckBox filterCSSCheckBox;
    private JCheckBox filterJSCheckBox;
    private JCheckBox filterFontsCheckBox;
    private JCheckBox filterStaticCheckBox;
    private JTextField customFilterTextField;
    
    // 用于过滤的预定义正则表达式
    private static final Pattern IMAGE_PATTERN = Pattern.compile("(?i)\\.(png|jpg|jpeg|gif|bmp|ico|svg|webp)$");
    private static final Pattern CSS_PATTERN = Pattern.compile("(?i)\\.(css)$");
    private static final Pattern JS_PATTERN = Pattern.compile("(?i)\\.(js)$");
    private static final Pattern FONT_PATTERN = Pattern.compile("(?i)\\.(woff|woff2|ttf|eot|otf)$");
    private static final Pattern STATIC_PATTERN = Pattern.compile("(?i)\\.(pdf|zip|rar|doc|docx|xls|xlsx|ppt|pptx|txt|xml|json)$");
    
    // 当前选中的问题
    private ScanIssue currentlyDisplayedItem;  // 新增：当前显示的问题
    
    // 存储原始的所有流量和用于显示的过滤后流量
    private List<IHttpRequestResponse> filteredTraffic = new ArrayList<>();

    // 新增：设置键常量，用于持久化存储
    private static final String SETTING_ENABLED = "unauthorized_detector.enabled";
    private static final String SETTING_ACTIVE_SCAN = "unauthorized_detector.active_scan";
    private static final String SETTING_PASSIVE_SCAN = "unauthorized_detector.passive_scan";
    private static final String SETTING_FILTER_ENABLED = "unauthorized_detector.filter_enabled";
    private static final String SETTING_FILTER_IMAGES = "unauthorized_detector.filter_images";
    private static final String SETTING_FILTER_CSS = "unauthorized_detector.filter_css";
    private static final String SETTING_FILTER_JS = "unauthorized_detector.filter_js";
    private static final String SETTING_FILTER_FONTS = "unauthorized_detector.filter_fonts";
    private static final String SETTING_FILTER_STATIC = "unauthorized_detector.filter_static";
    private static final String SETTING_CUSTOM_FILTER = "unauthorized_detector.custom_filter";
    private static final String SETTING_HEADERS_TO_REMOVE = "unauthorized_detector.headers_to_remove";
    // 新增：响应长度差异阈值设置键
    private static final String SETTING_THRESHOLD = "unauthorized_detector.threshold";
    // 新增：去重相关设置键
    private static final String SETTING_AUTO_DEDUPE = "unauthorized_detector.auto_dedupe";
    private static final String SETTING_QUERY_PARAM_DEDUPE = "unauthorized_detector.query_param_dedupe";
    private static final String SETTING_DEDUPE_IGNORE_PARAMS = "unauthorized_detector.dedupe_ignore_params";
    // 新增：列配置设置键
    private static final String SETTING_ISSUES_COLUMNS = "unauthorized_detector.issues_columns";
    private static final String SETTING_TRAFFIC_COLUMNS = "unauthorized_detector.traffic_columns";
    
    // 新增：临时变量存储加载的设置值
    private boolean loadedEnabledState = true;
    private boolean loadedActiveScanState = true;
    private boolean loadedPassiveScanState = true;
    private String loadedHeadersText = "Authorization\nCookie\nX-Auth-Token\nJWT-Token";

    // 新增：批量扫描相关变量
    private volatile boolean isBatchScanning = false;
    private volatile boolean shouldStopBatchScan = false;
    private Thread batchScanThread = null;
    private int batchScanProgress = 0;
    private int batchScanTotal = 0;

    // 新增：去重相关变量
    private boolean enableAutoDedupe = true; // 是否启用自动去重
    private boolean enableQueryParamDedupe = true; // 是否启用查询参数去重
    private String dedupeIgnoreParams = "timestamp,date,time,_t,_,random,nonce,csrf_token"; // 忽略的参数列表
    private JCheckBox autoDedupeCheckBox;
    private JCheckBox queryParamDedupeCheckBox;
    private JTextField dedupeIgnoreParamsField;

    // 新增：列配置相关变量
    private ColumnConfigManager issuesColumnConfig;
    private ColumnConfigManager trafficColumnConfig;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        
        // 设置扩展名称
        callbacks.setExtensionName("UnauthorizedScan");
        
        // 加载保存的设置
        loadSettings();

        // 初始化列配置管理器
        initializeColumnConfigs();

        // 初始化UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initializeUI();
            }
        });
        
        // 注册为Scanner检查
        callbacks.registerScannerCheck(this);
        
        // 注册为上下文菜单工厂
        callbacks.registerContextMenuFactory(this);
        
        // 注册为代理监听器和HTTP监听器
        callbacks.registerProxyListener(this);
        callbacks.registerHttpListener(this);
        
        stdout.println("UnauthorizedScan加载成功!");
    }
    
    private void initializeUI() {
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("配置"));
        
        // 创建一个水平功能按钮面板，将所有功能放在同一排
        JPanel functionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        functionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("功能控制"));
        
        // 添加所有功能控制到同一行
        // 启用插件复选框
        enabledCheckBox = new JCheckBox("启用插件");
        enabledCheckBox.setSelected(loadedEnabledState);
        enabledCheckBox.setToolTipText("全局开关，控制插件是否处理请求");
        enabledCheckBox.setFont(enabledCheckBox.getFont().deriveFont(enabledCheckBox.getFont().getSize2D() - 1f));
        enabledCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
        functionPanel.add(enabledCheckBox);
        
        // 主动扫描复选框
        activeCheckBox = new JCheckBox("启用主动扫描");
        activeCheckBox.setSelected(loadedActiveScanState);
        activeCheckBox.setFont(activeCheckBox.getFont().deriveFont(activeCheckBox.getFont().getSize2D() - 1f));
        activeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
        functionPanel.add(activeCheckBox);
        
        // 被动扫描复选框
        passiveScanCheckBox = new JCheckBox("启用被动扫描");
        passiveScanCheckBox.setSelected(loadedPassiveScanState);
        passiveScanCheckBox.setFont(passiveScanCheckBox.getFont().deriveFont(passiveScanCheckBox.getFont().getSize2D() - 1f));
        passiveScanCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
        functionPanel.add(passiveScanCheckBox);
        
        // 导出为MD文档按钮
        exportButton = new JButton("导出为MD文档");
        exportButton.setFont(exportButton.getFont().deriveFont(exportButton.getFont().getSize2D() - 1f));
        exportButton.setMargin(new Insets(1, 2, 1, 2));
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToMarkdown();
            }
        });
        functionPanel.add(exportButton);
        
        // 导出为TXT按钮
        JButton exportTxtButton = new JButton("导出为TXT");
        exportTxtButton.setFont(exportTxtButton.getFont().deriveFont(exportTxtButton.getFont().getSize2D() - 1f));
        exportTxtButton.setMargin(new Insets(1, 2, 1, 2));
        exportTxtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToTxt();
            }
        });
        functionPanel.add(exportTxtButton);
        
        // 导出API接口按钮
        JButton exportApiButton = new JButton("导出API接口");
        exportApiButton.setFont(exportApiButton.getFont().deriveFont(exportApiButton.getFont().getSize2D() - 1f));
        exportApiButton.setMargin(new Insets(1, 2, 1, 2));
        exportApiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToApiJson();
            }
        });
        functionPanel.add(exportApiButton);
        
        // 导出未授权请求按钮
        JButton exportUnauthReqButton = new JButton("导出未授权请求");
        exportUnauthReqButton.setFont(exportUnauthReqButton.getFont().deriveFont(exportUnauthReqButton.getFont().getSize2D() - 1f));
        exportUnauthReqButton.setMargin(new Insets(1, 2, 1, 2));
        exportUnauthReqButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportUnauthorizedRequests();
            }
        });
        functionPanel.add(exportUnauthReqButton);
        
        // 清空结果按钮
        JButton clearButton = new JButton("清空结果");
        clearButton.setFont(clearButton.getFont().deriveFont(clearButton.getFont().getSize2D() - 1f));
        clearButton.setMargin(new Insets(1, 2, 1, 2));
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });
        functionPanel.add(clearButton);

        // 新增：去重按钮
        JButton dedupeButton = new JButton("智能去重");
        dedupeButton.setFont(dedupeButton.getFont().deriveFont(dedupeButton.getFont().getSize2D() - 1f));
        dedupeButton.setMargin(new Insets(1, 2, 1, 2));
        dedupeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performManualDedupe();
            }
        });
        functionPanel.add(dedupeButton);

        // 新增：批量扫描历史记录按钮
        JButton batchScanButton = new JButton("批量扫描历史");
        batchScanButton.setFont(batchScanButton.getFont().deriveFont(batchScanButton.getFont().getSize2D() - 1f));
        batchScanButton.setMargin(new Insets(1, 2, 1, 2));
        batchScanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startBatchScanAllHistory();
            }
        });
        functionPanel.add(batchScanButton);

        // 新增：停止批量扫描按钮
        JButton stopScanButton = new JButton("停止扫描");
        stopScanButton.setFont(stopScanButton.getFont().deriveFont(stopScanButton.getFont().getSize2D() - 1f));
        stopScanButton.setMargin(new Insets(1, 2, 1, 2));
        stopScanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopBatchScan();
            }
        });
        functionPanel.add(stopScanButton);
        
        // 过滤器开关
        filterEnabledCheckBox = new JCheckBox("启用过滤");
        filterEnabledCheckBox.setSelected(filterEnabled);
        filterEnabledCheckBox.setToolTipText("是否启用资源过滤功能");
        filterEnabledCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterEnabled = filterEnabledCheckBox.isSelected();
                applyFilter();
                saveSettings();
            }
        });
        functionPanel.add(filterEnabledCheckBox);
        
        // ---------- 新增：阈值输入框 ----------
        JLabel thresholdLabel = new JLabel("阈值(字节):");
        thresholdLabel.setFont(thresholdLabel.getFont().deriveFont(thresholdLabel.getFont().getSize2D() - 1f));
        functionPanel.add(thresholdLabel);

        thresholdField = new JTextField(String.valueOf(responseLengthThreshold), 4);
        thresholdField.setFont(thresholdField.getFont().deriveFont(thresholdField.getFont().getSize2D() - 1f));
        functionPanel.add(thresholdField);

        JButton applyThresholdButton = new JButton("应用阈值");
        applyThresholdButton.setFont(applyThresholdButton.getFont().deriveFont(applyThresholdButton.getFont().getSize2D() - 1f));
        applyThresholdButton.setMargin(new Insets(1, 3, 1, 3));
        applyThresholdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int newVal = Integer.parseInt(thresholdField.getText().trim());
                    if (newVal <= 0) throw new NumberFormatException();
                    responseLengthThreshold = newVal;
                    saveSettings();
                    reEvaluateVulnerabilities();
                    stdout.println("已更新阈值为 " + responseLengthThreshold + " 字节并重新评估历史流量");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "请输入有效的正整数阈值！", "输入错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        functionPanel.add(applyThresholdButton);
        
        // 将功能面板添加到配置面板顶部
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2; // 让功能面板跨越两列
        configPanel.add(functionPanel, gbc);
        
        // 创建资源过滤控制面板
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("资源过滤"));
        GridBagConstraints filterGbc = new GridBagConstraints();
        filterGbc.gridx = 0;
        filterGbc.gridy = 0;
        filterGbc.insets = new Insets(1, 1, 1, 3); // 减小内边距使布局更紧凑
        filterGbc.anchor = GridBagConstraints.WEST;
        
        // 注意：不再创建重复的过滤器开关，使用上面功能控制面板中的filterEnabledCheckBox
        
        // 过滤图片 - 紧凑布局
        filterImagesCheckBox = new JCheckBox("图片");
        filterImagesCheckBox.setSelected(filterImages);
        filterImagesCheckBox.setToolTipText("过滤图片资源 (PNG, JPG, GIF等)");
        filterImagesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterImages = filterImagesCheckBox.isSelected();
                applyFilter();
                saveSettings();
            }
        });
        filterImagesCheckBox.setMargin(new Insets(0, 0, 0, 0)); // 减小复选框内边距
        filterPanel.add(filterImagesCheckBox, filterGbc);
        
        // 过滤CSS - 紧凑布局
        filterGbc.gridx++;
        filterCSSCheckBox = new JCheckBox("CSS");
        filterCSSCheckBox.setSelected(filterCSS);
        filterCSSCheckBox.setToolTipText("过滤CSS样式表");
        filterCSSCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterCSS = filterCSSCheckBox.isSelected();
                applyFilter();
                saveSettings();
            }
        });
        filterCSSCheckBox.setMargin(new Insets(0, 0, 0, 0)); // 减小复选框内边距
        filterPanel.add(filterCSSCheckBox, filterGbc);
        
        // 过滤JS - 紧凑布局
        filterGbc.gridx++;
        filterJSCheckBox = new JCheckBox("JS");
        filterJSCheckBox.setSelected(filterJS);
        filterJSCheckBox.setToolTipText("过滤JavaScript文件");
        filterJSCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterJS = filterJSCheckBox.isSelected();
                applyFilter();
                saveSettings();
            }
        });
        filterJSCheckBox.setMargin(new Insets(0, 0, 0, 0)); // 减小复选框内边距
        filterPanel.add(filterJSCheckBox, filterGbc);
        
        // 过滤字体 - 紧凑布局
        filterGbc.gridx++;
        filterFontsCheckBox = new JCheckBox("字体");
        filterFontsCheckBox.setSelected(filterFonts);
        filterFontsCheckBox.setToolTipText("过滤字体文件 (WOFF, TTF等)");
        filterFontsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterFonts = filterFontsCheckBox.isSelected();
                applyFilter();
                saveSettings();
            }
        });
        filterFontsCheckBox.setMargin(new Insets(0, 0, 0, 0)); // 减小复选框内边距
        filterPanel.add(filterFontsCheckBox, filterGbc);
        
        // 过滤静态资源 - 紧凑布局
        filterGbc.gridx++;
        filterStaticCheckBox = new JCheckBox("其他静态");
        filterStaticCheckBox.setSelected(filterStatic);
        filterStaticCheckBox.setToolTipText("过滤其他静态资源 (PDF, ZIP等)");
        filterStaticCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterStatic = filterStaticCheckBox.isSelected();
                applyFilter();
                saveSettings();
            }
        });
        filterStaticCheckBox.setMargin(new Insets(0, 0, 0, 0)); // 减小复选框内边距
        filterPanel.add(filterStaticCheckBox, filterGbc);
        
        // 自定义过滤器输入框 - 紧凑布局
        filterGbc.gridx = 0;
        filterGbc.gridy = 1;
        filterGbc.gridwidth = 2; // 减少标签宽度
        JLabel filterLabel = new JLabel("自定义过滤正则:");
        filterLabel.setFont(filterLabel.getFont().deriveFont(filterLabel.getFont().getSize2D() - 1f)); // 稍微减小字体
        filterPanel.add(filterLabel, filterGbc);
        
        filterGbc.gridx = 2; // 向左移动
        filterGbc.gridwidth = 2;
        customFilterTextField = new JTextField(20);
        customFilterTextField.setText(customFilterPattern);
        customFilterTextField.setToolTipText("输入正则表达式来过滤URL");
        filterPanel.add(customFilterTextField, filterGbc);
        
        filterGbc.gridx = 4;
        filterGbc.gridwidth = 1;
        JButton applyCustomFilterButton = new JButton("应用");
        applyCustomFilterButton.setMargin(new Insets(1, 3, 1, 3)); // 减小按钮边距
        applyCustomFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customFilterPattern = customFilterTextField.getText().trim();
                updateCustomFilterPattern();
                applyFilter();
                saveSettings();
            }
        });
        filterPanel.add(applyCustomFilterButton, filterGbc);

        // 新增：去重配置区域
        filterGbc.gridx = 0;
        filterGbc.gridy = 2;
        filterGbc.gridwidth = 1;
        autoDedupeCheckBox = new JCheckBox("自动去重");
        autoDedupeCheckBox.setSelected(enableAutoDedupe);
        autoDedupeCheckBox.setToolTipText("自动去除重复的URL漏洞");
        autoDedupeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableAutoDedupe = autoDedupeCheckBox.isSelected();
                saveSettings();
            }
        });
        autoDedupeCheckBox.setMargin(new Insets(0, 0, 0, 0));
        filterPanel.add(autoDedupeCheckBox, filterGbc);

        filterGbc.gridx = 1;
        queryParamDedupeCheckBox = new JCheckBox("参数去重");
        queryParamDedupeCheckBox.setSelected(enableQueryParamDedupe);
        queryParamDedupeCheckBox.setToolTipText("忽略时间戳等参数进行去重");
        queryParamDedupeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableQueryParamDedupe = queryParamDedupeCheckBox.isSelected();
                saveSettings();
            }
        });
        queryParamDedupeCheckBox.setMargin(new Insets(0, 0, 0, 0));
        filterPanel.add(queryParamDedupeCheckBox, filterGbc);

        // 忽略参数配置
        filterGbc.gridx = 0;
        filterGbc.gridy = 3;
        filterGbc.gridwidth = 2;
        JLabel dedupeLabel = new JLabel("忽略参数(如删除?ts=xxxxx):");
        dedupeLabel.setFont(dedupeLabel.getFont().deriveFont(dedupeLabel.getFont().getSize2D() - 1f));
        filterPanel.add(dedupeLabel, filterGbc);

        filterGbc.gridx = 2;
        filterGbc.gridwidth = 2;
        dedupeIgnoreParamsField = new JTextField(20);
        dedupeIgnoreParamsField.setText(dedupeIgnoreParams);
        dedupeIgnoreParamsField.setToolTipText("用逗号分隔的忽略参数列表");
        filterPanel.add(dedupeIgnoreParamsField, filterGbc);

        filterGbc.gridx = 4;
        filterGbc.gridwidth = 1;
        JButton applyDedupeButton = new JButton("应用");
        applyDedupeButton.setMargin(new Insets(1, 3, 1, 3));
        applyDedupeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dedupeIgnoreParams = dedupeIgnoreParamsField.getText().trim();
                saveSettings();
            }
        });
        filterPanel.add(applyDedupeButton, filterGbc);

        // 添加过滤状态标签
        JPanel filterStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 1)); // 减小组件间距
        filterStatusLabel = new JLabel("过滤状态: 原始流量 0 条，显示 0 条");
        filterStatusLabel.setFont(filterStatusLabel.getFont().deriveFont(filterStatusLabel.getFont().getSize2D() - 1f)); // 稍微减小字体
        filterStatusPanel.add(filterStatusLabel);
        
        JButton refreshButton = new JButton("刷新流量");
        refreshButton.setMargin(new Insets(1, 3, 1, 3)); // 减小按钮边距
        refreshButton.setFont(refreshButton.getFont().deriveFont(refreshButton.getFont().getSize2D() - 1f)); // 稍微减小字体
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter();
                stdout.println("手动刷新流量表格，当前记录: " + allTraffic.size() + " 条，显示: " + filteredTraffic.size() + " 条");
            }
        });
        filterStatusPanel.add(refreshButton);
        
        // 创建认证头部分面板
        JPanel authHeaderPanel = new JPanel(new BorderLayout(2, 2));
        authHeaderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("认证头"));
        JLabel headerLabel = new JLabel("需要删除的认证头:");
        headerLabel.setFont(headerLabel.getFont().deriveFont(headerLabel.getFont().getSize2D() - 1f));
        authHeaderPanel.add(headerLabel, BorderLayout.NORTH);
        
        headersTextArea = new JTextArea(3, 15);
        headersTextArea.setText(loadedHeadersText);
        headersTextArea.setFont(headersTextArea.getFont().deriveFont(headersTextArea.getFont().getSize2D() - 1f));
        JScrollPane headersScrollPane = new JScrollPane(headersTextArea);
        authHeaderPanel.add(headersScrollPane, BorderLayout.CENTER);
        
        // 添加认证头部分的更新按钮
        JButton headerUpdateButton = new JButton("应用");
        headerUpdateButton.setFont(headerUpdateButton.getFont().deriveFont(headerUpdateButton.getFont().getSize2D() - 1f));
        headerUpdateButton.setMargin(new Insets(1, 2, 1, 2));
        headerUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateHeadersList();
                saveSettings();
            }
        });
        authHeaderPanel.add(headerUpdateButton, BorderLayout.SOUTH);
        
        // 将过滤器面板和认证头面板添加到配置面板的第二行，左右排列
        gbc.gridwidth = 1; // 重置为1列宽
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.5; // 左侧占一半宽度
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        configPanel.add(authHeaderPanel, gbc);
        
        gbc.gridx = 1;
        configPanel.add(filterPanel, gbc);
        
        // 添加过滤状态面板到配置面板最底部
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // 跨越2列
        gbc.weighty = 0.05;
        configPanel.add(filterStatusPanel, gbc);
        
        // 隐藏阈值设置，但保留默认值
        thresholdField = new JTextField("50", 4);
        responseLengthThreshold = 50; // 设置默认阈值
        
        // 创建选项卡面板包含结果和流量
        JTabbedPane resultsTabbedPane = new JTabbedPane();
        
        // 结果表格
        issuesModel = new IssuesTableModel();
        resultsTable = new JTable(issuesModel);
        JScrollPane issuesScrollPane = new JScrollPane(resultsTable);

        // 启用表格多选
        resultsTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // 新增：添加表格排序功能
        TableRowSorter<TableModel> issuesSorter = new TableRowSorter<>(issuesModel);
        resultsTable.setRowSorter(issuesSorter);

        // 新增：为未授权漏洞表格添加列配置右键菜单
        resultsTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showColumnConfigMenu(e, resultsTable, issuesColumnConfig, "未授权漏洞表格列设置", () -> {
                        issuesModel.fireTableStructureChanged();
                        // 重新设置排序器
                        TableRowSorter<TableModel> newSorter = new TableRowSorter<>(issuesModel);
                        resultsTable.setRowSorter(newSorter);
                    });
                }
            }
        });

        // 新增：为未授权漏洞表格添加键盘支持
        addKeyboardNavigationSupport(resultsTable, true);
        
        // 新增：所有流量表格
        trafficTableModel = new TrafficTableModel();
        trafficTable = new JTable(trafficTableModel);
        JScrollPane trafficScrollPane = new JScrollPane(trafficTable);

        // 新增：添加流量表格排序功能
        TableRowSorter<TableModel> trafficSorter = new TableRowSorter<>(trafficTableModel);
        trafficTable.setRowSorter(trafficSorter);

        // 启用流量表格多选
        trafficTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // 新增：为所有流量表格添加列配置右键菜单
        trafficTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showColumnConfigMenu(e, trafficTable, trafficColumnConfig, "所有流量表格列设置", () -> {
                        trafficTableModel.fireTableStructureChanged();
                        // 重新设置排序器
                        TableRowSorter<TableModel> newSorter = new TableRowSorter<>(trafficTableModel);
                        trafficTable.setRowSorter(newSorter);
                    });
                }
            }
        });

        // 新增：为所有流量表格添加键盘支持
        addKeyboardNavigationSupport(trafficTable, false);
        
        // 添加到选项卡
        resultsTabbedPane.addTab("未授权漏洞(自行筛选)", issuesScrollPane);
        resultsTabbedPane.addTab("所有流量(重复的包不检测)", trafficScrollPane);
        
        // 初始化消息查看器
        requestResponseViewer = new JPanel();
        requestResponseViewer.setLayout(new GridLayout(1, 4, 4, 0)); // 四列，间距4像素
        
        // 创建消息编辑器并设置它们为只读模式
        MessageEditorController editorController = new MessageEditorController();
        originalRequestViewer = callbacks.createMessageEditor(editorController, false);
        originalResponseViewer = callbacks.createMessageEditor(editorController, false);
        unauthorizedRequestViewer = callbacks.createMessageEditor(editorController, false);
        unauthorizedResponseViewer = callbacks.createMessageEditor(editorController, false);
        
        // 每个编辑器加标题
        JPanel originalRequestPanel = new JPanel(new BorderLayout());
        originalRequestPanel.add(new JLabel("原始请求", JLabel.CENTER), BorderLayout.NORTH);
        originalRequestPanel.add(originalRequestViewer.getComponent(), BorderLayout.CENTER);
        
        JPanel originalResponsePanel = new JPanel(new BorderLayout());
        originalResponsePanel.add(new JLabel("原始响应", JLabel.CENTER), BorderLayout.NORTH);
        originalResponsePanel.add(originalResponseViewer.getComponent(), BorderLayout.CENTER);
        
        JPanel unauthorizedRequestPanel = new JPanel(new BorderLayout());
        unauthorizedRequestPanel.add(new JLabel("未授权请求", JLabel.CENTER), BorderLayout.NORTH);
        unauthorizedRequestPanel.add(unauthorizedRequestViewer.getComponent(), BorderLayout.CENTER);
        
        JPanel unauthorizedResponsePanel = new JPanel(new BorderLayout());
        unauthorizedResponsePanel.add(new JLabel("未授权响应", JLabel.CENTER), BorderLayout.NORTH);
        unauthorizedResponsePanel.add(unauthorizedResponseViewer.getComponent(), BorderLayout.CENTER);
        
        requestResponseViewer.add(originalRequestPanel);
        requestResponseViewer.add(originalResponsePanel);
        requestResponseViewer.add(unauthorizedRequestPanel);
        requestResponseViewer.add(unauthorizedResponsePanel);
        
        // 添加表格选择监听器
        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取选中的行，对于多选，显示最后一个选中的行的内容
                int viewRow = resultsTable.getSelectedRow();
                int row = viewRow != -1 ? resultsTable.convertRowIndexToModel(viewRow) : -1;
                if (row != -1) {
                    ScanIssue issue = issues.get(row);
                    currentlyDisplayedItem = issue;
                    
                    // 显示原始请求和响应
                    IHttpRequestResponse[] messages = issue.getHttpMessages();
                    if (messages.length >= 2) {
                        // 第一个消息是原始请求/响应
                        originalRequestViewer.setMessage(messages[0].getRequest(), true);
                        originalResponseViewer.setMessage(messages[0].getResponse(), false);
                        
                        // 第二个消息是未授权请求/响应
                        unauthorizedRequestViewer.setMessage(messages[1].getRequest(), true);
                        unauthorizedResponseViewer.setMessage(messages[1].getResponse(), false);
                    }
                }
                
                // 处理右键点击
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e, resultsTable);
                }
            }
        });
        
        // 添加流量表格选择监听器
        trafficTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = trafficTable.getSelectedRow();
                int row = viewRow != -1 ? trafficTable.convertRowIndexToModel(viewRow) : -1;
                
                if (row == -1) return;
                
                // 获取当前使用的流量列表（根据过滤状态）
                List<IHttpRequestResponse> trafficToUse = filterEnabled ? filteredTraffic : allTraffic;
                
                if (row < trafficToUse.size()) {
                    IHttpRequestResponse message = trafficToUse.get(row);
                    
                    // 显示原始请求和响应
                    originalRequestViewer.setMessage(message.getRequest(), true);
                    originalResponseViewer.setMessage(message.getResponse(), false);
                    
                    // 找出该消息在原始列表中的索引，以便获取对应的未授权请求
                    int originalIndex = allTraffic.indexOf(message);
                    
                    // 显示未授权请求和响应（如果存在）
                    if (originalIndex != -1 && originalIndex < unauthorizedTraffic.size() && 
                        unauthorizedTraffic.get(originalIndex) != null) {
                        IHttpRequestResponse unauthorizedMessage = unauthorizedTraffic.get(originalIndex);
                        unauthorizedRequestViewer.setMessage(unauthorizedMessage.getRequest(), true);
                        unauthorizedResponseViewer.setMessage(unauthorizedMessage.getResponse(), false);
                    } else {
                        // 如果未授权请求不存在，清空查看器
                        unauthorizedRequestViewer.setMessage(new byte[0], true);
                        unauthorizedResponseViewer.setMessage(new byte[0], false);
                    }
                }
                
                // 处理右键点击
                if (SwingUtilities.isRightMouseButton(e)) {
                    showTrafficContextMenu(e, trafficTable);
                }
            }
        });
        
        // 为每个消息编辑器添加右键菜单功能
        addEditorContextMenu(originalRequestViewer.getComponent());
        addEditorContextMenu(originalResponseViewer.getComponent());
        addEditorContextMenu(unauthorizedRequestViewer.getComponent());
        addEditorContextMenu(unauthorizedResponseViewer.getComponent());
        
        // 创建一个垂直分割面板包含表格和请求/响应查看器
        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultsTabbedPane, requestResponseViewer);
        resultsSplitPane.setResizeWeight(0.5); // 增加上部表格的权重，使表格和请求/响应查看器各占一半
        
        // 将配置面板和结果分割面板添加到主分割面板
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, configPanel, resultsSplitPane);
        mainSplitPane.setResizeWeight(0.1); // 减小配置区域的权重，使结果区域占据更多空间
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(mainSplitPane, gbc);
        
        // 初始化认证头列表
        updateHeadersList();
        
        // 初始化过滤器
        updateCustomFilterPattern();
        applyFilter();
        
        // 将面板添加到Burp的UI
        callbacks.customizeUiComponent(mainPanel);
        callbacks.addSuiteTab(this);
        
        // 添加关闭时自动保存设置的钩子
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                saveSettings();
                stdout.println("应用关闭时保存设置完成");
            }
        }));
    }
    
    private void updateHeadersList() {
        headersToRemove.clear();
        String[] headers = headersTextArea.getText().split("\\n");
        for (String header : headers) {
            String trimmed = header.trim();
            if (!trimmed.isEmpty()) {
                headersToRemove.add(trimmed);
            }
        }
        stdout.println("更新认证头列表: " + headersToRemove);
        // 使用默认阈值
        responseLengthThreshold = 50;
        stdout.println("使用默认响应长度差异阈值: " + responseLengthThreshold + " 字节");
    }

    @Override
    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        // 检查全局开关和被动扫描开关
        if (!enabledCheckBox.isSelected() || !passiveScanCheckBox.isSelected()) {
            return null;
        }
        
        // 记录所有经过的流量
        recordTraffic(baseRequestResponse);
        
        // 如果是静态资源且过滤功能开启，不进行未授权检测
        if (filterEnabled && shouldFilter(baseRequestResponse)) {
            stdout.println("跳过对静态资源的未授权检测: " + helpers.analyzeRequest(baseRequestResponse).getUrl());
            return null;
        }
        
        return checkForUnauthorizedAccess(baseRequestResponse);
    }

    @Override
    public List<IScanIssue> doActiveScan(IHttpRequestResponse baseRequestResponse, IScannerInsertionPoint insertionPoint) {
        // 检查全局开关和主动扫描开关
        if (!enabledCheckBox.isSelected() || !activeCheckBox.isSelected()) {
            return null;
        }
        
        // 记录所有经过的流量
        recordTraffic(baseRequestResponse);
        
        // 如果是静态资源且过滤功能开启，不进行未授权检测
        if (filterEnabled && shouldFilter(baseRequestResponse)) {
            stdout.println("跳过对静态资源的主动扫描: " + helpers.analyzeRequest(baseRequestResponse).getUrl());
            return null;
        }
        
        return checkForUnauthorizedAccess(baseRequestResponse);
    }

    private List<IScanIssue> checkForUnauthorizedAccess(IHttpRequestResponse baseRequestResponse) {
        try {
            // 获取原始请求和响应
            byte[] originalRequest = baseRequestResponse.getRequest();
            byte[] originalResponse = baseRequestResponse.getResponse();

            if (originalRequest == null) {
                stdout.println("未授权检测：原始请求为空，无法进行检测");
                return null;
            }

            if (originalResponse == null) {
                String urlForLog = "N/A";
                try {
                    urlForLog = helpers.analyzeRequest(baseRequestResponse).getUrl().toString();
                } catch (Exception e) {
                    // Ignore, urlForLog remains "N/A"
                }
                stdout.println("未授权检测：原始响应为空，无法进行检测。 URL: " + urlForLog);
                return null; // 响应为空，无法进行检测
            }

            IRequestInfo requestInfo = helpers.analyzeRequest(baseRequestResponse);
            List<String> originalHeaders = requestInfo.getHeaders();

            if (originalHeaders.isEmpty()) {
                stdout.println("未授权检测：原始请求头列表为空，无法构建未授权请求，跳过。 URL: " + requestInfo.getUrl());
                return null;
            }

            // 调试：输出原始请求的所有头部
            stdout.println("未授权检测：原始请求头数量：" + originalHeaders.size() + "，URL：" + requestInfo.getUrl());
            if (stdout.checkError() == false) { // 避免输出太多日志导致stdout错误
                for (int i = 0; i < Math.min(20, originalHeaders.size()); i++) { // 限制最多输出20个头部
                    stdout.println("  原始头[" + i + "]: " + originalHeaders.get(i));
                }
            }

            // 创建一个新的请求，删除所有认证头
            List<String> newHeaders = new ArrayList<>();
            newHeaders.add(originalHeaders.get(0)); // 始终保留请求行 (e.g., "GET /path HTTP/1.1")

            boolean removedAnyAuthHeader = false;
            // 从索引1开始处理实际的请求头
            for (int i = 1; i < originalHeaders.size(); i++) {
                String header = originalHeaders.get(i);
                boolean skip = false;
                for (String authHeader : headersToRemove) {
                    if (header.toLowerCase().startsWith(authHeader.toLowerCase() + ":")) {
                        skip = true;
                        stdout.println("未授权检测：删除认证头 - " + header + " (URL: " + requestInfo.getUrl() + ")");
                        removedAnyAuthHeader = true;
                        break;
                    }
                }
                if (!skip) {
                    newHeaders.add(header);
                }
            }

            // 调试：输出新构建的请求的所有头部
            stdout.println("未授权检测：新构建请求头数量：" + newHeaders.size() + "，URL：" + requestInfo.getUrl());
            if (stdout.checkError() == false) {
                for (int i = 0; i < Math.min(20, newHeaders.size()); i++) {
                    stdout.println("  新头[" + i + "]: " + newHeaders.get(i));
                }
            }

            // 提取并打印请求体信息
            int bodyOffset = requestInfo.getBodyOffset();
            stdout.println("未授权检测：请求体偏移量：" + bodyOffset + "，原始请求总长度：" + originalRequest.length);
            
            // 确保请求体提取正确
            byte[] bodyBytes;
            if (bodyOffset < originalRequest.length) {
                bodyBytes = Arrays.copyOfRange(originalRequest, bodyOffset, originalRequest.length);
                stdout.println("未授权检测：提取请求体长度：" + bodyBytes.length + " 字节");
            } else {
                bodyBytes = new byte[0];
                stdout.println("未授权检测：无请求体或请求体为空，使用空字节数组");
            }
            
            // 检查请求体前几个字节
            if (bodyBytes.length > 0) {
                try {
                    int previewLength = Math.min(50, bodyBytes.length);
                    String bodyPreview = new String(bodyBytes, 0, previewLength, "UTF-8");
                    // 替换不可打印字符为点，避免控制台输出混乱
                    bodyPreview = bodyPreview.replaceAll("[\\p{C}]", ".");
                    stdout.println("未授权检测：请求体预览：" + bodyPreview + (bodyBytes.length > previewLength ? "..." : ""));
                } catch (Exception e) {
                    stdout.println("未授权检测：请求体预览失败：" + e.getMessage());
                }
            }

            // 构建未授权请求
            byte[] newRequest = helpers.buildHttpMessage(newHeaders, bodyBytes);

            if (newRequest == null || newRequest.length == 0) {
                stdout.println("未授权检测：构建的未授权请求为空或无效，跳过。 URL: " + requestInfo.getUrl());
                return null;
            }

            // 验证新请求的正确性
            stdout.println("未授权检测：新构建的未授权请求长度：" + newRequest.length + " 字节");
            try {
                IRequestInfo newReqInfo = helpers.analyzeRequest(newRequest);
                stdout.println("未授权检测：新请求方法：" + newReqInfo.getMethod() + "，URL：" + newReqInfo.getUrl());
            } catch (Exception e) {
                stdout.println("未授权检测：警告 - 无法解析新构建的请求：" + e.getMessage());
                // 尽管警告，但仍然继续尝试发送请求
            }

            stdout.println("未授权检测：创建未授权请求，URL=" + requestInfo.getUrl());

            // 发送没有认证头的请求
            IHttpRequestResponse unauthorizedRequestResponse = null;
            int maxRetries = 3;
            int retryCount = 0;
            boolean success = false;
            
            while (!success && retryCount < maxRetries) {
                try {
                    stdout.println("未授权检测：尝试 #" + (retryCount + 1) + " 发送未授权请求...");
                    unauthorizedRequestResponse = callbacks.makeHttpRequest(
                            baseRequestResponse.getHttpService(), newRequest);
                    
                    if (unauthorizedRequestResponse == null) {
                        stdout.println("未授权检测：警告 - makeHttpRequest返回null，重试...");
                        retryCount++;
                        continue;
                    }
                    
                    if (unauthorizedRequestResponse.getResponse() == null) {
                        stdout.println("未授权检测：警告 - 未收到未授权响应，重试...");
                        retryCount++;
                        continue;
                    }
                    
                    // 获取到有效响应
                    success = true;
                } catch (Exception e) {
                    retryCount++;
                    stdout.println("未授权检测：发送未授权请求异常 #" + retryCount + ": " + e.getMessage());
                    if (retryCount >= maxRetries) {
                        stdout.println("未授权检测：达到最大重试次数，放弃...");
                        e.printStackTrace(stdout);
                    } else {
                        try {
                            // 短暂等待后重试
                            Thread.sleep(500);
                        } catch (InterruptedException ie) {
                            // 忽略中断
                        }
                    }
                }
            }
            
            // 如果所有尝试都失败，但我们仍需要返回一个请求/响应对象用于显示
            if (!success || unauthorizedRequestResponse == null || unauthorizedRequestResponse.getResponse() == null) {
                stdout.println("未授权检测：所有尝试发送未授权请求失败，不进行后续比较");
                
                // 创建一个模拟响应记录到allTraffic，方便用户排查
                IHttpRequestResponse mockResponse = createMockResponseObject(
                    baseRequestResponse.getHttpService(), 
                    newRequest, 
                    "尝试发送未授权请求失败，无法获取响应"
                );
                
                unauthorizedTraffic.add(mockResponse);
                isVulnerableList.add(false);
                
                return null; // 返回null表示无漏洞，但已记录尝试
            }
            
            byte[] unauthorizedResponse = unauthorizedRequestResponse.getResponse();
            
            // 分析响应
            IResponseInfo originalResponseInfo = helpers.analyzeResponse(originalResponse);
            IResponseInfo unauthorizedResponseInfo = helpers.analyzeResponse(unauthorizedResponse);
            
            int originalStatusCode = originalResponseInfo.getStatusCode();
            int unauthorizedStatusCode = unauthorizedResponseInfo.getStatusCode();
            
            stdout.println("未授权检测：原始状态码=" + originalStatusCode + ", 未授权状态码=" + unauthorizedStatusCode + ", URL=" + requestInfo.getUrl());

            int originalLength = originalResponse.length - originalResponseInfo.getBodyOffset();
            int unauthorizedLength = unauthorizedResponse.length - unauthorizedResponseInfo.getBodyOffset();

            stdout.println("未授权检测：原始长度=" + originalLength + ", 未授权长度=" + unauthorizedLength + ", 差异=" + Math.abs(originalLength - unauthorizedLength) + ", URL=" + requestInfo.getUrl());

            // 检查原始响应和未授权响应的MIME类型
            String originalMimeType = originalResponseInfo.getStatedMimeType();
            String unauthorizedMimeType = unauthorizedResponseInfo.getStatedMimeType();
            stdout.println("未授权检测：原始MIME类型=" + originalMimeType + ", 未授权MIME类型=" + unauthorizedMimeType);

            // 新增：只在未授权响应为2xx且长度大于0时才判定
            boolean hasOriginalLength = originalLength > 0;
            boolean hasUnauthorizedLength = unauthorizedLength > 0;
            boolean hasValidStatusCode = unauthorizedStatusCode >= 200 && unauthorizedStatusCode < 300;
            boolean isExcludedStatusCode = unauthorizedStatusCode == 204 || unauthorizedStatusCode == 304 || 
                                        unauthorizedStatusCode == 401 || unauthorizedStatusCode == 403 || 
                                        unauthorizedStatusCode == 404;
            boolean hasSmallDifference = Math.abs(originalLength - unauthorizedLength) < responseLengthThreshold;

            // 详细记录判断过程
            stdout.println("未授权检测：判断条件：");
            stdout.println("  - 原始响应长度>0: " + hasOriginalLength);
            stdout.println("  - 未授权响应长度>0: " + hasUnauthorizedLength);
            stdout.println("  - 未授权状态码是2xx: " + hasValidStatusCode);
            stdout.println("  - 未授权状态码不在排除列表中: " + !isExcludedStatusCode);
            stdout.println("  - 响应长度差异<" + responseLengthThreshold + ": " + hasSmallDifference);

            if (originalLength == 0 || unauthorizedLength == 0) {
                stdout.println("未授权检测：响应长度为0，不判定为漏洞");
                return null;
            }
            if (!(unauthorizedStatusCode >= 200 && unauthorizedStatusCode < 300)) {
                stdout.println("未授权检测：未授权响应状态码不是2xx，不判定为漏洞");
                return null;
            }
            if (unauthorizedStatusCode == 304 || unauthorizedStatusCode == 401 ||
                unauthorizedStatusCode == 403 || unauthorizedStatusCode == 404) {
                stdout.println("未授权检测：未授权响应状态码为" + unauthorizedStatusCode + "，不判定为漏洞");
                return null;
            }

            // 如果是JSON API，尝试比较JSON内容
            boolean isJsonResponse = originalMimeType.toLowerCase().contains("json") || 
                                  unauthorizedMimeType.toLowerCase().contains("json");
            if (isJsonResponse) {
                stdout.println("未授权检测：检测到JSON响应，尝试比较响应内容");
                // 提取响应体字符串
                String originalBody = "";
                String unauthorizedBody = "";
                try {
                    originalBody = new String(originalResponse, originalResponseInfo.getBodyOffset(), originalLength, "UTF-8");
                    unauthorizedBody = new String(unauthorizedResponse, unauthorizedResponseInfo.getBodyOffset(), unauthorizedLength, "UTF-8");
                    // 去除所有空白字符以进行更精确的比较
                    originalBody = originalBody.replaceAll("\\s", "");
                    unauthorizedBody = unauthorizedBody.replaceAll("\\s", "");
                    stdout.println("未授权检测：原始JSON长度(无空白)=" + originalBody.length() + ", 未授权JSON长度(无空白)=" + unauthorizedBody.length());
                    
                    // 可能有特殊条件：如果未授权响应是JSON错误消息，但仍然有内容，不算漏洞
                    boolean jsonErrorPatternFound = unauthorizedBody.contains("\"error\"") || 
                                                 unauthorizedBody.contains("\"message\"") || 
                                                 unauthorizedBody.contains("\"code\"");
                    if (jsonErrorPatternFound) {
                        stdout.println("未授权检测：未授权响应包含JSON错误模式，可能不是漏洞");
                    }
                } catch (Exception e) {
                    stdout.println("未授权检测：解析JSON响应时出错: " + e.getMessage());
                }
            }

            // 检查响应长度 - 简化为仅比较长度
            boolean isVulnerable = false;
            String reason = "";
            
            if (Math.abs(originalLength - unauthorizedLength) < responseLengthThreshold) { // 使用可配置的阈值
                isVulnerable = true;
                reason = "移除认证头后，响应内容长度与原始请求基本相同，差异小于 " + responseLengthThreshold + " 字节";
                stdout.println("未授权检测：发现漏洞 - " + reason);
            }
            
            if (isVulnerable) {
                // 输出详情信息
                String detail = String.format(
                        "状态: %s\n原始状态码: %d\n未授权状态码: %d\n原始长度: %d\n未授权长度: %d\n差异: %d\n漏洞判断依据: %s",
                        "High",
                        originalStatusCode,
                        unauthorizedStatusCode,
                        originalLength,
                        unauthorizedLength,
                        Math.abs(originalLength - unauthorizedLength),
                        reason
                );
                
                ScanIssue issue = new ScanIssue(
                        baseRequestResponse.getHttpService(),
                        requestInfo.getUrl(),
                        new IHttpRequestResponse[] { baseRequestResponse, unauthorizedRequestResponse },
                        "未授权访问漏洞",
                        detail,
                        "High",
                        "Certain"
                );
                
                // 使用统一的添加漏洞方法（包含自动去重）
                addIssueWithAutoDedupe(issue);
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        issuesModel.fireTableDataChanged();
                    }
                });
                
                List<IScanIssue> reportIssues = new ArrayList<>();
                reportIssues.add(issue);
                return reportIssues;
            } else {
                stdout.println("未授权检测：未发现漏洞");
            }
            
        } catch (Exception e) {
            stderr.println("检测未授权访问时出错: " + e.getMessage());
            e.printStackTrace(stderr);
        }
        
        return null;
    }

    @Override
    public int consolidateDuplicateIssues(IScanIssue existingIssue, IScanIssue newIssue) {
        if (existingIssue.getUrl().equals(newIssue.getUrl()) &&
                existingIssue.getIssueName().equals(newIssue.getIssueName())) {
            return -1; // 重复问题
        }
        return 0; // 不同问题
    }

    @Override
    public String getTabCaption() {
        return "UnauthorizedScan";
    }

    @Override
    public Component getUiComponent() {
        return mainPanel;
    }
    
    // 用于显示扫描结果的表格模型（增强版，支持可配置列）
    private class IssuesTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return issues.size();
        }

        @Override
        public int getColumnCount() {
            return issuesColumnConfig.getVisibleColumnCount();
        }

        @Override
        public String getColumnName(int column) {
            String[] visibleColumns = issuesColumnConfig.getVisibleColumns();
            return column < visibleColumns.length ? visibleColumns[column] : "";
        }

        @Override
        public Class<?> getColumnClass(int column) {
            int actualColumn = issuesColumnConfig.mapVisibleToActual(column);
            switch (actualColumn) {
                case 0: return Integer.class; // #
                case 4: return Integer.class; // 状态码
                case 5: return Integer.class; // 原始长度
                case 6: return Integer.class; // 未授权长度
                case 7: return Integer.class; // 长度差异
                case 17: return java.util.Date.class; // 时间
                default: return String.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= issues.size()) {
                return "";
            }

            ScanIssue issue = issues.get(rowIndex);
            IHttpRequestResponse[] messages = issue.getHttpMessages();
            IHttpRequestResponse originalMessage = messages[0];
            IRequestInfo requestInfo = helpers.analyzeRequest(originalMessage);

            int actualColumn = issuesColumnConfig.mapVisibleToActual(columnIndex);

            switch (actualColumn) {
                case 0: // #
                    return rowIndex + 1;
                case 1: // Host
                    return requestInfo.getUrl().getHost();
                case 2: // 请求方法
                    return extractRequestMethod(originalMessage.getRequest());
                case 3: // URL
                    return issue.getUrl().toString();
                case 4: // 状态码
                    if (originalMessage.getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(originalMessage.getResponse());
                        return responseInfo.getStatusCode();
                    }
                    return "";
                case 5: // 原始长度
                    if (originalMessage.getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(originalMessage.getResponse());
                        return originalMessage.getResponse().length - responseInfo.getBodyOffset();
                    }
                    return "";
                case 6: // 未授权长度
                    if (messages.length > 1 && messages[1].getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(messages[1].getResponse());
                        return messages[1].getResponse().length - responseInfo.getBodyOffset();
                    }
                    return "";
                case 7: // 长度差异
                    if (messages.length > 1 && originalMessage.getResponse() != null && messages[1].getResponse() != null) {
                        IResponseInfo origRespInfo = helpers.analyzeResponse(originalMessage.getResponse());
                        IResponseInfo unauthRespInfo = helpers.analyzeResponse(messages[1].getResponse());
                        int origLen = originalMessage.getResponse().length - origRespInfo.getBodyOffset();
                        int unauthLen = messages[1].getResponse().length - unauthRespInfo.getBodyOffset();
                        return Math.abs(origLen - unauthLen);
                    }
                    return "";
                case 8: // MIME类型
                    if (originalMessage.getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(originalMessage.getResponse());
                        return responseInfo.getInferredMimeType();
                    }
                    return "";
                case 9: // 参数
                    List<IParameter> params = requestInfo.getParameters();
                    return params.isEmpty() ? "" : String.valueOf(params.size());
                case 10: // 已编辑
                    return ""; // 暂时不支持
                case 11: // 扩展
                    String url = issue.getUrl().toString();
                    int lastDot = url.lastIndexOf('.');
                    int lastSlash = url.lastIndexOf('/');
                    if (lastDot > lastSlash && lastDot < url.length() - 1) {
                        return url.substring(lastDot + 1);
                    }
                    return "";
                case 12: // 标题
                    return ""; // 暂时不支持
                case 13: // Notes
                    return ""; // 暂时不支持
                case 14: // TLS
                    return requestInfo.getUrl().getProtocol().equalsIgnoreCase("https") ? "Yes" : "";
                case 15: // IP
                    return ""; // 暂时不支持
                case 16: // Cookies
                    List<IParameter> cookies = requestInfo.getParameters();
                    long cookieCount = cookies.stream().filter(p -> p.getType() == IParameter.PARAM_COOKIE).count();
                    return cookieCount > 0 ? String.valueOf(cookieCount) : "";
                case 17: // 时间
                    // 尝试从请求中获取时间信息，如果没有则使用当前时间
                    return new java.util.Date();
                default:
                    return "";
            }
        }
    }
    
    // 扫描问题实现
    private class ScanIssue implements IScanIssue {
        private final IHttpService httpService;
        private final java.net.URL url;
        private final IHttpRequestResponse[] requestResponses;
        private final String name;
        private final String detail;
        private final String severity;
        private final String confidence;
        
        public ScanIssue(IHttpService httpService, java.net.URL url, IHttpRequestResponse[] requestResponses,
                            String name, String detail, String severity, String confidence) {
            this.httpService = httpService;
            this.url = url;
            this.requestResponses = requestResponses;
            this.name = name;
            this.detail = detail;
            this.severity = severity;
            this.confidence = confidence;
        }
        
        @Override
        public java.net.URL getUrl() {
            return url;
        }
        
        @Override
        public String getIssueName() {
            return name;
        }
        
        @Override
        public int getIssueType() {
            return 0;
        }
        
        @Override
        public String getSeverity() {
            return severity;
        }
        
        @Override
        public String getConfidence() {
            return confidence;
        }
        
        @Override
        public String getIssueBackground() {
            return "未授权访问漏洞是由于缺少适当的授权控制机制，使未经授权的用户可以访问受限资源。当移除认证头部后，服务器仍然返回与有认证头部的请求相同或非常相似的响应时，就说明存在未授权访问漏洞。";
        }
        
        @Override
        public String getRemediationBackground() {
            return "确保所有敏感操作和资源都通过适当的授权检查。实施合理的访问控制机制，例如基于角色的访问控制(RBAC)，确保用户只能访问他们有权访问的资源。";
        }
        
        @Override
        public String getIssueDetail() {
            return detail;
        }
        
        @Override
        public String getRemediationDetail() {
            return "1. 确保服务器端代码在处理每个请求时都进行正确的认证和授权检查\n" +
                   "2. 采用一致的访问控制机制，并在所有API端点上实施\n" +
                   "3. 使用安全框架来处理认证和授权，避免手动实现\n" +
                   "4. 定期审查访问控制机制，确保它们正常工作";
        }
        
        @Override
        public IHttpRequestResponse[] getHttpMessages() {
            return requestResponses;
        }
        
        @Override
        public IHttpService getHttpService() {
            return httpService;
        }
    }
    
    // 为组件添加右键菜单
    private void addEditorContextMenu(Component component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showEditorContextMenu(e, component);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showEditorContextMenu(e, component);
                }
            }
        });
    }
    
    // 显示消息编辑器的上下文菜单
    private void showEditorContextMenu(MouseEvent e, Component component) {
        if (currentlyDisplayedItem == null) {
            return;
        }
        
        JPopupMenu menu = new JPopupMenu();
        
        // 确定当前哪个请求/响应查看器处理
        byte[] request = null;
        byte[] response = null;
        IHttpService service = null;
        IHttpRequestResponse message = null;
        
        if (component == originalRequestViewer.getComponent() || component == originalResponseViewer.getComponent()) {
            message = currentlyDisplayedItem.getHttpMessages()[0];
        } else if (component == unauthorizedRequestViewer.getComponent() || component == unauthorizedResponseViewer.getComponent()) {
            message = currentlyDisplayedItem.getHttpMessages()[1];
        }
        
        if (message != null) {
            service = message.getHttpService();
            request = message.getRequest();
            response = message.getResponse();
        }
        
        // 为菜单添加常见的burp工具选项
        addBurpToolsMenuItems(menu, service, request, response);
        
        // 显示菜单
        menu.show(component, e.getX(), e.getY());
    }
    
    // 表格的上下文菜单
    private void showContextMenu(MouseEvent e, Component component) {
        if (component != resultsTable) {
            return;
        }
        
        JPopupMenu menu = new JPopupMenu();
        
        // 添加删除菜单项
        JMenuItem deleteItem = new JMenuItem("删除选中项");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRows();
            }
        });
        menu.add(deleteItem);
        
        // 分隔线
        menu.addSeparator();
        
        // 获取当前选中的行并显示Burp工具选项
        int viewRow = resultsTable.getSelectedRow();
        int row = viewRow != -1 ? resultsTable.convertRowIndexToModel(viewRow) : -1;
        if (row != -1) {
            ScanIssue issue = issues.get(row);
            IHttpRequestResponse[] messages = issue.getHttpMessages();
            if (messages.length >= 2) {
                menu.add(new JLabel("发送原始请求到:"));
                IHttpService service = messages[0].getHttpService();
                byte[] request = messages[0].getRequest();
                byte[] response = messages[0].getResponse();
                addBurpToolsMenuItems(menu, service, request, response);
                
                menu.addSeparator();
                
                menu.add(new JLabel("发送未授权请求到:"));
                service = messages[1].getHttpService();
                request = messages[1].getRequest();
                response = messages[1].getResponse();
                addBurpToolsMenuItems(menu, service, request, response);
            }
        }
        
        menu.show(component, e.getX(), e.getY());
    }
    
    // 添加Burp工具菜单项
    private void addBurpToolsMenuItems(JPopupMenu menu, IHttpService service, byte[] request, byte[] response) {
        // 只在有请求时添加菜单项
        if (service == null || request == null) {
            return;
        }
        
        // 发送到Repeater
        JMenuItem sendToRepeaterItem = new JMenuItem("发送到Repeater");
        sendToRepeaterItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callbacks.sendToRepeater(
                        service.getHost(),
                        service.getPort(),
                        service.getProtocol().equals("https"),
                        request,
                        "未授权测试");
            }
        });
        menu.add(sendToRepeaterItem);
        
        // 发送到Intruder
        JMenuItem sendToIntruderItem = new JMenuItem("发送到Intruder");
        sendToIntruderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callbacks.sendToIntruder(
                        service.getHost(),
                        service.getPort(),
                        service.getProtocol().equals("https"),
                        request);
            }
        });
        menu.add(sendToIntruderItem);
        
        // 发送到Scanner
        JMenuItem sendToScannerItem = new JMenuItem("发送到Scanner");
        sendToScannerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callbacks.doActiveScan(
                        service.getHost(),
                        service.getPort(),
                        service.getProtocol().equals("https"),
                        request);
            }
        });
        menu.add(sendToScannerItem);
        
        // 发送到Comparer
        JMenuItem sendToComparerRequestItem = new JMenuItem("发送请求到Comparer");
        sendToComparerRequestItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callbacks.sendToComparer(request);
            }
        });
        menu.add(sendToComparerRequestItem);
        
        // 如果有响应，添加响应相关选项
        if (response != null) {
            JMenuItem sendToComparerResponseItem = new JMenuItem("发送响应到Comparer");
            sendToComparerResponseItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    callbacks.sendToComparer(response);
                }
            });
            menu.add(sendToComparerResponseItem);
        }
        
        // 发送到Decoder
        JMenuItem sendToDecoderItem = new JMenuItem("发送到Decoder");
        sendToDecoderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 使用复制到剪贴板功能替代，因为Burp API没有直接的sendToDecoder方法
                try {
                    // 将请求转换为字符串
                    String requestStr = helpers.bytesToString(request);
                    // 复制到系统剪贴板
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                        new java.awt.datatransfer.StringSelection(requestStr), null);
                    // 提示用户
                    callbacks.issueAlert("已复制请求到剪贴板，可粘贴到Decoder中");
                } catch (Exception ex) {
                    stderr.println("复制到剪贴板时出错: " + ex.getMessage());
                }
            }
        });
        menu.add(sendToDecoderItem);
        
        // 添加分隔符和其他高级选项
        menu.addSeparator();
        
        // 在浏览器中打开URL
        JMenuItem openInBrowserItem = new JMenuItem("在浏览器中打开URL");
        openInBrowserItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    IRequestInfo requestInfo = helpers.analyzeRequest(request);
                    callbacks.issueAlert("打开URL: " + requestInfo.getUrl().toString());
                    // 使用burp提供的打开URL的方法，如果不可用，可以使用Java的Desktop类
                    try {
                        java.awt.Desktop.getDesktop().browse(requestInfo.getUrl().toURI());
                    } catch (Exception ex) {
                        stderr.println("在浏览器中打开URL时出错: " + ex.getMessage());
                    }
                } catch (Exception ex) {
                    stderr.println("在浏览器中打开URL时出错: " + ex.getMessage());
                }
            }
        });
        menu.add(openInBrowserItem);
    }
    
    // 消息编辑器控制器，用于支持上下文操作
    private class MessageEditorController implements IMessageEditorController {
        @Override
        public IHttpService getHttpService() {
            return currentlyDisplayedItem != null ? currentlyDisplayedItem.getHttpService() : null;
        }
        
        @Override
        public byte[] getRequest() {
            if (currentlyDisplayedItem == null || currentlyDisplayedItem.getHttpMessages().length == 0) {
                return null;
            }
            return currentlyDisplayedItem.getHttpMessages()[0].getRequest();
        }
        
        @Override
        public byte[] getResponse() {
            if (currentlyDisplayedItem == null || currentlyDisplayedItem.getHttpMessages().length == 0) {
                return null;
            }
            return currentlyDisplayedItem.getHttpMessages()[0].getResponse();
        }
    }
    
    // 导出为Markdown文档
    private void exportToMarkdown() {
        if (issues.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "没有可以导出的结果！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存Markdown文件");
            fileChooser.setSelectedFile(new File("未授权访问漏洞报告_" + 
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".md"));
            
            int userChoice = fileChooser.showSaveDialog(mainPanel);
            
            if (userChoice == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                
                try (FileWriter writer = new FileWriter(outputFile)) {
                    writer.write("# 未授权访问漏洞检测报告\n\n");
                    writer.write("生成时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n\n");
                    
                    // 新增：在顶部添加所有未授权URL的代码框
                    writer.write("## 所有漏洞URL\n\n");
                    writer.write("```\n");
                    for (int i = 0; i < issues.size(); i++) {
                        IHttpRequestResponse[] messages = issues.get(i).getHttpMessages();
                        String requestMethod = extractRequestMethod(messages[0].getRequest());
                        writer.write(String.format("%3d. [%s] %s\n", (i+1), requestMethod, issues.get(i).getUrl().toString()));
                    }
                    writer.write("```\n\n");
                    
                    writer.write("## 发现的漏洞\n\n");
                    
                    writer.write("| 编号 | URL | 请求方法 | 原始响应长度 | 未授权响应长度 | 状态 |\n");
                    writer.write("|------|-----|----------|------------|--------------|------|\n");
                    
                    int count = 1;
                    for (ScanIssue issue : issues) {
                        IHttpRequestResponse[] messages = issue.getHttpMessages();
                        String url = issue.getUrl().toString();
                        String requestMethod = extractRequestMethod(messages[0].getRequest());
                        
                        IResponseInfo originalResponseInfo = helpers.analyzeResponse(messages[0].getResponse());
                        IResponseInfo unauthorizedResponseInfo = helpers.analyzeResponse(messages[1].getResponse());
                        
                        int originalLength = messages[0].getResponse().length - originalResponseInfo.getBodyOffset();
                        int unauthorizedLength = messages[1].getResponse().length - unauthorizedResponseInfo.getBodyOffset();
                        
                        writer.write(String.format("| %d | %s | %s | %d | %d | %s |\n",
                                count++, url, requestMethod, originalLength, unauthorizedLength, issue.getSeverity()));
                    }
                    
                    writer.write("\n## 详细信息\n\n");
                    count = 1;
                    for (ScanIssue issue : issues) {
                        IHttpRequestResponse[] messages = issue.getHttpMessages();
                        String url = issue.getUrl().toString();
                        String requestMethod = extractRequestMethod(messages[0].getRequest());
                        
                        writer.write("### " + count + ". " + url + "\n\n");
                        writer.write("- **请求方法**: " + requestMethod + "\n");
                        writer.write("- **漏洞等级**: " + issue.getSeverity() + "\n");
                        // 修复：将<br>替换为Markdown换行（两个空格加换行）
                        String detail = issue.getIssueDetail().replace("\n", "  \n");
                        writer.write("- **详情**: " + detail + "\n\n");
                        
                        // 原始请求
                        writer.write("#### 原始请求\n\n");
                        writer.write("```http\n");
                        writer.write(helpers.bytesToString(messages[0].getRequest()));
                        writer.write("\n```\n\n");
                        
                        // 未授权请求
                        writer.write("#### 未授权请求\n\n");
                        writer.write("```http\n");
                        writer.write(helpers.bytesToString(messages[1].getRequest()));
                        writer.write("\n```\n\n");
                        
                        count++;
                    }
                    
                    writer.write("\n## 修复建议\n\n");
                    writer.write("1. 确保服务器端代码在处理每个请求时都进行正确的认证和授权检查\n");
                    writer.write("2. 采用一致的访问控制机制，并在所有API端点上实施\n");
                    writer.write("3. 使用安全框架来处理认证和授权，避免手动实现\n");
                    writer.write("4. 定期审查访问控制机制，确保它们正常工作\n");
                    
                    JOptionPane.showMessageDialog(mainPanel, "导出成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainPanel, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    stderr.println("导出到Markdown失败: " + e.getMessage());
                    e.printStackTrace(stderr);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            stderr.println("导出到Markdown失败: " + e.getMessage());
            e.printStackTrace(stderr);
        }
    }
    
    // 导出为TXT文件
    private void exportToTxt() {
        if (issues.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "没有可以导出的结果！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存TXT文件");
            fileChooser.setSelectedFile(new File("未授权访问漏洞报告_" + 
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
            
            int userChoice = fileChooser.showSaveDialog(mainPanel);
            
            if (userChoice == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                
                try (FileWriter writer = new FileWriter(outputFile)) {
                    writer.write("未授权访问漏洞检测报告\n");
                    writer.write("====================\n\n");
                    writer.write("生成时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n\n");
                    
                    // 所有URL列表 - 改进格式，增加请求方法
                    writer.write("所有漏洞URL:\n");
                    writer.write("===========\n\n");
                    for (int i = 0; i < issues.size(); i++) {
                        IHttpRequestResponse[] messages = issues.get(i).getHttpMessages();
                        String requestMethod = extractRequestMethod(messages[0].getRequest());
                        writer.write(String.format("%3d. [%s] %s\n", (i+1), requestMethod, issues.get(i).getUrl().toString()));
                    }
                    writer.write("\n");
                    
                    writer.write("发现的漏洞:\n");
                    writer.write("==========\n\n");
                    
                    int count = 1;
                    for (ScanIssue issue : issues) {
                        IHttpRequestResponse[] messages = issue.getHttpMessages();
                        String url = issue.getUrl().toString();
                        String requestMethod = extractRequestMethod(messages[0].getRequest());
                        
                        IResponseInfo originalResponseInfo = helpers.analyzeResponse(messages[0].getResponse());
                        IResponseInfo unauthorizedResponseInfo = helpers.analyzeResponse(messages[1].getResponse());
                        
                        int originalLength = messages[0].getResponse().length - originalResponseInfo.getBodyOffset();
                        int unauthorizedLength = messages[1].getResponse().length - unauthorizedResponseInfo.getBodyOffset();
                        
                        writer.write("编号: " + count++ + "\n");
                        writer.write("URL: " + url + "\n");
                        writer.write("请求方法: " + requestMethod + "\n");
                        writer.write("原始响应长度: " + originalLength + "\n");
                        writer.write("未授权响应长度: " + unauthorizedLength + "\n");
                        writer.write("漏洞等级: " + issue.getSeverity() + "\n");
                        writer.write("详情: " + issue.getIssueDetail().replace("\n", "\n  ") + "\n");
                        writer.write("----------\n\n");
                    }
                    
                    writer.write("\n修复建议:\n");
                    writer.write("========\n\n");
                    writer.write("1. 确保服务器端代码在处理每个请求时都进行正确的认证和授权检查\n");
                    writer.write("2. 采用一致的访问控制机制，并在所有API端点上实施\n");
                    writer.write("3. 使用安全框架来处理认证和授权，避免手动实现\n");
                    writer.write("4. 定期审查访问控制机制，确保它们正常工作\n");
                    
                    JOptionPane.showMessageDialog(mainPanel, "导出成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainPanel, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    stderr.println("导出到TXT失败: " + e.getMessage());
                    e.printStackTrace(stderr);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            stderr.println("导出到TXT失败: " + e.getMessage());
            e.printStackTrace(stderr);
        }
    }
    
    // 从请求中提取HTTP方法
    private String extractRequestMethod(byte[] request) {
        String requestString = helpers.bytesToString(request);
        if (requestString.startsWith("GET")) return "GET";
        if (requestString.startsWith("POST")) return "POST";
        if (requestString.startsWith("PUT")) return "PUT";
        if (requestString.startsWith("DELETE")) return "DELETE";
        if (requestString.startsWith("PATCH")) return "PATCH";
        if (requestString.startsWith("HEAD")) return "HEAD";
        if (requestString.startsWith("OPTIONS")) return "OPTIONS";
        if (requestString.startsWith("TRACE")) return "TRACE";
        return "未知";
    }
    
    // 导出为API JSON格式
    private void exportToApiJson() {
        if (issues.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "没有可以导出的未授权漏洞接口！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存API JSON文件");
            fileChooser.setSelectedFile(new File("api_" + 
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".json"));
            
            int userChoice = fileChooser.showSaveDialog(mainPanel);
            
            if (userChoice == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                
                try (FileWriter writer = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
                    writer.write("{\n");
                    
                    boolean first = true;
                    Set<String> processedUrls = new HashSet<>();
                    
                    for (ScanIssue issue : issues) {
                        IHttpRequestResponse[] messages = issue.getHttpMessages();
                        if (messages == null || messages.length == 0 || messages[0] == null || messages[0].getRequest() == null) {
                            continue;
                        }
                        
                        try {
                            // 使用原始请求（第一个消息）来导出API信息
                            IHttpRequestResponse originalRequest = messages[0];
                            IRequestInfo requestInfo = helpers.analyzeRequest(originalRequest);
                            String url = requestInfo.getUrl().toString();
                            String method = requestInfo.getMethod();
                            
                            // 避免重复导出相同的URL
                            if (processedUrls.contains(url)) {
                                continue;
                            }
                            processedUrls.add(url);
                            
                            if (!first) {
                                writer.write(",\n");
                            }
                            first = false;
                            
                            writer.write("  \"" + escapeJsonString(url) + "\": ");
                            
                            if ("GET".equals(method)) {
                                // GET请求只有键没有值
                                writer.write("null");
                            } else {
                                // 其他请求方法解析请求体
                                Map<String, Object> requestBody = parseRequestBody(originalRequest);
                                writer.write(mapToJsonString(requestBody));
                            }
                            
                        } catch (Exception e) {
                            stderr.println("处理漏洞请求时出错: " + e.getMessage());
                            e.printStackTrace(stderr);
                        }
                    }
                    
                    writer.write("\n}");
                    
                    JOptionPane.showMessageDialog(mainPanel, "未授权漏洞API接口导出成功！\n共导出 " + processedUrls.size() + " 个接口", 
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainPanel, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    stderr.println("导出API JSON失败: " + e.getMessage());
                    e.printStackTrace(stderr);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            stderr.println("导出API JSON失败: " + e.getMessage());
            e.printStackTrace(stderr);
        }
    }
    
    // 解析请求体参数
    private Map<String, Object> parseRequestBody(IHttpRequestResponse requestResponse) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            IRequestInfo requestInfo = helpers.analyzeRequest(requestResponse);
            byte[] request = requestResponse.getRequest();
            
            // 获取请求体
            int bodyOffset = requestInfo.getBodyOffset();
            if (bodyOffset < request.length) {
                String body = new String(request, bodyOffset, request.length - bodyOffset, StandardCharsets.UTF_8);
                
                // 获取Content-Type头
                String contentType = "";
                for (String header : requestInfo.getHeaders()) {
                    if (header.toLowerCase().startsWith("content-type:")) {
                        contentType = header.substring(13).trim().toLowerCase();
                        break;
                    }
                }
                
                // 根据Content-Type解析请求体
                if (contentType.contains("application/json")) {
                    // JSON格式
                    result.put("_json_body", body.trim());
                } else if (contentType.contains("application/x-www-form-urlencoded")) {
                    // 表单格式
                    parseFormData(body, result);
                } else if (contentType.contains("multipart/form-data")) {
                    // 多部分表单
                    result.put("_multipart_body", body.trim());
                } else if (!body.trim().isEmpty()) {
                    // 其他格式的请求体
                    result.put("_raw_body", body.trim());
                }
            }
            
            // 解析URL参数
            String url = requestInfo.getUrl().toString();
            if (url.contains("?")) {
                String queryString = url.substring(url.indexOf("?") + 1);
                parseFormData(queryString, result);
            }
            
        } catch (Exception e) {
            stderr.println("解析请求体时出错: " + e.getMessage());
            e.printStackTrace(stderr);
            result.put("_parse_error", e.getMessage());
        }
        
        return result;
    }
    
    // 解析表单数据
    private void parseFormData(String data, Map<String, Object> result) {
        try {
            if (data == null || data.trim().isEmpty()) {
                return;
            }
            
            String[] pairs = data.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length >= 1) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                    String value = keyValue.length > 1 ? 
                            URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name()) : "";
                    result.put(key, value);
                }
            }
        } catch (Exception e) {
            stderr.println("解析表单数据时出错: " + e.getMessage());
            e.printStackTrace(stderr);
        }
    }
    
    // 将Map转换为JSON字符串
    private String mapToJsonString(Map<String, Object> map) {
        if (map.isEmpty()) {
            return "null";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",\n");
            }
            first = false;
            
            sb.append("    \"").append(escapeJsonString(entry.getKey())).append("\": ");
            
            Object value = entry.getValue();
            if (value == null) {
                sb.append("null");
            } else if (value instanceof String) {
                sb.append("\"").append(escapeJsonString((String) value)).append("\"");
            } else {
                sb.append("\"").append(escapeJsonString(value.toString())).append("\"");
            }
        }
        
        sb.append("\n  }");
        return sb.toString();
    }
    
    // 转义JSON字符串
    private String escapeJsonString(String str) {
        if (str == null) {
            return "";
        }
        
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    // 实现IContextMenuFactory接口
    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        List<JMenuItem> menuItems = new ArrayList<>();

        // 在任何有HTTP请求响应的地方都显示菜单
        IHttpRequestResponse[] selectedMessages = invocation.getSelectedMessages();
        if (selectedMessages != null && selectedMessages.length > 0) {

            // 扩展主动扫描菜单项（遵循过滤逻辑）
            JMenuItem smartScanItem = new JMenuItem("智能主动扫描 (遵循过滤规则)");
            smartScanItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startSmartScanSelected(selectedMessages);
                }
            });
            menuItems.add(smartScanItem);

            // 强制扫描菜单项（忽略所有限制）
            JMenuItem forceScanItem = new JMenuItem("强制主动扫描 (忽略所有限制)");
            forceScanItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startBatchScanSelected(selectedMessages);
                }
            });
            menuItems.add(forceScanItem);

            // 如果正在扫描，添加停止扫描选项
            if (isBatchScanning) {
                menuItems.add(null); // 分隔符
                JMenuItem stopScanItem = new JMenuItem("停止批量扫描");
                stopScanItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        stopBatchScan();
                    }
                });
                menuItems.add(stopScanItem);
            }
        }

        return menuItems.isEmpty() ? null : menuItems;
    }

    /**
     * 清空所有结果和流量记录
     */
    private void clearResults() {
        if (issues.isEmpty() && allTraffic.isEmpty()) {
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(
            mainPanel,
            "确定要清空所有结果和流量记录吗？",
            "确认清空",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            issues.clear();
            allTraffic.clear();
            unauthorizedTraffic.clear();
            isVulnerableList.clear();
            filteredTraffic.clear();
            issuesModel.fireTableDataChanged();
            trafficTableModel.fireTableDataChanged();
            
            // 清空消息编辑器
            originalRequestViewer.setMessage(new byte[0], true);
            originalResponseViewer.setMessage(new byte[0], false);
            unauthorizedRequestViewer.setMessage(new byte[0], true);
            unauthorizedResponseViewer.setMessage(new byte[0], false);
            
            currentlyDisplayedItem = null;
            
            stdout.println("所有结果和流量记录已清空");
        }
    }
    
    // 新增：删除选中行
    private void deleteSelectedRows() {
        int[] viewRows = resultsTable.getSelectedRows();
        int[] selectedRows = new int[viewRows.length];
        for (int i = 0; i < viewRows.length; i++) {
            selectedRows[i] = resultsTable.convertRowIndexToModel(viewRows[i]);
        }
        if (selectedRows.length == 0) {
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(
            mainPanel,
            "确定要删除选中的 " + selectedRows.length + " 个项目吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // 从大到小删除，防止索引改变导致删错
            Arrays.sort(selectedRows);
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                issues.remove(selectedRows[i]);
            }
            
            issuesModel.fireTableDataChanged();
            
            // 如果删除后没有项目了，清空消息编辑器
            if (issues.isEmpty()) {
                originalRequestViewer.setMessage(new byte[0], true);
                originalResponseViewer.setMessage(new byte[0], false);
                unauthorizedRequestViewer.setMessage(new byte[0], true);
                unauthorizedResponseViewer.setMessage(new byte[0], false);
                currentlyDisplayedItem = null;
            }
        }
    }

    // 流量表格右键菜单
    private void showTrafficContextMenu(MouseEvent e, Component component) {
        if (component != trafficTable) {
            return;
        }
        
        JPopupMenu menu = new JPopupMenu();
        
        // 添加Burp工具菜单项
        int viewRow = trafficTable.getSelectedRow();
        int row = viewRow != -1 ? trafficTable.convertRowIndexToModel(viewRow) : -1;
        
        // 获取当前使用的流量列表（根据过滤状态）
        List<IHttpRequestResponse> trafficToUse = filterEnabled ? filteredTraffic : allTraffic;
        
        // 将所有选中的行索引从视图转换为模型索引，方便后续批量删除操作
        int[] viewRows = trafficTable.getSelectedRows();
        final int[] selectedModelRows = new int[viewRows.length];
        for (int i = 0; i < viewRows.length; i++) {
            selectedModelRows[i] = trafficTable.convertRowIndexToModel(viewRows[i]);
        }
        
        if (row != -1 && row < trafficToUse.size()) {
            IHttpRequestResponse message = trafficToUse.get(row);
            IHttpService service = message.getHttpService();
            byte[] request = message.getRequest();
            byte[] response = message.getResponse();
            
            menu.add(new JLabel("原始请求:"));
            addBurpToolsMenuItems(menu, service, request, response);
            
            // 添加未授权请求相关菜单
            int originalIndex = allTraffic.indexOf(message);
            if (originalIndex != -1 && originalIndex < unauthorizedTraffic.size() && unauthorizedTraffic.get(originalIndex) != null) {
                IHttpRequestResponse unauthorizedMessage = unauthorizedTraffic.get(originalIndex);
                menu.addSeparator();
                menu.add(new JLabel("未授权请求:"));
                addBurpToolsMenuItems(menu, unauthorizedMessage.getHttpService(), 
                                     unauthorizedMessage.getRequest(), 
                                     unauthorizedMessage.getResponse());
            }
            
            // 添加检测按钮
            menu.addSeparator();
            JMenuItem checkItem = new JMenuItem("检测未授权访问漏洞");
            // 如果是静态资源且过滤器开启，则禁用检测按钮
            boolean isStaticResource = filterEnabled && shouldFilter(message);
            checkItem.setEnabled(!isStaticResource);
            if (isStaticResource) {
                checkItem.setToolTipText("静态资源无需检测未授权访问");
            }
            
            checkItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 再次检查是否是静态资源，双重保险
                    if (filterEnabled && shouldFilter(message)) {
                        JOptionPane.showMessageDialog(
                            mainPanel,
                            "静态资源（JS/CSS/图片等）无需检测未授权访问",
                            "提示",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    
                    List<IScanIssue> foundIssues = checkForUnauthorizedAccess(message);
                    if (foundIssues != null && !foundIssues.isEmpty()) {
                        callbacks.addScanIssue(foundIssues.get(0));
                        JOptionPane.showMessageDialog(
                            mainPanel,
                            "发现未授权访问漏洞!",
                            "检测结果",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                            mainPanel,
                            "未发现未授权访问漏洞",
                            "检测结果",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    issuesModel.fireTableDataChanged();
                }
            });
            menu.add(checkItem);
            
            // 添加生成未授权请求按钮
            JMenuItem generateItem = new JMenuItem("生成/更新未授权请求");
            // 如果是静态资源且过滤器开启，则禁用生成按钮
            generateItem.setEnabled(!isStaticResource);
            if (isStaticResource) {
                generateItem.setToolTipText("静态资源无需生成未授权请求");
            }
            
            generateItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 再次检查是否是静态资源，双重保险
                    if (filterEnabled && shouldFilter(message)) {
                        JOptionPane.showMessageDialog(
                            mainPanel,
                            "静态资源（JS/CSS/图片等）无需生成未授权请求",
                            "提示",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }
                    
                    IHttpRequestResponse unauthorizedReq = createUnauthorizedRequest(message);
                    if (unauthorizedReq != null) {
                        // 获取消息在原始列表中的索引
                        int originalIndex = allTraffic.indexOf(message);
                        if (originalIndex == -1) {
                            // 如果未找到，尝试添加到列表末尾
                            originalIndex = allTraffic.size();
                            allTraffic.add(message);
                        }
                        
                        // 更新未授权请求
                        if (originalIndex < unauthorizedTraffic.size()) {
                            unauthorizedTraffic.set(originalIndex, unauthorizedReq);
                        } else {
                            while (unauthorizedTraffic.size() < originalIndex) {
                                unauthorizedTraffic.add(null);
                            }
                            unauthorizedTraffic.add(unauthorizedReq);
                        }
                        
                        // 更新显示
                        unauthorizedRequestViewer.setMessage(unauthorizedReq.getRequest(), true);
                        unauthorizedResponseViewer.setMessage(unauthorizedReq.getResponse(), false);
                        trafficTableModel.fireTableDataChanged();
                        
                        JOptionPane.showMessageDialog(
                            mainPanel,
                            "未授权请求已生成并更新!",
                            "成功",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            });
            menu.add(generateItem);
            
            // ---------- 强制加入未授权漏洞列表 ----------
            JMenuItem forceAddItem = new JMenuItem("强制加入未授权漏洞列表");
            forceAddItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    List<IHttpRequestResponse> trafficRef = filterEnabled ? filteredTraffic : allTraffic;
                    for (int modelRow : selectedModelRows) {
                        if (modelRow >= trafficRef.size()) continue;
                        IHttpRequestResponse origReq = trafficRef.get(modelRow);
                        int origIndex = allTraffic.indexOf(origReq);
                        if (origIndex == -1) continue;
                        IHttpRequestResponse unauthReq;
                        if (origIndex < unauthorizedTraffic.size() && unauthorizedTraffic.get(origIndex) != null) {
                            unauthReq = unauthorizedTraffic.get(origIndex);
                        } else {
                            unauthReq = createUnauthorizedRequest(origReq);
                            while (unauthorizedTraffic.size() <= origIndex) unauthorizedTraffic.add(null);
                            unauthorizedTraffic.set(origIndex, unauthReq);
                        }

                        while (isVulnerableList.size() <= origIndex) isVulnerableList.add(false);
                        isVulnerableList.set(origIndex, true);

                        try {
                            IRequestInfo reqInfo = helpers.analyzeRequest(origReq);
                            String detail = "状态: 手动添加";
                            ScanIssue issue = new ScanIssue(origReq.getHttpService(), reqInfo.getUrl(), new IHttpRequestResponse[]{origReq, unauthReq}, "未授权访问漏洞(手动)", detail, "High", "Firm");
                            addIssueWithAutoDedupe(issue);
                        } catch (Exception ex) {
                            stderr.println("手动添加漏洞失败: " + ex.getMessage());
                        }
                    }
                    trafficTableModel.fireTableDataChanged();
                    issuesModel.fireTableDataChanged();
                }
            });
            menu.add(forceAddItem);
            
            // 添加删除按钮（批量删除选中项）
            menu.addSeparator();
            JMenuItem deleteItem = new JMenuItem("从列表中删除选中项");
            deleteItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selectedModelRows.length == 0) return;
                    
                    // 用于确认对话框
                    int result = JOptionPane.showConfirmDialog(
                        mainPanel,
                        "确定要删除选中的 " + selectedModelRows.length + " 个流量记录吗？",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (result == JOptionPane.YES_OPTION) {
                        // 获取当前使用的流量列表
                        List<IHttpRequestResponse> currentTraffic = filterEnabled ? filteredTraffic : allTraffic;
                        
                        // 获取要删除的请求对象
                        Set<IHttpRequestResponse> toDelete = new HashSet<>();
                        for (int modelRow : selectedModelRows) {
                            if (modelRow < currentTraffic.size()) {
                                toDelete.add(currentTraffic.get(modelRow));
                            }
                        }
                        
                        // 从主列表和关联列表中删除
                        for (IHttpRequestResponse reqResp : toDelete) {
                            int origIndex = allTraffic.indexOf(reqResp);
                            if (origIndex != -1) {
                                allTraffic.remove(origIndex);
                                if (origIndex < unauthorizedTraffic.size()) {
                                    unauthorizedTraffic.remove(origIndex);
                                }
                                if (origIndex < isVulnerableList.size()) {
                                    isVulnerableList.remove(origIndex);
                                }
                            }
                        }
                        
                        // 如果过滤器启用，重新应用过滤
                        if (filterEnabled) {
                            applyFilter();
                        } else {
                        trafficTableModel.fireTableDataChanged();
                        }
                    }
                }
            });
            menu.add(deleteItem);
        }
        
        menu.show(component, e.getX(), e.getY());
    }

    // 流量表格模型（增强版，支持可配置列）
    private class TrafficTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            // 使用过滤后的流量集合
            return filterEnabled ? filteredTraffic.size() : allTraffic.size();
        }

        @Override
        public int getColumnCount() {
            return trafficColumnConfig.getVisibleColumnCount();
        }

        @Override
        public String getColumnName(int column) {
            String[] visibleColumns = trafficColumnConfig.getVisibleColumns();
            return column < visibleColumns.length ? visibleColumns[column] : "";
        }

        @Override
        public Class<?> getColumnClass(int column) {
            int actualColumn = trafficColumnConfig.mapVisibleToActual(column);
            switch (actualColumn) {
                case 0: return Integer.class; // #
                case 4: return Integer.class; // 状态码
                case 5: return Integer.class; // 原始长度
                case 6: return Integer.class; // 未授权长度
                case 7: return Integer.class; // 长度差异
                case 17: return java.util.Date.class; // 时间
                default: return String.class;
            }
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            // 使用过滤后的流量或原始流量
            List<IHttpRequestResponse> trafficToUse = filterEnabled ? filteredTraffic : allTraffic;

            if (rowIndex >= trafficToUse.size()) {
                return "";
            }

            IHttpRequestResponse message = trafficToUse.get(rowIndex);
            IRequestInfo requestInfo = helpers.analyzeRequest(message);

            // 找出当前消息在原始列表中的索引
            int originalIndex = allTraffic.indexOf(message);

            int actualColumn = trafficColumnConfig.mapVisibleToActual(columnIndex);

            switch (actualColumn) {
                case 0: // #
                    return rowIndex + 1;
                case 1: // Host
                    return requestInfo.getUrl().getHost();
                case 2: // 请求方法
                    return extractRequestMethod(message.getRequest());
                case 3: // URL
                    return requestInfo.getUrl().toString();
                case 4: // 状态码
                    if (message.getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(message.getResponse());
                        return responseInfo.getStatusCode();
                    }
                    return "N/A";
                case 5: // 原始长度
                    if (message.getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(message.getResponse());
                        return message.getResponse().length - responseInfo.getBodyOffset();
                    }
                    return "N/A";
                case 6: // 未授权长度
                    if (originalIndex != -1 && originalIndex < unauthorizedTraffic.size() &&
                        unauthorizedTraffic.get(originalIndex) != null &&
                        unauthorizedTraffic.get(originalIndex).getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(unauthorizedTraffic.get(originalIndex).getResponse());
                        return unauthorizedTraffic.get(originalIndex).getResponse().length - responseInfo.getBodyOffset();
                    } else {
                        return "N/A";
                    }
                case 7: // 长度差异
                    if (message.getResponse() != null &&
                        originalIndex != -1 &&
                        originalIndex < unauthorizedTraffic.size() &&
                        unauthorizedTraffic.get(originalIndex) != null &&
                        unauthorizedTraffic.get(originalIndex).getResponse() != null) {

                        IResponseInfo origInfo = helpers.analyzeResponse(message.getResponse());
                        IResponseInfo unauthInfo = helpers.analyzeResponse(unauthorizedTraffic.get(originalIndex).getResponse());

                        int origLen = message.getResponse().length - origInfo.getBodyOffset();
                        int unauthLen = unauthorizedTraffic.get(originalIndex).getResponse().length - unauthInfo.getBodyOffset();

                        return Math.abs(origLen - unauthLen);
                    } else {
                        return "N/A";
                    }
                case 8: // MIME类型
                    if (message.getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(message.getResponse());
                        return responseInfo.getInferredMimeType();
                    }
                    return "";
                case 9: // 参数
                    List<IParameter> params = requestInfo.getParameters();
                    return params.isEmpty() ? "" : String.valueOf(params.size());
                case 10: // 已编辑
                    return ""; // 暂时不支持
                case 11: // 扩展
                    String url = requestInfo.getUrl().toString();
                    int lastDot = url.lastIndexOf('.');
                    int lastSlash = url.lastIndexOf('/');
                    if (lastDot > lastSlash && lastDot < url.length() - 1) {
                        return url.substring(lastDot + 1);
                    }
                    return "";
                case 12: // 标题
                    return ""; // 暂时不支持
                case 13: // Notes
                    return ""; // 暂时不支持
                case 14: // TLS
                    return requestInfo.getUrl().getProtocol().equalsIgnoreCase("https") ? "Yes" : "";
                case 15: // IP
                    return ""; // 暂时不支持
                case 16: // Cookies
                    List<IParameter> cookies = requestInfo.getParameters();
                    long cookieCount = cookies.stream().filter(p -> p.getType() == IParameter.PARAM_COOKIE).count();
                    return cookieCount > 0 ? String.valueOf(cookieCount) : "";
                case 17: // 时间
                    // 尝试从请求中获取时间信息，如果没有则使用当前时间
                    return new java.util.Date();
                default:
                    return "";
            }
        }
    }

    /**
     * 记录请求响应流量
     */
    private void recordTraffic(IHttpRequestResponse requestResponse) {
        try {
            // 确保请求和响应都不为空
            if (requestResponse.getRequest() == null || requestResponse.getResponse() == null) {
                return;
            }
            
            // 分析请求
            IRequestInfo requestInfo = helpers.analyzeRequest(requestResponse);
            
            // 检查是否已存在相同URL的请求，避免重复
            String url = requestInfo.getUrl().toString();
            int existingIndex = -1;
            
            for (int i = 0; i < allTraffic.size(); i++) {
                IRequestInfo existingInfo = helpers.analyzeRequest(allTraffic.get(i));
                if (existingInfo.getUrl().toString().equals(url)) {
                    // 已存在，更新位置
                    existingIndex = i;
                    break;
                }
            }
            
            // 避免记录太多请求导致内存问题，限制最大记录数量
            if (allTraffic.size() > 1000 && existingIndex == -1) {
                allTraffic.remove(0); // 移除最旧的请求
                unauthorizedTraffic.remove(0); // 同时移除相应的未授权请求
                isVulnerableList.remove(0); // 同时移除相应的漏洞标记
            }
            
            // 检查是否为静态资源
            boolean isStaticResource = shouldFilter(requestResponse);
            
            // 处理新请求或更新已存在的请求
            if (existingIndex == -1) {
                // 添加新请求
                allTraffic.add(requestResponse);
                
                // 创建和记录未授权请求版本（仅对非静态资源或过滤未启用时）
                IHttpRequestResponse unauthorizedReq = null;
                if (!filterEnabled || !isStaticResource) {
                    unauthorizedReq = createUnauthorizedRequest(requestResponse);
                } else {
                    // 对于静态资源，创建一个空标记，但不实际发送请求
                    stdout.println("跳过对静态资源创建未授权请求: " + url);
                }
                unauthorizedTraffic.add(unauthorizedReq);
                
                // 判断是否存在漏洞并记录（仅对非静态资源或过滤未启用时）
                boolean isVulnerable = false;
                if (!filterEnabled || !isStaticResource) {
                    isVulnerable = checkVulnerability(requestResponse, unauthorizedReq);
                }
                isVulnerableList.add(isVulnerable);
                
                // 调试信息
                stdout.println("记录新流量: " + url + 
                               (isStaticResource ? " [静态资源]" : "") + 
                               (isVulnerable ? " [存在漏洞]" : ""));

                // 如果存在漏洞且未在issues表中，则自动添加
                if (isVulnerable) {
                    if (unauthorizedReq != null && unauthorizedReq.getResponse() != null) {
                        IResponseInfo originalResponseInfo = helpers.analyzeResponse(requestResponse.getResponse());
                        IResponseInfo unauthorizedResponseInfo = helpers.analyzeResponse(unauthorizedReq.getResponse());
                        int originalLength = requestResponse.getResponse().length - originalResponseInfo.getBodyOffset();
                        int unauthorizedLength = unauthorizedReq.getResponse().length - unauthorizedResponseInfo.getBodyOffset();
                        String detail = String.format(
                            "状态: %s\n原始状态码: %d\n未授权状态码: %d\n原始长度: %d\n未授权长度: %d\n差异: %d\n漏洞判断依据: %s",
                            "High",
                            originalResponseInfo.getStatusCode(),
                            unauthorizedResponseInfo.getStatusCode(),
                            originalLength,
                            unauthorizedLength,
                            Math.abs(originalLength - unauthorizedLength),
                            "移除认证头后，响应内容长度与原始请求基本相同，差异小于 " + responseLengthThreshold + " 字节"
                        );
                        ScanIssue issue = new ScanIssue(
                            requestResponse.getHttpService(),
                            requestInfo.getUrl(),
                            new IHttpRequestResponse[] { requestResponse, unauthorizedReq },
                            "未授权访问漏洞",
                            detail,
                            "High",
                            "Certain"
                        );
                        
                        // 使用统一的添加漏洞方法（包含自动去重）
                        addIssueWithAutoDedupe(issue);
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                issuesModel.fireTableDataChanged();
                            }
                        });
                    }
                }
            } else {
                // 更新已存在的请求
                allTraffic.set(existingIndex, requestResponse);
                
                // 更新未授权请求版本（仅对非静态资源或过滤未启用时）
                IHttpRequestResponse unauthorizedReq = null;
                if (!filterEnabled || !isStaticResource) {
                    unauthorizedReq = createUnauthorizedRequest(requestResponse);
                } else {
                    // 对于静态资源，保留原有的未授权请求（如果有）
                    if (existingIndex < unauthorizedTraffic.size()) {
                        unauthorizedReq = unauthorizedTraffic.get(existingIndex);
                    }
                    stdout.println("跳过对静态资源更新未授权请求: " + url);
                }
                
                if (existingIndex < unauthorizedTraffic.size()) {
                    unauthorizedTraffic.set(existingIndex, unauthorizedReq);
                } else {
                    while (unauthorizedTraffic.size() < existingIndex) {
                        unauthorizedTraffic.add(null);
                    }
                    unauthorizedTraffic.add(unauthorizedReq);
                }
                
                // 更新漏洞标记（仅对非静态资源或过滤未启用时）
                boolean isVulnerable = false;
                if (!filterEnabled || !isStaticResource) {
                    isVulnerable = checkVulnerability(requestResponse, unauthorizedReq);
                }
                
                if (existingIndex < isVulnerableList.size()) {
                    isVulnerableList.set(existingIndex, isVulnerable);
                } else {
                    while (isVulnerableList.size() < existingIndex) {
                        isVulnerableList.add(false);
                    }
                    isVulnerableList.add(isVulnerable);
                }
                
                stdout.println("更新已存在流量: " + url + 
                               (isStaticResource ? " [静态资源]" : "") + 
                               (isVulnerable ? " [存在漏洞]" : ""));
            }
            
            // 如果请求不应被过滤，则添加到过滤后列表
            if (!shouldFilter(requestResponse)) {
                // 检查是否已存在
                boolean alreadyInFiltered = filteredTraffic.contains(requestResponse);
                // 添加到过滤后列表或更新
                if (!alreadyInFiltered) {
                    filteredTraffic.add(requestResponse);
                } else {
                    int idx = filteredTraffic.indexOf(requestResponse);
                    if (idx != -1) {
                        filteredTraffic.set(idx, requestResponse);
                    }
                }
            } else {
                // 如果应该被过滤，确保从过滤后列表中移除
                filteredTraffic.remove(requestResponse);
            }
            
            // 更新UI
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    trafficTableModel.fireTableDataChanged();
                }
            });
        } catch (Exception e) {
            stderr.println("记录流量时出错: " + e.getMessage());
            e.printStackTrace(stderr);
        }
    }
    
    // 新增：检查是否存在未授权访问漏洞
    private boolean checkVulnerability(IHttpRequestResponse originalReq, IHttpRequestResponse unauthorizedReq) {
        try {
            if (originalReq == null || originalReq.getResponse() == null || 
                unauthorizedReq == null || unauthorizedReq.getResponse() == null) {
                return false;
            }
            
            IResponseInfo originalResponseInfo = helpers.analyzeResponse(originalReq.getResponse());
            IResponseInfo unauthorizedResponseInfo = helpers.analyzeResponse(unauthorizedReq.getResponse());
            
            int originalStatusCode = originalResponseInfo.getStatusCode();
            int unauthorizedStatusCode = unauthorizedResponseInfo.getStatusCode();
            
            int originalLength = originalReq.getResponse().length - originalResponseInfo.getBodyOffset();
            int unauthorizedLength = unauthorizedReq.getResponse().length - unauthorizedResponseInfo.getBodyOffset();
            
            // 新增：只在未授权响应为2xx且长度大于0时才判定
            if (originalLength == 0 || unauthorizedLength == 0) return false;
            if (!(unauthorizedStatusCode >= 200 && unauthorizedStatusCode < 300)) return false;
            if (unauthorizedStatusCode == 304 || unauthorizedStatusCode == 401 ||
                unauthorizedStatusCode == 403 || unauthorizedStatusCode == 404) return false;
            
            return Math.abs(originalLength - unauthorizedLength) < responseLengthThreshold;
        } catch (Exception e) {
            stderr.println("检查漏洞时出错: " + e.getMessage());
            e.printStackTrace(stderr);
            return false;
        }
    }

    // 创建未授权版本的请求并获取响应
    private IHttpRequestResponse createUnauthorizedRequest(IHttpRequestResponse originalReq) {
        try {
            // 确保原始请求不为空
            if (originalReq == null || originalReq.getRequest() == null) {
                stdout.println("[创建未授权请求] 错误：原始请求为空");
                return null;
            }

            // 分析原始请求
            IRequestInfo requestInfo = helpers.analyzeRequest(originalReq);
            List<String> originalHeaders = requestInfo.getHeaders();

            stdout.println("[创建未授权请求] 原始请求URL: " + requestInfo.getUrl());
            stdout.println("[创建未授权请求] 原始请求头数量: " + originalHeaders.size());
            
            // 输出原始请求的HTTP方法
            stdout.println("[创建未授权请求] 原始请求HTTP方法: " + requestInfo.getMethod());

            if (originalHeaders.isEmpty()) {
                stdout.println("[创建未授权请求] 错误：原始请求头列表为空，无法构建。 URL: " + requestInfo.getUrl());
                return null;
            }

            // 输出原始请求的所有头部(限制数量)
            if (stdout.checkError() == false) {
                for (int i = 0; i < Math.min(20, originalHeaders.size()); i++) {
                    stdout.println("[创建未授权请求] 原始头[" + i + "]: " + originalHeaders.get(i));
                }
            }

            // 创建没有认证头的新请求头列表
            List<String> newHeaders = new ArrayList<>();
            newHeaders.add(originalHeaders.get(0)); // 始终保留请求行

            boolean removedAnyHeader = false;
            // 从索引1开始处理实际的请求头
            for (int i = 1; i < originalHeaders.size(); i++) {
                String header = originalHeaders.get(i);
                boolean skip = false;
                for (String authHeader : headersToRemove) {
                    if (header.toLowerCase().startsWith(authHeader.toLowerCase() + ":")) {
                        skip = true;
                        removedAnyHeader = true;
                        stdout.println("[创建未授权请求] 删除认证头: " + header + " (URL: " + requestInfo.getUrl() + ")");
                        break;
                    }
                }
                if (!skip) {
                    newHeaders.add(header);
                }
            }

            if (!removedAnyHeader) {
                stdout.println("[创建未授权请求] 警告：没有找到任何配置中需要删除的认证头。 URL: " + requestInfo.getUrl());
            }

            // 输出新构建的请求头
            stdout.println("[创建未授权请求] 新构建的请求头数量: " + newHeaders.size());
            if (stdout.checkError() == false) {
                for (int i = 0; i < Math.min(20, newHeaders.size()); i++) {
                    stdout.println("[创建未授权请求] 新头[" + i + "]: " + newHeaders.get(i));
                }
            }

            // 提取请求体信息
            int bodyOffset = requestInfo.getBodyOffset();
            byte[] originalRequestBytes = originalReq.getRequest();
            stdout.println("[创建未授权请求] 请求体偏移量: " + bodyOffset + ", 原始请求总长度: " + originalRequestBytes.length);

            // 确保请求体提取正确
            byte[] bodyBytes;
            if (bodyOffset < originalRequestBytes.length) {
                bodyBytes = Arrays.copyOfRange(originalRequestBytes, bodyOffset, originalRequestBytes.length);
                stdout.println("[创建未授权请求] 提取请求体长度: " + bodyBytes.length + " 字节");
            } else {
                bodyBytes = new byte[0];
                stdout.println("[创建未授权请求] 无请求体或请求体为空，使用空字节数组");
            }

            // 检查请求体前几个字节
            if (bodyBytes.length > 0) {
                try {
                    int previewLength = Math.min(50, bodyBytes.length);
                    String bodyPreview = new String(bodyBytes, 0, previewLength, "UTF-8");
                    // 替换不可打印字符为点，避免控制台输出混乱
                    bodyPreview = bodyPreview.replaceAll("[\\p{C}]", ".");
                    stdout.println("[创建未授权请求] 请求体预览: " + bodyPreview + (bodyBytes.length > previewLength ? "..." : ""));
                } catch (Exception e) {
                    stdout.println("[创建未授权请求] 请求体预览失败: " + e.getMessage());
                }
            }

            // 构建新请求
            byte[] newRequest = helpers.buildHttpMessage(newHeaders, bodyBytes);

            if (newRequest == null || newRequest.length == 0) {
                stdout.println("[创建未授权请求] 错误：构建的新未授权请求为空或无效。 URL: " + requestInfo.getUrl());
                return null;
            }

            stdout.println("[创建未授权请求] 新请求大小: " + newRequest.length + " 字节");

            // 验证新请求的正确性
            try {
                IRequestInfo newReqInfo = helpers.analyzeRequest(newRequest);
                stdout.println("[创建未授权请求] 新请求方法: " + newReqInfo.getMethod() + ", URL: " + newReqInfo.getUrl());
            } catch (Exception e) {
                stdout.println("[创建未授权请求] 警告 - 无法解析新构建的请求: " + e.getMessage());
                // 继续尝试发送请求
            }

            // 确保HTTP服务不为空
            if (originalReq.getHttpService() == null) {
                stdout.println("[创建未授权请求] 错误：HTTP服务为空");
                return null;
            }

            // 获取HTTP服务详情，以便后续日志记录和问题排查
            IHttpService service = originalReq.getHttpService();
            String targetHost = service.getHost();
            int targetPort = service.getPort();
            String targetProtocol = service.getProtocol();
            
            stdout.println("[创建未授权请求] 正在发送请求到: " + 
                        targetProtocol + "://" + targetHost + ":" + targetPort);
            
            // 尝试发送请求，包含重试逻辑
            IHttpRequestResponse newReqResp = null;
            int maxRetries = 3;
            int retryCount = 0;
            boolean success = false;
            
            while (!success && retryCount < maxRetries) {
                try {
                    stdout.println("[创建未授权请求] 尝试 #" + (retryCount + 1) + " 发送请求...");
                    newReqResp = callbacks.makeHttpRequest(service, newRequest);
                    
                    if (newReqResp == null) {
                        stdout.println("[创建未授权请求] 警告: makeHttpRequest返回null，重试...");
                        retryCount++;
                        continue;
                    }
                    
                    if (newReqResp.getResponse() == null) {
                        stdout.println("[创建未授权请求] 警告: 未收到响应，重试...");
                        retryCount++;
                        continue;
                    }
                    
                    // 获取到有效响应
                    IResponseInfo responseInfo = helpers.analyzeResponse(newReqResp.getResponse());
                    stdout.println("[创建未授权请求] 成功收到响应: 状态码=" + responseInfo.getStatusCode() + 
                                ", 长度=" + newReqResp.getResponse().length + " 字节");
                    success = true;
                } catch (Exception e) {
                    retryCount++;
                    stdout.println("[创建未授权请求] 发送请求异常 #" + retryCount + ": " + e.getMessage());
                    if (retryCount >= maxRetries) {
                        stdout.println("[创建未授权请求] 达到最大重试次数，放弃...");
                        e.printStackTrace(stdout);
                    } else {
                        try {
                            // 短暂等待后重试
                            Thread.sleep(500);
                        } catch (InterruptedException ie) {
                            // 忽略中断
                        }
                    }
                }
            }
            
            // 如果所有尝试都失败，但我们仍需要返回一个请求/响应对象以供界面显示
            if (!success || newReqResp == null || newReqResp.getResponse() == null) {
                stdout.println("[创建未授权请求] 所有尝试发送请求失败，创建模拟响应对象");
                // 创建一个简单的模拟响应，并标记为失败，而不是返回null
                return createMockResponseObject(service, newRequest, "尝试发送未授权请求失败");
            }
            
            return newReqResp;
            
        } catch (Exception e) {
            stdout.println("[创建未授权请求] 处理请求时出错: " + e.getMessage());
            e.printStackTrace(stdout);
            
            // 创建一个模拟的响应对象，显示错误信息
            try {
                if (originalReq != null && originalReq.getHttpService() != null && originalReq.getRequest() != null) {
                    IRequestInfo requestInfo = helpers.analyzeRequest(originalReq);
                    return createMockResponseObject(
                        originalReq.getHttpService(), 
                        originalReq.getRequest(), 
                        "处理请求时发生错误: " + e.getMessage()
                    );
                }
            } catch (Exception ex) {
                stdout.println("[创建未授权请求] 创建错误响应对象失败: " + ex.getMessage());
            }
            
            return null;
        }
    }
    
    /**
     * 创建一个模拟的HTTP响应对象，用于UI显示和调试
     */
    private IHttpRequestResponse createMockResponseObject(IHttpService service, byte[] request, String errorMessage) {
        // 创建一个简单的错误响应
        String respText = "HTTP/1.1 503 Service Unavailable\r\n" +
                        "Content-Type: text/plain; charset=UTF-8\r\n" +
                        "X-Error: Unauthorized-Detector-Error\r\n" +
                        "Server: Unauthorized-Detector-Mock\r\n" +
                        "Content-Length: " + errorMessage.length() + "\r\n\r\n" +
                        errorMessage;
        
        final byte[] responseBytes = helpers.stringToBytes(respText);
        
        // 创建一个自定义的HTTP请求响应对象
        return new IHttpRequestResponse() {
            @Override
            public byte[] getRequest() {
                return request;
            }
            
            @Override
            public void setRequest(byte[] message) {
                // 不做任何事
            }
            
            @Override
            public byte[] getResponse() {
                return responseBytes;
            }
            
            @Override
            public void setResponse(byte[] message) {
                // 不做任何事
            }
            
            @Override
            public String getComment() {
                return "[模拟响应] " + errorMessage;
            }
            
            @Override
            public void setComment(String comment) {
                // 不做任何事
            }
            
            @Override
            public String getHighlight() {
                return "red";
            }
            
            @Override
            public void setHighlight(String color) {
                // 不做任何事
            }
            
            @Override
            public IHttpService getHttpService() {
                return service;
            }
            
            @Override
            public void setHttpService(IHttpService httpService) {
                // 不做任何事
            }
        };
    }

    // 实现IProxyListener接口
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        // 只处理响应
        if (!messageIsRequest && enabledCheckBox != null && enabledCheckBox.isSelected()) {
            recordTraffic(message.getMessageInfo());
        }
    }
    
    // 实现IHttpListener接口
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        // 只处理响应且不处理我们自己发起的请求（避免无限循环）
        if (!messageIsRequest && toolFlag != IBurpExtenderCallbacks.TOOL_EXTENDER && enabledCheckBox != null && enabledCheckBox.isSelected()) {
            // 即使是静态资源也记录流量，但不进行未授权测试
            // 这样可以在UI中展示所有流量，但不会发送不必要的请求
            recordTraffic(messageInfo);
        }
    }
    
    /**
     * 判断请求是否应该被过滤掉
     * @param requestResponse 请求响应对象
     * @return 如果应该被过滤则返回true，否则返回false
     */
    private boolean shouldFilter(IHttpRequestResponse requestResponse) {
        // 如果过滤功能未启用，则不过滤
        if (!filterEnabled) {
            return false;
        }

        try {
            // 提取URL字符串
            IRequestInfo requestInfo = helpers.analyzeRequest(requestResponse);
            String url = requestInfo.getUrl().toString();
            
            // 提取MIME类型
            String mimeType = "";
            if (requestResponse.getResponse() != null) {
                IResponseInfo responseInfo = helpers.analyzeResponse(requestResponse.getResponse());
                mimeType = responseInfo.getStatedMimeType().toLowerCase();
            }
            
            // 判断是否为静态资源（基于URL和MIME类型）
            
            // 检查图片
            if (filterImages && (IMAGE_PATTERN.matcher(url).find() || 
                                mimeType.contains("image"))) {
                return true;
            }
            
            // 检查CSS
            if (filterCSS && (CSS_PATTERN.matcher(url).find() || 
                             mimeType.contains("css") || 
                             mimeType.contains("style"))) {
                return true;
            }
            
            // 检查JavaScript
            if (filterJS && (JS_PATTERN.matcher(url).find() || 
                            mimeType.contains("javascript") || 
                            mimeType.contains("script"))) {
                return true;
            }
            
            // 检查字体
            if (filterFonts && (FONT_PATTERN.matcher(url).find() || 
                               mimeType.contains("font"))) {
                return true;
            }
            
            // 检查其他静态资源
            if (filterStatic && STATIC_PATTERN.matcher(url).find()) {
                return true;
            }
            
            // 检查自定义过滤条件
            if (compiledCustomPattern != null && compiledCustomPattern.matcher(url).find()) {
                return true;
            }
            
            // 已经明确是某些静态资源的MIME类型
            Set<String> staticMimeTypes = new HashSet<>(Arrays.asList(
                "image", "font", "css", "javascript", "text/plain", "audio", "video"
            ));
            
            for (String staticType : staticMimeTypes) {
                if (mimeType.contains(staticType)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 出错时不过滤
            stderr.println("过滤请求时出错: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 应用过滤器并更新流量显示
     */
    private void applyFilter() {
        filteredTraffic.clear();
        
        // 从所有流量中筛选不需要过滤的请求
        for (IHttpRequestResponse traffic : allTraffic) {
            if (!shouldFilter(traffic)) {
                filteredTraffic.add(traffic);
            }
        }
        
        // 更新过滤状态标签
        final String statusText = String.format("过滤状态: 原始流量 %d 条，显示 %d 条%s", 
                allTraffic.size(), 
                filteredTraffic.size(),
                allTraffic.size() > 0 ? String.format(" (%.1f%%)", (float)filteredTraffic.size() / allTraffic.size() * 100) : "");
        
        // 更新表格和标签
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                trafficTableModel.fireTableDataChanged();
                if (filterStatusLabel != null) {
                    filterStatusLabel.setText(statusText);
                }
            }
        });
        
        stdout.println("过滤器已应用，" + statusText);
    }
    
    /**
     * 更新自定义过滤器正则表达式
     */
    private void updateCustomFilterPattern() {
        try {
            if (customFilterPattern != null && !customFilterPattern.trim().isEmpty()) {
                compiledCustomPattern = Pattern.compile(customFilterPattern, Pattern.CASE_INSENSITIVE);
            } else {
                compiledCustomPattern = null;
            }
        } catch (Exception e) {
            stderr.println("编译自定义过滤正则表达式时出错: " + e.getMessage());
            compiledCustomPattern = null;
        }
    }
    
    /**
     * 加载保存的设置
     */
    private void loadSettings() {
        try {
            // 加载复选框状态，如果没有保存的设置则使用默认值
            String enabledSetting = callbacks.loadExtensionSetting(SETTING_ENABLED);
            loadedEnabledState = enabledSetting == null ? true : Boolean.parseBoolean(enabledSetting);
            
            String activeScanSetting = callbacks.loadExtensionSetting(SETTING_ACTIVE_SCAN);
            loadedActiveScanState = activeScanSetting == null ? true : Boolean.parseBoolean(activeScanSetting);
            
            String passiveScanSetting = callbacks.loadExtensionSetting(SETTING_PASSIVE_SCAN);
            loadedPassiveScanState = passiveScanSetting == null ? true : Boolean.parseBoolean(passiveScanSetting);
            
            String filterEnabledSetting = callbacks.loadExtensionSetting(SETTING_FILTER_ENABLED);
            filterEnabled = filterEnabledSetting == null ? true : Boolean.parseBoolean(filterEnabledSetting);
            
            // 加载过滤器选项
            String filterImagesSetting = callbacks.loadExtensionSetting(SETTING_FILTER_IMAGES);
            filterImages = filterImagesSetting == null ? true : Boolean.parseBoolean(filterImagesSetting);
            
            String filterCssSetting = callbacks.loadExtensionSetting(SETTING_FILTER_CSS);
            filterCSS = filterCssSetting == null ? true : Boolean.parseBoolean(filterCssSetting);
            
            String filterJsSetting = callbacks.loadExtensionSetting(SETTING_FILTER_JS);
            filterJS = filterJsSetting == null ? true : Boolean.parseBoolean(filterJsSetting);
            
            String filterFontsSetting = callbacks.loadExtensionSetting(SETTING_FILTER_FONTS);
            filterFonts = filterFontsSetting == null ? true : Boolean.parseBoolean(filterFontsSetting);
            
            String filterStaticSetting = callbacks.loadExtensionSetting(SETTING_FILTER_STATIC);
            filterStatic = filterStaticSetting == null ? true : Boolean.parseBoolean(filterStaticSetting);
            
            // 加载自定义过滤器
            String customFilterSetting = callbacks.loadExtensionSetting(SETTING_CUSTOM_FILTER);
            customFilterPattern = customFilterSetting == null ? "" : customFilterSetting;
            
            // 加载认证头设置
            String headersSetting = callbacks.loadExtensionSetting(SETTING_HEADERS_TO_REMOVE);
            loadedHeadersText = headersSetting == null ? "Authorization\nCookie\nX-Auth-Token\nJWT-Token" : headersSetting;
            
            // 加载阈值
            String thresholdSetting = callbacks.loadExtensionSetting(SETTING_THRESHOLD);
            if (thresholdSetting != null) {
                try {
                    responseLengthThreshold = Integer.parseInt(thresholdSetting);
                } catch (NumberFormatException ignore) {}
            }

            // 加载去重设置
            String autoDedupeSettings = callbacks.loadExtensionSetting(SETTING_AUTO_DEDUPE);
            enableAutoDedupe = autoDedupeSettings == null ? true : Boolean.parseBoolean(autoDedupeSettings);

            String queryParamDedupeSettings = callbacks.loadExtensionSetting(SETTING_QUERY_PARAM_DEDUPE);
            enableQueryParamDedupe = queryParamDedupeSettings == null ? true : Boolean.parseBoolean(queryParamDedupeSettings);

            String dedupeIgnoreParamsSetting = callbacks.loadExtensionSetting(SETTING_DEDUPE_IGNORE_PARAMS);
            dedupeIgnoreParams = dedupeIgnoreParamsSetting == null ? "timestamp,date,time,_t,_,random,nonce,csrf_token" : dedupeIgnoreParamsSetting;

            stdout.println("设置加载完成: 启用插件=" + loadedEnabledState + ", 主动扫描=" + loadedActiveScanState +
                          ", 被动扫描=" + loadedPassiveScanState + ", 启用过滤=" + filterEnabled +
                          ", 自动去重=" + enableAutoDedupe + ", 参数去重=" + enableQueryParamDedupe);
                          
        } catch (Exception e) {
            stderr.println("加载设置时出错: " + e.getMessage());
            e.printStackTrace(stderr);
        }
    }
    
    /**
     * 保存当前设置
     */
    private void saveSettings() {
        try {
            // 保存复选框状态
            if (enabledCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_ENABLED, String.valueOf(enabledCheckBox.isSelected()));
            }
            if (activeCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_ACTIVE_SCAN, String.valueOf(activeCheckBox.isSelected()));
            }
            if (passiveScanCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_PASSIVE_SCAN, String.valueOf(passiveScanCheckBox.isSelected()));
            }
            if (filterEnabledCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_FILTER_ENABLED, String.valueOf(filterEnabledCheckBox.isSelected()));
            }
            
            // 保存过滤器选项
            if (filterImagesCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_FILTER_IMAGES, String.valueOf(filterImagesCheckBox.isSelected()));
            }
            if (filterCSSCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_FILTER_CSS, String.valueOf(filterCSSCheckBox.isSelected()));
            }
            if (filterJSCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_FILTER_JS, String.valueOf(filterJSCheckBox.isSelected()));
            }
            if (filterFontsCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_FILTER_FONTS, String.valueOf(filterFontsCheckBox.isSelected()));
            }
            if (filterStaticCheckBox != null) {
                callbacks.saveExtensionSetting(SETTING_FILTER_STATIC, String.valueOf(filterStaticCheckBox.isSelected()));
            }
            
            // 保存自定义过滤器
            if (customFilterTextField != null) {
                callbacks.saveExtensionSetting(SETTING_CUSTOM_FILTER, customFilterTextField.getText());
            }
            
            // 保存认证头设置
            if (headersTextArea != null) {
                callbacks.saveExtensionSetting(SETTING_HEADERS_TO_REMOVE, headersTextArea.getText());
            }
            
            // 保存阈值
            callbacks.saveExtensionSetting(SETTING_THRESHOLD, String.valueOf(responseLengthThreshold));

            // 保存去重设置
            callbacks.saveExtensionSetting(SETTING_AUTO_DEDUPE, String.valueOf(enableAutoDedupe));
            callbacks.saveExtensionSetting(SETTING_QUERY_PARAM_DEDUPE, String.valueOf(enableQueryParamDedupe));
            if (dedupeIgnoreParamsField != null) {
                callbacks.saveExtensionSetting(SETTING_DEDUPE_IGNORE_PARAMS, dedupeIgnoreParamsField.getText());
            } else {
                callbacks.saveExtensionSetting(SETTING_DEDUPE_IGNORE_PARAMS, dedupeIgnoreParams);
            }

        } catch (Exception e) {
            stderr.println("保存设置时出错: " + e.getMessage());
            e.printStackTrace(stderr);
        }
    }

    // ---------- 新增：重新评估历史流量，基于当前阈值 ----------
    private void reEvaluateVulnerabilities() {
        try {
            issues.clear();
            for (int i = 0; i < allTraffic.size(); i++) {
                boolean isVul = false;
                if (i < unauthorizedTraffic.size() && unauthorizedTraffic.get(i) != null) {
                    isVul = checkVulnerability(allTraffic.get(i), unauthorizedTraffic.get(i));
                }
                if (i < isVulnerableList.size()) {
                    isVulnerableList.set(i, isVul);
                } else {
                    isVulnerableList.add(isVul);
                }

                if (isVul) {
                    // 创建新的漏洞条目
                    IHttpRequestResponse orig = allTraffic.get(i);
                    IHttpRequestResponse unauth = unauthorizedTraffic.get(i);
                    if (unauth == null) continue;
                    IRequestInfo reqInfo = helpers.analyzeRequest(orig);
                    IResponseInfo origRespInfo = helpers.analyzeResponse(orig.getResponse());
                    IResponseInfo unauthRespInfo = helpers.analyzeResponse(unauth.getResponse());
                    int origLen = orig.getResponse().length - origRespInfo.getBodyOffset();
                    int unauthLen = unauth.getResponse().length - unauthRespInfo.getBodyOffset();
                    String detail = String.format("状态: %s\n原始状态码: %d\n未授权状态码: %d\n原始长度: %d\n未授权长度: %d\n差异: %d", "高风险", origRespInfo.getStatusCode(), unauthRespInfo.getStatusCode(), origLen, unauthLen, Math.abs(origLen - unauthLen));
                    ScanIssue issue = new ScanIssue(orig.getHttpService(), reqInfo.getUrl(), new IHttpRequestResponse[]{orig, unauth}, "未授权访问漏洞", detail, "High", "Certain");
                    addIssueWithAutoDedupe(issue);
                }
            }
        } catch (Exception ex) {
            stderr.println("重新评估漏洞时出错: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                trafficTableModel.fireTableDataChanged();
                issuesModel.fireTableDataChanged();
            }
        });
    }

    // ---------- 新增：导出所有未授权请求 ----------
    private void exportUnauthorizedRequests() {
        if (issues.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "当前没有检测到未授权漏洞！", "无数据", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("保存未授权请求");
            chooser.setSelectedFile(new File("unauthorized_requests_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
            int userChoice = chooser.showSaveDialog(mainPanel);
            if (userChoice != JFileChooser.APPROVE_OPTION) return;

            File out = chooser.getSelectedFile();
            // 直接写入原始字节，避免因错误解码导致中文乱码
            try (FileOutputStream fos = new FileOutputStream(out)) {
                byte[] separator = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);
                for (ScanIssue issue : issues) {
                    IHttpRequestResponse[] msgs = issue.getHttpMessages();
                    if (msgs == null || msgs.length < 2 || msgs[1] == null) continue; // 防御
                    byte[] reqBytes = msgs[1].getRequest();
                    if (reqBytes != null) {
                        fos.write(reqBytes);
                        fos.write(separator); // 以空行分隔下一个请求
                    }
                }
            }

            JOptionPane.showMessageDialog(mainPanel, "导出成功！", "完成", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainPanel, "导出失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            stderr.println("导出未授权请求失败: " + ex.getMessage());
            ex.printStackTrace(stderr);
        }
    }

    // 新增：开始批量扫描所有历史记录
    private void startBatchScanAllHistory() {
        if (isBatchScanning) {
            JOptionPane.showMessageDialog(mainPanel, "批量扫描正在进行中，请等待完成或停止当前扫描", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (allTraffic.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "没有历史流量记录可以扫描", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
            mainPanel,
            "确定要对所有 " + allTraffic.size() + " 条历史记录进行扩展主动扫描吗？\n" +
            "此操作将忽略重复检测逻辑，重新扫描所有流量。\n" +
            "扫描过程在后台进行，不会影响其他操作。",
            "确认批量扫描",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            startBatchScanSelected(allTraffic.toArray(new IHttpRequestResponse[0]));
        }
    }

    // 新增：开始批量扫描选中的记录（强制模式，忽略所有限制）
    private void startBatchScanSelected(IHttpRequestResponse[] selectedMessages) {
        if (isBatchScanning) {
            JOptionPane.showMessageDialog(mainPanel, "批量扫描正在进行中，请等待完成或停止当前扫描", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedMessages == null || selectedMessages.length == 0) {
            JOptionPane.showMessageDialog(mainPanel, "没有选中的记录可以扫描", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 强制模式：不过滤任何内容，扫描所有选中的记录
        List<IHttpRequestResponse> toScan = new ArrayList<>();
        for (IHttpRequestResponse message : selectedMessages) {
            if (message != null && message.getRequest() != null) {
                toScan.add(message);
            }
        }

        if (toScan.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "没有有效的记录可以扫描", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 显示强制扫描确认信息
        String message = String.format(
            "强制扫描将处理 %d 条记录\n" +
            "此模式将忽略所有过滤规则和限制\n" +
            "包括静态资源也会被扫描\n" +
            "是否继续？",
            toScan.size()
        );

        int result = JOptionPane.showConfirmDialog(mainPanel, message, "确认强制扫描", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            startBatchScanInternal(toScan, "强制扫描");
        }
    }

    // 新增：执行批量扫描
    private void performBatchScan(List<IHttpRequestResponse> toScan) {
        try {
            stdout.println("批量扫描开始，总计 " + toScan.size() + " 条记录");

            for (int i = 0; i < toScan.size() && !shouldStopBatchScan; i++) {
                IHttpRequestResponse message = toScan.get(i);
                batchScanProgress = i + 1;

                try {
                    // 更新进度信息
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (filterStatusLabel != null) {
                                filterStatusLabel.setText(String.format("批量扫描进度: %d/%d (%d%%)",
                                    batchScanProgress, batchScanTotal,
                                    (batchScanProgress * 100) / batchScanTotal));
                            }
                        }
                    });

                    // 记录流量（如果不在列表中）
                    if (!allTraffic.contains(message)) {
                        recordTraffic(message);
                    }

                    // 执行未授权检测（忽略重复检测逻辑）
                    List<IScanIssue> foundIssues = checkForUnauthorizedAccessForced(message);
                    if (foundIssues != null && !foundIssues.isEmpty()) {
                        // 添加到Burp的扫描结果中
                        for (IScanIssue issue : foundIssues) {
                            callbacks.addScanIssue(issue);
                        }
                        stdout.println("批量扫描发现漏洞: " + helpers.analyzeRequest(message).getUrl());
                    }

                    // 短暂延迟，避免过于频繁的请求
                    Thread.sleep(100);

                } catch (Exception e) {
                    stderr.println("批量扫描单个请求时出错: " + e.getMessage());
                    // 继续扫描下一个
                }
            }

        } catch (Exception e) {
            stderr.println("批量扫描过程中出错: " + e.getMessage());
            e.printStackTrace(stderr);
        } finally {
            // 扫描完成
            isBatchScanning = false;
            shouldStopBatchScan = false;

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // 更新UI
                    issuesModel.fireTableDataChanged();
                    trafficTableModel.fireTableDataChanged();
                    applyFilter(); // 刷新过滤状态

                    String message = shouldStopBatchScan ?
                        "批量扫描已停止，已完成 " + batchScanProgress + "/" + batchScanTotal + " 条记录" :
                        "批量扫描完成，共扫描 " + batchScanTotal + " 条记录";

                    stdout.println(message);
                    JOptionPane.showMessageDialog(mainPanel, message, "扫描完成", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    // 新增：停止批量扫描
    private void stopBatchScan() {
        if (!isBatchScanning) {
            JOptionPane.showMessageDialog(mainPanel, "当前没有正在进行的批量扫描", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        shouldStopBatchScan = true;
        stdout.println("正在停止批量扫描...");

        // 等待扫描线程结束
        if (batchScanThread != null && batchScanThread.isAlive()) {
            try {
                batchScanThread.interrupt();
            } catch (Exception e) {
                stderr.println("停止扫描线程时出错: " + e.getMessage());
            }
        }
    }

    // 新增：强制检测未授权访问（忽略重复检测逻辑）
    private List<IScanIssue> checkForUnauthorizedAccessForced(IHttpRequestResponse baseRequestResponse) {
        try {
            // 获取原始请求和响应
            byte[] originalRequest = baseRequestResponse.getRequest();
            byte[] originalResponse = baseRequestResponse.getResponse();

            if (originalRequest == null) {
                return null;
            }

            if (originalResponse == null) {
                return null;
            }

            IRequestInfo requestInfo = helpers.analyzeRequest(baseRequestResponse);
            List<String> originalHeaders = requestInfo.getHeaders();

            if (originalHeaders.isEmpty()) {
                return null;
            }

            // 创建一个新的请求，删除所有认证头
            List<String> newHeaders = new ArrayList<>();
            newHeaders.add(originalHeaders.get(0)); // 始终保留请求行

            boolean removedAnyAuthHeader = false;
            // 从索引1开始处理实际的请求头
            for (int i = 1; i < originalHeaders.size(); i++) {
                String header = originalHeaders.get(i);
                boolean skip = false;
                for (String authHeader : headersToRemove) {
                    if (header.toLowerCase().startsWith(authHeader.toLowerCase() + ":")) {
                        skip = true;
                        removedAnyAuthHeader = true;
                        break;
                    }
                }
                if (!skip) {
                    newHeaders.add(header);
                }
            }

            // 提取请求体
            int bodyOffset = requestInfo.getBodyOffset();
            byte[] bodyBytes;
            if (bodyOffset < originalRequest.length) {
                bodyBytes = Arrays.copyOfRange(originalRequest, bodyOffset, originalRequest.length);
            } else {
                bodyBytes = new byte[0];
            }

            // 构建未授权请求
            byte[] newRequest = helpers.buildHttpMessage(newHeaders, bodyBytes);

            if (newRequest == null || newRequest.length == 0) {
                return null;
            }

            // 发送没有认证头的请求
            IHttpRequestResponse unauthorizedRequestResponse = null;
            try {
                unauthorizedRequestResponse = callbacks.makeHttpRequest(
                        baseRequestResponse.getHttpService(), newRequest);

                if (unauthorizedRequestResponse == null || unauthorizedRequestResponse.getResponse() == null) {
                    return null;
                }
            } catch (Exception e) {
                stderr.println("强制检测时发送未授权请求失败: " + e.getMessage());
                return null;
            }

            byte[] unauthorizedResponse = unauthorizedRequestResponse.getResponse();

            // 分析响应
            IResponseInfo originalResponseInfo = helpers.analyzeResponse(originalResponse);
            IResponseInfo unauthorizedResponseInfo = helpers.analyzeResponse(unauthorizedResponse);

            int originalStatusCode = originalResponseInfo.getStatusCode();
            int unauthorizedStatusCode = unauthorizedResponseInfo.getStatusCode();

            int originalLength = originalResponse.length - originalResponseInfo.getBodyOffset();
            int unauthorizedLength = unauthorizedResponse.length - unauthorizedResponseInfo.getBodyOffset();

            // 检查是否存在漏洞
            boolean isVulnerable = false;
            String reason = "";

            // 基本检查条件
            if (originalLength > 0 && unauthorizedLength > 0 &&
                unauthorizedStatusCode >= 200 && unauthorizedStatusCode < 300 &&
                unauthorizedStatusCode != 204 && unauthorizedStatusCode != 304 &&
                unauthorizedStatusCode != 401 && unauthorizedStatusCode != 403 &&
                unauthorizedStatusCode != 404) {

                if (Math.abs(originalLength - unauthorizedLength) < responseLengthThreshold) {
                    isVulnerable = true;
                    reason = "移除认证头后，响应内容长度与原始请求基本相同，差异小于 " + responseLengthThreshold + " 字节";
                }
            }

            if (isVulnerable) {
                String detail = String.format(
                        "状态: %s\n原始状态码: %d\n未授权状态码: %d\n原始长度: %d\n未授权长度: %d\n差异: %d\n漏洞判断依据: %s",
                        "High",
                        originalStatusCode,
                        unauthorizedStatusCode,
                        originalLength,
                        unauthorizedLength,
                        Math.abs(originalLength - unauthorizedLength),
                        reason
                );

                ScanIssue issue = new ScanIssue(
                        baseRequestResponse.getHttpService(),
                        requestInfo.getUrl(),
                        new IHttpRequestResponse[] { baseRequestResponse, unauthorizedRequestResponse },
                        "未授权访问漏洞(批量扫描)",
                        detail,
                        "High",
                        "Certain"
                );

                // 使用统一的添加漏洞方法（包含自动去重）
                addIssueWithAutoDedupe(issue);

                // 同时记录到流量列表
                recordTrafficForced(baseRequestResponse, unauthorizedRequestResponse, true);

                List<IScanIssue> reportIssues = new ArrayList<>();
                reportIssues.add(issue);
                return reportIssues;
            } else {
                // 即使没有漏洞也记录流量
                recordTrafficForced(baseRequestResponse, unauthorizedRequestResponse, false);
            }

        } catch (Exception e) {
            stderr.println("强制检测未授权访问时出错: " + e.getMessage());
            e.printStackTrace(stderr);
        }

        return null;
    }

    // 新增：强制记录流量（用于批量扫描）
    private void recordTrafficForced(IHttpRequestResponse originalMessage, IHttpRequestResponse unauthorizedMessage, boolean isVulnerable) {
        synchronized (allTraffic) {
            // 查找是否已存在
            int existingIndex = allTraffic.indexOf(originalMessage);

            if (existingIndex == -1) {
                // 不存在，添加新记录
                allTraffic.add(originalMessage);
                unauthorizedTraffic.add(unauthorizedMessage);
                isVulnerableList.add(isVulnerable);
            } else {
                // 已存在，更新记录
                if (existingIndex < unauthorizedTraffic.size()) {
                    unauthorizedTraffic.set(existingIndex, unauthorizedMessage);
                } else {
                    while (unauthorizedTraffic.size() <= existingIndex) {
                        unauthorizedTraffic.add(null);
                    }
                    unauthorizedTraffic.set(existingIndex, unauthorizedMessage);
                }

                if (existingIndex < isVulnerableList.size()) {
                    isVulnerableList.set(existingIndex, isVulnerable);
                } else {
                    while (isVulnerableList.size() <= existingIndex) {
                        isVulnerableList.add(false);
                    }
                    isVulnerableList.set(existingIndex, isVulnerable);
                }
            }
        }

        // 如果过滤器启用，重新应用过滤
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (filterEnabled) {
                    applyFilter();
                } else {
                    trafficTableModel.fireTableDataChanged();
                }
            }
        });
    }

    // 新增：智能扫描方法（遵循过滤逻辑）
    private void startSmartScanSelected(IHttpRequestResponse[] selectedMessages) {
        if (isBatchScanning) {
            JOptionPane.showMessageDialog(mainPanel, "批量扫描正在进行中，请等待完成或停止当前扫描", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedMessages == null || selectedMessages.length == 0) {
            JOptionPane.showMessageDialog(mainPanel, "没有选中的记录可以扫描", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 应用过滤逻辑，过滤掉静态资源
        List<IHttpRequestResponse> toScan = new ArrayList<>();
        int filteredCount = 0;

        for (IHttpRequestResponse message : selectedMessages) {
            if (message != null && message.getRequest() != null) {
                // 检查是否应该过滤
                if (filterEnabled && shouldFilter(message)) {
                    filteredCount++;
                    stdout.println("智能扫描跳过静态资源: " + helpers.analyzeRequest(message).getUrl());
                    continue;
                }
                toScan.add(message);
            }
        }

        if (toScan.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel,
                "所有选中的记录都被过滤器排除\n" +
                "过滤的记录数: " + filteredCount + "\n" +
                "请检查过滤器设置或使用'强制主动扫描'",
                "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 显示扫描信息
        String message = String.format(
            "智能扫描将处理 %d 条记录\n" +
            "已过滤静态资源: %d 条\n" +
            "扫描将遵循当前的过滤规则\n" +
            "是否继续？",
            toScan.size(), filteredCount
        );

        int result = JOptionPane.showConfirmDialog(mainPanel, message, "确认智能扫描", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            startBatchScanInternal(toScan, "智能扫描");
        }
    }

    // 新增：统一的批量扫描内部方法
    private void startBatchScanInternal(List<IHttpRequestResponse> toScan, String scanType) {
        // 启动后台扫描线程
        isBatchScanning = true;
        shouldStopBatchScan = false;
        batchScanProgress = 0;
        batchScanTotal = toScan.size();

        batchScanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                performBatchScan(toScan);
            }
        });
        batchScanThread.setDaemon(true);
        batchScanThread.start();

        stdout.println("开始" + scanType + " " + toScan.size() + " 条记录...");
        JOptionPane.showMessageDialog(mainPanel,
            scanType + "已开始，共 " + toScan.size() + " 条记录\n" +
            "扫描在后台进行，您可以继续其他操作\n" +
            "可随时点击'停止扫描'按钮中止扫描",
            "扫描开始",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // 新增：手动去重方法
    private void performManualDedupe() {
        if (issues.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "没有漏洞记录需要去重", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int originalCount = issues.size();
        performDedupe();
        int newCount = issues.size();
        int removedCount = originalCount - newCount;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                issuesModel.fireTableDataChanged();
            }
        });

        String message = String.format(
            "去重完成！\n" +
            "原始漏洞数: %d\n" +
            "去重后漏洞数: %d\n" +
            "移除重复项: %d",
            originalCount, newCount, removedCount
        );

        JOptionPane.showMessageDialog(mainPanel, message, "去重结果", JOptionPane.INFORMATION_MESSAGE);
        stdout.println("手动去重完成: 移除 " + removedCount + " 个重复项");
    }

    // 新增：执行去重逻辑
    private void performDedupe() {
        if (issues.isEmpty()) {
            return;
        }

        Set<String> seenUrls = new HashSet<>();
        List<ScanIssue> deduplicatedIssues = new ArrayList<>();

        for (ScanIssue issue : issues) {
            String url = issue.getUrl().toString();
            String normalizedUrl = normalizeUrlForDedupe(url);

            if (!seenUrls.contains(normalizedUrl)) {
                seenUrls.add(normalizedUrl);
                deduplicatedIssues.add(issue);
            } else {
                stdout.println("去重移除重复URL: " + url + " (标准化: " + normalizedUrl + ")");
            }
        }

        issues.clear();
        issues.addAll(deduplicatedIssues);
    }

    // 新增：URL标准化方法（用于去重）
    private String normalizeUrlForDedupe(String url) {
        if (!enableQueryParamDedupe) {
            return url;
        }

        try {
            // 分离URL和查询参数
            int queryIndex = url.indexOf('?');
            if (queryIndex == -1) {
                return url; // 没有查询参数
            }

            String baseUrl = url.substring(0, queryIndex);
            String queryString = url.substring(queryIndex + 1);

            // 解析查询参数
            String[] params = queryString.split("&");
            List<String> filteredParams = new ArrayList<>();

            // 获取忽略参数列表
            Set<String> ignoreParams = new HashSet<>();
            if (dedupeIgnoreParams != null && !dedupeIgnoreParams.trim().isEmpty()) {
                String[] ignoreArray = dedupeIgnoreParams.split(",");
                for (String param : ignoreArray) {
                    ignoreParams.add(param.trim().toLowerCase());
                }
            }

            // 过滤参数
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length > 0) {
                    String key = keyValue[0].trim().toLowerCase();
                    if (!ignoreParams.contains(key)) {
                        filteredParams.add(param);
                    }
                }
            }

            // 重新构建URL
            if (filteredParams.isEmpty()) {
                return baseUrl;
            } else {
                return baseUrl + "?" + String.join("&", filteredParams);
            }

        } catch (Exception e) {
            stderr.println("URL标准化失败: " + url + " - " + e.getMessage());
            return url; // 失败时返回原始URL
        }
    }

    // 新增：统一的添加漏洞方法（包含自动去重）
    private void addIssueWithAutoDedupe(ScanIssue newIssue) {
        synchronized (issues) {
            String newUrl = newIssue.getUrl().toString();
            String normalizedNewUrl = normalizeUrlForDedupe(newUrl);

            // 检查是否已存在相同URL的漏洞
            boolean duplicateFound = false;
            for (int i = 0; i < issues.size(); i++) {
                ScanIssue existingIssue = issues.get(i);
                String existingUrl = existingIssue.getUrl().toString();

                // 如果启用自动去重，使用标准化URL比较
                String compareUrl = enableAutoDedupe ? normalizeUrlForDedupe(existingUrl) : existingUrl;
                String newCompareUrl = enableAutoDedupe ? normalizedNewUrl : newUrl;

                if (compareUrl.equals(newCompareUrl)) {
                    // 发现重复，更新现有漏洞
                    issues.set(i, newIssue);
                    duplicateFound = true;
                    if (enableAutoDedupe) {
                        stdout.println("自动去重更新漏洞: " + newUrl + " (标准化: " + normalizedNewUrl + ")");
                    } else {
                        stdout.println("更新已存在的未授权漏洞: " + newUrl);
                    }
                    break;
                }
            }

            // 如果没有找到重复的，则添加新漏洞
            if (!duplicateFound) {
                issues.add(newIssue);
                if (enableAutoDedupe) {
                    stdout.println("自动去重添加新漏洞: " + newUrl + " (标准化: " + normalizedNewUrl + ")");
                } else {
                    stdout.println("添加新的未授权漏洞: " + newUrl);
                }
            }
        }
    }

    // 新增：列配置管理器
    private class ColumnConfigManager {
        private final String[] allColumns;
        private final boolean[] visibleColumns;
        private final String settingKey;

        public ColumnConfigManager(String[] columns, String settingKey) {
            this.allColumns = columns.clone();
            this.visibleColumns = new boolean[columns.length];
            this.settingKey = settingKey;

            // 默认显示所有列
            Arrays.fill(visibleColumns, true);

            // 加载保存的配置
            loadColumnConfig();
        }

        public void setDefaultVisibleColumns(boolean[] defaultVisible) {
            if (defaultVisible.length == visibleColumns.length) {
                // 只有在没有保存配置时才使用默认配置
                String config = callbacks.loadExtensionSetting(settingKey);
                if (config == null || config.isEmpty()) {
                    System.arraycopy(defaultVisible, 0, visibleColumns, 0, defaultVisible.length);
                }
            }
        }

        public String[] getAllColumns() {
            return allColumns.clone();
        }

        public String[] getVisibleColumns() {
            List<String> visible = new ArrayList<>();
            for (int i = 0; i < allColumns.length; i++) {
                if (visibleColumns[i]) {
                    visible.add(allColumns[i]);
                }
            }
            return visible.toArray(new String[0]);
        }

        public boolean[] getVisibleColumnFlags() {
            return visibleColumns.clone();
        }

        public void setColumnVisible(int index, boolean visible) {
            if (index >= 0 && index < visibleColumns.length) {
                visibleColumns[index] = visible;
                saveColumnConfig();
            }
        }

        public boolean isColumnVisible(int index) {
            return index >= 0 && index < visibleColumns.length && visibleColumns[index];
        }

        public int getVisibleColumnCount() {
            int count = 0;
            for (boolean visible : visibleColumns) {
                if (visible) count++;
            }
            return count;
        }

        public int mapVisibleToActual(int visibleIndex) {
            int actualIndex = 0;
            int visibleCount = 0;

            for (int i = 0; i < visibleColumns.length; i++) {
                if (visibleColumns[i]) {
                    if (visibleCount == visibleIndex) {
                        return i;
                    }
                    visibleCount++;
                }
            }
            return -1;
        }

        public int mapActualToVisible(int actualIndex) {
            if (!isColumnVisible(actualIndex)) {
                return -1;
            }

            int visibleIndex = 0;
            for (int i = 0; i < actualIndex; i++) {
                if (visibleColumns[i]) {
                    visibleIndex++;
                }
            }
            return visibleIndex;
        }

        private void loadColumnConfig() {
            String config = callbacks.loadExtensionSetting(settingKey);
            if (config != null && !config.isEmpty()) {
                String[] parts = config.split(",");
                for (int i = 0; i < Math.min(parts.length, visibleColumns.length); i++) {
                    visibleColumns[i] = Boolean.parseBoolean(parts[i]);
                }
            }
        }

        private void saveColumnConfig() {
            StringBuilder config = new StringBuilder();
            for (int i = 0; i < visibleColumns.length; i++) {
                if (i > 0) config.append(",");
                config.append(visibleColumns[i]);
            }
            callbacks.saveExtensionSetting(settingKey, config.toString());
        }

        public void showColumnConfigDialog(Component parent, Runnable onConfigChanged) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "列设置", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(400, 500);
            dialog.setLocationRelativeTo(parent);

            JPanel panel = new JPanel(new BorderLayout());

            // 标题
            JLabel titleLabel = new JLabel("选择要显示的列:", JLabel.CENTER);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(titleLabel, BorderLayout.NORTH);

            // 列选择区域
            JPanel checkboxPanel = new JPanel();
            checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
            checkboxPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            JCheckBox[] checkboxes = new JCheckBox[allColumns.length];
            for (int i = 0; i < allColumns.length; i++) {
                checkboxes[i] = new JCheckBox(allColumns[i], visibleColumns[i]);
                checkboxes[i].setAlignmentX(Component.LEFT_ALIGNMENT);
                checkboxPanel.add(checkboxes[i]);
                checkboxPanel.add(Box.createVerticalStrut(5));
            }

            JScrollPane scrollPane = new JScrollPane(checkboxPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            panel.add(scrollPane, BorderLayout.CENTER);

            // 按钮区域
            JPanel buttonPanel = new JPanel(new FlowLayout());

            JButton selectAllButton = new JButton("全选");
            selectAllButton.addActionListener(e -> {
                for (JCheckBox cb : checkboxes) {
                    cb.setSelected(true);
                }
            });

            JButton selectNoneButton = new JButton("全不选");
            selectNoneButton.addActionListener(e -> {
                for (JCheckBox cb : checkboxes) {
                    cb.setSelected(false);
                }
            });

            JButton defaultButton = new JButton("默认设置");
            defaultButton.addActionListener(e -> {
                for (JCheckBox cb : checkboxes) {
                    cb.setSelected(true);
                }
            });

            JButton confirmButton = new JButton("确定");
            confirmButton.addActionListener(e -> {
                // 检查至少选择一列
                boolean hasSelected = false;
                for (JCheckBox cb : checkboxes) {
                    if (cb.isSelected()) {
                        hasSelected = true;
                        break;
                    }
                }

                if (!hasSelected) {
                    JOptionPane.showMessageDialog(dialog, "至少需要选择一列！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 保存配置
                for (int i = 0; i < checkboxes.length; i++) {
                    visibleColumns[i] = checkboxes[i].isSelected();
                }
                saveColumnConfig();

                // 通知配置变更
                if (onConfigChanged != null) {
                    onConfigChanged.run();
                }

                dialog.dispose();
            });

            JButton cancelButton = new JButton("取消");
            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(selectAllButton);
            buttonPanel.add(selectNoneButton);
            buttonPanel.add(defaultButton);
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.setVisible(true);
        }
    }

    // 新增：初始化列配置管理器
    private void initializeColumnConfigs() {
        // 未授权漏洞表格列配置 - 按用户要求的顺序和默认显示
        String[] issuesColumns = { "#", "Host", "请求方法", "URL", "状态码", "原始长度", "未授权长度", "长度差异", "MIME类型", "参数", "已编辑", "扩展", "标题", "Notes", "TLS", "IP", "Cookies", "时间" };
        issuesColumnConfig = new ColumnConfigManager(issuesColumns, SETTING_ISSUES_COLUMNS);
        // 设置默认显示的列：#, Host, 请求方法, URL, 状态码, 原始长度, 未授权长度, 长度差异, MIME类型, 时间
        issuesColumnConfig.setDefaultVisibleColumns(new boolean[]{true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, true});

        // 所有流量表格列配置 - 按用户要求的顺序和默认显示
        String[] trafficColumns = { "#", "Host", "请求方法", "URL", "状态码", "原始长度", "未授权长度", "长度差异", "MIME类型", "参数", "已编辑", "扩展", "标题", "Notes", "TLS", "IP", "Cookies", "时间" };
        trafficColumnConfig = new ColumnConfigManager(trafficColumns, SETTING_TRAFFIC_COLUMNS);
        // 设置默认显示的列：#, Host, 请求方法, URL, 状态码, 原始长度, 未授权长度, 长度差异, MIME类型, 时间
        trafficColumnConfig.setDefaultVisibleColumns(new boolean[]{true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, true});
    }

    // 新增：显示列配置菜单
    private void showColumnConfigMenu(MouseEvent e, JTable table, ColumnConfigManager columnConfig, String title, Runnable onConfigChanged) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem configItem = new JMenuItem("列设置...");
        configItem.addActionListener(event -> {
            columnConfig.showColumnConfigDialog(table, onConfigChanged);
        });
        menu.add(configItem);

        menu.show(table.getTableHeader(), e.getX(), e.getY());
    }

    // 新增：为表格添加键盘导航支持
    private void addKeyboardNavigationSupport(JTable table, boolean isIssuesTable) {
        // 添加键盘监听器
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int selectedRow = table.getSelectedRow();
                int rowCount = table.getRowCount();

                if (rowCount == 0) return;

                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP:
                        if (selectedRow > 0) {
                            table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
                            table.scrollRectToVisible(table.getCellRect(selectedRow - 1, 0, true));
                            updateMessageViewers(table, selectedRow - 1, isIssuesTable);
                        }
                        e.consume(); // 防止默认行为
                        break;

                    case java.awt.event.KeyEvent.VK_DOWN:
                        if (selectedRow < rowCount - 1) {
                            int newRow = selectedRow == -1 ? 0 : selectedRow + 1;
                            table.setRowSelectionInterval(newRow, newRow);
                            table.scrollRectToVisible(table.getCellRect(newRow, 0, true));
                            updateMessageViewers(table, newRow, isIssuesTable);
                        }
                        e.consume(); // 防止默认行为
                        break;

                    case java.awt.event.KeyEvent.VK_HOME:
                        if (rowCount > 0) {
                            table.setRowSelectionInterval(0, 0);
                            table.scrollRectToVisible(table.getCellRect(0, 0, true));
                            updateMessageViewers(table, 0, isIssuesTable);
                        }
                        e.consume();
                        break;

                    case java.awt.event.KeyEvent.VK_END:
                        if (rowCount > 0) {
                            table.setRowSelectionInterval(rowCount - 1, rowCount - 1);
                            table.scrollRectToVisible(table.getCellRect(rowCount - 1, 0, true));
                            updateMessageViewers(table, rowCount - 1, isIssuesTable);
                        }
                        e.consume();
                        break;
                }
            }
        });

        // 确保表格可以获得焦点
        table.setFocusable(true);
        table.requestFocusInWindow();
    }

    // 新增：更新消息查看器
    private void updateMessageViewers(JTable table, int viewRow, boolean isIssuesTable) {
        if (viewRow == -1) return;

        int row = table.convertRowIndexToModel(viewRow);
        if (row == -1) return;

        try {
            if (isIssuesTable) {
                // 未授权漏洞表格
                if (row < issues.size()) {
                    ScanIssue issue = issues.get(row);
                    IHttpRequestResponse[] messages = issue.getHttpMessages();

                    if (messages.length >= 2) {
                        // 显示原始请求和响应
                        originalRequestViewer.setMessage(messages[0].getRequest(), true);
                        originalResponseViewer.setMessage(messages[0].getResponse(), false);

                        // 显示未授权请求和响应
                        unauthorizedRequestViewer.setMessage(messages[1].getRequest(), true);
                        unauthorizedResponseViewer.setMessage(messages[1].getResponse(), false);
                    }
                }
            } else {
                // 所有流量表格
                List<IHttpRequestResponse> trafficToUse = filterEnabled ? filteredTraffic : allTraffic;

                if (row < trafficToUse.size()) {
                    IHttpRequestResponse message = trafficToUse.get(row);

                    // 显示原始请求和响应
                    originalRequestViewer.setMessage(message.getRequest(), true);
                    originalResponseViewer.setMessage(message.getResponse(), false);

                    // 查找对应的未授权请求
                    int originalIndex = allTraffic.indexOf(message);
                    if (originalIndex != -1 && originalIndex < unauthorizedTraffic.size() &&
                        unauthorizedTraffic.get(originalIndex) != null) {
                        IHttpRequestResponse unauthorizedMessage = unauthorizedTraffic.get(originalIndex);
                        unauthorizedRequestViewer.setMessage(unauthorizedMessage.getRequest(), true);
                        unauthorizedResponseViewer.setMessage(unauthorizedMessage.getResponse(), false);
                    } else {
                        // 清空未授权查看器
                        unauthorizedRequestViewer.setMessage(new byte[0], true);
                        unauthorizedResponseViewer.setMessage(new byte[0], false);
                    }
                }
            }
        } catch (Exception e) {
            stderr.println("更新消息查看器时出错: " + e.getMessage());
        }
    }
}