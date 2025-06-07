# -*- encoding: utf-8 -*-
# TODO:@ModuleName: Request2Python
# TODO:@Author: tomato
# TODO:@Version: Python3.12.0
# TODO:@Time: 2025/06/03 19:09

from burp import IBurpExtender, IContextMenuFactory  # TODO: 导入Burp API接口相关类
from javax.swing import JMenuItem  # TODO: 导入Swing组件用于创建菜单项
from java.io import File  # TODO: 导入Java文件操作类
from java.awt.event import ActionListener  # TODO: 导入Java事件监听器
from javax.swing import JFileChooser  # TODO: 导入Swing的文件选择器
from java.io import FileOutputStream, OutputStreamWriter
from java.nio.charset import StandardCharsets
import base64
import sys

# TODO: 设置默认编码为UTF-8（针对Jython 2.7）
if hasattr(sys, 'setdefaultencoding'):
    reload(sys)
    sys.setdefaultencoding('utf-8')

try:
    import json
except ImportError:
    # TODO: Jython 2.7可能没有内置json模块，使用简单实现
    class SimpleJSON:
        @staticmethod
        def loads(s):
            # TODO: 简单的JSON解析，仅处理基本情况
            return eval(s)

        @staticmethod
        def dumps(obj, ensure_ascii=True, indent=None):
            def format_value(v):
                if isinstance(v, (str, unicode)):
                    # TODO: 确保字符串用双引号包围并转义，保持中文字符
                    escaped = unicode(v).replace(u'\\', u'\\\\').replace(u'"', u'\\"')
                    return u'"{}"'.format(escaped)
                elif isinstance(v, bool):
                    return u"true" if v else u"false"
                elif v is None:
                    return u"null"
                else:
                    return unicode(v)

            if isinstance(obj, dict):
                if not obj:
                    return u"{}"
                items = []
                for k, v in obj.items():
                    key_str = u'"{}"'.format(unicode(k).replace(u'"', u'\\"'))
                    val_str = format_value(v)
                    items.append(u'    {}: {}'.format(key_str, val_str))
                return u"{\n" + u",\n".join(items) + u"\n}"
            elif isinstance(obj, (list, tuple)):
                if not obj:
                    return u"[]"
                items = [format_value(item) for item in obj]
                return u"[" + u", ".join(items) + u"]"
            else:
                return format_value(obj)


    json = SimpleJSON()


# TODO: Burp插件类继承Burp
class BurpExtender(IBurpExtender, IContextMenuFactory):

    # TODO: Burp加载插件时注册菜单
    def registerExtenderCallbacks(self, callbacks):
        self._callbacks = callbacks
        self._helpers = callbacks.getHelpers()
        callbacks.setExtensionName("Request2Python")
        callbacks.registerContextMenuFactory(self)

    # TODO: 添加到右键使用扩展
    def createMenuItems(self, invocation):
        menu_item = JMenuItem("Export to Python")
        menu_item.addActionListener(ExportPythonAction(self, invocation))
        return [menu_item]

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

        code_lines = [
            u"# -*- coding: utf-8 -*-",
            u"import requests",
            u"",
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
                    u"params = {}".format(json.dumps(params['query'], ensure_ascii=False, indent=4)),
                    u"response = requests.{}(url, headers=headers, params=params)".format(request_method)
                ])
            else:
                # TODO: 如果URL已经包含参数或没有查询参数，则直接请求
                code_lines.extend([
                    u"",
                    u"response = requests.{}(url, headers=headers)".format(request_method)
                ])
        # TODO: 处理post请求
        elif method == "POST":
            if params["json"] is not None:
                code_lines.extend([
                    u"",
                    u"data = {}".format(json.dumps(params["json"], ensure_ascii=False, indent=4)),
                    u"",
                    u"response = requests.{}(url, headers=headers, json=data)".format(request_method)
                ])
            elif params["form"]:
                code_lines.extend([
                    u"",
                    u"data = {}".format(json.dumps(params["form"], ensure_ascii=False, indent=4)),
                    u"response = requests.{}(url, headers=headers, data=data)".format(request_method)
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
                            u"response = requests.{}(url, headers=headers, data=data.encode('utf-8'))".format(
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
                                u"response = requests.{}(url, headers=headers, data=data)".format(request_method)
                            ])
                        except Exception as e2:
                            code_lines.extend([
                                u"",
                                u"# 无法处理请求体数据: {}".format(self.safe_unicode(str(e2))),
                                u"response = requests.{}(url, headers=headers)".format(request_method)
                            ])
                else:
                    code_lines.extend([
                        u"",
                        u"response = requests.{}(url, headers=headers)".format(request_method)
                    ])
        else:
            # TODO: 处理其他http方法
            # TODO: 对于其他HTTP方法，也检查是否有查询参数
            url_has_params = "?" in url

            if params["query"] and not url_has_params:
                code_lines.extend([
                    u"",
                    u"params = {}".format(json.dumps(params['query'], ensure_ascii=False, indent=4)),
                    u"response = requests.{}(url, headers=headers, params=params)".format(request_method)
                ])
            else:
                code_lines.extend([
                    u"",
                    u"response = requests.{}(url, headers=headers)".format(request_method)
                ])

        # TODO: 添加打印响应的代码
        code_lines.extend([
            u"",
            u"print('[状态码]:', response.status_code)",
            u"print('[响应正文]:\\n', response.text)"
        ])

        return u'\n'.join(code_lines)

    # TODO: 保存生成的python代码 - 适配Jython 2.7
    def save_to_file(self, code):
        chooser = JFileChooser()
        chooser.setDialogTitle("保存Python脚本")
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

                self._callbacks.printOutput("保存成功!")
            except Exception as e:
                self._callbacks.printError("保存错误: " + str(e))


# TODO: 菜单点击处理
class ExportPythonAction(ActionListener):
    def __init__(self, extender, invocation):
        self.extender = extender
        self.invocation = invocation

    def actionPerformed(self, event):
        http_traffic = self.invocation.getSelectedMessages()
        if not http_traffic:
            self.extender._callbacks.printError("未选择请求!")
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
            self.extender._callbacks.printError("导出错误: " + str(e))


# TODO: 避免重复创建对象
if 'extender' not in globals():
    extender = BurpExtender()
