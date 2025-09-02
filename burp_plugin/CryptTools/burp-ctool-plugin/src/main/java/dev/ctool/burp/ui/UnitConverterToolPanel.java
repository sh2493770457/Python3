package dev.ctool.burp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class UnitConverterToolPanel extends JPanel {
    private JComboBox<String> categoryCombo;
    private JComboBox<String> fromUnitCombo;
    private JComboBox<String> toUnitCombo;
    private JTextField inputField;
    private JTextField outputField;
    private JButton convertButton;
    private JButton clearButton;
    private JButton swapButton;
    
    // 单位转换数据
    private final Map<String, Map<String, Double>> conversionData;
    private final Map<String, String[]> categoryUnits;
    
    public UnitConverterToolPanel() {
        conversionData = initializeConversionData();
        categoryUnits = initializeCategoryUnits();
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private Map<String, Map<String, Double>> initializeConversionData() {
        Map<String, Map<String, Double>> data = new HashMap<>();
        
        // 长度单位（以米为基准）
        Map<String, Double> length = new HashMap<>();
        length.put("毫米", 0.001);
        length.put("厘米", 0.01);
        length.put("分米", 0.1);
        length.put("米", 1.0);
        length.put("千米", 1000.0);
        length.put("英寸", 0.0254);
        length.put("英尺", 0.3048);
        length.put("码", 0.9144);
        length.put("英里", 1609.344);
        length.put("海里", 1852.0);
        data.put("长度", length);
        
        // 重量单位（以克为基准）
        Map<String, Double> weight = new HashMap<>();
        weight.put("毫克", 0.001);
        weight.put("克", 1.0);
        weight.put("千克", 1000.0);
        weight.put("吨", 1000000.0);
        weight.put("盎司", 28.3495);
        weight.put("磅", 453.592);
        weight.put("英石", 6350.29);
        data.put("重量", weight);
        
        // 面积单位（以平方米为基准）
        Map<String, Double> area = new HashMap<>();
        area.put("平方毫米", 0.000001);
        area.put("平方厘米", 0.0001);
        area.put("平方米", 1.0);
        area.put("平方千米", 1000000.0);
        area.put("公顷", 10000.0);
        area.put("亩", 666.667);
        area.put("平方英寸", 0.00064516);
        area.put("平方英尺", 0.092903);
        area.put("平方码", 0.836127);
        area.put("英亩", 4046.86);
        data.put("面积", area);
        
        // 体积单位（以升为基准）
        Map<String, Double> volume = new HashMap<>();
        volume.put("毫升", 0.001);
        volume.put("升", 1.0);
        volume.put("立方米", 1000.0);
        volume.put("加仑(美)", 3.78541);
        volume.put("加仑(英)", 4.54609);
        volume.put("夸脱", 0.946353);
        volume.put("品脱", 0.473176);
        volume.put("液盎司", 0.0295735);
        data.put("体积", volume);
        
        // 时间单位（以秒为基准）
        Map<String, Double> time = new HashMap<>();
        time.put("毫秒", 0.001);
        time.put("秒", 1.0);
        time.put("分钟", 60.0);
        time.put("小时", 3600.0);
        time.put("天", 86400.0);
        time.put("周", 604800.0);
        time.put("月", 2629746.0); // 平均月
        time.put("年", 31556952.0); // 平均年
        data.put("时间", time);
        
        // 速度单位（以米/秒为基准）
        Map<String, Double> speed = new HashMap<>();
        speed.put("米/秒", 1.0);
        speed.put("千米/小时", 0.277778);
        speed.put("英里/小时", 0.44704);
        speed.put("节", 0.514444);
        speed.put("马赫", 343.0);
        data.put("速度", speed);
        
        return data;
    }
    
    private Map<String, String[]> initializeCategoryUnits() {
        Map<String, String[]> units = new HashMap<>();
        
        units.put("长度", new String[]{"毫米", "厘米", "分米", "米", "千米", "英寸", "英尺", "码", "英里", "海里"});
        units.put("重量", new String[]{"毫克", "克", "千克", "吨", "盎司", "磅", "英石"});
        units.put("面积", new String[]{"平方毫米", "平方厘米", "平方米", "平方千米", "公顷", "亩", "平方英寸", "平方英尺", "平方码", "英亩"});
        units.put("体积", new String[]{"毫升", "升", "立方米", "加仑(美)", "加仑(英)", "夸脱", "品脱", "液盎司"});
        units.put("时间", new String[]{"毫秒", "秒", "分钟", "小时", "天", "周", "月", "年"});
        units.put("速度", new String[]{"米/秒", "千米/小时", "英里/小时", "节", "马赫"});
        units.put("温度", new String[]{"摄氏度", "华氏度", "开尔文"});
        
        return units;
    }
    
    private void initializeComponents() {
        // 类别选择
        String[] categories = {"长度", "重量", "面积", "体积", "时间", "速度", "温度"};
        categoryCombo = new JComboBox<>(categories);
        
        // 单位选择
        fromUnitCombo = new JComboBox<>();
        toUnitCombo = new JComboBox<>();
        
        // 输入输出字段
        inputField = new JTextField(15);
        inputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        outputField = new JTextField(15);
        outputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputField.setEditable(false);
        outputField.setBackground(new Color(248, 248, 248));
        
        // 按钮
        convertButton = new JButton("转换");
        clearButton = new JButton("清空");
        swapButton = new JButton("交换单位");
        
        // 初始化单位列表
        updateUnitCombos();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 控制面板
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("单位转换"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("类别:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        controlPanel.add(categoryCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("从:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        controlPanel.add(fromUnitCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        controlPanel.add(new JLabel("到:"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 1;
        controlPanel.add(toUnitCombo, gbc);
        
        // 输入输出面板
        JPanel ioPanel = new JPanel(new GridBagLayout());
        ioPanel.setBorder(BorderFactory.createTitledBorder("数值转换"));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        ioPanel.add(new JLabel("输入值:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        ioPanel.add(inputField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        ioPanel.add(new JLabel("结果:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        ioPanel.add(outputField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(convertButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(clearButton);
        
        // 主布局
        add(controlPanel, BorderLayout.NORTH);
        add(ioPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        categoryCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUnitCombos();
            }
        });
        
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        swapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapUnits();
            }
        });
        
        // 回车键触发转换
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });
    }
    
    private void updateUnitCombos() {
        String category = (String) categoryCombo.getSelectedItem();
        String[] units = categoryUnits.get(category);
        
        fromUnitCombo.removeAllItems();
        toUnitCombo.removeAllItems();
        
        if (units != null) {
            for (String unit : units) {
                fromUnitCombo.addItem(unit);
                toUnitCombo.addItem(unit);
            }
            
            // 设置默认选择
            if (units.length > 1) {
                toUnitCombo.setSelectedIndex(1);
            }
        }
    }
    
    private void performConversion() {
        try {
            String inputText = inputField.getText().trim();
            if (inputText.isEmpty()) {
                outputField.setText("请输入数值");
                return;
            }
            
            double inputValue = Double.parseDouble(inputText);
            String category = (String) categoryCombo.getSelectedItem();
            String fromUnit = (String) fromUnitCombo.getSelectedItem();
            String toUnit = (String) toUnitCombo.getSelectedItem();
            
            double result;
            
            if ("温度".equals(category)) {
                result = convertTemperature(inputValue, fromUnit, toUnit);
            } else {
                result = convertStandardUnit(inputValue, category, fromUnit, toUnit);
            }
            
            // 格式化结果
            BigDecimal bd = new BigDecimal(result);
            bd = bd.setScale(10, RoundingMode.HALF_UP);
            bd = bd.stripTrailingZeros();
            
            outputField.setText(bd.toPlainString());
            
        } catch (NumberFormatException e) {
            outputField.setText("输入格式错误");
        } catch (Exception e) {
            outputField.setText("转换失败: " + e.getMessage());
        }
    }
    
    private double convertStandardUnit(double value, String category, String fromUnit, String toUnit) {
        Map<String, Double> units = conversionData.get(category);
        if (units == null) {
            throw new IllegalArgumentException("不支持的类别: " + category);
        }
        
        Double fromFactor = units.get(fromUnit);
        Double toFactor = units.get(toUnit);
        
        if (fromFactor == null || toFactor == null) {
            throw new IllegalArgumentException("不支持的单位");
        }
        
        // 先转换为基准单位，再转换为目标单位
        double baseValue = value * fromFactor;
        return baseValue / toFactor;
    }
    
    private double convertTemperature(double value, String fromUnit, String toUnit) {
        // 先转换为摄氏度
        double celsius;
        switch (fromUnit) {
            case "摄氏度":
                celsius = value;
                break;
            case "华氏度":
                celsius = (value - 32) * 5.0 / 9.0;
                break;
            case "开尔文":
                celsius = value - 273.15;
                break;
            default:
                throw new IllegalArgumentException("不支持的温度单位: " + fromUnit);
        }
        
        // 从摄氏度转换为目标单位
        switch (toUnit) {
            case "摄氏度":
                return celsius;
            case "华氏度":
                return celsius * 9.0 / 5.0 + 32;
            case "开尔文":
                return celsius + 273.15;
            default:
                throw new IllegalArgumentException("不支持的温度单位: " + toUnit);
        }
    }
    
    private void swapUnits() {
        String fromUnit = (String) fromUnitCombo.getSelectedItem();
        String toUnit = (String) toUnitCombo.getSelectedItem();
        
        fromUnitCombo.setSelectedItem(toUnit);
        toUnitCombo.setSelectedItem(fromUnit);
        
        // 如果有输入，自动转换
        if (!inputField.getText().trim().isEmpty()) {
            performConversion();
        }
    }
    
    private void clearAll() {
        inputField.setText("");
        outputField.setText("");
        categoryCombo.setSelectedIndex(0);
        updateUnitCombos();
    }
}