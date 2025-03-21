## 文件上传

### 1. 概念

程序员在**开发文件上传功能**时，未对文件格式**后缀进行合法性校验**，或只在**前端**进行通过**js进行后缀校验**，导致攻击者成功上传具有恶意功能的程序

### 2. 成因

1. 未严格过滤
   * 未过滤
   * 过滤被绕过
2. 合法性校验不完全
   * 黑名单不完善
3. 程序问题
   * 中间件解析漏洞
   * PUT方法

### 3. 利用条件

1. **开启**文件上传功能
2. 目标目录具有**可写、执行**权限
3. Web容器**可以解析**上传脚本

### 4. 方法

#### (1). 前端简单检测

1.  JS检测后缀名

#### (2). 后端检测

1. 文件类型
   1. 文件头(幻数头)
   2. MIME-TYPE检测
2. 文件大小

#### (3). 白名单

* 只允许 `.jpg`、`.png`、`.pdf` 等已知安全的文件格式上传

##### a. 绕过

1. 图片马配合文件包含漏洞
2. 图片马配合中间件解析漏洞
3. 文件头(幻数头)

#### (4). 黑名单

* 列出不允许上传的文件类型、扩展名或特征

##### a. 绕过

1. 后缀名大小写
2. 特殊文件名
3. Windows流
4. 00截断
   * php < 5.3.4
   * **Magic_quotes_gpc = OFF**
5. 解析漏洞
   1. Apache
      1. 多后缀
         * 版本
         * 解析规则
      2. 配置问题
         * conf配置文件的**具体内容**
      3. .htaccess解析文件
         * 写入内容

   2. IIS6.0
      1. 目录解析
         * 形式
         * 原理
      2. 文件解析
         * 形式
         * 原理
      3. 特殊后缀解析
         * 形式
         * 原理

   3. PHP CGI
      1. 利用条件
      2. 利用形式

   4. Nginx空字节代码执行
      1. 影响版本
      2. 利用形式


#### (5). 编辑器漏洞

1. FCKeditor
2. Ewebeditor

## 

