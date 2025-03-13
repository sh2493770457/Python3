# -*- coding: utf-8 -*-
from burp import IBurpExtender, IContextMenuFactory
from javax.swing import JMenuItem
from java.io import File
from java.awt.event import ActionListener
from javax.swing import JFileChooser
import base64

class BurpExtender(IBurpExtender, IContextMenuFactory):

    def registerExtenderCallbacks(self, callbacks):
        self._callbacks = callbacks
        self._helpers = callbacks.getHelpers()
        callbacks.setExtensionName("Request2Python")
        callbacks.registerContextMenuFactory(self)

    def createMenuItems(self, invocation):
        menu_item = JMenuItem("Export to Python")
        menu_item.addActionListener(ExportPythonAction(self, invocation))
        return [menu_item]

    def build_correct_url(self, http_service, request_info):
        """正确构建完整URL"""
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
            # 处理表单数据
            if "application/x-www-form-urlencoded" in content_type.lower():
                for param in request_info.getParameters():
                    if param.getType() == param.PARAM_BODY:
                        params["form"][param.getName()] = param.getValue()
            
            # 处理JSON数据
            elif "application/json" in content_type.lower():
                try:
                    params["json"] = self._helpers.bytesToString(body_bytes)
                except:
                    params["json"] = None

        return params

    def generate_code(self, method, url, headers, params):
        headers_dict = {}
        for header in headers:
            if ":" in header and not header.lower().startswith(("host:", "content-length")):
                key, val = header.split(":", 1)
                headers_dict[key.strip()] = val.strip()

        code = [
            "import requests",
            "",
            "url = '{}'".format(url),
            "headers = {"
        ]

        headers_str = ",\n    ".join(["'{}': '{}'".format(k, v.replace("'", "\\'")) for k, v in headers_dict.items()])
        code.append("    " + headers_str + "\n}")

        request_method = method.lower()
        if method == "GET" and params["query"]:
            code.extend([
                "",
                "params = " + str(params['query']),
                "response = requests.{0}(url, headers=headers, params=params)".format(request_method)
            ])
        elif method == "POST":
            if params["json"] is not None:
                code.insert(1, "import json")
                json_str = params["json"].replace('"""', '\\"\\"\\"')
                code.extend([
                    "",
                    "payload = json.loads(r\"\"\"{}\"\"\")".format(json_str),
                    "response = requests.{0}(url, headers=headers, json=payload)".format(request_method)
                ])
            elif params["form"]:
                data_str = "{\n    " + ",\n    ".join("'{}': '{}'".format(k, v.replace("'", "\\'")) for k, v in params["form"].items()) + "\n}"
                code.extend([
                    "",
                    "data = " + data_str,
                    "response = requests.{0}(url, headers=headers, data=data)".format(request_method)
                ])
            else:
                # 处理二进制数据
                raw_body = params["raw_body"]
                if raw_body:
                    code.extend([
                        "",
                        "import base64",
                        "data = base64.b64decode('{}')".format(base64.b64encode(raw_body).decode('ascii')),
                        "response = requests.{0}(url, headers=headers, data=data)".format(request_method)
                    ])
                else:
                    code.extend([
                        "",
                        "response = requests.{0}(url, headers=headers)".format(request_method)
                    ])
        else:
            code.append("response = requests.{0}(url, headers=headers)".format(request_method))

        code.extend([
            "\nprint('[状态码]:', response.status_code)",
            "print('[响应正文]:\\n', response.text)"
        ])

        return '\n'.join(code)

    def save_to_file(self, code):
        chooser = JFileChooser()
        chooser.setDialogTitle("保存Python脚本")
        chooser.setSelectedFile(File("request.py"))

        if chooser.showSaveDialog(None) == JFileChooser.APPROVE_OPTION:
            try:
                with open(chooser.getSelectedFile().getPath(), "w") as f:
                    f.write(code)
                self._callbacks.printOutput("保存成功!")
            except Exception as e:
                self._callbacks.printError("保存错误: " + str(e))


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


if 'extender' not in globals():
    extender = BurpExtender()