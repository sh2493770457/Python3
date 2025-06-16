# -*- encoding: utf-8 -*-
# TODO:@ModuleName: Request2Python
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/5/12 19:09

from burp import IBurpExtender, IContextMenuFactory, IMessageEditorTabFactory, \
    IMessageEditorTab  # TODO: 导入Burp API接口相关类
from javax.swing import JMenuItem, JTextArea, JScrollPane, JPopupMenu, \
    JMenuItem as JMenuItemCopy  # TODO: 导入Swing组件用于创建菜单项和文本区域
from java.io import File  # TODO: 导入Java文件操作类
from java.awt.event import ActionListener, MouseAdapter, MouseEvent  # TODO: 导入Java事件监听器
from javax.swing import JFileChooser  # TODO: 导入Swing的文件选择器
from java.io import FileOutputStream, OutputStreamWriter
from java.nio.charset import StandardCharsets
from java.awt import Font, Toolkit
from java.awt.datatransfer import StringSelection
import base64
import sys

# TODO: 设置默认编码为UTF-8（针对Jython 2.7）
if hasattr(sys, 'setdefaultencoding'):
    reload(sys)
    sys.setdefaultencoding('utf-8')

try:
    import json as system_json


    # TODO: 包装系统json模块，添加我们需要的功能
    class JSONWrapper:
        @staticmethod
        def loads(s):
            return system_json.loads(s)

        @staticmethod
        def dumps(obj, ensure_ascii=True, indent=None, convert_bool_to_string=False):
            if convert_bool_to_string:
                # TODO: 需要转换布尔值时，先处理对象
                processed_obj = JSONWrapper._convert_bool_to_string(obj)
                return system_json.dumps(processed_obj, ensure_ascii=ensure_ascii, indent=indent)
            else:
                return system_json.dumps(obj, ensure_ascii=ensure_ascii, indent=indent)

        @staticmethod
        def _convert_bool_to_string(obj):
            """递归转换对象中的布尔值为字符串"""
            if isinstance(obj, dict):
                result = {}
                for k, v in obj.items():
                    result[k] = JSONWrapper._convert_bool_to_string(v)
                return result
            elif isinstance(obj, (list, tuple)):
                return [JSONWrapper._convert_bool_to_string(item) for item in obj]
            elif isinstance(obj, bool):
                return "true" if obj else "false"
            else:
                return obj


    json = JSONWrapper()
except ImportError:
    # TODO: Jython 2.7可能没有内置json模块，使用简单实现
    class SimpleJSON:
        @staticmethod
        def loads(s):
            # TODO: 简单的JSON解析，仅处理基本情况
            return eval(s)

        @staticmethod
        def dumps(obj, ensure_ascii=True, indent=None, convert_bool_to_string=False):
            def format_value(v):
                if isinstance(v, (str, unicode)):
                    # TODO: 确保字符串用双引号包围并转义，保持中文字符
                    escaped = unicode(v).replace(u'\\', u'\\\\').replace(u'"', u'\\"')
                    return u'"{}"'.format(escaped)
                elif isinstance(v, bool):
                    if convert_bool_to_string:
                        # TODO: 为Python代码生成时，将布尔值转换为字符串
                        return u'"{}"'.format(u"true" if v else u"false")
                    else:
                        # TODO: 正常JSON格式
                        return u"true" if v else u"false"
                elif v is None:
                    return u"null"
                else:
                    return unicode(v)

            def format_dict(d):
                if not d:
                    return u"{}"
                items = []
                for k, v in d.items():
                    key_str = u'"{}"'.format(unicode(k).replace(u'"', u'\\"'))
                    if isinstance(v, dict):
                        val_str = format_dict(v)
                    elif isinstance(v, (list, tuple)):
                        val_str = format_list(v)
                    else:
                        val_str = format_value(v)
                    items.append(u'    {}: {}'.format(key_str, val_str))
                return u"{\n" + u",\n".join(items) + u"\n}"

            def format_list(l):
                if not l:
                    return u"[]"
                items = []
                for item in l:
                    if isinstance(item, dict):
                        items.append(format_dict(item))
                    elif isinstance(item, (list, tuple)):
                        items.append(format_list(item))
                    else:
                        items.append(format_value(item))
                return u"[" + u", ".join(items) + u"]"

            if isinstance(obj, dict):
                return format_dict(obj)
            elif isinstance(obj, (list, tuple)):
                return format_list(obj)
            else:
                return format_value(obj)


    json = SimpleJSON()


# TODO: Burp插件类继承Burp
class BurpExtender(IBurpExtender, IContextMenuFactory, IMessageEditorTabFactory):

    # TODO: Burp加载插件时注册菜单
    def registerExtenderCallbacks(self, callbacks):
        self._callbacks = callbacks
        self._helpers = callbacks.getHelpers()
        callbacks.setExtensionName("Request2Python")
        callbacks.registerContextMenuFactory(self)
        callbacks.registerMessageEditorTabFactory(self)

        # TODO: 从Burp配置中恢复导出路径
        self._export_path = self.load_export_path()

    # TODO: 加载导出路径配置
    def load_export_path(self):
        """从Burp扩展设置中加载导出路径"""
        try:
            # TODO: 从Burp的扩展设置中获取保存的路径
            saved_path = self._callbacks.loadExtensionSetting("export_path")
            if saved_path:
                # TODO: 验证路径是否存在
                from java.io import File
                if File(saved_path).exists():
                    self._callbacks.printOutput(u"已恢复导出路径: {}".format(saved_path).encode('utf-8'))
                    return saved_path
                else:
                    self._callbacks.printOutput(u"之前配置的路径不存在，已清除: {}".format(saved_path).encode('utf-8'))
                    # TODO: 清除无效的路径配置
                    self._callbacks.saveExtensionSetting("export_path", None)
            return None
        except Exception as e:
            self._callbacks.printError(u"加载配置错误: {}".format(self.safe_unicode(str(e))).encode('utf-8'))
            return None

    # TODO: 保存导出路径配置
    def save_export_path(self, path):
        """保存导出路径到Burp扩展设置中"""
        try:
            if path:
                self._callbacks.saveExtensionSetting("export_path", path)
                self._callbacks.printOutput(u"导出路径已保存: {}".format(path).encode('utf-8'))
            else:
                self._callbacks.saveExtensionSetting("export_path", None)
                self._callbacks.printOutput(u"导出路径配置已清除".encode('utf-8'))
        except Exception as e:
            self._callbacks.printError(u"保存配置错误: {}".format(self.safe_unicode(str(e))).encode('utf-8'))

    # TODO: 添加到右键使用扩展
    def createMenuItems(self, invocation):
        menu_items = []

        # TODO: 快速导出菜单项（如果已配置路径）
        if self._export_path:
            quick_export_item = JMenuItem(u"快速导出到Python")
            quick_export_item.addActionListener(QuickExportAction(self, invocation))
            menu_items.append(quick_export_item)

        # TODO: 普通导出菜单项
        export_item = JMenuItem(u"选择路径导出")
        export_item.addActionListener(ExportPythonAction(self, invocation))
        menu_items.append(export_item)

        # TODO: 新增API接口导出菜单项
        api_export_item = JMenuItem(u"导出API接口")
        api_export_item.addActionListener(ExportAPIAction(self, invocation))
        menu_items.append(api_export_item)

        # TODO: 路径相关菜单项
        if self._export_path:
            # TODO: 重新配置路径
            config_item = JMenuItem(u"重新配置导出路径")
            config_item.addActionListener(ConfigPathAction(self, invocation))
            menu_items.append(config_item)

            # TODO: 清除路径配置
            clear_item = JMenuItem(u"清除导出路径配置")
            clear_item.addActionListener(ClearPathAction(self, invocation))
            menu_items.append(clear_item)
        else:
            # TODO: 首次配置路径
            config_item = JMenuItem(u"配置导出路径")
            config_item.addActionListener(ConfigPathAction(self, invocation))
            menu_items.append(config_item)

        return menu_items

    # TODO: 创建消息编辑器标签页
    def createNewInstance(self, controller, editable):
        return PythonTab(self, controller, editable)

    # TODO: 安全的Unicode字符串处理
    def safe_unicode(self, s):
        """安全地将字符串转换为Unicode"""
        if s is None:
            return u""
        try:
            if isinstance(s, unicode):
                return s
            elif isinstance(s, str):
                return s.decode('utf-8')
            else:
                return unicode(s)
        except UnicodeDecodeError:
            try:
                return s.decode('latin1')
            except:
                return unicode(str(s), errors='ignore')

    # TODO: 从字节数组正确解码UTF-8字符串
    def bytes_to_utf8_string(self, body_bytes):
        """正确处理字节数组到UTF-8字符串的转换"""
        try:
            # TODO: 方法1: 如果是Java字节数组，直接转换
            if hasattr(body_bytes, '__len__') and hasattr(body_bytes, '__getitem__'):
                # TODO: 将Java字节数组转换为Python字符串
                byte_list = []
                for i in range(len(body_bytes)):
                    byte_val = body_bytes[i]
                    # TODO: 处理Java的有符号字节到无符号字节的转换
                    if byte_val < 0:
                        byte_val = byte_val + 256
                    byte_list.append(chr(byte_val))

                # TODO: 将字节列表连接成字符串，然后用UTF-8解码
                byte_string = ''.join(byte_list)
                return byte_string.decode('utf-8')

            # TODO: 方法2: 使用Burp的helper方法作为后备
            else:
                return self._helpers.bytesToString(body_bytes)

        except Exception as e:
            # TODO: 如果UTF-8解码失败，尝试其他方法
            try:
                return self._helpers.bytesToString(body_bytes)
            except:
                return str(body_bytes)

    # TODO: 构建请求的完整URL，包含协议、主机、端口、路径和查询参数
    def build_correct_url(self, http_service, request_info):
        url_obj = request_info.getUrl()
        protocol = url_obj.getProtocol()
        host = url_obj.getHost()
        port = url_obj.getPort()
        path = url_obj.getPath()
        query = url_obj.getQuery()

        port_str = ""
        if (protocol == "http" and port not in (-1, 80)) or (protocol == "https" and port not in (-1, 443)):
            port_str = ":{0}".format(port)

        full_path = path if query is None else path + "?" + query
        return "{0}://{1}{2}{3}".format(protocol, host, port_str, full_path)

    # TODO: 解析请求参数，包括URL参数、表单数据、JSON和原始数据
    def parse_parameters(self, request_info, body_bytes):
        params = {
            "query": {},
            "form": {},
            "json": None,
            "raw_body": body_bytes
        }

        for param in request_info.getParameters():
            if param.getType() == param.PARAM_URL:
                params["query"][param.getName()] = param.getValue()

        content_type = ""
        headers = request_info.getHeaders()
        for h in headers:
            if h.lower().startswith("content-type:"):
                content_type = h.split(":", 1)[1].strip()
                break

        if body_bytes:
            # TODO: 处理表单类型请求体
            if "application/x-www-form-urlencoded" in content_type.lower():
                for param in request_info.getParameters():
                    if param.getType() == param.PARAM_BODY:
                        params["form"][param.getName()] = param.getValue()

            # TODO: 处理JSON类型请求体 - 修复编码问题
            elif "application/json" in content_type.lower():
                try:
                    # TODO: 正确处理字节数组到UTF-8字符串的转换
                    body_str = self.bytes_to_utf8_string(body_bytes)
                    # TODO: 确保是Unicode字符串
                    body_unicode = self.safe_unicode(body_str)
                    # TODO: 解析JSON
                    params["json"] = json.loads(body_unicode)
                except Exception as e:
                    self._callbacks.printError("JSON解析错误: " + str(e))
                    # TODO: 尝试另一种方法
                    try:
                        # TODO: 直接使用原始方法作为后备
                        body_str = self._helpers.bytesToString(body_bytes)
                        params["json"] = json.loads(body_str)
                    except:
                        params["json"] = None

        return params

    # TODO: 根据解析生成Python代码
    def generate_code(self, method, url, headers, params):
        headers_dict = {}
        for header in headers:
            if ":" in header and not header.lower().startswith(("host:", "content-length")):
                key, val = header.split(":", 1)
                headers_dict[self.safe_unicode(key.strip())] = self.safe_unicode(val.strip())

        # TODO: 获取当前时间
        try:
            from datetime import datetime
            current_time = datetime.now().strftime("%Y/%m/%d %H:%M")
        except:
            # TODO: Jython 2.7 fallback
            import time
            current_time = time.strftime("%Y/%m/%d %H:%M")

        code_lines = [
            u"# -*- encoding: utf-8 -*-",
            u"# TODO:@ModuleName: ",
            u"# TODO:@Author: tomato",
            u"# TODO:@Version: Python3.12.0",
            u"# TODO:@Time: {}".format(current_time),
            u"import urllib3",
            u"import requests",
            u"",
            u"urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)",
            u'url = "{}"'.format(self.safe_unicode(url))
        ]

        # TODO: 构造请求头字典headers
        if headers_dict:
            code_lines.append(u"")
            code_lines.append(u"headers = {")
            header_lines = []
            for k, v in headers_dict.items():
                # TODO: 转义双引号
                escaped_key = k.replace(u'"', u'\\"')
                escaped_val = v.replace(u'"', u'\\"')
                header_lines.append(u'    "{}": "{}"'.format(escaped_key, escaped_val))
            code_lines.append(u",\n".join(header_lines))
            code_lines.append(u"}")

        request_method = method.lower()

        # TODO: 处理get请求 - 修复重复参数问题
        if method == "GET":
            # TODO: 检查URL是否已经包含查询参数
            url_has_params = "?" in url

            if params["query"] and not url_has_params:
                # TODO: 如果有查询参数但URL中没有，则使用params参数
                code_lines.extend([
                    u"",
                    u"params = {}".format(
                        json.dumps(params['query'], ensure_ascii=False, indent=4, convert_bool_to_string=True)),
                    u"response = requests.{}(url, headers=headers, params=params, verify=False)".format(request_method)
                ])
            else:
                # TODO: 如果URL已经包含参数或没有查询参数，则直接请求
                code_lines.extend([
                    u"",
                    u"response = requests.{}(url, headers=headers, verify=False)".format(request_method)
                ])
        # TODO: 处理post请求
        elif method == "POST":
            if params["json"] is not None:
                code_lines.extend([
                    u"",
                    u"data = {}".format(
                        json.dumps(params["json"], ensure_ascii=False, indent=4, convert_bool_to_string=True)),
                    u"",
                    u"response = requests.{}(url, headers=headers, json=data, verify=False)".format(request_method)
                ])
            elif params["form"]:
                code_lines.extend([
                    u"",
                    u"data = {}".format(
                        json.dumps(params["form"], ensure_ascii=False, indent=4, convert_bool_to_string=True)),
                    u"response = requests.{}(url, headers=headers, data=data, verify=False)".format(request_method)
                ])
            else:
                # TODO: 处理原始,二进制数据请求体
                raw_body = params["raw_body"]
                if raw_body:
                    try:
                        # TODO: 使用改进的UTF-8转换方法
                        body_unicode = self.bytes_to_utf8_string(raw_body)

                        code_lines.extend([
                            u"",
                            u'data = "{}"'.format(body_unicode.replace(u'"', u'\\"')),
                            u"response = requests.{}(url, headers=headers, data=data.encode('utf-8'), verify=False)".format(
                                request_method)
                        ])
                    except Exception as e:
                        # TODO: 如果无法转换，使用base64
                        try:
                            # TODO: 在Jython中处理字节数组
                            if hasattr(raw_body, '__iter__'):
                                # TODO: 如果是Java字节数组，转换为Python字节
                                raw_bytes = ''.join(chr(b & 0xFF) for b in raw_body)
                            else:
                                raw_bytes = str(raw_body)

                            encoded = base64.b64encode(raw_bytes).decode('ascii')
                            code_lines.extend([
                                u"",
                                u"import base64",
                                u"data = base64.b64decode('{}')".format(encoded),
                                u"response = requests.{}(url, headers=headers, data=data, verify=False)".format(request_method)
                            ])
                        except Exception as e2:
                            code_lines.extend([
                                u"",
                                u"# 无法处理请求体数据: {}".format(self.safe_unicode(str(e2))),
                                u"response = requests.{}(url, headers=headers, verify=False)".format(request_method)
                            ])
                else:
                    code_lines.extend([
                        u"",
                        u"response = requests.{}(url, headers=headers, verify=False)".format(request_method)
                    ])
        else:
            # TODO: 处理其他http方法
            # TODO: 对于其他HTTP方法，也检查是否有查询参数
            url_has_params = "?" in url

            if params["query"] and not url_has_params:
                code_lines.extend([
                    u"",
                    u"params = {}".format(
                        json.dumps(params['query'], ensure_ascii=False, indent=4, convert_bool_to_string=True)),
                    u"response = requests.{}(url, headers=headers, params=params, verify=False)".format(request_method)
                ])
            else:
                code_lines.extend([
                    u"",
                    u"response = requests.{}(url, headers=headers, verify=False)".format(request_method)
                ])

        # TODO: 添加打印响应的代码
        code_lines.extend([
            u"",
            u"response.encoding = 'utf-8'",
            u"print('[状态码]:', response.status_code)",
            u"print('[响应正文]:\\n', response.text)"
        ])

        # TODO: 确保返回Unicode字符串
        result = u'\n'.join(code_lines)
        return result

    # TODO: 保存生成的python代码 - 适配Jython 2.7
    def save_to_file(self, code):
        chooser = JFileChooser()
        chooser.setDialogTitle(u"保存Python脚本")
        chooser.setSelectedFile(File("request.py"))

        if chooser.showSaveDialog(None) == JFileChooser.APPROVE_OPTION:
            try:
                # TODO: 在Jython中使用Java IO来正确处理UTF-8编码
                file_path = chooser.getSelectedFile().getPath()
                fos = FileOutputStream(file_path)
                writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)

                # TODO: 确保代码是Unicode字符串
                if isinstance(code, str):
                    code = code.decode('utf-8')

                writer.write(code)
                writer.close()
                fos.close()

                self._callbacks.printOutput(u"保存成功!".encode('utf-8'))
            except Exception as e:
                self._callbacks.printError(u"保存错误: {}".format(self.safe_unicode(str(e))).encode('utf-8'))

    # TODO: 快速保存到配置路径
    def quick_save_to_file(self, code, method, url):
        if not self._export_path:
            self._callbacks.printError(u"未配置导出路径!".encode('utf-8'))
            return False

        try:
            # TODO: 生成文件名：使用当前时间和请求方法
            try:
                from datetime import datetime
                timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            except:
                import time
                timestamp = time.strftime("%Y%m%d_%H%M%S")

            # TODO: 从URL中提取主机名作为文件名的一部分
            import re
            try:
                host_match = re.search(r'://([^/]+)', url)
                if host_match:
                    host = host_match.group(1).replace(':', '_').replace('.', '_')
                else:
                    host = "unknown"
            except:
                host = "request"

            filename = "{}_{}_{}.py".format(method.lower(), host, timestamp)
            file_path = File(self._export_path, filename).getPath()

            # TODO: 在Jython中使用Java IO来正确处理UTF-8编码
            fos = FileOutputStream(file_path)
            writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)

            # TODO: 确保代码是Unicode字符串
            if isinstance(code, str):
                code = code.decode('utf-8')

            writer.write(code)
            writer.close()
            fos.close()

            self._callbacks.printOutput(u"快速导出成功: {}".format(filename).encode('utf-8'))
            return True
        except Exception as e:
            self._callbacks.printError(u"快速导出错误: {}".format(self.safe_unicode(str(e))).encode('utf-8'))
            return False

    # TODO: 配置导出路径
    def configure_export_path(self):
        chooser = JFileChooser()
        chooser.setDialogTitle(u"选择导出路径")
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)

        # TODO: 如果已有配置路径，设置为默认目录
        if self._export_path:
            chooser.setCurrentDirectory(File(self._export_path))

        if chooser.showOpenDialog(None) == JFileChooser.APPROVE_OPTION:
            self._export_path = chooser.getSelectedFile().getPath()
            # TODO: 持久化保存路径配置
            self.save_export_path(self._export_path)
            return True
        return False

    # TODO: 清除导出路径配置
    def clear_export_path(self):
        """清除导出路径配置"""
        self._export_path = None
        self.save_export_path(None)
        return True

    # TODO: 提取API接口数据
    def extract_api_data(self, http_messages):
        """从多个HTTP消息中提取API接口数据，返回{url: data}格式的字典"""
        api_data = {}

        for message in http_messages:
            try:
                http_service = message.getHttpService()
                request_info = self._helpers.analyzeRequest(message)
                body_bytes = message.getRequest()[request_info.getBodyOffset():]

                # TODO: 构建完整URL
                url = self.build_correct_url(http_service, request_info)

                # TODO: 获取请求方法
                method = request_info.getMethod()

                # TODO: 获取请求体数据
                request_data = None
                if method.upper() == "GET":
                    # TODO: GET请求设置为None
                    request_data = None
                else:
                    # TODO: 解析请求体
                    if body_bytes and len(body_bytes) > 0:
                        # TODO: 获取Content-Type
                        content_type = ""
                        headers = request_info.getHeaders()
                        for h in headers:
                            if h.lower().startswith("content-type:"):
                                content_type = h.split(":", 1)[1].strip()
                                break

                        # TODO: 根据Content-Type处理请求体
                        if "application/json" in content_type.lower():
                            try:
                                # TODO: JSON请求体转换为字符串格式，直接保存为_json_body字段
                                body_str = self.bytes_to_utf8_string(body_bytes)
                                body_unicode = self.safe_unicode(body_str)
                                # TODO: 按照示例格式，JSON请求体保存为{"_json_body": "原始JSON字符串"}
                                request_data = {"_json_body": body_unicode}
                            except Exception as e:
                                self._callbacks.printError(
                                    u"JSON解析错误: {}".format(self.safe_unicode(str(e))).encode('utf-8'))
                                request_data = None
                        else:
                            # TODO: 其他类型的请求体（包括表单数据），都视为有请求体，设置为空字典
                            try:
                                body_str = self.bytes_to_utf8_string(body_bytes)
                                # TODO: 如果有请求体内容，保存原始字符串；如果是空的，使用空字典表示有请求体但内容为空
                                if body_str.strip():
                                    request_data = {"_raw_body": body_str}
                                else:
                                    request_data = {}
                            except Exception as e:
                                request_data = {}
                    else:
                        # TODO: 空请求体
                        request_data = None

                # TODO: 添加到结果字典中
                api_data[url] = request_data

            except Exception as e:
                self._callbacks.printError(u"提取API数据错误: {}".format(self.safe_unicode(str(e))).encode('utf-8'))
                continue

        return api_data

    # TODO: 保存API接口数据到文件
    def save_api_data_to_file(self, api_data):
        """保存API接口数据到JSON文件"""
        chooser = JFileChooser()
        chooser.setDialogTitle(u"保存API接口数据")

        # TODO: 生成默认文件名（带时间戳）
        try:
            from datetime import datetime
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        except:
            import time
            timestamp = time.strftime("%Y%m%d_%H%M%S")

        default_filename = "api_{}.json".format(timestamp)
        chooser.setSelectedFile(File(default_filename))

        if chooser.showSaveDialog(None) == JFileChooser.APPROVE_OPTION:
            try:
                file_path = chooser.getSelectedFile().getPath()
                fos = FileOutputStream(file_path)
                writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)

                # TODO: 转换为JSON格式
                json_str = json.dumps(api_data, ensure_ascii=False, indent=2)

                # TODO: 确保是Unicode字符串
                if isinstance(json_str, str):
                    json_str = json_str.decode('utf-8')

                writer.write(json_str)
                writer.close()
                fos.close()

                self._callbacks.printOutput(u"API接口数据保存成功: {}".format(file_path).encode('utf-8'))
                self._callbacks.printOutput(u"共导出 {} 个API接口".format(len(api_data)).encode('utf-8'))
                return True
            except Exception as e:
                self._callbacks.printError(u"保存API数据错误: {}".format(self.safe_unicode(str(e))).encode('utf-8'))
                return False
        return False


# TODO: Python代码预览标签页类
class PythonTab(IMessageEditorTab):
    def __init__(self, extender, controller, editable):
        self.extender = extender
        self.controller = controller
        self.editable = editable
        self.current_message = None

        # TODO: 创建文本区域用于显示Python代码
        self.text_area = JTextArea()
        # TODO: 设置支持中文的字体
        font_names = ["Monospaced", "Dialog", "SansSerif", "Serif"]
        font_set = False
        for font_name in font_names:
            try:
                test_font = Font(font_name, Font.PLAIN, 12)
                self.text_area.setFont(test_font)
                font_set = True
                break
            except:
                continue

        if not font_set:
            # TODO: 最后的备用方案
            self.text_area.setFont(Font(None, Font.PLAIN, 12))

        # TODO: 设置文本区域属性
        self.text_area.setEditable(True)
        self.text_area.setLineWrap(False)
        self.text_area.setWrapStyleWord(False)

        # TODO: 设置初始文本以确保字符编码正确
        self.text_area.setText(u"# 等待请求数据...")

        # TODO: 添加滚动面板
        self.scroll_pane = JScrollPane(self.text_area)

        # TODO: 添加右键菜单
        self.create_popup_menu()

    def safe_display_text(self, text):
        """安全显示文本，确保中文字符正确显示"""
        try:
            if text is None:
                return u"# 无内容"

            # TODO: 确保文本是Unicode格式
            if isinstance(text, str):
                try:
                    # TODO: 尝试解码为Unicode
                    unicode_text = text.decode('utf-8')
                except:
                    try:
                        unicode_text = text.decode('gbk')
                    except:
                        unicode_text = unicode(text, errors='ignore')
            else:
                unicode_text = unicode(text)

            return unicode_text
        except Exception as e:
            return u"# 文本显示错误: {}".format(unicode(str(e)))

    def create_popup_menu(self):
        """创建右键菜单"""
        popup_menu = JPopupMenu()

        # TODO: 复制菜单项 - 使用Unicode字符串
        copy_item = JMenuItemCopy(u"复制")
        copy_item.addActionListener(CopyAction(self.text_area))
        popup_menu.add(copy_item)

        # TODO: 全选菜单项
        select_all_item = JMenuItemCopy(u"全选")
        select_all_item.addActionListener(SelectAllAction(self.text_area))
        popup_menu.add(select_all_item)

        popup_menu.addSeparator()

        # TODO: 快速保存菜单项（如果已配置路径）
        if self.extender._export_path:
            quick_save_item = JMenuItemCopy(u"快速保存")
            quick_save_item.addActionListener(QuickSaveTabAction(self.extender, self.text_area, self))
            popup_menu.add(quick_save_item)

        # TODO: 保存到文件菜单项
        save_item = JMenuItemCopy(u"保存到文件...")
        save_item.addActionListener(SaveFileAction(self.extender, self.text_area))
        popup_menu.add(save_item)

        popup_menu.addSeparator()

        # TODO: 配置导出路径菜单项
        if self.extender._export_path:
            config_path_item = JMenuItemCopy(u"重新配置导出路径")
            config_path_item.addActionListener(ConfigPathTabAction(self.extender, self))
            popup_menu.add(config_path_item)

            # TODO: 显示当前路径菜单项
            current_path_item = JMenuItemCopy(u"当前路径: {}".format(self.extender._export_path[:30] + "..." if len(
                self.extender._export_path) > 30 else self.extender._export_path))
            current_path_item.setEnabled(False)  # TODO: 仅用于显示，不可点击
            popup_menu.add(current_path_item)

            # TODO: 清除路径配置菜单项
            clear_path_item = JMenuItemCopy(u"清除导出路径配置")
            clear_path_item.addActionListener(ClearPathTabAction(self.extender, self))
            popup_menu.add(clear_path_item)
        else:
            config_path_item = JMenuItemCopy(u"配置导出路径")
            config_path_item.addActionListener(ConfigPathTabAction(self.extender, self))
            popup_menu.add(config_path_item)

        # TODO: 刷新代码菜单项
        refresh_item = JMenuItemCopy(u"刷新代码")
        refresh_item.addActionListener(RefreshCodeAction(self))
        popup_menu.add(refresh_item)

        # TODO: 添加鼠标监听器显示右键菜单
        self.text_area.addMouseListener(PopupMouseListener(popup_menu))

    def getTabCaption(self):
        """返回标签页标题"""
        return "Python"

    def getUiComponent(self):
        """返回UI组件"""
        return self.scroll_pane

    def isEnabled(self, content, isRequest):
        """确定是否显示此标签页"""
        return isRequest  # TODO: 只在请求标签页中显示

    def setMessage(self, content, isRequest):
        """设置消息内容并生成Python代码"""
        if content is None:
            self.text_area.setText(u"# 无请求数据")
            self.current_message = None
            return

        # TODO: 检查内容是否真的发生了变化
        if content == self.current_message:
            return

        self.current_message = content

        try:
            # TODO: 解析请求
            request_info = self.extender._helpers.analyzeRequest(content)
            body_bytes = content[request_info.getBodyOffset():]
            headers = list(request_info.getHeaders())
            method = request_info.getMethod()

            # TODO: 手动构建URL - 避免使用request_info.getUrl()
            host = "example.com"  # TODO: 默认值
            port = 443
            protocol = "https"
            path = "/"
            query = None

            # TODO: 从Host头部提取主机信息
            for header in headers:
                if header.lower().startswith("host:"):
                    host_value = header.split(":", 1)[1].strip()
                    if ":" in host_value:
                        host, port_str = host_value.split(":", 1)
                        try:
                            port = int(port_str)
                        except:
                            port = 443
                    else:
                        host = host_value
                    break

            # TODO: 从请求行中提取路径和查询参数
            if headers and len(headers) > 0:
                request_line = headers[0]  # TODO: 第一行是请求行，如 "GET /path?query=value HTTP/1.1"
                parts = request_line.split(" ")
                if len(parts) >= 2:
                    full_path = parts[1]
                    if "?" in full_path:
                        path, query = full_path.split("?", 1)
                    else:
                        path = full_path

            # TODO: 构建完整URL
            if query:
                url = "{0}://{1}:{2}{3}?{4}".format(protocol, host, port, path, query)
            else:
                url = "{0}://{1}:{2}{3}".format(protocol, host, port, path)

            # TODO: 如果端口是默认端口，则移除端口号
            if (protocol == "https" and port == 443) or (protocol == "http" and port == 80):
                if query:
                    url = "{0}://{1}{2}?{3}".format(protocol, host, path, query)
                else:
                    url = "{0}://{1}{2}".format(protocol, host, path)

            # TODO: 解析参数
            params = self.extender.parse_parameters(request_info, body_bytes)

            # TODO: 生成Python代码
            code = self.extender.generate_code(method, url, headers, params)

            # TODO: 安全显示代码，确保中文字符正确
            display_code = self.safe_display_text(code)

            # TODO: 显示代码
            self.text_area.setText(display_code)
            self.text_area.setCaretPosition(0)  # TODO: 滚动到顶部

        except Exception as e:
            error_msg = u"# 生成Python代码时出错:\n# {}\n\n# 请检查请求格式".format(self.extender.safe_unicode(str(e)))
            self.text_area.setText(error_msg)

    def getMessage(self):
        """返回当前消息（此标签页不修改原始消息）"""
        return self.current_message

    def isModified(self):
        """标签页内容是否被修改"""
        return False

    def getSelectedData(self):
        """返回选中的数据"""
        selected = self.text_area.getSelectedText()
        if selected:
            return selected.encode('utf-8')
        return None

    def refresh_if_needed(self, new_content):
        """检查内容是否变化，如果变化则刷新显示"""
        if new_content != self.current_message:
            self.setMessage(new_content, True)


# TODO: 右键菜单事件处理类
class PopupMouseListener(MouseAdapter):
    def __init__(self, popup_menu):
        self.popup_menu = popup_menu

    def mousePressed(self, event):
        self.show_popup(event)

    def mouseReleased(self, event):
        self.show_popup(event)

    def show_popup(self, event):
        if event.isPopupTrigger():
            self.popup_menu.show(event.getComponent(), event.getX(), event.getY())


# TODO: 复制操作
class CopyAction(ActionListener):
    def __init__(self, text_area):
        self.text_area = text_area

    def actionPerformed(self, event):
        selected_text = self.text_area.getSelectedText()
        if selected_text:
            # TODO: 复制到剪贴板
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
            clipboard.setContents(StringSelection(selected_text), None)


# TODO: 全选操作
class SelectAllAction(ActionListener):
    def __init__(self, text_area):
        self.text_area = text_area

    def actionPerformed(self, event):
        self.text_area.selectAll()


# TODO: 保存到文件操作
class SaveFileAction(ActionListener):
    def __init__(self, extender, text_area):
        self.extender = extender
        self.text_area = text_area

    def actionPerformed(self, event):
        code = self.text_area.getText()
        if code:
            self.extender.save_to_file(code)


# TODO: 刷新代码操作
class RefreshCodeAction(ActionListener):
    def __init__(self, python_tab):
        self.python_tab = python_tab

    def actionPerformed(self, event):
        if self.python_tab.current_message:
            self.python_tab.setMessage(self.python_tab.current_message, True)


# TODO: 标签页快速保存操作
class QuickSaveTabAction(ActionListener):
    def __init__(self, extender, text_area, python_tab):
        self.extender = extender
        self.text_area = text_area
        self.python_tab = python_tab

    def actionPerformed(self, event):
        code = self.text_area.getText()
        if code and self.python_tab.current_message:
            try:
                # TODO: 解析当前消息获取方法和URL
                request_info = self.extender._helpers.analyzeRequest(self.python_tab.current_message)
                method = request_info.getMethod()

                # TODO: 从请求头中提取URL信息
                headers = list(request_info.getHeaders())
                url = "unknown"
                for header in headers:
                    if header.lower().startswith("host:"):
                        host = header.split(":", 1)[1].strip()
                        url = "https://{}".format(host)
                        break

                self.extender.quick_save_to_file(code, method, url)
            except Exception as e:
                self.extender._callbacks.printError(
                    u"快速保存错误: {}".format(self.extender.safe_unicode(str(e))).encode('utf-8'))


# TODO: 标签页配置路径操作
class ConfigPathTabAction(ActionListener):
    def __init__(self, extender, python_tab):
        self.extender = extender
        self.python_tab = python_tab

    def actionPerformed(self, event):
        if self.extender.configure_export_path():
            # TODO: 配置成功后重新创建右键菜单以显示快速保存选项
            self.python_tab.create_popup_menu()


# TODO: 标签页清除路径配置操作
class ClearPathTabAction(ActionListener):
    def __init__(self, extender, python_tab):
        self.extender = extender
        self.python_tab = python_tab

    def actionPerformed(self, event):
        if self.extender.clear_export_path():
            # TODO: 清除成功后重新创建右键菜单以隐藏快速保存选项
            self.python_tab.create_popup_menu()


# TODO: 菜单点击处理 - 普通导出（弹出文件选择对话框）
class ExportPythonAction(ActionListener):
    def __init__(self, extender, invocation):
        self.extender = extender
        self.invocation = invocation

    def actionPerformed(self, event):
        http_traffic = self.invocation.getSelectedMessages()
        if not http_traffic:
            self.extender._callbacks.printError(u"未选择请求!".encode('utf-8'))
            return

        try:
            http_service = http_traffic[0].getHttpService()
            request_info = self.extender._helpers.analyzeRequest(http_traffic[0])
            body_bytes = http_traffic[0].getRequest()[request_info.getBodyOffset():]

            method = request_info.getMethod()
            url = self.extender.build_correct_url(http_service, request_info)
            headers = list(request_info.getHeaders())

            params = self.extender.parse_parameters(request_info, body_bytes)
            code = self.extender.generate_code(method, url, headers, params)

            self.extender.save_to_file(code)

        except Exception as e:
            self.extender._callbacks.printError(
                u"导出错误: {}".format(self.extender.safe_unicode(str(e))).encode('utf-8'))


# TODO: 快速导出处理 - 直接导出到配置路径
class QuickExportAction(ActionListener):
    def __init__(self, extender, invocation):
        self.extender = extender
        self.invocation = invocation

    def actionPerformed(self, event):
        http_traffic = self.invocation.getSelectedMessages()
        if not http_traffic:
            self.extender._callbacks.printError(u"未选择请求!".encode('utf-8'))
            return

        try:
            http_service = http_traffic[0].getHttpService()
            request_info = self.extender._helpers.analyzeRequest(http_traffic[0])
            body_bytes = http_traffic[0].getRequest()[request_info.getBodyOffset():]

            method = request_info.getMethod()
            url = self.extender.build_correct_url(http_service, request_info)
            headers = list(request_info.getHeaders())

            params = self.extender.parse_parameters(request_info, body_bytes)
            code = self.extender.generate_code(method, url, headers, params)

            self.extender.quick_save_to_file(code, method, url)

        except Exception as e:
            self.extender._callbacks.printError(
                u"快速导出错误: {}".format(self.extender.safe_unicode(str(e))).encode('utf-8'))


# TODO: 配置导出路径处理
class ConfigPathAction(ActionListener):
    def __init__(self, extender, invocation):
        self.extender = extender
        self.invocation = invocation

    def actionPerformed(self, event):
        self.extender.configure_export_path()


# TODO: 清除导出路径配置处理
class ClearPathAction(ActionListener):
    def __init__(self, extender, invocation):
        self.extender = extender
        self.invocation = invocation

    def actionPerformed(self, event):
        self.extender.clear_export_path()


# TODO: 导出API接口处理
class ExportAPIAction(ActionListener):
    def __init__(self, extender, invocation):
        self.extender = extender
        self.invocation = invocation

    def actionPerformed(self, event):
        http_traffic = self.invocation.getSelectedMessages()
        if not http_traffic:
            self.extender._callbacks.printError(u"未选择请求!".encode('utf-8'))
            return

        try:
            # TODO: 提取所有选中请求的API数据
            api_data = self.extender.extract_api_data(http_traffic)

            if not api_data:
                self.extender._callbacks.printError(u"没有可导出的API数据!".encode('utf-8'))
                return

            # TODO: 保存API数据到文件
            if self.extender.save_api_data_to_file(api_data):
                self.extender._callbacks.printOutput(u"API接口导出完成!".encode('utf-8'))

        except Exception as e:
            self.extender._callbacks.printError(
                u"API接口导出错误: {}".format(self.extender.safe_unicode(str(e))).encode('utf-8'))


# TODO: 避免重复创建对象
if 'extender' not in globals():
    extender = BurpExtender()
