package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

public class WebSocketToolPanel extends JPanel {
    private JTextField urlField;
    private JTextArea messageArea;
    private JTextArea logArea;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton sendButton;
    private JButton clearLogButton;
    private JButton clearMessageButton;
    private JLabel statusLabel;
    private JComboBox<String> messageTypeCombo;
    
    private WebSocket webSocket;
    private boolean isConnected = false;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    public WebSocketToolPanel() {
        initComponents();
        layoutComponents();
        addEventListeners();
        updateButtonStates();
    }
    
    private void initComponents() {
        // URL输入
        urlField = new JTextField("ws://localhost:8080/websocket", 30);
        
        // 消息类型
        String[] messageTypes = {"文本消息", "二进制消息", "Ping", "Pong"};
        messageTypeCombo = new JComboBox<>(messageTypes);
        
        // 消息输入区域
        messageArea = new JTextArea(5, 50);
        messageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        messageArea.setText("Hello WebSocket!");
        
        // 日志区域
        logArea = new JTextArea(15, 50);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBackground(new Color(248, 248, 248));
        
        // 按钮
        connectButton = new JButton("连接");
        disconnectButton = new JButton("断开连接");
        sendButton = new JButton("发送消息");
        clearLogButton = new JButton("清空日志");
        clearMessageButton = new JButton("清空消息");
        
        // 状态标签
        statusLabel = new JLabel("状态: 未连接");
        statusLabel.setForeground(Color.RED);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // 顶部连接面板
        JPanel connectionPanel = new JPanel(new BorderLayout());
        connectionPanel.setBorder(new TitledBorder("WebSocket连接"));
        
        JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urlPanel.add(new JLabel("URL:"));
        urlPanel.add(urlField);
        urlPanel.add(connectButton);
        urlPanel.add(disconnectButton);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        
        connectionPanel.add(urlPanel, BorderLayout.NORTH);
        connectionPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // 消息发送面板
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(new TitledBorder("消息发送"));
        
        JPanel messageControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messageControlPanel.add(new JLabel("类型:"));
        messageControlPanel.add(messageTypeCombo);
        messageControlPanel.add(sendButton);
        messageControlPanel.add(clearMessageButton);
        
        messagePanel.add(messageControlPanel, BorderLayout.NORTH);
        messagePanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        
        // 日志面板
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new TitledBorder("连接日志"));
        
        JPanel logControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logControlPanel.add(clearLogButton);
        
        logPanel.add(logControlPanel, BorderLayout.NORTH);
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        
        // 主分割面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(connectionPanel, BorderLayout.NORTH);
        topPanel.add(messagePanel, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, logPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void addEventListeners() {
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectWebSocket();
            }
        });
        
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectWebSocket();
            }
        });
        
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        clearLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
            }
        });
        
        clearMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageArea.setText("");
            }
        });
    }
    
    private void updateButtonStates() {
        connectButton.setEnabled(!isConnected);
        disconnectButton.setEnabled(isConnected);
        sendButton.setEnabled(isConnected);
    }
    
    private void connectWebSocket() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            appendLog("错误: 请输入WebSocket URL");
            return;
        }
        
        try {
            URI uri = URI.create(url);
            HttpClient client = HttpClient.newHttpClient();
            
            appendLog("正在连接到: " + url);
            
            CompletableFuture<WebSocket> webSocketFuture = client.newWebSocketBuilder()
                .buildAsync(uri, new WebSocketListener());
            
            webSocketFuture.thenAccept(ws -> {
                webSocket = ws;
                isConnected = true;
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("状态: 已连接");
                    statusLabel.setForeground(Color.GREEN);
                    updateButtonStates();
                    appendLog("WebSocket连接成功");
                });
            }).exceptionally(throwable -> {
                SwingUtilities.invokeLater(() -> {
                    appendLog("连接失败: " + throwable.getMessage());
                });
                return null;
            });
            
        } catch (Exception e) {
            appendLog("连接错误: " + e.getMessage());
        }
    }
    
    private void disconnectWebSocket() {
        if (webSocket != null) {
            try {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "客户端主动断开连接");
                appendLog("正在断开连接...");
            } catch (Exception e) {
                appendLog("断开连接时出错: " + e.getMessage());
            }
        }
        
        isConnected = false;
        webSocket = null;
        statusLabel.setText("状态: 未连接");
        statusLabel.setForeground(Color.RED);
        updateButtonStates();
    }
    
    private void sendMessage() {
        if (webSocket == null || !isConnected) {
            appendLog("错误: WebSocket未连接");
            return;
        }
        
        String message = messageArea.getText();
        String messageType = (String) messageTypeCombo.getSelectedItem();
        
        try {
            switch (messageType) {
                case "文本消息":
                    webSocket.sendText(message, true);
                    appendLog("发送文本消息: " + message);
                    break;
                case "二进制消息":
                    byte[] bytes = message.getBytes("UTF-8");
                    webSocket.sendBinary(ByteBuffer.wrap(bytes), true);
                    appendLog("发送二进制消息: " + bytes.length + " 字节");
                    break;
                case "Ping":
                    webSocket.sendPing(ByteBuffer.wrap(message.getBytes("UTF-8")));
                    appendLog("发送Ping: " + message);
                    break;
                case "Pong":
                    webSocket.sendPong(ByteBuffer.wrap(message.getBytes("UTF-8")));
                    appendLog("发送Pong: " + message);
                    break;
            }
        } catch (Exception e) {
            appendLog("发送消息失败: " + e.getMessage());
        }
    }
    
    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = timeFormat.format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    private class WebSocketListener implements WebSocket.Listener {
        @Override
        public void onOpen(WebSocket webSocket) {
            appendLog("WebSocket连接已打开");
            webSocket.request(1);
        }
        
        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            appendLog("收到文本消息: " + data.toString());
            webSocket.request(1);
            return null;
        }
        
        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            byte[] bytes = new byte[data.remaining()];
            data.get(bytes);
            appendLog("收到二进制消息: " + bytes.length + " 字节 - " + new String(bytes));
            webSocket.request(1);
            return null;
        }
        
        @Override
        public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
            appendLog("收到Ping: " + new String(message.array()));
            webSocket.request(1);
            return null;
        }
        
        @Override
        public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
            appendLog("收到Pong: " + new String(message.array()));
            webSocket.request(1);
            return null;
        }
        
        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            SwingUtilities.invokeLater(() -> {
                isConnected = false;
                statusLabel.setText("状态: 未连接");
                statusLabel.setForeground(Color.RED);
                updateButtonStates();
                appendLog("WebSocket连接已关闭 - 状态码: " + statusCode + ", 原因: " + reason);
            });
            return null;
        }
        
        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            appendLog("WebSocket错误: " + error.getMessage());
        }
    }
}