package burp;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IContextMenuFactory;
import burp.IContextMenuInvocation;
import burp.IExtensionHelpers;
import burp.IHttpRequestResponse;
import burp.IHttpRequestResponsePersisted;
import burp.IHttpService;
import burp.IMessageEditor;
import burp.IMessageEditorController;
import burp.IParameter;
import burp.IRequestInfo;
import burp.ITab;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BurpExtender
extends AbstractTableModel
implements IBurpExtender,
ITab,
IMessageEditorController,
IContextMenuFactory {
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private JSplitPane splitPane;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private JTable originalTable;
    private JTable resultTable;
    private List<OriginalLogEntry> originalLog = new ArrayList<OriginalLogEntry>();
    private List<TestLogEntry> testLog = new ArrayList<TestLogEntry>();
    private JButton clearListButton;
    private JButton whiteListButton;
    private JTextField whiteListField;
    private JTextField customPhoneField;
    private JButton modifyPhoneButton;
    private String customPhoneNumber = "18888888888";
    private JTextField customSmsContentField;
    private JButton modifySmsContentButton;
    private String customSmsContentParam = "";
    private boolean smsContentTestEnabled = false;
    private JButton smsInterfaceTestButton;
    private JButton combineTestButton;
    private boolean smsInterfaceTestEnabled = false;
    private boolean combineTestEnabled = false;
    private IHttpRequestResponse currentlyDisplayedItem;
    private int selectedOriginalId = -1;
    private int originalCounter = 0;
    private int testCounter = 0;
    public PrintWriter stdout;
    private boolean whiteListEnabled = false;
    private String whiteListDomains = "";
    private List<String> selectedNumbers = Collections.emptyList();
    private Map<Integer, AtomicBoolean> cancelFlags = new ConcurrentHashMap<Integer, AtomicBoolean>();
    private static final String[] PAYLOAD_PATTERNS = new String[]{"xxxxxxxxxxx", "xxxxxxxxxxx,", "xxxxxxxxxxx,,", "xxxxxxxxxxx,,,", "xxxxxxxxxxx,,,,", "xxxxxxxxxxx,,,,,", ",,,,,xxxxxxxxxxx", ",,,,xxxxxxxxxxx", ",,,xxxxxxxxxxx", ",,xxxxxxxxxxx", ",xxxxxxxxxxx", " xxxxxxxxxxx", "  xxxxxxxxxxx", "   xxxxxxxxxxx", "%20xxxxxxxxxxx", "%20%20xxxxxxxxxxx", "%20%20%20xxxxxxxxxxx", "xxxxxxxxxxx ", "xxxxxxxxxxx  ", "xxxxxxxxxxx   ", "xxxxxxxxxxx%20", "xxxxxxxxxxx%20%20", "xxxxxxxxxxx%20%20%20", "@xxxxxxxxxxx", "@@xxxxxxxxxxx", "@@@xxxxxxxxxxx", "xxxxxxxxxxx@", "xxxxxxxxxxx@@", "xxxxxxxxxxx@@@", "xxxxxxxxxxx#", "xxxxxxxxxxx##", "xxxxxxxxxxx###", "xxxxxxxxxxx####", "#xxxxxxxxxxx", "##xxxxxxxxxxx", "###xxxxxxxxxxx", "####xxxxxxxxxxx", "xxxxxxxxxxx?", "xxxxxxxxxxx??", "xxxxxxxxxxx???", "xxxxxxxxxxx????", "?xxxxxxxxxxx", "??xxxxxxxxxxx", "???xxxxxxxxxxx", "????xxxxxxxxxxx", "xxxxxxxxxxx!", "xxxxxxxxxxx!!", "xxxxxxxxxxx!!!", "xxxxxxxxxxx!!!!", "!xxxxxxxxxxx", "!!xxxxxxxxxxx", "!!!xxxxxxxxxxx", "!!!!xxxxxxxxxxx", "xxxxxxxxxxx.", "xxxxxxxxxxx..", "xxxxxxxxxxx...", "xxxxxxxxxxx....", ".xxxxxxxxxxx", "..xxxxxxxxxxx", "...xxxxxxxxxxx", "....xxxxxxxxxxx", "%00xxxxxxxxxxx", "%00%00xxxxxxxxxxx", "%00%00%00xxxxxxxxxxx", "xxxxxxxxxxx%00", "xxxxxxxxxxx%00%00", "xxxxxxxxxxx%00%00%00", "xxxxxxxxxxx\\n", "xxxxxxxxxxx\\n\\n", "xxxxxxxxxxx\\n\\n\\n", "xxxxxxxxxxx\\n\\n\\n\\n", "\\nxxxxxxxxxxx", "\\n\\nxxxxxxxxxxx", "\\n\\n\\nxxxxxxxxxxx", "\\n\\n\\n\\nxxxxxxxxxxx", "xxxxxxxxxxx\\r", "xxxxxxxxxxx\\r\\r", "xxxxxxxxxxx\\r\\r\\r", "xxxxxxxxxxx\\r\\r\\r\\r", "\\rxxxxxxxxxxx", "\\r\\rxxxxxxxxxxx", "\\r\\r\\rxxxxxxxxxxx", "\\r\\r\\r\\rxxxxxxxxxxx", "xxxxxxxxxxx+", "xxxxxxxxxxx++", "xxxxxxxxxxx+++", "xxxxxxxxxxx++++", "+xxxxxxxxxxx", "++xxxxxxxxxxx", "+++xxxxxxxxxxx", "++++xxxxxxxxxxx", "xxxxxxxxxxx-", "xxxxxxxxxxx--", "xxxxxxxxxxx---", "xxxxxxxxxxx----", "-xxxxxxxxxxx", "--xxxxxxxxxxx", "---xxxxxxxxxxx", "----xxxxxxxxxxx", "xxxxxxxxxxx*", "xxxxxxxxxxx**", "xxxxxxxxxxx***", "xxxxxxxxxxx****", "*xxxxxxxxxxx", "**xxxxxxxxxxx", "***xxxxxxxxxxx", "****xxxxxxxxxxx", "xxxxxxxxxxx/", "xxxxxxxxxxx//", "xxxxxxxxxxx///", "xxxxxxxxxxx////", "/xxxxxxxxxxx", "//xxxxxxxxxxx", "///xxxxxxxxxxx", "////xxxxxxxxxxx", "+86xxxxxxxxxxx", "+86 xxxxxxxxxxx", "+86%20xxxxxxxxxxx", "+12xxxxxxxxxxx", "+12 xxxxxxxxxxx", "+12%20xxxxxxxxxxx", "+852xxxxxxxxxxx", "+852 xxxxxxxxxxx", "+852%20xxxxxxxxxxx", "+853xxxxxxxxxxx", "+853 xxxxxxxxxxx", "+853%20xxxxxxxxxxx", "0086xxxxxxxxxxx", "0086 xxxxxxxxxxx", "0086%20xxxxxxxxxxx", "0012xxxxxxxxxxx", "0012 xxxxxxxxxxx", "0012%20xxxxxxxxxxx", "00852xxxxxxxxxxx", "00852 xxxxxxxxxxx", "00852%20xxxxxxxxxxx", "00853xxxxxxxxxxx", "00853 xxxxxxxxxxx", "00853%20xxxxxxxxxxx", "9986xxxxxxxxxxx", "9986 xxxxxxxxxxx", "9986%20xxxxxxxxxxx", "9912xxxxxxxxxxx", "9912 xxxxxxxxxxx", "9912%20xxxxxxxxxxx", "99852xxxxxxxxxxx", "99852 xxxxxxxxxxx", "99852%20xxxxxxxxxxx", "99853xxxxxxxxxxx", "99853 xxxxxxxxxxx", "99853%20xxxxxxxxxxx", "86xxxxxxxxxxx", "86 xxxxxxxxxxx", "86%20xxxxxxxxxxx", "12xxxxxxxxxxx", "12 xxxxxxxxxxx", "12%20xxxxxxxxxxx", "852xxxxxxxxxxx", "852 xxxxxxxxxxx", "852%20xxxxxxxxxxx", "853xxxxxxxxxxx", "853 xxxxxxxxxxx", "853%20xxxxxxxxxxx", "086xxxxxxxxxxx", "086 xxxxxxxxxxx", "086%20xxxxxxxxxxx", "012xxxxxxxxxxx", "012 xxxxxxxxxxx", "012%20xxxxxxxxxxx", "0852xxxxxxxxxxx", "0852 xxxxxxxxxxx", "0852%20xxxxxxxxxxx", "0853xxxxxxxxxxx", "0853 xxxxxxxxxxx", "0853%20xxxxxxxxxxx", "%86xxxxxxxxxxx", "%86 xxxxxxxxxxx", "%86%2%xxxxxxxxxxx", "%12xxxxxxxxxxx", "%12 xxxxxxxxxxx", "%12%2%xxxxxxxxxxx", "%852xxxxxxxxxxx", "%852 xxxxxxxxxxx", "%852%2%xxxxxxxxxxx", "%853xxxxxxxxxxx", "%853 xxxxxxxxxxx", "%853%2%xxxxxxxxxxx", " 0xxxxxxxxxxx", "%200xxxxxxxxxxx", "0xxxxxxxxxxx", "00xxxxxxxxxxx", "000xxxxxxxxxxx", "0000xxxxxxxxxxx", "00000xxxxxxxxxxx", "+)WAFXR#!Txxxxxxxxxxx", "xxxxxxxxxxx+)WAFXR#!T", "xxxxxxxxxxx.js", "xxxxxxxxxxx.json", "xxxxxxxxxxx.html", "xxxxxxxxxxx.css", "xxxxxxxxxxx.jpg", "xxxxxxxxxxx.png", "xxxxxxxxxxx.icon", "xxxxxxxxxxx.txt", "xxxxxxxxxxx.mp3", "xxxxxxxxxxx.mp4", "xxxxxxxxxxx.0", "xxxxxxxxxxx.1", "xxxxxxxxxxx.2", "xxxxxxxxxxx.3", "xxxxxxxxxxx,18888888888", "xxxxxxxxxxx,,18888888888", "xxxxxxxxxxx,,,18888888888", "xxxxxxxxxxx&18888888888", "xxxxxxxxxxx&&18888888888", "xxxxxxxxxxx&&&18888888888", "xxxxxxxxxxx&&&&18888888888"};

    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks) {
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stdout.println("[+] ####################################");
        this.stdout.println("[+] SMS Tomato Fuzzer!");
        this.stdout.println("[+] Version:4.1");
        this.stdout.println("[+] Author:Tomato");
        this.stdout.println("[+] Github:https://github.com/sh2493770457/Python3");
        this.stdout.println("[+] ####################################");
        this.stdout.println("[+] Smile Tomato!");
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        callbacks.setExtensionName("SMS Tomato Fuzzer");
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                BurpExtender.this.requestViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.responseViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                BurpExtender.this.originalTable = new JTable(BurpExtender.this);
                BurpExtender.this.resultTable = new JTable(new ResultTableModel());
                TableRowSorter<ResultTableModel> sorter = new TableRowSorter<ResultTableModel>((ResultTableModel)BurpExtender.this.resultTable.getModel());
                sorter.setComparator(2, Comparator.comparingInt(o -> (Integer)o));
                sorter.setComparator(3, Comparator.comparingInt(o -> (Integer)o));
                sorter.setComparator(4, Comparator.comparingInt(o -> (Integer)o));
                BurpExtender.this.resultTable.setRowSorter(sorter);

                // 添加表格复制功能
                BurpExtender.this.setupTableCopyFeature();
                BurpExtender.this.originalTable.getSelectionModel().addListSelectionListener(e -> {
                    int selectedRow = BurpExtender.this.originalTable.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < BurpExtender.this.originalLog.size()) {
                        BurpExtender.this.selectedOriginalId = ((OriginalLogEntry)((BurpExtender)BurpExtender.this).originalLog.get((int)selectedRow)).id;
                        ((ResultTableModel)BurpExtender.this.resultTable.getModel()).setFilter(BurpExtender.this.selectedOriginalId);
                        sorter.setSortKeys(null);
                        OriginalLogEntry entry = (OriginalLogEntry)BurpExtender.this.originalLog.get(selectedRow);
                        BurpExtender.this.requestViewer.setMessage(entry.requestResponse.getRequest(), true);
                        BurpExtender.this.responseViewer.setMessage(entry.requestResponse.getResponse(), false);
                        BurpExtender.this.currentlyDisplayedItem = entry.requestResponse;
                    }
                });
                BurpExtender.this.resultTable.getSelectionModel().addListSelectionListener(e -> {
                    int selectedRow = BurpExtender.this.resultTable.getSelectedRow();
                    List filtered = BurpExtender.this.getFilteredTestLog();
                    if (selectedRow >= 0 && selectedRow < filtered.size()) {
                        TestLogEntry entry = (TestLogEntry)filtered.get(selectedRow);
                        BurpExtender.this.requestViewer.setMessage(entry.requestResponse.getRequest(), true);
                        BurpExtender.this.responseViewer.setMessage(entry.requestResponse.getResponse(), false);
                        BurpExtender.this.currentlyDisplayedItem = entry.requestResponse;
                    }
                });
                JPanel headerPanel = new JPanel(new GridLayout(4, 1));
                headerPanel.add(new JLabel("\u540d\u79f0\uff1aSMS Tomato Fuzzer"));
                headerPanel.add(new JLabel("\u4f5c\u8005\uff1aTomato"));
                headerPanel.add(new JLabel("Github\uff1ahttps://github.com/sh2493770457/Python3"));
                headerPanel.add(new JLabel("\u7248\u672c\uff1aV4.1"));
                BurpExtender.this.clearListButton = new JButton("\u6e05\u7a7a\u5217\u8868");
                BurpExtender.this.clearListButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BurpExtender.this.originalLog.clear();
                        BurpExtender.this.testLog.clear();
                        BurpExtender.this.originalCounter = 0;
                        BurpExtender.this.testCounter = 0;
                        BurpExtender.this.fireTableDataChanged();
                        ((ResultTableModel)BurpExtender.this.resultTable.getModel()).fireTableDataChanged();
                    }
                });
                BurpExtender.this.whiteListButton = new JButton("\u542f\u52a8\u767d\u540d\u5355");
                BurpExtender.this.whiteListButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (BurpExtender.this.whiteListEnabled) {
                            BurpExtender.this.whiteListEnabled = false;
                            BurpExtender.this.whiteListButton.setText("\u542f\u52a8\u767d\u540d\u5355");
                            BurpExtender.this.whiteListField.setEditable(true);
                            BurpExtender.this.whiteListField.setForeground(Color.BLACK);
                        } else {
                            BurpExtender.this.whiteListEnabled = true;
                            BurpExtender.this.whiteListButton.setText("\u5173\u95ed\u767d\u540d\u5355");
                            BurpExtender.this.whiteListDomains = BurpExtender.this.whiteListField.getText();
                            BurpExtender.this.whiteListField.setEditable(false);
                            BurpExtender.this.whiteListField.setForeground(Color.GRAY);
                        }
                    }
                });
                BurpExtender.this.whiteListField = new JTextField("\u586b\u5199\u767d\u540d\u5355\u57df\u540d", 20);
                BurpExtender.this.customPhoneField = new JTextField("18888888888", 20);
                BurpExtender.this.modifyPhoneButton = new JButton("\u4fee\u6539\u6d4b\u8bd5number");
                BurpExtender.this.modifyPhoneButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String input = BurpExtender.this.customPhoneField.getText().trim();
                        if (!input.isEmpty()) {
                            BurpExtender.this.customPhoneNumber = input;
                        } else {
                            BurpExtender.this.customPhoneNumber = "18888888888";
                        }
                        callbacks.printOutput("\u81ea\u5b9a\u4e49\u6d4b\u8bd5\u53f7\u7801\u66f4\u65b0\u4e3a\uff1a" + BurpExtender.this.customPhoneNumber);
                    }
                });
                BurpExtender.this.customSmsContentField = new JTextField("code", 20);
                BurpExtender.this.modifySmsContentButton = new JButton("\u8bbe\u7f6e\u77ed\u4fe1\u5185\u5bb9\u53c2\u6570");
                BurpExtender.this.modifySmsContentButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String input = BurpExtender.this.customSmsContentField.getText().trim();
                        if (!input.isEmpty()) {
                            BurpExtender.this.customSmsContentParam = input;
                            BurpExtender.this.smsContentTestEnabled = true;
                            BurpExtender.this.modifySmsContentButton.setText("\u5173\u95ed\u77ed\u4fe1\u5185\u5bb9\u6d4b\u8bd5");
                        } else {
                            BurpExtender.this.customSmsContentParam = "";
                            BurpExtender.this.smsContentTestEnabled = false;
                            BurpExtender.this.modifySmsContentButton.setText("\u8bbe\u7f6e\u77ed\u4fe1\u5185\u5bb9\u53c2\u6570");
                        }
                        callbacks.printOutput("\u77ed\u4fe1\u5185\u5bb9\u53c2\u6570\u8bbe\u7f6e\u4e3a\uff1a" + BurpExtender.this.customSmsContentParam + " (\u72b6\u6001: " + (BurpExtender.this.smsContentTestEnabled ? "\u542f\u7528" : "\u5173\u95ed") + ")");
                    }
                });
                BurpExtender.this.smsInterfaceTestButton = new JButton("\u6d4b\u8bd5\u77ed\u4fe1\u63a5\u53e3");
                BurpExtender.this.smsInterfaceTestButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BurpExtender.this.smsInterfaceTestEnabled = !BurpExtender.this.smsInterfaceTestEnabled;
                        BurpExtender.this.smsInterfaceTestButton.setText(BurpExtender.this.smsInterfaceTestEnabled ? "\u5173\u95ed\u77ed\u4fe1\u63a5\u53e3\u6d4b\u8bd5" : "\u6d4b\u8bd5\u77ed\u4fe1\u63a5\u53e3");
                        callbacks.printOutput("\u77ed\u4fe1\u63a5\u53e3\u6d4b\u8bd5 " + (BurpExtender.this.smsInterfaceTestEnabled ? "\u542f\u7528" : "\u5173\u95ed"));
                    }
                });
                BurpExtender.this.combineTestButton = new JButton("\u7ec4\u5408\u6d4b\u8bd5");
                BurpExtender.this.combineTestButton.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BurpExtender.this.combineTestEnabled = !BurpExtender.this.combineTestEnabled;
                        BurpExtender.this.combineTestButton.setText(BurpExtender.this.combineTestEnabled ? "\u5173\u95ed\u7ec4\u5408\u6d4b\u8bd5" : "\u7ec4\u5408\u6d4b\u8bd5");
                        callbacks.printOutput("\u7ec4\u5408\u6d4b\u8bd5 " + (BurpExtender.this.combineTestEnabled ? "\u542f\u7528" : "\u5173\u95ed"));
                    }
                });
                JPanel controlPanel = new JPanel();
                controlPanel.setLayout(new BoxLayout(controlPanel, 1));
                JPanel firstRow = new JPanel(new FlowLayout(0));
                firstRow.add(BurpExtender.this.clearListButton);
                firstRow.add(BurpExtender.this.whiteListField);
                firstRow.add(BurpExtender.this.whiteListButton);
                firstRow.add(BurpExtender.this.customPhoneField);
                firstRow.add(BurpExtender.this.modifyPhoneButton);
                controlPanel.add(firstRow);
                JPanel secondRow = new JPanel(new FlowLayout(0));
                secondRow.add(BurpExtender.this.customSmsContentField);
                secondRow.add(BurpExtender.this.modifySmsContentButton);
                controlPanel.add(secondRow);
                JPanel thirdRow = new JPanel(new FlowLayout(1));
                thirdRow.add(BurpExtender.this.smsInterfaceTestButton);
                thirdRow.add(BurpExtender.this.combineTestButton);
                controlPanel.add(thirdRow);
                JPanel topPanel = new JPanel();
                topPanel.setLayout(new BoxLayout(topPanel, 1));
                topPanel.add(headerPanel);
                topPanel.add(controlPanel);
                JSplitPane tableSplitPane = new JSplitPane(0);
                JScrollPane originalScrollPane = new JScrollPane(BurpExtender.this.originalTable);
                JScrollPane resultScrollPane = new JScrollPane(BurpExtender.this.resultTable);
                tableSplitPane.setTopComponent(originalScrollPane);
                tableSplitPane.setBottomComponent(resultScrollPane);
                tableSplitPane.setDividerLocation(200);
                JTabbedPane viewerTabs = new JTabbedPane();
                viewerTabs.addTab("Request", BurpExtender.this.requestViewer.getComponent());
                viewerTabs.addTab("Response", BurpExtender.this.responseViewer.getComponent());
                JPanel rightPanel = new JPanel(new BorderLayout());
                rightPanel.add((Component)topPanel, "North");
                rightPanel.add((Component)viewerTabs, "Center");
                BurpExtender.this.splitPane = new JSplitPane(1, tableSplitPane, rightPanel);
                BurpExtender.this.splitPane.setDividerLocation(600);
                class ButtonRenderer
                extends JButton
                implements TableCellRenderer {
                    public ButtonRenderer() {
                        this.setOpaque(true);
                    }

                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                        if (val != null) {
                            this.setText(val.toString());
                            this.setEnabled(true);
                        } else {
                            this.setText("");
                            this.setEnabled(false);
                        }
                        return this;
                    }
                }
                BurpExtender.this.originalTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                class ButtonEditor
                extends AbstractCellEditor
                implements TableCellEditor {
                    private JButton button = new JButton();
                    private int currentRow;

                    public ButtonEditor() {
                        this.button.addActionListener(e -> {
                            int origId = (Integer)BurpExtender.this.originalTable.getValueAt(this.currentRow, 0);
                            ((AtomicBoolean)BurpExtender.this.cancelFlags.get(origId)).set(true);
                            BurpExtender.this.originalLog.stream().filter(entry -> entry.id == origId).forEach(entry -> {
                                entry.state = "\u6d4b\u8bd5\u5b8c\u6bd5";
                            });
                            BurpExtender.this.fireTableDataChanged();
                            this.stopCellEditing();
                        });
                    }

                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object val, boolean isSel, int row, int col) {
                        this.currentRow = row;
                        this.button.setText(val == null ? "" : val.toString());
                        this.button.setEnabled(val != null);
                        return this.button;
                    }

                    @Override
                    public Object getCellEditorValue() {
                        return this.button.getText();
                    }
                }
                BurpExtender.this.originalTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor());
                callbacks.customizeUiComponent(BurpExtender.this.splitPane);
                callbacks.addSuiteTab(BurpExtender.this);
            }
        });
        callbacks.registerContextMenuFactory(this);
    }

    @Override
    public String getTabCaption() {
        return "SMS Tomato Fuzzer";
    }

    @Override
    public Component getUiComponent() {
        return this.splitPane;
    }

    @Override
    public byte[] getRequest() {
        return this.currentlyDisplayedItem != null ? this.currentlyDisplayedItem.getRequest() : null;
    }

    @Override
    public byte[] getResponse() {
        return this.currentlyDisplayedItem != null ? this.currentlyDisplayedItem.getResponse() : null;
    }

    @Override
    public IHttpService getHttpService() {
        return this.currentlyDisplayedItem != null ? this.currentlyDisplayedItem.getHttpService() : null;
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        ArrayList<JMenuItem> menuItems = new ArrayList<JMenuItem>();
        final IHttpRequestResponse[] selectedMessages = invocation.getSelectedMessages();
        if (selectedMessages != null && selectedMessages.length > 0) {
            JMenuItem menuItem = new JMenuItem("Send to SMS Tomato Fuzzer");
            menuItem.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread(new Runnable(){

                        @Override
                        public void run() {
                            BurpExtender.this.checkPayloads(selectedMessages[0]);
                        }
                    }).start();
                }
            });
            menuItems.add(menuItem);
        }
        return menuItems;
    }

    private boolean isPhoneTestCandidate(String value) {
        if (value == null) {
            return false;
        }
        if (value.matches("^\\d{11}$")) {
            return true;
        }
        if (value.matches("^(?=.*\\d)(?=.*\\*)(?!^\\*+$)[\\d\\*]{11}$")) {
            return true;
        }
        return value.matches("^(\\+86|86)\\d{11}$");
    }

    private void collectJsonPhoneCandidates(Object json, List<String> candidates) {
        block4: {
            block3: {
                if (!(json instanceof JSONObject)) break block3;
                JSONObject obj = (JSONObject)json;
                for (String k : obj.keySet()) {
                    Object v = obj.get(k);
                    if (v instanceof String || v instanceof Number) {
                        String s = v.toString();
                        if (!this.isPhoneTestCandidate(s) || candidates.contains(s)) continue;
                        candidates.add(s);
                        continue;
                    }
                    this.collectJsonPhoneCandidates(v, candidates);
                }
                break block4;
            }
            if (!(json instanceof JSONArray)) break block4;
            JSONArray arr = (JSONArray)json;
            for (int i = 0; i < arr.length(); ++i) {
                this.collectJsonPhoneCandidates(arr.get(i), candidates);
            }
        }
    }

    private void checkPayloads(IHttpRequestResponse baseRequestResponse) {
        String[] extraHeaders;
        String[] parts;
        IRequestInfo originalReqInfo;
        ArrayList<String> origHeaders;
        String reqLine;
        TestLogEntry testEntry;
        IHttpRequestResponsePersisted persistedTest;
        short responseCode;
        IHttpRequestResponse testResponse;
        IHttpRequestResponse testResponse2;
        IRequestInfo ri = this.helpers.analyzeRequest(baseRequestResponse);
        List<IParameter> params = ri.getParameters();
        ArrayList<String> phoneCandidates = new ArrayList<String>();
        for (IParameter p : params) {
            String v = String.valueOf(p.getValue());
            if (!this.isPhoneTestCandidate(v) || phoneCandidates.contains(v)) continue;
            phoneCandidates.add(v);
        }
        List<String> head = ri.getHeaders();
        boolean isjson = head.stream().anyMatch(h -> h.toLowerCase().contains("application/json"));
        if (isjson) {
            byte[] req = baseRequestResponse.getRequest();
            int bodyOffset = ri.getBodyOffset();
            String body = this.helpers.bytesToString(Arrays.copyOfRange(req, bodyOffset, req.length)).trim();
            try {
                Object json = this.parseJson(body);
                this.collectJsonPhoneCandidates(json, phoneCandidates);
            }
            catch (JSONException ex) {
                this.callbacks.printOutput("JSON \u89e3\u6790\u5931\u8d25\uff0c\u65e0\u6cd5\u6536\u96c6 JSON \u4e2d\u7684\u624b\u673a\u53f7\uff1a" + ex.getMessage());
            }
        }
        String url_path = ri.getUrl().getPath();
        for (String segment : url_path.split("/")) {
            if (!this.isPhoneTestCandidate(segment) || phoneCandidates.contains(segment)) continue;
            phoneCandidates.add(segment);
        }
        if (phoneCandidates.isEmpty()) {
            JOptionPane.showMessageDialog(null, "\u672a\u68c0\u6d4b\u5230\u53ef\u6d4b\u8bd5\u7684\u624b\u673a\u53f7", "\u63d0\u793a", 1);
            return;
        }
        this.selectedNumbers = this.showPhoneSelectionDialog(phoneCandidates);
        if (this.selectedNumbers.isEmpty()) {
            this.callbacks.printOutput("\u672a\u9009\u62e9\u624b\u673a\u53f7\uff0c\u505c\u6b62 Fuzz\u3002");
            return;
        }
        URL url = this.helpers.analyzeRequest(baseRequestResponse).getUrl();
        String urlStr = url.toString();
        if (this.whiteListEnabled) {
            String[] domains;
            boolean allowed = false;
            for (String domain : domains = this.whiteListDomains.split(",")) {
                if (!urlStr.contains(domain.trim())) continue;
                allowed = true;
                break;
            }
            if (allowed) {
                this.callbacks.printOutput("\u505c\u6b62\u6d4b\u8bd5\uff0c\u7531\u4e8e\u8bf7\u6c42URL\u5728\u767d\u540d\u5355\u5185\uff1a" + urlStr);
                return;
            }
        }
        IHttpRequestResponsePersisted persistedOriginal = this.callbacks.saveBuffersToTempFiles(baseRequestResponse);
        OriginalLogEntry origEntry = new OriginalLogEntry(this.originalCounter++, baseRequestResponse.getHttpService(), url, persistedOriginal, "\u6d4b\u8bd5\u4e2d...");
        this.originalLog.add(origEntry);
        this.fireTableDataChanged();
        SwingUtilities.invokeLater(() -> {
            this.fireTableDataChanged();
            int rowIndex = this.originalLog.size() - 1;
            if (rowIndex >= 0) {
                this.originalTable.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
            }
        });
        this.cancelFlags.put(origEntry.id, new AtomicBoolean(false));
        IRequestInfo requestInfo = this.helpers.analyzeRequest(baseRequestResponse);
        List<IParameter> parameters = requestInfo.getParameters();
        boolean testExecuted = false;
        block7: for (IParameter param : parameters) {
            int i;
            TestLogEntry testEntry2;
            IHttpRequestResponsePersisted persistedTest2;
            byte type = param.getType();
            if (type != 0 && type != 1 && type != 2) continue;
            String value = String.valueOf(param.getValue());
            if (this.isPhoneTestCandidate(value) && this.selectedNumbers.contains(value)) {
                byte[] newRequest;
                testExecuted = true;
                int payloadIndex = 1;
                for (String pattern : PAYLOAD_PATTERNS) {
                    if (this.cancelFlags.get(origEntry.id).get()) break;
                    byte[] reqBuf = baseRequestResponse.getRequest();
                    for (String num : this.selectedNumbers) {
                        String payload = pattern.replace("xxxxxxxxxxx", num);
                        if (payload.contains("18888888888")) {
                            payload = payload.replace("18888888888", this.customPhoneNumber);
                        }
                        IParameter np = this.helpers.buildParameter(param.getName(), payload, type);
                        reqBuf = this.helpers.updateParameter(reqBuf, np);
                    }

                    // 添加短信内容参数的序号标记
                    if (this.smsContentTestEnabled && !this.customSmsContentParam.isEmpty()) {
                        reqBuf = this.addSmsContentParameter(reqBuf, String.valueOf(payloadIndex));
                    }

                    IHttpRequestResponse rr = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), reqBuf);
                    int len = rr.getResponse() != null ? rr.getResponse().length : 0;
                    short code = rr.getResponse() != null ? this.helpers.analyzeResponse(rr.getResponse()).getStatusCode() : (short)0;
                    IHttpRequestResponsePersisted pr = this.callbacks.saveBuffersToTempFiles(rr);
                    String merged = this.selectedNumbers.stream().map(n -> pattern.replace("xxxxxxxxxxx", (CharSequence)n)).collect(Collectors.joining(";"));
                    TestLogEntry te = new TestLogEntry(this.testCounter++, origEntry.id, param.getName() + "_multi", merged, len, 0, code, pr);
                    this.testLog.add(te);
                    SwingUtilities.invokeLater(() -> {
                        ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                        if (this.selectedOriginalId == origEntry.id) {
                            model.setFilter(origEntry.id);
                            int rowCount = this.resultTable.getRowCount();
                            if (rowCount > 0) {
                                this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                            }
                        }
                    });
                    payloadIndex++;
                }
                for (int i2 = 1; i2 <= value.length(); ++i2) {
                    String prefix = value.substring(0, i2);
                    String encodedPrefix = this.alwaysUrlEncode(prefix);
                    String newPhone = encodedPrefix + value.substring(i2);
                    IParameter newParam = this.helpers.buildParameter(param.getName(), newPhone, type);
                    newRequest = this.helpers.updateParameter(baseRequestResponse.getRequest(), newParam);
                    IHttpService httpService = baseRequestResponse.getHttpService();
                    IHttpRequestResponse testResponse3 = this.callbacks.makeHttpRequest(httpService, newRequest);
                    int responseLength = testResponse3.getResponse() != null ? testResponse3.getResponse().length : 0;
                    short responseCode2 = testResponse3.getResponse() != null ? this.helpers.analyzeResponse(testResponse3.getResponse()).getStatusCode() : (short)0;
                    IHttpRequestResponsePersisted persistedTest3 = this.callbacks.saveBuffersToTempFiles(testResponse3);
                    TestLogEntry testEntry3 = new TestLogEntry(this.testCounter++, origEntry.id, param.getName() + "_urlencode", newPhone, responseLength, 0, responseCode2, persistedTest3);
                    this.testLog.add(testEntry3);
                    SwingUtilities.invokeLater(() -> {
                        ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                        if (this.selectedOriginalId == origEntry.id) {
                            model.setFilter(origEntry.id);
                            int rowCount = this.resultTable.getRowCount();
                            if (rowCount > 0) {
                                this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                            }
                        }
                    });
                }
                if (this.combineTestEnabled) {
                    byte[] req = baseRequestResponse.getRequest();
                    IRequestInfo reqInfo = this.helpers.analyzeRequest(req);
                    ArrayList<String> headers = new ArrayList<String>(reqInfo.getHeaders());
                    String requestLine = (String)headers.get(0);
                    if (requestLine.startsWith("GET ")) {
                        String[] parts2 = requestLine.split(" ");
                        if (parts2.length >= 3) {
                            String urlPart = parts2[1];
                            urlPart = urlPart.contains("?") ? urlPart + "&" + param.getName() + "=" + this.customPhoneNumber : urlPart + "?" + param.getName() + "=" + this.customPhoneNumber;
                            String newRequestLine = parts2[0] + " " + urlPart + " " + parts2[2];
                            headers.set(0, newRequestLine);
                        }
                        newRequest = this.helpers.buildHttpMessage(headers, null);
                        IHttpRequestResponse testResponse4 = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                        int responseLength = testResponse4.getResponse() != null ? testResponse4.getResponse().length : 0;
                        short responseCode3 = testResponse4.getResponse() != null ? this.helpers.analyzeResponse(testResponse4.getResponse()).getStatusCode() : (short)0;
                        IHttpRequestResponsePersisted persistedTest4 = this.callbacks.saveBuffersToTempFiles(testResponse4);
                        TestLogEntry testEntry4 = new TestLogEntry(this.testCounter++, origEntry.id, param.getName() + "_dup", this.customPhoneNumber, responseLength, 0, responseCode3, persistedTest4);
                        this.testLog.add(testEntry4);
                        SwingUtilities.invokeLater(() -> {
                            ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                            if (this.selectedOriginalId == origEntry.id) {
                                model.setFilter(origEntry.id);
                                int rowCount = this.resultTable.getRowCount();
                                if (rowCount > 0) {
                                    this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                }
                            }
                        });
                    } else {
                        int bodyOffset = reqInfo.getBodyOffset();
                        String body = bodyOffset < req.length ? this.helpers.bytesToString(Arrays.copyOfRange(req, bodyOffset, req.length)) : "";
                        String newBody = body + "&" + param.getName() + "=" + this.customPhoneNumber;
                        byte[] newRequest2 = this.helpers.buildHttpMessage(headers, newBody.getBytes(StandardCharsets.UTF_8));
                        IHttpRequestResponse testResponse5 = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest2);
                        int responseLength = testResponse5.getResponse() != null ? testResponse5.getResponse().length : 0;
                        short responseCode4 = testResponse5.getResponse() != null ? this.helpers.analyzeResponse(testResponse5.getResponse()).getStatusCode() : (short)0;
                        persistedTest2 = this.callbacks.saveBuffersToTempFiles(testResponse5);
                        testEntry2 = new TestLogEntry(this.testCounter++, origEntry.id, param.getName() + "_dup", this.customPhoneNumber, responseLength, 0, responseCode4, persistedTest2);
                        this.testLog.add(testEntry2);
                        SwingUtilities.invokeLater(() -> {
                            ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                            if (this.selectedOriginalId == origEntry.id) {
                                model.setFilter(origEntry.id);
                                int rowCount = this.resultTable.getRowCount();
                                if (rowCount > 0) {
                                    this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                }
                            }
                        });
                    }
                }
            }
            if (this.smsInterfaceTestEnabled && this.isSmsKeyword(value)) {
                String[] keywords;
                testExecuted = true;
                for (String kw : keywords = new String[]{"register", "reg", "regist", "login", "recall", "retrieve", "ret", "true", "false"}) {
                    if (kw.equalsIgnoreCase(value)) continue;
                    IParameter newParam = this.helpers.buildParameter(param.getName(), kw, type);
                    byte[] newRequest = this.helpers.updateParameter(baseRequestResponse.getRequest(), newParam);
                    IHttpService httpService = baseRequestResponse.getHttpService();
                    IHttpRequestResponse testResponse6 = this.callbacks.makeHttpRequest(httpService, newRequest);
                    int responseLength = testResponse6.getResponse() != null ? testResponse6.getResponse().length : 0;
                    short responseCode5 = testResponse6.getResponse() != null ? this.helpers.analyzeResponse(testResponse6.getResponse()).getStatusCode() : (short)0;
                    persistedTest2 = this.callbacks.saveBuffersToTempFiles(testResponse6);
                    testEntry2 = new TestLogEntry(this.testCounter++, origEntry.id, param.getName() + "_sms", kw, responseLength, 0, responseCode5, persistedTest2);
                    this.testLog.add(testEntry2);
                    SwingUtilities.invokeLater(() -> {
                        ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                        if (this.selectedOriginalId == origEntry.id) {
                            model.setFilter(origEntry.id);
                            int rowCount = this.resultTable.getRowCount();
                            if (rowCount > 0) {
                                this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                            }
                        }
                    });
                }
            }
            if (this.smsInterfaceTestEnabled && this.isInteger(value) && this.isInRange(value)) {
                testExecuted = true;
                int origInt = Integer.parseInt(value);
                for (i = -100; i <= 100; ++i) {
                    if (i == origInt) continue;
                    IParameter newParam = this.helpers.buildParameter(param.getName(), String.valueOf(i), type);
                    byte[] newRequest = this.helpers.updateParameter(baseRequestResponse.getRequest(), newParam);
                    IHttpService httpService = baseRequestResponse.getHttpService();
                    testResponse2 = this.callbacks.makeHttpRequest(httpService, newRequest);
                    int responseLength = testResponse2.getResponse() != null ? testResponse2.getResponse().length : 0;
                    short responseCode6 = testResponse2.getResponse() != null ? this.helpers.analyzeResponse(testResponse2.getResponse()).getStatusCode() : (short)0;
                    IHttpRequestResponsePersisted persistedTest5 = this.callbacks.saveBuffersToTempFiles(testResponse2);
                    TestLogEntry testEntry5 = new TestLogEntry(this.testCounter++, origEntry.id, param.getName() + "_int", String.valueOf(i), responseLength, 0, responseCode6, persistedTest5);
                    this.testLog.add(testEntry5);
                    SwingUtilities.invokeLater(() -> {
                        ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                        if (this.selectedOriginalId == origEntry.id) {
                            model.setFilter(origEntry.id);
                            int rowCount = this.resultTable.getRowCount();
                            if (rowCount > 0) {
                                this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                            }
                        }
                    });
                }
            }
            if (!this.combineTestEnabled || !this.isPhoneTestCandidate(value) || !this.selectedNumbers.contains(value)) continue;
            testExecuted = true;
            String[] origInt = PAYLOAD_PATTERNS;
            i = origInt.length;
            for (int newParam = 0; newParam < i; ++newParam) {
                byte[] modifiedRequest;
                IRequestInfo reqInfo;
                ArrayList<String> headers;
                String requestLine;
                String pattern = origInt[newParam];
                if (this.cancelFlags.get(origEntry.id).get()) continue block7;
                String payload = pattern.replace("xxxxxxxxxxx", value);
                if (payload.contains("18888888888")) {
                    payload = payload.replace("18888888888", this.customPhoneNumber);
                }
                if ((requestLine = (String)(headers = new ArrayList<String>((reqInfo = this.helpers.analyzeRequest(modifiedRequest = this.helpers.updateParameter(baseRequestResponse.getRequest(), this.helpers.buildParameter(param.getName(), payload, type)))).getHeaders())).get(0)).startsWith("GET ")) {
                    String[] parts3 = requestLine.split(" ");
                    if (parts3.length >= 3) {
                        String urlPart = parts3[1];
                        urlPart = urlPart.contains("?") ? urlPart + "&" + param.getName() + "=" + this.customPhoneNumber : urlPart + "?" + param.getName() + "=" + this.customPhoneNumber;
                        String newRequestLine = parts3[0] + " " + urlPart + " " + parts3[2];
                        headers.set(0, newRequestLine);
                    }
                    byte[] newRequest = this.helpers.buildHttpMessage(headers, null);
                    testResponse = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                    int responseLength = testResponse.getResponse() != null ? testResponse.getResponse().length : 0;
                    responseCode = testResponse.getResponse() != null ? this.helpers.analyzeResponse(testResponse.getResponse()).getStatusCode() : (short)0;
                    persistedTest = this.callbacks.saveBuffersToTempFiles(testResponse);
                    testEntry = new TestLogEntry(this.testCounter++, origEntry.id, param.getName() + "_combo", payload + " + dup:" + this.customPhoneNumber, responseLength, 0, responseCode, persistedTest);
                    this.testLog.add(testEntry);
                    SwingUtilities.invokeLater(() -> {
                        ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                        if (this.selectedOriginalId == origEntry.id) {
                            model.setFilter(origEntry.id);
                            int rowCount = this.resultTable.getRowCount();
                            if (rowCount > 0) {
                                this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                            }
                        }
                    });
                    continue;
                }
                int bodyOffset = reqInfo.getBodyOffset();
                String body = bodyOffset < modifiedRequest.length ? this.helpers.bytesToString(Arrays.copyOfRange(modifiedRequest, bodyOffset, modifiedRequest.length)) : "";
                String newBody = body + "&" + param.getName() + "=" + this.customPhoneNumber;
                byte[] newRequest = this.helpers.buildHttpMessage(headers, newBody.getBytes(StandardCharsets.UTF_8));
                IHttpRequestResponse testResponse7 = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                int responseLength = testResponse7.getResponse() != null ? testResponse7.getResponse().length : 0;
                short responseCode7 = testResponse7.getResponse() != null ? this.helpers.analyzeResponse(testResponse7.getResponse()).getStatusCode() : (short)0;
                IHttpRequestResponsePersisted persistedTest6 = this.callbacks.saveBuffersToTempFiles(testResponse7);
                TestLogEntry testEntry6 = new TestLogEntry(this.testCounter++, origEntry.id, param.getName() + "_combo", payload + " + dup:" + this.customPhoneNumber, responseLength, 0, responseCode7, persistedTest6);
                this.testLog.add(testEntry6);
                SwingUtilities.invokeLater(() -> {
                    ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                    if (this.selectedOriginalId == origEntry.id) {
                        model.setFilter(origEntry.id);
                        int rowCount = this.resultTable.getRowCount();
                        if (rowCount > 0) {
                            this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                        }
                    }
                });
            }
        }
        String path = url.getPath();
        if (path != null && path.matches(".*\\d{11}$")) {
            String phoneFromPath = path.replaceAll(".*?(\\d{11})$", "$1");
            testExecuted = true;
            for (String pattern : PAYLOAD_PATTERNS) {
                if (this.cancelFlags.get(origEntry.id).get()) break;
                String payload = pattern.replace("xxxxxxxxxxx", phoneFromPath);
                if (payload.contains("18888888888")) {
                    payload = payload.replace("18888888888", this.customPhoneNumber);
                }
                ArrayList<String> headers = new ArrayList<String>(requestInfo.getHeaders());
                String firstHeader = (String)headers.get(0);
                String newRequestLine = firstHeader.replace(phoneFromPath, payload);
                headers.set(0, newRequestLine);
                int bodyOffset = requestInfo.getBodyOffset();
                byte[] req = baseRequestResponse.getRequest();
                byte[] bodyBytes = bodyOffset < req.length ? Arrays.copyOfRange(req, bodyOffset, req.length) : new byte[]{};
                byte[] newRequest = this.helpers.buildHttpMessage(headers, bodyBytes);
                IHttpService httpService = baseRequestResponse.getHttpService();
                testResponse = this.callbacks.makeHttpRequest(httpService, newRequest);
                int responseLength = testResponse.getResponse() != null ? testResponse.getResponse().length : 0;
                responseCode = testResponse.getResponse() != null ? this.helpers.analyzeResponse(testResponse.getResponse()).getStatusCode() : (short)0;
                persistedTest = this.callbacks.saveBuffersToTempFiles(testResponse);
                testEntry = new TestLogEntry(this.testCounter++, origEntry.id, "path", payload, responseLength, 0, responseCode, persistedTest);
                this.testLog.add(testEntry);
                SwingUtilities.invokeLater(() -> {
                    ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                    if (this.selectedOriginalId == origEntry.id) {
                        model.setFilter(origEntry.id);
                        int rowCount = this.resultTable.getRowCount();
                        if (rowCount > 0) {
                            this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                        }
                    }
                });
            }
        }
        if ((reqLine = (String)(origHeaders = new ArrayList<String>((originalReqInfo = this.helpers.analyzeRequest(baseRequestResponse.getRequest())).getHeaders())).get(0)).startsWith("POST ") && (parts = reqLine.split(" ")).length >= 3) {
            String urlPart = parts[1];
            while (urlPart.endsWith("/")) {
                urlPart = urlPart.substring(0, urlPart.length() - 1);
            }
            String[] staticExts = new String[]{".js", ".json", ".html", ".css", ".jpg", ".png", ".txt", ".mp3", ".mp4", ".icon", ";.js", ";.json", ";.html", ";.css", ";.jpg", ";.png", ";.txt", ";.mp3", ";.mp4", ";.icon"};
            int bodyOffset = originalReqInfo.getBodyOffset();
            byte[] bodyBytes = bodyOffset < baseRequestResponse.getRequest().length ? Arrays.copyOfRange(baseRequestResponse.getRequest(), bodyOffset, baseRequestResponse.getRequest().length) : new byte[]{};
            for (String ext : staticExts) {
                if (this.cancelFlags.get(origEntry.id).get()) break;
                String newUrlPart = urlPart + ext;
                String newRequestLine = parts[0] + " " + newUrlPart + " " + parts[2];
                ArrayList<String> newHeaders = new ArrayList<String>(origHeaders);
                newHeaders.set(0, newRequestLine);
                byte[] newRequest = this.helpers.buildHttpMessage(newHeaders, bodyBytes);
                IHttpService httpService = baseRequestResponse.getHttpService();
                IHttpRequestResponse testResponse8 = this.callbacks.makeHttpRequest(httpService, newRequest);
                int responseLength = testResponse8.getResponse() != null ? testResponse8.getResponse().length : 0;
                short responseCode8 = testResponse8.getResponse() != null ? this.helpers.analyzeResponse(testResponse8.getResponse()).getStatusCode() : (short)0;
                IHttpRequestResponsePersisted persistedTest7 = this.callbacks.saveBuffersToTempFiles(testResponse8);
                TestLogEntry testEntry7 = new TestLogEntry(this.testCounter++, origEntry.id, "postExt", ext, responseLength, 0, responseCode8, persistedTest7);
                this.testLog.add(testEntry7);
                SwingUtilities.invokeLater(() -> {
                    ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                    if (this.selectedOriginalId == origEntry.id) {
                        model.setFilter(origEntry.id);
                        int rowCount = this.resultTable.getRowCount();
                        if (rowCount > 0) {
                            this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                        }
                    }
                });
            }
        }
        for (String h2 : extraHeaders = new String[]{"X-Forwarded-For: 127.0.0.1", "X-Forwarded: 127.0.0.1", "Forwarded-For: 127.0.0.1", "Forwarded: 127.0.0.1", "X-Requested-With: 127.0.0.1", "X-Forwarded-Proto: 127.0.0.1", "X-Forwarded-Host: 127.0.0.1", "X-remote-IP: 127.0.0.1", "X-remote-addr: 127.0.0.1", "True-Client-IP: 127.0.0.1", "X-Client-IP: 127.0.0.1", "Client-IP: 127.0.0.1", "X-Real-IP: 127.0.0.1", "Ali-CDN-Real-IP: 127.0.0.1", "Cdn-Src-Ip: 127.0.0.1", "Cdn-Real-Ip: 127.0.0.1", "CF-Connecting-IP: 127.0.0.1", "X-Cluster-Client-IP: 127.0.0.1", "WL-Proxy-Client-IP: 127.0.0.1", "Proxy-Client-IP: 127.0.0.1", "Fastly-Client-Ip: 127.0.0.1", "True-Client-Ip: 127.0.0.1", "X-Originating-IP: 127.0.0.1", "X-Host: 127.0.0.1", "X-Custom-IP-Authorization: 127.0.0.1"}) {
            origHeaders.add(h2);
        }
        int bodyOffset = originalReqInfo.getBodyOffset();
        Object bodyBytes = bodyOffset < baseRequestResponse.getRequest().length ? Arrays.copyOfRange(baseRequestResponse.getRequest(), bodyOffset, baseRequestResponse.getRequest().length) : new byte[]{};
        byte[] newRequest = this.helpers.buildHttpMessage(origHeaders, (byte[])bodyBytes);
        IHttpService httpService = baseRequestResponse.getHttpService();
        testResponse2 = this.callbacks.makeHttpRequest(httpService, newRequest);
        int responseLength = testResponse2.getResponse() != null ? testResponse2.getResponse().length : 0;
        short responseCode9 = testResponse2.getResponse() != null ? this.helpers.analyzeResponse(testResponse2.getResponse()).getStatusCode() : (short)0;
        IHttpRequestResponsePersisted persistedTest8 = this.callbacks.saveBuffersToTempFiles(testResponse2);
        TestLogEntry testEntry8 = new TestLogEntry(this.testCounter++, origEntry.id, "extraHeaders", "Add extra headers", responseLength, 0, responseCode9, persistedTest8);
        this.testLog.add(testEntry8);
        SwingUtilities.invokeLater(() -> {
            ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
            if (this.selectedOriginalId == origEntry.id) {
                model.setFilter(origEntry.id);
                int rowCount = this.resultTable.getRowCount();
                if (rowCount > 0) {
                    this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                }
            }
        });
        List<String> headers = requestInfo.getHeaders();
        boolean isJson = false;
        Iterator<String> headerIterator = headers.iterator();
        while (headerIterator.hasNext()) {
            String header = headerIterator.next();
            if (!header.toLowerCase().contains("application/json")) continue;
            isJson = true;
            break;
        }
        if (isJson) {
            byte[] req = baseRequestResponse.getRequest();
            int bodyOffset2 = requestInfo.getBodyOffset();
            String body = this.helpers.bytesToString(Arrays.copyOfRange(req, bodyOffset2, req.length)).trim();
            try {
                Object json = this.parseJson(body);
                boolean jsonExecuted = this.processJsonAndTest(json, baseRequestResponse, headers, origEntry);
                testExecuted |= jsonExecuted;
            }
            catch (Exception ex) {
                this.callbacks.printOutput("JSON\u89e3\u6790\u5931\u8d25: " + ex.getMessage());
            }
        }
        if (testExecuted) {
            origEntry.state = "\u6d4b\u8bd5\u5b8c\u6bd5";
            this.fireTableDataChanged();
        }
    }

    private boolean processJsonAndTest(Object json, IHttpRequestResponse baseRequestResponse, List<String> headers, OriginalLogEntry origEntry) {
        boolean executed;
        block20: {
            block19: {
                executed = false;
                if (!(json instanceof JSONObject)) break block19;
                JSONObject obj = (JSONObject)json;
                Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object valueObj = obj.get(key);
                    if (valueObj instanceof String || valueObj instanceof Number) {
                        TestLogEntry testEntry;
                        IHttpRequestResponsePersisted persistedTest;
                        short responseCode;
                        int responseLength;
                        IHttpRequestResponse testResponse;
                        byte[] newRequest;
                        byte[] bodyBytes;
                        JSONObject newObj;
                        String str = valueObj.toString();
                        if (this.isPhoneTestCandidate(str) && this.selectedNumbers.contains(str)) {
                            String payload;
                            executed = true;
                            int payloadIndex = 1;
                            for (String pattern : PAYLOAD_PATTERNS) {
                                if (this.cancelFlags.get(origEntry.id).get()) break;
                                payload = pattern.replace("xxxxxxxxxxx", str);
                                if (payload.contains("18888888888")) {
                                    payload = payload.replace("18888888888", this.customPhoneNumber);
                                }
                                newObj = new JSONObject(obj.toString());
                                newObj.put(key, payload);

                                // 添加短信内容参数的序号标记
                                if (this.smsContentTestEnabled && !this.customSmsContentParam.isEmpty()) {
                                    newObj.put(this.customSmsContentParam, String.valueOf(payloadIndex));
                                }

                                bodyBytes = newObj.toString().getBytes(StandardCharsets.UTF_8);
                                newRequest = this.helpers.buildHttpMessage(headers, bodyBytes);
                                payloadIndex++;
                                testResponse = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                                responseLength = testResponse.getResponse() != null ? testResponse.getResponse().length : 0;
                                responseCode = testResponse.getResponse() != null ? this.helpers.analyzeResponse(testResponse.getResponse()).getStatusCode() : (short)0;
                                persistedTest = this.callbacks.saveBuffersToTempFiles(testResponse);
                                testEntry = new TestLogEntry(this.testCounter++, origEntry.id, key, payload, responseLength, 0, responseCode, persistedTest);
                                this.testLog.add(testEntry);
                                SwingUtilities.invokeLater(() -> {
                                    ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                                    if (this.selectedOriginalId == origEntry.id) {
                                        model.setFilter(origEntry.id);
                                        int rowCount = this.resultTable.getRowCount();
                                        if (rowCount > 0) {
                                            this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                        }
                                    }
                                });
                            }
                            if (this.combineTestEnabled) {
                                for (String pattern : PAYLOAD_PATTERNS) {
                                    if (this.cancelFlags.get(origEntry.id).get()) break;
                                    payload = pattern.replace("xxxxxxxxxxx", str);
                                    if (payload.contains("18888888888")) {
                                        payload = payload.replace("18888888888", this.customPhoneNumber);
                                    }
                                    String dupJson = this.addDuplicateParameterToJsonWithPayload(obj, key, payload, this.customPhoneNumber);
                                    bodyBytes = dupJson.getBytes(StandardCharsets.UTF_8);
                                    newRequest = this.helpers.buildHttpMessage(headers, bodyBytes);
                                    testResponse = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                                    responseLength = testResponse.getResponse() != null ? testResponse.getResponse().length : 0;
                                    responseCode = testResponse.getResponse() != null ? this.helpers.analyzeResponse(testResponse.getResponse()).getStatusCode() : (short)0;
                                    persistedTest = this.callbacks.saveBuffersToTempFiles(testResponse);
                                    testEntry = new TestLogEntry(this.testCounter++, origEntry.id, key + "_combo", payload + " + dup:" + this.customPhoneNumber, responseLength, 0, responseCode, persistedTest);
                                    this.testLog.add(testEntry);
                                    SwingUtilities.invokeLater(() -> {
                                        ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                                        if (this.selectedOriginalId == origEntry.id) {
                                            model.setFilter(origEntry.id);
                                            int rowCount = this.resultTable.getRowCount();
                                            if (rowCount > 0) {
                                                this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        if (this.smsInterfaceTestEnabled && this.isSmsKeyword(str)) {
                            String[] keywords;
                            executed = true;
                            for (String kw : keywords = new String[]{"register", "reg", "regist", "login", "recall", "retrieve", "ret", "true", "false"}) {
                                if (kw.equalsIgnoreCase(str)) continue;
                                newObj = new JSONObject(obj.toString());
                                newObj.put(key, kw);
                                bodyBytes = newObj.toString().getBytes(StandardCharsets.UTF_8);
                                newRequest = this.helpers.buildHttpMessage(headers, bodyBytes);
                                testResponse = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                                responseLength = testResponse.getResponse() != null ? testResponse.getResponse().length : 0;
                                responseCode = testResponse.getResponse() != null ? this.helpers.analyzeResponse(testResponse.getResponse()).getStatusCode() : (short)0;
                                persistedTest = this.callbacks.saveBuffersToTempFiles(testResponse);
                                testEntry = new TestLogEntry(this.testCounter++, origEntry.id, key + "_sms", kw, responseLength, 0, responseCode, persistedTest);
                                this.testLog.add(testEntry);
                                SwingUtilities.invokeLater(() -> {
                                    ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                                    if (this.selectedOriginalId == origEntry.id) {
                                        model.setFilter(origEntry.id);
                                        int rowCount = this.resultTable.getRowCount();
                                        if (rowCount > 0) {
                                            this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                        }
                                    }
                                });
                            }
                        }
                        if (!this.smsInterfaceTestEnabled || !this.isInteger(str) || !this.isInRange(str)) continue;
                        executed = true;
                        int origInt = Integer.parseInt(str);
                        for (int i = -100; i <= 100; ++i) {
                            if (i == origInt) continue;
                            JSONObject newObj2 = new JSONObject(obj.toString());
                            newObj2.put(key, String.valueOf(i));
                            byte[] bodyBytes2 = newObj2.toString().getBytes(StandardCharsets.UTF_8);
                            byte[] newRequest2 = this.helpers.buildHttpMessage(headers, bodyBytes2);
                            IHttpRequestResponse testResponse2 = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest2);
                            int responseLength2 = testResponse2.getResponse() != null ? testResponse2.getResponse().length : 0;
                            short responseCode2 = testResponse2.getResponse() != null ? this.helpers.analyzeResponse(testResponse2.getResponse()).getStatusCode() : (short)0;
                            IHttpRequestResponsePersisted persistedTest2 = this.callbacks.saveBuffersToTempFiles(testResponse2);
                            TestLogEntry testEntry2 = new TestLogEntry(this.testCounter++, origEntry.id, key + "_int", String.valueOf(i), responseLength2, 0, responseCode2, persistedTest2);
                            this.testLog.add(testEntry2);
                            SwingUtilities.invokeLater(() -> {
                                ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                                if (this.selectedOriginalId == origEntry.id) {
                                    model.setFilter(origEntry.id);
                                    int rowCount = this.resultTable.getRowCount();
                                    if (rowCount > 0) {
                                        this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                    }
                                }
                            });
                        }
                        continue;
                    }
                    if (!(valueObj instanceof JSONObject) && !(valueObj instanceof JSONArray)) continue;
                    executed |= this.processJsonAndTest(valueObj, baseRequestResponse, headers, origEntry);
                }
                break block20;
            }
            if (!(json instanceof JSONArray)) break block20;
            JSONArray arr = (JSONArray)json;
            for (int i = 0; i < arr.length(); ++i) {
                Object element = arr.get(i);
                if (element instanceof String || element instanceof Number) {
                    TestLogEntry testEntry;
                    IHttpRequestResponsePersisted persistedTest;
                    short responseCode;
                    IHttpRequestResponse testResponse;
                    JSONArray newArr;
                    String str = element.toString();
                    if (this.isPhoneTestCandidate(str) && this.selectedNumbers.contains(str)) {
                        executed = true;
                        int payloadIndex = 1;
                        for (String pattern : PAYLOAD_PATTERNS) {
                            if (this.cancelFlags.get(origEntry.id).get()) break;
                            String payload = pattern.replace("xxxxxxxxxxx", str);
                            if (payload.contains("18888888888")) {
                                payload = payload.replace("18888888888", this.customPhoneNumber);
                            }
                            newArr = new JSONArray(arr.toString());
                            newArr.put(i, payload);
                            byte[] bodyBytes = newArr.toString().getBytes(StandardCharsets.UTF_8);

                            // 对于JSON数组，如果需要添加短信内容参数，需要修改整个请求体
                            if (this.smsContentTestEnabled && !this.customSmsContentParam.isEmpty()) {
                                bodyBytes = this.addSmsContentToJsonArray(bodyBytes, String.valueOf(payloadIndex), headers);
                            }

                            byte[] newRequest = this.helpers.buildHttpMessage(headers, bodyBytes);
                            payloadIndex++;
                            testResponse = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                            int responseLength = testResponse.getResponse() != null ? testResponse.getResponse().length : 0;
                            responseCode = testResponse.getResponse() != null ? this.helpers.analyzeResponse(testResponse.getResponse()).getStatusCode() : (short)0;
                            persistedTest = this.callbacks.saveBuffersToTempFiles(testResponse);
                            testEntry = new TestLogEntry(this.testCounter++, origEntry.id, "[" + i + "]", payload, responseLength, 0, responseCode, persistedTest);
                            this.testLog.add(testEntry);
                            SwingUtilities.invokeLater(() -> {
                                ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                                if (this.selectedOriginalId == origEntry.id) {
                                    model.setFilter(origEntry.id);
                                    int rowCount = this.resultTable.getRowCount();
                                    if (rowCount > 0) {
                                        this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                    }
                                }
                            });
                        }
                    }
                    if (this.smsInterfaceTestEnabled && this.isSmsKeyword(str)) {
                        String[] keywords;
                        executed = true;
                        for (String kw : keywords = new String[]{"register", "reg", "regist", "login", "recall", "retrieve", "ret", "true", "false"}) {
                            if (kw.equalsIgnoreCase(str)) continue;
                            newArr = new JSONArray(arr.toString());
                            newArr.put(i, kw);
                            byte[] bodyBytes = newArr.toString().getBytes(StandardCharsets.UTF_8);
                            byte[] newRequest = this.helpers.buildHttpMessage(headers, bodyBytes);
                            testResponse = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                            int responseLength = testResponse.getResponse() != null ? testResponse.getResponse().length : 0;
                            responseCode = testResponse.getResponse() != null ? this.helpers.analyzeResponse(testResponse.getResponse()).getStatusCode() : (short)0;
                            persistedTest = this.callbacks.saveBuffersToTempFiles(testResponse);
                            testEntry = new TestLogEntry(this.testCounter++, origEntry.id, "[" + i + "]_sms", kw, responseLength, 0, responseCode, persistedTest);
                            this.testLog.add(testEntry);
                            SwingUtilities.invokeLater(() -> {
                                ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                                if (this.selectedOriginalId == origEntry.id) {
                                    model.setFilter(origEntry.id);
                                    int rowCount = this.resultTable.getRowCount();
                                    if (rowCount > 0) {
                                        this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                    }
                                }
                            });
                        }
                    }
                    if (!this.smsInterfaceTestEnabled || !this.isInteger(str) || !this.isInRange(str)) continue;
                    executed = true;
                    int origInt = Integer.parseInt(str);
                    for (int j = -100; j <= 100; ++j) {
                        if (j == origInt) continue;
                        JSONArray newArr2 = new JSONArray(arr.toString());
                        newArr2.put(i, String.valueOf(j));
                        byte[] bodyBytes = newArr2.toString().getBytes(StandardCharsets.UTF_8);
                        byte[] newRequest = this.helpers.buildHttpMessage(headers, bodyBytes);
                        IHttpRequestResponse testResponse3 = this.callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), newRequest);
                        int responseLength = testResponse3.getResponse() != null ? testResponse3.getResponse().length : 0;
                        short responseCode3 = testResponse3.getResponse() != null ? this.helpers.analyzeResponse(testResponse3.getResponse()).getStatusCode() : (short)0;
                        IHttpRequestResponsePersisted persistedTest3 = this.callbacks.saveBuffersToTempFiles(testResponse3);
                        TestLogEntry testEntry3 = new TestLogEntry(this.testCounter++, origEntry.id, "[" + i + "]_int", String.valueOf(j), responseLength, 0, responseCode3, persistedTest3);
                        this.testLog.add(testEntry3);
                        SwingUtilities.invokeLater(() -> {
                            ResultTableModel model = (ResultTableModel)this.resultTable.getModel();
                            if (this.selectedOriginalId == origEntry.id) {
                                model.setFilter(origEntry.id);
                                int rowCount = this.resultTable.getRowCount();
                                if (rowCount > 0) {
                                    this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(rowCount - 1, 0, true));
                                }
                            }
                        });
                    }
                    continue;
                }
                if (!(element instanceof JSONObject) && !(element instanceof JSONArray)) continue;
                executed |= this.processJsonAndTest(element, baseRequestResponse, headers, origEntry);
            }
        }
        return executed;
    }

    private Object parseJson(String body) throws JSONException {
        if ((body = body.trim()).startsWith("{")) {
            return new JSONObject(body);
        }
        if (body.startsWith("[")) {
            return new JSONArray(body);
        }
        throw new JSONException("\u4e0d\u662f\u6709\u6548\u7684JSON\u683c\u5f0f");
    }

    private boolean isSmsKeyword(String value) {
        String[] keywords;
        for (String kw : keywords = new String[]{"register", "reg", "regist", "login", "recall", "retrieve", "ret", "true", "false"}) {
            if (!value.equalsIgnoreCase(kw)) continue;
            return true;
        }
        return false;
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isInRange(String value) {
        try {
            int num = Integer.parseInt(value);
            return num >= -100 && num <= 100 && num != 86;
        }
        catch (Exception ex) {
            return false;
        }
    }

    private String alwaysUrlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(String.format("%%%02X", c));
        }
        return sb.toString();
    }

    private String buildMultiPhoneJson(JSONObject obj, String targetKey, String pattern, List<String> numbers) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (String k : obj.keySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("\"").append(k).append("\":");
            if (k.equals(targetKey)) {
                for (String n : numbers) {
                    String p = pattern.replace("xxxxxxxxxxx", n);
                    if (p.contains("18888888888")) {
                        p = p.replace("18888888888", this.customPhoneNumber);
                    }
                    sb.append("\"").append(p).append("\"").append(",");
                }
                sb.append("\"").append(obj.getString(k)).append("\"");
                continue;
            }
            Object v = obj.get(k);
            sb.append(v instanceof String ? "\"" + v + "\"" : v.toString());
        }
        sb.append("}");
        return sb.toString();
    }

    private List<String> showPhoneSelectionDialog(List<String> candidates) {
        JList<String> list = new JList<String>(candidates.toArray(new String[0]));
        list.setSelectionMode(2);
        JScrollPane scroll = new JScrollPane(list);
        int ret = JOptionPane.showConfirmDialog(null, scroll, "\u9009\u62e9\u8981 Fuzz \u7684\u624b\u673a\u53f7", 2);
        if (ret == 0) {
            return list.getSelectedValuesList();
        }
        return Collections.emptyList();
    }

    private String addDuplicateParameterToJsonWithPayload(JSONObject obj, String targetKey, String payload, String dupValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (String k : obj.keySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            Object value = obj.get(k);
            sb.append("\"").append(k).append("\":");
            if (k.equals(targetKey)) {
                sb.append("\"").append(payload).append("\"");
                sb.append(",\"").append(k).append("\":\"").append(dupValue).append("\"");
                continue;
            }
            if (value instanceof JSONObject || value instanceof JSONArray) {
                sb.append(value.toString());
                continue;
            }
            if (value instanceof Number || value instanceof Boolean) {
                sb.append(value.toString());
                continue;
            }
            sb.append("\"").append(value.toString()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private String addJsonDuplicateParameterForCombo(JSONObject obj, String targetKey, String fuzzPattern, String dupValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (String k : obj.keySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            Object value = obj.get(k);
            if (k.equals(targetKey)) {
                String originalValue = value.toString();
                String fuzzedValue = fuzzPattern.replace("xxxxxxxxxxx", originalValue);
                sb.append("\"").append(k).append("\":\"").append(fuzzedValue).append("\"");
                sb.append(",\"").append(k).append("\":\"").append(dupValue).append("\"");
                continue;
            }
            if (value instanceof JSONObject || value instanceof JSONArray) {
                sb.append("\"").append(k).append("\":").append(value.toString());
                continue;
            }
            if (value instanceof Number || value instanceof Boolean) {
                sb.append("\"").append(k).append("\":").append(value.toString());
                continue;
            }
            sb.append("\"").append(k).append("\":\"").append(value.toString()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private List<TestLogEntry> getFilteredTestLog() {
        ArrayList<TestLogEntry> filtered = new ArrayList<TestLogEntry>();
        for (TestLogEntry entry : this.testLog) {
            if (entry.originalId != this.selectedOriginalId) continue;
            filtered.add(entry);
        }
        return filtered;
    }

    private void setupTableCopyFeature() {
        // 添加键盘快捷键支持 (Ctrl+C)
        this.resultTable.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK), "copy");
        this.resultTable.getActionMap().put("copy", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedCells();
            }
        });

        // 添加右键菜单
        javax.swing.JPopupMenu popupMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem copyItem = new javax.swing.JMenuItem("\u590d\u5236");
        copyItem.addActionListener(e -> copySelectedCells());
        popupMenu.add(copyItem);

        this.resultTable.setComponentPopupMenu(popupMenu);
        this.resultTable.setCellSelectionEnabled(true);
    }

    private void copySelectedCells() {
        int[] selectedRows = this.resultTable.getSelectedRows();
        int[] selectedCols = this.resultTable.getSelectedColumns();

        if (selectedRows.length == 0 || selectedCols.length == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedRows.length; i++) {
            for (int j = 0; j < selectedCols.length; j++) {
                Object value = this.resultTable.getValueAt(selectedRows[i], selectedCols[j]);
                sb.append(value != null ? value.toString() : "");
                if (j < selectedCols.length - 1) {
                    sb.append("\t");
                }
            }
            if (i < selectedRows.length - 1) {
                sb.append("\n");
            }
        }

        java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(sb.toString());
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    private byte[] addSmsContentParameter(byte[] request, String sequenceNumber) {
        try {
            IRequestInfo requestInfo = this.helpers.analyzeRequest(request);
            List<IParameter> parameters = requestInfo.getParameters();

            // 检查是否已存在短信内容参数
            for (IParameter param : parameters) {
                if (param.getName().equals(this.customSmsContentParam)) {
                    // 更新现有参数
                    IParameter newParam = this.helpers.buildParameter(this.customSmsContentParam, sequenceNumber, param.getType());
                    return this.helpers.updateParameter(request, newParam);
                }
            }

            // 如果参数不存在，尝试添加到请求体（JSON格式）
            if (requestInfo.getContentType() == IRequestInfo.CONTENT_TYPE_JSON) {
                return this.addSmsContentToJson(request, sequenceNumber);
            }

            // 如果是表单数据，添加为POST参数
            IParameter newParam = this.helpers.buildParameter(this.customSmsContentParam, sequenceNumber, IParameter.PARAM_BODY);
            return this.helpers.addParameter(request, newParam);

        } catch (Exception e) {
            this.callbacks.printError("添加短信内容参数时出错: " + e.getMessage());
            return request;
        }
    }

    private byte[] addSmsContentToJson(byte[] request, String sequenceNumber) {
        try {
            IRequestInfo requestInfo = this.helpers.analyzeRequest(request);
            int bodyOffset = requestInfo.getBodyOffset();

            if (bodyOffset >= request.length) {
                return request;
            }

            String body = new String(Arrays.copyOfRange(request, bodyOffset, request.length), StandardCharsets.UTF_8);
            Object jsonObj = this.parseJson(body);

            if (jsonObj instanceof JSONObject) {
                JSONObject obj = (JSONObject) jsonObj;
                obj.put(this.customSmsContentParam, sequenceNumber);

                List<String> headers = new ArrayList<>(requestInfo.getHeaders());
                byte[] newBody = obj.toString().getBytes(StandardCharsets.UTF_8);
                return this.helpers.buildHttpMessage(headers, newBody);
            }

        } catch (Exception e) {
            this.callbacks.printError("添加短信内容到JSON时出错: " + e.getMessage());
        }

        return request;
    }

    private byte[] addSmsContentToJsonArray(byte[] arrayBodyBytes, String sequenceNumber, List<String> headers) {
        try {
            // 对于JSON数组，我们需要将其包装在一个对象中，或者添加到现有的父对象中
            // 这里我们尝试创建一个包含数组和短信内容参数的新对象
            String arrayBody = new String(arrayBodyBytes, StandardCharsets.UTF_8);
            JSONObject wrapper = new JSONObject();
            wrapper.put("data", new JSONArray(arrayBody));
            wrapper.put(this.customSmsContentParam, sequenceNumber);

            return wrapper.toString().getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            this.callbacks.printError("添加短信内容到JSON数组时出错: " + e.getMessage());
            return arrayBodyBytes;
        }
    }

    @Override
    public int getRowCount() {
        return this.originalLog.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return "#";
            }
            case 1: {
                return "URL";
            }
            case 2: {
                return "\u72b6\u6001";
            }
            case 3: {
                return "\u64cd\u4f5c";
            }
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OriginalLogEntry e = this.originalLog.get(rowIndex);
        switch (columnIndex) {
            case 0: {
                return e.id;
            }
            case 1: {
                return e.url.toString();
            }
            case 2: {
                return e.state;
            }
            case 3: {
                return e.state.startsWith("\u6d4b\u8bd5\u4e2d") ? "\u505c\u6b62" : null;
            }
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 3;
    }

    private class ResultTableModel
    extends AbstractTableModel {
        private List<TestLogEntry> filtered = new ArrayList<TestLogEntry>();

        private ResultTableModel() {
        }

        public void setFilter(int originalId) {
            this.filtered.clear();
            for (TestLogEntry entry : BurpExtender.this.testLog) {
                if (entry.originalId != originalId) continue;
                this.filtered.add(entry);
            }
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return this.filtered.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return "\u53c2\u6570";
                }
                case 1: {
                    return "Payload";
                }
                case 2: {
                    return "\u54cd\u5e94\u957f\u5ea6";
                }
                case 3: {
                    return "\u8017\u65f6(ms)";
                }
                case 4: {
                    return "\u54cd\u5e94\u7801";
                }
            }
            return "";
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TestLogEntry entry = this.filtered.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return entry.parameter;
                }
                case 1: {
                    return entry.payload;
                }
                case 2: {
                    return entry.responseLength;
                }
                case 3: {
                    return entry.responseTime;
                }
                case 4: {
                    return entry.responseCode;
                }
            }
            return "";
        }
    }

    private static class TestLogEntry {
        int id;
        int originalId;
        String parameter;
        String payload;
        int responseLength;
        int responseTime;
        int responseCode;
        IHttpRequestResponsePersisted requestResponse;

        TestLogEntry(int id, int originalId, String parameter, String payload, int responseLength, int responseTime, int responseCode, IHttpRequestResponsePersisted reqResp) {
            this.id = id;
            this.originalId = originalId;
            this.parameter = parameter;
            this.payload = payload;
            this.responseLength = responseLength;
            this.responseTime = responseTime;
            this.responseCode = responseCode;
            this.requestResponse = reqResp;
        }
    }

    private static class OriginalLogEntry {
        int id;
        int tool;
        IHttpRequestResponsePersisted requestResponse;
        URL url;
        String state;

        OriginalLogEntry(int id, IHttpService service, URL url, IHttpRequestResponsePersisted reqResp, String state) {
            this.id = id;
            this.tool = service.getPort();
            this.url = url;
            this.requestResponse = reqResp;
            this.state = state;
        }
    }
}

