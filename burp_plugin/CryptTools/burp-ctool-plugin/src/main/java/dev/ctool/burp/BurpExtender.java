package dev.ctool.burp;

import burp.*;
import dev.ctool.burp.ui.CtoolMainPanel;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

/**
 * Burp Suite CryptoTools插件主入口类
 * 实现程序开发常用工具集成到Burp Suite中
 * 
 * @author CryptoTools Team
 * @version 2.4.0
 */
public class BurpExtender implements IBurpExtender, ITab {
    
    private static final String EXTENSION_NAME = "CryptoTools";
    private static final String TAB_CAPTION = "CryptoTools";
    
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private PrintWriter stdout;
    private PrintWriter stderr;
    
    private CtoolMainPanel mainPanel;
    
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        // 保存回调引用
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        
        // 获取输出流
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        
        // 设置扩展名称
        callbacks.setExtensionName(EXTENSION_NAME);
        
        // 输出加载信息
        stdout.println("=====================================");
        stdout.println("CryptoTools Plugin for Burp Suite v1.1.0");
        stdout.println("程序开发常用工具 - Burp Suite插件版本");
        stdout.println("功能包括：哈希、加密解密、编码转换、时间戳、二维码、拼音转换等");
        stdout.println("=====================================");
        
        // 在EDT线程中创建UI
        SwingUtilities.invokeLater(() -> {
            try {
                // 创建主面板
                mainPanel = new CtoolMainPanel(callbacks, helpers, stdout, stderr);
                
                // 注册为新标签页
                callbacks.addSuiteTab(BurpExtender.this);
                
                stdout.println("CryptoTools插件加载成功！");
                
            } catch (Exception e) {
                stderr.println("创建CryptoTools插件UI时发生错误: " + e.getMessage());
                e.printStackTrace(stderr);
            }
        });
    }
    
    @Override
    public String getTabCaption() {
        return TAB_CAPTION;
    }
    
    @Override
    public Component getUiComponent() {
        return mainPanel;
    }
    
    /**
     * 获取Burp回调接口
     */
    public IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }
    
    /**
     * 获取Burp帮助工具
     */
    public IExtensionHelpers getHelpers() {
        return helpers;
    }
    
    /**
     * 获取标准输出流
     */
    public PrintWriter getStdout() {
        return stdout;
    }
    
    /**
     * 获取错误输出流
     */
    public PrintWriter getStderr() {
        return stderr;
    }
}
