package dev.ctool.burp.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 汉字转拼音工具面板
 * 支持汉字转拼音功能，包括带声调和不带声调的转换
 */
public class PinyinToolPanel extends JPanel {
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JCheckBox toneCheckBox;
    private JCheckBox firstLetterCheckBox;
    private JButton convertButton;
    private JButton clearButton;
    
    // 简化的拼音映射表（实际应用中应使用完整的拼音库）
    private static final Map<Character, String> PINYIN_MAP = new HashMap<>();
    
    static {
        // 初始化常用汉字拼音映射
        initializePinyinMap();
    }
    
    public PinyinToolPanel() {
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        
        // 创建选项面板
        JPanel optionPanel = createOptionPanel();
        
        // 创建输出面板
        JPanel outputPanel = createOutputPanel();
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 布局
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(optionPanel, BorderLayout.SOUTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(outputPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("输入汉字"));
        
        inputArea = new JTextArea(8, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(inputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOptionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("转换选项"));
        
        toneCheckBox = new JCheckBox("包含声调", true);
        firstLetterCheckBox = new JCheckBox("仅首字母", false);
        
        panel.add(toneCheckBox);
        panel.add(firstLetterCheckBox);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("拼音结果"));
        
        outputArea = new JTextArea(8, 50);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        convertButton = new JButton("转换");
        clearButton = new JButton("清空");
        
        convertButton.setPreferredSize(new Dimension(100, 30));
        clearButton.setPreferredSize(new Dimension(100, 30));
        
        panel.add(convertButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertToPinyin();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        
        // 选项变化时自动转换
        toneCheckBox.addActionListener(e -> {
            if (!inputArea.getText().trim().isEmpty()) {
                convertToPinyin();
            }
        });
        
        firstLetterCheckBox.addActionListener(e -> {
            if (!inputArea.getText().trim().isEmpty()) {
                convertToPinyin();
            }
        });
    }
    
    private void convertToPinyin() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            outputArea.setText("");
            return;
        }
        
        try {
            StringBuilder result = new StringBuilder();
            boolean withTone = toneCheckBox.isSelected();
            boolean firstLetterOnly = firstLetterCheckBox.isSelected();
            
            for (char c : input.toCharArray()) {
                if (isChinese(c)) {
                    String pinyin = getPinyin(c, withTone);
                    if (firstLetterOnly && !pinyin.isEmpty()) {
                        result.append(pinyin.charAt(0));
                    } else {
                        result.append(pinyin);
                    }
                    result.append(" ");
                } else {
                    result.append(c);
                }
            }
            
            outputArea.setText(result.toString().trim());
        } catch (Exception e) {
            outputArea.setText("转换出错: " + e.getMessage());
        }
    }
    
    private boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FFF;
    }
    
    private String getPinyin(char c, boolean withTone) {
        String pinyin = PINYIN_MAP.get(c);
        if (pinyin == null) {
            return String.valueOf(c); // 如果找不到拼音，返回原字符
        }
        
        if (!withTone) {
            // 移除声调
            pinyin = removeTone(pinyin);
        }
        
        return pinyin;
    }
    
    private String removeTone(String pinyin) {
        // 简单的声调移除（实际应用中需要更完善的处理）
        StringBuilder result = new StringBuilder();
        for (char c : pinyin.toCharArray()) {
            switch (c) {
                case 'ā': case 'á': case 'ǎ': case 'à': result.append('a'); break;
                case 'ē': case 'é': case 'ě': case 'è': result.append('e'); break;
                case 'ī': case 'í': case 'ǐ': case 'ì': result.append('i'); break;
                case 'ō': case 'ó': case 'ǒ': case 'ò': result.append('o'); break;
                case 'ū': case 'ú': case 'ǔ': case 'ù': result.append('u'); break;
                case 'ǖ': case 'ǘ': case 'ǚ': case 'ǜ': result.append('ü'); break;
                default: result.append(c); break;
            }
        }
        return result.toString();
    }
    
    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
    }
    
    private static void initializePinyinMap() {
        // 初始化常用汉字拼音映射（这里只是示例，实际应用需要完整的拼音库）
        PINYIN_MAP.put('你', "nǐ");
        PINYIN_MAP.put('好', "hǎo");
        PINYIN_MAP.put('世', "shì");
        PINYIN_MAP.put('界', "jiè");
        PINYIN_MAP.put('中', "zhōng");
        PINYIN_MAP.put('国', "guó");
        PINYIN_MAP.put('人', "rén");
        PINYIN_MAP.put('民', "mín");
        PINYIN_MAP.put('共', "gòng");
        PINYIN_MAP.put('和', "hé");
        PINYIN_MAP.put('汉', "hàn");
        PINYIN_MAP.put('字', "zì");
        PINYIN_MAP.put('转', "zhuǎn");
        PINYIN_MAP.put('拼', "pīn");
        PINYIN_MAP.put('音', "yīn");
        PINYIN_MAP.put('工', "gōng");
        PINYIN_MAP.put('具', "jù");
        PINYIN_MAP.put('测', "cè");
        PINYIN_MAP.put('试', "shì");
        PINYIN_MAP.put('文', "wén");
        PINYIN_MAP.put('本', "běn");
        PINYIN_MAP.put('处', "chù");
        PINYIN_MAP.put('理', "lǐ");
        PINYIN_MAP.put('功', "gōng");
        PINYIN_MAP.put('能', "néng");
        PINYIN_MAP.put('支', "zhī");
        PINYIN_MAP.put('持', "chí");
        PINYIN_MAP.put('声', "shēng");
        PINYIN_MAP.put('调', "diào");
        PINYIN_MAP.put('首', "shǒu");
        PINYIN_MAP.put('母', "mǔ");
        PINYIN_MAP.put('提', "tí");
        PINYIN_MAP.put('取', "qǔ");
        PINYIN_MAP.put('简', "jiǎn");
        PINYIN_MAP.put('单', "dān");
        PINYIN_MAP.put('易', "yì");
        PINYIN_MAP.put('用', "yòng");
        PINYIN_MAP.put('快', "kuài");
        PINYIN_MAP.put('速', "sù");
        PINYIN_MAP.put('准', "zhǔn");
        PINYIN_MAP.put('确', "què");
        
        // 添加更多常用字...
        // 注意：实际应用中应该使用完整的拼音数据库
    }
}