package dev.ctool.burp.ui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class BarcodeToolPanel extends JPanel {
    private JTextField inputField;
    private JComboBox<BarcodeType> formatCombo;
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;
    private JSpinner marginSpinner;
    private JColorChooser backgroundColorChooser;
    private JColorChooser lineColorChooser;
    private JCheckBox showTextCheckBox;
    private JComboBox<String> textPositionCombo;
    private JComboBox<String> textAlignCombo;
    private JComboBox<String> fontCombo;
    private JSpinner fontSizeSpinner;
    private JCheckBox boldCheckBox;
    private JCheckBox italicCheckBox;
    private JLabel barcodeLabel;
    private JButton generateButton;
    private JButton saveButton;
    private JButton clearButton;
    
    private BufferedImage currentBarcodeImage;
    
    // 条形码类型枚举
    private enum BarcodeType {
        CODE128("CODE128", BarcodeFormat.CODE_128),
        CODE39("CODE39", BarcodeFormat.CODE_39),
        CODE93("CODE93", BarcodeFormat.CODE_93),
        EAN13("EAN13", BarcodeFormat.EAN_13),
        EAN8("EAN8", BarcodeFormat.EAN_8),
        UPC_A("UPC-A", BarcodeFormat.UPC_A),
        UPC_E("UPC-E", BarcodeFormat.UPC_E),
        ITF("ITF", BarcodeFormat.ITF),
        CODABAR("CODABAR", BarcodeFormat.CODABAR);
        
        private final String displayName;
        private final BarcodeFormat format;
        
        BarcodeType(String displayName, BarcodeFormat format) {
            this.displayName = displayName;
            this.format = format;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        public BarcodeFormat getFormat() {
            return format;
        }
    }
    
    public BarcodeToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        inputField = new JTextField("Example 1234", 20);
        
        formatCombo = new JComboBox<>(BarcodeType.values());
        formatCombo.setSelectedItem(BarcodeType.CODE128);
        
        widthSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
        heightSpinner = new JSpinner(new SpinnerNumberModel(50, 10, 150, 5));
        marginSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 25, 1));
        
        backgroundColorChooser = new JColorChooser(Color.WHITE);
        lineColorChooser = new JColorChooser(Color.BLACK);
        
        showTextCheckBox = new JCheckBox("显示文本", true);
        textPositionCombo = new JComboBox<>(new String[]{"底部", "顶部", "不显示"});
        textAlignCombo = new JComboBox<>(new String[]{"居中", "左对齐", "右对齐"});
        
        fontCombo = new JComboBox<>(new String[]{"Monospaced", "Sans-serif", "Serif"});
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 36, 1));
        boldCheckBox = new JCheckBox("粗体", false);
        italicCheckBox = new JCheckBox("斜体", false);
        
        barcodeLabel = new JLabel();
        barcodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        barcodeLabel.setVerticalAlignment(SwingConstants.CENTER);
        barcodeLabel.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        barcodeLabel.setPreferredSize(new Dimension(400, 200));
        barcodeLabel.setText("条形码将显示在这里");
        
        generateButton = new JButton("生成条形码");
        saveButton = new JButton("保存图片");
        clearButton = new JButton("清空");
        
        saveButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 输入面板
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBorder(new TitledBorder("输入内容"));
        inputPanel.add(new JLabel("内容:"));
        inputPanel.add(inputField);
        inputPanel.add(new JLabel("格式:"));
        inputPanel.add(formatCombo);
        inputPanel.add(generateButton);
        
        // 显示面板
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(new TitledBorder("条形码显示"));
        displayPanel.add(new JScrollPane(barcodeLabel), BorderLayout.CENTER);
        
        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder("配置选项"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 尺寸配置
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("条宽:"), gbc);
        gbc.gridx = 1;
        configPanel.add(widthSpinner, gbc);
        
        gbc.gridx = 2; 
        configPanel.add(new JLabel("高度:"), gbc);
        gbc.gridx = 3;
        configPanel.add(heightSpinner, gbc);
        
        gbc.gridx = 4;
        configPanel.add(new JLabel("边距:"), gbc);
        gbc.gridx = 5;
        configPanel.add(marginSpinner, gbc);
        
        // 颜色配置
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("背景色:"), gbc);
        gbc.gridx = 1;
        JButton bgColorButton = new JButton();
        bgColorButton.setPreferredSize(new Dimension(50, 25));
        bgColorButton.setBackground(Color.WHITE);
        bgColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "选择背景颜色", backgroundColorChooser.getColor());
            if (color != null) {
                backgroundColorChooser.setColor(color);
                bgColorButton.setBackground(color);
            }
        });
        configPanel.add(bgColorButton, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("线条色:"), gbc);
        gbc.gridx = 3;
        JButton lineColorButton = new JButton();
        lineColorButton.setPreferredSize(new Dimension(50, 25));
        lineColorButton.setBackground(Color.BLACK);
        lineColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "选择线条颜色", lineColorChooser.getColor());
            if (color != null) {
                lineColorChooser.setColor(color);
                lineColorButton.setBackground(color);
            }
        });
        configPanel.add(lineColorButton, gbc);
        
        // 文本配置
        gbc.gridx = 0; gbc.gridy = 2;
        configPanel.add(showTextCheckBox, gbc);
        gbc.gridx = 1;
        configPanel.add(new JLabel("位置:"), gbc);
        gbc.gridx = 2;
        configPanel.add(textPositionCombo, gbc);
        gbc.gridx = 3;
        configPanel.add(new JLabel("对齐:"), gbc);
        gbc.gridx = 4;
        configPanel.add(textAlignCombo, gbc);
        
        // 字体配置
        gbc.gridx = 0; gbc.gridy = 3;
        configPanel.add(new JLabel("字体:"), gbc);
        gbc.gridx = 1;
        configPanel.add(fontCombo, gbc);
        gbc.gridx = 2;
        configPanel.add(new JLabel("大小:"), gbc);
        gbc.gridx = 3;
        configPanel.add(fontSizeSpinner, gbc);
        gbc.gridx = 4;
        configPanel.add(boldCheckBox, gbc);
        gbc.gridx = 5;
        configPanel.add(italicCheckBox, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        
        // 左侧面板
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(displayPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 主分割面板
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, configPanel);
        mainSplit.setDividerLocation(500);
        
        add(mainSplit, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBarcode();
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBarcode();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBarcode();
            }
        });
        
        // 文本显示复选框事件
        showTextCheckBox.addActionListener(e -> {
            boolean enabled = showTextCheckBox.isSelected();
            textPositionCombo.setEnabled(enabled);
            textAlignCombo.setEnabled(enabled);
            fontCombo.setEnabled(enabled);
            fontSizeSpinner.setEnabled(enabled);
            boldCheckBox.setEnabled(enabled);
            italicCheckBox.setEnabled(enabled);
        });
    }
    
    private void generateBarcode() {
        String content = inputField.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要生成条形码的内容", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            BarcodeType selectedType = (BarcodeType) formatCombo.getSelectedItem();
            if (selectedType == null) {
                return;
            }
            
            // 验证内容格式
            if (!isValidContent(content, selectedType)) {
                JOptionPane.showMessageDialog(this, 
                    "输入内容不符合 " + selectedType.displayName + " 格式要求", 
                    "格式错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 生成条形码
            BitMatrix bitMatrix = generateBitMatrix(content, selectedType);
            if (bitMatrix == null) {
                return;
            }
            
            // 创建图像
            currentBarcodeImage = createBarcodeImage(bitMatrix, content);
            
            // 显示图像
            ImageIcon icon = new ImageIcon(currentBarcodeImage);
            barcodeLabel.setIcon(icon);
            barcodeLabel.setText("");
            
            saveButton.setEnabled(true);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "生成条形码时发生错误: " + ex.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isValidContent(String content, BarcodeType type) {
        // 基本验证：只允许ASCII字符
        for (char c : content.toCharArray()) {
            if (c < 0 || c > 127) {
                return false;
            }
        }
        
        // 特定格式验证
        switch (type) {
            case EAN13:
                return content.matches("\\d{12,13}");
            case EAN8:
                return content.matches("\\d{7,8}");
            case UPC_A:
                return content.matches("\\d{11,12}");
            case UPC_E:
                return content.matches("\\d{6,8}");
            case CODE39:
                return content.matches("[A-Z0-9 \\-.$/%+]*");
            case ITF:
                return content.matches("\\d*") && content.length() % 2 == 0;
            default:
                return content.length() <= 30; // 通用长度限制
        }
    }
    
    private BitMatrix generateBitMatrix(String content, BarcodeType type) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, marginSpinner.getValue());
        
        int width = 300;
        int height = (Integer) heightSpinner.getValue();
        
        switch (type) {
            case CODE128:
                return new Code128Writer().encode(content, type.getFormat(), width, height, hints);
            case CODE39:
                return new Code39Writer().encode(content, type.getFormat(), width, height, hints);
            case CODE93:
                return new Code93Writer().encode(content, type.getFormat(), width, height, hints);
            case EAN13:
                return new EAN13Writer().encode(content, type.getFormat(), width, height, hints);
            case EAN8:
                return new EAN8Writer().encode(content, type.getFormat(), width, height, hints);
            case UPC_A:
                return new UPCAWriter().encode(content, type.getFormat(), width, height, hints);
            case UPC_E:
                return new UPCEWriter().encode(content, type.getFormat(), width, height, hints);
            case ITF:
                return new ITFWriter().encode(content, type.getFormat(), width, height, hints);
            case CODABAR:
                return new CodaBarWriter().encode(content, type.getFormat(), width, height, hints);
            default:
                throw new WriterException("不支持的条形码格式: " + type.displayName);
        }
    }
    
    private BufferedImage createBarcodeImage(BitMatrix bitMatrix, String content) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        
        Color backgroundColor = backgroundColorChooser.getColor();
        Color lineColor = lineColorChooser.getColor();
        
        // 计算文本高度
        int textHeight = 0;
        if (showTextCheckBox.isSelected() && !"不显示".equals(textPositionCombo.getSelectedItem())) {
            textHeight = (Integer) fontSizeSpinner.getValue() + 10;
        }
        
        BufferedImage image = new BufferedImage(width, height + textHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 填充背景
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height + textHeight);
        
        // 绘制条形码
        int yOffset = "顶部".equals(textPositionCombo.getSelectedItem()) ? textHeight : 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitMatrix.get(x, y)) {
                    g2d.setColor(lineColor);
                    g2d.fillRect(x, y + yOffset, 1, 1);
                }
            }
        }
        
        // 绘制文本
        if (showTextCheckBox.isSelected() && !"不显示".equals(textPositionCombo.getSelectedItem())) {
            String fontName = (String) fontCombo.getSelectedItem();
            int fontSize = (Integer) fontSizeSpinner.getValue();
            int fontStyle = Font.PLAIN;
            if (boldCheckBox.isSelected()) fontStyle |= Font.BOLD;
            if (italicCheckBox.isSelected()) fontStyle |= Font.ITALIC;
            
            Font font = new Font(fontName, fontStyle, fontSize);
            g2d.setFont(font);
            g2d.setColor(lineColor);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(content);
            
            int textX;
            String alignment = (String) textAlignCombo.getSelectedItem();
            switch (alignment) {
                case "左对齐":
                    textX = 5;
                    break;
                case "右对齐":
                    textX = width - textWidth - 5;
                    break;
                default: // 居中
                    textX = (width - textWidth) / 2;
                    break;
            }
            
            int textY;
            if ("顶部".equals(textPositionCombo.getSelectedItem())) {
                textY = fontSize;
            } else {
                textY = height + yOffset + fontSize;
            }
            
            g2d.drawString(content, textX, textY);
        }
        
        g2d.dispose();
        return image;
    }
    
    private void saveBarcode() {
        if (currentBarcodeImage == null) {
            JOptionPane.showMessageDialog(this, "没有可保存的条形码图片", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存条形码图片");
        fileChooser.setSelectedFile(new File("barcode.png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String fileName = file.getName().toLowerCase();
                String format = "png";
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    format = "jpg";
                }
                
                ImageIO.write(currentBarcodeImage, format, file);
                JOptionPane.showMessageDialog(this, "条形码图片保存成功: " + file.getAbsolutePath(),
                    "保存成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存图片时发生错误: " + ex.getMessage(),
                    "保存失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearBarcode() {
        inputField.setText("Example 1234");
        barcodeLabel.setIcon(null);
        barcodeLabel.setText("条形码将显示在这里");
        currentBarcodeImage = null;
        saveButton.setEnabled(false);
    }
}