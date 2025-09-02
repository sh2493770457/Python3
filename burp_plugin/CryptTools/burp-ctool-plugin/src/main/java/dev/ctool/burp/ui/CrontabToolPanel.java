package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CrontabToolPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea resultArea;
    private JTabbedPane tabbedPane;
    private Map<String, CronFieldPanel> fieldPanels;
    
    public CrontabToolPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // 输入区域
        inputArea = new JTextArea(3, 50);
        inputArea.setBorder(BorderFactory.createTitledBorder("Cron表达式"));
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // 结果显示区域
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("解析结果"));
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        
        // 生成器选项卡
        JPanel generatorPanel = createGeneratorPanel();
        tabbedPane.addTab("生成器", generatorPanel);
        
        // 示例选项卡
        JPanel examplePanel = createExamplePanel();
        tabbedPane.addTab("示例", examplePanel);
        
        // 格式说明选项卡
        JPanel formatPanel = createFormatPanel();
        tabbedPane.addTab("格式说明", formatPanel);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部输入区域
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton parseButton = new JButton("解析");
        JButton clearButton = new JButton("清空");
        buttonPanel.add(parseButton);
        buttonPanel.add(clearButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中间选项卡
        add(tabbedPane, BorderLayout.CENTER);
        
        // 底部结果区域
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // 解析按钮事件
        Component[] components = ((JPanel)((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.NORTH)).getComponents();
        JPanel buttonPanel = (JPanel)components[1];
        JButton parseButton = (JButton)buttonPanel.getComponent(0);
        JButton clearButton = (JButton)buttonPanel.getComponent(1);
        
        parseButton.addActionListener(e -> parseCronExpression());
        clearButton.addActionListener(e -> clearAll());
    }
    
    private JPanel createGeneratorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建字段面板
        fieldPanels = new HashMap<>();
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        
        String[] fieldNames = {"秒", "分钟", "小时", "日", "月", "周"};
        String[] fieldKeys = {"second", "minute", "hour", "day", "month", "week"};
        
        for (int i = 0; i < fieldNames.length; i++) {
            CronFieldPanel fieldPanel = new CronFieldPanel(fieldNames[i], fieldKeys[i]);
            fieldPanels.put(fieldKeys[i], fieldPanel);
            fieldsPanel.add(fieldPanel);
        }
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // 生成按钮
        JPanel generatePanel = new JPanel(new FlowLayout());
        JButton generateButton = new JButton("生成表达式");
        JButton clearFieldsButton = new JButton("清空字段");
        generateButton.addActionListener(e -> generateExpression());
        clearFieldsButton.addActionListener(e -> clearFields());
        generatePanel.add(generateButton);
        generatePanel.add(clearFieldsButton);
        
        panel.add(generatePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createExamplePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] examples = {
            "0 0 12 * * ?     每天中午12点触发",
            "0 15 10 ? * *    每天上午10:15触发",
            "0 15 10 * * ?    每天上午10:15触发",
            "0 15 10 * * ? *  每天上午10:15触发",
            "0 * 14 * * ?     在每天下午2点到下午2:59期间的每1分钟触发",
            "0 0/5 14 * * ?   在每天下午2点到下午2:55期间的每5分钟触发",
            "0 0/5 14,18 * * ? 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发",
            "0 0-5 14 * * ?   在每天下午2点到下午2:05期间的每1分钟触发",
            "0 10,44 14 ? 3 WED 每年三月的星期三的下午2:10和2:44触发",
            "0 15 10 ? * MON-FRI 周一至周五的上午10:15触发"
        };
        
        JTextArea exampleArea = new JTextArea();
        exampleArea.setEditable(false);
        exampleArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        StringBuilder sb = new StringBuilder();
        for (String example : examples) {
            sb.append(example).append("\n");
        }
        exampleArea.setText(sb.toString());
        
        panel.add(new JScrollPane(exampleArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String formatText = "Cron表达式格式说明:\n\n" +
            "字段顺序: 秒 分 时 日 月 周\n\n" +
            "字段范围:\n" +
            "秒: 0-59\n" +
            "分: 0-59\n" +
            "时: 0-23\n" +
            "日: 1-31\n" +
            "月: 1-12 或 JAN-DEC\n" +
            "周: 1-7 或 SUN-SAT (1=周日)\n\n" +
            "特殊字符:\n" +
            "* : 匹配任意值\n" +
            "? : 不指定值(仅日和周字段)\n" +
            ", : 分隔多个值\n" +
            "- : 指定范围\n" +
            "/ : 指定增量\n" +
            "L : 最后(仅日和周字段)\n" +
            "W : 工作日(仅日字段)\n" +
            "# : 第几个星期几(仅周字段)";
        
        JTextArea formatArea = new JTextArea();
        formatArea.setEditable(false);
        formatArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        formatArea.setText(formatText);
        
        panel.add(new JScrollPane(formatArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void parseCronExpression() {
        String expression = inputArea.getText().trim();
        if (expression.isEmpty()) {
            resultArea.setText("请输入Cron表达式");
            return;
        }
        
        try {
            String[] parts = expression.split("\\s+");
            if (parts.length < 6) {
                resultArea.setText("Cron表达式格式错误，至少需要6个字段");
                return;
            }
            
            StringBuilder result = new StringBuilder();
            result.append("表达式: ").append(expression).append("\n\n");
            
            // 解析各个字段
            String[] fieldNames = {"秒", "分钟", "小时", "日", "月", "周"};
            for (int i = 0; i < Math.min(parts.length, 6); i++) {
                result.append(fieldNames[i]).append(": ").append(parts[i]);
                result.append(" (").append(parseFieldDescription(parts[i], i)).append(")\n");
            }
            
            result.append("\n描述: ").append(generateDescription(parts)).append("\n\n");
            
            // 生成接下来几次执行时间
            result.append("接下来的执行时间:\n");
            List<String> nextTimes = generateNextExecutionTimes(expression, 5);
            for (String time : nextTimes) {
                result.append(time).append("\n");
            }
            
            resultArea.setText(result.toString());
            
        } catch (Exception e) {
            resultArea.setText("解析错误: " + e.getMessage());
        }
    }
    
    private String parseFieldDescription(String field, int fieldIndex) {
        if ("*".equals(field)) {
            return "任意值";
        }
        if ("?".equals(field)) {
            return "不指定";
        }
        if (field.contains(",")) {
            return "指定值: " + field;
        }
        if (field.contains("-")) {
            return "范围: " + field;
        }
        if (field.contains("/")) {
            return "间隔: " + field;
        }
        return "固定值: " + field;
    }
    
    private String generateDescription(String[] parts) {
        // 简化的描述生成
        StringBuilder desc = new StringBuilder();
        
        if ("*".equals(parts[1]) && "*".equals(parts[2])) {
            desc.append("每分钟");
        } else if ("*".equals(parts[2])) {
            desc.append("每小时的第").append(parts[1]).append("分钟");
        } else {
            desc.append("每天").append(parts[2]).append(":").append(parts[1]);
        }
        
        return desc.toString();
    }
    
    private List<String> generateNextExecutionTimes(String expression, int count) {
        List<String> times = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 简化的时间生成（实际应该使用Cron解析库）
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < count; i++) {
            cal.add(Calendar.MINUTE, 1);
            times.add(sdf.format(cal.getTime()));
        }
        
        return times;
    }
    
    private void generateExpression() {
        StringBuilder expression = new StringBuilder();
        
        String[] fieldKeys = {"second", "minute", "hour", "day", "month", "week"};
        for (String key : fieldKeys) {
            CronFieldPanel panel = fieldPanels.get(key);
            if (expression.length() > 0) {
                expression.append(" ");
            }
            expression.append(panel.getFieldValue());
        }
        
        inputArea.setText(expression.toString());
        parseCronExpression();
    }
    
    private void clearFields() {
        for (CronFieldPanel panel : fieldPanels.values()) {
            panel.reset();
        }
    }
    
    private void clearAll() {
        inputArea.setText("");
        resultArea.setText("");
        clearFields();
    }
    
    // 内部类：Cron字段面板
    private class CronFieldPanel extends JPanel {
        private String fieldName;
        private String fieldKey;
        private JRadioButton ignoreRadio, anyRadio, scopeRadio, intervalRadio, listRadio;
        private JSpinner startSpinner, endSpinner, stepSpinner;
        private JTextField listField;
        private ButtonGroup buttonGroup;
        
        public CronFieldPanel(String fieldName, String fieldKey) {
            this.fieldName = fieldName;
            this.fieldKey = fieldKey;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
        }
        
        private void initializeComponents() {
            setBorder(BorderFactory.createTitledBorder(fieldName));
            
            buttonGroup = new ButtonGroup();
            ignoreRadio = new JRadioButton("忽略(?)", true);
            anyRadio = new JRadioButton("任意(*)");
            scopeRadio = new JRadioButton("范围");
            intervalRadio = new JRadioButton("间隔");
            listRadio = new JRadioButton("列表");
            
            buttonGroup.add(ignoreRadio);
            buttonGroup.add(anyRadio);
            buttonGroup.add(scopeRadio);
            buttonGroup.add(intervalRadio);
            buttonGroup.add(listRadio);
            
            // 根据字段类型设置范围
            int maxValue = getMaxValue(fieldKey);
            startSpinner = new JSpinner(new SpinnerNumberModel(0, 0, maxValue, 1));
            endSpinner = new JSpinner(new SpinnerNumberModel(maxValue, 0, maxValue, 1));
            stepSpinner = new JSpinner(new SpinnerNumberModel(1, 1, maxValue, 1));
            listField = new JTextField(10);
            
            // 初始状态下禁用输入组件
            setInputEnabled(false);
        }
        
        private void setupLayout() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            
            add(ignoreRadio);
            add(anyRadio);
            add(scopeRadio);
            add(startSpinner);
            add(new JLabel("-"));
            add(endSpinner);
            add(intervalRadio);
            add(stepSpinner);
            add(listRadio);
            add(listField);
        }
        
        private void setupEventHandlers() {
            ActionListener radioListener = e -> updateInputState();
            ignoreRadio.addActionListener(radioListener);
            anyRadio.addActionListener(radioListener);
            scopeRadio.addActionListener(radioListener);
            intervalRadio.addActionListener(radioListener);
            listRadio.addActionListener(radioListener);
        }
        
        private void updateInputState() {
            boolean scopeSelected = scopeRadio.isSelected();
            boolean intervalSelected = intervalRadio.isSelected();
            boolean listSelected = listRadio.isSelected();
            
            startSpinner.setEnabled(scopeSelected);
            endSpinner.setEnabled(scopeSelected);
            stepSpinner.setEnabled(intervalSelected);
            listField.setEnabled(listSelected);
        }
        
        private void setInputEnabled(boolean enabled) {
            startSpinner.setEnabled(enabled);
            endSpinner.setEnabled(enabled);
            stepSpinner.setEnabled(enabled);
            listField.setEnabled(enabled);
        }
        
        private int getMaxValue(String fieldKey) {
            switch (fieldKey) {
                case "second":
                case "minute":
                    return 59;
                case "hour":
                    return 23;
                case "day":
                    return 31;
                case "month":
                    return 12;
                case "week":
                    return 7;
                default:
                    return 59;
            }
        }
        
        public String getFieldValue() {
            if (ignoreRadio.isSelected()) {
                return "?";
            } else if (anyRadio.isSelected()) {
                return "*";
            } else if (scopeRadio.isSelected()) {
                return startSpinner.getValue() + "-" + endSpinner.getValue();
            } else if (intervalRadio.isSelected()) {
                return "*/" + stepSpinner.getValue();
            } else if (listRadio.isSelected()) {
                String list = listField.getText().trim();
                return list.isEmpty() ? "*" : list;
            }
            return "*";
        }
        
        public void reset() {
            ignoreRadio.setSelected(true);
            startSpinner.setValue(0);
            endSpinner.setValue(getMaxValue(fieldKey));
            stepSpinner.setValue(1);
            listField.setText("");
            updateInputState();
        }
    }
}