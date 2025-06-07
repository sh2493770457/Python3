import tkinter as tk
import tkinter.messagebox

class MyApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title('header处理工具')
        win_width = self.winfo_screenwidth()
        win_height = self.winfo_screenheight()
        my_width = 800
        my_height = 560
        x = (win_width - my_width) / 2
        y = (win_height - my_height) / 2
        self.geometry("%dx%d+%d+%d" % (my_width, my_height, x, y))
        self.maxsize(my_width, my_height)
        self.minsize(my_width, my_height)
        self.button = tk.Button(self, text='转换', font=('宋体', 12), command=self.update)
        self.button.place(x=380, y=300)
        self.label1 = tk.Label(self, text='原始文本', font=('宋体', 10))
        self.label1.place(x=160, y=5)
        self.text1 = tk.Text(width=50, height=40)
        self.text1.place(x=5, y=30)
        self.label2 = tk.Label(self, text='结果文本', font=('宋体', 10))
        self.label2.place(x=600, y=5)
        self.text2 = tk.Text(width=50, height=40)
        self.text2.place(x=440, y=30)

    def update(self):
        if len(self.text1.get('0.0', 'end')) == 1:
            tkinter.messagebox.showinfo('提示', '请先填入内容继续')
        else:
            self.text2.delete('1.0', 'end')
            text = self.text1.get('0.0', 'end').split("\n")
            self.text2.insert('insert', "headers = {\n")
            text = [i for i in text if (i is not None) and (str(i).strip() != '')]
            for _ in text:
                value = ''
                if '\t' in _:
                    key_, value_ = _.split('\t', 1)
                    value = f"'{key_}': '{value_}'"
                elif ':' in _ and _[0] != ':' and '\t' not in _:
                    key_, value_ = _.split(':', 1)
                    value = f"'{key_}': '{value_.replace(' ', '', 1)}'"
                elif ':' in _ and _[0] == ':' and '\t' not in _:
                    if _[0] == ':':
                        key_, value_ = _.replace(':', '', 1).split(':', 1)
                    else:
                        key_, value_ = _.replace(':', '', 1).split(':', 1)
                    value = f"'{key_}': '{value_.replace(' ', '', 1)}'"
                if text.index(_) < len(text) - 1 and _ != '' and _ != '\n':
                    value = value + ',\n'
                self.text2.insert('insert', value)
            self.text2.insert('insert', "\n}")
            self.text2.update()

def main():
    app = MyApp()
    app.mainloop()

if __name__ == '__main__':
    main()




