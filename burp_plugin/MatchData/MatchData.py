# -*- encoding: utf-8 -*-
# TODO:@ModuleName: burp_regex_extractor
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/06/23 10:25

from burp import IBurpExtender, IMessageEditorTabFactory, IMessageEditorTab
from javax.swing import JScrollPane, JTable, JPanel, JLabel, JTextField, JButton, JPopupMenu, JMenuItem, JComboBox, \
    BoxLayout, JFileChooser
from javax.swing.table import DefaultTableModel
from javax.swing.filechooser import FileNameExtensionFilter
from java.awt import BorderLayout, FlowLayout
from java.awt.datatransfer import StringSelection
from java.awt import Toolkit
from java.awt.event import MouseAdapter
from java.io import File
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
        self._python_expressions = []  # 存储多个Python表达式输入框
        self._setup_ui()

    def _setup_ui(self):
        """构建用户界面"""
        self._mode_combo = JComboBox([u"正则表达式", u"JSON路径", u"Python表达式"])
        self._pattern_field = JTextField(30)

        apply_button = JButton(u"应用", actionPerformed=self._apply_pattern)
        copy_button = JButton(u"复制已选", actionPerformed=self._copy_selection)
        export_button = JButton(u"导出CSV", actionPerformed=self._export_to_csv)
        self._mode_combo.addActionListener(self._on_mode_changed)

        top_panel = JPanel(BorderLayout())
        first_line = JPanel(FlowLayout(FlowLayout.LEFT))
        first_line.add(JLabel(u"模式:"))
        first_line.add(self._mode_combo)
        first_line.add(JLabel(u"表达式:"))
        first_line.add(self._pattern_field)
        first_line.add(apply_button)
        first_line.add(copy_button)
        first_line.add(export_button)

        # Python表达式区域
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

        top_panel.add(first_line, BorderLayout.NORTH)
        top_panel.add(self._python_panel, BorderLayout.SOUTH)

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
        if not pattern_text:
            return

        mode = self._mode_combo.getSelectedItem()
        try:
            if mode == u"正则表达式":
                self._apply_regex(pattern_text)
            elif mode == u"JSON路径":
                self._apply_json_path(pattern_text)
            else:
                self._apply_python_expression(pattern_text)
        except Exception as e:
            self._table_model.addRow([u"错误", str(e)])

    def _apply_regex(self, pattern_text):
        """执行正则提取"""
        matches = re.compile(pattern_text, re.UNICODE).findall(self._current_body_str)

        # 重新设置表格列
        self._table_model.setColumnIdentifiers([u"行号", u"匹配结果"])
        self._table_model.setRowCount(0)

        for idx, match in enumerate(matches, 1):
            self._table_model.addRow([idx, match])

    def _apply_json_path(self, path_text):
        """提取 JSON 路径中的字段值"""
        json_data = json.loads(self._current_body_str)
        results = self._extract_json_path(json_data, path_text)

        # 重新设置表格列
        self._table_model.setColumnIdentifiers([u"行号", u"提取结果"])
        self._table_model.setRowCount(0)

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
        """将结果转为可打印字符串，修复中文显示问题"""
        if result is None:
            return u"null"
        elif isinstance(result, bool):
            return u"true" if result else u"false"
        elif isinstance(result, (int, float)):
            return unicode(result)
        elif isinstance(result, unicode):
            return result
        elif isinstance(result, str):
            try:
                return result.decode('utf-8')
            except:
                return unicode(result, errors='ignore')
        elif isinstance(result, (list, dict)):
            return json.dumps(result, ensure_ascii=False, separators=(',', ':'))
        else:
            try:
                return unicode(result)
            except:
                return unicode(str(result), errors='ignore')

    def _add_default_python_expression(self):
        """添加默认的Python表达式输入框"""
        expr_panel = JPanel(FlowLayout(FlowLayout.LEFT))
        expr_field = JTextField(40)
        expr_field.setText(u"[i['name'] for i in a]")

        expr_panel.add(JLabel(u"表达式:"))
        expr_panel.add(expr_field)

        self._python_expressions.append(expr_field)
        self._python_panel.add(expr_panel)

    def _add_python_expression(self, event=None):
        """添加新的Python表达式输入框"""
        expr_panel = JPanel(FlowLayout(FlowLayout.LEFT))
        expr_field = JTextField(40)

        # 根据当前表达式数量设置不同的默认值
        expr_count = len(self._python_expressions)
        default_expressions = [
            u"[i['name'] for i in a]",
            u"[i['_id'] for i in a]",
            u"[i['human_rule'] for i in a]",
            u"[i['update_date'] for i in a]"
        ]

        if expr_count < len(default_expressions):
            expr_field.setText(default_expressions[expr_count])

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
        self._python_panel.setVisible(self._mode_combo.getSelectedItem() == u"Python表达式")
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