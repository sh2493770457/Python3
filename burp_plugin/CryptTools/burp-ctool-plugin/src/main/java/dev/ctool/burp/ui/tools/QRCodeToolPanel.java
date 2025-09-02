package dev.ctool.burp.ui.tools;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * QR码工具面板
 */
public class QRCodeToolPanel extends JPanel {
    
    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JLabel qrImageLabel;
    private JComboBox<String> sizeComboBox;
    private JComboBox<ErrorCorrectionLevel> errorCorrectionComboBox;
    private JRadioButton generateRadio;
    private JRadioButton parseRadio;
    private BufferedImage currentQRImage;
    
    private final String[] sizes = {
        "100x100", "150x150", "200x200", "250x250", "300x300", "400x400", "500x500"
    };
    
    public QRCodeToolPanel(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建选项面板
        JPanel optionPanel = createOptionPanel();
        
        // 创建显示面板
        JPanel displayPanel = createDisplayPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.CENTER);
        leftPanel.add(optionPanel, BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, displayPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输入内容"));
        
        inputArea = new JTextArea(8, 30);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("选项设置"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 操作类型
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        
        generateRadio = new JRadioButton("生成二维码", true);
        parseRadio = new JRadioButton("解析二维码", false);
        ButtonGroup operationGroup = new ButtonGroup();
        operationGroup.add(generateRadio);
        operationGroup.add(parseRadio);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.add(generateRadio);
        radioPanel.add(Box.createHorizontalStrut(10));
        radioPanel.add(parseRadio);
        panel.add(radioPanel, gbc);
        
        // 尺寸设置
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("尺寸:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sizeComboBox = new JComboBox<>(sizes);
        sizeComboBox.setSelectedItem("200x200");
        panel.add(sizeComboBox, gbc);
        
        // 纠错级别
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("纠错级别:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        errorCorrectionComboBox = new JComboBox<>(ErrorCorrectionLevel.values());
        errorCorrectionComboBox.setSelectedItem(ErrorCorrectionLevel.M);
        panel.add(errorCorrectionComboBox, gbc);
        
        return panel;
    }
    
    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("二维码显示"));
        
        // 图片显示区域
        qrImageLabel = new JLabel();
        qrImageLabel.setHorizontalAlignment(JLabel.CENTER);
        qrImageLabel.setVerticalAlignment(JLabel.CENTER);
        qrImageLabel.setPreferredSize(new java.awt.Dimension(300, 300));
        qrImageLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        qrImageLabel.setText("二维码将显示在这里");
        
        JScrollPane imageScrollPane = new JScrollPane(qrImageLabel);
        panel.add(imageScrollPane, BorderLayout.CENTER);
        
        // 输出文本区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("解析结果"));
        
        outputArea = new JTextArea(6, 30);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        JSplitPane displaySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, outputPanel);
        displaySplitPane.setDividerLocation(300);
        displaySplitPane.setResizeWeight(0.7);
        
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(displaySplitPane, BorderLayout.CENTER);
        
        return containerPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton processButton = new JButton("执行");
        processButton.addActionListener(new ProcessActionListener());
        
        JButton loadImageButton = new JButton("加载图片");
        loadImageButton.addActionListener(e -> loadImageFile());
        
        JButton saveImageButton = new JButton("保存图片");
        saveImageButton.addActionListener(e -> saveImageFile());
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
            qrImageLabel.setIcon(null);
            qrImageLabel.setText("二维码将显示在这里");
            currentQRImage = null;
        });
        
        JButton copyButton = new JButton("复制结果");
        copyButton.addActionListener(e -> {
            if (!outputArea.getText().trim().isEmpty()) {
                outputArea.selectAll();
                outputArea.copy();
                JOptionPane.showMessageDialog(this, "已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        panel.add(processButton);
        panel.add(loadImageButton);
        panel.add(saveImageButton);
        panel.add(clearButton);
        panel.add(copyButton);
        
        return panel;
    }
    
    private class ProcessActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (generateRadio.isSelected()) {
                generateQRCode();
            } else {
                parseQRCode();
            }
        }
    }
    
    private void generateQRCode() {
        String content = inputArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要生成二维码的内容", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String sizeStr = (String) sizeComboBox.getSelectedItem();
            String[] sizeParts = sizeStr.split("x");
            int width = Integer.parseInt(sizeParts[0]);
            int height = Integer.parseInt(sizeParts[1]);
            
            ErrorCorrectionLevel errorCorrection = (ErrorCorrectionLevel) errorCorrectionComboBox.getSelectedItem();
            
            // 设置二维码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrection);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            
            // 生成二维码
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            
            // 转换为图片
            currentQRImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            // 显示图片
            ImageIcon icon = new ImageIcon(currentQRImage);
            qrImageLabel.setIcon(icon);
            qrImageLabel.setText("");
            
            // 显示生成信息
            StringBuilder result = new StringBuilder();
            result.append("二维码生成成功\n");
            result.append("内容: ").append(content).append("\n");
            result.append("尺寸: ").append(width).append("x").append(height).append("\n");
            result.append("纠错级别: ").append(errorCorrection).append("\n");
            result.append("字符编码: UTF-8\n");
            result.append("内容长度: ").append(content.length()).append(" 字符\n");
            
            outputArea.setText(result.toString());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "生成二维码时发生错误: " + ex.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
            outputArea.setText("生成错误: " + ex.getMessage());
        }
    }
    
    private void parseQRCode() {
        if (currentQRImage == null) {
            JOptionPane.showMessageDialog(this, "请先加载二维码图片或生成二维码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // 解析二维码
            LuminanceSource source = new BufferedImageLuminanceSource(currentQRImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(bitmap);
            
            String content = result.getText();
            inputArea.setText(content);
            
            // 显示解析信息
            StringBuilder info = new StringBuilder();
            info.append("二维码解析成功\n");
            info.append("内容: ").append(content).append("\n");
            info.append("格式: ").append(result.getBarcodeFormat()).append("\n");
            info.append("内容长度: ").append(content.length()).append(" 字符\n");
            
            if (result.getResultMetadata() != null) {
                info.append("\n元数据:\n");
                result.getResultMetadata().forEach((key, value) -> 
                    info.append(key).append(": ").append(value).append("\n"));
            }
            
            outputArea.setText(info.toString());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "解析二维码时发生错误: " + ex.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
            outputArea.setText("解析错误: " + ex.getMessage());
        }
    }
    
    private void loadImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("图片文件", "png", "jpg", "jpeg", "gif", "bmp"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                currentQRImage = ImageIO.read(file);
                ImageIcon icon = new ImageIcon(currentQRImage);
                
                // 调整图片大小以适应显示区域
                if (currentQRImage.getWidth() > 300 || currentQRImage.getHeight() > 300) {
                    Image scaledImage = currentQRImage.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledImage);
                }
                
                qrImageLabel.setIcon(icon);
                qrImageLabel.setText("");
                
                outputArea.setText("图片加载成功: " + file.getName() + "\n" +
                                 "尺寸: " + currentQRImage.getWidth() + "x" + currentQRImage.getHeight());
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "加载图片时发生错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveImageFile() {
        if (currentQRImage == null) {
            JOptionPane.showMessageDialog(this, "没有可保存的二维码图片", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG图片", "png"));
        fileChooser.setSelectedFile(new File("qrcode.png"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // 确保文件扩展名为.png
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                
                ImageIO.write(currentQRImage, "PNG", file);
                JOptionPane.showMessageDialog(this, "二维码图片保存成功: " + file.getAbsolutePath(), 
                    "提示", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "保存图片时发生错误: " + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}