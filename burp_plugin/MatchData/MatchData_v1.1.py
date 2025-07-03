# -*- encoding: utf-8 -*-
# TODO:@ModuleName: burp_regex_extractor
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/06/23 10:25

from burp import IBurpExtender, IMessageEditorTabFactory, IMessageEditorTab
from javax.swing import JScrollPane, JTable, JPanel, JLabel, JTextField, JButton, JPopupMenu, JMenuItem, JComboBox, \
    JCheckBox, JCheckBoxMenuItem, JDialog, BoxLayout, JFileChooser
from javax.swing.table import DefaultTableModel
from javax.swing.filechooser import FileNameExtensionFilter
from java.awt import BorderLayout, FlowLayout
from java.awt.datatransfer import StringSelection
from java.awt import Toolkit
from java.awt.event import MouseAdapter
from java.awt.event import ItemEvent
from java.io import File
import re
import json

# 兼容 Python3 中已移除的 unicode 类型
try:
    unicode
except NameError:  # pragma: no cover
    unicode = str  # 在 Python3 环境下将 unicode 指向 str，保持后续代码兼容


class BurpExtender(IBurpExtender, IMessageEditorTabFactory):
    """
    Burp 插件入口，实现 IBurpExtender 和 IMessageEditorTabFactory
    :param callbacks: Burp 提供的回调函数集合
    """

    def registerExtenderCallbacks(self, callbacks):
        self._callbacks = callbacks
        self._helpers = callbacks.getHelpers()
        callbacks.setExtensionName(u"MatchData_v1.1")
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
        self._python_expressions = []  # 存储多个Python表达式输入框
        self._regex_patterns = []  # 存储多个正则表达式输入框
        self._css_patterns = []  # 存储多个 CSS 选择器输入框
        self._all_headers = []  # 所有列名
        self._visible_headers = []  # 当前显示列名
        self._current_rows = []  # 最近一次渲染的全部行数据
        self._setup_ui()

    def _setup_ui(self):
        """构建用户界面"""
        self._mode_combo = JComboBox([u"自动提取", u"正则表达式", u"JSON路径", u"Python表达式", u"CSS选择器", u"混合模式"])
        self._pattern_field = JTextField(30)
        # 预置一个 JSON 路径示例，进入 JSON 路径模式时如果为空则显示
        self._pattern_field.setText(u"['items'][0]['id']")
        self._pattern_label = JLabel(u"表达式:")

        apply_button = JButton(u"应用", actionPerformed=self._apply_pattern)
        copy_button = JButton(u"复制已选", actionPerformed=self._copy_selection)
        export_button = JButton(u"导出CSV", actionPerformed=self._export_to_csv)
        self._fields_button = JButton(u"选择字段", actionPerformed=self._show_field_selector)
        self._mode_combo.addActionListener(self._on_mode_changed)

        top_panel = JPanel(BorderLayout())
        first_line = JPanel(FlowLayout(FlowLayout.LEFT))
        first_line.add(JLabel(u"模式:"))
        first_line.add(self._mode_combo)
        first_line.add(self._pattern_label)
        first_line.add(self._pattern_field)
        first_line.add(apply_button)
        first_line.add(copy_button)
        first_line.add(export_button)
        first_line.add(self._fields_button)

        # ---------------- Python表达式区域 ----------------
        self._python_panel = JPanel()
        self._python_panel.setLayout(BoxLayout(self._python_panel, BoxLayout.Y_AXIS))

        # Python表达式控制按钮
        python_control_panel = JPanel(FlowLayout(FlowLayout.LEFT))
        python_control_panel.add(JLabel(u"Python表达式:"))
        add_expr_button = JButton(u"添加表达式", actionPerformed=self._add_python_expression)
        remove_expr_button = JButton(u"删除表达式", actionPerformed=self._remove_python_expression)
        python_control_panel.add(add_expr_button)
        python_control_panel.add(remove_expr_button)

        self._python_panel.add(python_control_panel)

        # 默认添加一个Python表达式输入框
        self._add_default_python_expression()

        self._python_panel.setVisible(False)

        # ---------------- 正则表达式区域 ----------------
        self._regex_panel = JPanel()
        self._regex_panel.setLayout(BoxLayout(self._regex_panel, BoxLayout.Y_AXIS))

        regex_control_panel = JPanel(FlowLayout(FlowLayout.LEFT))
        regex_control_panel.add(JLabel(u"正则表达式:"))
        add_regex_button = JButton(u"添加表达式", actionPerformed=self._add_regex_pattern)
        remove_regex_button = JButton(u"删除表达式", actionPerformed=self._remove_regex_pattern)
        regex_control_panel.add(add_regex_button)
        regex_control_panel.add(remove_regex_button)

        self._regex_panel.add(regex_control_panel)

        # 默认添加一个正则表达式输入框
        self._add_default_regex_pattern()

        self._regex_panel.setVisible(False)

        # ---------------- CSS 选择器区域 ----------------
        self._css_panel = JPanel()
        self._css_panel.setLayout(BoxLayout(self._css_panel, BoxLayout.Y_AXIS))

        css_control_panel = JPanel(FlowLayout(FlowLayout.LEFT))
        css_control_panel.add(JLabel(u"CSS选择器:"))
        add_css_button = JButton(u"添加表达式", actionPerformed=self._add_css_pattern)
        remove_css_button = JButton(u"删除表达式", actionPerformed=self._remove_css_pattern)
        css_control_panel.add(add_css_button)
        css_control_panel.add(remove_css_button)

        self._css_panel.add(css_control_panel)

        # 默认添加一个 CSS 选择器输入框
        self._add_default_css_pattern()

        self._css_panel.setVisible(False)

        # ---------------- JSON 路径面板（仅混合模式内部使用） ----------------
        self._json_panel = JPanel()
        self._json_panel.setLayout(BoxLayout(self._json_panel, BoxLayout.Y_AXIS))

        json_inner = JPanel(FlowLayout(FlowLayout.LEFT))
        self._json_field = JTextField(40)
        self._json_field.setText(u"['items'][0]['id']")
        json_inner.add(JLabel(u"JSON路径:"))
        json_inner.add(self._json_field)

        self._json_panel.add(json_inner)
        self._json_panel.setVisible(False)

        # ---------------- 组合表达式面板 ----------------
        self._expression_panel = JPanel()
        self._expression_panel.setLayout(BoxLayout(self._expression_panel, BoxLayout.Y_AXIS))
        self._expression_panel.add(self._json_panel)
        self._expression_panel.add(self._python_panel)
        self._expression_panel.add(self._regex_panel)
        self._expression_panel.add(self._css_panel)

        # ---------- 混合模式切换面板 ----------
        self._mix_toggle_panel = JPanel(FlowLayout(FlowLayout.LEFT))
        self._mix_toggle_panel.add(JLabel(u"启用类型:"))
        
        self._mix_chk_json = JCheckBox(u"JSON路径", False)
        self._mix_chk_python = JCheckBox(u"Python", False)
        self._mix_chk_regex = JCheckBox(u"正则", False)
        self._mix_chk_css = JCheckBox(u"CSS", False)


        # 监听勾选变化来显示/隐藏对应面板/输入框
        def _toggle_listener(event):
            self._update_mix_panels()

        for cb in [self._mix_chk_json, self._mix_chk_python, self._mix_chk_regex, self._mix_chk_css]:
            cb.addItemListener(_toggle_listener)
            self._mix_toggle_panel.add(cb)

        self._mix_toggle_panel.setVisible(False)

        top_panel.add(first_line, BorderLayout.NORTH)
        top_panel.add(self._expression_panel, BorderLayout.SOUTH)
        top_panel.add(self._mix_toggle_panel, BorderLayout.CENTER)

        # 设置表格
        self._table_model = DefaultTableModel([u"行号"], 0)
        self._table = JTable(self._table_model)
        self._table.setRowSelectionAllowed(True)
        self._table.setColumnSelectionAllowed(True)
        self._table.setCellSelectionEnabled(True)

        self._setup_context_menu()

        # 主面板
        self._main_panel = JPanel(BorderLayout())
        self._main_panel.add(top_panel, BorderLayout.NORTH)
        self._main_panel.add(JScrollPane(self._table), BorderLayout.CENTER)

        # 初始化控件可见性/可编辑性
        self._on_mode_changed(None)

    def _setup_context_menu(self):
        """设置右键菜单"""
        popup_menu = JPopupMenu()
        popup_menu.add(JMenuItem(u"复制选中内容", actionPerformed=self._copy_selection))
        popup_menu.add(JMenuItem(u"导出为CSV", actionPerformed=self._export_to_csv))

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
        # 对于响应包始终显示Tab，即使内容为空也显示
        # 这样可以确保导出CSV按钮始终可见
        return not isRequest

    def setMessage(self, content, isRequest):
        """
        处理响应消息体（Body），并尝试使用多种编码方式解析为字符串
        修复中文乱码问题
        """
        self._current_body_str = u""
        if isRequest:
            return

        # 即使 content 为 None，也要确保UI能正常工作
        if content is None:
            self._current_body_str = u""
            self._update_table()
            return

        try:
            response_info = self._helpers.analyzeResponse(content)
            body_bytes = content[response_info.getBodyOffset():]

            # 优化编码检测顺序，增加更多编码方式
            encodings = ['utf-8', 'gbk', 'gb2312', 'gb18030', 'big5', 'utf-16', 'iso-8859-1']

            for encoding in encodings:
                try:
                    self._current_body_str = body_bytes.tostring().decode(encoding)
                    break
                except (UnicodeDecodeError, AttributeError):
                    continue

            # 如果所有编码都失败，使用 Burp 的方法作为后备
            if not self._current_body_str:
                self._current_body_str = self._helpers.bytesToString(body_bytes)

        except Exception as e:
            # 最后的后备方案
            try:
                self._current_body_str = str(content, 'utf-8', errors='ignore')
            except:
                self._current_body_str = ""

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
            # 即使没有数据，也保持基本的表格结构
            self._table_model.setColumnIdentifiers([u"行号", u"数据"])
            self._table_model.addRow([u"无数据", u"请在左侧选择响应包或输入表达式"])
            return

        pattern_text = self._pattern_field.getText()
        mode = self._mode_combo.getSelectedItem()

        # ---- 输入校验 ----
        if mode == u"自动提取":
            pass  # 不需要表达式
        elif mode == u"正则表达式":
            # 至少有一个正则输入框非空才继续，具体提示交由 _apply_regex 处理
            if not any(f.getText().strip() for f in self._regex_patterns):
                self._apply_regex()  # 让其显示"未输入"提示
                return
        elif mode == u"CSS选择器":
            if not any(f.getText().strip() for f in self._css_patterns):
                self._apply_css()  # 显示未输入提示
                return
        elif mode == u"混合模式":
            # 至少有任一表达式存在
            has_any = False
            if self._mix_chk_regex.isSelected():
                has_any |= any(f.getText().strip() for f in self._regex_patterns)
            if self._mix_chk_css.isSelected():
                has_any |= any(f.getText().strip() for f in self._css_patterns)
            if self._mix_chk_python.isSelected():
                has_any |= any(f.getText().strip() for f in self._python_expressions)
            if self._mix_chk_json.isSelected():
                has_any |= bool(pattern_text.strip())
            if not has_any:
                self._apply_mixed()  # 显示未输入提示
                return
        else:
            # JSON 路径 / Python 表达式 需要输入内容
            if not pattern_text.strip():
                return

        try:
            if mode == u"自动提取":
                self._auto_extract_data()
            elif mode == u"正则表达式":
                self._apply_regex()
            elif mode == u"JSON路径":
                self._apply_json_path(pattern_text)
            elif mode == u"CSS选择器":
                self._apply_css()
            elif mode == u"混合模式":
                self._apply_mixed()
            else:
                self._apply_python_expression(pattern_text)
        except Exception as e:
            self._table_model.addRow([u"错误", str(e)])

    def _apply_regex(self):
        """执行多条正则提取，并将结果按列显示"""
        # 收集所有非空正则表达式
        patterns = [f.getText().strip() for f in self._regex_patterns if f.getText().strip()]

        if not patterns:
            # 无有效规则
            self._table_model.setColumnIdentifiers([u"提示", u"信息"])
            self._table_model.setRowCount(0)
            self._table_model.addRow([u"未输入", u"请至少输入一条正则表达式"])
            return

        # 为每条正则匹配结果
        column_results = []
        column_headers = [u"行号"]

        for idx, pat in enumerate(patterns, 1):
            try:
                # DOTALL 使 . 可以匹配换行，兼容格式化/缩进后的 JSON
                matches = re.compile(pat, re.UNICODE | re.DOTALL).findall(self._current_body_str)
            except Exception as e:
                matches = [u"错误: " + unicode(str(e), errors='ignore')]

            column_headers.append(u"匹配{}".format(idx))
            column_results.append([self._format_result(m) for m in matches])

        # 重新设置表格列
        self._table_model.setColumnIdentifiers(column_headers)
        self._table_model.setRowCount(0)

        # 行数取各列最大长度
        max_rows = max(len(col) for col in column_results)

        if max_rows == 0:
            # 所有表达式均无匹配结果
            self._table_model.setRowCount(0)
            self._table_model.setColumnIdentifiers([u"提示", u"信息"])
            self._table_model.addRow([u"未匹配", u"所有正则表达式均未匹配到内容，请检查表达式或开启 DOTALL 选项"])
            return

        for row_idx in range(max_rows):
            row_data = [row_idx + 1]
            for col_data in column_results:
                row_data.append(col_data[row_idx] if row_idx < len(col_data) else u"")
            self._table_model.addRow(row_data)

    def _apply_json_path(self, path_text):
        """提取 JSON 路径中的字段值"""
        json_data = json.loads(self._current_body_str)
        results = self._extract_json_path(json_data, path_text)

        # 重新设置表格列
        self._table_model.setColumnIdentifiers([u"行号", u"提取结果"])
        self._table_model.setRowCount(0)

        for idx, result in enumerate(results, 1):
            self._table_model.addRow([idx, self._format_result(result)])

    def _flatten_json(self, obj, parent_key=u""):
        """递归展开嵌套 JSON，返回扁平化 dict，键以点或索引表示路径"""
        items = {}

        if isinstance(obj, dict):
            for k, v in obj.items():
                # 保证键为 unicode
                k_unicode = k if isinstance(k, unicode) else unicode(k)
                new_key = u"{}.{}".format(parent_key, k_unicode) if parent_key else k_unicode

                if isinstance(v, (dict, list)):
                    items.update(self._flatten_json(v, new_key))
                else:
                    items[new_key] = v

        elif isinstance(obj, list):
            for idx, v in enumerate(obj):
                new_key = u"{}[{}]".format(parent_key, idx) if parent_key else u"[{}]".format(idx)

                if isinstance(v, (dict, list)):
                    items.update(self._flatten_json(v, new_key))
                else:
                    items[new_key] = v

        else:
            # 基本类型
            items[parent_key] = obj

        return items

    def _auto_extract_data(self):
        """自动识别并提取结构化数据（目前支持 JSON，后续可扩展）"""
        try:
            json_data = json.loads(self._current_body_str)

            # 如果是列表
            if isinstance(json_data, list):
                if not json_data:
                    self._table_model.setColumnIdentifiers([u"提示", u"信息"])
                    self._table_model.addRow([u"空列表", u"JSON 列表为空"])
                    return

                # 如果列表元素是字典，则扁平化后提取所有键
                if all(isinstance(item, dict) for item in json_data):
                    # 扁平化每条记录
                    flat_items = [self._flatten_json(item) for item in json_data]

                    # 计算所有键的并集以生成列
                    keys_union = set()
                    for flat in flat_items:
                        keys_union.update(flat.keys())

                    headers = [u"行号"] + sorted(list(keys_union))
                    self._table_model.setColumnIdentifiers(headers)
                    self._table_model.setRowCount(0)

                    rows = []
                    for idx, flat in enumerate(flat_items, 1):
                        row = [idx]
                        for key in headers[1:]:
                            row.append(self._format_result(flat.get(key)))
                        rows.append(row)
                    self._display_table(headers, rows)
                    return

                else:
                    # 列表元素非字典，则逐行展示其值
                    self._table_model.setColumnIdentifiers([u"行号", u"值"])
                    self._table_model.setRowCount(0)
                    for idx, item in enumerate(json_data, 1):
                        self._table_model.addRow([idx, self._format_result(item)])
                    return

            # 如果是字典
            elif isinstance(json_data, dict):
                # 若 dict 内部包含单个列表字段（如 items），且列表元素为 dict，则把列表当作记录行
                list_like_keys = [k for k, v in json_data.items() if isinstance(v, list) and v and isinstance(v[0], dict)]

                if len(list_like_keys) == 1:
                    primary_key = list_like_keys[0]
                    record_list = json_data[primary_key]

                    # 扁平化每条记录
                    flat_items = [self._flatten_json(item) for item in record_list]

                    # 计算所有键的并集
                    keys_union = set()
                    for flat in flat_items:
                        keys_union.update(flat.keys())

                    headers = [u"行号"] + sorted(list(keys_union))
                    self._table_model.setColumnIdentifiers(headers)
                    self._table_model.setRowCount(0)

                    rows = []
                    for idx, flat in enumerate(flat_items, 1):
                        row = [idx]
                        for key in headers[1:]:
                            row.append(self._format_result(flat.get(key)))
                        rows.append(row)
                    self._display_table(headers, rows)

                    # 可选: 追加顶层其他字段信息 (page/size/total等)
                    # 用户如需查看，可切换到 JSON路径 或 Python 表达式 模式自行提取
                    return

                # 默认: 扁平化当前 dict
                flat = self._flatten_json(json_data)
                headers = [u"行号"] + sorted(list(flat.keys()))
                rows = [[1] + [self._format_result(flat.get(key)) for key in headers[1:]]]
                self._display_table(headers, rows)
                return

        except Exception:
            # JSON 解析失败，尝试简单的键值对提取
            import re as _re
            kv_matches = _re.findall(r'"([^"]+)"\s*:\s*"?([^",\n\r]+)"?', self._current_body_str)

            if kv_matches:
                self._table_model.setColumnIdentifiers([u"行号", u"键", u"值"])
                self._table_model.setRowCount(0)
                for idx, (k, v) in enumerate(kv_matches, 1):
                    self._table_model.addRow([idx, k, v])
            else:
                # 未识别到结构化数据
                self._table_model.setColumnIdentifiers([u"提示", u"信息"])
                self._table_model.setRowCount(0)
                self._table_model.addRow([u"未识别", u"未发现可自动提取的结构化数据"])

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
        """将结果转为可打印字符串，修复中文显示问题"""
        # 辅助函数：检测并解码形如 "\u5b89\u5fbd" 的转义序列
        def _decode_unicode_escape(s):
            try:
                if isinstance(s, unicode) and re.search(r'\\u[0-9a-fA-F]{4}', s):
                    try:
                        # 先编码再以 unicode_escape 解码，可处理双反斜杠等情况
                        return s.encode('utf-8').decode('unicode_escape')
                    except Exception:
                        # 退而求其次直接 unicode_escape 解码
                        return s.decode('unicode_escape') if hasattr(s, 'decode') else s
            except Exception:
                pass
            return s

        if result is None:
            return u"null"
        elif isinstance(result, bool):
            return u"true" if result else u"false"
        elif isinstance(result, (int, float)):
            return unicode(result)
        elif isinstance(result, unicode):
            return _decode_unicode_escape(result)
        elif isinstance(result, str):
            try:
                decoded = result.decode('utf-8') if hasattr(result, 'decode') else result
            except Exception:
                decoded = unicode(result, errors='ignore')
            return _decode_unicode_escape(decoded)
        elif isinstance(result, (list, dict)):
            # 对序列或映射整体 JSON 序列化，保留中文
            return json.dumps(result, ensure_ascii=False, separators=(',', ':'))
        else:
            try:
                return unicode(result)
            except Exception:
                return unicode(str(result), errors='ignore')

    def _add_default_python_expression(self):
        """添加默认的Python表达式输入框"""
        expr_panel = JPanel(FlowLayout(FlowLayout.LEFT))
        expr_field = JTextField(40)
        expr_field.setText(u"[i['name'] for i in data['items']]")

        expr_panel.add(JLabel(u"表达式:"))
        expr_panel.add(expr_field)

        self._python_expressions.append(expr_field)
        self._python_panel.add(expr_panel)

    def _add_python_expression(self, event=None):
        """添加新的Python表达式输入框"""
        expr_panel = JPanel(FlowLayout(FlowLayout.LEFT))
        expr_field = JTextField(40)

        # 仅首个表达式显示内置示例，其后全部继承上一条
        if self._python_expressions:
            prev_text = self._python_expressions[-1].getText()
            expr_field.setText(prev_text)
        else:
            expr_field.setText(u"[i['name'] for i in a]")

        expr_panel.add(JLabel(u"表达式:"))
        expr_panel.add(expr_field)

        self._python_expressions.append(expr_field)
        self._python_panel.add(expr_panel)

        self._python_panel.revalidate()
        self._python_panel.repaint()

    def _remove_python_expression(self, event=None):
        """删除最后一个Python表达式输入框"""
        if len(self._python_expressions) > 1:
            # 移除最后一个表达式
            self._python_expressions.pop()

            # 移除最后一个面板（除了控制按钮面板）
            component_count = self._python_panel.getComponentCount()
            if component_count > 1:
                self._python_panel.remove(component_count - 1)

            self._python_panel.revalidate()
            self._python_panel.repaint()

    def _on_mode_changed(self, event):
        mode = self._mode_combo.getSelectedItem()
        self._python_panel.setVisible(mode in [u"Python表达式", u"混合模式"])
        self._regex_panel.setVisible(mode in [u"正则表达式", u"混合模式"])
        self._css_panel.setVisible(mode in [u"CSS选择器", u"混合模式"])
        self._json_panel.setVisible(mode == u"混合模式")

        # 混合模式动态显示
        if mode == u"混合模式":
            self._mix_toggle_panel.setVisible(True)
            self._update_mix_panels()
        else:
            self._mix_toggle_panel.setVisible(False)
            self._python_panel.setVisible(mode == u"Python表达式")
            self._regex_panel.setVisible(mode == u"正则表达式")
            self._css_panel.setVisible(mode == u"CSS选择器")
            self._json_panel.setVisible(False)

        # 表达式输入框显隐 & 启用策略
        if mode in [u"JSON路径", u"Python表达式"]:
            self._pattern_field.setEnabled(True)
            self._pattern_label.setVisible(True)
            self._pattern_field.setVisible(True)
        else:
            self._pattern_field.setEnabled(False)
            self._pattern_label.setVisible(False)
            self._pattern_field.setVisible(False)

        # 仅自动提取模式显示"选择字段"按钮
        if hasattr(self, '_fields_button'):
            self._fields_button.setVisible(mode == u"自动提取")

        # 当切换到 JSON 路径模式且输入框为空时，填充示例
        if mode == u"JSON路径" and not self._pattern_field.getText().strip():
            self._pattern_field.setText(u"['items'][0]['id']")

        self._main_panel.revalidate()
        self._main_panel.repaint()

    def _apply_python_expression(self, pattern_text):
        """用户自定义 Python 表达式提取复杂结构数据 - 按列显示，支持赋值"""
        # 首先解析赋值表达式
        assignment_vars = {}

        if pattern_text.strip():
            # 检查是否有赋值语句
            if '=' in pattern_text and not pattern_text.strip().startswith('['):
                # 解析赋值语句，如 a = ['items']
                try:
                    var_name, path_expr = pattern_text.split('=', 1)
                    var_name = var_name.strip()
                    path_expr = path_expr.strip()

                    json_data = json.loads(self._current_body_str)

                    # 简化：直接使用简单的路径解析
                    if path_expr == "['items']":
                        if 'items' in json_data:
                            assignment_vars[var_name] = json_data['items']
                        else:
                            self._table_model.setColumnIdentifiers([u"错误信息"])
                            self._table_model.setRowCount(0)
                            self._table_model.addRow([u"JSON中没有找到items字段"])
                            return
                    else:
                        # 使用原来的复杂路径解析
                        results = self._extract_json_path(json_data, path_expr)
                        extracted_value = results[0] if len(results) == 1 else results
                        assignment_vars[var_name] = extracted_value

                    data = json_data  # 保持原始数据

                except Exception as e:
                    self._table_model.setColumnIdentifiers([u"错误信息"])
                    self._table_model.setRowCount(0)
                    self._table_model.addRow([u"赋值解析失败: " + unicode(str(e), errors='ignore')])
                    return
            else:
                # 普通路径表达式
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

        # 创建一个更完整的执行环境
        import __builtin__ as builtins
        safe_builtins = {}

        # 安全的内置函数列表
        safe_functions = [
            'len', 'str', 'int', 'float', 'bool', 'list', 'dict', 'tuple', 'set',
            'enumerate', 'range', 'zip', 'map', 'filter', 'sorted', 'reversed',
            'isinstance', 'hasattr', 'getattr', 'setattr', 'delattr',
            'max', 'min', 'sum', 'abs', 'round', 'divmod', 'pow',
            'any', 'all', 'iter', 'next', 'ord', 'chr', 'bin', 'oct', 'hex',
            'repr', 'unicode', 'slice', 'type', 'id', 'hash'
        ]

        # 只添加存在的安全函数
        for func_name in safe_functions:
            if hasattr(builtins, func_name):
                safe_builtins[func_name] = getattr(builtins, func_name)

        exec_env = {
            'json': json, 'data': data, 're': re,
            '__builtins__': safe_builtins
        }

        # 添加赋值变量到执行环境
        exec_env.update(assignment_vars)

        # 收集所有表达式的结果
        column_results = []
        column_headers = [u"行号"]

        for i, expr_field in enumerate(self._python_expressions):
            python_code = expr_field.getText().strip()
            if not python_code:
                continue

            try:
                result = eval(python_code, exec_env)

                # 从表达式中提取列名
                column_name = self._extract_column_name_from_expression(python_code)
                if not column_name:
                    column_name = u"列{}".format(i + 1)

                column_headers.append(column_name)

                # 确保结果是列表格式
                if isinstance(result, list):
                    column_results.append([self._format_result(item) for item in result])
                else:
                    column_results.append([self._format_result(result)])

            except Exception as e:
                error_msg = unicode(str(e), errors='ignore')
                column_name = u"错误{}".format(i + 1)
                column_headers.append(column_name)
                column_results.append([error_msg])

        # 重新设置表格列
        self._table_model.setColumnIdentifiers(column_headers)
        self._table_model.setRowCount(0)

        # 确定最大行数
        max_rows = max([len(col) for col in column_results]) if column_results else 0

        # 按行填充数据
        for row_idx in range(max_rows):
            row_data = [row_idx + 1]  # 行号

            for col_data in column_results:
                if row_idx < len(col_data):
                    row_data.append(col_data[row_idx])
                else:
                    row_data.append(u"")  # 空白填充

            self._table_model.addRow(row_data)

    def _extract_column_name_from_expression(self, expression):
        """从Python表达式中提取列名"""
        try:
            # 匹配 [i['字段名'] for i in data] 格式
            import re
            match = re.search(r"i\['([^']+)'\]", expression)
            if match:
                return match.group(1)

            # 匹配 [i["字段名"] for i in data] 格式
            match = re.search(r'i\["([^"]+)"\]', expression)
            if match:
                return match.group(1)

            # 匹配 [i.字段名 for i in data] 格式
            match = re.search(r'i\.(\w+)', expression)
            if match:
                return match.group(1)

        except:
            pass

        return None

    def _export_to_csv(self, _event=None):
        """导出表格数据为CSV文件"""
        try:
            # 检查是否有数据可以导出
            if self._table_model.getRowCount() == 0:
                self._table_model.setRowCount(0)
                self._table_model.setColumnIdentifiers([u"提示", u"信息"])
                self._table_model.addRow([u"无数据", u"没有数据可导出，请先提取数据"])
                return

            # 选择保存位置
            file_chooser = JFileChooser()
            file_chooser.setDialogTitle(u"导出为CSV文件")
            file_chooser.setFileFilter(FileNameExtensionFilter(u"CSV files (*.csv)", ["csv"]))
            file_chooser.setSelectedFile(File("extracted_data.csv"))

            if file_chooser.showSaveDialog(self._main_panel) == JFileChooser.APPROVE_OPTION:
                file_path = file_chooser.getSelectedFile().getAbsolutePath()
                if not file_path.endswith('.csv'):
                    file_path += '.csv'

                self._write_csv_file(file_path)
        except Exception as e:
            self._table_model.setRowCount(0)
            self._table_model.setColumnIdentifiers([u"错误", u"详情"])
            self._table_model.addRow([u"导出错误", unicode(str(e), errors='ignore')])

    def _write_csv_file(self, file_path):
        """写入CSV文件 - 支持导出选中部分或全部"""
        try:
            import codecs

            # 检查是否有选中的行和列
            selected_rows = self._table.getSelectedRows()
            selected_cols = self._table.getSelectedColumns()

            # 如果没有选中任何内容，导出全部
            if not selected_rows or not selected_cols:
                selected_rows = range(self._table_model.getRowCount())
                selected_cols = range(self._table_model.getColumnCount())
                export_info = u"导出全部数据"
            else:
                export_info = u"导出选中区域: {}行 {}列".format(len(selected_rows), len(selected_cols))

            # 使用UTF-8编码写入CSV文件
            with codecs.open(file_path, 'w', encoding='utf-8-sig') as f:
                # 写入表头（选中的列）
                headers = []
                for col in selected_cols:
                    column_name = self._table_model.getColumnName(col)
                    if column_name is None:
                        column_name = u"列" + unicode(col)
                    else:
                        column_name = unicode(column_name)
                    headers.append(column_name)
                f.write(u','.join([u'"{}"'.format(h.replace(u'"', u'""')) for h in headers]) + u'\n')

                # 写入数据行（选中的行和列）
                for row in selected_rows:
                    row_data = []
                    for col in selected_cols:
                        value = self._table_model.getValueAt(row, col)
                        if value is None:
                            value = u""
                        else:
                            value = unicode(value)

                        # 处理包含逗号和引号的值
                        value_str = value.replace(u'"', u'""')
                        row_data.append(u'"{}"'.format(value_str))
                    f.write(u','.join(row_data) + u'\n')

            # 提示用户文件已保存
            self._table_model.setRowCount(0)
            self._table_model.addRow([u"导出成功", u"CSV文件已保存到: " + file_path])
            self._table_model.addRow([u"导出信息", export_info])
            self._table_model.addRow([u"提示", u"CSV文件支持中文，可用Excel打开"])

        except Exception as e:
            self._table_model.setRowCount(0)
            self._table_model.addRow([u"导出失败", unicode(str(e), errors='ignore')])

    def _copy_selection(self, _event=None):
        """将表格中选中的内容复制到剪贴板"""
        rows = self._table.getSelectedRows()
        cols = self._table.getSelectedColumns()

        if not rows or not cols:
            # 如果没有选中内容，显示提示
            self._table_model.setRowCount(0)
            self._table_model.addRow([u"提示", u"请先选择要复制的单元格或区域"])
            return

        lines = [u"\t".join(str(self._table.getValueAt(r, c)) for c in cols) for r in rows]
        try:
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
            clipboard.setContents(StringSelection(u"\n".join(lines)), None)
            # 复制成功后显示提示
            self._table_model.setRowCount(0)
            self._table_model.addRow([u"复制成功", u"已复制{}行{}列到剪贴板".format(len(rows), len(cols))])
        except Exception as e:
            self._table_model.setRowCount(0)
            self._table_model.addRow([u"复制失败", unicode(str(e), errors='ignore')])

    def _show_field_selector(self, event=None):
        """弹出对话框让用户批量选择可见字段"""
        if not self._all_headers:
            return

        dialog = JDialog()
        dialog.setTitle(u"选择字段")
        dialog.setModal(True)

        # 列表面板
        list_panel = JPanel()
        list_panel.setLayout(BoxLayout(list_panel, BoxLayout.Y_AXIS))

        checkboxes = []

        for header in self._all_headers:
            if header == u"行号":
                continue

            cb = JCheckBox(header)
            cb.setSelected(header in self._visible_headers)
            checkboxes.append(cb)
            list_panel.add(cb)

        scroll = JScrollPane(list_panel)
        # 提高滚轮滚动速度
        scroll.getVerticalScrollBar().setUnitIncrement(20)

        # 按钮面板
        btn_panel = JPanel(FlowLayout(FlowLayout.RIGHT))
        ok_btn = JButton(u"确定")
        cancel_btn = JButton(u"取消")
        btn_panel.add(ok_btn)
        btn_panel.add(cancel_btn)

        def _apply(_):
            # 更新可见列
            self._visible_headers = [u"行号"] + [cb.getText() for cb in checkboxes if cb.isSelected()]
            if len(self._visible_headers) == 1 and len(self._all_headers) > 1:
                # 防止全部取消导致只剩行号
                self._visible_headers.append(self._all_headers[1])

            self._display_table(self._all_headers, self._current_rows)
            dialog.dispose()

        def _close(_):
            dialog.dispose()

        ok_btn.addActionListener(_apply)
        cancel_btn.addActionListener(_close)

        content = JPanel(BorderLayout())
        content.add(scroll, BorderLayout.CENTER)
        content.add(btn_panel, BorderLayout.SOUTH)

        dialog.getContentPane().add(content)
        dialog.setSize(300, 400)
        dialog.setLocationRelativeTo(self._main_panel)
        dialog.setVisible(True)

    def _display_table(self, headers, rows):
        # 保存全部列与行
        prev_headers = getattr(self, '_all_headers', None)
        header_changed = (prev_headers != headers)
        self._all_headers = headers
        self._current_rows = rows

        # 当列集合发生变化时，重新初始化可见列
        if header_changed:
            if len(headers) <= 7:
                # 列数不多，全部显示
                self._visible_headers = list(headers)
            else:
                # 默认：行号 + 6 列
                self._visible_headers = headers[:7]

        # 如果现有可见列均不在 headers（极端情况），也重置
        if not any(h in headers for h in self._visible_headers):
            self._visible_headers = headers if len(headers) <= 7 else headers[:7]

        # 行号列始终显示且在首位
        if u"行号" in headers:
            if u"行号" not in self._visible_headers:
                self._visible_headers.insert(0, u"行号")
            else:
                # 确保行号在首位
                self._visible_headers = [u"行号"] + [h for h in self._visible_headers if h != u"行号"]

        # 过滤可见列不存在的项
        self._visible_headers = [h for h in self._visible_headers if h in headers]

        display_indices = [headers.index(h) for h in self._visible_headers]
        display_headers = [headers[i] for i in display_indices]

        self._table_model.setColumnIdentifiers(display_headers)
        self._table_model.setRowCount(0)

        for row in rows:
            display_row = [row[i] if i < len(row) else u"" for i in display_indices]
            self._table_model.addRow(display_row)

    def _add_default_regex_pattern(self):
        """添加一个默认的正则表达式输入框"""
        panel = JPanel(FlowLayout(FlowLayout.LEFT))
        field = JTextField(40)
        # 默认示例正则
        if not self._regex_patterns:
            field.setText(u'"name":"(.*?)"')
        panel.add(JLabel(u"表达式:"))
        panel.add(field)
        self._regex_patterns.append(field)
        self._regex_panel.add(panel)

    def _add_regex_pattern(self, event=None):
        """动态添加新的正则输入框"""
        panel = JPanel(FlowLayout(FlowLayout.LEFT))
        field = JTextField(40)

        # 若已有表达式，则默认填充为上一条，用户可按需微调
        if self._regex_patterns:
            prev_text = self._regex_patterns[-1].getText()
            field.setText(prev_text)

        panel.add(JLabel(u"表达式:"))
        panel.add(field)
        self._regex_patterns.append(field)
        self._regex_panel.add(panel)
        self._regex_panel.revalidate()
        self._regex_panel.repaint()

    def _remove_regex_pattern(self, event=None):
        """删除最后一个正则输入框"""
        if len(self._regex_patterns) > 1:
            self._regex_patterns.pop()
            comp_cnt = self._regex_panel.getComponentCount()
            if comp_cnt > 1:
                self._regex_panel.remove(comp_cnt - 1)
            self._regex_panel.revalidate()
            self._regex_panel.repaint()

    def _apply_css(self):
        """支持多 CSS 选择器，按列展示结果"""
        try:
            from org.jsoup import Jsoup

            # 收集所有非空选择器
            selectors = [f.getText().strip() for f in self._css_patterns if f.getText().strip()]

            if not selectors:
                self._table_model.setColumnIdentifiers([u"提示", u"信息"])
                self._table_model.setRowCount(0)
                self._table_model.addRow([u"未输入", u"请至少输入一个 CSS 选择器"])
                return

            doc = Jsoup.parse(self._current_body_str)

            column_headers = [u"行号"]
            column_results = []

            for idx, sel in enumerate(selectors, 1):
                try:
                    elements = doc.select(sel).toArray()
                    matches = []
                    for el in elements:
                        try:
                            txt = el.text().strip()
                            if not txt:
                                txt = el.outerHtml().strip()
                        except Exception:
                            txt = unicode(str(el), errors='ignore')
                        matches.append(self._format_result(txt))
                except Exception as e:
                    matches = [u"错误: " + unicode(str(e), errors='ignore')]

                column_headers.append(u"匹配{}".format(idx))
                column_results.append(matches)

            # 设置表格结构
            self._table_model.setColumnIdentifiers(column_headers)
            self._table_model.setRowCount(0)

            max_rows = max(len(col) for col in column_results)

            if max_rows == 0:
                self._table_model.setColumnIdentifiers([u"提示", u"信息"])
                self._table_model.addRow([u"未匹配", u"所有 CSS 选择器均未匹配到内容"])
                return

            for row_idx in range(max_rows):
                row_data = [row_idx + 1]
                for col_data in column_results:
                    row_data.append(col_data[row_idx] if row_idx < len(col_data) else u"")
                self._table_model.addRow(row_data)

        except Exception as e:
            self._table_model.setColumnIdentifiers([u"错误", u"详情"])
            self._table_model.setRowCount(0)
            self._table_model.addRow([u"CSS解析失败", unicode(str(e), errors='ignore')])

    # --------- CSS 选择器输入框管理 ---------
    def _add_default_css_pattern(self):
        panel = JPanel(FlowLayout(FlowLayout.LEFT))
        field = JTextField(40)
        if not self._css_patterns:
            field.setText(u"div.title")
        panel.add(JLabel(u"表达式:"))
        panel.add(field)
        self._css_patterns.append(field)
        self._css_panel.add(panel)

    def _add_css_pattern(self, event=None):
        panel = JPanel(FlowLayout(FlowLayout.LEFT))
        field = JTextField(40)
        if self._css_patterns:
            field.setText(self._css_patterns[-1].getText())
        panel.add(JLabel(u"表达式:"))
        panel.add(field)
        self._css_patterns.append(field)
        self._css_panel.add(panel)
        self._css_panel.revalidate()
        self._css_panel.repaint()

    def _remove_css_pattern(self, event=None):
        if len(self._css_patterns) > 1:
            self._css_patterns.pop()
            comp_cnt = self._css_panel.getComponentCount()
            if comp_cnt > 1:
                self._css_panel.remove(comp_cnt - 1)
            self._css_panel.revalidate()
            self._css_panel.repaint()

    def _apply_mixed(self):
        """混合提取：聚合正则、JSON路径、CSS 选择器、Python 表达式结果"""
        pattern_text = self._pattern_field.getText()

        # 根据勾选决定哪些类型要处理
        # ------- 正则 --------
        regex_results = []
        regex_headers = []
        if self._mix_chk_regex.isSelected():
            for idx, pat_field in enumerate(self._regex_patterns, 1):
                pat = pat_field.getText().strip()
                if not pat:
                    continue
                try:
                    matches = re.compile(pat, re.UNICODE | re.DOTALL).findall(self._current_body_str)
                except Exception as e:
                    matches = [u"错误: " + unicode(str(e), errors='ignore')]
                regex_headers.append(u"正则{}".format(idx))
                regex_results.append([self._format_result(m) for m in matches])

        # ------- CSS --------
        css_results = []
        css_headers = []
        if self._mix_chk_css.isSelected():
            try:
                from org.jsoup import Jsoup
                doc = Jsoup.parse(self._current_body_str)
                for idx, sel_field in enumerate(self._css_patterns, 1):
                    sel = sel_field.getText().strip()
                    if not sel:
                        continue
                    try:
                        elements = doc.select(sel).toArray()
                        matches = []
                        for el in elements:
                            txt = el.text().strip()
                            if not txt:
                                txt = el.outerHtml().strip()
                            matches.append(self._format_result(txt))
                    except Exception as e:
                        matches = [u"错误: " + unicode(str(e), errors='ignore')]
                    css_headers.append(u"CSS{}".format(idx))
                    css_results.append(matches)
            except Exception:
                # Jsoup 不可用
                pass

        # ------- JSON路径 --------
        json_headers = []
        json_results = []
        assignment_vars = {}

        # JSON 路径文本来自专用字段
        json_expr_text = self._json_field.getText().strip()

        if self._mix_chk_json.isSelected() and json_expr_text:
            try:
                json_data = json.loads(self._current_body_str)

                # 支持赋值语法：var = [path]
                if '=' in json_expr_text and not json_expr_text.strip().startswith('['):
                    var_name, path_expr = json_expr_text.split('=', 1)
                    var_name = var_name.strip()
                    path_expr = path_expr.strip()

                    jp_results = self._extract_json_path(json_data, path_expr)
                    extracted_value = jp_results[0] if len(jp_results) == 1 else jp_results
                    assignment_vars[var_name] = extracted_value

                    # 仍然在表格中显示提取结果
                    json_headers.append(var_name)
                    json_results.append([self._format_result(extracted_value)])
                else:
                    jp_results = self._extract_json_path(json_data, json_expr_text)
                    json_headers.append(u"JSON路径")
                    json_results.append([self._format_result(r) for r in jp_results])

            except Exception as e:
                json_headers.append(u"JSON路径")
                json_results.append([u"错误: " + unicode(str(e), errors='ignore')])

        # ------- Python表达式 --------
        py_headers = []
        py_results = []
        # 复用 _apply_python_expression 的内部逻辑但不直接调用以避免 UI 冲突
        # 简化：按每条表达式整体 eval
        if self._mix_chk_python.isSelected():
            try:
                data_env = {
                    'json': json, 're': re,
                    'text': self._current_body_str,
                }
                try:
                    data_env['data'] = json.loads(self._current_body_str)
                except:
                    data_env['data'] = self._current_body_str

                # 注入 assignment 变量
                data_env.update(assignment_vars)

                import __builtin__ as builtins
                safe_funcs = {name: getattr(builtins, name) for name in ('len','str','int','float','bool','list','dict','set','enumerate','range') if hasattr(builtins, name)}
                data_env['__builtins__'] = safe_funcs

                for idx, expr_field in enumerate(self._python_expressions, 1):
                    code = expr_field.getText().strip()
                    if not code:
                        continue
                    try:
                        res = eval(code, data_env)
                        if isinstance(res, list):
                            formatted = [self._format_result(r) for r in res]
                        else:
                            formatted = [self._format_result(res)]
                    except Exception as e:
                        formatted = [u"错误: " + unicode(str(e), errors='ignore')]
                    py_headers.append(u"PY{}".format(idx))
                    py_results.append(formatted)
            except Exception:
                pass

        headers = [u"行号"] + regex_headers + css_headers + json_headers + py_headers
        result_lists = regex_results + css_results + json_results + py_results

        # 若没有任何列
        if not result_lists:
            self._table_model.setColumnIdentifiers([u"提示", u"信息"])
            self._table_model.setRowCount(0)
            self._table_model.addRow([u"未输入", u"请至少输入一种表达式"])
            return

        self._table_model.setColumnIdentifiers(headers)
        self._table_model.setRowCount(0)

        max_rows = max(len(col) for col in result_lists)

        for row_idx in range(max_rows):
            row = [row_idx + 1]
            for col in result_lists:
                row.append(col[row_idx] if row_idx < len(col) else u"")
            self._table_model.addRow(row)

    def _update_mix_panels(self):
        """根据混合模式的复选框显示/隐藏各面板"""
        self._python_panel.setVisible(self._mix_chk_python.isSelected())
        self._regex_panel.setVisible(self._mix_chk_regex.isSelected())
        self._css_panel.setVisible(self._mix_chk_css.isSelected())
        self._json_panel.setVisible(self._mix_chk_json.isSelected())

        # 顶部 pattern 字段在混合模式下始终隐藏
        self._pattern_field.setVisible(False)
        self._pattern_label.setVisible(False)

        self._expression_panel.revalidate()
        self._expression_panel.repaint()