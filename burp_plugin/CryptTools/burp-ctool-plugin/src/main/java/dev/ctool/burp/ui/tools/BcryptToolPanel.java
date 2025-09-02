package dev.ctool.burp.ui.tools;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BcryptToolPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JSpinner roundsSpinner;
    private JTextField hashField;
    private JTextField passwordField;
    private JLabel verifyResultLabel;
    private JTabbedPane tabbedPane;

    public BcryptToolPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Bcrypt 密码哈希工具"));

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        
        // 哈希生成选项卡
        JPanel hashPanel = createHashPanel();
        tabbedPane.addTab("生成哈希", hashPanel);
        
        // 密码验证选项卡
        JPanel verifyPanel = createVerifyPanel();
        tabbedPane.addTab("验证密码", verifyPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHashPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("输入密码"));
        
        inputArea = new JTextArea(5, 40);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 选项面板
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.add(new JLabel("轮数 (Rounds):"));
        roundsSpinner = new JSpinner(new SpinnerNumberModel(12, 4, 31, 1));
        optionsPanel.add(roundsSpinner);
        
        inputPanel.add(optionsPanel, BorderLayout.SOUTH);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("Bcrypt 哈希"));
        
        outputArea = new JTextArea(5, 40);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton generateButton = new JButton("生成哈希");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateHash();
            }
        });
        
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearHashFields();
            }
        });
        
        JButton copyButton = new JButton("复制结果");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyHashResult();
            }
        });
        
        buttonPanel.add(generateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(copyButton);
        
        outputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(outputPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createVerifyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 密码输入
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("原始密码:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JTextField(30);
        panel.add(passwordField, gbc);
        
        // 哈希输入
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Bcrypt 哈希:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        hashField = new JTextField(30);
        panel.add(hashField, gbc);
        
        // 验证按钮
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel verifyButtonPanel = new JPanel(new FlowLayout());
        
        JButton verifyButton = new JButton("验证密码");
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyPassword();
            }
        });
        
        JButton clearVerifyButton = new JButton("清空");
        clearVerifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearVerifyFields();
            }
        });
        
        verifyButtonPanel.add(verifyButton);
        verifyButtonPanel.add(clearVerifyButton);
        panel.add(verifyButtonPanel, gbc);
        
        // 验证结果
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        
        verifyResultLabel = new JLabel(" ");
        verifyResultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        verifyResultLabel.setBorder(BorderFactory.createTitledBorder("验证结果"));
        panel.add(verifyResultLabel, gbc);
        
        return panel;
    }

    private void generateHash() {
        try {
            String password = inputArea.getText().trim();
            if (password.isEmpty()) {
                outputArea.setText("请输入要哈希的密码");
                return;
            }
            
            int rounds = (Integer) roundsSpinner.getValue();
            String salt = BCrypt.gensalt(rounds);
            String hash = BCrypt.hashpw(password, salt);
            
            outputArea.setText(hash);
            
        } catch (Exception e) {
            outputArea.setText("生成哈希时出错: " + e.getMessage());
        }
    }

    private void verifyPassword() {
        try {
            String password = passwordField.getText().trim();
            String hash = hashField.getText().trim();
            
            if (password.isEmpty() || hash.isEmpty()) {
                verifyResultLabel.setText("请输入密码和哈希值");
                verifyResultLabel.setForeground(Color.RED);
                return;
            }
            
            boolean isValid = BCrypt.checkpw(password, hash);
            
            if (isValid) {
                verifyResultLabel.setText("✓ 密码验证成功");
                verifyResultLabel.setForeground(new Color(0, 128, 0));
            } else {
                verifyResultLabel.setText("✗ 密码验证失败");
                verifyResultLabel.setForeground(Color.RED);
            }
            
        } catch (Exception e) {
            verifyResultLabel.setText("验证时出错: " + e.getMessage());
            verifyResultLabel.setForeground(Color.RED);
        }
    }

    private void clearHashFields() {
        inputArea.setText("");
        outputArea.setText("");
        roundsSpinner.setValue(12);
    }

    private void clearVerifyFields() {
        passwordField.setText("");
        hashField.setText("");
        verifyResultLabel.setText(" ");
        verifyResultLabel.setForeground(Color.BLACK);
    }

    private void copyHashResult() {
        String result = outputArea.getText();
        if (!result.isEmpty()) {
            StringSelection selection = new StringSelection(result);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}