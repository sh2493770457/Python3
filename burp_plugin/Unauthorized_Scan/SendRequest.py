# -*- coding: utf-8 -*-
# 批量请求发送工具 - Burp Suite扩展插件
from burp import IBurpExtender, ITab, IHttpService, IMessageEditorController

from java.awt import BorderLayout, Dimension, FlowLayout, GridBagLayout, GridBagConstraints, Insets
from java.util import ArrayList, List
from java.util.concurrent import Executors, TimeUnit
import threading
from java.lang import Runnable
from javax.swing import (JPanel, JButton, JFileChooser, JScrollPane, JTable,
                         JLabel, JSpinner, SpinnerNumberModel, JCheckBox,
                         SwingUtilities, JSplitPane, JPopupMenu, JMenuItem,
                         JDialog, JFrame, BoxLayout, BorderFactory, JTextField,
                         JTextArea, JProgressBar)
from javax.swing.table import AbstractTableModel, TableRowSorter
from javax.swing.event import ListSelectionListener
from java.net import URL, InetAddress
from java.awt.event import MouseAdapter, ItemListener
from java.awt import FlowLayout
from java.io import File, FileOutputStream, FileInputStream
from java.util import Properties, Comparator

import re
import time
import os

class BulkRequester(IBurpExtender, ITab, IMessageEditorController):
    """简单的批量请求工具扩展，读取包含多个HTTP请求的文本文件，
    并发重放这些请求，并在带有集成消息查看器的表格中显示请求/响应结果。
    """

    # 表格列名 - 完全匹配Proxy显示
    COLS = [u"#", u"Host", u"请求方法", u"URL", u"参数", u"已编辑", u"状态码", u"长度", u"MIME类型", u"扩展", u"标题", u"Notes", u"TLS", u"IP", u"Cookies", u"时间"]
    
    # 默认显示的列（除了#列总是显示）
    DEFAULT_VISIBLE_COLS = [True, True, True, True, False, False, True, True, True, False, False, False, True, True, False, True]

    def registerExtenderCallbacks(self, callbacks):
        self._callbacks = callbacks
        self._helpers = callbacks.getHelpers()
        callbacks.setExtensionName("SendRequest")

        # 数据容器
        self._messages = ArrayList()  # 保存IHttpRequestResponse实例
        self._start_ts = {}
        self._elapsed_map = {}
        self._redirect_map = {}
        self._orig_url_map = {}
        self._msg_url_map = {}  # 消息索引到原始URL的映射
        self._current_file_path = None  # 保存当前文件路径用于重新加载
        self._ip_cache = {}  # 缓存主机名到IP的映射
        
        # 用于同步消息添加的锁对象
        self._message_lock = threading.Lock()
        
        # 进度跟踪
        self._total_requests = 0
        self._completed_requests = 0
        
        # 列配置
        self._config_file = os.path.join(os.path.expanduser("~"), ".sendrequest_config.properties")
        self._visible_cols = self.DEFAULT_VISIBLE_COLS[:]  # 复制默认配置
        self._load_column_config()  # 加载保存的列配置
        
        # 请求修改配置 - 设置默认示例
        self._add_headers = u"Cookie: sessionid=your_session_here\nAuthorization: Bearer your_token_here\ntoken: xxxxx"
        self._remove_headers = u"Cookie\nToken\ntoken\nAuthorization\n"
        self._replace_headers = u"Cookie: new_session_value\ntoken: xxxx"
        self._remove_params = u"timestamp\nts\ntoken\napi_key"
        
        # 功能启用状态
        self._enable_add_headers = False
        self._enable_remove_headers = False
        self._enable_replace_headers = False
        self._enable_remove_params = False

        # 构建用户界面
        self._main_panel = JPanel(BorderLayout())
        self._top_panel = JPanel()
        self._load_btn = JButton(u"加载文件", actionPerformed=self._load_file)
        self._reload_btn = JButton(u"重新加载", actionPerformed=self._reload_file)
        self._start_btn = JButton(u"开始发送", actionPerformed=self._start_requests)
        self._clear_btn = JButton(u"清空结果", actionPerformed=self._clear_table)
        self._columns_btn = JButton(u"列设置", actionPerformed=self._show_column_config)
        self._request_config_btn = JButton(u"请求设置", actionPerformed=self._show_request_config)
        self._start_btn.setEnabled(False)
        self._reload_btn.setEnabled(False)
        self._thread_spinner = JSpinner(SpinnerNumberModel(5, 1, 50, 1))
        self._redirect_chk = JCheckBox(u"跟随重定向", False)
        self._top_panel.add(self._load_btn)
        self._top_panel.add(self._reload_btn)
        self._top_panel.add(JLabel(u"线程数:"))
        self._top_panel.add(self._thread_spinner)
        self._top_panel.add(self._redirect_chk)
        self._top_panel.add(self._clear_btn)
        self._top_panel.add(self._columns_btn)
        self._top_panel.add(self._request_config_btn)
        self._top_panel.add(self._start_btn)
        
        # 创建进度条面板
        self._progress_panel = JPanel(BorderLayout())
        self._progress_bar = JProgressBar(0, 100)
        self._progress_bar.setStringPainted(True)
        self._progress_bar.setString(u"就绪")
        self._progress_bar.setVisible(False)  # 初始隐藏
        self._progress_label = JLabel(u"")
        self._progress_panel.add(self._progress_bar, BorderLayout.CENTER)
        self._progress_panel.add(self._progress_label, BorderLayout.EAST)
        
        # 组合顶部区域
        top_container = JPanel(BorderLayout())
        top_container.add(self._top_panel, BorderLayout.NORTH)
        top_container.add(self._progress_panel, BorderLayout.SOUTH)
        self._main_panel.add(top_container, BorderLayout.NORTH)

        # 表格和模型
        self._table_model = self._ResultTableModel(self)
        self._table = JTable(self._table_model)
        self._table.setPreferredScrollableViewportSize(Dimension(800, 300))
        
        # 添加排序功能
        self._row_sorter = TableRowSorter(self._table_model)
        self._table.setRowSorter(self._row_sorter)
        self._setup_column_comparators()
        
        self._table.getSelectionModel().addListSelectionListener(self._RowSelectionListener(self))

        # 消息查看器
        self._req_viewer = callbacks.createMessageEditor(self, False)
        self._resp_viewer = callbacks.createMessageEditor(self, False)
        viewer_panel = JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                  self._req_viewer.getComponent(),
                                  self._resp_viewer.getComponent())
        viewer_panel.setResizeWeight(0.5)

        split = JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           JScrollPane(self._table),
                           viewer_panel)
        split.setResizeWeight(0.3)
        self._main_panel.add(split, BorderLayout.CENTER)

        # 右键菜单
        self._popup = JPopupMenu()
        repeater_item = JMenuItem(u"发送到 Repeater", actionPerformed=self._send_to_repeater)
        intruder_item = JMenuItem(u"发送到 Intruder", actionPerformed=self._send_to_intruder)
        comparer_item = JMenuItem(u"发送到 Comparer", actionPerformed=self._send_to_comparer)
        delete_item = JMenuItem(u"删除条目", actionPerformed=self._delete_selected)
        self._popup.add(repeater_item)
        self._popup.add(intruder_item)
        self._popup.add(comparer_item)
        self._popup.addSeparator()
        self._popup.add(delete_item)

        # 添加右键监听器
        self._table.addMouseListener(self._PopupListener(self))

        # 当用户切换重定向勾选时重新绘制表头
        class _Toggle(ItemListener):
            def __init__(self, outer):
                self._outer = outer
            def itemStateChanged(self, e):
                self._outer._table_model.fireTableStructureChanged()
                self._outer._setup_column_comparators()  # 重新设置排序器
        self._redirect_chk.addItemListener(_Toggle(self))

        callbacks.addSuiteTab(self)


    def _load_file(self, _):
        chooser = JFileChooser()
        if chooser.showOpenDialog(self._main_panel) != JFileChooser.APPROVE_OPTION:
            return
        file_path = chooser.getSelectedFile().getAbsolutePath()
        self._current_file_path = file_path
        
        # 显示加载状态
        self._progress_bar.setVisible(True)
        self._progress_bar.setString(u"加载文件中...")
        self._progress_bar.setIndeterminate(True)
        
        self._requests = self._parse_file(file_path)
        
        # 恢复进度条状态
        self._progress_bar.setIndeterminate(False)
        self._progress_bar.setVisible(False)
        self._progress_bar.setString(u"就绪")
        
        if self._requests:
            self._start_btn.setEnabled(True)
            self._reload_btn.setEnabled(True)
            self._callbacks.printOutput(u"从文件加载了 %d 个请求: %s" % (len(self._requests), file_path))
        else:
            self._callbacks.printError(u"文件中未找到有效请求")

    def _reload_file(self, _):
        if self._current_file_path is None:
            self._callbacks.printError(u"没有文件可以重新加载")
            return
            
        # 显示重新加载状态
        self._progress_bar.setVisible(True)
        self._progress_bar.setString(u"重新加载中...")
        self._progress_bar.setIndeterminate(True)
        
        self._requests = self._parse_file(self._current_file_path)
        
        # 恢复进度条状态
        self._progress_bar.setIndeterminate(False)
        self._progress_bar.setVisible(False)
        self._progress_bar.setString(u"就绪")
        
        if self._requests:
            self._callbacks.printOutput(u"重新加载了 %d 个请求: %s" % (len(self._requests), self._current_file_path))
        else:
            self._callbacks.printError(u"重新加载失败：文件中未找到有效请求")

    def _start_requests(self, _):
        thread_count = int(self._thread_spinner.getValue())
        pool = Executors.newFixedThreadPool(thread_count)
        
        # 使用锁保护清空操作
        with self._message_lock:
            self._messages.clear()
            self._redirect_map.clear()
            self._msg_url_map.clear()
            self._table_model.fireTableDataChanged()
            
            # 初始化进度跟踪
            self._total_requests = len(self._requests)
            self._completed_requests = 0

        # 显示和初始化进度条
        self._progress_bar.setVisible(True)
        self._progress_bar.setValue(0)
        self._progress_bar.setString(u"开始处理请求...")
        self._progress_label.setText(u"0/%d" % self._total_requests)

        # 启动请求
        for idx, (svc, send_bytes, orig_bytes, orig_url) in enumerate(self._requests):
            self._orig_url_map[idx] = orig_url
            runnable = self._RequestTask(self, idx, svc, send_bytes, orig_url)
            pool.submit(runnable)
        
        pool.shutdown()
        self._callbacks.printOutput(u"已使用 %d 个线程提交 %d 个请求，正在异步处理..." % (thread_count, len(self._requests)))

    def _update_editors(self):
        row = self._table.getSelectedRow()
        if row < 0:
            return
        # 转换视图行索引到模型行索引
        model_row = self._table.convertRowIndexToModel(row)
        if model_row >= self._messages.size():
            return
        message = self._messages.get(model_row)
        self._req_viewer.setMessage(message.getRequest(), True)
        self._resp_viewer.setMessage(message.getResponse(), False)


    def getHttpService(self):
        sel = self._table.getSelectedRow()
        if sel < 0:
            return None
        model_row = self._table.convertRowIndexToModel(sel)
        if model_row >= self._messages.size():
            return None
        return self._messages.get(model_row).getHttpService()

    def getRequest(self):
        sel = self._table.getSelectedRow()
        if sel < 0:
            return None
        model_row = self._table.convertRowIndexToModel(sel)
        if model_row >= self._messages.size():
            return None
        return self._messages.get(model_row).getRequest()

    def getResponse(self):
        sel = self._table.getSelectedRow()
        if sel < 0:
            return None
        model_row = self._table.convertRowIndexToModel(sel)
        if model_row >= self._messages.size():
            return None
        return self._messages.get(model_row).getResponse()


    def _parse_file(self, path):
        """解析文件并返回请求列表"""
        with open(path, 'rb') as fp:
            raw_content = fp.read()

        # 使用正向查找："空行 + 请求行" 作为分隔符，确保保留 body
        splitter = re.compile(br'\r?\n\r?\n(?=[A-Z]+ [^\r\n]+ HTTP/)')
        raw_reqs = splitter.split(raw_content)

        parsed = []
        for raw in raw_reqs:
            if not raw.strip():
                continue
            # 确保以 CRLF 结尾，若文件已带则不重复添加
            if not raw.endswith(b'\r\n'):
                raw += b'\r\n'
            if not raw.endswith(b'\r\n\r\n'):
                raw += b'\r\n'

            # 构建带CRLF终止符的原始字节
            # 原始文件可能混用换行符，统一转 CRLF 但不改变 body 内容
            orig_bytes = raw.replace(b'\r\n', b'\n').replace(b'\r', b'').replace(b'\n', b'\r\n')

            # 派生发送字节（如果存在则将HTTP/2转换为HTTP/1.1）
            lf_view = raw.replace(b'\r\n', b'\n')
            first_line_end = lf_view.find(b'\n')
            if first_line_end == -1:
                self._callbacks.printError(u'跳过格式错误的请求（没有首行）')
                continue
            first_line = lf_view[:first_line_end]
            rest = lf_view[first_line_end+1:]
            if first_line.endswith(b'HTTP/2'):
                first_line = first_line[:-6] + b'HTTP/1.1'

            send_bytes = first_line + b'\r\n' + rest.replace(b'\n', b'\r\n')
            if not send_bytes.endswith(b'\r\n\r\n'):
                send_bytes += b'\r\n\r\n'

            header_part = lf_view.split(b'\n\n', 1)[0]
            header_text = header_part.decode('iso-8859-1', 'replace')
            m = re.search(r'^Host:\s*([^:\r\n]+)(?::(\d+))?', header_text, re.M)
            if not m:
                self._callbacks.printError(u'跳过没有Host头的请求')
                continue
            host = m.group(1)
            port = int(m.group(2)) if m.group(2) else None
            
            # 智能协议判定：
            protocol = 'http'  # 默认
            
            # 检查明确的HTTPS指示
            if (first_line.endswith(b'HTTP/2') or
                b'Origin: https://' in raw or 
                b'Referer: https://' in raw):
                protocol = 'https'
            elif port in (443, 8443, 9443):  # 常见HTTPS端口
                protocol = 'https'
            elif port and port > 1000:  # 对于高端口，检查现代浏览器安全头部
                # 现代浏览器的安全头部通常表明HTTPS请求
                if (b'Sec-Ch-Ua:' in raw or 
                    b'Sec-Ch-Ua-Platform:' in raw or
                    b'Sec-Fetch-Site:' in raw or
                    b'Sec-Fetch-Mode:' in raw or
                    b'Sec-Fetch-Dest:' in raw):
                    protocol = 'https'
            
            # 设置默认端口
            if port is None:
                port = 443 if protocol == 'https' else 80
            
            svc = self._helpers.buildHttpService(host, port, protocol)

            # 构建原始URL
            path = first_line.split(b" ", 2)[1]
            url_str = "%s://%s%s" % (protocol, host.decode('latin-1') if isinstance(host, bytes) else host, path.decode('latin-1'))
            parsed.append((svc, send_bytes, orig_bytes, url_str))
        return parsed


    class _ResultTableModel(AbstractTableModel):
        def __init__(self, outer):
            self._outer = outer

        def getColumnCount(self):
            # 计算可见列数量
            visible_count = sum(1 for visible in self._outer._visible_cols if visible)
            return visible_count + (1 if self._outer._redirect_chk.isSelected() else 0)

        def getRowCount(self):
            return self._outer._messages.size()

        def getColumnName(self, col):
            visible_cols = [i for i, visible in enumerate(self._outer._visible_cols) if visible]
            if col < len(visible_cols):
                return BulkRequester.COLS[visible_cols[col]]
            else:
                return u"重定向URL"

        def getValueAt(self, row, col):
            msg = self._outer._messages.get(row)
            # Use msg.getRequest() to get raw bytes for analyzeRequest
            req_info = self._outer._helpers.analyzeRequest(msg.getRequest())
            method = req_info.getMethod()
            url = self._outer._msg_url_map.get(row, "")
            host = msg.getHttpService().getHost()
            
            # 提取URL参数
            params = u"✓" if ('?' in url and url.split('?', 1)[1]) else ""
            
            resp_info = None
            status = "-"
            length = "-"
            mime_type = ""
            title = ""
            cookies = ""
            if msg.getResponse() is not None:
                resp_info = self._outer._helpers.analyzeResponse(msg.getResponse())
                status = resp_info.getStatusCode()
                length = len(msg.getResponse())
                mime_type = resp_info.getStatedMimeType()
                if not mime_type:
                    mime_type = resp_info.getInferredMimeType()
                
                # 提取title
                try:
                    response_bytes = msg.getResponse()
                    response_str = ''.join(chr(b & 0xFF) for b in response_bytes[:10000])  # 只检查前10KB
                    if '<title>' in response_str.lower():
                        title_start = response_str.lower().find('<title>') + 7
                        title_end = response_str.lower().find('</title>', title_start)
                        if title_end > title_start:
                            title = response_str[title_start:title_end].strip()[:50]  # 限制长度
                except:
                    title = ""
                
                # 检查Cookie
                cookies = u"✓" if any(h.lower().startswith('set-cookie:') for h in resp_info.getHeaders()) else ""
            
            # 从URL提取扩展名
            path = url.split('?')[0]
            extension = path.split('.')[-1] if '.' in path else ""
            
            # 获取备注信息
            comment = msg.getComment()
            notes = comment[:30] if comment else ""
            
            tls = u"✓" if msg.getHttpService().getProtocol() == "https" else ""
            
            # 解析真实IP地址
            ip = self._outer._resolve_ip(host)
            
            # 格式化时间显示
            elapsed = self._outer._elapsed_map.get(row, "-")
            if elapsed != "-":
                if elapsed < 1000:
                    time_str = "%d ms" % elapsed
                else:
                    time_str = "%.2f s" % (elapsed / 1000.0)
            else:
                time_str = "-"
            
            # 16列数据：#, Host, 请求方法, URL, 参数, 已编辑, 状态码, 长度, MIME类型, 扩展, 标题, Notes, TLS, IP, Cookies, 时间
            all_data = [row + 1, host, method, url, params, "", status, length, mime_type, extension, title, notes, tls, ip, cookies, time_str]
            
            # 根据可见列映射返回数据
            visible_cols = [i for i, visible in enumerate(self._outer._visible_cols) if visible]
            
            if col < len(visible_cols):
                return all_data[visible_cols[col]]
            else:
                # 重定向列
                return self._outer._redirect_map.get(row, "")


    def getTabCaption(self):
        return u"SendRequest"

    def getUiComponent(self):
        return self._main_panel


    class _RowSelectionListener(ListSelectionListener):
        def __init__(self, outer):
            self._outer = outer

        def valueChanged(self, event):
            self._outer._update_editors()

    class _RequestTask(Runnable):
        def __init__(self, outer, index, svc, send_bytes, orig_url):
            self._outer = outer
            self._index = index
            self._svc = svc
            self._send_bytes = send_bytes
            self._orig_url = orig_url

        def run(self):
            try:
                self._outer._start_ts[self._index] = time.time()
                
                # 修改请求（添加/移除请求头，移除URL参数）
                modified_bytes, modified_url = self._outer._modify_request(self._send_bytes, self._orig_url)
                
                current_resp = self._outer._callbacks.makeHttpRequest(self._svc, modified_bytes)
                final_redirect_url = ""

                if self._outer._redirect_chk.isSelected():
                    redirect_limit = 5
                    redirects = 0
                    while redirects < redirect_limit:
                        analyze = self._outer._helpers.analyzeResponse(current_resp.getResponse())
                        status = analyze.getStatusCode()
                        if status not in (301, 302, 303, 307, 308):
                            break
                        # 提取Location头
                        loc = None
                        for h in analyze.getHeaders():
                            if h.lower().startswith("location:"):
                                loc = h.split(":", 1)[1].strip()
                                break
                        if loc is None:
                            break
                        try:
                            new_url = URL(loc) if loc.startswith("http") else URL("http", self._svc.getHost(), self._svc.getPort(), loc)
                        except Exception as ex:
                            self._outer._callbacks.printError(u"无效的重定向URL %s: %s" % (loc, ex))
                            break

                        new_svc = self._outer._helpers.buildHttpService(new_url.getHost(),
                                                                         new_url.getPort() if new_url.getPort() != -1 else (443 if new_url.getProtocol() == "https" else 80),
                                                                         new_url.getProtocol())

                        # 构建新请求
                        new_req = self._outer._helpers.buildHttpRequest(new_url)
                        
                        # 对重定向请求也应用修改
                        modified_new_req, _ = self._outer._modify_request(new_req, new_url.toString())

                        current_resp = self._outer._callbacks.makeHttpRequest(new_svc, modified_new_req)
                        redirects += 1
                        final_redirect_url = new_url.toString()

                # 计算耗时（在工作线程中完成，避免UI阻塞）
                elapsed_time = int((time.time() - self._outer._start_ts[self._index]) * 1000)
                
                # 使用SwingUtilities.invokeLater异步提交UI更新，避免阻塞工作线程
                def add_response():
                    # 使用锁避免竞争条件
                    with self._outer._message_lock:
                        # 添加响应到消息列表并记录URL映射
                        msg_index = self._outer._messages.size()
                        self._outer._messages.add(current_resp)
                        self._outer._msg_url_map[msg_index] = modified_url
                        
                        # 保存重定向信息
                        if self._outer._redirect_chk.isSelected() and final_redirect_url:
                            self._outer._redirect_map[msg_index] = final_redirect_url
                        
                        # 保存耗时
                        self._outer._elapsed_map[msg_index] = elapsed_time
                        
                        # 更新表格显示
                        self._outer._table_model.fireTableRowsInserted(msg_index, msg_index)
                        
                        # 更新进度
                        self._outer._completed_requests += 1
                        completed = self._outer._completed_requests
                        total = self._outer._total_requests
                        
                        if total > 0:
                            progress = int((completed * 100) / total)
                            self._outer._progress_bar.setValue(progress)
                            self._outer._progress_label.setText(u"%d/%d" % (completed, total))
                            
                            if completed == total:
                                # 所有请求完成
                                self._outer._progress_bar.setString(u"全部完成！")
                                # 3秒后隐藏进度条
                                def hide_progress():
                                    self._outer._progress_bar.setVisible(False)
                                    self._outer._progress_bar.setString(u"就绪")
                                timer = SwingUtilities.invokeLater(lambda: None)
                                Executors.newSingleThreadScheduledExecutor().schedule(
                                    lambda: SwingUtilities.invokeLater(hide_progress), 
                                    3, TimeUnit.SECONDS)
                            else:
                                self._outer._progress_bar.setString(u"处理中... (%d%%)" % progress)
                
                SwingUtilities.invokeLater(add_response)
                
            except Exception as e:
                self._outer._callbacks.printError(u"请求任务错误: %s" % e)
                
                # 即使出错也要更新进度计数
                def update_error_progress():
                    with self._outer._message_lock:
                        self._outer._completed_requests += 1
                        completed = self._outer._completed_requests
                        total = self._outer._total_requests
                        
                        if total > 0:
                            progress = int((completed * 100) / total)
                            self._outer._progress_bar.setValue(progress)
                            self._outer._progress_label.setText(u"%d/%d" % (completed, total))
                            
                            if completed == total:
                                # 所有请求完成（包括错误的）
                                self._outer._progress_bar.setString(u"处理完成（含错误）")
                                # 3秒后隐藏进度条
                                def hide_progress():
                                    self._outer._progress_bar.setVisible(False)
                                    self._outer._progress_bar.setString(u"就绪")
                                Executors.newSingleThreadScheduledExecutor().schedule(
                                    lambda: SwingUtilities.invokeLater(hide_progress), 
                                    3, TimeUnit.SECONDS)
                            else:
                                self._outer._progress_bar.setString(u"处理中... (%d%%) [含错误]" % progress)
                
                SwingUtilities.invokeLater(update_error_progress)


    def _resolve_ip(self, hostname):
        """解析主机名到IP地址，使用缓存提高性能"""
        if hostname in self._ip_cache:
            return self._ip_cache[hostname]
        
        try:
            addr = InetAddress.getByName(hostname)
            ip = addr.getHostAddress()
            self._ip_cache[hostname] = ip
            return ip
        except Exception:
            # 如果解析失败，返回主机名
            self._ip_cache[hostname] = hostname
            return hostname

    # 清空表格
    def _clear_table(self, _):
        with self._message_lock:
            self._messages.clear()
            self._redirect_map.clear()
            self._orig_url_map.clear()
            self._msg_url_map.clear()
            self._start_ts.clear()
            self._elapsed_map.clear()
            self._ip_cache.clear()  # 清空IP缓存
            
            # 重置进度
            self._total_requests = 0
            self._completed_requests = 0
            
            self._table_model.fireTableDataChanged()
        
        # 隐藏进度条
        self._progress_bar.setVisible(False)
        self._progress_bar.setValue(0)
        self._progress_bar.setString(u"就绪")
        self._progress_label.setText(u"")

    # 右键操作实现
    def _current_message(self):
        row = self._table.getSelectedRow()
        if row < 0:
            return None
        model_row = self._table.convertRowIndexToModel(row)
        if model_row >= self._messages.size():
            return None
        return self._messages.get(model_row)

    def _send_to_repeater(self, _):
        msg = self._current_message()
        if msg is None:
            return
        svc = msg.getHttpService()
        self._callbacks.sendToRepeater(svc.getHost(), svc.getPort(), svc.getProtocol() == "https", msg.getRequest(), None, None)

    def _send_to_intruder(self, _):
        msg = self._current_message()
        if msg is None:
            return
        self._callbacks.sendToIntruder(msg.getHttpService().getHost(), msg.getHttpService().getPort(), msg.getHttpService().getProtocol() == "https", msg.getRequest())

    def _send_to_comparer(self, _):
        msg = self._current_message()
        if msg is None:
            return
        # 直接使用消息，因为现在我们存储的就是原始消息
        self._callbacks.sendToComparer(msg)

    def _delete_selected(self, _):
        row = self._table.getSelectedRow()
        if row < 0:
            return
        model_row = self._table.convertRowIndexToModel(row)
        if model_row >= self._messages.size():
            return
        
        with self._message_lock:
            # 删除消息和相关映射
            self._messages.remove(model_row)
            
            # 更新所有映射，删除指定行并调整后续行的索引
            def update_map(map_dict):
                new_map = {}
                for key, value in map_dict.items():
                    if key < model_row:
                        new_map[key] = value
                    elif key > model_row:
                        new_map[key - 1] = value
                    # key == model_row 的项被删除
                return new_map
            
            self._redirect_map = update_map(self._redirect_map)
            self._msg_url_map = update_map(self._msg_url_map)
            self._elapsed_map = update_map(self._elapsed_map)
            
            self._table_model.fireTableDataChanged()

    # 右键弹出菜单监听器
    class _PopupListener(MouseAdapter):
        def __init__(self, outer):
            self._outer = outer

        def _check(self, event):
            if event.isPopupTrigger():
                row = self._outer._table.rowAtPoint(event.getPoint())
                if row >= 0:
                    self._outer._table.setRowSelectionInterval(row, row)
                    # 右键菜单显示时直接使用视图行索引，具体操作中会转换
                self._outer._popup.show(self._outer._table, event.getX(), event.getY())

        def mousePressed(self, event):
            self._check(event)

        def mouseReleased(self, event):
            self._check(event)

    # 配置相关方法
    def _load_column_config(self):
        """加载列配置和请求设置"""
        try:
            if os.path.exists(self._config_file):
                props = Properties()
                with open(self._config_file, 'rb') as f:
                    props.load(f)
                
                # 读取列可见性配置
                for i in range(len(self.COLS)):
                    key = "column_%d_visible" % i
                    if props.containsKey(key):
                        self._visible_cols[i] = props.getProperty(key) == "true"
                
                # 读取请求设置配置（如果存在的话，否则保持默认值）
                if props.containsKey("add_headers"):
                    self._add_headers = props.getProperty("add_headers")
                if props.containsKey("remove_headers"):
                    self._remove_headers = props.getProperty("remove_headers")
                if props.containsKey("replace_headers"):
                    self._replace_headers = props.getProperty("replace_headers")
                if props.containsKey("remove_params"):
                    self._remove_params = props.getProperty("remove_params")
                    
                # 读取功能启用状态
                if props.containsKey("enable_add_headers"):
                    self._enable_add_headers = props.getProperty("enable_add_headers") == "true"
                if props.containsKey("enable_remove_headers"):
                    self._enable_remove_headers = props.getProperty("enable_remove_headers") == "true"
                if props.containsKey("enable_replace_headers"):
                    self._enable_replace_headers = props.getProperty("enable_replace_headers") == "true"
                if props.containsKey("enable_remove_params"):
                    self._enable_remove_params = props.getProperty("enable_remove_params") == "true"
                    
        except Exception as e:
            self._callbacks.printError(u"加载配置失败: %s" % e)

    def _save_column_config(self):
        """保存列配置和请求设置"""
        try:
            props = Properties()
            # 保存列可见性配置
            for i, visible in enumerate(self._visible_cols):
                props.setProperty("column_%d_visible" % i, "true" if visible else "false")
            
            # 保存请求设置配置
            props.setProperty("add_headers", self._add_headers)
            props.setProperty("remove_headers", self._remove_headers)
            props.setProperty("replace_headers", self._replace_headers)
            props.setProperty("remove_params", self._remove_params)
            
            # 保存功能启用状态
            props.setProperty("enable_add_headers", "true" if self._enable_add_headers else "false")
            props.setProperty("enable_remove_headers", "true" if self._enable_remove_headers else "false")
            props.setProperty("enable_replace_headers", "true" if self._enable_replace_headers else "false")
            props.setProperty("enable_remove_params", "true" if self._enable_remove_params else "false")
            
            with open(self._config_file, 'wb') as f:
                props.store(f, u"SendRequest Configuration")
        except Exception as e:
            self._callbacks.printError(u"保存配置失败: %s" % e)

    def _show_column_config(self, _):
        """显示列配置对话框"""
        # 创建对话框
        dialog = JDialog(None, u"列设置", True)
        dialog.setSize(400, 500)
        dialog.setLocationRelativeTo(self._main_panel)
        
        # 主面板
        main_panel = JPanel()
        main_panel.setLayout(BoxLayout(main_panel, BoxLayout.Y_AXIS))
        main_panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
        
        # 添加说明
        info_label = JLabel(u"选择要显示的列：")
        main_panel.add(info_label)
        
        # 创建复选框列表
        checkboxes = []
        for i, col_name in enumerate(self.COLS):
            checkbox = JCheckBox(col_name, self._visible_cols[i])
            # #列不允许取消
            if i == 0:
                checkbox.setEnabled(False)
                checkbox.setSelected(True)
            checkboxes.append(checkbox)
            main_panel.add(checkbox)
        
        # 按钮面板
        button_panel = JPanel(FlowLayout())
        
        # 确定按钮
        def on_ok(_):
            # 更新配置（#列始终显示）
            for i, checkbox in enumerate(checkboxes):
                if i == 0:
                    self._visible_cols[i] = True
                else:
                    self._visible_cols[i] = checkbox.isSelected()
            
            # 保存配置
            self._save_column_config()
            
            # 刷新表格
            self._table_model.fireTableStructureChanged()
            
            # 重新设置排序器
            self._setup_column_comparators()
            
            dialog.dispose()
        
        ok_btn = JButton(u"确定", actionPerformed=on_ok)
        
        # 取消按钮
        def on_cancel(_):
            dialog.dispose()
        
        cancel_btn = JButton(u"取消", actionPerformed=on_cancel)
        
        # 全选按钮
        def on_select_all(_):
            for checkbox in checkboxes:
                if checkbox.isEnabled():
                    checkbox.setSelected(True)
        
        select_all_btn = JButton(u"全选", actionPerformed=on_select_all)
        
        # 全不选按钮
        def on_select_none(_):
            for i, checkbox in enumerate(checkboxes):
                if checkbox.isEnabled() and i != 0:  # 保持#列选中
                    checkbox.setSelected(False)
        
        select_none_btn = JButton(u"全不选", actionPerformed=on_select_none)
        
        # 默认设置按钮
        def on_default(_):
            for i, checkbox in enumerate(checkboxes):
                if checkbox.isEnabled():
                    checkbox.setSelected(self.DEFAULT_VISIBLE_COLS[i])
        
        default_btn = JButton(u"默认设置", actionPerformed=on_default)
        
        button_panel.add(select_all_btn)
        button_panel.add(select_none_btn)
        button_panel.add(default_btn)
        button_panel.add(ok_btn)
        button_panel.add(cancel_btn)
        
        main_panel.add(button_panel)
        
        dialog.add(main_panel)
        dialog.setVisible(True)

    def _show_request_config(self, _):
        """显示请求配置对话框"""
        # 创建对话框
        dialog = JDialog(None, u"请求设置", True)
        dialog.setSize(500, 500)
        dialog.setLocationRelativeTo(self._main_panel)
        
        # 主面板
        main_panel = JPanel(GridBagLayout())
        main_panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
        
        gbc = GridBagConstraints()
        gbc.insets = Insets(5, 5, 5, 5)
        gbc.fill = GridBagConstraints.HORIZONTAL
        
        # 添加请求头
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.anchor = GridBagConstraints.NORTHWEST
        add_headers_panel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        add_headers_chk = JCheckBox(u"添加请求头 (格式: Cookie: xxx,每行一个):", self._enable_add_headers)
        add_headers_panel.add(add_headers_chk)
        main_panel.add(add_headers_panel, gbc)
        
        gbc.gridy = 1
        gbc.weightx = 1.0
        gbc.weighty = 0.25
        gbc.fill = GridBagConstraints.BOTH
        add_headers_area = JTextArea(self._add_headers, 3, 40)
        add_headers_area.setLineWrap(True)
        add_headers_scroll = JScrollPane(add_headers_area)
        main_panel.add(add_headers_scroll, gbc)
        
        # 移除请求头
        gbc.gridy = 2
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        remove_headers_panel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        remove_headers_chk = JCheckBox(u"移除请求头 (如: Cookie,适用于未授权检测,每行一个请求头名称):", self._enable_remove_headers)
        remove_headers_panel.add(remove_headers_chk)
        main_panel.add(remove_headers_panel, gbc)
        
        gbc.gridy = 3
        gbc.weighty = 0.25
        gbc.fill = GridBagConstraints.BOTH
        remove_headers_area = JTextArea(self._remove_headers, 3, 40)
        remove_headers_area.setLineWrap(True)
        remove_headers_scroll = JScrollPane(remove_headers_area)
        main_panel.add(remove_headers_scroll, gbc)
        
        # 替换请求头
        gbc.gridy = 4
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        replace_headers_panel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        replace_headers_chk = JCheckBox(u"替换请求头 (格式: Cookie: xxx,适用于越权测试,直接替换整个头部):", self._enable_replace_headers)
        replace_headers_panel.add(replace_headers_chk)
        main_panel.add(replace_headers_panel, gbc)
        
        gbc.gridy = 5
        gbc.weighty = 0.25
        gbc.fill = GridBagConstraints.BOTH
        replace_headers_area = JTextArea(self._replace_headers, 3, 40)
        replace_headers_area.setLineWrap(True)
        replace_headers_scroll = JScrollPane(replace_headers_area)
        main_panel.add(replace_headers_scroll, gbc)
        
        # 移除URL参数
        gbc.gridy = 6
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        remove_params_panel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        remove_params_chk = JCheckBox(u"移除URL参数 (如: ?token=xxx填token,适用于未授权中URL携带参数情况,每行一个参数名):", self._enable_remove_params)
        remove_params_panel.add(remove_params_chk)
        main_panel.add(remove_params_panel, gbc)
        
        gbc.gridy = 7
        gbc.weighty = 0.25
        gbc.fill = GridBagConstraints.BOTH
        remove_params_area = JTextArea(self._remove_params, 3, 40)
        remove_params_area.setLineWrap(True)
        remove_params_scroll = JScrollPane(remove_params_area)
        main_panel.add(remove_params_scroll, gbc)
        
        # 按钮面板
        gbc.gridy = 8
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        button_panel = JPanel(FlowLayout())
        
        # 确定按钮
        def on_ok(_):
            self._add_headers = add_headers_area.getText()
            self._remove_headers = remove_headers_area.getText()
            self._replace_headers = replace_headers_area.getText()
            self._remove_params = remove_params_area.getText()
            
            # 保存勾选状态
            self._enable_add_headers = add_headers_chk.isSelected()
            self._enable_remove_headers = remove_headers_chk.isSelected()
            self._enable_replace_headers = replace_headers_chk.isSelected()
            self._enable_remove_params = remove_params_chk.isSelected()
            
            # 保存配置到文件
            self._save_column_config()
            
            dialog.dispose()
        
        ok_btn = JButton(u"确定", actionPerformed=on_ok)
        
        # 取消按钮
        def on_cancel(_):
            dialog.dispose()
        
        cancel_btn = JButton(u"取消", actionPerformed=on_cancel)
        
        # 恢复默认按钮
        def on_reset_default(_):
            # 恢复默认示例内容
            add_headers_area.setText(u"Cookie: sessionid=your_session_here\nAuthorization: Bearer your_token_here\ntoken: xxxxx")
            remove_headers_area.setText(u"Cookie\nToken\ntoken\nAuthorization\n")
            replace_headers_area.setText(u"Cookie: new_session_value\ntoken: xxxx")
            remove_params_area.setText(u"timestamp\nts\ntoken\napi_key\n")
            
            # 清空勾选状态
            add_headers_chk.setSelected(False)
            remove_headers_chk.setSelected(False)
            replace_headers_chk.setSelected(False)
            remove_params_chk.setSelected(False)
        
        reset_btn = JButton(u"恢复默认", actionPerformed=on_reset_default)
        
        # 清空按钮
        def on_clear(_):
            add_headers_area.setText("")
            remove_headers_area.setText("")
            replace_headers_area.setText("")
            remove_params_area.setText("")
            
            # 清空勾选状态
            add_headers_chk.setSelected(False)
            remove_headers_chk.setSelected(False)
            replace_headers_chk.setSelected(False)
            remove_params_chk.setSelected(False)
        
        clear_btn = JButton(u"清空", actionPerformed=on_clear)
        
        button_panel.add(ok_btn)
        button_panel.add(cancel_btn)
        button_panel.add(reset_btn)
        button_panel.add(clear_btn)
        
        main_panel.add(button_panel, gbc)
        
        dialog.add(main_panel)
        dialog.setVisible(True)

    def _modify_request(self, request_bytes, orig_url):
        """修改请求：添加/移除请求头，移除URL参数
        返回: (修改后的请求字节, 修改后的URL)
        """
        try:
            # 将字节转换为字符串处理
            request_str = request_bytes.decode('iso-8859-1')
            
            # 分离请求行、头部和body
            parts = request_str.split('\r\n\r\n', 1)
            if len(parts) == 2:
                header_part, body = parts
            else:
                header_part = parts[0]
                body = ""
            
            lines = header_part.split('\r\n')
            if not lines:
                return request_bytes, orig_url
                
            # 获取请求行
            request_line = lines[0]
            headers = lines[1:] if len(lines) > 1 else []
            
            # 用于构建修改后的URL
            modified_url = orig_url
            
            # 处理URL参数移除
            if self._enable_remove_params and self._remove_params.strip():
                params_to_remove = [p.strip() for p in self._remove_params.split('\n') if p.strip()]
                if params_to_remove:
                    # 解析请求行
                    parts = request_line.split(' ')
                    if len(parts) >= 2:
                        method = parts[0]
                        url_path = parts[1]
                        http_version = parts[2] if len(parts) > 2 else "HTTP/1.1"
                        
                        # 处理URL参数
                        if '?' in url_path:
                            path, query = url_path.split('?', 1)
                            # 解析查询参数
                            if query:
                                params = []
                                for param in query.split('&'):
                                    if '=' in param:
                                        key, value = param.split('=', 1)
                                    else:
                                        key, value = param, ""
                                    
                                    # 只保留不在移除列表中的参数
                                    if key not in params_to_remove:
                                        params.append(param)
                                
                                # 重新构建URL路径
                                if params:
                                    url_path = path + '?' + '&'.join(params)
                                else:
                                    url_path = path
                            
                            # 重新构建请求行
                            request_line = "%s %s %s" % (method, url_path, http_version)
                            
                            # 更新完整的URL用于显示
                            if '://' in orig_url:
                                protocol_and_host = orig_url.split('/', 3)[:3]  # ['http:', '', 'host:port']
                                if len(protocol_and_host) >= 3:
                                    modified_url = '/'.join(protocol_and_host) + url_path
            
            # 处理移除请求头
            if self._enable_remove_headers and self._remove_headers.strip():
                headers_to_remove = [h.strip().lower() for h in self._remove_headers.split('\n') if h.strip()]
                if headers_to_remove:
                    new_headers = []
                    for header in headers:
                        if ':' in header:
                            header_name = header.split(':', 1)[0].strip().lower()
                            if header_name not in headers_to_remove:
                                new_headers.append(header)
                        else:
                            new_headers.append(header)
                    headers = new_headers
            
            # 处理替换请求头
            if self._enable_replace_headers and self._replace_headers.strip():
                replace_headers = {}
                for line in self._replace_headers.split('\n'):
                    line = line.strip()
                    if line and ':' in line:
                        # 解析格式：Header: new_value
                        header_name, new_value = line.split(':', 1)
                        replace_headers[header_name.strip().lower()] = line.strip()  # 保存完整的头部定义
                
                if replace_headers:
                    new_headers = []
                    replaced_headers = set()
                    
                    # 遍历现有头部，替换匹配的头部
                    for header in headers:
                        if ':' in header:
                            header_name = header.split(':', 1)[0].strip().lower()
                            if header_name in replace_headers:
                                # 直接替换整个头部
                                new_headers.append(replace_headers[header_name])
                                replaced_headers.add(header_name)
                            else:
                                new_headers.append(header)
                        else:
                            new_headers.append(header)
                    
                    # 添加未匹配到现有头部的新头部
                    for header_name, full_header in replace_headers.items():
                        if header_name not in replaced_headers:
                            new_headers.append(full_header)
                    
                    headers = new_headers
            
            # 处理添加请求头
            if self._enable_add_headers and self._add_headers.strip():
                headers_to_add = [h.strip() for h in self._add_headers.split('\n') if h.strip() and ':' in h]
                for header in headers_to_add:
                    # 检查是否已存在相同名称的头部
                    header_name = header.split(':', 1)[0].strip().lower()
                    existing = False
                    for i, existing_header in enumerate(headers):
                        if ':' in existing_header:
                            existing_name = existing_header.split(':', 1)[0].strip().lower()
                            if existing_name == header_name:
                                # 替换现有头部
                                headers[i] = header
                                existing = True
                                break
                    if not existing:
                        # 添加新头部
                        headers.append(header)
            
            # 重新组装请求
            modified_request = request_line + '\r\n'
            if headers:
                modified_request += '\r\n'.join(headers) + '\r\n'
            modified_request += '\r\n'
            if body:
                modified_request += body
            
            return modified_request.encode('iso-8859-1'), modified_url
            
        except Exception as e:
            self._callbacks.printError(u"修改请求时出错: %s" % e)
            return request_bytes, orig_url

    def _setup_column_comparators(self):
        """设置列排序比较器"""
        if not hasattr(self, '_row_sorter'):
            return
            
        # 获取可见列索引
        visible_cols = [i for i, visible in enumerate(self._visible_cols) if visible]
        
        # 为每个可见列设置合适的比较器
        for view_col, orig_col in enumerate(visible_cols):
            col_name = self.COLS[orig_col]
            
            if orig_col == 0:  # #列
                self._row_sorter.setComparator(view_col, self._NumberComparator())
            elif orig_col == 6:  # 状态码列
                self._row_sorter.setComparator(view_col, self._StatusComparator())
            elif orig_col == 7:  # 长度列
                self._row_sorter.setComparator(view_col, self._SizeComparator())
            elif orig_col == 15:  # 时间列
                self._row_sorter.setComparator(view_col, self._TimeComparator())
            # 其他列使用默认字符串比较
        
        # 处理重定向列
        if self._redirect_chk.isSelected():
            redirect_col = len(visible_cols)
            # 重定向列使用默认字符串比较

    # 自定义比较器类
    class _NumberComparator(Comparator):
        """数字比较器"""
        def compare(self, a, b):
            try:
                num_a = int(str(a)) if a != "-" else 0
                num_b = int(str(b)) if b != "-" else 0
                return num_a - num_b
            except:
                return str(a).compareTo(str(b))

    class _StatusComparator(Comparator):
        """状态码比较器"""
        def compare(self, a, b):
            try:
                # 处理 "-" 状态
                if a == "-" and b == "-":
                    return 0
                elif a == "-":
                    return 1  # "-" 排在数字后面
                elif b == "-":
                    return -1
                
                num_a = int(str(a))
                num_b = int(str(b))
                return num_a - num_b
            except:
                return str(a).compareTo(str(b))

    class _SizeComparator(Comparator):
        """大小比较器"""
        def compare(self, a, b):
            try:
                # 处理 "-" 大小
                if a == "-" and b == "-":
                    return 0
                elif a == "-":
                    return 1
                elif b == "-":
                    return -1
                
                num_a = int(str(a))
                num_b = int(str(b))
                return num_a - num_b
            except:
                return str(a).compareTo(str(b))

    class _TimeComparator(Comparator):
        """时间比较器"""
        def compare(self, a, b):
            try:
                # 处理 "-" 时间
                if a == "-" and b == "-":
                    return 0
                elif a == "-":
                    return 1
                elif b == "-":
                    return -1
                
                # 解析时间字符串
                def parse_time(time_str):
                    time_str = str(time_str).strip()
                    if time_str.endswith(" ms"):
                        return float(time_str[:-3])
                    elif time_str.endswith(" s"):
                        return float(time_str[:-2]) * 1000  # 转换为毫秒
                    else:
                        return 0
                
                time_a = parse_time(a)
                time_b = parse_time(b)
                
                if time_a < time_b:
                    return -1
                elif time_a > time_b:
                    return 1
                else:
                    return 0
            except:
                return str(a).compareTo(str(b))

# Burp Suite要求扩展主类名为BurpExtender，这里简单继承并复用实现
class BurpExtender(BulkRequester):
    def registerExtenderCallbacks(self, callbacks):
        super(BurpExtender, self).registerExtenderCallbacks(callbacks) 