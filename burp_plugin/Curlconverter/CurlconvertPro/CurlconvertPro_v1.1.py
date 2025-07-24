# -*- encoding: utf-8 -*-
# TODO:@ModuleName: CurlconvertPro
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/7/16 13:20
import json
import os
import pickle
import sys
import time
import urllib2
from burp import IBurpExtender, IMessageEditorTabFactory, IMessageEditorTab, ITab, IExtensionStateListener
from java.awt import (BorderLayout, FlowLayout, Dimension, Color, Font,
                      GridBagLayout, GridBagConstraints, Insets, Toolkit)
from java.awt.datatransfer import StringSelection
from java.awt.event import ActionListener, MouseAdapter, MouseEvent
from java.io import PrintWriter, FileWriter, OutputStreamWriter, FileOutputStream
from java.lang import RuntimeException, String
from java.net import URL
from java.nio.charset import StandardCharsets
# GUI imports
from javax.swing import (JPanel, JLabel, JComboBox, JTextField, JButton,
                         JScrollPane, JTextArea, BoxLayout, JOptionPane,
                         BorderFactory, Box, JCheckBox, SwingUtilities,
                         JPopupMenu, JMenuItem, JFileChooser)
from javax.swing.border import EmptyBorder

# TODO:设置默认编码为UTF-8
if hasattr(sys, "setdefaultencoding"):
    reload(sys)
    sys.setdefaultencoding("utf-8")

FILE_EXTENSIONS = {
    "python": ".py", "javascript": ".js", "node": ".js", "java": ".java",
    "go": ".go", "php": ".php", "ruby": ".rb", "rust": ".rs",
    "csharp": ".cs", "swift": ".swift", "kotlin": ".kt", "dart": ".dart",
    "r": ".R", "julia": ".jl", "perl": ".pl", "lua": ".lua",
    "clojure": ".clj", "elixir": ".ex", "objectivec": ".m", "ocaml": ".ml",
    "matlab": ".m", "powershell": ".ps1", "c": ".c", "cfml": ".cfm"
}

EXCLUDED_HEADERS = {
    "content-length", "connection",
}

# Jython/Python兼容性处理
try:
    unicode
except NameError:
    unicode = str


def safe_decode_bytes(byte_array):
    """安全地解码字节数组，特别处理中文字符"""
    if byte_array is None or len(byte_array) == 0:
        return u""

    try:
        if hasattr(byte_array, 'tostring'):
            byte_string = byte_array.tostring()
        else:
            byte_string = str(byte_array)

        encodings = ['utf-8', 'gbk', 'gb2312', 'big5', 'latin1']
        for encoding in encodings:
            try:
                if isinstance(byte_string, str):
                    decoded = byte_string.decode(encoding)
                    return decoded
                else:
                    decoded = byte_string.decode(encoding) if hasattr(byte_string, 'decode') else unicode(byte_string)
                    return decoded
            except (UnicodeDecodeError, UnicodeEncodeError, AttributeError):
                continue

        return safe_unicode(byte_string)

    except Exception:
        try:
            return safe_unicode(str(byte_array))
        except:
            return u"<decode_error>"


def safe_unicode(text):
    """安全地将文本转换为Unicode，专门优化处理中文字符"""
    if text is None:
        return u""

    # 如果已经是Unicode，直接返回
    if isinstance(text, unicode):
        return text

    # 处理字节串和字符串
    if isinstance(text, (str, bytes)):
        # 方法1：尝试标准的UTF-8解码
        try:
            if hasattr(text, 'decode'):
                return text.decode('utf-8', 'replace')  # 使用replace模式避免严格错误
            else:
                return unicode(text, 'utf-8', 'replace')
        except (UnicodeDecodeError, UnicodeEncodeError, AttributeError, LookupError):
            pass

        # 方法2：尝试GBK编码（中文常用编码）
        try:
            if hasattr(text, 'decode'):
                return text.decode('gbk', 'replace')
            else:
                return unicode(text, 'gbk', 'replace')
        except (UnicodeDecodeError, UnicodeEncodeError, AttributeError, LookupError):
            pass

        # 方法3：尝试GB2312编码
        try:
            if hasattr(text, 'decode'):
                return text.decode('gb2312', 'replace')
            else:
                return unicode(text, 'gb2312', 'replace')
        except (UnicodeDecodeError, UnicodeEncodeError, AttributeError, LookupError):
            pass

        # 方法4：尝试Latin-1编码（最宽松的编码）
        try:
            if hasattr(text, 'decode'):
                return text.decode('latin1')
            else:
                return unicode(text, 'latin1')
        except (UnicodeDecodeError, UnicodeEncodeError, AttributeError, LookupError):
            pass

        # 方法5：手动字节转换，安全处理每个字节
        try:
            if hasattr(text, '__iter__') and not isinstance(text, (str, unicode)):
                # 处理字节数组或类似结构
                safe_chars = []
                for byte_val in text:
                    try:
                        if isinstance(byte_val, int):
                            # 处理整数字节值
                            if byte_val < 0:
                                byte_val = byte_val + 256  # 处理负数字节
                            if 0 <= byte_val <= 127:  # ASCII范围
                                safe_chars.append(unicode(chr(byte_val)))
                            elif 128 <= byte_val <= 255:  # 扩展ASCII
                                # 尝试作为UTF-8字节处理
                                safe_chars.append(u'?')  # 用?替代非ASCII字节
                            else:
                                safe_chars.append(u'?')
                        elif isinstance(byte_val, (str, unicode)):
                            safe_chars.append(safe_unicode(byte_val))
                        else:
                            safe_chars.append(unicode(str(byte_val)))
                    except (ValueError, TypeError, UnicodeError):
                        safe_chars.append(u'?')
                return u''.join(safe_chars)
            else:
                # 处理普通字符串
                # 方法6：逐字符安全转换
                safe_chars = []
                str_text = str(text)  # 确保是字符串

                i = 0
                while i < len(str_text):
                    try:
                        char = str_text[i]
                        char_code = ord(char)

                        if char_code <= 127:
                            # ASCII字符，直接添加
                            safe_chars.append(unicode(char))
                        else:
                            # 非ASCII字符，尝试UTF-8解码
                            # 检查是否是UTF-8字节序列的开始
                            if char_code >= 192:  # UTF-8多字节序列开始
                                # 尝试读取完整的UTF-8字符
                                try:
                                    # 估算UTF-8字符长度
                                    if char_code < 224:  # 2字节字符
                                        utf8_bytes = str_text[i:i + 2]
                                        decoded = utf8_bytes.decode('utf-8')
                                        safe_chars.append(decoded)
                                        i += 1  # 跳过下一个字节
                                    elif char_code < 240:  # 3字节字符
                                        utf8_bytes = str_text[i:i + 3]
                                        decoded = utf8_bytes.decode('utf-8')
                                        safe_chars.append(decoded)
                                        i += 2  # 跳过接下来的字节
                                    elif char_code < 248:  # 4字节字符
                                        utf8_bytes = str_text[i:i + 4]
                                        decoded = utf8_bytes.decode('utf-8')
                                        safe_chars.append(decoded)
                                        i += 3  # 跳过接下来的字节
                                    else:
                                        safe_chars.append(u'?')
                                except (UnicodeDecodeError, IndexError):
                                    safe_chars.append(u'?')
                            else:
                                # 可能是其他编码的字符，用?替代
                                safe_chars.append(u'?')

                        i += 1

                    except (ValueError, TypeError, UnicodeError, IndexError):
                        safe_chars.append(u'?')
                        i += 1

                return u''.join(safe_chars)

        except Exception:
            pass

        # 方法7：最终兜底，使用repr并清理
        try:
            repr_text = repr(text)
            # 移除repr的引号
            if repr_text.startswith("'") and repr_text.endswith("'"):
                repr_text = repr_text[1:-1]
            elif repr_text.startswith('"') and repr_text.endswith('"'):
                repr_text = repr_text[1:-1]

            # 替换常见的转义序列
            repr_text = repr_text.replace('\\n', '\n').replace('\\t', '\t').replace('\\r', '\r')

            return unicode(repr_text)
        except Exception:
            pass

    # 处理其他类型（数字、对象等）
    try:
        # 先尝试直接转换
        return unicode(text)
    except (UnicodeDecodeError, UnicodeEncodeError, TypeError):
        try:
            # 转换为字符串再转换为Unicode
            str_text = str(text)
            return safe_unicode(str_text)  # 递归调用处理字符串
        except Exception:
            # 最终兜底方案
            try:
                # 使用repr获取对象的字符串表示
                return unicode(repr(text))
            except Exception:
                # 彻底失败，返回错误标记
                return u"<conversion_error>"


def safe_encode_utf8(text):
    """安全地将文本编码为UTF-8字节，优化中文字符处理"""
    if text is None:
        return ""

    # 首先确保text是Unicode字符串
    unicode_text = safe_unicode(text)

    # 方法1：尝试标准UTF-8编码
    try:
        return unicode_text.encode('utf-8')
    except (UnicodeEncodeError, AttributeError) as e:
        pass

    # 方法2：使用replace模式，替换无法编码的字符
    try:
        return unicode_text.encode('utf-8', 'replace')
    except (UnicodeEncodeError, AttributeError):
        pass

    # 方法3：使用ignore模式，忽略无法编码的字符
    try:
        return unicode_text.encode('utf-8', 'ignore')
    except (UnicodeEncodeError, AttributeError):
        pass

    # 方法4：使用xmlcharrefreplace模式，将无法编码的字符转换为XML字符引用
    try:
        return unicode_text.encode('utf-8', 'xmlcharrefreplace')
    except (UnicodeEncodeError, AttributeError):
        pass

    # 方法5：手动处理每个字符
    try:
        result_bytes = []
        for char in unicode_text:
            try:
                # 尝试编码单个字符
                char_bytes = char.encode('utf-8')
                result_bytes.append(char_bytes)
            except UnicodeEncodeError:
                # 无法编码的字符用?替代
                result_bytes.append('?')
            except Exception:
                # 其他错误也用?替代
                result_bytes.append('?')

        return ''.join(result_bytes)
    except Exception:
        pass

    # 方法6：如果unicode_text不是真正的Unicode字符串，尝试不同方法
    try:
        if isinstance(unicode_text, unicode):
            # 强制转换为字节字符串
            str_text = str(unicode_text)
            return str_text.encode('utf-8', 'ignore')
        else:
            # 如果不是Unicode，直接返回字符串形式
            return str(unicode_text)
    except Exception:
        pass

    # 方法7：最终兜底方案，逐字符ASCII化
    try:
        if hasattr(unicode_text, '__iter__'):
            ascii_chars = []
            for char in unicode_text:
                try:
                    char_code = ord(char)
                    if char_code <= 127:
                        # ASCII字符直接添加
                        ascii_chars.append(char)
                    else:
                        # 非ASCII字符用?替代
                        ascii_chars.append('?')
                except (TypeError, ValueError):
                    ascii_chars.append('?')

            ascii_text = ''.join(ascii_chars)
            return ascii_text.encode('utf-8', 'ignore')
        else:
            # 不可迭代，转换为字符串
            return str(unicode_text).encode('utf-8', 'ignore')
    except Exception:
        pass

    # 方法8：彻底失败时的最终兜底
    try:
        # 使用repr获取安全的字符串表示
        repr_text = repr(unicode_text)
        if repr_text.startswith("u'") and repr_text.endswith("'"):
            repr_text = repr_text[2:-1]  # 移除u'和'
        elif repr_text.startswith("'") and repr_text.endswith("'"):
            repr_text = repr_text[1:-1]  # 移除'和'
        elif repr_text.startswith('"') and repr_text.endswith('"'):
            repr_text = repr_text[1:-1]  # 移除"和"

        # 清理转义序列
        repr_text = repr_text.replace('\\\\', '\\')

        return repr_text.encode('ascii', 'ignore')
    except Exception:
        pass

    # 最终的最终兜底：返回空字符串
    return ""


def get_current_timestamp():
    """获取当前时间戳"""
    return time.strftime("%Y/%m/%d %H:%M")


def analyze_http_request(helpers, controller, content):
    """统一的HTTP请求解析方法"""
    if hasattr(controller, 'getHttpService') and controller:
        http_service = controller.getHttpService()
        if http_service:
            return helpers.analyzeRequest(http_service, content)
        else:
            return helpers.analyzeRequest(content)
    else:
        return helpers.analyzeRequest(content)


class ExtensionStateListener(IExtensionStateListener):
    """扩展状态监听器，用于在插件卸载时清理资源"""

    def __init__(self, extender):
        self._extender = extender

    def extensionUnloaded(self):
        """插件卸载时的清理工作"""
        try:
            self._extender._cleanupApiServices()
        except Exception:
            pass


def export_text_to_file(text_content, file_path, encoding='utf-8'):
    """通用的文件导出方法"""
    if not text_content:
        raise ValueError(u"没有可导出的内容！")

    if hasattr(text_content, 'tostring'):
        content_str = text_content.tostring()
    else:
        content_str = str(text_content)

    unicode_content = safe_unicode(content_str)
    java_string = String(unicode_content.encode('utf-8'), StandardCharsets.UTF_8)

    file_output_stream = FileOutputStream(file_path)
    output_stream_writer = OutputStreamWriter(file_output_stream, StandardCharsets.UTF_8)
    output_stream_writer.write(java_string)
    output_stream_writer.flush()
    output_stream_writer.close()
    file_output_stream.close()


class BurpExtender(IBurpExtender, IMessageEditorTabFactory, ITab):

    def registerExtenderCallbacks(self, callbacks):
        self._callbacks = callbacks
        self._helpers = callbacks.getHelpers()

        callbacks.setExtensionName("CurlonvertPro")

        self._stdout = PrintWriter(callbacks.getStdout(), True)
        self._stderr = PrintWriter(callbacks.getStderr(), True)

        self._conversion_cache = {}
        self._api_process = None
        self._managed_api_pids = set()

        self._initConfig()
        self._setupConfigUI()

        callbacks.registerMessageEditorTabFactory(self)
        callbacks.addSuiteTab(self)
        callbacks.registerExtensionStateListener(ExtensionStateListener(self))

        self._stdout.println(u"=== CurlonvertPro_v1.2.0 ===")
        self._stdout.println(u"\n插件加载成功！支持28种编程语言")
        self._stdout.println(u"插件作者：tomato")
        self._stdout.println(u"项目地址：https://github.com/sh2493770457/Python3/tree/main/burp_plugin/CurlonvertPro")
        self._stdout.println(u"当前API: " + self._config["current_api"])
        self._stdout.println(u"========================")

    def _initConfig(self):
        """初始化配置"""
        user_home = os.path.expanduser("~")
        self._config_dir = os.path.join(user_home, ".burp_code_converter")
        self._config_file = os.path.join(self._config_dir, "config.dat")

        default_config = {
            "current_api": "http://localhost:3000/convert",
            "current_language": "python",
            "enable_templates": True,
            "export_path": os.path.join(user_home, "Desktop"),
            "project_edit_path": "",
            "local_api_project_path": "",
            "local_api_status": "stopped",
            "api_list": [
                "http://localhost:3000/convert",
            ],
            "supported_languages": [
                "python", "javascript", "node", "java", "go", "php", "ruby",
                "rust", "csharp", "swift", "kotlin", "dart", "r", "julia",
                "perl", "lua", "clojure", "elixir", "objectivec", "ocaml",
                "matlab", "powershell", "ansible", "http", "httpie", "wget",
                "cfml", "c"
            ]
        }

        self._language_display_names = {
            "python": "Python", "javascript": "Javascript", "node": "Node",
            "java": "Java", "go": "Golang", "php": "Php", "ruby": "Ruby",
            "rust": "Rust", "csharp": "Csharp", "swift": "Swift", "kotlin": "Kotlin",
            "dart": "Dart", "r": "R", "julia": "Julia", "perl": "Perl",
            "lua": "Lua", "clojure": "Clojure", "elixir": "Elixir",
            "objectivec": "Objectivec", "ocaml": "Ocaml", "matlab": "Matlab",
            "powershell": "Powershell", "ansible": "Ansible", "http": "Http",
            "httpie": "Httpie", "wget": "Wget", "cfml": "Cfml", "c": "C"
        }

        self._config = self._loadConfig(default_config)

    def _getLanguageDisplayName(self, lang_code):
        """获取语言的显示名称（首字母大写）"""
        return self._language_display_names.get(lang_code, lang_code.capitalize())

    def _getLanguageDisplayNames(self):
        """获取所有语言的显示名称列表"""
        return [self._getLanguageDisplayName(lang) for lang in self._config["supported_languages"]]

    def _getLanguageCodeFromDisplayName(self, display_name):
        """从显示名称获取语言代码"""
        for code, name in self._language_display_names.items():
            if name == display_name:
                return code
        return display_name.lower()

    def _loadConfig(self, default_config):
        """加载配置文件"""
        try:
            if os.path.exists(self._config_file):
                with open(self._config_file, 'rb') as f:
                    saved_config = pickle.load(f)
                config = default_config.copy()
                config.update(saved_config)
                return config
        except Exception:
            pass
        return default_config

    def _saveConfig(self):
        """保存配置文件"""
        try:
            if not os.path.exists(self._config_dir):
                os.makedirs(self._config_dir)
            with open(self._config_file, 'wb') as f:
                pickle.dump(self._config, f)
        except Exception:
            pass

    def _setupConfigUI(self):
        """设置配置界面"""
        self._configPanel = JPanel(BorderLayout())
        self._configPanel.setBorder(EmptyBorder(10, 10, 10, 10))

        mainPanel = JPanel()
        mainPanel.setLayout(BoxLayout(mainPanel, BoxLayout.Y_AXIS))

        titleLabel = JLabel(u"CurlonvertPro Settings")
        titleLabel.setFont(Font("Arial", Font.BOLD, 16))
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT)
        mainPanel.add(titleLabel)
        mainPanel.add(Box.createVerticalStrut(20))

        langPanel = self._createLanguagePanel()
        mainPanel.add(langPanel)
        mainPanel.add(Box.createVerticalStrut(15))

        apiPanel = self._createApiPanel()
        mainPanel.add(apiPanel)
        mainPanel.add(Box.createVerticalStrut(15))

        exportPanel = self._createExportPanel()
        mainPanel.add(exportPanel)
        mainPanel.add(Box.createVerticalStrut(15))

        localApiPanel = self._createLocalApiPanel()
        mainPanel.add(localApiPanel)
        mainPanel.add(Box.createVerticalStrut(15))

        statusPanel = self._createStatusPanel()
        mainPanel.add(statusPanel)

        self._configPanel.add(mainPanel, BorderLayout.NORTH)

    def _createLanguagePanel(self):
        """创建语言选择面板"""
        panel = JPanel(FlowLayout(FlowLayout.LEFT))
        panel.setBorder(BorderFactory.createTitledBorder(u"目标语言"))

        panel.add(JLabel(u"转换为: "))

        display_names = self._getLanguageDisplayNames()
        self._languageCombo = JComboBox(display_names)
        current_display_name = self._getLanguageDisplayName(self._config["current_language"])
        self._languageCombo.setSelectedItem(current_display_name)
        self._languageCombo.addActionListener(LanguageChangeListener(self))
        panel.add(self._languageCombo)

        return panel

    def _createApiPanel(self):
        """创建API配置面板"""
        panel = JPanel()
        panel.setLayout(BoxLayout(panel, BoxLayout.Y_AXIS))
        panel.setBorder(BorderFactory.createTitledBorder(u"API配置"))

        selectPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        selectPanel.add(JLabel(u"API地址: "))

        self._apiCombo = JComboBox(self._config["api_list"])
        self._apiCombo.setSelectedItem(self._config["current_api"])
        self._apiCombo.setPreferredSize(Dimension(400, 25))
        self._apiCombo.addActionListener(ApiChangeListener(self))
        selectPanel.add(self._apiCombo)

        panel.add(selectPanel)

        inputPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        inputPanel.add(JLabel(u"自定义API: "))

        self._customApiField = JTextField(30)
        inputPanel.add(self._customApiField)

        addApiBtn = JButton(u"添加API")
        addApiBtn.addActionListener(AddApiListener(self))
        inputPanel.add(addApiBtn)

        removeApiBtn = JButton(u"删除选中")
        removeApiBtn.addActionListener(RemoveApiListener(self))
        inputPanel.add(removeApiBtn)

        panel.add(inputPanel)

        return panel

    def _createExportPanel(self):
        """创建导出路径配置面板"""
        exportPanel = JPanel()
        exportPanel.setLayout(BoxLayout(exportPanel, BoxLayout.Y_AXIS))
        exportPanel.setBorder(BorderFactory.createTitledBorder(u"导出设置"))

        defaultPathPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        defaultPathPanel.add(JLabel(u"默认导出路径: "))
        self._exportPathField = JTextField(self._config.get("export_path", ""), 25)
        self._exportPathField.setEditable(False)
        defaultPathPanel.add(self._exportPathField)

        browseDefaultBtn = JButton(u"浏览...")
        browseDefaultBtn.addActionListener(BrowseExportPathListener(self))
        defaultPathPanel.add(browseDefaultBtn)

        exportPanel.add(defaultPathPanel)

        projectPathPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        projectPathPanel.add(JLabel(u"项目编辑路径: "))
        self._projectEditPathField = JTextField(self._config.get("project_edit_path", ""), 25)
        self._projectEditPathField.setEditable(False)
        projectPathPanel.add(self._projectEditPathField)

        browseProjectBtn = JButton(u"浏览...")
        browseProjectBtn.addActionListener(BrowseProjectEditPathListener(self))
        projectPathPanel.add(browseProjectBtn)

        exportPanel.add(projectPathPanel)

        return exportPanel

    def _createLocalApiPanel(self):
        """创建本地API管理面板"""
        panel = JPanel()
        panel.setLayout(BoxLayout(panel, BoxLayout.Y_AXIS))
        panel.setBorder(BorderFactory.createTitledBorder(u"本地API管理"))

        pathPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        pathPanel.add(JLabel(u"程序路径: "))

        self._localApiPathField = JTextField(self._config.get("local_api_project_path", ""), 30)
        self._localApiPathField.setEditable(False)
        pathPanel.add(self._localApiPathField)

        browseApiBtn = JButton(u"选择目录")
        browseApiBtn.addActionListener(BrowseLocalApiPathListener(self))
        pathPanel.add(browseApiBtn)

        panel.add(pathPanel)

        controlPanel = JPanel(GridBagLayout())
        gbc = GridBagConstraints()
        gbc.insets = Insets(2, 5, 2, 5)
        gbc.gridy = 0

        gbc.gridx = 0
        gbc.anchor = GridBagConstraints.WEST
        gbc.fill = GridBagConstraints.NONE
        gbc.weightx = 0.0
        self._startApiBtn = JButton(u"启动API (start.vbs)")
        self._startApiBtn.addActionListener(StartLocalApiListener(self))
        self._startApiBtn.setEnabled(bool(self._config.get("local_api_project_path", "")))
        button_size = Dimension(160, 25)
        self._startApiBtn.setPreferredSize(button_size)
        self._startApiBtn.setMinimumSize(button_size)
        self._startApiBtn.setMaximumSize(button_size)
        controlPanel.add(self._startApiBtn, gbc)

        gbc.gridx = 1
        self._stopApiBtn = JButton(u"停止API")
        self._stopApiBtn.addActionListener(StopLocalApiListener(self))
        self._stopApiBtn.setEnabled(False)
        stop_size = Dimension(80, 25)
        self._stopApiBtn.setPreferredSize(stop_size)
        self._stopApiBtn.setMinimumSize(stop_size)
        self._stopApiBtn.setMaximumSize(stop_size)
        controlPanel.add(self._stopApiBtn, gbc)

        gbc.gridx = 2
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        status_text = u"状态: " + self._config.get("local_api_status", "stopped")
        self._apiStatusLabel = JLabel(status_text)
        self._apiStatusLabel.setPreferredSize(Dimension(0, 25))
        self._apiStatusLabel.setMinimumSize(Dimension(0, 25))
        if self._config.get("local_api_status", "stopped") == "running":
            self._apiStatusLabel.setForeground(Color(0, 128, 0))
        else:
            self._apiStatusLabel.setForeground(Color(128, 128, 128))
        controlPanel.add(self._apiStatusLabel, gbc)

        panel.add(controlPanel)

        return panel

    def _updateApiStatusDisplay(self):
        """更新API状态显示"""
        status = self._config.get("local_api_status", "stopped")
        if status == "running":
            self._apiStatusLabel.setText(u"状态: running")
            self._apiStatusLabel.setForeground(Color(0, 128, 0))
        else:
            self._apiStatusLabel.setText(u"状态: stopped")
            self._apiStatusLabel.setForeground(Color(128, 128, 128))

        self._apiStatusLabel.repaint()

    def _createStatusPanel(self):
        """创建状态面板"""
        panel = JPanel()
        panel.setLayout(BoxLayout(panel, BoxLayout.Y_AXIS))
        panel.setBorder(BorderFactory.createTitledBorder(u"状态"))

        self._statusArea = JTextArea(8, 50)
        self._statusArea.setEditable(False)
        self._statusArea.setFont(Font("Monospaced", Font.PLAIN, 12))
        self._updateStatusText()

        scrollPane = JScrollPane(self._statusArea)
        panel.add(scrollPane)

        buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        testBtn = JButton(u"测试API连接")
        testBtn.addActionListener(TestApiListener(self))
        buttonPanel.add(testBtn)

        clearCacheBtn = JButton(u"清除缓存")
        clearCacheBtn.addActionListener(ClearCacheListener(self))
        buttonPanel.add(clearCacheBtn)

        forceCleanBtn = JButton(u"强制清理端口3000")
        forceCleanBtn.addActionListener(ForceCleanPortListener(self))
        buttonPanel.add(forceCleanBtn)

        panel.add(buttonPanel)

        return panel

    def _updateStatusText(self):
        """更新状态文本"""
        status = u"当前配置:\n"
        status += u"语言: " + self._config["current_language"] + u"\n"
        status += u"API地址: " + self._config["current_api"] + u"\n"
        status += u"代码模板: " + (u"启用" if self._config.get("enable_templates", True) else u"禁用") + u"\n"
        status += u"缓存大小: " + safe_unicode(str(len(self._conversion_cache))) + u" 项\n"

        project_edit_path = self._config.get("project_edit_path", "")
        if project_edit_path:
            status += u"项目编辑路径: " + os.path.basename(project_edit_path) + u"\n"
        else:
            status += u"项目编辑路径: 未配置\n"

        local_api_path = self._config.get("local_api_project_path", "")
        local_api_status = self._config.get("local_api_status", "stopped")
        if local_api_path:
            status += u"本地程序目录: " + os.path.basename(local_api_path) + u"\n"
            status += u"API状态: " + local_api_status + u"\n"
        else:
            status += u"本地程序目录: 未配置\n"

        status += u"\n支持的语言 (" + safe_unicode(str(len(self._config["supported_languages"]))) + u"种):\n"

        languages = self._config["supported_languages"]
        for i in range(0, len(languages), 6):
            line_langs = languages[i:i + 6]
            status += ", ".join(line_langs) + "\n"

        template_languages = ["Python", "Javascript", "Node", "Java", "Golang", "Php"]
        if template_languages:
            status += u"\n已配置模板的语言:\n"
            status += ", ".join(template_languages) + u"\n"

        self._statusArea.setText(status)

    def _checkPortOccupied(self):
        """检查端口3000是否被占用，返回占用的进程PID列表"""
        try:
            import platform
            import subprocess

            platform_system = platform.system()
            is_windows = (
                    platform_system.lower() == "windows" or
                    os.name == "nt" or
                    "win" in platform_system.lower() or
                    os.path.exists("C:\\Windows")
            )

            if is_windows:
                try:
                    netstat_cmd = 'netstat -ano | findstr :3000'
                    result = subprocess.check_output(netstat_cmd, shell=True, stderr=subprocess.STDOUT)
                    output = result.decode('gbk', errors='ignore')

                    lines = output.strip().split('\n')
                    occupied_pids = []

                    for line in lines:
                        if line.strip():
                            parts = line.split()
                            if len(parts) >= 4:
                                local_address = parts[1]
                                state = parts[3] if len(parts) > 3 else ""
                                pid = parts[4] if len(parts) > 4 else ""

                                if ":3000" in local_address and "LISTENING" in state and pid.isdigit():
                                    occupied_pids.append(pid)

                    return occupied_pids

                except subprocess.CalledProcessError:
                    return []
            else:
                try:
                    lsof_cmd = 'lsof -i :3000'
                    result = subprocess.check_output(lsof_cmd, shell=True, stderr=subprocess.STDOUT)
                    output = result.decode('utf-8', errors='ignore')

                    lines = output.strip().split('\n')[1:]
                    occupied_pids = []

                    for line in lines:
                        if line.strip():
                            parts = line.split()
                            if len(parts) >= 2:
                                pid = parts[1]
                                if pid.isdigit():
                                    occupied_pids.append(pid)

                    return occupied_pids
                except subprocess.CalledProcessError:
                    return []

        except Exception:
            return []

    def _cleanupApiServices(self):
        """清理所有API服务"""
        try:
            if hasattr(self, '_api_process') and self._api_process:
                try:
                    self._api_process.terminate()
                    self._api_process.wait(timeout=3)
                    self._api_process = None
                except:
                    try:
                        self._api_process.kill()
                        self._api_process = None
                    except:
                        pass

            occupied_pids = self._checkPortOccupied()
            if occupied_pids:
                self._forceKillPort3000Processes(occupied_pids)

            if hasattr(self, '_managed_api_pids'):
                self._managed_api_pids.clear()

            self._config["local_api_status"] = "stopped"
            self._saveConfig()

        except Exception:
            pass

    def _forceKillPort3000Processes(self, pids):
        """强制终止指定的进程"""
        import platform
        import subprocess

        platform_system = platform.system()
        is_windows = (
                platform_system.lower() == "windows" or
                os.name == "nt" or
                "win" in platform_system.lower() or
                os.path.exists("C:\\Windows")
        )

        for pid in pids:
            try:
                if is_windows:
                    kill_cmd = 'taskkill /PID ' + pid + ' /F'
                    subprocess.check_output(kill_cmd, shell=True, stderr=subprocess.STDOUT)
                else:
                    kill_cmd = 'kill -9 ' + pid
                    subprocess.check_output(kill_cmd, shell=True, stderr=subprocess.STDOUT)

            except Exception:
                pass

    def _testApiConnection(self):
        """测试API连接"""
        try:
            api_url = self._config["current_api"]

            test_curl = 'curl -X GET "https://httpbin.org/get"'
            data = {
                "curl": test_curl,
                "language": "python"
            }

            json_data = json.dumps(data)

            req = urllib2.Request(api_url)
            req.add_header('Content-Type', 'application/json')
            req.add_header('Accept', '*/*')
            req.add_header('User-Agent', 'Code-Converter-Test/1.0')

            response = urllib2.urlopen(req, json_data.encode('utf-8'), timeout=5)
            response_data = response.read()

            result = json.loads(response_data)

            if result.get('success'):
                self._stdout.println(u"API连接成功!")
                status_text = self._statusArea.getText()
                status_text += u"\n[" + time.strftime("%H:%M:%S") + u"] API测试: 成功"
                self._statusArea.setText(status_text)
                JOptionPane.showMessageDialog(None, u"API连接成功!", u"测试结果", JOptionPane.INFORMATION_MESSAGE)
            else:
                error_msg = u"API返回错误: " + safe_unicode(str(result))
                status_text = self._statusArea.getText()
                status_text += u"\n[" + time.strftime("%H:%M:%S") + u"] API测试: 失败 - " + safe_unicode(str(
                    result.get('error', u'未知错误')))
                self._statusArea.setText(status_text)
                JOptionPane.showMessageDialog(None, error_msg, u"测试失败", JOptionPane.ERROR_MESSAGE)

        except Exception as e:
            error_msg = u"API连接失败: " + safe_unicode(e)
            status_text = self._statusArea.getText()
            status_text += u"\n[" + time.strftime("%H:%M:%S") + u"] API测试: 错误 - " + safe_unicode(e)
            self._statusArea.setText(status_text)
            JOptionPane.showMessageDialog(None, error_msg, u"连接错误", JOptionPane.ERROR_MESSAGE)

    # ITab interface methods
    def getTabCaption(self):
        return u"CurlonvertPro"

    def getUiComponent(self):
        return self._configPanel

    def createNewInstance(self, controller, editable):
        return CodeConverterTab(self, controller, editable)

    def _getPythonTemplate(self):
        """生成Python代码头部模板（动态时间戳）"""
        current_time = get_current_timestamp()
        return u"""# -*- coding: utf-8 -*-
# TODO:@ModuleName: 
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: {time}

""".format(time=current_time)

    def _getPythonFooter(self):
        """生成Python代码尾部模板"""
        return u"""

# 确保响应编码为UTF-8，避免中文乱码
response.encoding = 'utf-8'
print('[状态码]:', response.status_code)
print('[响应正文]:\\n', response.text)"""

    def _getJavaScriptTemplate(self):
        """生成JavaScript代码头部模板（动态时间戳）"""
        current_time = get_current_timestamp()
        return u"""/**
 * @ModuleName: 
 * @Author: tomato
 * @Version: Node.js v22.16.0
 * @Time: {time}
 */
""".format(time=current_time)

    def _getJavaScriptFooter(self):
        """生成JavaScript代码尾部模板"""
        return u"""
console.log('[状态码]:', response.status);
response.text().then(data => {
    console.log('[响应正文]:', data);
});"""

    def _getJavaTemplate(self):
        """生成Java代码头部模板（动态时间戳）"""
        current_time = get_current_timestamp()
        return u"""/**
 * @ModuleName: 
 * @Author: tomato
 * @Version: JDK 21
 * @Time: {time}
 */
""".format(time=current_time)

    def _getJavaFooter(self):
        """生成Java代码尾部模板"""
        return u"""
System.out.println("[状态码]: " + response.statusCode());
System.out.println("[响应正文]: " + response.body());"""

    def _getGoTemplate(self):
        """生成Go代码头部模板（动态时间戳）"""
        current_time = get_current_timestamp()
        return u"""/*
 * @ModuleName: 
 * @Author: tomato
 * @Version: Go 1.24.2
 * @Time: {time}
 */
""".format(time=current_time)

    def _getGoFooter(self):
        """生成Go代码尾部模板"""
        return u"""
fmt.Printf("[状态码]: %d\\n", resp.StatusCode)
body, _ := ioutil.ReadAll(resp.Body)
fmt.Printf("[响应正文]: %s\\n", string(body))"""

    def _getPhpTemplate(self):
        """生成PHP代码头部模板（动态时间戳）"""
        current_time = get_current_timestamp()
        return u"""<?php
/**
 * @ModuleName: 
 * @Author: tomato
 * @Version: PHP 8.2.0
 * @Time: {time}
 */
""".format(time=current_time)

    def _getPhpFooter(self):
        """生成PHP代码尾部模板"""
        return u"""
echo "[状态码]: " . curl_getinfo($ch, CURLINFO_HTTP_CODE) . "\\n";
echo "[响应正文]: " . $response . "\\n";
?>"""


# Action Listeners
class LanguageChangeListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        selected_display_name = self._extender._languageCombo.getSelectedItem()
        selected_language = self._extender._getLanguageCodeFromDisplayName(selected_display_name)
        self._extender._config["current_language"] = selected_language
        self._extender._saveConfig()
        self._extender._updateStatusText()


class ApiChangeListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        selected_api = self._extender._apiCombo.getSelectedItem()
        self._extender._config["current_api"] = selected_api
        self._extender._saveConfig()
        self._extender._updateStatusText()


class AddApiListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        custom_api = self._extender._customApiField.getText().strip()
        if custom_api and custom_api not in self._extender._config["api_list"]:
            self._extender._config["api_list"].append(custom_api)
            self._extender._apiCombo.addItem(custom_api)
            self._extender._apiCombo.setSelectedItem(custom_api)
            self._extender._config["current_api"] = custom_api
            self._extender._customApiField.setText("")
            self._extender._updateStatusText()
        elif custom_api in self._extender._config["api_list"]:
            JOptionPane.showMessageDialog(None, u"API地址已存在！")


class RemoveApiListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        selected_api = self._extender._apiCombo.getSelectedItem()
        if len(self._extender._config["api_list"]) > 1:
            self._extender._config["api_list"].remove(selected_api)
            self._extender._apiCombo.removeItem(selected_api)
            self._extender._config["current_api"] = self._extender._apiCombo.getSelectedItem()
            self._extender._updateStatusText()
        else:
            JOptionPane.showMessageDialog(None, u"不能删除最后一个API地址！")


class TestApiListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        self._extender._testApiConnection()


class ClearCacheListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        cache_size = len(self._extender._conversion_cache)
        self._extender._conversion_cache.clear()
        self._extender._updateStatusText()


class ForceCleanPortListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        try:
            occupied_pids = self._extender._checkPortOccupied()

            if not occupied_pids:
                JOptionPane.showMessageDialog(None, u"端口3000当前未被占用！")
                return

            message = u"检测到端口3000被以下进程占用:\n"
            for pid in occupied_pids:
                message += u"PID: " + pid + u"\n"
            message += u"\n确定要强制关闭这些进程吗？\n⚠️ 此操作不可撤销！"

            choice = JOptionPane.showConfirmDialog(
                None,
                message,
                u"强制清理确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            )

            if choice == JOptionPane.YES_OPTION:
                self._extender._forceKillPort3000Processes(occupied_pids)

                import time
                time.sleep(1)
                remaining_pids = self._extender._checkPortOccupied()

                if remaining_pids:
                    error_msg = u"清理未完全成功，仍有进程占用端口: " + safe_unicode(str(remaining_pids))
                    JOptionPane.showMessageDialog(None, error_msg, u"清理失败", JOptionPane.ERROR_MESSAGE)
                else:
                    JOptionPane.showMessageDialog(None, u"端口3000已成功清理！")

                    self._extender._config["local_api_status"] = "stopped"
                    self._extender._saveConfig()
                    self._extender._startApiBtn.setEnabled(True)
                    self._extender._stopApiBtn.setEnabled(False)
                    self._extender._updateApiStatusDisplay()
                    self._extender._updateStatusText()

        except Exception as e:
            error_msg = u"强制清理端口时出错: " + safe_unicode(e)
            JOptionPane.showMessageDialog(None, error_msg, u"清理错误", JOptionPane.ERROR_MESSAGE)


class BrowseExportPathListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        import java.io
        chooser = JFileChooser()
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        chooser.setDialogTitle(u"选择默认导出路径")
        chooser.setCurrentDirectory(java.io.File(self._extender._config.get("export_path", "")))

        if chooser.showOpenDialog(None) == JFileChooser.APPROVE_OPTION:
            selected_path = chooser.getSelectedFile().getAbsolutePath()
            self._extender._config["export_path"] = selected_path
            self._extender._exportPathField.setText(selected_path)
            self._extender._saveConfig()
            self._extender._updateStatusText()
            JOptionPane.showMessageDialog(None, u"默认导出路径已更新！")


class BrowseProjectEditPathListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        import java.io
        chooser = JFileChooser()
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        chooser.setDialogTitle(u"选择项目编辑路径")

        current_path = self._extender._config.get("project_edit_path", "")
        if current_path and os.path.exists(current_path):
            chooser.setCurrentDirectory(java.io.File(current_path))

        if chooser.showOpenDialog(None) == JFileChooser.APPROVE_OPTION:
            selected_path = chooser.getSelectedFile().getAbsolutePath()
            self._extender._config["project_edit_path"] = selected_path
            self._extender._projectEditPathField.setText(selected_path)
            self._extender._saveConfig()
            self._extender._updateStatusText()
            JOptionPane.showMessageDialog(None, u"项目编辑路径已更新！")


class BrowseLocalApiPathListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        import java.io
        chooser = JFileChooser()
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        chooser.setDialogTitle(u"选择包含 start.vbs 的文件夹")

        current_path = self._extender._config.get("local_api_project_path", "")
        if current_path and os.path.exists(current_path):
            chooser.setCurrentDirectory(java.io.File(current_path))

        if chooser.showOpenDialog(None) == JFileChooser.APPROVE_OPTION:
            selected_path = chooser.getSelectedFile().getAbsolutePath()

            start_vbs_path = os.path.join(selected_path, "start.vbs")
            if os.path.exists(start_vbs_path):
                self._extender._config["local_api_project_path"] = selected_path
                self._extender._localApiPathField.setText(selected_path)
                self._extender._startApiBtn.setEnabled(True)
                self._extender._saveConfig()
                self._extender._updateStatusText()
                JOptionPane.showMessageDialog(None, u"程序路径已设置！")
            else:
                JOptionPane.showMessageDialog(None,
                                              u"选择的文件夹不包含 start.vbs 文件！\n请选择包含 start.vbs 的文件夹。",
                                              u"路径错误", JOptionPane.WARNING_MESSAGE)


class StartLocalApiListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        project_path = self._extender._config.get("local_api_project_path", "")
        if not project_path or not os.path.exists(project_path):
            JOptionPane.showMessageDialog(None, u"请先选择有效的程序路径！")
            return

        # 检查 start.vbs 文件是否存在
        start_vbs_path = os.path.join(project_path, "start.vbs")
        if not os.path.exists(start_vbs_path):
            JOptionPane.showMessageDialog(None, u"选择的目录中未找到 start.vbs 文件！")
            return

        try:
            occupied_pids = self._extender._checkPortOccupied()
            if occupied_pids:
                message = u"检测到端口3000已被以下进程占用:\n"
                for pid in occupied_pids:
                    message += u"PID: " + pid + u"\n"
                message += u"\n是否要强制关闭这些进程并启动新的API服务?"

                choice = JOptionPane.showConfirmDialog(
                    None,
                    message,
                    u"端口冲突",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                )

                if choice == JOptionPane.YES_OPTION:
                    self._extender._forceKillPort3000Processes(occupied_pids)

                    import time
                    time.sleep(2)

                    remaining_pids = self._extender._checkPortOccupied()
                    if remaining_pids:
                        JOptionPane.showMessageDialog(None, u"无法完全关闭现有进程，请手动处理后再试！")
                        return
                else:
                    return

            if hasattr(self._extender, '_api_process') and self._extender._api_process:
                if self._extender._api_process.poll() is None:
                    JOptionPane.showMessageDialog(None, u"插件管理的API进程仍在运行，请先停止！")
                    return

            import subprocess
            import platform

            # 构建 start.vbs 的完整路径
            start_vbs_path = os.path.join(project_path, "start.vbs")

            system_name = platform.system()
            if system_name.lower().startswith("win") or system_name.lower() == "java":
                try:
                    # 在 Windows 上执行 start.vbs
                    vbs_cmd = 'cscript.exe "' + start_vbs_path + '"'
                    self._extender._api_process = subprocess.Popen(
                        vbs_cmd,
                        shell=True,
                        stdout=subprocess.PIPE,
                        stderr=subprocess.STDOUT,
                        cwd=project_path
                    )
                except:
                    # 备用方法：直接执行 vbs 文件
                    self._extender._api_process = subprocess.Popen(
                        [start_vbs_path],
                        shell=True,
                        stdout=subprocess.PIPE,
                        stderr=subprocess.STDOUT,
                        cwd=project_path
                    )
            else:
                raise Exception(u"VBS 文件只能在 Windows 系统上执行, 当前 platform.system() = %s" % system_name)

            import time
            time.sleep(3)

            new_pids = self._extender._checkPortOccupied()
            if new_pids:
                self._extender._managed_api_pids.update(new_pids)

            self._extender._config["local_api_status"] = "running"
            self._extender._saveConfig()
            self._extender._startApiBtn.setEnabled(False)
            self._extender._stopApiBtn.setEnabled(True)
            self._extender._updateApiStatusDisplay()
            self._extender._updateStatusText()

            JOptionPane.showMessageDialog(None,
                                          u"start.vbs 程序启动成功！\nAPI服务地址为: http://localhost:3000/convert")

        except Exception as e:
            error_msg = u"启动 start.vbs 程序失败: " + safe_unicode(e)
            JOptionPane.showMessageDialog(None, error_msg, u"启动失败", JOptionPane.ERROR_MESSAGE)


class StopLocalApiListener(ActionListener):
    def __init__(self, extender):
        self._extender = extender

    def actionPerformed(self, event):
        try:
            current_pids = self._extender._checkPortOccupied()

            if hasattr(self._extender, '_api_process') and self._extender._api_process:
                try:
                    self._extender._api_process.terminate()

                    try:
                        self._extender._api_process.wait(timeout=3)
                    except:
                        self._extender._api_process.kill()

                    self._extender._api_process = None
                except Exception:
                    pass

            import time
            time.sleep(1)

            remaining_pids = self._extender._checkPortOccupied()
            if remaining_pids:
                self._extender._forceKillPort3000Processes(remaining_pids)

                time.sleep(1)
                final_check_pids = self._extender._checkPortOccupied()
                if final_check_pids:
                    pass

            if hasattr(self._extender, '_managed_api_pids'):
                self._extender._managed_api_pids.clear()

            self._extender._config["local_api_status"] = "stopped"
            self._extender._saveConfig()
            self._extender._startApiBtn.setEnabled(True)
            self._extender._stopApiBtn.setEnabled(False)
            self._extender._updateApiStatusDisplay()
            self._extender._updateStatusText()

            JOptionPane.showMessageDialog(None, u"API停止操作完成！")

        except Exception as e:
            error_msg = u"停止程序时出错: " + safe_unicode(e)
            JOptionPane.showMessageDialog(None, error_msg, u"停止失败", JOptionPane.ERROR_MESSAGE)


class LocalLanguageChangeListener(ActionListener):
    def __init__(self, tab):
        self._tab = tab

    def actionPerformed(self, event):
        if self._tab._currentContent:
            selected_display_name = self._tab._localLanguageCombo.getSelectedItem()
            selected_language = self._tab._extender._getLanguageCodeFromDisplayName(selected_display_name)
            self._tab._convertWithLanguage(self._tab._currentContent, selected_language)


class RetryConversionListener(ActionListener):
    def __init__(self, tab):
        self._tab = tab

    def actionPerformed(self, event):
        if self._tab._currentContent:
            selected_display_name = self._tab._localLanguageCombo.getSelectedItem()
            selected_language = self._tab._extender._getLanguageCodeFromDisplayName(selected_display_name)
            self._tab._forceRefresh = True
            content_str = self._tab._extender._helpers.bytesToString(self._tab._currentContent)
            request_hash = hash(content_str + selected_language)
            if request_hash in self._tab._extender._conversion_cache:
                del self._tab._extender._conversion_cache[request_hash]
            self._tab._convertWithLanguage(self._tab._currentContent, selected_language)


class TemplateToggleListener(ActionListener):
    def __init__(self, tab):
        self._tab = tab

    def actionPerformed(self, event):
        self._tab._extender._config["enable_templates"] = self._tab._templateCheckBox.isSelected()
        self._tab._extender._saveConfig()
        if self._tab._currentContent:
            selected_display_name = self._tab._localLanguageCombo.getSelectedItem()
            selected_language = self._tab._extender._getLanguageCodeFromDisplayName(selected_display_name)
            content_str = self._tab._extender._helpers.bytesToString(self._tab._currentContent)
            request_hash = hash(content_str + selected_language)
            if request_hash in self._tab._extender._conversion_cache:
                del self._tab._extender._conversion_cache[request_hash]
            self._tab._convertWithLanguage(self._tab._currentContent, selected_language)


class CopyAllListener(ActionListener):
    def __init__(self, tab):
        self._tab = tab

    def actionPerformed(self, event):
        text_content = self._tab._txtInput.getText()
        if text_content:
            try:
                if hasattr(text_content, 'tostring'):
                    content_str = text_content.tostring()
                else:
                    content_str = str(text_content)

                unicode_content = safe_unicode(content_str)

                string_selection = StringSelection(unicode_content)
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
                clipboard.setContents(string_selection, None)
                JOptionPane.showMessageDialog(None, u"代码已复制到剪贴板！")
            except Exception as e:
                error_msg = u"复制失败: " + safe_unicode(e)
                JOptionPane.showMessageDialog(None, error_msg)
        else:
            JOptionPane.showMessageDialog(None, u"没有可复制的内容！")


class BaseExportListener(ActionListener):
    """基础导出监听器，包含公共导出逻辑"""

    def __init__(self, tab):
        self._tab = tab

    def _exportToPath(self, export_path, path_type):
        """公共导出方法"""
        text_content = self._tab._txtInput.getText()
        if not text_content:
            JOptionPane.showMessageDialog(None, u"没有可导出的内容！")
            return

        selected_display_name = self._tab._localLanguageCombo.getSelectedItem()
        selected_language = self._tab._extender._getLanguageCodeFromDisplayName(selected_display_name)

        extension = FILE_EXTENSIONS.get(selected_language, ".txt")

        filename = self._tab._generateMeaningfulFilename(extension)

        full_path = os.path.join(export_path, filename)

        try:
            export_text_to_file(text_content, full_path)
            JOptionPane.showMessageDialog(None, u"文件已导出到" + path_type + u": " + full_path)
        except Exception as e:
            error_msg = u"导出到" + path_type + u"失败: " + safe_unicode(e)
            JOptionPane.showMessageDialog(None, error_msg)


class ExportToDefaultPathListener(BaseExportListener):
    def __init__(self, tab):
        super(ExportToDefaultPathListener, self).__init__(tab)

    def actionPerformed(self, event):
        export_path = self._tab._extender._config.get("export_path", "")
        if not export_path:
            JOptionPane.showMessageDialog(None, u"请先配置默认导出路径！")
            return
        self._exportToPath(export_path, u"默认路径")


class ExportToProjectPathListener(BaseExportListener):
    def __init__(self, tab):
        super(ExportToProjectPathListener, self).__init__(tab)

    def actionPerformed(self, event):
        project_path = self._tab._extender._config.get("project_edit_path", "")
        if not project_path:
            JOptionPane.showMessageDialog(None, u"请先配置项目编辑路径！")
            return
        self._exportToPath(project_path, u"编辑路径")


class RightClickMenuListener(MouseAdapter):
    def __init__(self, tab, popup):
        self._tab = tab
        self._popup = popup

    def mousePressed(self, event):
        if event.isPopupTrigger():
            self._popup.show(event.getComponent(), event.getX(), event.getY())

    def mouseReleased(self, event):
        if event.isPopupTrigger():
            self._popup.show(event.getComponent(), event.getX(), event.getY())


class CodeConverterTab(IMessageEditorTab):

    def __init__(self, extender, controller, editable):
        self._extender = extender
        self._controller = controller
        self._editable = False  # 强制只读
        self._txtInput = extender._callbacks.createTextEditor()
        self._txtInput.setEditable(False)
        self._currentRequestHash = None
        self._originalMessage = None
        self._setupUI()

    def _setupUI(self):
        """设置UI界面"""
        self._mainPanel = JPanel(BorderLayout())

        controlPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        controlPanel.setBorder(EmptyBorder(5, 5, 5, 5))

        controlPanel.add(JLabel(u"语言: "))
        display_names = self._extender._getLanguageDisplayNames()
        self._localLanguageCombo = JComboBox(display_names)
        current_display_name = self._extender._getLanguageDisplayName(self._extender._config["current_language"])
        self._localLanguageCombo.setSelectedItem(current_display_name)
        self._localLanguageCombo.addActionListener(LocalLanguageChangeListener(self))
        controlPanel.add(self._localLanguageCombo)

        self._templateCheckBox = JCheckBox(u"启用模板", self._extender._config["enable_templates"])
        self._templateCheckBox.addActionListener(TemplateToggleListener(self))
        controlPanel.add(self._templateCheckBox)

        self._retryBtn = JButton(u"重新转换")
        self._retryBtn.addActionListener(RetryConversionListener(self))
        self._retryBtn.setEnabled(False)
        controlPanel.add(self._retryBtn)

        self._copyBtn = JButton(u"复制全部")
        self._copyBtn.addActionListener(CopyAllListener(self))
        controlPanel.add(self._copyBtn)

        self._mainPanel.add(controlPanel, BorderLayout.NORTH)
        self._mainPanel.add(self._txtInput.getComponent(), BorderLayout.CENTER)

        self._setupRightClickMenu()

        self._currentContent = None
        self._lastConversionFailed = False

    def _setupRightClickMenu(self):
        """设置右键菜单"""
        popup = JPopupMenu()

        copyItem = JMenuItem(u"复制全部")
        copyItem.addActionListener(CopyAllListener(self))
        popup.add(copyItem)

        popup.addSeparator()

        exportDefaultItem = JMenuItem(u"导出 -> 默认路径")
        exportDefaultItem.addActionListener(ExportToDefaultPathListener(self))
        popup.add(exportDefaultItem)

        exportProjectItem = JMenuItem(u"导出 -> 编辑路径")
        exportProjectItem.addActionListener(ExportToProjectPathListener(self))
        popup.add(exportProjectItem)

        textComponent = self._txtInput.getComponent()
        textComponent.addMouseListener(RightClickMenuListener(self, popup))

    def getTabCaption(self):
        return u"CurlonvertPro"

    def getUiComponent(self):
        return self._mainPanel

    def isEnabled(self, content, isRequest):
        return isRequest and content is not None

    def setMessage(self, content, isRequest):
        if content is None:
            self._txtInput.setText("")
            self._currentRequestHash = None
            self._currentContent = None
            self._originalMessage = None
            self._retryBtn.setEnabled(False)
            self._retryBtn.setText(u"重新转换")
            return

        if isRequest:
            self._originalMessage = content
            self._currentContent = content
            selected_display_name = self._localLanguageCombo.getSelectedItem()
            selected_language = self._extender._getLanguageCodeFromDisplayName(selected_display_name)
            self._convertWithLanguage(content, selected_language)
        else:
            self._txtInput.setText("")
            self._currentContent = None
            self._originalMessage = None
            self._retryBtn.setEnabled(False)

    def _convertWithLanguage(self, content, target_language):
        """使用指定语言转换请求"""
        try:
            content_str = self._extender._helpers.bytesToString(content)
            request_hash = hash(content_str + target_language)

            self._retryBtn.setEnabled(False)
            self._retryBtn.setText(u"转换中...")

            if hasattr(self, '_lastRequestHash') and self._lastRequestHash == request_hash and not hasattr(self,
                                                                                                           '_forceRefresh'):
                if request_hash in self._extender._conversion_cache:
                    cached_result = self._extender._conversion_cache[request_hash]
                    safe_text = safe_unicode(cached_result)
                    display_text = self._safeDisplayText(safe_text)
                    self._txtInput.setText(safe_encode_utf8(display_text))
                    self._lastConversionFailed = False
                    self._retryBtn.setEnabled(True)
                    self._retryBtn.setText(u"重新转换")
                    return

            self._lastRequestHash = request_hash

            if request_hash in self._extender._conversion_cache and not hasattr(self, '_forceRefresh'):
                cached_result = self._extender._conversion_cache[request_hash]
                safe_text = safe_unicode(cached_result)
                display_text = self._safeDisplayText(safe_text)
                self._txtInput.setText(safe_encode_utf8(display_text))
                self._lastConversionFailed = False
                self._retryBtn.setEnabled(True)
                self._retryBtn.setText(u"重新转换")
                return

            if hasattr(self, '_forceRefresh'):
                delattr(self, '_forceRefresh')

            loading_msg = u"正在转换为 " + target_language + u" 代码，请稍候..."
            safe_loading_msg = self._safeDisplayText(loading_msg)
            self._txtInput.setText(safe_encode_utf8(safe_loading_msg))

            requestInfo = analyze_http_request(self._extender._helpers, self._controller, content)
            headers = requestInfo.getHeaders()

            curl_command = self._buildCurlCommand(content, requestInfo, headers)

            if curl_command:
                converted_code = self._convertToLanguage(curl_command, target_language)

                if converted_code and not converted_code.startswith(
                        "# Curl to " + target_language.upper() + " Converter - API ERROR"):
                    safe_code = safe_unicode(converted_code)
                    self._extender._conversion_cache[request_hash] = safe_code
                    display_code = self._safeDisplayText(safe_code)
                    self._txtInput.setText(safe_encode_utf8(display_code))
                    self._lastConversionFailed = False
                    self._retryBtn.setEnabled(True)
                    self._retryBtn.setText(u"重新转换")
                else:
                    safe_error = safe_unicode(converted_code) if converted_code else u"转换失败"
                    display_error = self._safeDisplayText(safe_error)
                    self._txtInput.setText(safe_encode_utf8(display_error))
                    self._lastConversionFailed = True
                    self._retryBtn.setEnabled(False)
                    self._retryBtn.setText(u"转换失败")
            else:
                error_msg = u"无法从此请求生成curl命令\n\n"
                error_msg += u"可能的原因:\n"
                error_msg += u"• HTTP服务信息不可用\n"
                error_msg += u"• 请求URL格式异常\n"
                error_msg += u"• 请求内容损坏或不完整\n\n"
                error_msg += u"请尝试:\n"
                error_msg += u"1. 刷新请求列表\n"
                error_msg += u"2. 重新发送请求\n"
                error_msg += u"3. 检查请求是否完整"
                display_error_msg = self._safeDisplayText(error_msg)
                self._txtInput.setText(safe_encode_utf8(display_error_msg))
                self._lastConversionFailed = True
                self._retryBtn.setEnabled(True)
                self._retryBtn.setText(u"重新尝试")

        except Exception as e:
            error_msg = u"处理请求时出错: " + safe_unicode(e)
            display_error_msg = self._safeDisplayText(error_msg)
            self._txtInput.setText(safe_encode_utf8(display_error_msg))
            self._lastConversionFailed = True
            self._retryBtn.setEnabled(False)
            self._retryBtn.setText(u"转换失败")

    def getMessage(self):
        return self._originalMessage

    def isModified(self):
        return False

    def getSelectedData(self):
        return self._txtInput.getSelectedText()

    def _safeDisplayText(self, text):
        """安全设置编码问题"""
        try:
            if text is None:
                return u"# 无内容"

            if isinstance(text, str):
                try:
                    unicode_text = text.decode("utf-8")
                except:
                    try:
                        unicode_text = text.decode("gbk")
                    except:
                        unicode_text = unicode(text, errors="ignore")
            else:
                unicode_text = unicode(text)

            return unicode_text
        except Exception as e:
            return u"# 文本显示错误: {}".format(unicode(str(e)))

    def _detectChineseCharacters(self, content):
        """检测请求内容中是否包含中文字符"""
        try:
            if content is None or len(content) == 0:
                return False, ""

            requestInfo = analyze_http_request(self._extender._helpers, self._controller, content)
            if requestInfo.getBodyOffset() < len(content):
                body = content[requestInfo.getBodyOffset():]
                if body and len(body) > 0:
                    body_str = self._bytesToUtf8String(body)
                    chinese_chars = []
                    for i, char in enumerate(body_str):
                        if isinstance(char, unicode) and ord(char) > 127:
                            if 0x4e00 <= ord(char) <= 0x9fff:
                                chinese_chars.append((i, char))

                    if chinese_chars:
                        return True, u"请求体包含中文字符，数量: " + safe_unicode(str(len(chinese_chars))) + u"个"

            return False, ""
        except Exception:
            return False, ""

    def _bytesToUtf8String(self, body_bytes):
        """从字节数组正确解码UTF-8字符串，特别优化中文字符处理"""
        if body_bytes is None or len(body_bytes) == 0:
            return u""

        try:
            # 方法1：使用Java的String构造函数 - 最可靠的UTF-8解码方式
            from java.lang import String
            from java.nio.charset import StandardCharsets

            if hasattr(body_bytes, '__getitem__') and hasattr(body_bytes, '__len__'):
                # 安全地转换字节数组
                java_bytes = []
                for i in range(len(body_bytes)):
                    try:
                        byte_val = body_bytes[i]
                        # 处理负数字节值（Java字节是有符号的）
                        if byte_val < 0:
                            byte_val = byte_val + 256
                        # 确保字节值在正确范围内
                        if byte_val > 255:
                            byte_val = byte_val % 256
                        java_bytes.append(byte_val)
                    except (IndexError, TypeError):
                        # 如果某个字节无法读取，跳过
                        continue

                # 转换为Java字节数组
                from java.lang import Byte
                try:
                    java_byte_array = [Byte.valueOf(b if b < 128 else b - 256) for b in java_bytes]
                    # 使用UTF-8创建String
                    java_string = String(java_byte_array, StandardCharsets.UTF_8)
                    result = safe_unicode(java_string.toString())
                    return result
                except Exception as java_error:
                    # Java String构造失败，回退到其他方法
                    pass
            else:
                # 直接使用字节数组
                try:
                    java_string = String(body_bytes, StandardCharsets.UTF_8)
                    return safe_unicode(java_string.toString())
                except Exception:
                    pass

        except Exception as method1_error:
            pass

        try:
            # 方法2：手动字节处理，使用安全的Unicode解码
            if hasattr(body_bytes, "__len__") and hasattr(body_bytes, "__getitem__"):
                byte_list = []
                for i in range(len(body_bytes)):
                    try:
                        byte_val = body_bytes[i]
                        # 处理负数字节
                        if byte_val < 0:
                            byte_val = byte_val + 256
                        # 确保在有效范围内
                        if 0 <= byte_val <= 255:
                            byte_list.append(chr(byte_val))
                        else:
                            byte_list.append('?')  # 无效字节用?替代
                    except (IndexError, TypeError, ValueError):
                        byte_list.append('?')  # 出错时用?替代

                # 将字节列表连接成字符串，然后用UTF-8解码
                byte_string = "".join(byte_list)

                # 尝试多种编码方式解码
                encodings = ['utf-8', 'gbk', 'gb2312', 'latin1']
                for encoding in encodings:
                    try:
                        if hasattr(byte_string, 'decode'):
                            result = byte_string.decode(encoding)
                            return safe_unicode(result)
                    except (UnicodeDecodeError, UnicodeEncodeError):
                        continue

                # 所有编码都失败，使用UTF-8的ignore模式
                try:
                    if hasattr(byte_string, 'decode'):
                        result = byte_string.decode('utf-8', 'ignore')
                        return safe_unicode(result)
                except:
                    pass
            else:
                # 不是可迭代的字节数组，直接转换
                return safe_unicode(str(body_bytes))

        except Exception as method2_error:
            pass

        try:
            # 方法3：使用Burp的辅助方法作为备选
            result = self._extender._helpers.bytesToString(body_bytes)
            return safe_unicode(result)
        except Exception as method3_error:
            pass

        # 最后的兜底方案：使用safe_decode_bytes
        try:
            return safe_decode_bytes(body_bytes)
        except Exception:
            # 最终兜底：返回错误占位符
            return u"<bytes_decode_error>"

    def _generateMeaningfulFilename(self, extension):
        """生成有意义的文件名"""
        try:
            if self._currentContent:
                requestInfo = analyze_http_request(self._extender._helpers, self._controller, self._currentContent)
                headers = requestInfo.getHeaders()

                method = requestInfo.getMethod().lower()

                try:
                    from java.net import URL
                    url_obj = URL(str(requestInfo.getUrl()))
                    hostname = url_obj.getHost()
                    port = str(url_obj.getPort()) if url_obj.getPort() != -1 else str(url_obj.getDefaultPort())
                except Exception:
                    hostname = "unknown_host"
                    port = "unknown_port"

                hostname_clean = hostname.replace(".", "_")

                import time
                timestamp = time.strftime("%Y%m%d_%H%M%S")

                filename = method + "_" + hostname_clean + "_" + port + "_" + timestamp + extension
                return filename

        except Exception:
            pass

        import time
        timestamp = time.strftime("%Y%m%d_%H%M%S")
        return "curlonvert_code_" + timestamp + extension

    def _buildCurlCommand(self, content, requestInfo, headers):
        """构建curl命令，优化中文字符处理"""
        try:
            # 安全地获取请求方法
            try:
                method = safe_unicode(requestInfo.getMethod())
            except Exception:
                method = u"GET"  # 默认方法

            try:
                original_url = safe_unicode(str(requestInfo.getUrl()))
                if not original_url:
                    return None

                from java.net import URL
                url_obj = URL(original_url)
                protocol = url_obj.getProtocol()
                host = url_obj.getHost()
                port = url_obj.getPort()
                path = url_obj.getPath()
                query = url_obj.getQuery()

                url_without_default_port = protocol + "://" + host
                if port != -1 and not ((protocol == "http" and port == 80) or (protocol == "https" and port == 443)):
                    url_without_default_port += ":" + str(port)
                url_without_default_port += path
                if query:
                    url_without_default_port += "?" + query

            except Exception:
                return None

            curl_parts = ["curl", "--path-as-is", "-i", "-s", "-k", "-X", "$'" + method.upper() + "'"]

            cookie_values = []
            other_headers = []
            host_header = None

            for header in headers[1:]:
                try:
                    # 安全地处理每个header
                    safe_header_str = safe_unicode(header) if header else u""
                    if safe_header_str and ":" in safe_header_str:
                        header_parts = safe_header_str.split(":", 1)
                        if len(header_parts) == 2:
                            header_name = safe_unicode(header_parts[0].strip().lower())
                            header_value = safe_unicode(header_parts[1].strip())

                            if header_name == u"cookie":
                                cookie_values.append(header_value)
                            elif header_name == u"host":
                                host_header = header_value
                            elif header_name not in EXCLUDED_HEADERS:
                                # 检测header值中是否有中文字符，使用安全的字符检查
                                header_has_chinese = False
                                try:
                                    header_has_chinese = any(ord(char) > 127 for char in header_value if
                                                             isinstance(char, unicode) and 0x4e00 <= ord(
                                                                 char) <= 0x9fff)
                                except (TypeError, ValueError):
                                    header_has_chinese = False

                                if header_has_chinese:
                                    safe_value = header_value
                                    safe_value = safe_value.replace("\\", "\\\\")
                                    safe_value = safe_value.replace('"', '\\"')
                                    safe_value = safe_value.replace("$", "\\$")
                                    escaped_header = header_name.title() + ": " + safe_value
                                    other_headers.append('-H "' + escaped_header + '"')
                                else:
                                    escaped_value = header_value.replace("\\", "\\\\").replace("'", "\\'").replace("\"",
                                                                                                                   "\\\"")
                                    escaped_header = header_name.title() + ": " + escaped_value
                                    other_headers.append("-H $'" + escaped_header + "'")
                except Exception as header_error:
                    # 如果某个header处理失败，跳过它，继续处理其他headers
                    continue

            if host_header:
                curl_parts.append("-H $'Host: " + host_header + "'")

            curl_parts.extend(other_headers)

            if cookie_values:
                try:
                    # 安全地处理cookie值
                    safe_cookie_values = [safe_unicode(cookie) for cookie in cookie_values]
                    cookie_string = "; ".join(safe_cookie_values)

                    # 安全地检测中文字符
                    cookie_has_chinese = False
                    try:
                        cookie_has_chinese = any(ord(char) > 127 for char in cookie_string if
                                                 isinstance(char, unicode) and 0x4e00 <= ord(char) <= 0x9fff)
                    except (TypeError, ValueError):
                        cookie_has_chinese = False

                    if cookie_has_chinese:
                        safe_cookie = cookie_string
                        safe_cookie = safe_cookie.replace("\\", "\\\\")
                        safe_cookie = safe_cookie.replace('"', '\\"')
                        safe_cookie = safe_cookie.replace("$", "\\$")
                        curl_parts.append('-b "' + safe_cookie + '"')
                    else:
                        escaped_cookie = cookie_string.replace("\\", "\\\\").replace("'", "\\'")
                        curl_parts.append("-b $'" + escaped_cookie + "'")
                except Exception as cookie_error:
                    # Cookie处理失败，跳过cookie（不影响主要功能）
                    pass

            if requestInfo.getBodyOffset() < len(content):
                try:
                    body = content[requestInfo.getBodyOffset():]
                    if body and len(body) > 0:
                        # 使用安全的字节解码方法
                        body_str = self._bytesToUtf8String(body)
                        if body_str and body_str.strip():

                            # 安全地检测中文字符
                            has_chinese = False
                            try:
                                has_chinese = any(ord(char) > 127 for char in body_str if
                                                  isinstance(char, unicode) and 0x4e00 <= ord(char) <= 0x9fff)
                            except (TypeError, ValueError):
                                has_chinese = False

                            if has_chinese:
                                safe_body = body_str
                                safe_body = safe_body.replace("\\", "\\\\")
                                safe_body = safe_body.replace('"', '\\"')
                                safe_body = safe_body.replace("$", "\\$")

                                curl_parts.append('--data-binary "' + safe_body + '"')
                            else:
                                escaped_body = body_str.replace("\\", "\\\\").replace("'", "\\'")
                                curl_parts.append("--data-binary $'" + escaped_body + "'")
                except Exception as body_error:
                    # 请求体处理失败，跳过（GET请求等不需要body）
                    pass

            # 安全地处理URL中的中文字符
            try:
                url_has_chinese = False
                try:
                    url_has_chinese = any(ord(char) > 127 for char in url_without_default_port if
                                          isinstance(char, unicode) and 0x4e00 <= ord(char) <= 0x9fff)
                except (TypeError, ValueError):
                    url_has_chinese = False

                if url_has_chinese:
                    safe_url = url_without_default_port
                    safe_url = safe_url.replace("\\", "\\\\")
                    safe_url = safe_url.replace('"', '\\"')
                    safe_url = safe_url.replace("$", "\\$")
                    curl_parts.append('"' + safe_url + '"')
                else:
                    escaped_url = url_without_default_port.replace("\\", "\\\\").replace("'", "\\'")
                    curl_parts.append("$'" + escaped_url + "'")
            except Exception as url_error:
                # URL处理失败，使用基本格式
                try:
                    escaped_url = url_without_default_port.replace("\\", "\\\\").replace("'", "\\'")
                    curl_parts.append("$'" + escaped_url + "'")
                except Exception:
                    # 最后兜底：使用最简单的URL格式
                    curl_parts.append('"' + str(url_without_default_port) + '"')

            # 安全地组合最终的curl命令
            try:
                final_curl = " \\\n    ".join(curl_parts)
                # 确保返回的是Unicode字符串
                return safe_unicode(final_curl)
            except Exception as join_error:
                # curl命令组合失败，返回None
                return None

        except Exception:
            return None

    def _convertToLanguage(self, curl_command, target_language):
        """转换curl命令为指定语言代码"""
        try:
            start_time = time.time()

            api_url = self._extender._config["current_api"]

            # 确保curl命令和语言都是安全的Unicode字符串
            safe_curl_command = safe_unicode(curl_command)
            safe_target_language = safe_unicode(target_language)

            data = {
                "curl": safe_curl_command,
                "language": safe_target_language
            }

            # 改进的JSON序列化方法，更好地处理中文字符
            json_data = None
            try:
                # 方法1：尝试使用ensure_ascii=True，这样中文字符会被转义为\uXXXX格式
                json_data = json.dumps(data, ensure_ascii=True, separators=(',', ':'))
                # 确保结果是unicode字符串
                json_data = safe_unicode(json_data)
            except Exception as json_error1:
                try:
                    # 方法2：手动构造JSON，使用更安全的字符串处理
                    # 先安全地转义curl命令中的特殊字符
                    escaped_curl = safe_curl_command.replace('\\', '\\\\').replace('"', '\\"').replace('\n',
                                                                                                       '\\n').replace(
                        '\r', '\\r').replace('\t', '\\t')
                    escaped_lang = safe_target_language.replace('\\', '\\\\').replace('"', '\\"')

                    # 手动构造JSON字符串
                    json_data = '{"curl":"' + escaped_curl + '","language":"' + escaped_lang + '"}'
                    json_data = safe_unicode(json_data)
                except Exception as json_error2:
                    try:
                        # 方法3：使用codecs模块进行UTF-8编码
                        import codecs
                        temp_data = {
                            "curl": codecs.encode(safe_curl_command, 'unicode_escape').decode('ascii'),
                            "language": safe_target_language
                        }
                        json_data = json.dumps(temp_data, ensure_ascii=True, separators=(',', ':'))
                        json_data = safe_unicode(json_data)
                    except Exception as json_error3:
                        # 方法4：最基本的JSON构造
                        # 将中文字符转换为Unicode转义序列
                        def unicode_escape_chinese(text):
                            result = []
                            for char in text:
                                if isinstance(char, unicode) and ord(char) > 127:
                                    result.append('\\u{:04x}'.format(ord(char)))
                                else:
                                    result.append(char)
                            return ''.join(result)

                        try:
                            escaped_curl_unicode = unicode_escape_chinese(safe_curl_command)
                            escaped_curl_unicode = escaped_curl_unicode.replace('\\', '\\\\').replace('"', '\\"')
                            escaped_lang_unicode = unicode_escape_chinese(safe_target_language)

                            json_data = '{"curl":"' + escaped_curl_unicode + '","language":"' + escaped_lang_unicode + '"}'
                            json_data = safe_unicode(json_data)
                        except Exception:
                            # 最终兜底方案：移除所有非ASCII字符
                            clean_curl = ''.join(char if ord(char) < 128 else '?' for char in safe_curl_command)
                            clean_lang = ''.join(char if ord(char) < 128 else '?' for char in safe_target_language)
                            json_data = '{"curl":"' + clean_curl.replace('"',
                                                                         '\\"') + '","language":"' + clean_lang + '"}'
                            json_data = safe_unicode(json_data)

            # 如果json_data仍然为None，说明所有方法都失败了
            if json_data is None:
                error_msg = u"JSON序列化失败：无法处理请求中的特殊字符"
                return self._generateFallbackCode(curl_command, error_msg, target_language)

            req = urllib2.Request(api_url)
            req.add_header('Content-Type', 'application/json; charset=utf-8')
            req.add_header('Accept', '*/*')
            req.add_header('User-Agent',
                           'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36')
            req.add_header('Accept-Language', 'zh-CN,zh;q=0.9,en;q=0.8')
            req.add_header('Accept-Charset', 'utf-8')

            # 改进的请求体编码处理
            request_body = None
            try:
                # 确保json_data是Unicode字符串，然后安全地编码为UTF-8字节
                if isinstance(json_data, unicode):
                    request_body = json_data.encode('utf-8', 'replace')
                elif isinstance(json_data, str):
                    # 如果是字节字符串，先安全解码再编码
                    try:
                        unicode_json = json_data.decode('utf-8', 'replace')
                        request_body = unicode_json.encode('utf-8', 'replace')
                    except UnicodeDecodeError:
                        # 解码失败，尝试其他编码
                        try:
                            unicode_json = json_data.decode('latin1')
                            request_body = unicode_json.encode('utf-8', 'replace')
                        except:
                            # 最后兜底：直接当作字节处理
                            request_body = json_data
                else:
                    # 其他类型，强制转换
                    unicode_json = safe_unicode(json_data)
                    request_body = unicode_json.encode('utf-8', 'replace')
            except Exception as encode_error:
                # 编码失败时的兜底方案
                try:
                    request_body = safe_encode_utf8(json_data)
                except Exception:
                    # 最终兜底：构造最简单的请求体
                    try:
                        simple_json = '{"curl":"' + str(curl_command).replace('"', '\\"').replace('\n',
                                                                                                  '\\n') + '","language":"' + str(
                            target_language) + '"}'
                        request_body = simple_json.encode('utf-8', 'ignore')
                    except Exception:
                        # 彻底失败
                        error_msg = u"请求体编码失败：" + safe_unicode(str(encode_error))
                        return self._generateFallbackCode(curl_command, error_msg, target_language)

            # 检查request_body是否成功生成
            if request_body is None:
                error_msg = u"请求体生成失败"
                return self._generateFallbackCode(curl_command, error_msg, target_language)

            response = urllib2.urlopen(req, request_body, timeout=10)
            response_data = response.read()

            elapsed_time = time.time() - start_time

            # 更安全的响应解码
            response_text = None
            try:
                # 多种方式尝试解码响应
                if hasattr(response_data, 'decode'):
                    try:
                        response_text = response_data.decode('utf-8', 'replace')
                    except UnicodeDecodeError:
                        # UTF-8解码失败，尝试其他编码
                        try:
                            response_text = response_data.decode('latin1')
                        except UnicodeDecodeError:
                            response_text = safe_unicode(response_data)
                else:
                    response_text = safe_unicode(response_data)
            except Exception:
                response_text = safe_unicode(response_data)

            # 检查response_text是否成功生成
            if response_text is None:
                error_msg = u"响应解码失败"
                return self._generateFallbackCode(curl_command, error_msg, target_language)

            # 更安全的JSON解析
            try:
                # 确保response_text是正确的Unicode字符串
                safe_response_text = safe_unicode(response_text)
                result = json.loads(safe_response_text)
            except (UnicodeDecodeError, ValueError, TypeError) as parse_error:
                # JSON解析失败，返回错误信息
                error_msg = u"API响应解析错误: " + safe_unicode(str(parse_error))
                return self._generateFallbackCode(curl_command, error_msg, target_language)

            if result.get('success'):
                converted_code = result.get('result', 'No ' + target_language + ' code returned')

                safe_converted_code = safe_unicode(converted_code)

                final_code = self._applyLanguageTemplate(safe_converted_code, target_language)

                safe_final_code = self._safeDisplayText(final_code)

                return safe_final_code
            else:
                error_msg = u"API转换失败: " + safe_unicode(str(result))
                return self._generateFallbackCode(curl_command, error_msg, target_language)

        except urllib2.HTTPError as e:
            error_msg = u"HTTP错误 {}: {}".format(e.code, safe_unicode(str(e.reason)))
            return self._generateFallbackCode(curl_command, error_msg, target_language)
        except urllib2.URLError as e:
            error_msg = u"网络错误: " + safe_unicode(str(e.reason))
            return self._generateFallbackCode(curl_command, error_msg, target_language)
        except Exception as e:
            error_msg = u"意外错误: " + safe_unicode(e)
            return self._generateFallbackCode(curl_command, error_msg, target_language)

    def _generateFallbackCode(self, curl_command, error_msg, target_language):
        """当API调用失败时，显示错误信息和原始curl命令"""
        output = "# " + "=" * 60 + "\n"
        output += "# Curl to " + target_language.upper() + " Converter - API ERROR\n"
        output += "# " + error_msg + "\n"
        output += "# " + "=" * 60 + "\n\n"
        output += "# Original curl command:\n"

        curl_lines = curl_command.split('\n')
        for line in curl_lines:
            output += "# " + line.strip() + "\n"

        output += u"\n# 请检查:\n"
        output += u"# 1. API地址是否可访问\n"
        output += u"# 2. 网络连接是否正常\n"
        output += u"# 3. Curl命令格式是否正确\n"
        output += u"# 4. 尝试在配置标签页中测试API连接\n"

        return output

    def _cleanGeneratedCode(self, code, target_language):
        """清理生成的代码，去除冗余内容"""
        if not code:
            return code

        lines = code.split('\n')
        cleaned_lines = []
        seen_imports = set()
        in_comment_block = False

        for line in lines:
            stripped_line = line.strip()

            if stripped_line.startswith('# Note:') and 'json_data will not be serialized' in stripped_line:
                in_comment_block = True
                continue
            elif in_comment_block and stripped_line.startswith('#'):
                continue
            elif in_comment_block and not stripped_line.startswith('#'):
                in_comment_block = False

            if target_language == "python":
                if stripped_line.startswith('import ') or stripped_line.startswith('from '):
                    if stripped_line in seen_imports:
                        continue
                    seen_imports.add(stripped_line)

                if stripped_line.startswith('#data =') or stripped_line.startswith('#response = requests.'):
                    continue

                if stripped_line == '#':
                    continue

            cleaned_lines.append(line)

        result_lines = []
        prev_empty = False

        for line in cleaned_lines:
            is_empty = line.strip() == ''
            if is_empty and prev_empty:
                continue
            result_lines.append(line)
            prev_empty = is_empty

        return '\n'.join(result_lines)

    def _applyLanguageTemplate(self, code, target_language):
        """为指定语言应用代码模板（动态生成时间戳）"""
        if not self._extender._config.get("enable_templates", True):
            return self._cleanGeneratedCode(code, target_language)

        cleaned_code = self._cleanGeneratedCode(code, target_language)

        header = ""
        footer = ""

        if target_language == "python":
            header = self._extender._getPythonTemplate()
            footer = self._extender._getPythonFooter()

            code_lines = cleaned_code.split('\n')
            imports = []
            other_lines = []

            for line in code_lines:
                stripped = line.strip()
                if stripped.startswith('import ') or stripped.startswith('from '):
                    imports.append(line)
                else:
                    other_lines.append(line)

            if imports:
                imports_section = '\n'.join(imports)

                other_code = '\n'.join(other_lines).strip()
                final_code = header + imports_section + '\n\n' + other_code + footer
            else:
                final_code = header + cleaned_code + footer

            final_code = self._postProcessPythonCode(final_code)

        elif target_language == "javascript" or target_language == "node":
            header = self._extender._getJavaScriptTemplate()
            footer = self._extender._getJavaScriptFooter()
            final_code = header + cleaned_code + footer
        elif target_language == "java":
            header = self._extender._getJavaTemplate()
            footer = self._extender._getJavaFooter()
            final_code = header + cleaned_code + footer
        elif target_language == "go":
            header = self._extender._getGoTemplate()
            footer = self._extender._getGoFooter()
            final_code = header + cleaned_code + footer
        elif target_language == "php":
            header = self._extender._getPhpTemplate()
            footer = self._extender._getPhpFooter()
            final_code = header + cleaned_code + footer
        else:
            final_code = cleaned_code

        return final_code

    def _postProcessPythonCode(self, code):
        """后处理Python代码，智能处理verify=False参数"""
        if not code:
            return code

        try:
            import re

            pattern = r'(requests\.\w+\([^)]*?[\'"])(https?)://([^\'"\)]*?)([\'"][^)]*?)(,\s*verify=False)([^)]*?\))'

            def replace_verify(match):
                prefix = match.group(1)
                protocol = match.group(2)
                url_part = match.group(3)
                url_suffix = match.group(4)
                verify_param = match.group(5)
                suffix = match.group(6)

                if protocol.lower() == 'http':
                    result = prefix + protocol + "://" + url_part + url_suffix + suffix
                    result = re.sub(r',\s*,', ',', result)
                    result = re.sub(r',\s*\)', ')', result)
                    return result
                else:
                    return match.group(0)

            processed_code = re.sub(pattern, replace_verify, code)

            return processed_code

        except Exception:
            return code