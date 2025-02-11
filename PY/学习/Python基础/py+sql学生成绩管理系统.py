import pymysql
import re


# 定义id
def idinput(string):
    ID = input(string)
    pattern = re.compile(r"^\d{1,3}$")
    while not re.match(pattern, ID):
        ID = input("请输入1-3位整数：")
    return ID


# 增加学生
def addStudent():
    ID = idinput("请输入学生ID：")
    db = pymysql.connect(host="localhost", user="root", password="334455LM", database="student", charset="utf8",
                         port=3306)
    cursor = db.cursor()
    sql = "SELECT * FROM stusys WHERE ID='%s'" % ID
    cursor.execute(sql)
    while cursor.rowcount > 0:
        ID = idinput("该ID已存在，请重新输入：")
        sql = "SELECT * FROM stusys WHERE ID='%s'" % int(ID)
        cursor.execute(sql)
    name = input("请输入学生姓名：")
    chinese = input_grade("语文")
    math = input_grade("数学")
    english = input_grade("英语")
    total = int(chinese) + int(math) + int(english)
    sql = """INSERT INTO stusys(ID,NAME,CHINESE,MATH,ENGLISH,TOTAL)
             VALUES(%s, %s, %s, %s, %s, %s)"""
    cursor.execute(sql, (ID, name, chinese, math, english, total))
    db.commit()
    db.close()


# 输入成绩
def input_grade(subject):
    grade = input(f"请输入{subject}成绩：")
    while not grade.isdigit() or int(grade) < 0 or int(grade) > 100:
        grade = input("请输入0-100的整数：")
    return grade


# 删除学生
def delStudent():
    delstudentid = idinput("请输入要删除的学生ID：")
    if queryStudent(delstudentid):
        select = input("是否删除该学生信息？(Y/N)")
        if select.upper() == "Y":
            db = pymysql.connect(host="localhost", user="root", password="334455LM", database="student", charset="utf8",
                                 port=3306)
            cursor = db.cursor()
            sql = "DELETE FROM stusys WHERE ID=%s"
            cursor.execute(sql, (delstudentid,))
            db.commit()
            db.close()
            print("删除成功！")
        elif select.upper() == "N":
            print("已取消删除！")
        else:
            print("输入有误，请重新输入！")


#
def queryStudent(querystudentid):
    db = pymysql.connect(host="localhost", user="root", password="334455LM", database="student", charset="utf8",
                         port=3306)
    cursor = db.cursor()
    sql = "SELECT * FROM stusys WHERE ID=%s"
    cursor.execute(sql, (querystudentid,))
    if cursor.rowcount == 0:
        print("该学生不存在！")
        return False
    else:
        print("该学生信息如下：")
        for row in cursor.fetchall():
            print("ID:%s\tName:%s\tChinese:%s\tMath:%s\tEnglish:%s\tTotal:%s" % row)
        return True


#修改学生信息
def updateStudent():
    updatestudentid = idinput("请输入要修改的学生ID：")
    if queryStudent(updatestudentid):
        name = input("请输入学生姓名：")
        chinese = input_grade("语文")
        math = input_grade("数学")
        english = input_grade("英语")
        total = int(chinese) + int(math) + int(english)
        db = pymysql.connect(host="localhost", user="root", password="334455LM", database="student", charset="utf8",
                             port=3306)
        cursor = db.cursor()
        sql = """UPDATE stusys
                 SET NAME=%s, CHINESE=%s, MATH=%s, ENGLISH=%s, TOTAL=%s
                 WHERE ID=%s"""
        cursor.execute(sql, (name, chinese, math, english, total, updatestudentid))
        db.commit()
        db.close()


# 显示所有学生信息
def showAll():
    db = pymysql.connect(host="localhost", user="root", password="334455LM", database="student", charset="utf8",
                         port=3306)
    cursor = db.cursor()
    sql = "SELECT * FROM stusys"
    cursor.execute(sql)
    for row in cursor.fetchall():
        print("ID:%s\tName:%s\tChinese:%s\tMath:%s\tEnglish:%s\tTotal:%s" % row)


# 主函数
def showMenu():
    while True:
        print("欢迎来到学生管理系统")
        print("1.添加学生")
        print("2.删除学生")
        print("3.查询学生")
        print("4.修改学生")
        print("5.显示所有学生")
        print("6.退出系统")
        print("请选择:")
        select = input()
        if select == "1":
            addStudent()
        elif select == "2":
            delStudent()
        elif select == "3":
            querystudentid = idinput("请输入要查询的学生ID：")
            queryStudent(querystudentid)
        elif select == "4":
            updateStudent()
        elif select == "5":
            showAll()
        elif select == "6":
            break
        else:
            print("输入有误，请重新输入！")


if __name__ == "__main__":
    showMenu()
