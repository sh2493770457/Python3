# -*- encoding: utf-8 -*-
# TODO:@ModuleName: burp_regex_extractor
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/06/23 10:25

from burp import IBurpExtender, IMessageEditorTabFactory, IMessageEditorTab
from javax.swing import JScrollPane, JTable, JPanel, JLabel, JTextField, JButton, JPopupMenu, JMenuItem, JComboBox
from javax.swing.table import DefaultTableModel
from java.awt import BorderLayout
from java.awt.datatransfer import StringSelection
from java.awt import Toolkit
from java.awt.event import MouseAdapter
import re
import json

class BurpExtender(IBurpExtender, IMessageEditorTabFactory):
    """
    Burp 插件入口，实现 IBurpExtender 和 IMessageEditorTabFactory
    :param callbacks: Burp 提供的回调函数集合
    """
    def registerExtenderCallbacks(self, callbacks):
        self._callbacks = callbacks
        self._helpers = callbacks.getHelpers()
        callbacks.setExtensionName(u"MatchData")
        callbacks.registerMessageEditorTabFactory(self)

    def createNewInstance(self, controller, editable):
        return _RegexExtractorTab(self._callbacks, self._helpers, controller)


class _RegexExtractorTab(IMessageEditorTab):
    """
    Burp 插件核心 UI 与功能实现，提供三种数据提取方式：正则表达式、JSON路径、Python代码
    :param callbacks: Burp 回调函数
    :param helpers: Burp 辅助对象
    :param controller: 当前 HTTP 消息控制器
    """
    def __init__(self, callbacks, helpers, controller):
        self._helpers = helpers
        self._controller = controller
        self._current_body_str = u""
        self._setup_ui()

    def _setup_ui(self):
        """构建用户界面"""
        self._mode_combo = JComboBox([u"正则表达式", u"JSON路径", u"Python表达式"])
        self._pattern_field = JTextField(30)
        self._python_field = JTextField(30)
        self._python_field.setText(u"[i['id'] for i in data]")

        apply_button = JButton(u"应用", actionPerformed=self._apply_pattern)
        copy_button = JButton(u"复制已选", actionPerformed=self._copy_selection)
        self._mode_combo.addActionListener(self._on_mode_changed)

        top_panel = JPanel(BorderLayout())
        first_line = JPanel()
        first_line.add(JLabel(u"模式:"))
        first_line.add(self._mode_combo)
        first_line.add(JLabel(u"表达式:"))
        first_line.add(self._pattern_field)
        first_line.add(apply_button)
        first_line.add(copy_button)

        self._second_line = JPanel()
        self._second_line.add(JLabel(u"Python代码:"))
        self._second_line.add(self._python_field)
        self._second_line.setVisible(False)

        top_panel.add(first_line, BorderLayout.NORTH)
        top_panel.add(self._second_line, BorderLayout.SOUTH)

        # 设置表格
        self._table_model = DefaultTableModel([u"#", u"匹配数据"], 0)
        self._table = JTable(self._table_model)
        self._table.setRowSelectionAllowed(True)
        self._table.setColumnSelectionAllowed(True)
        self._table.setCellSelectionEnabled(True)

        self._setup_context_menu()

        # 主面板
        self._main_panel = JPanel(BorderLayout())
        self._main_panel.add(top_panel, BorderLayout.NORTH)
        self._main_panel.add(JScrollPane(self._table), BorderLayout.CENTER)

    def _setup_context_menu(self):
        """设置右键复制菜单"""
        popup_menu = JPopupMenu()
        popup_menu.add(JMenuItem(u"复制选中内容", actionPerformed=self._copy_selection))

        class MouseListener(MouseAdapter):
            def mousePressed(self, event):
                if event.isPopupTrigger():
                    popup_menu.show(event.getComponent(), event.getX(), event.getY())
            def mouseReleased(self, event):
                if event.isPopupTrigger():
                    popup_menu.show(event.getComponent(), event.getX(), event.getY())

        self._table.addMouseListener(MouseListener())

    # IMessageEditorTab 接口实现
    def getTabCaption(self):
        return u"MatchData"

    def getUiComponent(self):
        return self._main_panel

    def isEnabled(self, content, isRequest):
        return content is not None and not isRequest

    def setMessage(self, content, isRequest):
        """
        处理响应消息体（Body），并尝试使用多种编码方式解析为字符串
        """
        self._current_body_str = u""
        if content is None or isRequest:
            return

        try:
            response_info = self._helpers.analyzeResponse(content)
            body_bytes = content[response_info.getBodyOffset():]
            try:
                self._current_body_str = body_bytes.decode('utf-8')
            except:
                try:
                    self._current_body_str = body_bytes.decode('gb18030', 'ignore')
                except:
                    self._current_body_str = self._helpers.bytesToString(body_bytes)
        except:
            pass

        self._update_table()

    def getMessage(self):
        return None

    def isModified(self):
        return False

    def getSelectedData(self):
        return None

    def _apply_pattern(self, _event=None):
        """根据当前模式应用提取规则"""
        self._update_table()

    def _update_table(self):
        self._table_model.setRowCount(0)
        if not self._current_body_str:
            return

        pattern_text = self._pattern_field.getText()
        if not pattern_text:
            return

        mode = self._mode_combo.getSelectedItem()
        try:
            if mode == u"正则表达式":
                self._apply_regex(pattern_text)
            elif mode == u"JSON路径":
                self._apply_json_path(pattern_text)
            else:
                self._apply_python_expression(pattern_text, self._python_field.getText())
        except Exception as e:
            self._table_model.addRow([u"错误", str(e)])

    def _apply_regex(self, pattern_text):
        """执行正则提取"""
        matches = re.compile(pattern_text, re.UNICODE).findall(self._current_body_str)
        for idx, match in enumerate(matches, 1):
            self._table_model.addRow([idx, match])

    def _apply_json_path(self, path_text):
        """提取 JSON 路径中的字段值"""
        json_data = json.loads(self._current_body_str)
        results = self._extract_json_path(json_data, path_text)
        for idx, result in enumerate(results, 1):
            self._table_model.addRow([idx, self._format_result(result)])

    def _extract_json_path(self, data, path_text):
        """支持多层路径提取（类似 ['key'][0]['subkey']）"""
        results = []
        data_list = [data] if not isinstance(data, list) else data
        for item in data_list:
            try:
                result = self._eval_json_path(item, path_text)
                if result is not None:
                    results.append(result)
            except:
                continue
        return results

    def _eval_json_path(self, data, path_text):
        try:
            if path_text.startswith('['):
                current = data
                path = path_text
                while path:
                    if path.startswith("['") or path.startswith('[\"'):
                        end_quote = path.find("']", 2) if path.startswith("['") else path.find('"]', 2)
                        if end_quote == -1:
                            break
                        current = current[path[2:end_quote]]
                        path = path[end_quote + 2:]
                    elif path.startswith('['):
                        end_bracket = path.find(']')
                        if end_bracket == -1:
                            break
                        current = current[int(path[1:end_bracket])]
                        path = path[end_bracket + 1:]
                    else:
                        break
                return current
            else:
                current = data
                for key in path_text.split('.'):
                    current = current[int(key)] if key.isdigit() else current[key]
                return current
        except:
            return None

    def _format_result(self, result):
        """将结果转为可打印字符串"""
        if result is None:
            return u"null"
        elif isinstance(result, bool):
            return u"true" if result else u"false"
        elif isinstance(result, (int, float)):
            return unicode(result)
        elif isinstance(result, (str, unicode)):
            return result
        elif isinstance(result, (list, dict)):
            return json.dumps(result, ensure_ascii=False, separators=(',', ':'))
        else:
            return unicode(result)

    def _on_mode_changed(self, event):
        self._second_line.setVisible(self._mode_combo.getSelectedItem() == u"Python表达式")
        self._main_panel.revalidate()
        self._main_panel.repaint()

    def _apply_python_expression(self, pattern_text, python_code):
        """用户自定义 Python 表达式提取复杂结构数据"""
        if pattern_text.strip():
            try:
                json_data = json.loads(self._current_body_str)
                results = self._extract_json_path(json_data, pattern_text)
                data = results[0] if len(results) == 1 else results
            except:
                try:
                    data = re.compile(pattern_text, re.UNICODE).findall(self._current_body_str)
                except:
                    data = []
        else:
            try:
                data = json.loads(self._current_body_str)
            except:
                data = self._current_body_str

        exec_env = {
            'json': json, 'data': data, 're': re,
            '__builtins__': {
                'len': len, 'str': str, 'int': int, 'float': float, 'bool': bool,
                'list': list, 'dict': dict, 'enumerate': enumerate, 'range': range,
                'isinstance': isinstance, 'hasattr': hasattr, 'getattr': getattr,
                'max': max, 'min': min, 'sum': sum, 'sorted': sorted, 'set': set, 'tuple': tuple,
            }
        }

        try:
            result = eval(python_code, exec_env)
        except SyntaxError:
            exec_locals = {}
            exec(python_code, exec_env, exec_locals)
            result = exec_locals.get('result', exec_locals.get('data', u"执行完成，无返回值"))

        if isinstance(result, list):
            for idx, item in enumerate(result, 1):
                self._table_model.addRow([idx, self._format_result(item)])
        else:
            self._table_model.addRow([1, self._format_result(result)])

    def _copy_selection(self, _event=None):
        """将表格中选中的内容复制到剪贴板"""
        rows = self._table.getSelectedRows()
        cols = self._table.getSelectedColumns()

        if not rows or not cols:
            return

        lines = [u"\t".join(unicode(self._table.getValueAt(r, c)) for c in cols) for r in rows]
        try:
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
            clipboard.setContents(StringSelection(u"\n".join(lines)), None)
        except:
            pass
