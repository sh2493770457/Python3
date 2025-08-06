package burp;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IContextMenuFactory;
import burp.IContextMenuInvocation;
import burp.IExtensionHelpers;
import burp.IHttpListener;
import burp.IHttpRequestResponse;
import burp.IHttpRequestResponsePersisted;
import burp.IHttpService;
import burp.IMessageEditor;
import burp.IMessageEditorController;
import burp.IParameter;
import burp.IRequestInfo;
import burp.IResponseInfo;
import burp.IScanIssue;
import burp.IScannerCheck;
import burp.IScannerInsertionPoint;
import burp.ITab;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class BurpExtender
extends AbstractTableModel
implements IBurpExtender,
ITab,
IHttpListener,
IScannerCheck,
IMessageEditorController,
IContextMenuFactory {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private JSplitPane splitPane;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private IMessageEditor requestViewer_1;
    private IMessageEditor responseViewer_1;
    private IMessageEditor requestViewer_2;
    private IMessageEditor responseViewer_2;
    private IHttpRequestResponse currentlyDisplayedItem;
    private IHttpRequestResponse currentlyDisplayedItem_1;
    private IHttpRequestResponse currentlyDisplayedItem_2;
    public PrintWriter stdout;
    JTabbedPane tabs;
    int original_data_len;
    String temp_data;
    Table logTable;
    private final List<LogEntry> log = new ArrayList<LogEntry>();
    private final List<LogEntry> originalLog = new ArrayList<LogEntry>(); // 备份原始数据，用于恢复
    private final List<Request_md5> log4_md5 = new ArrayList<Request_md5>();
    int switchs = 0;
    int conut = 0;
    int select_row = 0;
    String data_1 = "";
    String data_2 = "";
    String universal_cookie = "";
    String xy_version = "2.7";
    private TableRowSorter<AbstractTableModel> tableSorter;
    private JCheckBox chkbox_url_auth;
    private JCheckBox chkbox_url_unauth;
    private JTextArea jta_url;
    private JTextArea jta_url_unauth;
    private JTextArea jta;
    private JTextArea jta_1;
    private JCheckBox chkbox_repeat_test;
    private JCheckBox chkbox_show_vuln_only;
    private IMessageEditor leftReqViewer;
    private IMessageEditor rightReqViewer;
    private IMessageEditor leftRespViewer;
    private IMessageEditor rightRespViewer;
    private IMessageEditor thirdReqViewer;
    private IMessageEditor thirdRespViewer;
    private JCheckBox chkbox_suffix_blacklist;
    private JTextField txt_suffix_blacklist;
    private JLabel jls_suffix_blacklist;
    private JCheckBox chkbox_manual_test;
    private JPanel jps_2;
    private JPanel jPanel2;
    private JCheckBox chkbox1;
    private JCheckBox chkbox2;

    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks) {
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stdout.println("hello tomato_yue!");
        this.stdout.println("\u4f60\u597d \u6b22\u8fce\u4f7f\u7528 tomato_yue!");
        this.stdout.println("version:" + this.xy_version);
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        callbacks.setExtensionName("tomato_yue V" + this.xy_version);
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                BurpExtender.this.splitPane = new JSplitPane(1);
                JSplitPane splitPanes = new JSplitPane(0);
                JSplitPane splitPanes_2 = new JSplitPane(0);
                BurpExtender.this.logTable = new Table(BurpExtender.this);

                // 设置列宽
                BurpExtender.this.logTable.getColumnModel().getColumn(0).setPreferredWidth(10);
                BurpExtender.this.logTable.getColumnModel().getColumn(1).setPreferredWidth(50);
                BurpExtender.this.logTable.getColumnModel().getColumn(2).setPreferredWidth(300);
                BurpExtender.this.logTable.getColumnModel().getColumn(3).setPreferredWidth(100);
                BurpExtender.this.logTable.getColumnModel().getColumn(4).setPreferredWidth(80);
                BurpExtender.this.logTable.getColumnModel().getColumn(5).setPreferredWidth(80);
                BurpExtender.this.logTable.getColumnModel().getColumn(6).setPreferredWidth(80);

                // 设置所有列左对齐
                DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
                leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
                for (int i = 0; i < BurpExtender.this.logTable.getColumnCount(); i++) {
                    BurpExtender.this.logTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
                }
                JScrollPane jScrollPane = new JScrollPane(BurpExtender.this.logTable);
                JPanel jPanel = new JPanel();
                jPanel.setLayout(new GridLayout(1, 1));
                jPanel.add(jScrollPane);
                BurpExtender.this.jps_2 = new JPanel();
                BurpExtender.this.jps_2.setLayout(new GridLayout(0, 1, 2, 0)); // 减小垂直间距
                JLabel jps_2_jls_1 = new JLabel("\u8d8a\u6743\uff1a\u586b\u5199\u4f4e\u6743\u9650\u8ba4\u8bc1\u4fe1\u606f\uff0c\u5c06\u88ab\u66ff\u6362\u6216\u8005\u65b0\u589e");
                JLabel headerLabel = new JLabel("\u66ff\u6362\u8bf7\u6c42\u5934\u53c2\u6570(\u6bcf\u884c\u4e00\u4e2a,\u683c\u5f0f:\u5934\u540d: \u5934\u503c):");
                BurpExtender.this.jta = new JTextArea("Cookie: JSESSIONID=test;UUID=1; userid=admin\nAuthorization: Bearer test", 5, 30);
                JScrollPane jsp = new JScrollPane(BurpExtender.this.jta);
                BurpExtender.this.chkbox_url_auth = new JCheckBox("<html>\u5904\u7406\u8bf7\u6c42\u884c\u548cPOST\u8bf7\u6c42\u4f53\u4e2d\u7684\u8ba4\u8bc1\u53c2\u6570<font color='gray'>\uff08\u7528\u4e8e\u8ba4\u8bc1\u53c2\u6570\u5728\u8bf7\u6c42\u884c\u6216POST\u8bf7\u6c42\u4f53\u4e2d\u7684\u60c5\u51b5,\u4f8b\u5982url\u6216\u8bf7\u6c42\u4f53\u5b58\u5728?token=xxxx)</font></html>");
                JLabel jps_2_jls_url = new JLabel("\u66ff\u6362\u8bf7\u6c42\u884c\u53c2\u6570(\u6bcf\u884c\u4e00\u4e2a,\u683c\u5f0f:\u53c2\u6570\u540d=\u53c2\u6570\u503c):");
                BurpExtender.this.jta_url = new JTextArea("token=11111\nticket=test", 3, 30);
                JScrollPane jsp_url = new JScrollPane(BurpExtender.this.jta_url);
                JLabel jps_2_jls_2 = new JLabel("\u672a\u6388\u6743\u6d4b\u8bd5\u9009\u9879\uff1a\u6dfb\u52a0\u672a\u6388\u6743\u6d4b\u8bd5\u7684\u8bf7\u6c42\u884c\u6216\u8bf7\u6c42\u5934\uff0c\u8bf7\u6c42\u4f53\uff0c\u53d1\u5305\u6d4b\u8bd5\u65f6\u5c06\u79fb\u9664");
                BurpExtender.this.chkbox_url_unauth = new JCheckBox("<html>\u5904\u7406\u8bf7\u6c42\u884c\u548cPOST\u8bf7\u6c42\u4f53\u4e2d\u7684\u8ba4\u8bc1\u53c2\u6570<font color='gray'>\uff08\u5c06\u79fb\u9664\u6dfb\u52a0\u7684\u8bf7\u6c42\u884c\u4e2d\u7684\u503c\uff09</font></html>");
                JLabel jps_2_jls_url_unauth = new JLabel("\u8981\u79fb\u9664\u7684\u548cPOST\u8bf7\u6c42\u4f53\u4e2d\u7684\u8ba4\u8bc1\u53c2\u6570(\u533a\u5206\u5927\u5c0f\u5199\uff0c\u6bcf\u884c\u4e00\u4e2a):");
                BurpExtender.this.jta_url_unauth = new JTextArea("token\nticket", 3, 30);
                JScrollPane jsp_url_unauth = new JScrollPane(BurpExtender.this.jta_url_unauth);
                JLabel headerRemoveLabel = new JLabel("\u8981\u79fb\u9664\u7684\u8bf7\u6c42\u5934\u53c2\u6570(\u5c06\u79fb\u9664\u6dfb\u52a0\u7684\u8bf7\u6c42\u5934\uff0c\u533a\u5206\u5927\u5c0f\u5199\uff0c\u6bcf\u884c\u4e00\u4e2a):");
                BurpExtender.this.jta_1 = new JTextArea("Cookie\nAuthorization\nToken", 5, 30);
                JScrollPane jsp_1 = new JScrollPane(BurpExtender.this.jta_1);
                BurpExtender.this.jps_2.add(jps_2_jls_1);
                BurpExtender.this.jps_2.add(headerLabel);
                BurpExtender.this.jps_2.add(jsp);
                BurpExtender.this.jps_2.add(BurpExtender.this.chkbox_url_auth);
                BurpExtender.this.jps_2.add(jps_2_jls_url);
                BurpExtender.this.jps_2.add(jsp_url);
                BurpExtender.this.jps_2.add(jps_2_jls_2);
                BurpExtender.this.jps_2.add(BurpExtender.this.chkbox_url_unauth);
                BurpExtender.this.jps_2.add(jps_2_jls_url_unauth);
                BurpExtender.this.jps_2.add(jsp_url_unauth);
                BurpExtender.this.jps_2.add(headerRemoveLabel);
                BurpExtender.this.jps_2.add(jsp_1);
                BurpExtender.this.chkbox_url_auth.addItemListener(new ItemListener(){

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (BurpExtender.this.chkbox_url_auth.isSelected()) {
                            BurpExtender.this.jta_url.setForeground(Color.BLACK);
                            BurpExtender.this.jta_url.setBackground(Color.WHITE);
                            BurpExtender.this.jta_url.setEditable(true);
                        } else {
                            BurpExtender.this.jta_url.setForeground(Color.GRAY);
                            BurpExtender.this.jta_url.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.jta_url.setEditable(false);
                        }
                    }
                });
                BurpExtender.this.chkbox_url_unauth.addItemListener(new ItemListener(){

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (BurpExtender.this.chkbox_url_unauth.isSelected()) {
                            BurpExtender.this.jta_url_unauth.setForeground(Color.BLACK);
                            BurpExtender.this.jta_url_unauth.setBackground(Color.WHITE);
                            BurpExtender.this.jta_url_unauth.setEditable(true);
                        } else {
                            BurpExtender.this.jta_url_unauth.setForeground(Color.GRAY);
                            BurpExtender.this.jta_url_unauth.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.jta_url_unauth.setEditable(false);
                        }
                    }
                });
                BurpExtender.this.jta_url.setForeground(Color.GRAY);
                BurpExtender.this.jta_url.setBackground(Color.LIGHT_GRAY);
                BurpExtender.this.jta_url.setEditable(false);
                BurpExtender.this.jta_url_unauth.setForeground(Color.GRAY);
                BurpExtender.this.jta_url_unauth.setBackground(Color.LIGHT_GRAY);
                BurpExtender.this.jta_url_unauth.setEditable(false);
                BurpExtender.this.jPanel2 = new JPanel();
                BurpExtender.this.jPanel2.setLayout(new GridLayout(0, 1, 2, 0)); // 减小垂直间距
                JLabel jls = new JLabel("\u63d2\u4ef6\u540d\uff1atomato_yue Pro V" + BurpExtender.this.xy_version + "\u0020\u0020\u0020\u0020\u0020\u4f5c\u8005\uff1a\u0074\u006f\u006d\u0061\u0074\u006f");
                BurpExtender.this.chkbox1 = new JCheckBox("\u542f\u52a8\u63d2\u4ef6");
                BurpExtender.this.chkbox2 = new JCheckBox("\u542f\u52a8\u4e07\u80fdcookie");
                BurpExtender.this.chkbox_repeat_test = new JCheckBox("\u5141\u8bb8\u91cd\u590d\u8bf7\u6c42\u6d4b\u8bd5(\u9002\u7528\u4e8e\u901a\u8fc7body\u4f20\u53c2URL\u4e0d\u53d8\u7684\u7cfb\u7edf)");
                BurpExtender.this.chkbox_show_vuln_only = new JCheckBox("\u4ec5\u663e\u793a\u5b58\u5728\u8d8a\u6743\u7684\u8bf7\u6c42");
                BurpExtender.this.chkbox_show_vuln_only.addItemListener(new ItemListener(){

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        BurpExtender.this.fireTableDataChanged();
                    }
                });


                BurpExtender.this.jPanel2.add(jls);
                BurpExtender.this.jPanel2.add(BurpExtender.this.chkbox1);
                BurpExtender.this.jPanel2.add(BurpExtender.this.chkbox_repeat_test);
                BurpExtender.this.jPanel2.add(BurpExtender.this.chkbox_show_vuln_only);

                BurpExtender.this.chkbox_manual_test = new JCheckBox("\u4ec5\u63a5\u53d7\u53f3\u952e\u53d1\u9001\u7684\u5305\u8fdb\u884c\u8d8a\u6743\u6d4b\u8bd5(\u5173\u95ed\u81ea\u52a8\u6d4b\u8bd5)");
                BurpExtender.this.jPanel2.add(BurpExtender.this.chkbox_manual_test);

                BurpExtender.this.chkbox_suffix_blacklist = new JCheckBox("\u542f\u7528\u540e\u7f00\u9ed1\u540d\u5355");
                BurpExtender.this.jls_suffix_blacklist = new JLabel("\u540e\u7f00\u9ed1\u540d\u5355(\u7528,\u9017\u53f7\u5206\u9694,\u9ed8\u8ba4html):");
                BurpExtender.this.txt_suffix_blacklist = new JTextField("html", 10);
                BurpExtender.this.chkbox_suffix_blacklist.addItemListener(new ItemListener(){

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (BurpExtender.this.chkbox_suffix_blacklist.isSelected()) {
                            BurpExtender.this.txt_suffix_blacklist.setForeground(Color.BLACK);
                            BurpExtender.this.txt_suffix_blacklist.setBackground(Color.WHITE);
                            BurpExtender.this.txt_suffix_blacklist.setEditable(true);
                        } else {
                            BurpExtender.this.txt_suffix_blacklist.setForeground(Color.GRAY);
                            BurpExtender.this.txt_suffix_blacklist.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.txt_suffix_blacklist.setEditable(false);
                        }
                    }
                });
                BurpExtender.this.chkbox_suffix_blacklist.setSelected(true);
                BurpExtender.this.txt_suffix_blacklist.setForeground(Color.BLACK);
                BurpExtender.this.txt_suffix_blacklist.setBackground(Color.WHITE);
                BurpExtender.this.txt_suffix_blacklist.setEditable(true);
                BurpExtender.this.jPanel2.add(BurpExtender.this.chkbox_suffix_blacklist);
                BurpExtender.this.jPanel2.add(BurpExtender.this.jls_suffix_blacklist);
                BurpExtender.this.jPanel2.add(BurpExtender.this.txt_suffix_blacklist);
                BurpExtender.this.chkbox1.addItemListener(new ItemListener(){

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (BurpExtender.this.chkbox1.isSelected()) {
                            BurpExtender.this.switchs = 1;
                            BurpExtender.this.data_1 = BurpExtender.this.jta.getText();
                            BurpExtender.this.data_2 = BurpExtender.this.jta_1.getText();
                            BurpExtender.this.jta.setForeground(Color.BLACK);
                            BurpExtender.this.jta.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.jta.setEditable(false);
                            BurpExtender.this.jta_1.setForeground(Color.BLACK);
                            BurpExtender.this.jta_1.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.jta_1.setEditable(false);
                            BurpExtender.this.jta_url.setForeground(Color.BLACK);
                            BurpExtender.this.jta_url.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.jta_url.setEditable(false);
                            BurpExtender.this.jta_url_unauth.setForeground(Color.BLACK);
                            BurpExtender.this.jta_url_unauth.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.jta_url_unauth.setEditable(false);
                            if (BurpExtender.this.chkbox_suffix_blacklist != null) {
                                BurpExtender.this.chkbox_suffix_blacklist.setEnabled(false);
                            }
                            if (BurpExtender.this.txt_suffix_blacklist != null) {
                                BurpExtender.this.txt_suffix_blacklist.setForeground(Color.GRAY);
                                BurpExtender.this.txt_suffix_blacklist.setBackground(Color.LIGHT_GRAY);
                                BurpExtender.this.txt_suffix_blacklist.setEditable(false);
                            }
                            return;
                        }
                        BurpExtender.this.switchs = 0;
                        BurpExtender.this.jta.setForeground(Color.BLACK);
                        BurpExtender.this.jta.setBackground(Color.WHITE);
                        BurpExtender.this.jta.setEditable(true);
                        BurpExtender.this.jta_1.setForeground(Color.BLACK);
                        BurpExtender.this.jta_1.setBackground(Color.WHITE);
                        BurpExtender.this.jta_1.setEditable(true);
                        if (BurpExtender.this.chkbox_url_auth.isSelected()) {
                            BurpExtender.this.jta_url.setForeground(Color.BLACK);
                            BurpExtender.this.jta_url.setBackground(Color.WHITE);
                            BurpExtender.this.jta_url.setEditable(true);
                        } else {
                            BurpExtender.this.jta_url.setForeground(Color.GRAY);
                            BurpExtender.this.jta_url.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.jta_url.setEditable(false);
                        }
                        if (BurpExtender.this.chkbox_url_unauth.isSelected()) {
                            BurpExtender.this.jta_url_unauth.setForeground(Color.BLACK);
                            BurpExtender.this.jta_url_unauth.setBackground(Color.WHITE);
                            BurpExtender.this.jta_url_unauth.setEditable(true);
                        } else {
                            BurpExtender.this.jta_url_unauth.setForeground(Color.GRAY);
                            BurpExtender.this.jta_url_unauth.setBackground(Color.LIGHT_GRAY);
                            BurpExtender.this.jta_url_unauth.setEditable(false);
                        }
                        if (BurpExtender.this.chkbox_suffix_blacklist != null) {
                            BurpExtender.this.chkbox_suffix_blacklist.setEnabled(true);
                        }
                        if (BurpExtender.this.txt_suffix_blacklist != null) {
                            BurpExtender.this.txt_suffix_blacklist.setForeground(Color.BLACK);
                            BurpExtender.this.txt_suffix_blacklist.setBackground(Color.WHITE);
                            BurpExtender.this.txt_suffix_blacklist.setEditable(true);
                        }
                    }
                });
                BurpExtender.this.chkbox2.addItemListener(new ItemListener(){

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        BurpExtender.this.universal_cookie = BurpExtender.this.chkbox2.isSelected() ? "" : "";
                    }
                });
                BurpExtender.this.tabs = new JTabbedPane();
                BurpExtender.this.requestViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.responseViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.requestViewer_1 = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.responseViewer_1 = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.requestViewer_2 = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.responseViewer_2 = callbacks.createMessageEditor(BurpExtender.this, false);
                JSplitPane y_jp = new JSplitPane(1);
                y_jp.setDividerLocation(500);
                y_jp.setLeftComponent(BurpExtender.this.requestViewer.getComponent());
                y_jp.setRightComponent(BurpExtender.this.responseViewer.getComponent());
                JSplitPane d_jp = new JSplitPane(1);
                d_jp.setDividerLocation(500);
                d_jp.setLeftComponent(BurpExtender.this.requestViewer_1.getComponent());
                d_jp.setRightComponent(BurpExtender.this.responseViewer_1.getComponent());
                JSplitPane w_jp = new JSplitPane(1);
                w_jp.setDividerLocation(500);
                w_jp.setLeftComponent(BurpExtender.this.requestViewer_2.getComponent());
                w_jp.setRightComponent(BurpExtender.this.responseViewer_2.getComponent());
                BurpExtender.this.tabs.addTab("\u539f\u59cb\u6570\u636e\u5305", y_jp);
                BurpExtender.this.tabs.addTab("\u4f4e\u6743\u9650\u6570\u636e\u5305", d_jp);
                BurpExtender.this.tabs.addTab("\u672a\u6388\u6743\u6570\u636e\u5305", w_jp);
                splitPanes_2.setLeftComponent(BurpExtender.this.jPanel2);
                splitPanes_2.setRightComponent(BurpExtender.this.jps_2);
                splitPanes.setLeftComponent(jPanel);
                splitPanes.setRightComponent(BurpExtender.this.tabs);
                BurpExtender.this.splitPane.setLeftComponent(splitPanes);
                BurpExtender.this.splitPane.setRightComponent(splitPanes_2);
                BurpExtender.this.splitPane.setDividerLocation(1000);
                callbacks.customizeUiComponent(BurpExtender.this.splitPane);
                callbacks.customizeUiComponent(BurpExtender.this.logTable);
                callbacks.customizeUiComponent(jScrollPane);
                callbacks.customizeUiComponent(BurpExtender.this.jPanel2);
                callbacks.customizeUiComponent(jPanel);
                callbacks.customizeUiComponent(BurpExtender.this.tabs);
                // 现在所有组件都已初始化，可以安全地设置表格排序功能
                BurpExtender.this.tableSorter = new TableRowSorter<AbstractTableModel>(BurpExtender.this);
                BurpExtender.this.logTable.setRowSorter(BurpExtender.this.tableSorter);

                // 确保表格头部支持排序
                BurpExtender.this.logTable.setAutoCreateRowSorter(false); // 我们手动设置了排序器
                BurpExtender.this.logTable.getTableHeader().setReorderingAllowed(true);

                // 调试：确认排序器设置
                callbacks.addSuiteTab(BurpExtender.this);
                callbacks.registerHttpListener(BurpExtender.this);
                callbacks.registerScannerCheck(BurpExtender.this);
                // 创建对比视图
                JTabbedPane diffTabbedPane = new JTabbedPane();
                
                // 创建对比视图控制面板
                JPanel diffControlPanel = new JPanel(new BorderLayout());
                JCheckBox chkbox_show_unauth = new JCheckBox("显示未授权视图对比", false);
                chkbox_show_unauth.setToolTipText("勾选后显示：原始视图 + 低权限视图 + 未授权视图；不勾选显示：原始视图 + 低权限视图");
                
                JPanel controlTopPanel = new JPanel();
                controlTopPanel.add(chkbox_show_unauth);
                diffControlPanel.add(controlTopPanel, "North");
                
                // 创建消息编辑器
                BurpExtender.this.leftReqViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.rightReqViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.leftRespViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.rightRespViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                
                // 创建第三个视图编辑器（用于未授权视图）
                BurpExtender.this.thirdReqViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.thirdRespViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                
                // 创建面板
                JPanel leftReqPanel = new JPanel(new BorderLayout());
                JPanel rightReqPanel = new JPanel(new BorderLayout());
                JPanel leftRespPanel = new JPanel(new BorderLayout());
                JPanel rightRespPanel = new JPanel(new BorderLayout());
                JPanel thirdReqPanel = new JPanel(new BorderLayout());
                JPanel thirdRespPanel = new JPanel(new BorderLayout());
                
                // 创建标签
                JLabel leftReqLabel = new JLabel("原始请求包", 0);
                JLabel rightReqLabel = new JLabel("低权限请求包", 0);
                JLabel leftRespLabel = new JLabel("原始响应包", 0);
                JLabel rightRespLabel = new JLabel("低权限响应包", 0);
                JLabel thirdReqLabel = new JLabel("未授权请求包", 0);
                JLabel thirdRespLabel = new JLabel("未授权响应包", 0);
                
                // 组装面板
                leftReqPanel.add((Component)leftReqLabel, "North");
                leftReqPanel.add(BurpExtender.this.leftReqViewer.getComponent(), "Center");
                
                rightReqPanel.add((Component)rightReqLabel, "North");
                rightReqPanel.add(BurpExtender.this.rightReqViewer.getComponent(), "Center");
                
                thirdReqPanel.add((Component)thirdReqLabel, "North");
                thirdReqPanel.add(BurpExtender.this.thirdReqViewer.getComponent(), "Center");

                leftRespPanel.add((Component)leftRespLabel, "North");
                leftRespPanel.add(BurpExtender.this.leftRespViewer.getComponent(), "Center");

                rightRespPanel.add((Component)rightRespLabel, "North");
                rightRespPanel.add(BurpExtender.this.rightRespViewer.getComponent(), "Center");

                thirdRespPanel.add((Component)thirdRespLabel, "North");
                thirdRespPanel.add(BurpExtender.this.thirdRespViewer.getComponent(), "Center");
                
                // 默认设置（原始视图 + 低权限视图）
                JSplitPane reqDiffPane = new JSplitPane(1);
                JSplitPane respDiffPane = new JSplitPane(1);
                reqDiffPane.setLeftComponent(leftReqPanel);
                reqDiffPane.setRightComponent(rightReqPanel);
                reqDiffPane.setDividerLocation(400); // 默认二等分
                reqDiffPane.setResizeWeight(0.5); // 允许用户调整
                
                respDiffPane.setLeftComponent(leftRespPanel);
                respDiffPane.setRightComponent(rightRespPanel);
                respDiffPane.setDividerLocation(400); // 默认二等分
                respDiffPane.setResizeWeight(0.5); // 允许用户调整
                
                // 复选框事件监听器
                chkbox_show_unauth.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (chkbox_show_unauth.isSelected()) {
                            // 显示三个视图：原始 + 低权限 + 未授权
                            JSplitPane reqTriplePane = new JSplitPane(1);
                            JSplitPane reqLeftPane = new JSplitPane(1);

                            // 设置三等分布局 - 真正的等比例分配
                            reqLeftPane.setLeftComponent(leftReqPanel);
                            reqLeftPane.setRightComponent(rightReqPanel);
                            reqLeftPane.setResizeWeight(0.5); // 左右两个面板等比例

                            reqTriplePane.setLeftComponent(reqLeftPane);
                            reqTriplePane.setRightComponent(thirdReqPanel);
                            reqTriplePane.setResizeWeight(0.6667); // 前两个面板占2/3，第三个面板占1/3，实现三等分

                            JSplitPane respTriplePane = new JSplitPane(1);
                            JSplitPane respLeftPane = new JSplitPane(1);

                            respLeftPane.setLeftComponent(leftRespPanel);
                            respLeftPane.setRightComponent(rightRespPanel);
                            respLeftPane.setResizeWeight(0.5); // 左右两个面板等比例

                            respTriplePane.setLeftComponent(respLeftPane);
                            respTriplePane.setRightComponent(thirdRespPanel);
                            respTriplePane.setResizeWeight(0.6667); // 前两个面板占2/3，第三个面板占1/3，实现三等分

                            // 使用SwingUtilities.invokeLater确保在组件完全布局后设置分割位置
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    // 获取当前面板宽度并计算三等分位置
                                    int totalWidth = reqTriplePane.getWidth();
                                    if (totalWidth > 0) {
                                        reqLeftPane.setDividerLocation(totalWidth / 3);
                                        reqTriplePane.setDividerLocation(totalWidth * 2 / 3);
                                        respLeftPane.setDividerLocation(totalWidth / 3);
                                        respTriplePane.setDividerLocation(totalWidth * 2 / 3);
                                    }
                                }
                            });
                            
                            // 替换面板内容
                            diffTabbedPane.removeAll();
                            diffTabbedPane.addTab("请求对比", reqTriplePane);
                            diffTabbedPane.addTab("响应对比", respTriplePane);
                            
                            // 如果有选中的条目，更新第三个视图
                            if (BurpExtender.this.select_row >= 0 && BurpExtender.this.select_row < BurpExtender.this.log.size()) {
                                LogEntry selectedEntry = BurpExtender.this.log.get(BurpExtender.this.select_row);
                                BurpExtender.this.thirdReqViewer.setMessage(selectedEntry.requestResponse_2.getRequest(), true);
                                BurpExtender.this.thirdRespViewer.setMessage(selectedEntry.requestResponse_2.getResponse(), false);
                            }
                        } else {
                            // 显示两个视图：原始 + 低权限
                            reqDiffPane.setLeftComponent(leftReqPanel);
                            reqDiffPane.setRightComponent(rightReqPanel);
                            respDiffPane.setLeftComponent(leftRespPanel);
                            respDiffPane.setRightComponent(rightRespPanel);
                            
                            // 替换面板内容
                            diffTabbedPane.removeAll();
                            diffTabbedPane.addTab("请求对比", reqDiffPane);
                            diffTabbedPane.addTab("响应对比", respDiffPane);
                        }
                        
                        diffTabbedPane.revalidate();
                        diffTabbedPane.repaint();
                    }
                });
                
                diffTabbedPane.addTab("请求对比", reqDiffPane);
                diffTabbedPane.addTab("响应对比", respDiffPane);
                
                // 将控制面板和对比视图组合
                JPanel diffMainPanel = new JPanel(new BorderLayout());
                diffMainPanel.add(diffControlPanel, "North");
                diffMainPanel.add(diffTabbedPane, "Center");
                
                BurpExtender.this.tabs.addTab("对比视图", diffMainPanel);

                } catch (Exception e) {
                    BurpExtender.this.stdout.println("UI initialization failed: " + e.getMessage());
                    e.printStackTrace(BurpExtender.this.stdout);
                }
            }
        });
        callbacks.registerContextMenuFactory(this);
    }

    @Override
    public String getTabCaption() {
        return "tomato_yue Pro";
    }

    @Override
    public Component getUiComponent() {
        return this.splitPane;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processHttpMessage(final int toolFlag, boolean messageIsRequest, final IHttpRequestResponse messageInfo) {
        IRequestInfo requestInfo = this.helpers.analyzeRequest(messageInfo);
        String method = requestInfo.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }
        if (this.chkbox_manual_test != null && this.chkbox_manual_test.isSelected()) {
            return;
        }
        if (this.switchs == 1 && toolFlag == 4 && !messageIsRequest) {
            List<LogEntry> list = this.log;
            synchronized (list) {
                Thread thread = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            BurpExtender.this.checkVul(messageInfo, toolFlag);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            BurpExtender.this.stdout.println("\u5904\u7406\u8bf7\u6c42\u65f6\u53d1\u751f\u9519\u8bef: " + ex.getMessage());
                        }
                    }
                });
                thread.start();
            }
        }
    }

    @Override
    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        return null;
    }

    // 改进的内容类型检测方法 - 结合URL后缀和Content-Type头
    private boolean shouldSkipRequest(IHttpRequestResponse baseRequestResponse, IRequestInfo requestInfo) {
        try {
            // 获取黑名单后缀
            String[] blacklistSuffixes = this.txt_suffix_blacklist.getText().trim().split(",");
            String urlPath = requestInfo.getUrl().getPath().toLowerCase();

            // 检查URL后缀
            boolean hasBlacklistedSuffix = false;
            for (String suffix : blacklistSuffixes) {
                suffix = suffix.trim().toLowerCase();
                if (urlPath.endsWith("." + suffix)) {
                    hasBlacklistedSuffix = true;
                    break;
                }
            }

            // 检查Content-Type头
            boolean hasBlacklistedContentType = false;
            if (baseRequestResponse.getResponse() != null) {
                IResponseInfo responseInfo = this.helpers.analyzeResponse(baseRequestResponse.getResponse());
                List<String> headers = responseInfo.getHeaders();

                for (String header : headers) {
                    if (header.toLowerCase().startsWith("content-type:")) {
                        String contentType = header.substring(header.indexOf(":") + 1).trim().toLowerCase();

                        // 检查是否为HTML内容类型
                        if (contentType.contains("text/html") ||
                            contentType.contains("application/xhtml")) {
                            hasBlacklistedContentType = true;
                            break;
                        }

                        // 检查其他静态资源类型
                        String[] staticContentTypes = {
                            "image/", "text/css", "text/javascript", "application/javascript",
                            "application/pdf", "audio/", "video/", "font/", "application/font"
                        };

                        for (String staticType : staticContentTypes) {
                            if (contentType.contains(staticType)) {
                                hasBlacklistedContentType = true;
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            // 如果URL后缀或Content-Type任一匹配黑名单，则跳过
            return hasBlacklistedSuffix || hasBlacklistedContentType;

        } catch (Exception e) {
            this.stdout.println("Error in shouldSkipRequest: " + e.getMessage());
            return false;
        }
    }

    private void checkVul(IHttpRequestResponse baseRequestResponse, int toolFlag) {
        IRequestInfo analyIRequestInfo = this.helpers.analyzeRequest(baseRequestResponse);
        String requestMethod = analyIRequestInfo.getMethod();
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            this.stdout.println("[*] \u8df3\u8fc7 OPTIONS \u8bf7\u6c42\uff0c\u4e0d\u8fdb\u884c\u8d8a\u6743\u6d4b\u8bd5");
            return;
        }
        String tempData = String.valueOf(analyIRequestInfo.getUrl());
        if (tempData == null || tempData.isEmpty()) {
            this.stdout.println("[*] URL \u4e3a\u7a7a\uff0c\u8df3\u8fc7\u8d8a\u6743\u6d4b\u8bd5");
            return;
        }
        this.temp_data = tempData;

        // 改进的内容类型检测 - 结合URL后缀和Content-Type头
        if (this.chkbox_suffix_blacklist != null && this.chkbox_suffix_blacklist.isSelected()) {
            if (shouldSkipRequest(baseRequestResponse, analyIRequestInfo)) {
                this.stdout.println("[*] \u8df3\u8fc7\u9759\u6001\u8d44\u6e90\u6216HTML\u5185\u5bb9\uff1a" + this.temp_data);
                return;
            }
        }
        this.original_data_len = baseRequestResponse.getResponse().length;
        int original_len = this.original_data_len - this.helpers.analyzeResponse(baseRequestResponse.getResponse()).getBodyOffset();
        String[] temp_data_strarray = this.temp_data.split("\\?");
        Object temp_data = temp_data_strarray[0];
        if (toolFlag == 4 || toolFlag == 64) {
            String[] static_file = new String[]{"jpg", "png", "gif", "css", "js", "pdf", "mp3", "mp4", "avi", "map", "svg", "ico", "svg", "woff", "woff2", "ttf"};
            String[] static_file_1 = ((String)temp_data).split("\\.");
            String static_file_2 = static_file_1[static_file_1.length - 1];
            for (String i : static_file) {
                if (!static_file_2.equals(i)) continue;
                return;
            }
        }
        List<IParameter> paraLists = analyIRequestInfo.getParameters();
        for (IParameter para : paraLists) {
            temp_data = (String)temp_data + "+" + para.getName();
        }
        String temp_data2 = (String)temp_data + "+" + requestMethod;
        this.stdout.println("\nMD5(\"" + temp_data2 + "\")");
        String temp_data3 = BurpExtender.MD5(temp_data2);
        this.stdout.println(temp_data3);
        if (!this.chkbox_repeat_test.isSelected()) {
            for (Request_md5 i2 : this.log4_md5) {
                if (!i2.md5_data.equals(temp_data3)) continue;
                return;
            }
        }
        this.log4_md5.add(new Request_md5(temp_data3));
        IHttpService iHttpService = baseRequestResponse.getHttpService();
        byte[] requestBytes = baseRequestResponse.getRequest();
        int bodyOffset = analyIRequestInfo.getBodyOffset();
        byte[] body = new byte[requestBytes.length - bodyOffset];
        System.arraycopy(requestBytes, bodyOffset, body, 0, requestBytes.length - bodyOffset);
        String bodyString = this.helpers.bytesToString(body);
        List<String> headers_y = analyIRequestInfo.getHeaders();
        String[] data_1_list = this.data_1.split("\n");
        if (this.chkbox_url_auth.isSelected()) {
            String[] urlAuthParams;
            String originalFirstLine = headers_y.get(0);
            String[] requestParts = originalFirstLine.split(" ");
            String authRequestMethod = requestParts[0];
            String originalPath = requestParts[1];
            String httpVersion = requestParts[2];
            String[] pathParts = originalPath.split("\\?", 2);
            String basePath = pathParts[0];
            String queryString = pathParts.length > 1 ? pathParts[1] : "";
            for (String param : urlAuthParams = this.jta_url.getText().split("\n")) {
                String[] existingBodyParams;
                String[] parts;
                if (param.trim().isEmpty() || (parts = param.split("=", 2)).length != 2) continue;
                String paramName = parts[0].trim();
                String paramValue = parts[1].trim();
                if (queryString.contains(paramName + "=")) {
                    queryString = queryString.replaceAll(paramName + "=[^&]+", paramName + "=" + paramValue);
                }
                if (!"POST".equalsIgnoreCase(authRequestMethod)) continue;
                int isFormUrlEncoded = 0;
                for (String header : headers_y) {
                    if (!header.toLowerCase().contains("content-type: application/x-www-form-urlencoded")) continue;
                    isFormUrlEncoded = 1;
                    break;
                }
                if (isFormUrlEncoded == 0) continue;
                ArrayList<String> bodyParams = new ArrayList<String>();
                for (String existingParam : existingBodyParams = bodyString.split("&")) {
                    String[] existingParts = existingParam.split("=");
                    if (existingParts.length == 2 && existingParts[0].trim().equals(paramName)) {
                        bodyParams.add(paramName + "=" + paramValue);
                        continue;
                    }
                    bodyParams.add(existingParam);
                }
                bodyString = String.join("&", bodyParams);
                body = this.helpers.stringToBytes(bodyString);
            }
            String newPath = basePath + (String)(queryString.isEmpty() ? "" : "?" + queryString);
            String firstLine = authRequestMethod + " " + newPath + " " + httpVersion;
            headers_y.set(0, firstLine);
        }
        for (int i3 = 0; i3 < headers_y.size(); ++i3) {
            String head_key = headers_y.get(i3).split(":")[0];
            for (String str2 : data_1_list) {
                if (!head_key.equals(str2.split(":")[0])) continue;
                headers_y.remove(i3);
                --i3;
            }
        }
        for (String str3 : data_1_list) {
            headers_y.add(headers_y.size() / 2, str3);
        }
        byte[] newRequest_y = this.helpers.buildHttpMessage(headers_y, body);
        IHttpRequestResponse requestResponse_y = this.callbacks.makeHttpRequest(iHttpService, newRequest_y);
        int low_len = requestResponse_y.getResponse().length - this.helpers.analyzeResponse(requestResponse_y.getResponse()).getBodyOffset();
        Object low_len_data = original_len == 0 ? Integer.toString(low_len) : (original_len == low_len ? Integer.toString(low_len) + "  \u2714" : Integer.toString(low_len) + "  ==> " + Integer.toString(original_len - low_len));
        List<String> headers_w = analyIRequestInfo.getHeaders();
        String[] data_2_list = this.data_2.split("\n");
        if (this.chkbox_url_unauth.isSelected()) {
            String[] urlParamsToRemove;
            String originalFirstLine = headers_w.get(0);
            String[] requestParts = originalFirstLine.split(" ");
            String unauthRequestMethod = requestParts[0];
            String originalPath = requestParts[1];
            String httpVersion = requestParts[2];
            String[] pathParts = originalPath.split("\\?", 2);
            String basePath = pathParts[0];
            String queryString = pathParts.length > 1 ? pathParts[1] : "";
            for (String paramName : urlParamsToRemove = this.jta_url_unauth.getText().split("\n")) {
                String[] existingBodyParams;
                if ((paramName = paramName.trim()).isEmpty()) continue;
                if (queryString.contains(paramName + "=")) {
                    queryString = queryString.replaceAll(paramName + "=[^&]+(&)?", "");
                }
                if (!"POST".equalsIgnoreCase(unauthRequestMethod)) continue;
                boolean isFormUrlEncoded = false;
                for (String header : headers_w) {
                    if (!header.toLowerCase().contains("content-type: application/x-www-form-urlencoded")) continue;
                    isFormUrlEncoded = true;
                    break;
                }
                if (!isFormUrlEncoded) continue;
                ArrayList<String> bodyParams = new ArrayList<String>();
                for (String existingParam : existingBodyParams = bodyString.split("&")) {
                    String[] existingParts = existingParam.split("=");
                    if (existingParts.length == 2 && existingParts[0].trim().equals(paramName)) continue;
                    bodyParams.add(existingParam);
                }
                bodyString = String.join("&", bodyParams);
                body = this.helpers.stringToBytes(bodyString);
            }
            String newPath = basePath + (String)((queryString = queryString.replaceAll("&&+", "&").replaceAll("&$", "")).isEmpty() ? "" : "?" + queryString);
            String firstLine = unauthRequestMethod + " " + newPath + " " + httpVersion;
            headers_w.set(0, firstLine);
        }
        for (int i4 = 0; i4 < headers_w.size(); ++i4) {
            String head_key2 = headers_w.get(i4).split(":")[0];
            for (String str4 : data_2_list) {
                if (!head_key2.equals(str4)) continue;
                headers_w.remove(i4);
                --i4;
            }
        }
        if (this.universal_cookie.length() != 0) {
            String[] universal_cookies = this.universal_cookie.split("\n");
            headers_w.add(headers_w.size() / 2, universal_cookies[0]);
            headers_w.add(headers_w.size() / 2, universal_cookies[1]);
        }
        byte[] newRequest_w = this.helpers.buildHttpMessage(headers_w, body);
        IHttpRequestResponse requestResponse_w = this.callbacks.makeHttpRequest(iHttpService, newRequest_w);
        int Unauthorized_len = requestResponse_w.getResponse().length - this.helpers.analyzeResponse(requestResponse_w.getResponse()).getBodyOffset();
        Object original_len_data = original_len == 0 ? Integer.toString(Unauthorized_len) : (original_len == Unauthorized_len ? Integer.toString(Unauthorized_len) + "  \u2714" : Integer.toString(Unauthorized_len) + "  ==> " + Integer.toString(original_len - Unauthorized_len));

        // 获取MIME类型
        String mimeType = "unknown";
        try {
            if (baseRequestResponse.getResponse() != null) {
                IResponseInfo responseInfo = this.helpers.analyzeResponse(baseRequestResponse.getResponse());
                List<String> headers = responseInfo.getHeaders();

                // 调试：打印所有响应头
                this.stdout.println("=== Response Headers for " + String.valueOf(analyIRequestInfo.getUrl()) + " ===");
                for (String header : headers) {
                    this.stdout.println("Header: " + header);
                    if (header.toLowerCase().startsWith("content-type:")) {
                        mimeType = header.substring(header.indexOf(":") + 1).trim();
                        if (mimeType.contains(";")) {
                            mimeType = mimeType.substring(0, mimeType.indexOf(";")).trim();
                        }
                        this.stdout.println("Extracted MIME type: " + mimeType);
                        break;
                    }
                }

                // 如果没有找到Content-Type头，尝试根据响应内容推断
                if (mimeType.equals("unknown")) {
                    byte[] responseBytes = baseRequestResponse.getResponse();
                    int responseBodyOffset = responseInfo.getBodyOffset();
                    if (responseBodyOffset < responseBytes.length) {
                        String responseBody = this.helpers.bytesToString(responseBytes).substring(responseBodyOffset);
                        String trimmedBody = responseBody.trim();

                        // 简单的内容类型推断
                        if (trimmedBody.startsWith("{") || trimmedBody.startsWith("[")) {
                            mimeType = "application/json (inferred)";
                        } else if (trimmedBody.startsWith("<")) {
                            mimeType = "text/html (inferred)";
                        } else {
                            mimeType = "text/plain (inferred)";
                        }
                        this.stdout.println("Inferred MIME type: " + mimeType);
                    }
                }
            }
        } catch (Exception e) {
            mimeType = "error: " + e.getMessage();
            this.stdout.println("Error getting MIME type: " + e.getMessage());
        }

        // 过滤无效记录：如果原始、未授权、越权响应长度都为0，则不添加到列表
        if (original_len == 0 && low_len == 0 && Unauthorized_len == 0) {
            this.stdout.println("[*] \u8df3\u8fc7\u65e0\u6548\u8bb0\u5f55\uff08\u6240\u6709\u54cd\u5e94\u957f\u5ea6\u90fd\u4e3a0\uff09\uff1a" + this.temp_data);
            return;
        }

        ++this.conut;
        int id = this.conut;
        LogEntry newEntry = new LogEntry(id, requestMethod, this.callbacks.saveBuffersToTempFiles(baseRequestResponse), this.callbacks.saveBuffersToTempFiles(requestResponse_y), this.callbacks.saveBuffersToTempFiles(requestResponse_w), String.valueOf(analyIRequestInfo.getUrl()), mimeType, original_len, (String)low_len_data, (String)original_len_data);
        this.log.add(newEntry);
        this.originalLog.add(newEntry); // 同时添加到备份列表
        this.fireTableDataChanged();
        this.logTable.setRowSelectionInterval(this.select_row, this.select_row);
    }

    @Override
    public List<IScanIssue> doActiveScan(IHttpRequestResponse baseRequestResponse, IScannerInsertionPoint insertionPoint) {
        return null;
    }

    @Override
    public int consolidateDuplicateIssues(IScanIssue existingIssue, IScanIssue newIssue) {
        if (existingIssue.getIssueName().equals(newIssue.getIssueName())) {
            return -1;
        }
        return 0;
    }

    @Override
    public int getRowCount() {
        if (this.chkbox_show_vuln_only == null || !this.chkbox_show_vuln_only.isSelected()) {
            return this.log.size();
        }
        int count = 0;
        for (LogEntry entry : this.log) {
            if (!entry.Unauthorized_len.contains("\u2714") && !entry.low_len.contains("\u2714")) continue;
            ++count;
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return "#";
            }
            case 1: {
                return "\u7c7b\u578b";
            }
            case 2: {
                return "URL";
            }
            case 3: {
                return "MIME\u7c7b\u578b";
            }
            case 4: {
                return "\u539f\u59cb\u5305\u957f\u5ea6";
            }
            case 5: {
                return "\u4f4e\u6743\u9650\u5305\u957f\u5ea6";
            }
            case 6: {
                return "\u672a\u6388\u6743\u5305\u957f\u5ea6";
            }
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: // ID列
                return Integer.class;
            case 1: // 方法列
                return String.class;
            case 2: // URL列
                return String.class;
            case 3: // MIME类型列
                return String.class;
            case 4: // 原始数据包长度列
                return Integer.class;
            case 5: // 低权限数据包长度列
                return String.class; // 可能包含特殊符号如✓
            case 6: // 未授权数据包长度列
                return String.class; // 可能包含特殊符号如✓
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (this.chkbox_show_vuln_only == null || !this.chkbox_show_vuln_only.isSelected()) {
            LogEntry logEntry = this.log.get(rowIndex);
            return this.getLogEntryValue(logEntry, columnIndex);
        }
        ArrayList<LogEntry> vulnEntries = new ArrayList<LogEntry>();
        for (LogEntry entry : this.log) {
            if (!entry.Unauthorized_len.contains("\u2714") && !entry.low_len.contains("\u2714")) continue;
            vulnEntries.add(entry);
        }
        LogEntry logEntry = (LogEntry)vulnEntries.get(rowIndex);
        return this.getLogEntryValue(logEntry, columnIndex);
    }

    private Object getLogEntryValue(LogEntry logEntry, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return logEntry.id;
            }
            case 1: {
                return logEntry.Method;
            }
            case 2: {
                return logEntry.url;
            }
            case 3: {
                return logEntry.mimeType;
            }
            case 4: {
                return logEntry.original_len;
            }
            case 5: {
                return logEntry.low_len;
            }
            case 6: {
                return logEntry.Unauthorized_len;
            }
        }
        return "";
    }

    @Override
    public byte[] getRequest() {
        return this.currentlyDisplayedItem.getRequest();
    }

    @Override
    public byte[] getResponse() {
        return this.currentlyDisplayedItem.getResponse();
    }

    @Override
    public IHttpService getHttpService() {
        return this.currentlyDisplayedItem.getHttpService();
    }

    public static String MD5(String key) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = key.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                int i = k;
                int k2 = k + 1;
                str[i] = hexDigits[byte0 >>> 4 & 0xF];
                k = k2 + 1;
                str[k2] = hexDigits[byte0 & 0xF];
            }
            return new String(str);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<JMenuItem> createMenuItems(final IContextMenuInvocation invocation) {
        ArrayList<JMenuItem> menuItems = new ArrayList<JMenuItem>();

        // 检查调用上下文
        byte context = invocation.getInvocationContext();
        IHttpRequestResponse[] messages = invocation.getSelectedMessages();

        // 如果是在我们的插件表格中右键
        if (context == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_REQUEST ||
            context == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_RESPONSE ||
            context == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST ||
            context == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE) {

            // 添加发送到其他工具的菜单项
            if (messages != null && messages.length > 0) {
                JMenuItem sendToRepeater = new JMenuItem("Send to Repeater");
                sendToRepeater.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (IHttpRequestResponse message : messages) {
                            BurpExtender.this.callbacks.sendToRepeater(
                                message.getHttpService().getHost(),
                                message.getHttpService().getPort(),
                                message.getHttpService().getProtocol().equals("https"),
                                message.getRequest(),
                                "tomato_yue"
                            );
                        }
                    }
                });
                menuItems.add(sendToRepeater);

                JMenuItem sendToIntruder = new JMenuItem("Send to Intruder");
                sendToIntruder.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (IHttpRequestResponse message : messages) {
                            BurpExtender.this.callbacks.sendToIntruder(
                                message.getHttpService().getHost(),
                                message.getHttpService().getPort(),
                                message.getHttpService().getProtocol().equals("https"),
                                message.getRequest()
                            );
                        }
                    }
                });
                menuItems.add(sendToIntruder);

                JMenuItem sendToComparer = new JMenuItem("Send to Comparer");
                sendToComparer.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (IHttpRequestResponse message : messages) {
                            BurpExtender.this.callbacks.sendToComparer(message.getRequest());
                            if (message.getResponse() != null) {
                                BurpExtender.this.callbacks.sendToComparer(message.getResponse());
                            }
                        }
                    }
                });
                menuItems.add(sendToComparer);

                // 添加分隔符
                menuItems.add(new JMenuItem("---"));

                // 添加移除选中项的菜单
                JMenuItem removeSelected = new JMenuItem("删除选中");
                removeSelected.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BurpExtender.this.removeSelectedEntries();
                    }
                });
                menuItems.add(removeSelected);

                menuItems.add(new JMenuItem("---"));
            }
        }

        JMenuItem sendToXiaYue = new JMenuItem("send to tomato_yue");
        sendToXiaYue.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                IHttpRequestResponse[] messages = invocation.getSelectedMessages();
                if (messages != null && messages.length > 0) {
                    if (BurpExtender.this.switchs == 1) {
                        for (IHttpRequestResponse message : messages) {
                            IRequestInfo requestInfo = BurpExtender.this.helpers.analyzeRequest(message);
                            String method = requestInfo.getMethod();
                            if ("OPTIONS".equalsIgnoreCase(method)) {
                                BurpExtender.this.stdout.println("[*] \u8df3\u8fc7 OPTIONS \u8bf7\u6c42\uff0c\u4e0d\u8fdb\u884c\u8d8a\u6743\u6d4b\u8bd5");
                                continue;
                            }
                            new Thread(() -> {
                                try {
                                    BurpExtender.this.checkVul(message, 4);
                                }
                                catch (Exception ex) {
                                    BurpExtender.this.stdout.println("\u5904\u7406\u8bf7\u6c42\u65f6\u751f\u9519\u8bef: " + ex.getMessage());
                                    ex.printStackTrace(BurpExtender.this.stdout);
                                }
                            }).start();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "\u8bf7\u5148\u542f\u52a8\u63d2\u4ef6\uff01", "tomato_yue\u63d0\u793a", 2);
                    }
                }
            }
        });
        JMenuItem extractAuth = new JMenuItem("set Cookie");
        extractAuth.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                IHttpRequestResponse[] messages = invocation.getSelectedMessages();
                if (messages != null && messages.length > 0) {
                    IHttpRequestResponse message = messages[0];
                    IRequestInfo requestInfo = BurpExtender.this.helpers.analyzeRequest(message);
                    List<String> headers = requestInfo.getHeaders();
                    StringBuilder authInfo = new StringBuilder();
                    StringBuilder authFields = new StringBuilder();
                    for (String header : headers) {
                        if ((header = header.trim()).startsWith("POST ") || header.startsWith("GET ") || header.startsWith("PUT ") || header.startsWith("DELETE ") || !header.toLowerCase().startsWith("cookie:") && !header.toLowerCase().startsWith("authorization:") && !header.toLowerCase().startsWith("token:")) continue;
                        authInfo.append(header).append("\n");
                        String fieldName = header.split(":")[0].trim();
                        if (authFields.toString().contains(fieldName)) continue;
                        authFields.append(fieldName).append("\n");
                    }
                    if (authInfo.length() > 0) {
                        final String finalAuthInfo = authInfo.toString().trim();
                        final String finalAuthFields = authFields.toString().trim();
                        SwingUtilities.invokeLater(new Runnable(){

                            @Override
                            public void run() {
                                BurpExtender.this.jta.setText(finalAuthInfo);
                                BurpExtender.this.jta_1.setText(finalAuthFields);
                            }
                        });
                        JOptionPane.showMessageDialog(null, "\u5df2\u6210\u529f\u63d0\u53d6\u8ba4\u8bc1\u4fe1\u606f", "tomato_yue\u63d0\u793a", 1);
                    } else {
                        JOptionPane.showMessageDialog(null, "\u672a\u627e\u5230\u8ba4\u8bc1\u4fe1\u606f", "tomato_yue\u63d0\u793a", 2);
                    }
                }
            }
        });
        menuItems.add(sendToXiaYue);
        menuItems.add(extractAuth);
        return menuItems;
    }

    // 导出请求的方法
    private void exportRequests(boolean exportUnauth) {
        List<LogEntry> targetEntries = new ArrayList<LogEntry>();

        // 根据参数决定导出哪种类型的请求
        for (LogEntry entry : this.log) {
            if (exportUnauth) {
                // 导出未授权请求（未授权包长度列包含✔）
                if (entry.Unauthorized_len.contains("✔")) {
                    targetEntries.add(entry);
                }
            } else {
                // 导出越权请求（低权限包长度列包含✔）
                if (entry.low_len.contains("✔")) {
                    targetEntries.add(entry);
                }
            }
        }

        if (targetEntries.isEmpty()) {
            String message = exportUnauth ? "未发现存在未授权的请求" : "未发现存在越权的请求";
            JOptionPane.showMessageDialog(null, message, "番茄提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 选择保存文件
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存导出文件");
        String defaultFileName = exportUnauth ? "unauthorized_requests.txt" : "privilege_escalation_requests.txt";
        fileChooser.setSelectedFile(new File(defaultFileName));

        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                exportToFile(targetEntries, file);
                String message = String.format("成功导出 %d 个请求到文件: %s", targetEntries.size(), file.getAbsolutePath());
                JOptionPane.showMessageDialog(null, message, "番茄提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "导出失败: " + e.getMessage(), "番茄提示", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 将请求导出到文件
    private void exportToFile(List<LogEntry> entries, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        try {
            for (LogEntry entry : entries) {
                // 写入原始请求
                String request = this.helpers.bytesToString(entry.requestResponse.getRequest());
                writer.write(request);
                writer.write("\n\n");
            }
        } finally {
            writer.close();
        }
    }

    // 移除选中的数据条目
    private void removeSelectedEntries() {
        int[] selectedRows = this.logTable.getSelectedRows();
        if (selectedRows.length == 0) {
            this.stdout.println("[*] 请先选择要移除的数据行");
            return;
        }

        // 直接删除，不需要确认弹窗
            // 转换表格行索引为模型行索引（考虑排序）
            List<Integer> modelRows = new ArrayList<Integer>();
            for (int viewRow : selectedRows) {
                int modelRow = this.logTable.convertRowIndexToModel(viewRow);
                modelRows.add(modelRow);
            }

            // 按降序排序，从后往前删除
            Collections.sort(modelRows, Collections.reverseOrder());

            // 如果显示的是过滤后的数据，需要特殊处理
            if (this.chkbox_show_vuln_only.isSelected()) {
                // 获取过滤后的条目列表
                List<LogEntry> vulnEntries = new ArrayList<LogEntry>();
                for (LogEntry entry : this.log) {
                    if (entry.Unauthorized_len.contains("✔") || entry.low_len.contains("✔")) {
                        vulnEntries.add(entry);
                    }
                }

                // 从原始列表中移除对应的条目
                for (int modelRow : modelRows) {
                    if (modelRow < vulnEntries.size()) {
                        LogEntry entryToRemove = vulnEntries.get(modelRow);
                        this.log.remove(entryToRemove);
                        this.originalLog.remove(entryToRemove);
                    }
                }
            } else {
                // 直接从列表中移除
                for (int modelRow : modelRows) {
                    if (modelRow < this.log.size()) {
                        LogEntry entryToRemove = this.log.get(modelRow);
                        this.log.remove(modelRow);
                        this.originalLog.remove(entryToRemove);
                    }
                }
            }

            // 刷新表格
            this.fireTableDataChanged();

            this.stdout.println(String.format("[*] 已成功移除 %d 条数据", selectedRows.length));
    }

    public static class LogEntry {
        final int id;
        final String Method;
        final IHttpRequestResponsePersisted requestResponse;
        final IHttpRequestResponsePersisted requestResponse_1;
        final IHttpRequestResponsePersisted requestResponse_2;
        final String url;
        final String mimeType;
        final int original_len;
        final String low_len;
        final String Unauthorized_len;

        LogEntry(int id, String Method, IHttpRequestResponsePersisted requestResponse, IHttpRequestResponsePersisted requestResponse_1, IHttpRequestResponsePersisted requestResponse_2, String url, String mimeType, int original_len, String low_len, String Unauthorized_len) {
            this.id = id;
            this.Method = Method;
            this.requestResponse = requestResponse;
            this.requestResponse_1 = requestResponse_1;
            this.requestResponse_2 = requestResponse_2;
            this.url = url;
            this.mimeType = mimeType;
            this.original_len = original_len;
            this.low_len = low_len;
            this.Unauthorized_len = Unauthorized_len;
        }
    }

    public static class Request_md5 {
        final String md5_data;

        Request_md5(String md5_data) {
            this.md5_data = md5_data;
        }
    }

    public class Table
    extends JTable {
        public Table(TableModel tableModel) {
            super(tableModel);

            // 添加右键菜单支持
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showContextMenu(e);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showContextMenu(e);
                    }
                }

                private void showContextMenu(MouseEvent e) {
                    int row = Table.this.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        // 如果右键的行没有被选中，则选中它
                        if (!Table.this.isRowSelected(row)) {
                            Table.this.setRowSelectionInterval(row, row);
                        }

                        // 创建包含Burp原生扩展支持的右键菜单
                        JPopupMenu popup = createSimpleContextMenu();
                        popup.show(Table.this, e.getX(), e.getY());
                    }
                }
            });
        }







        // 创建简化的右键菜单（只包含我们自己的功能）
        private JPopupMenu createSimpleContextMenu() {
            JPopupMenu popup = new JPopupMenu();
            int[] selectedRows = this.getSelectedRows();

            // 只有在有选中项时才显示发送到工具的菜单
            if (selectedRows.length > 0) {
                JMenuItem sendToRepeater = new JMenuItem("发送到Repeater");
                sendToRepeater.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendSelectedToRepeater();
                    }
                });
                popup.add(sendToRepeater);

                JMenuItem sendToIntruder = new JMenuItem("发送到Intruder");
                sendToIntruder.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendSelectedToIntruder();
                    }
                });
                popup.add(sendToIntruder);

                JMenuItem sendToComparer = new JMenuItem("发送到Comparer");
                sendToComparer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendSelectedToComparer();
                    }
                });
                popup.add(sendToComparer);

                popup.addSeparator();

                // 移除选中数据
                JMenuItem removeSelected = new JMenuItem("移除选中条目");
                removeSelected.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BurpExtender.this.removeSelectedEntries();
                    }
                });
                popup.add(removeSelected);

                popup.addSeparator();
            }

            // 导出功能子菜单
            JMenu exportMenu = new JMenu("导出");

            JMenuItem exportUnauth = new JMenuItem("导出未授权请求");
            exportUnauth.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exportUnauthRequests();
                }
            });
            exportMenu.add(exportUnauth);

            JMenuItem exportPrivEsc = new JMenuItem("导出越权请求");
            exportPrivEsc.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exportPrivEscRequests();
                }
            });
            exportMenu.add(exportPrivEsc);

            popup.add(exportMenu);

            // 复制功能
            JMenuItem copyVulnUrls = new JMenuItem("复制所有存在越权的URL");
            copyVulnUrls.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    copyAllVulnerableUrls();
                }
            });
            popup.add(copyVulnUrls);

            popup.addSeparator();

            // 去重功能
            JMenuItem deduplicateEntries = new JMenuItem("去重");
            deduplicateEntries.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deduplicateLogEntries();
                }
            });
            popup.add(deduplicateEntries);

            // 清空列表
            JMenuItem clearList = new JMenuItem("清空列表");
            clearList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearAllEntries();
                }
            });
            popup.add(clearList);

            return popup;
        }



        private void sendSelectedToRepeater() {
            int[] selectedRows = this.getSelectedRows();
            for (int viewRow : selectedRows) {
                LogEntry entry = getLogEntryFromRow(viewRow);
                if (entry != null) {
                    BurpExtender.this.callbacks.sendToRepeater(
                        entry.requestResponse.getHttpService().getHost(),
                        entry.requestResponse.getHttpService().getPort(),
                        entry.requestResponse.getHttpService().getProtocol().equals("https"),
                        entry.requestResponse.getRequest(),
                        "tomato_yue_" + entry.id
                    );
                }
            }
        }

        private void sendSelectedToIntruder() {
            int[] selectedRows = this.getSelectedRows();
            for (int viewRow : selectedRows) {
                LogEntry entry = getLogEntryFromRow(viewRow);
                if (entry != null) {
                    BurpExtender.this.callbacks.sendToIntruder(
                        entry.requestResponse.getHttpService().getHost(),
                        entry.requestResponse.getHttpService().getPort(),
                        entry.requestResponse.getHttpService().getProtocol().equals("https"),
                        entry.requestResponse.getRequest()
                    );
                }
            }
        }

        private void sendSelectedToComparer() {
            int[] selectedRows = this.getSelectedRows();
            for (int viewRow : selectedRows) {
                LogEntry entry = getLogEntryFromRow(viewRow);
                if (entry != null) {
                    BurpExtender.this.callbacks.sendToComparer(entry.requestResponse.getRequest());
                    if (entry.requestResponse.getResponse() != null) {
                        BurpExtender.this.callbacks.sendToComparer(entry.requestResponse.getResponse());
                    }
                }
            }
        }

        private LogEntry getLogEntryFromRow(int viewRow) {
            int modelRow = this.convertRowIndexToModel(viewRow);
            if (BurpExtender.this.chkbox_show_vuln_only.isSelected()) {
                List<LogEntry> vulnEntries = new ArrayList<LogEntry>();
                for (LogEntry entry : BurpExtender.this.log) {
                    if (entry.Unauthorized_len.contains("✔") || entry.low_len.contains("✔")) {
                        vulnEntries.add(entry);
                    }
                }
                if (modelRow < vulnEntries.size()) {
                    return vulnEntries.get(modelRow);
                }
            } else {
                if (modelRow < BurpExtender.this.log.size()) {
                    return BurpExtender.this.log.get(modelRow);
                }
            }
            return null;
        }

        @Override
        public void changeSelection(int row, int col, boolean toggle, boolean extend) {
            // 将视图行索引转换为模型行索引（处理排序）
            int modelRow = row;
            if (this.getRowSorter() != null) {
                modelRow = this.convertRowIndexToModel(row);
            }

            LogEntry logEntry;
            if (BurpExtender.this.chkbox_show_vuln_only.isSelected()) {
                ArrayList<LogEntry> vulnEntries = new ArrayList<LogEntry>();
                for (LogEntry entry : BurpExtender.this.log) {
                    if (!entry.Unauthorized_len.contains("\u2714") && !entry.low_len.contains("\u2714")) continue;
                    vulnEntries.add(entry);
                }
                logEntry = (LogEntry)vulnEntries.get(modelRow);
            } else {
                logEntry = BurpExtender.this.log.get(modelRow);
            }
            BurpExtender.this.select_row = row;
            if (col == 5) {
                BurpExtender.this.tabs.setSelectedIndex(1);
            } else if (col == 6) {
                BurpExtender.this.tabs.setSelectedIndex(2);
            } else if (col == 4) {
                BurpExtender.this.tabs.setSelectedIndex(0);
            }
            BurpExtender.this.requestViewer.setMessage(logEntry.requestResponse.getRequest(), true);
            BurpExtender.this.responseViewer.setMessage(logEntry.requestResponse.getResponse(), false);
            BurpExtender.this.currentlyDisplayedItem = logEntry.requestResponse;
            BurpExtender.this.requestViewer_1.setMessage(logEntry.requestResponse_1.getRequest(), true);
            BurpExtender.this.responseViewer_1.setMessage(logEntry.requestResponse_1.getResponse(), false);
            BurpExtender.this.currentlyDisplayedItem_1 = logEntry.requestResponse_1;
            BurpExtender.this.requestViewer_2.setMessage(logEntry.requestResponse_2.getRequest(), true);
            BurpExtender.this.responseViewer_2.setMessage(logEntry.requestResponse_2.getResponse(), false);
            BurpExtender.this.currentlyDisplayedItem_2 = logEntry.requestResponse_2;
            // 更新对比视图 - 默认显示原始视图 vs 低权限视图
            BurpExtender.this.leftReqViewer.setMessage(logEntry.requestResponse.getRequest(), true);
            BurpExtender.this.rightReqViewer.setMessage(logEntry.requestResponse_1.getRequest(), true);
            BurpExtender.this.leftRespViewer.setMessage(logEntry.requestResponse.getResponse(), false);
            BurpExtender.this.rightRespViewer.setMessage(logEntry.requestResponse_1.getResponse(), false);

            // 更新第三个视图（未授权视图）- 确保在切换数据包时实时更新
            if (BurpExtender.this.thirdReqViewer != null && BurpExtender.this.thirdRespViewer != null) {
                BurpExtender.this.thirdReqViewer.setMessage(logEntry.requestResponse_2.getRequest(), true);
                BurpExtender.this.thirdRespViewer.setMessage(logEntry.requestResponse_2.getResponse(), false);
            }

            super.changeSelection(row, col, toggle, extend);
        }
    }

    // 新增的右键菜单功能实现
    private void copyAllVulnerableUrls() {
        StringBuilder vulnUrls = new StringBuilder();
        for (LogEntry entry : this.log) {
            if (entry.Unauthorized_len.contains("✔") || entry.low_len.contains("✔")) {
                vulnUrls.append(entry.url).append("\n");
            }
        }

        if (vulnUrls.length() > 0) {
            StringSelection stringSelection = new StringSelection(vulnUrls.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null,
                "已复制 " + vulnUrls.toString().split("\n").length + " 个存在越权的URL到剪贴板",
                "番茄提示",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                "未发现存在漏洞的URL",
                "番茄提示",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportUnauthRequests() {
        List<LogEntry> unauthEntries = new ArrayList<LogEntry>();
        for (LogEntry entry : this.log) {
            if (entry.Unauthorized_len.contains("✔")) {
                unauthEntries.add(entry);
            }
        }

        if (unauthEntries.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "没有找到未授权请求",
                "番茄提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出未授权请求");
        fileChooser.setSelectedFile(new File("unauth_requests_" +
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                exportToFile(unauthEntries, fileChooser.getSelectedFile());
                JOptionPane.showMessageDialog(null,
                    "成功导出 " + unauthEntries.size() + " 个未授权请求",
                    "番茄提示",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    "导出失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportPrivEscRequests() {
        List<LogEntry> privEscEntries = new ArrayList<LogEntry>();
        for (LogEntry entry : this.log) {
            if (entry.low_len.contains("✔")) {
                privEscEntries.add(entry);
            }
        }

        if (privEscEntries.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "没有找到越权请求",
                "番茄提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出越权请求");
        fileChooser.setSelectedFile(new File("privesc_requests_" +
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                exportToFile(privEscEntries, fileChooser.getSelectedFile());
                JOptionPane.showMessageDialog(null,
                    "成功导出 " + privEscEntries.size() + " 个越权请求",
                    "番茄提示",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    "导出失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 去重功能 - 根据URL和请求方法去重
    private void deduplicateLogEntries() {
        if (this.log.isEmpty()) {
            this.stdout.println("[*] 列表为空，无需去重");
            return;
        }

        int originalSize = this.log.size();

        // 使用LinkedHashSet保持插入顺序，同时去重
        Set<String> seenEntries = new LinkedHashSet<String>();
        List<LogEntry> uniqueEntries = new ArrayList<LogEntry>();

        for (LogEntry entry : this.log) {
            // 创建唯一标识：URL + 请求方法
            String uniqueKey = entry.url + "|" + entry.Method;

            if (!seenEntries.contains(uniqueKey)) {
                seenEntries.add(uniqueKey);
                uniqueEntries.add(entry);
            }
        }

        // 更新列表
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BurpExtender.this.log.clear();
                BurpExtender.this.log.addAll(uniqueEntries);

                // 同时更新备份列表
                BurpExtender.this.originalLog.clear();
                BurpExtender.this.originalLog.addAll(uniqueEntries);

                BurpExtender.this.fireTableDataChanged();

                int removedCount = originalSize - uniqueEntries.size();
                BurpExtender.this.stdout.println(String.format("[*] 去重完成，移除 %d 条重复记录，保留 %d 条唯一记录",
                    removedCount, uniqueEntries.size()));
            }
        });
    }

    private void clearAllEntries() {
        // 直接清空，不需要确认弹窗
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int clearedCount = BurpExtender.this.log.size();
                BurpExtender.this.log.clear();
                BurpExtender.this.originalLog.clear();
                BurpExtender.this.log4_md5.clear();
                BurpExtender.this.conut = 0;
                BurpExtender.this.fireTableDataChanged();
                BurpExtender.this.stdout.println(String.format("[*] 已清空所有数据，共 %d 条记录", clearedCount));
            }
        });
    }
}

