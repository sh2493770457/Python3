import sys
import requests
import json
from PyQt5.QtWidgets import QTextEdit, QApplication, QMessageBox, QVBoxLayout, QWidget, QPushButton
from PyQt5.QtCore import Qt
from PyQt5 import QtGui, QtWidgets

class EnterTextEdit(QTextEdit):
    def __init__(self, parent=None):
        super().__init__(parent)

    def keyPressEvent(self, event):
        # 检查是否按下了 Enter 键
        if event.key() == Qt.Key_Return or event.key() == Qt.Key_Enter:
            # 如果是 Enter 键，调用父类的 translate_text 方法
            self.parent().translate_text()
        elif event.key() == Qt.Key_Escape:
            # 如果是 Esc 键，调用父类的 confirm_exit 方法
            self.parent().confirm_exit()
        else:
            # 调用父类的 keyPressEvent 方法处理其他按键事件
            super().keyPressEvent(event)

class TranslatorApp(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        # 设置窗口标题和大小
        self.setWindowTitle('翻译器')
        self.resize(600, 400)

        layout = QVBoxLayout()

        # 创建用于显示聊天记录的 QTextEdit 控件
        self.chat_history = QTextEdit()
        self.chat_history.setReadOnly(True)
        layout.addWidget(self.chat_history)

        # 创建用于输入文本的 QTextEdit 控件，并设置占位文本
        self.input_box = EnterTextEdit(self)
        self.input_box.setPlaceholderText("请输入你要翻译的信息（Enter发送/Esc退出）：")
        layout.addWidget(self.input_box)

        # 创建发送按钮，并连接到 translate_text 方法
        self.send_button = QPushButton('发送')
        self.send_button.clicked.connect(self.translate_text)
        layout.addWidget(self.send_button)

        self.setLayout(layout)

    def translate_text(self):
        # 获取输入框中的文本并去除首尾的空格
        word = self.input_box.toPlainText().strip()
        if not word:
            QMessageBox.warning(self, '提示', '消息不能为空！')
            return

        # 设置翻译请求的 URL、请求头和数据
        url = 'https://ifanyi.iciba.com/index.php?c=trans'
        header = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36 SE 2.X MetaSr 1.0'
        }
        data = {
            'from': 'auto',
            'to': 'auto',
            'q': word
        }
        # 发送 POST 请求并获取响应
        res = requests.post(url, headers=header, data=data)
        # 解析 JSON 响应
        dic = json.loads(res.text)
        # 获取翻译结果
        translation = dic['out']

        # 清空聊天记录并添加新的对话
        self.chat_history.append(f'你: {word}')
        self.chat_history.append(f'雪儿: {translation}\n')
        # 清空输入框
        self.input_box.clear()

    def confirm_exit(self):
        # 创建退出询问对话框
        reply = QMessageBox.question(self, '退出', '确定要退出吗？',
                                     QMessageBox.Yes | QMessageBox.No, QMessageBox.No)
        # 如果用户点击 Yes，退出应用
        if reply == QMessageBox.Yes:
            QApplication.quit()

    def closeEvent(self, event):
        # 重写窗口关闭事件，弹出退出询问对话框
        reply = QMessageBox.question(self, '退出', '确定要退出吗？',
                                     QMessageBox.Yes | QMessageBox.No, QMessageBox.No)
        # 如果用户点击 Yes，退出应用
        if reply == QMessageBox.Yes:
            event.accept()  # 接受关闭事件
        else:
            event.ignore()  # 忽略关闭事件

if __name__ == '__main__':
    app = QtWidgets.QApplication(sys.argv)
    app.setWindowIcon(QtGui.QIcon('favicon.ico'))  # 设置应用程序图标
    translator_app = TranslatorApp()
    translator_app.show()
    sys.exit(app.exec_())
