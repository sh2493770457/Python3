package burp;

import java.awt.Component;
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
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
    private JTabbedPane requestResponseViewer;  // 新增：请求和响应查看器
    private IMessageEditor originalRequestViewer;  // 新增：原始请求查看器
    private IMessageEditor originalResponseViewer;  // 新增：原始响应查看器
    private IMessageEditor unauthorizedRequestViewer;  // 新增：未授权请求查看器
    private IMessageEditor unauthorizedResponseViewer;  // 新增：未授权响应查看器
    private JButton exportButton;  // 新增：导出按钮
    
    // 新增：检测阈值设置
    private JTextField thresholdField;
    private int responseLengthThreshold = 50; // 默认50字节的差异阈值
    
    // 新增：所有流量表格和模型
    private JTable trafficTable;
    private TrafficTableModel trafficTableModel;
    
    // 存储检测到的问题
    private List<ScanIssue> issues = new ArrayList<>();
    
    // 新增：存储所有经过的数据包及其未授权版本
    private List<IHttpRequestResponse> allTraffic = new ArrayList<>();
    private List<IHttpRequestResponse> unauthorizedTraffic = new ArrayList<>();
    private List<Boolean> isVulnerableList = new ArrayList<>();  // 新增：标记是否存在漏洞
    
    // 需要删除的认证头列表
    private List<String> headersToRemove = new ArrayList<>();
    
    // 当前选中的问题
    private ScanIssue currentlyDisplayedItem;  // 新增：当前显示的问题

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        
        // 设置扩展名称
        callbacks.setExtensionName("Unauthorized Scan");
        
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
        
        stdout.println("Unauthorized Scan加载成功!");
    }
    
    private void initializeUI() {
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("配置"));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        configPanel.add(new JLabel("需要删除的认证头 (每行一个):"), gbc);
        
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        headersTextArea = new JTextArea();
        headersTextArea.setText("Authorization\nCookie\nX-Auth-Token\nJWT-Token");
        JScrollPane headersScrollPane = new JScrollPane(headersTextArea);
        configPanel.add(headersScrollPane, gbc);
        
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        // 创建水平面板放置按钮和复选框
        JPanel controlPanel = new JPanel();
        JButton updateButton = new JButton("更新认证头列表");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateHeadersList();
            }
        });
        controlPanel.add(updateButton);
        
        // 新增：添加导出按钮
        exportButton = new JButton("导出为MD文档");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToMarkdown();
            }
        });
        controlPanel.add(exportButton);
        
        // 新增：添加导出TXT按钮
        JButton exportTxtButton = new JButton("导出为TXT");
        exportTxtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToTxt();
            }
        });
        controlPanel.add(exportTxtButton);
        
        // 新增：添加清空结果按钮
        JButton clearButton = new JButton("清空结果");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });
        controlPanel.add(clearButton);
        
        configPanel.add(controlPanel, gbc);
        
        gbc.gridy = 3;
        JPanel thresholdPanel = new JPanel(new GridBagLayout());
        GridBagConstraints thresholdGbc = new GridBagConstraints();
        thresholdGbc.gridx = 0;
        thresholdGbc.gridy = 0;
        thresholdGbc.anchor = GridBagConstraints.WEST;
        thresholdGbc.insets = new Insets(0, 0, 5, 5);
        
        thresholdPanel.add(new JLabel("响应长度差异阈值(字节):"), thresholdGbc);
        
        thresholdGbc.gridx = 1;
        thresholdField = new JTextField("50", 4);
        thresholdField.setToolTipText("原始响应与未授权响应的长度差异阈值，小于此值视为潜在漏洞，默认50字节");
        thresholdPanel.add(thresholdField, thresholdGbc);
        
        JButton applyThresholdBtn = new JButton("应用");
        applyThresholdBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int newThreshold = Integer.parseInt(thresholdField.getText().trim());
                    if (newThreshold >= 0) {
                        responseLengthThreshold = newThreshold;
                        stdout.println("设置未授权检测响应长度差异阈值为: " + responseLengthThreshold + " 字节");
                        JOptionPane.showMessageDialog(mainPanel, "设置已更新！新阈值: " + responseLengthThreshold + " 字节", "成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "阈值必须是非负整数", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        thresholdGbc.gridx = 2;
        thresholdPanel.add(applyThresholdBtn, thresholdGbc);
        
        configPanel.add(thresholdPanel, gbc);
        
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 5, 5, 5);
        JPanel checkboxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cbGbc = new GridBagConstraints();
        cbGbc.gridx = 0;
        cbGbc.gridy = 0;
        cbGbc.anchor = GridBagConstraints.WEST;
        cbGbc.insets = new Insets(0, 0, 5, 10);
        
        // 新增：全局启用开关
        enabledCheckBox = new JCheckBox("启用插件");
        enabledCheckBox.setSelected(true);
        enabledCheckBox.setToolTipText("全局开关，控制插件是否处理请求");
        checkboxPanel.add(enabledCheckBox, cbGbc);
        
        cbGbc.gridx = 1;
        activeCheckBox = new JCheckBox("启用主动扫描");
        activeCheckBox.setSelected(true);
        checkboxPanel.add(activeCheckBox, cbGbc);
        
        cbGbc.gridx = 2;
        passiveScanCheckBox = new JCheckBox("启用被动扫描");
        passiveScanCheckBox.setSelected(true);
        checkboxPanel.add(passiveScanCheckBox, cbGbc);
        
        configPanel.add(checkboxPanel, gbc);
        
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
        
        // 新增：所有流量表格
        trafficTableModel = new TrafficTableModel();
        trafficTable = new JTable(trafficTableModel);
        JScrollPane trafficScrollPane = new JScrollPane(trafficTable);
        
        // 新增：添加流量表格排序功能
        TableRowSorter<TableModel> trafficSorter = new TableRowSorter<>(trafficTableModel);
        trafficTable.setRowSorter(trafficSorter);
        
        // 添加流量控制面板
        JPanel trafficControlPanel = new JPanel();
        JButton refreshButton = new JButton("刷新流量");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trafficTableModel.fireTableDataChanged();
                stdout.println("手动刷新流量表格，当前记录: " + allTraffic.size() + " 条");
            }
        });
        trafficControlPanel.add(refreshButton);
        
        // 创建流量面板
        JPanel trafficPanel = new JPanel(new GridBagLayout());
        GridBagConstraints trafficGbc = new GridBagConstraints();
        trafficGbc.gridx = 0;
        trafficGbc.gridy = 0;
        trafficGbc.weightx = 1.0;
        trafficGbc.fill = GridBagConstraints.HORIZONTAL;
        trafficPanel.add(trafficControlPanel, trafficGbc);
        
        trafficGbc.gridy = 1;
        trafficGbc.weighty = 1.0;
        trafficGbc.fill = GridBagConstraints.BOTH;
        trafficPanel.add(trafficScrollPane, trafficGbc);
        
        // 启用流量表格多选
        trafficTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // 添加到选项卡
        resultsTabbedPane.addTab("未授权漏洞", issuesScrollPane);
        resultsTabbedPane.addTab("所有流量", trafficPanel);
        
        // 初始化消息查看器
        requestResponseViewer = new JTabbedPane();
        // 创建消息编辑器并设置它们为只读模式
        MessageEditorController editorController = new MessageEditorController();
        originalRequestViewer = callbacks.createMessageEditor(editorController, false);
        originalResponseViewer = callbacks.createMessageEditor(editorController, false);
        unauthorizedRequestViewer = callbacks.createMessageEditor(editorController, false);
        unauthorizedResponseViewer = callbacks.createMessageEditor(editorController, false);
        
        requestResponseViewer.addTab("原始请求", originalRequestViewer.getComponent());
        requestResponseViewer.addTab("原始响应", originalResponseViewer.getComponent());
        requestResponseViewer.addTab("未授权请求", unauthorizedRequestViewer.getComponent());
        requestResponseViewer.addTab("未授权响应", unauthorizedResponseViewer.getComponent());
        
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
        
        // 新增：添加流量表格选择监听器
        trafficTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = trafficTable.getSelectedRow();
                int row = viewRow != -1 ? trafficTable.convertRowIndexToModel(viewRow) : -1;
                if (row != -1 && row < allTraffic.size()) {
                    IHttpRequestResponse message = allTraffic.get(row);
                    
                    // 显示原始请求和响应
                    originalRequestViewer.setMessage(message.getRequest(), true);
                    originalResponseViewer.setMessage(message.getResponse(), false);
                    
                    // 显示未授权请求和响应（如果存在）
                    if (row < unauthorizedTraffic.size() && unauthorizedTraffic.get(row) != null) {
                        IHttpRequestResponse unauthorizedMessage = unauthorizedTraffic.get(row);
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
        resultsSplitPane.setResizeWeight(0.3);
        
        // 将配置面板和结果分割面板添加到主分割面板
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, configPanel, resultsSplitPane);
        mainSplitPane.setResizeWeight(0.2);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(mainSplitPane, gbc);
        
        // 初始化认证头列表
        updateHeadersList();
        
        // 将面板添加到Burp的UI
        callbacks.customizeUiComponent(mainPanel);
        callbacks.addSuiteTab(this);
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
        
        // 更新阈值设置
        try {
            int newThreshold = Integer.parseInt(thresholdField.getText().trim());
            if (newThreshold >= 0) {
                responseLengthThreshold = newThreshold;
                stdout.println("更新响应长度差异阈值: " + responseLengthThreshold + " 字节");
            } else {
                thresholdField.setText(String.valueOf(responseLengthThreshold));
                stdout.println("警告: 阈值必须是非负整数，重置为: " + responseLengthThreshold);
            }
        } catch (NumberFormatException ex) {
            thresholdField.setText(String.valueOf(responseLengthThreshold));
            stdout.println("警告: 阈值不是有效数字，重置为: " + responseLengthThreshold);
        }
    }

    @Override
    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        // 检查全局开关和被动扫描开关
        if (!enabledCheckBox.isSelected() || !passiveScanCheckBox.isSelected()) {
            return null;
        }
        
        // 记录所有经过的流量
        recordTraffic(baseRequestResponse);
        
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
                        "高风险",
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
                        "高",
                        "确定"
                );
                
                issues.add(issue);
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
        return "Unauthorized Scan";
    }

    @Override
    public Component getUiComponent() {
        return mainPanel;
    }
    
    // 用于显示扫描结果的表格模型
    private class IssuesTableModel extends AbstractTableModel {
        private final String[] COLUMNS = { "编号", "URL", "请求方法", "原响应长度", "未授权响应长度", "差异", "状态" };
        
        @Override
        public int getRowCount() {
            return issues.size();
        }
        
        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }
        
        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0: return Integer.class; // 编号
                case 3: return Integer.class; // 原响应长度
                case 4: return Integer.class; // 未授权响应长度
                case 5: return Integer.class; // 差异
                default: return String.class;
            }
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ScanIssue issue = issues.get(rowIndex);
            IHttpRequestResponse[] messages = issue.getHttpMessages();
            
            switch (columnIndex) {
                case 0:
                    return rowIndex + 1; // 编号
                case 1:
                    return issue.getUrl().toString(); // URL
                case 2:
                    return extractRequestMethod(messages[0].getRequest()); // 请求方法
                case 3:
                    IResponseInfo originalResponseInfo = helpers.analyzeResponse(messages[0].getResponse());
                    return messages[0].getResponse().length - originalResponseInfo.getBodyOffset(); // 原始响应长度
                case 4:
                    IResponseInfo unauthorizedResponseInfo = helpers.analyzeResponse(messages[1].getResponse());
                    return messages[1].getResponse().length - unauthorizedResponseInfo.getBodyOffset(); // 未授权响应长度
                case 5:
                    // 计算差异
                    IResponseInfo origRespInfo = helpers.analyzeResponse(messages[0].getResponse());
                    IResponseInfo unauthRespInfo = helpers.analyzeResponse(messages[1].getResponse());
                    int origLen = messages[0].getResponse().length - origRespInfo.getBodyOffset();
                    int unauthLen = messages[1].getResponse().length - unauthRespInfo.getBodyOffset();
                    return Math.abs(origLen - unauthLen);
                case 6:
                    return issue.getSeverity(); // 状态
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

    // 实现IContextMenuFactory接口
    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        // 不显示任何右键菜单项
        return null;
    }

    // 新增：清空方法增加清空流量记录
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
            issuesModel.fireTableDataChanged();
            trafficTableModel.fireTableDataChanged();
            
            // 清空消息编辑器
            originalRequestViewer.setMessage(new byte[0], true);
            originalResponseViewer.setMessage(new byte[0], false);
            unauthorizedRequestViewer.setMessage(new byte[0], true);
            unauthorizedResponseViewer.setMessage(new byte[0], false);
            
            currentlyDisplayedItem = null;
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

    // 新增：流量表格右键菜单
    private void showTrafficContextMenu(MouseEvent e, Component component) {
        if (component != trafficTable) {
            return;
        }
        
        JPopupMenu menu = new JPopupMenu();
        
        // 添加Burp工具菜单项
        int viewRow = trafficTable.getSelectedRow();
        int row = viewRow != -1 ? trafficTable.convertRowIndexToModel(viewRow) : -1;
        // 将所有选中的行索引从视图转换为模型索引，方便后续批量删除操作
        int[] viewRows = trafficTable.getSelectedRows();
        final int[] selectedModelRows = new int[viewRows.length];
        for (int i = 0; i < viewRows.length; i++) {
            selectedModelRows[i] = trafficTable.convertRowIndexToModel(viewRows[i]);
        }
        
        if (row != -1 && row < allTraffic.size()) {
            IHttpRequestResponse message = allTraffic.get(row);
            IHttpService service = message.getHttpService();
            byte[] request = message.getRequest();
            byte[] response = message.getResponse();
            
            menu.add(new JLabel("原始请求:"));
            addBurpToolsMenuItems(menu, service, request, response);
            
            // 添加未授权请求相关菜单
            if (row < unauthorizedTraffic.size() && unauthorizedTraffic.get(row) != null) {
                IHttpRequestResponse unauthorizedMessage = unauthorizedTraffic.get(row);
                menu.addSeparator();
                menu.add(new JLabel("未授权请求:"));
                addBurpToolsMenuItems(menu, unauthorizedMessage.getHttpService(), 
                                     unauthorizedMessage.getRequest(), 
                                     unauthorizedMessage.getResponse());
            }
            
            // 添加检测按钮
            menu.addSeparator();
            JMenuItem checkItem = new JMenuItem("检测未授权访问漏洞");
            checkItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
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
            generateItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IHttpRequestResponse unauthorizedReq = createUnauthorizedRequest(message);
                    if (unauthorizedReq != null) {
                        // 更新未授权请求
                        if (row < unauthorizedTraffic.size()) {
                            unauthorizedTraffic.set(row, unauthorizedReq);
                        } else {
                            while (unauthorizedTraffic.size() < row) {
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
            
            // 添加删除按钮（批量删除选中项）
            menu.addSeparator();
            JMenuItem deleteItem = new JMenuItem("从列表中删除选中项");
            deleteItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selectedModelRows.length == 0) return;
                    int result = JOptionPane.showConfirmDialog(
                        mainPanel,
                        "确定要删除选中的 " + selectedModelRows.length + " 个流量记录吗？",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (result == JOptionPane.YES_OPTION) {
                        Arrays.sort(selectedModelRows);
                        for (int i = selectedModelRows.length - 1; i >= 0; i--) {
                            allTraffic.remove(selectedModelRows[i]);
                            if (selectedModelRows[i] < unauthorizedTraffic.size()) {
                                unauthorizedTraffic.remove(selectedModelRows[i]);
                            }
                            if (selectedModelRows[i] < isVulnerableList.size()) {
                                isVulnerableList.remove(selectedModelRows[i]);
                            }
                        }
                        trafficTableModel.fireTableDataChanged();
                    }
                }
            });
            menu.add(deleteItem);
        }
        
        menu.show(component, e.getX(), e.getY());
    }

    // 新增：流量表格模型
    private class TrafficTableModel extends AbstractTableModel {
        private final String[] COLUMNS = { "编号", "URL", "请求方法", "状态码", "响应长度", "未授权响应长度", "差异", "是否存在漏洞" };
        
        @Override
        public int getRowCount() {
            return allTraffic.size();
        }
        
        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }
        
        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0: return Integer.class; // 编号
                case 3: return Integer.class; // 状态码
                case 4: return Integer.class; // 响应长度
                case 5: return Integer.class; // 未授权响应长度
                case 6: return Integer.class; // 差异
                default: return String.class;
            }
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= allTraffic.size()) {
                return "";
            }
            
            IHttpRequestResponse message = allTraffic.get(rowIndex);
            IRequestInfo requestInfo = helpers.analyzeRequest(message);
            
            switch (columnIndex) {
                case 0:
                    return rowIndex + 1; // 编号
                case 1:
                    return requestInfo.getUrl().toString(); // URL
                case 2:
                    return extractRequestMethod(message.getRequest()); // 请求方法
                case 3:
                    if (message.getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(message.getResponse());
                        return responseInfo.getStatusCode(); // 状态码
                    } else {
                        return "N/A";
                    }
                case 4:
                    if (message.getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(message.getResponse());
                        return message.getResponse().length - responseInfo.getBodyOffset(); // 响应长度
                    } else {
                        return "N/A";
                    }
                case 5: // 未授权响应长度
                    if (rowIndex < unauthorizedTraffic.size() && unauthorizedTraffic.get(rowIndex) != null && 
                        unauthorizedTraffic.get(rowIndex).getResponse() != null) {
                        IResponseInfo responseInfo = helpers.analyzeResponse(unauthorizedTraffic.get(rowIndex).getResponse());
                        return unauthorizedTraffic.get(rowIndex).getResponse().length - responseInfo.getBodyOffset();
                    } else {
                        return "N/A";
                    }
                case 6: // 差异
                    if (message.getResponse() != null && 
                        rowIndex < unauthorizedTraffic.size() && 
                        unauthorizedTraffic.get(rowIndex) != null && 
                        unauthorizedTraffic.get(rowIndex).getResponse() != null) {
                        
                        IResponseInfo origInfo = helpers.analyzeResponse(message.getResponse());
                        IResponseInfo unauthInfo = helpers.analyzeResponse(unauthorizedTraffic.get(rowIndex).getResponse());
                        
                        int origLen = message.getResponse().length - origInfo.getBodyOffset();
                        int unauthLen = unauthorizedTraffic.get(rowIndex).getResponse().length - unauthInfo.getBodyOffset();
                        
                        return Math.abs(origLen - unauthLen);
                    } else {
                        return "N/A";
                    }
                case 7: // 是否存在漏洞
                    if (rowIndex < isVulnerableList.size() && isVulnerableList.get(rowIndex) != null) {
                        return isVulnerableList.get(rowIndex) ? "是" : "否";
                    } else {
                        return "未检测";
                    }
                default:
                    return "";
            }
        }
    }

    // 新增：记录所有流量
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
            
            // 处理新请求或更新已存在的请求
            if (existingIndex == -1) {
                // 添加新请求
                allTraffic.add(requestResponse);
                
                // 创建和记录未授权请求版本
                IHttpRequestResponse unauthorizedReq = createUnauthorizedRequest(requestResponse);
                unauthorizedTraffic.add(unauthorizedReq);
                
                // 判断是否存在漏洞并记录
                boolean isVulnerable = checkVulnerability(requestResponse, unauthorizedReq);
                isVulnerableList.add(isVulnerable);
                
                // 调试信息
                stdout.println("记录新流量: " + requestInfo.getUrl() + (isVulnerable ? " [存在漏洞]" : ""));

                // 新增：如果存在漏洞且未在issues表中，则自动添加
                if (isVulnerable) {
                    boolean alreadyExists = false;
                    for (ScanIssue issue : issues) {
                        if (issue.getUrl().toString().equals(url)) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists && unauthorizedReq != null && unauthorizedReq.getResponse() != null) {
                        IResponseInfo originalResponseInfo = helpers.analyzeResponse(requestResponse.getResponse());
                        IResponseInfo unauthorizedResponseInfo = helpers.analyzeResponse(unauthorizedReq.getResponse());
                        int originalLength = requestResponse.getResponse().length - originalResponseInfo.getBodyOffset();
                        int unauthorizedLength = unauthorizedReq.getResponse().length - unauthorizedResponseInfo.getBodyOffset();
                        String detail = String.format(
                            "状态: %s\n原始状态码: %d\n未授权状态码: %d\n原始长度: %d\n未授权长度: %d\n差异: %d\n漏洞判断依据: %s",
                            "高风险",
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
                            "高",
                            "确定"
                        );
                        issues.add(issue);
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
                
                // 更新未授权请求版本
                IHttpRequestResponse unauthorizedReq = createUnauthorizedRequest(requestResponse);
                if (existingIndex < unauthorizedTraffic.size()) {
                    unauthorizedTraffic.set(existingIndex, unauthorizedReq);
                } else {
                    while (unauthorizedTraffic.size() < existingIndex) {
                        unauthorizedTraffic.add(null);
                    }
                    unauthorizedTraffic.add(unauthorizedReq);
                }
                
                // 更新漏洞标记
                boolean isVulnerable = checkVulnerability(requestResponse, unauthorizedReq);
                if (existingIndex < isVulnerableList.size()) {
                    isVulnerableList.set(existingIndex, isVulnerable);
                } else {
                    while (isVulnerableList.size() < existingIndex) {
                        isVulnerableList.add(false);
                    }
                    isVulnerableList.add(isVulnerable);
                }
                
                stdout.println("更新已存在流量: " + requestInfo.getUrl() + (isVulnerable ? " [存在漏洞]" : ""));
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
            recordTraffic(messageInfo);
        }
    }
} 