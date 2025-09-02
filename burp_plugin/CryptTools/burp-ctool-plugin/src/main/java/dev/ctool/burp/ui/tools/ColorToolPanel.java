package dev.ctool.burp.ui.tools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorToolPanel extends JPanel {
    private JTabbedPane tabbedPane;
    
    // 颜色转换选项卡
    private JTextField hexField;
    private JTextField rgbField;
    private JTextField hslField;
    private JTextField hsvField;
    private JTextField cmykField;
    private JPanel colorPreviewPanel;
    
    // 颜色选择器选项卡
    private JColorChooser colorChooser;
    private JTextField selectedHexField;
    private JTextField selectedRgbField;
    private JTextField selectedHslField;
    private JTextField selectedHsvField;
    private JTextField selectedCmykField;
    
    // 颜色调色板选项卡
    private JSlider redSlider, greenSlider, blueSlider;
    private JSlider hueSlider, saturationSlider, lightnessSlider;
    private JTextField paletteHexField;
    private JPanel palettePreviewPanel;
    private JLabel rgbLabel, hslLabel;
    
    private Color currentColor = Color.WHITE;

    public ColorToolPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("颜色工具"));

        tabbedPane = new JTabbedPane();
        
        // 颜色转换选项卡
        JPanel convertPanel = createConvertPanel();
        tabbedPane.addTab("颜色转换", convertPanel);
        
        // 颜色选择器选项卡
        JPanel pickerPanel = createPickerPanel();
        tabbedPane.addTab("颜色选择器", pickerPanel);
        
        // 颜色调色板选项卡
        JPanel palettePanel = createPalettePanel();
        tabbedPane.addTab("调色板", palettePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createConvertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("颜色格式转换"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // HEX
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("HEX:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hexField = new JTextField("#FFFFFF", 15);
        hexField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputPanel.add(hexField, gbc);
        
        // RGB
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("RGB:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        rgbField = new JTextField("rgb(255, 255, 255)", 15);
        rgbField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputPanel.add(rgbField, gbc);
        
        // HSL
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("HSL:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hslField = new JTextField("hsl(0, 0%, 100%)", 15);
        hslField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputPanel.add(hslField, gbc);
        
        // HSV
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("HSV:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hsvField = new JTextField("hsv(0, 0%, 100%)", 15);
        hsvField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputPanel.add(hsvField, gbc);
        
        // CMYK
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("CMYK:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmykField = new JTextField("cmyk(0%, 0%, 0%, 0%)", 15);
        cmykField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputPanel.add(cmykField, gbc);
        
        // 颜色预览
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(new TitledBorder("颜色预览"));
        
        colorPreviewPanel = new JPanel();
        colorPreviewPanel.setBackground(Color.WHITE);
        colorPreviewPanel.setPreferredSize(new Dimension(200, 100));
        colorPreviewPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        previewPanel.add(colorPreviewPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton convertFromHexButton = new JButton("从HEX转换");
        convertFromHexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertFromHex();
            }
        });
        
        JButton convertFromRgbButton = new JButton("从RGB转换");
        convertFromRgbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertFromRgb();
            }
        });
        
        JButton convertFromHslButton = new JButton("从HSL转换");
        convertFromHslButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertFromHsl();
            }
        });
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearConvertFields();
            }
        });
        
        JButton copyAllButton = new JButton("复制所有格式");
        copyAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyAllFormats();
            }
        });
        
        buttonPanel.add(convertFromHexButton);
        buttonPanel.add(convertFromRgbButton);
        buttonPanel.add(convertFromHslButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(copyAllButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(previewPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createPickerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 颜色选择器
        colorChooser = new JColorChooser(Color.WHITE);
        colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateSelectedColorFields();
            }
        });
        
        // 颜色信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(new TitledBorder("选中颜色信息"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // HEX
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("HEX:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        selectedHexField = new JTextField(15);
        selectedHexField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        selectedHexField.setEditable(false);
        infoPanel.add(selectedHexField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton copyHexButton = new JButton("复制");
        copyHexButton.addActionListener(e -> copyToClipboard(selectedHexField.getText()));
        infoPanel.add(copyHexButton, gbc);
        
        // RGB
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("RGB:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        selectedRgbField = new JTextField(15);
        selectedRgbField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        selectedRgbField.setEditable(false);
        infoPanel.add(selectedRgbField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton copyRgbButton = new JButton("复制");
        copyRgbButton.addActionListener(e -> copyToClipboard(selectedRgbField.getText()));
        infoPanel.add(copyRgbButton, gbc);
        
        // HSL
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("HSL:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        selectedHslField = new JTextField(15);
        selectedHslField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        selectedHslField.setEditable(false);
        infoPanel.add(selectedHslField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton copyHslButton = new JButton("复制");
        copyHslButton.addActionListener(e -> copyToClipboard(selectedHslField.getText()));
        infoPanel.add(copyHslButton, gbc);
        
        // HSV
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("HSV:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        selectedHsvField = new JTextField(15);
        selectedHsvField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        selectedHsvField.setEditable(false);
        infoPanel.add(selectedHsvField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton copyHsvButton = new JButton("复制");
        copyHsvButton.addActionListener(e -> copyToClipboard(selectedHsvField.getText()));
        infoPanel.add(copyHsvButton, gbc);
        
        // CMYK
        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(new JLabel("CMYK:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        selectedCmykField = new JTextField(15);
        selectedCmykField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        selectedCmykField.setEditable(false);
        infoPanel.add(selectedCmykField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton copyCmykButton = new JButton("复制");
        copyCmykButton.addActionListener(e -> copyToClipboard(selectedCmykField.getText()));
        infoPanel.add(copyCmykButton, gbc);
        
        panel.add(colorChooser, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        // 初始化颜色信息
        updateSelectedColorFields();
        
        return panel;
    }

    private JPanel createPalettePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // RGB滑块面板
        JPanel rgbPanel = new JPanel(new GridBagLayout());
        rgbPanel.setBorder(new TitledBorder("RGB调色"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Red滑块
        gbc.gridx = 0; gbc.gridy = 0;
        rgbPanel.add(new JLabel("Red:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        redSlider = new JSlider(0, 255, 255);
        redSlider.addChangeListener(e -> updatePaletteColor());
        rgbPanel.add(redSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel redLabel = new JLabel("255");
        redSlider.addChangeListener(e -> redLabel.setText(String.valueOf(redSlider.getValue())));
        rgbPanel.add(redLabel, gbc);
        
        // Green滑块
        gbc.gridx = 0; gbc.gridy = 1;
        rgbPanel.add(new JLabel("Green:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        greenSlider = new JSlider(0, 255, 255);
        greenSlider.addChangeListener(e -> updatePaletteColor());
        rgbPanel.add(greenSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel greenLabel = new JLabel("255");
        greenSlider.addChangeListener(e -> greenLabel.setText(String.valueOf(greenSlider.getValue())));
        rgbPanel.add(greenLabel, gbc);
        
        // Blue滑块
        gbc.gridx = 0; gbc.gridy = 2;
        rgbPanel.add(new JLabel("Blue:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        blueSlider = new JSlider(0, 255, 255);
        blueSlider.addChangeListener(e -> updatePaletteColor());
        rgbPanel.add(blueSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel blueLabel = new JLabel("255");
        blueSlider.addChangeListener(e -> blueLabel.setText(String.valueOf(blueSlider.getValue())));
        rgbPanel.add(blueLabel, gbc);
        
        // HSL滑块面板
        JPanel hslPanel = new JPanel(new GridBagLayout());
        hslPanel.setBorder(new TitledBorder("HSL调色"));
        
        // Hue滑块
        gbc.gridx = 0; gbc.gridy = 0;
        hslPanel.add(new JLabel("Hue:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hueSlider = new JSlider(0, 360, 0);
        hueSlider.addChangeListener(e -> updatePaletteColorFromHsl());
        hslPanel.add(hueSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel hueLabel = new JLabel("0°");
        hueSlider.addChangeListener(e -> hueLabel.setText(hueSlider.getValue() + "°"));
        hslPanel.add(hueLabel, gbc);
        
        // Saturation滑块
        gbc.gridx = 0; gbc.gridy = 1;
        hslPanel.add(new JLabel("Saturation:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        saturationSlider = new JSlider(0, 100, 0);
        saturationSlider.addChangeListener(e -> updatePaletteColorFromHsl());
        hslPanel.add(saturationSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel saturationLabel = new JLabel("0%");
        saturationSlider.addChangeListener(e -> saturationLabel.setText(saturationSlider.getValue() + "%"));
        hslPanel.add(saturationLabel, gbc);
        
        // Lightness滑块
        gbc.gridx = 0; gbc.gridy = 2;
        hslPanel.add(new JLabel("Lightness:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lightnessSlider = new JSlider(0, 100, 100);
        lightnessSlider.addChangeListener(e -> updatePaletteColorFromHsl());
        hslPanel.add(lightnessSlider, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lightnessLabel = new JLabel("100%");
        lightnessSlider.addChangeListener(e -> lightnessLabel.setText(lightnessSlider.getValue() + "%"));
        hslPanel.add(lightnessLabel, gbc);
        
        // 滑块容器
        JPanel slidersPanel = new JPanel(new GridLayout(2, 1));
        slidersPanel.add(rgbPanel);
        slidersPanel.add(hslPanel);
        
        // 预览和信息面板
        JPanel infoPanel = new JPanel(new BorderLayout());
        
        // 颜色预览
        palettePreviewPanel = new JPanel();
        palettePreviewPanel.setBackground(Color.WHITE);
        palettePreviewPanel.setPreferredSize(new Dimension(200, 80));
        palettePreviewPanel.setBorder(BorderFactory.createTitledBorder("颜色预览"));
        
        // 颜色信息
        JPanel colorInfoPanel = new JPanel(new GridLayout(3, 2));
        colorInfoPanel.setBorder(new TitledBorder("颜色信息"));
        
        colorInfoPanel.add(new JLabel("HEX:"));
        paletteHexField = new JTextField();
        paletteHexField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        paletteHexField.setEditable(false);
        colorInfoPanel.add(paletteHexField);
        
        colorInfoPanel.add(new JLabel("RGB:"));
        rgbLabel = new JLabel();
        rgbLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        colorInfoPanel.add(rgbLabel);
        
        colorInfoPanel.add(new JLabel("HSL:"));
        hslLabel = new JLabel();
        hslLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        colorInfoPanel.add(hslLabel);
        
        infoPanel.add(palettePreviewPanel, BorderLayout.NORTH);
        infoPanel.add(colorInfoPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton copyPaletteHexButton = new JButton("复制HEX");
        copyPaletteHexButton.addActionListener(e -> copyToClipboard(paletteHexField.getText()));
        
        JButton resetButton = new JButton("重置为白色");
        resetButton.addActionListener(e -> resetPalette());
        
        buttonPanel.add(copyPaletteHexButton);
        buttonPanel.add(resetButton);
        
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(slidersPanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.EAST);
        
        // 初始化调色板
        updatePaletteColor();
        
        return panel;
    }

    // 颜色转换方法
    private void convertFromHex() {
        try {
            String hex = hexField.getText().trim();
            if (!hex.startsWith("#")) {
                hex = "#" + hex;
            }
            
            Color color = Color.decode(hex);
            updateAllFields(color);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "无效的HEX颜色格式", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void convertFromRgb() {
        try {
            String rgb = rgbField.getText().trim();
            Pattern pattern = Pattern.compile("rgb\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
            Matcher matcher = pattern.matcher(rgb);
            
            if (matcher.matches()) {
                int r = Integer.parseInt(matcher.group(1));
                int g = Integer.parseInt(matcher.group(2));
                int b = Integer.parseInt(matcher.group(3));
                
                if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
                    Color color = new Color(r, g, b);
                    updateAllFields(color);
                } else {
                    throw new IllegalArgumentException("RGB值必须在0-255之间");
                }
            } else {
                throw new IllegalArgumentException("RGB格式应为: rgb(r, g, b)");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "无效的RGB颜色格式: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void convertFromHsl() {
        try {
            String hsl = hslField.getText().trim();
            Pattern pattern = Pattern.compile("hsl\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)%\\s*,\\s*(\\d+)%\\s*\\)");
            Matcher matcher = pattern.matcher(hsl);
            
            if (matcher.matches()) {
                int h = Integer.parseInt(matcher.group(1));
                int s = Integer.parseInt(matcher.group(2));
                int l = Integer.parseInt(matcher.group(3));
                
                if (h >= 0 && h <= 360 && s >= 0 && s <= 100 && l >= 0 && l <= 100) {
                    Color color = hslToRgb(h, s / 100.0f, l / 100.0f);
                    updateAllFields(color);
                } else {
                    throw new IllegalArgumentException("HSL值范围: H(0-360), S(0-100%), L(0-100%)");
                }
            } else {
                throw new IllegalArgumentException("HSL格式应为: hsl(h, s%, l%)");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "无效的HSL颜色格式: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAllFields(Color color) {
        currentColor = color;
        
        // 更新HEX
        hexField.setText(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
        
        // 更新RGB
        rgbField.setText(String.format("rgb(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue()));
        
        // 更新HSL
        float[] hsl = rgbToHsl(color.getRed(), color.getGreen(), color.getBlue());
        hslField.setText(String.format("hsl(%d, %d%%, %d%%)", 
            Math.round(hsl[0]), Math.round(hsl[1] * 100), Math.round(hsl[2] * 100)));
        
        // 更新HSV
        float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsvField.setText(String.format("hsv(%d, %d%%, %d%%)", 
            Math.round(hsv[0] * 360), Math.round(hsv[1] * 100), Math.round(hsv[2] * 100)));
        
        // 更新CMYK
        float[] cmyk = rgbToCmyk(color.getRed(), color.getGreen(), color.getBlue());
        cmykField.setText(String.format("cmyk(%d%%, %d%%, %d%%, %d%%)", 
            Math.round(cmyk[0] * 100), Math.round(cmyk[1] * 100), Math.round(cmyk[2] * 100), Math.round(cmyk[3] * 100)));
        
        // 更新预览
        colorPreviewPanel.setBackground(color);
        colorPreviewPanel.repaint();
    }

    private void updateSelectedColorFields() {
        Color color = colorChooser.getColor();
        
        selectedHexField.setText(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
        selectedRgbField.setText(String.format("rgb(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue()));
        
        float[] hsl = rgbToHsl(color.getRed(), color.getGreen(), color.getBlue());
        selectedHslField.setText(String.format("hsl(%d, %d%%, %d%%)", 
            Math.round(hsl[0]), Math.round(hsl[1] * 100), Math.round(hsl[2] * 100)));
        
        float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        selectedHsvField.setText(String.format("hsv(%d, %d%%, %d%%)", 
            Math.round(hsv[0] * 360), Math.round(hsv[1] * 100), Math.round(hsv[2] * 100)));
        
        float[] cmyk = rgbToCmyk(color.getRed(), color.getGreen(), color.getBlue());
        selectedCmykField.setText(String.format("cmyk(%d%%, %d%%, %d%%, %d%%)", 
            Math.round(cmyk[0] * 100), Math.round(cmyk[1] * 100), Math.round(cmyk[2] * 100), Math.round(cmyk[3] * 100)));
    }

    private void updatePaletteColor() {
        Color color = new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
        updatePaletteDisplay(color);
        
        // 同步HSL滑块
        float[] hsl = rgbToHsl(color.getRed(), color.getGreen(), color.getBlue());
        hueSlider.removeChangeListener(hueSlider.getChangeListeners()[0]);
        saturationSlider.removeChangeListener(saturationSlider.getChangeListeners()[0]);
        lightnessSlider.removeChangeListener(lightnessSlider.getChangeListeners()[0]);
        
        hueSlider.setValue(Math.round(hsl[0]));
        saturationSlider.setValue(Math.round(hsl[1] * 100));
        lightnessSlider.setValue(Math.round(hsl[2] * 100));
        
        hueSlider.addChangeListener(e -> updatePaletteColorFromHsl());
        saturationSlider.addChangeListener(e -> updatePaletteColorFromHsl());
        lightnessSlider.addChangeListener(e -> updatePaletteColorFromHsl());
    }

    private void updatePaletteColorFromHsl() {
        Color color = hslToRgb(hueSlider.getValue(), saturationSlider.getValue() / 100.0f, lightnessSlider.getValue() / 100.0f);
        updatePaletteDisplay(color);
        
        // 同步RGB滑块
        redSlider.removeChangeListener(redSlider.getChangeListeners()[0]);
        greenSlider.removeChangeListener(greenSlider.getChangeListeners()[0]);
        blueSlider.removeChangeListener(blueSlider.getChangeListeners()[0]);
        
        redSlider.setValue(color.getRed());
        greenSlider.setValue(color.getGreen());
        blueSlider.setValue(color.getBlue());
        
        redSlider.addChangeListener(e -> updatePaletteColor());
        greenSlider.addChangeListener(e -> updatePaletteColor());
        blueSlider.addChangeListener(e -> updatePaletteColor());
    }

    private void updatePaletteDisplay(Color color) {
        paletteHexField.setText(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
        rgbLabel.setText(String.format("rgb(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue()));
        
        float[] hsl = rgbToHsl(color.getRed(), color.getGreen(), color.getBlue());
        hslLabel.setText(String.format("hsl(%d, %d%%, %d%%)", 
            Math.round(hsl[0]), Math.round(hsl[1] * 100), Math.round(hsl[2] * 100)));
        
        palettePreviewPanel.setBackground(color);
        palettePreviewPanel.repaint();
    }

    private void resetPalette() {
        redSlider.setValue(255);
        greenSlider.setValue(255);
        blueSlider.setValue(255);
        hueSlider.setValue(0);
        saturationSlider.setValue(0);
        lightnessSlider.setValue(100);
    }

    // 颜色空间转换辅助方法
    private float[] rgbToHsl(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;
        
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;
        
        float h = 0;
        float s = 0;
        float l = (max + min) / 2;
        
        if (delta != 0) {
            s = l > 0.5 ? delta / (2 - max - min) : delta / (max + min);
            
            if (max == rf) {
                h = ((gf - bf) / delta + (gf < bf ? 6 : 0)) / 6;
            } else if (max == gf) {
                h = ((bf - rf) / delta + 2) / 6;
            } else {
                h = ((rf - gf) / delta + 4) / 6;
            }
        }
        
        return new float[]{h * 360, s, l};
    }

    private Color hslToRgb(float h, float s, float l) {
        h = h / 360.0f;
        
        float r, g, b;
        
        if (s == 0) {
            r = g = b = l;
        } else {
            float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1.0f/3.0f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f/3.0f);
        }
        
        return new Color(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
    }

    private float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0f/6.0f) return p + (q - p) * 6 * t;
        if (t < 1.0f/2.0f) return q;
        if (t < 2.0f/3.0f) return p + (q - p) * (2.0f/3.0f - t) * 6;
        return p;
    }

    private float[] rgbToCmyk(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;
        
        float k = 1 - Math.max(rf, Math.max(gf, bf));
        float c = k == 1 ? 0 : (1 - rf - k) / (1 - k);
        float m = k == 1 ? 0 : (1 - gf - k) / (1 - k);
        float y = k == 1 ? 0 : (1 - bf - k) / (1 - k);
        
        return new float[]{c, m, y, k};
    }

    // 清空和复制方法
    private void clearConvertFields() {
        hexField.setText("#FFFFFF");
        rgbField.setText("rgb(255, 255, 255)");
        hslField.setText("hsl(0, 0%, 100%)");
        hsvField.setText("hsv(0, 0%, 100%)");
        cmykField.setText("cmyk(0%, 0%, 0%, 0%)");
        colorPreviewPanel.setBackground(Color.WHITE);
        colorPreviewPanel.repaint();
    }

    private void copyAllFormats() {
        StringBuilder sb = new StringBuilder();
        sb.append("HEX: ").append(hexField.getText()).append("\n");
        sb.append("RGB: ").append(rgbField.getText()).append("\n");
        sb.append("HSL: ").append(hslField.getText()).append("\n");
        sb.append("HSV: ").append(hsvField.getText()).append("\n");
        sb.append("CMYK: ").append(cmykField.getText());
        
        copyToClipboard(sb.toString());
    }

    private void copyToClipboard(String text) {
        if (!text.isEmpty()) {
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}