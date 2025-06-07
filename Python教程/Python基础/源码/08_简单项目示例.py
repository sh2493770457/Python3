# 08_简单项目示例.py
# Python基础语法示例：简单项目 - 待办事项管理系统

import os
import json
import datetime
import random

# ===== 待办事项管理系统 =====
print("===== 待办事项管理系统 =====")

# 定义待办事项类
class TodoItem:
    """待办事项类"""
    def __init__(self, title, description="", due_date=None, priority="中", completed=False):
        self.id = random.randint(1000, 9999)  # 生成随机ID
        self.title = title
        self.description = description
        self.due_date = due_date
        self.priority = priority
        self.completed = completed
        self.created_at = datetime.datetime.now()
    
    def __str__(self):
        status = "已完成" if self.completed else "未完成"
        due_date_str = self.due_date.strftime("%Y-%m-%d") if self.due_date else "无截止日期"
        return f"[{self.id}] {self.title} (优先级: {self.priority}, 截止日期: {due_date_str}, 状态: {status})"
    
    def mark_completed(self):
        """将待办事项标记为已完成"""
        self.completed = True
        return f"[{self.id}] {self.title} 已标记为完成"
    
    def update(self, title=None, description=None, due_date=None, priority=None):
        """更新待办事项信息"""
        if title:
            self.title = title
        if description:
            self.description = description
        if due_date:
            self.due_date = due_date
        if priority:
            self.priority = priority
        return f"[{self.id}] 待办事项已更新"
    
    def to_dict(self):
        """将待办事项转换为字典，用于JSON序列化"""
        return {
            "id": self.id,
            "title": self.title,
            "description": self.description,
            "due_date": self.due_date.strftime("%Y-%m-%d") if self.due_date else None,
            "priority": self.priority,
            "completed": self.completed,
            "created_at": self.created_at.strftime("%Y-%m-%d %H:%M:%S")
        }
    
    @classmethod
    def from_dict(cls, data):
        """从字典创建待办事项对象，用于JSON反序列化"""
        item = cls(data["title"], data["description"])
        item.id = data["id"]
        if data["due_date"]:
            item.due_date = datetime.datetime.strptime(data["due_date"], "%Y-%m-%d")
        item.priority = data["priority"]
        item.completed = data["completed"]
        item.created_at = datetime.datetime.strptime(data["created_at"], "%Y-%m-%d %H:%M:%S")
        return item

# 定义待办事项管理器类
class TodoManager:
    """待办事项管理器类"""
    def __init__(self, file_path="todo_data.json"):
        self.file_path = file_path
        self.todos = []
        self.load_data()
    
    def add_todo(self, title, description="", due_date_str=None, priority="中"):
        """添加新的待办事项"""
        due_date = None
        if due_date_str:
            try:
                due_date = datetime.datetime.strptime(due_date_str, "%Y-%m-%d")
            except ValueError:
                return "错误：日期格式无效，请使用YYYY-MM-DD格式"
        
        todo = TodoItem(title, description, due_date, priority)
        self.todos.append(todo)
        self.save_data()
        return f"已添加新待办事项: {todo}"
    
    def list_todos(self, show_completed=True, sort_by="created_at"):
        """列出所有待办事项"""
        if not self.todos:
            return "没有待办事项"
        
        filtered_todos = self.todos
        if not show_completed:
            filtered_todos = [todo for todo in self.todos if not todo.completed]
        
        # 根据不同条件排序
        if sort_by == "due_date":
            # 将没有截止日期的项目排在最后
            sorted_todos = sorted(filtered_todos, 
                                 key=lambda x: (x.due_date is None, x.due_date))
        elif sort_by == "priority":
            # 优先级排序：高、中、低
            priority_order = {"高": 0, "中": 1, "低": 2}
            sorted_todos = sorted(filtered_todos, 
                                 key=lambda x: priority_order.get(x.priority, 1))
        else:  # 默认按创建时间排序
            sorted_todos = sorted(filtered_todos, 
                                 key=lambda x: x.created_at)
        
        result = "待办事项列表:\n"
        for todo in sorted_todos:
            result += f"{todo}\n"
        return result
    
    def find_todo(self, todo_id):
        """根据ID查找待办事项"""
        for todo in self.todos:
            if todo.id == todo_id:
                return todo
        return None
    
    def complete_todo(self, todo_id):
        """将待办事项标记为已完成"""
        todo = self.find_todo(todo_id)
        if todo:
            result = todo.mark_completed()
            self.save_data()
            return result
        return f"错误：找不到ID为{todo_id}的待办事项"
    
    def update_todo(self, todo_id, title=None, description=None, due_date_str=None, priority=None):
        """更新待办事项"""
        todo = self.find_todo(todo_id)
        if not todo:
            return f"错误：找不到ID为{todo_id}的待办事项"
        
        due_date = None
        if due_date_str:
            try:
                due_date = datetime.datetime.strptime(due_date_str, "%Y-%m-%d")
            except ValueError:
                return "错误：日期格式无效，请使用YYYY-MM-DD格式"
        
        result = todo.update(title, description, due_date, priority)
        self.save_data()
        return result
    
    def delete_todo(self, todo_id):
        """删除待办事项"""
        todo = self.find_todo(todo_id)
        if todo:
            self.todos.remove(todo)
            self.save_data()
            return f"已删除待办事项: [{todo_id}] {todo.title}"
        return f"错误：找不到ID为{todo_id}的待办事项"
    
    def get_stats(self):
        """获取统计信息"""
        total = len(self.todos)
        completed = sum(1 for todo in self.todos if todo.completed)
        pending = total - completed
        
        # 按优先级统计
        priority_stats = {"高": 0, "中": 0, "低": 0}
        for todo in self.todos:
            if todo.priority in priority_stats:
                priority_stats[todo.priority] += 1
        
        # 查找最近的截止日期
        upcoming_todos = [todo for todo in self.todos 
                         if todo.due_date and not todo.completed]
        nearest_due = min(upcoming_todos, key=lambda x: x.due_date).due_date if upcoming_todos else None
        
        result = "统计信息:\n"
        result += f"总计待办事项: {total}\n"
        result += f"已完成: {completed}\n"
        result += f"待完成: {pending}\n"
        result += "优先级分布:\n"
        for priority, count in priority_stats.items():
            result += f"  {priority}: {count}\n"
        
        if nearest_due:
            result += f"最近截止日期: {nearest_due.strftime('%Y-%m-%d')}\n"
        
        return result
    
    def save_data(self):
        """保存数据到文件"""
        try:
            data = [todo.to_dict() for todo in self.todos]
            with open(self.file_path, "w", encoding="utf-8") as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
            return True
        except Exception as e:
            print(f"保存数据时出错: {e}")
            return False
    
    def load_data(self):
        """从文件加载数据"""
        if not os.path.exists(self.file_path):
            self.todos = []
            return
        
        try:
            with open(self.file_path, "r", encoding="utf-8") as f:
                data = json.load(f)
                self.todos = [TodoItem.from_dict(item) for item in data]
        except Exception as e:
            print(f"加载数据时出错: {e}")
            self.todos = []

# 创建简单的命令行界面
class TodoCLI:
    """待办事项命令行界面"""
    def __init__(self):
        self.manager = TodoManager()
        self.commands = {
            "help": self.show_help,
            "add": self.add_todo,
            "list": self.list_todos,
            "view": self.view_todo,
            "complete": self.complete_todo,
            "update": self.update_todo,
            "delete": self.delete_todo,
            "stats": self.show_stats,
            "exit": self.exit_app
        }
        self.running = True
    
    def show_help(self, *args):
        """显示帮助信息"""
        help_text = """可用命令:
  help                      - 显示此帮助信息
  add <标题> [描述] [截止日期] [优先级]  - 添加新待办事项
  list [all/pending] [sort]  - 列出待办事项 (sort: date/priority/created)
  view <ID>                 - 查看待办事项详情
  complete <ID>             - 将待办事项标记为已完成
  update <ID> [标题] [描述] [截止日期] [优先级] - 更新待办事项
  delete <ID>               - 删除待办事项
  stats                     - 显示统计信息
  exit                      - 退出程序

示例:
  add 学习Python 完成Python基础课程 2023-12-31 高
  list pending priority
  complete 1234
        """
        return help_text
    
    def add_todo(self, *args):
        """添加待办事项"""
        if len(args) < 1:
            return "错误：请提供待办事项标题"
        
        title = args[0]
        description = args[1] if len(args) > 1 else ""
        due_date = args[2] if len(args) > 2 else None
        priority = args[3] if len(args) > 3 else "中"
        
        return self.manager.add_todo(title, description, due_date, priority)
    
    def list_todos(self, *args):
        """列出待办事项"""
        show_completed = True
        sort_by = "created_at"
        
        if args and args[0] == "pending":
            show_completed = False
        
        if len(args) > 1:
            if args[1] == "date":
                sort_by = "due_date"
            elif args[1] == "priority":
                sort_by = "priority"
        
        return self.manager.list_todos(show_completed, sort_by)
    
    def view_todo(self, *args):
        """查看待办事项详情"""
        if not args:
            return "错误：请提供待办事项ID"
        
        try:
            todo_id = int(args[0])
            todo = self.manager.find_todo(todo_id)
            if todo:
                result = f"待办事项详情:\n"
                result += f"ID: {todo.id}\n"
                result += f"标题: {todo.title}\n"
                result += f"描述: {todo.description}\n"
                result += f"优先级: {todo.priority}\n"
                result += f"状态: {'已完成' if todo.completed else '未完成'}\n"
                result += f"创建时间: {todo.created_at.strftime('%Y-%m-%d %H:%M:%S')}\n"
                if todo.due_date:
                    result += f"截止日期: {todo.due_date.strftime('%Y-%m-%d')}\n"
                return result
            else:
                return f"错误：找不到ID为{todo_id}的待办事项"
        except ValueError:
            return "错误：ID必须是数字"
    
    def complete_todo(self, *args):
        """将待办事项标记为已完成"""
        if not args:
            return "错误：请提供待办事项ID"
        
        try:
            todo_id = int(args[0])
            return self.manager.complete_todo(todo_id)
        except ValueError:
            return "错误：ID必须是数字"
    
    def update_todo(self, *args):
        """更新待办事项"""
        if not args:
            return "错误：请提供待办事项ID"
        
        try:
            todo_id = int(args[0])
            title = args[1] if len(args) > 1 else None
            description = args[2] if len(args) > 2 else None
            due_date = args[3] if len(args) > 3 else None
            priority = args[4] if len(args) > 4 else None
            
            return self.manager.update_todo(todo_id, title, description, due_date, priority)
        except ValueError:
            return "错误：ID必须是数字"
    
    def delete_todo(self, *args):
        """删除待办事项"""
        if not args:
            return "错误：请提供待办事项ID"
        
        try:
            todo_id = int(args[0])
            return self.manager.delete_todo(todo_id)
        except ValueError:
            return "错误：ID必须是数字"
    
    def show_stats(self, *args):
        """显示统计信息"""
        return self.manager.get_stats()
    
    def exit_app(self, *args):
        """退出程序"""
        self.running = False
        return "感谢使用待办事项管理系统，再见！"
    
    def run(self):
        """运行命令行界面"""
        print("欢迎使用待办事项管理系统！输入 'help' 获取帮助。")
        
        while self.running:
            try:
                user_input = input("\n请输入命令> ")
                if not user_input.strip():
                    continue
                
                parts = user_input.split()
                command = parts[0].lower()
                args = parts[1:]
                
                if command in self.commands:
                    result = self.commands[command](*args)
                    print(result)
                else:
                    print(f"未知命令: {command}。输入 'help' 获取帮助。")
            except KeyboardInterrupt:
                print("\n程序被中断")
                self.running = False
            except Exception as e:
                print(f"发生错误: {e}")

# 演示系统功能
def demo_todo_system():
    """演示待办事项管理系统的功能"""
    print("\n=== 待办事项管理系统演示 ===")
    
    # 创建管理器
    manager = TodoManager("demo_todos.json")
    
    # 添加一些示例待办事项
    print("\n添加示例待办事项:")
    print(manager.add_todo("完成Python学习", "学习Python基础语法和标准库", "2023-12-31", "高"))
    print(manager.add_todo("准备面试", "复习编程知识，准备技术面试", "2023-11-15", "高"))
    print(manager.add_todo("健身计划", "每周至少锻炼3次", "2023-12-01", "中"))
    print(manager.add_todo("阅读新书", "阅读《Python编程：从入门到实践》", None, "低"))
    
    # 列出所有待办事项
    print("\n列出所有待办事项:")
    print(manager.list_todos())
    
    # 按优先级排序
    print("\n按优先级排序:")
    print(manager.list_todos(sort_by="priority"))
    
    # 完成一个待办事项
    todo_id = manager.todos[1].id  # 获取第二个待办事项的ID
    print(f"\n完成待办事项 (ID: {todo_id}):")
    print(manager.complete_todo(todo_id))
    
    # 只显示未完成的待办事项
    print("\n只显示未完成的待办事项:")
    print(manager.list_todos(show_completed=False))
    
    # 更新待办事项
    todo_id = manager.todos[0].id  # 获取第一个待办事项的ID
    print(f"\n更新待办事项 (ID: {todo_id}):")
    print(manager.update_todo(todo_id, "完成高级Python学习", "学习Python高级特性和框架"))
    
    # 查看统计信息
    print("\n查看统计信息:")
    print(manager.get_stats())
    
    # 删除待办事项
    todo_id = manager.todos[3].id  # 获取第四个待办事项的ID
    print(f"\n删除待办事项 (ID: {todo_id}):")
    print(manager.delete_todo(todo_id))
    
    # 再次列出所有待办事项
    print("\n再次列出所有待办事项:")
    print(manager.list_todos())
    
    # 清理演示文件
    if os.path.exists("demo_todos.json"):
        os.remove("demo_todos.json")
        print("\n已清理演示文件")

# 运行演示
demo_todo_system()

# 如果要运行完整的命令行界面，取消下面的注释
print("\n要启动完整的命令行界面，请取消代码中的注释")
# if __name__ == "__main__":
#     cli = TodoCLI()
#     cli.run()