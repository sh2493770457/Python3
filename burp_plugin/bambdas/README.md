# Bambda 脚本

欢迎来到 PortSwigger 官方 Bambda 脚本仓库。此仓库包含由 PortSwigger 和社区共同开发的脚本集合 🧡

---

## 📑 本页内容

- [Bambda 脚本类型](#bambda-脚本类型)
- [仓库内容](#仓库内容)
- [将脚本导入 Burp](#将脚本导入-burp)
- [更新脚本库中的脚本](#更新脚本库中的脚本)
- [贡献您自己的脚本](#贡献您自己的脚本)
- [资源](#资源)

---

## Bambda 脚本类型

Bambda 是在支持的 Burp 工具中运行的脚本。它们使您能够通过创建以下内容快速个性化 Burp Suite：

- **表格过滤器** – 在 Burp 中动态过滤表格。
- **表格列** – 添加自定义表格列以显示重要数据。
- **Repeater 自定义操作** – 在 Burp Repeater 中提取、转换和分析数据。
- **匹配和替换规则** – 在 HTTP 和 WebSocket 消息通过代理时替换其部分内容。

> 💡 您可以在 **Burp Suite 社区版** 和 **Burp Suite 专业版** 中使用表格过滤器脚本。所有其他脚本需要 **Burp Suite 专业版**。

---

## 仓库内容

您可以按脚本类型浏览仓库：

### 表格过滤器
- [HTTP 历史记录](https://github.com/PortSwigger/bambdas/tree/main/Filter/Proxy/HTTP)
- [WebSocket 历史记录](https://github.com/PortSwigger/bambdas/tree/main/Filter/Proxy/WS)
- [站点地图](https://github.com/PortSwigger/bambdas/tree/main/Filter/SiteMap)
- [日志视图过滤器](https://github.com/PortSwigger/bambdas/tree/main/Filter/Logger/View)

### 表格自定义列
- [HTTP 历史记录](https://github.com/PortSwigger/bambdas/tree/main/CustomColumn/Proxy/HTTP)
- [WebSocket 历史记录](https://github.com/PortSwigger/bambdas/tree/main/CustomColumn/Proxy/WS)
- [日志记录器](https://github.com/PortSwigger/bambdas/tree/main/CustomColumn/Logger)

### 其他类型
- [Repeater 自定义操作](https://github.com/PortSwigger/bambdas/tree/main/CustomAction)
- [匹配和替换规则](https://github.com/PortSwigger/bambdas/tree/main/MatchAndReplace)

---

## 将脚本导入 Burp

要使用此仓库中的脚本，请将它们导入到 Burp 中的 Bambda 库。导入后，您可以根据需要在 Burp 和不同项目中加载脚本。

将 GitHub 脚本导入 Burp：
1. 从此仓库下载脚本，或将整个仓库下载为 ZIP 文件。
2. 如果使用 ZIP 文件，请解压其内容。
3. 在 Burp 中，转到 **扩展 > Bambda 库**。
4. 点击 **导入**。**导入脚本** 对话框打开。
5. 选择 `.bambda` 文件或解压的 ZIP 文件夹。
6. 点击 **打开**。

Burp 将选定的文件添加到您的 Bambda 库中。如果您选择一个文件夹，Burp 会自动查找并包含其中及其子文件夹中的任何 `.bambda` 文件。

> ⚠️ **警告：** 脚本可以运行任意代码。出于安全考虑，请在导入和使用脚本时保持谨慎。

---

## 更新脚本库中的脚本

要使您的脚本与此仓库中的最新更改保持同步，只需重新导入它们。Burp 会提示您确认是否覆盖现有脚本。

有关更详细的指导，请参阅 [更新您的脚本](https://portswigger.net/burp/documentation/desktop/extend-burp/bambdas/importing#updating-your-scripts)。

---

## 贡献您自己的脚本

感谢您为社区做出贡献 🧡 我们很高兴看到您的脚本！

### 如果您是第一次贡献：
从分步指南开始：  
➡️ [向我们的 GitHub 仓库提交脚本](https://portswigger.net/burp/documentation/desktop/extend-burp/bambdas/creating/contribute-scripts)

### 如果您之前已经贡献过：
查看快速参考指南以刷新您对流程和指导原则的记忆：  
➡️ [贡献快速参考指南](https://github.com/PortSwigger/Bambdas/blob/main/CONTRIBUTING.md)  

在提交时，请确保您熟悉并尊重我们的 [行为准则](https://github.com/PortSwigger/bambdas/blob/main/CODE_OF_CONDUCT.md)。

---

## 资源

### 了解更多脚本类型：
- [**Bambda 文档**](https://portswigger.net/burp/documentation/desktop/extend-burp/bambdas) – 关于所有脚本类型及其使用位置的详细信息。
- [**Bambda**](https://www.youtube.com/watch?v=neQpukwW43g) – 包含过滤器脚本示例的快速视频介绍。
- [**Bambda 表格过滤器**](https://www.youtube.com/watch?v=EYSsd2I7qcs) – 表格过滤器脚本的视频概述。
- [**Bambda 表格自定义**](https://www.youtube.com/watch?v=QyME5blj3e4) – 创建自定义表格列脚本的视频概述。
- [**自定义操作介绍**](https://www.youtube.com/watch?v=u3GX4LgMdHQ) – 关于使用自定义操作来自定义 Burp Repeater 的视频。
- [**Bambda 库介绍**](https://www.youtube.com/watch?v=XtkXHCG4RL8) – 关于存储和管理脚本的视频。

### 学习编写您自己的脚本：
- [**创建脚本文档**](https://portswigger.net/burp/documentation/desktop/extend-burp/bambdas/creating) – 在 Burp 中创建脚本的指导，包括参考资料和示例。
- [**Bambda 输出控制台**](https://www.youtube.com/watch?v=J1kN8yDRzMo) – 关于使用输出控制台测试和调试脚本的视频。

### 查看脚本实际应用：
- [**使用 Bambda 找到那个奇怪的端点**](#) – [James Kettle]([https://portswigger.net/research](https://portswigger.net/research/finding-that-one-weird-endpoint-with-bambdas)) 的博客文章，关于在测试期间使用过滤器脚本。
- [**实验室：限制超限竞态条件**](https://portswigger.net/web-security/race-conditions/lab-limit-overrun-race-condition) – 使用自定义操作的 Web 安全学院实验室。
