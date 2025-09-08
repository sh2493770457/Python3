# Bambda脚本完整功能说明文档

## 项目概述

Bambda脚本是Burp Suite的扩展功能，允许用户使用Java代码来自定义Burp Suite的行为。这些脚本可以用于过滤、高亮、自定义操作等多种用途，极大地提升了Web安全测试的效率。

## 脚本分类

### 1. CustomAction（自定义动作脚本）- 18个脚本

#### 1.1 CalculateResponseMetadata.bambda
- **功能**：计算HTTP响应的元数据信息
- **作者**：未指定
- **用途**：分析响应数据的统计信息，帮助理解响应特征

#### 1.2 CookieInjection.bambda
- **功能**：检测Cookie注入漏洞
- **作者**：未指定
- **用途**：自动测试Cookie参数是否存在注入漏洞，提高漏洞发现效率

#### 1.3 CookiePrefixBypass.bambda
- **功能**：测试Cookie前缀绕过技术
- **作者**：未指定
- **用途**：检测应用程序是否正确处理Cookie前缀安全机制

#### 1.4 HackingAssistant.bambda
- **功能**：AI驱动的渗透测试助手
- **作者**：未指定
- **用途**：提供智能化的渗透测试建议和自动化攻击向量生成

#### 1.5 InlineStyleAttributeStealer.bambda
- **功能**：窃取内联样式属性
- **作者**：未指定
- **用途**：提取和分析HTML中的内联CSS样式，用于信息收集

#### 1.6 InsertHVTagsSpaceAndNewline.bambda
- **功能**：插入HTML验证标签、空格和换行符
- **作者**：未指定
- **用途**：测试应用程序对特殊HTML字符的处理能力

#### 1.7 NavigateAsAnonAndLookForDifferences.bambda
- **功能**：以匿名用户身份导航并查找差异
- **作者**：未指定
- **用途**：比较认证用户和匿名用户看到的内容差异，发现权限控制问题

#### 1.8 PerformReverseDNSLookup.bambda
- **功能**：执行反向DNS查询
- **作者**：未指定
- **用途**：获取IP地址对应的域名信息，用于信息收集

#### 1.9 PerformWebAPILookup.bambda
- **功能**：执行Web API查询
- **作者**：未指定
- **用途**：调用外部API获取目标相关信息

#### 1.10 ProbeForRaceCondition.bambda
- **功能**：探测竞态条件漏洞
- **作者**：未指定
- **用途**：通过并发请求测试应用程序是否存在竞态条件漏洞

#### 1.11 RepeaterClipNewFromClipboard.bambda
- **功能**：从剪贴板创建新的Repeater标签
- **作者**：未指定
- **用途**：快速从剪贴板内容创建新的HTTP请求进行测试

#### 1.12 RepeaterClipShareToClipboard.bambda
- **功能**：将Repeater内容分享到剪贴板
- **作者**：未指定
- **用途**：方便地将HTTP请求/响应内容复制到剪贴板

#### 1.13 RetryRequestWithoutCookies.bambda
- **功能**：重试请求但不包含Cookie
- **作者**：未指定
- **用途**：测试应用程序在没有Cookie情况下的行为

#### 1.14 RetryUntilSuccess.bambda
- **功能**：重试请求直到成功
- **作者**：未指定
- **用途**：自动重试失败的请求，用于测试间歇性问题

#### 1.15 Screenshot.bambda
- **功能**：截图工具
- **作者**：未指定
- **用途**：捕获屏幕截图，支持编辑功能（绘制、标注、保存等）

#### 1.16 SmugglingOrPipelining.bambda
- **功能**：HTTP请求走私或管道化测试
- **作者**：未指定
- **用途**：测试HTTP请求走私和管道化漏洞

#### 1.17 TestHTTPTRACESupport.bambda
- **功能**：测试HTTP TRACE方法支持
- **作者**：未指定
- **用途**：检测服务器是否支持TRACE方法，可能存在XST攻击风险

#### 1.18 Unicode-decodeSelectedText.bambda
- **功能**：Unicode解码选中文本
- **作者**：未指定
- **用途**：解码Unicode编码的文本，便于分析

### 2. Filter（过滤器脚本）- 45个脚本

#### 2.1 SiteMap过滤器（2个脚本）

##### 2.1.1 HideMissingResponses.bambda
- **功能**：隐藏缺失响应的请求
- **作者**：未指定
- **用途**：过滤掉没有响应的HTTP请求，清理站点地图视图

##### 2.1.2 ShowInjectionIssues.bambda
- **功能**：显示注入问题
- **作者**：未指定
- **用途**：高亮显示可能存在注入漏洞的请求

#### 2.2 Proxy HTTP过滤器（43个脚本）

##### 2.2.1 AnnotateSoapRequests.bambda
- **功能**：注释SOAP请求
- **作者**：Nick Coblentz
- **用途**：在代理历史的"Notes"列中填充SOAP请求的元素信息

##### 2.2.2 Detect101SwitchingProtocols.bambda
- **功能**：检测"101 Switching Protocols"响应
- **作者**：Tur24Tur / BugBountyzip
- **用途**：识别协议切换响应，可能涉及WebSocket升级

##### 2.2.3 Detect403Forbidden.bambda
- **功能**：检测"403 Forbidden"响应
- **作者**：ctflearner
- **用途**：快速识别被禁止访问的资源

##### 2.2.4 DetectCSPReportOnlyHeader.bambda
- **功能**：检测CSP Report-Only头部
- **作者**：ctflearner
- **用途**：识别内容安全策略报告模式，可能存在CSP绕过机会

##### 2.2.5 DetectSafeHttpMethods.bambda
- **功能**：检测安全的HTTP方法
- **作者**：ctflearner
- **用途**：过滤出使用安全HTTP方法的请求

##### 2.2.6 DetectServerNames.bambda
- **功能**：检测特定服务器名称
- **作者**：Tur24Tur / BugBountyzip
- **用途**：识别响应中的服务器类型，用于指纹识别

##### 2.2.7 DetectSuspiciousJSFunctions.bambda
- **功能**：检测可疑的JavaScript函数
- **作者**：Tur24Tur / BugBountyzip
- **用途**：识别可能存在安全风险的JavaScript代码

##### 2.2.8 DetectWeakReferrerPolicy.bambda
- **功能**：检测弱引用策略
- **作者**：ctflearner
- **用途**：识别不安全的Referrer-Policy配置

##### 2.2.9 DetectWeakXSSProtectionHeader.bambda
- **功能**：检测弱XSS保护头部
- **作者**：ctflearner
- **用途**：识别配置不当的X-XSS-Protection头部

##### 2.2.10 EmailHighlighter.bambda
- **功能**：邮箱地址高亮器
- **作者**：Tur24Tur / BugBountyzip
- **用途**：在响应中查找并高亮显示邮箱地址

##### 2.2.11 ExcludeCommonDomains.bambda
- **功能**：排除常见域名
- **作者**：y1shin
- **用途**：过滤掉不需要的常见域名请求，如Google、百度等

##### 2.2.12 FilterAuthenticated.bambda
- **功能**：过滤认证请求
- **作者**：joe-ds
- **用途**：显示包含认证信息的200 OK请求

##### 2.2.13 FilterAuthenticatedNonBearerTokens.bambda
- **功能**：过滤非Bearer令牌的认证请求
- **作者**：GangGreenTemperTatum
- **用途**：识别使用非标准Bearer令牌的认证请求

##### 2.2.14 FilterHighlightAnnotateOWASP.bambda
- **功能**：基于OWASP Top 25的参数过滤和高亮
- **作者**：Shain Lakin
- **用途**：根据OWASP漏洞参数列表高亮显示可能存在漏洞的请求

##### 2.2.15 FilterOnCookieValue.bambda
- **功能**：基于Cookie值过滤
- **作者**：LostCoder
- **用途**：过滤包含特定Cookie值的请求

##### 2.2.16 FilterOnSpecificHighlightColor.bambda
- **功能**：基于特定高亮颜色过滤
- **作者**：Nick Coblentz
- **用途**：显示具有特定高亮颜色的请求/响应

##### 2.2.17 FilterOutOptionsRequests.bambda
- **功能**：过滤掉OPTIONS请求
- **作者**：Trikster
- **用途**：隐藏OPTIONS预检请求，清理代理历史

##### 2.2.18 FindJSONresponsesWithIncorrectContentType.bambda
- **功能**：查找内容类型错误的JSON响应
- **作者**：albinowax
- **用途**：识别Content-Type不正确但实际是JSON的响应

##### 2.2.19 GraphQlEndpoints.bambda
- **功能**：查找GraphQL端点
- **作者**：Gareth Hayes
- **用途**：识别包含GraphQL查询的请求

##### 2.2.20 HighlightDeprecatedHTTPMethods.bambda
- **功能**：高亮显示已弃用的HTTP方法
- **作者**：Tur24Tur / BugBountyzip
- **用途**：识别使用TRACE、CONNECT等不常见HTTP方法的请求

##### 2.2.21 HighlightGraphQLMutations.bambda
- **功能**：高亮显示GraphQL变更操作
- **作者**：drwetter
- **用途**：识别GraphQL mutation请求

##### 2.2.22 HighlightHashes.bambda
- **功能**：高亮显示哈希值
- **作者**：Daniel Roberts
- **用途**：在响应中识别常见的密码哈希格式

##### 2.2.23 HighlightListenerPort.bambda
- **功能**：根据监听端口高亮显示
- **作者**：Bogo-6
- **用途**：根据不同的代理监听端口使用不同颜色高亮

##### 2.2.24 HighlightParamMinerTargets.bambda
- **功能**：高亮显示参数挖掘目标
- **作者**：GangGreenTemperTatum
- **用途**：识别适合进行参数挖掘的JSON响应

##### 2.2.25 HighlightPast48hrs.bambda
- **功能**：高亮显示过去48小时的请求
- **作者**：GangGreenTemperTatum
- **用途**：过滤显示最近48小时内的代理历史

##### 2.2.26 HighlightPwnFox.bambda
- **功能**：高亮显示PwnFox请求
- **作者**：GangGreenTemperTatum
- **用途**：识别包含PwnFox标头的请求

##### 2.2.27 HighlightResponsesWithDeveloperNotes.bambda
- **功能**：高亮显示包含开发者注释的响应
- **作者**：Tur24Tur / BugBountyzip
- **用途**：识别HTML和JavaScript中的开发者注释

##### 2.2.28 HighlightTrackerServices.bambda
- **功能**：高亮显示跟踪服务
- **作者**：Tur24Tur / BugBountyzip
- **用途**：识别和分析网络跟踪服务请求

##### 2.2.29 HighlightUnencryptedHTTP.bambda
- **功能**：高亮显示未加密的HTTP流量
- **作者**：Tur24Tur / BugBountyzip
- **用途**：识别使用HTTP而非HTTPS的请求

##### 2.2.30 HostnameInResponse.bambda
- **功能**：响应中包含主机名
- **作者**：emanuelduss
- **用途**：查找响应中包含主机名的情况，用于主机头注入测试

##### 2.2.31 IncorrectContentLength.bambda
- **功能**：内容长度不正确
- **作者**：albinowax
- **用途**：识别Content-Length头部与实际内容长度不匹配的响应

##### 2.2.32 JSONPForCSPBypass.bambda
- **功能**：用于CSP绕过的JSONP
- **作者**：Gareth Hayes
- **用途**：识别可能用于内容安全策略绕过的JSONP端点

##### 2.2.33 LargeRedirectResponses.bambda
- **功能**：大型重定向响应
- **作者**：albinowax
- **用途**：标记响应体超过1000字节的重定向响应

##### 2.2.34 MalformedHttpHeader.bambda
- **功能**：格式错误的HTTP头部
- **作者**：albinowax
- **用途**：查找头部名称中包含空格的格式错误HTTP头部

##### 2.2.35 MultipleHtmlTags.bambda
- **功能**：多个HTML标签
- **作者**：albinowax
- **用途**：查找包含多个HTML结束标签的响应

##### 2.2.36 NotesKeywordHighlighter.bambda
- **功能**：注释关键词高亮器
- **作者**：Tur24Tur / BugBountyzip
- **用途**：查找注释中包含指定关键词的条目

##### 2.2.37 OWASPTop25VulnerableParameters.bambda
- **功能**：OWASP Top 25易受攻击参数
- **作者**：Tur24Tur / BugBountyzip
- **用途**：基于OWASP Top 25过滤包含易受攻击参数的请求

##### 2.2.38 RedirectedToParameterValue.bambda
- **功能**：重定向到参数值
- **作者**：emanuelduss
- **用途**：查找重定向到GET参数提供位置的响应，用于开放重定向测试

##### 2.2.39 ReflectedParameters.bambda
- **功能**：反射参数
- **作者**：emanuelduss
- **用途**：查找反射参数名称和值的响应，用于XSS、SSTI等攻击面识别

##### 2.2.40 ShowOnlyCachedResponses.bambda
- **功能**：仅显示缓存响应
- **作者**：PortSwigger
- **用途**：过滤显示被缓存的响应

##### 2.2.41 ShowOnlyDuplicatehtmlTags.bambda
- **功能**：仅显示重复的HTML标签
- **作者**：PortSwigger
- **用途**：显示包含重复</html>标签的响应

##### 2.2.42 ShowOnlyLargeRedirectResponses.bambda
- **功能**：仅显示大型重定向响应
- **作者**：PortSwigger
- **用途**：显示响应体较大的重定向响应

##### 2.2.43 ShowRequestsBetweenDates.bambda
- **功能**：显示指定日期间的请求
- **作者**：Nick Coblentz
- **用途**：过滤显示特定时间范围内的请求/响应

##### 2.2.44 UrlInParameter.bambda
- **功能**：参数中包含URL
- **作者**：emanuelduss
- **用途**：查找包含URL的请求，用于SSRF攻击面识别

### 3. CustomColumn（自定义列脚本）- 10个脚本

#### 3.1 AddGraphQLOperationNameColumn.bambda
- **功能**：添加GraphQL操作名称列
- **作者**：未指定
- **用途**：在代理历史中显示GraphQL操作名称

#### 3.2 AddPublicCORSColumn.bambda
- **功能**：添加公共CORS列
- **作者**：未指定
- **用途**：显示CORS相关信息

#### 3.3 AddJWTAlgorithmColumn.bambda
- **功能**：添加JWT算法列
- **作者**：未指定
- **用途**：显示JWT令牌使用的算法

#### 3.4 AddResponseTimeColumn.bambda
- **功能**：添加响应时间列
- **作者**：未指定
- **用途**：显示HTTP请求的响应时间

#### 3.5 AddContentLengthColumn.bambda
- **功能**：添加内容长度列
- **作者**：未指定
- **用途**：显示响应内容的长度

#### 3.6 AddStatusCodeColumn.bambda
- **功能**：添加状态码列
- **作者**：未指定
- **用途**：显示HTTP响应状态码

#### 3.7 AddMethodColumn.bambda
- **功能**：添加HTTP方法列
- **作者**：未指定
- **用途**：显示HTTP请求方法

#### 3.8 AddMimeTypeColumn.bambda
- **功能**：添加MIME类型列
- **作者**：未指定
- **用途**：显示响应的MIME类型

#### 3.9 AddCookieCountColumn.bambda
- **功能**：添加Cookie数量列
- **作者**：未指定
- **用途**：显示请求中Cookie的数量

#### 3.10 AddParameterCountColumn.bambda
- **功能**：添加参数数量列
- **作者**：未指定
- **用途**：显示请求中参数的数量

### 4. MatchAndReplace（匹配和替换脚本）- 3个脚本

#### 4.1 Request目录（2个脚本）

##### 4.1.1 SignRequest.bambda
- **功能**：签名请求
- **作者**：未指定
- **用途**：自动为HTTP请求添加数字签名

##### 4.1.2 SupportrandomplzPlaceholder.bambda
- **功能**：支持随机占位符
- **作者**：未指定
- **用途**：在请求中替换随机占位符为实际值

#### 4.2 Response目录（1个脚本）

##### 4.2.1 RedirectCSPReportsToCollaborator.bambda
- **功能**：将CSP报告重定向到协作器
- **作者**：未指定
- **用途**：修改响应中的CSP报告URL，重定向到Burp Collaborator

## 使用指南

### 导入脚本
1. 在Burp Suite中打开相应的功能模块（Proxy、Repeater等）
2. 找到Bambda脚本选项
3. 点击"Load"按钮选择.bambda文件
4. 脚本将自动加载并生效

### 安全提醒
- 在使用这些脚本之前，请仔细阅读代码以了解其功能
- 某些脚本可能会发送网络请求或执行系统操作
- 建议在测试环境中先验证脚本功能
- 定期更新脚本以获取最新功能和安全修复

### 更新脚本
- 访问GitHub仓库获取最新版本
- 使用BambdaChecker工具验证脚本完整性
- 备份现有配置后再更新

## 学习资源

- **官方文档**：[Burp Suite Bambda文档](https://portswigger.net/burp/documentation/desktop/tools/bambdas)
- **视频教程**：[Bambda脚本介绍视频](https://www.youtube.com/watch?v=Ql8e7Qz-oZs)
- **博客文章**：[Bambda脚本实战指南](https://portswigger.net/research/bambdas)
- **实验室练习**：[Web Security Academy](https://portswigger.net/web-security)

## 总结

本文档详细介绍了bambdas项目中的所有78个脚本，涵盖了Web安全测试的各个方面。这些脚本可以显著提升渗透测试的效率和准确性，是Web安全从业者的重要工具集。建议根据具体的测试需求选择合适的脚本，并在实际使用中不断学习和改进。