## 贡献指南

感谢您为 Bambdas 仓库做出贡献！🚀

> 如果这是您第一次贡献，请从我们的分步指南开始：[向我们的 GitHub 仓库提交脚本](https://portswigger.net/burp/documentation/desktop/extend-burp/bambdas/creating/contribute-scripts)。

本页面是之前贡献过的用户的快速参考。它涵盖：

- 提交流程概述  
- 如何手动运行验证  
- 您的脚本必须满足的提交要求

> 在提交时，请确保您熟悉并尊重我们的 [行为准则](https://github.com/PortSwigger/bambdas/blob/main/CODE_OF_CONDUCT.md)。

---

### 📦 提交流程概述

为了确保高效的审查，请一次提交一个脚本。如果其中任何一个需要更改，捆绑多个脚本可能会延迟发布。

按照以下步骤提交您的脚本：

1. 在脚本顶部添加符合我们 [文件要求](https://github.com/PortSwigger/bambdas/blob/main/CONTRIBUTING.md#submission-guidelines) 的 Javadoc 块。
2. 完善您的脚本以满足我们的 [质量标准](https://github.com/PortSwigger/bambdas/blob/main/CONTRIBUTING.md#submission-guidelines)。
3. 从 Burp 的 Bambda 库中导出您的脚本。文件名使用驼峰命名法。
4. Fork 我们的 GitHub 仓库。
5. 在 fork 的仓库中，将您的脚本添加到适当的目录。
6. 运行 [验证 Bambdas](https://github.com/PortSwigger/bambdas/actions/workflows/bambda-checker-validate-only.yml) GitHub 工作流。
7. 打开拉取请求。

我们将审查您的提交并向您反馈任何意见。

---

### ✅ 提交指南

每个文件必须满足以下要求：

- 必须是 `.bambda` 文件。  
  _不要包含或修改 markdown 文件 — `README.md` 文件在合并后会自动生成。_
- 必须是 YAML 格式，包含必需的元数据：`ID`、`name`、`function`、`location` 和 `source`。  
  _为确保元数据正确，请从 Burp 中的 Bambda 库 [导出您的脚本](https://portswigger.net/burp/documentation/desktop/extend-burp/bambdas/managing#exporting-scripts)。_
- 文件名必须使用驼峰命名法。例如，`MyCustomScript.bambda`
- 必须以 Javadoc 块开头，按以下顺序：
  1. 脚本功能的简短描述（1-2 句话）。
  2. 按此格式的 `@author` 标签：
     ```java
     @author <your_name> (https://github.com/<your_profile>)
     ```
     _使用直接、未混淆的 GitHub 个人资料链接。_
  3. 如果需要，在 `@author` 标签下方添加额外说明。  
     _这些不会出现在目录 README 中。_
     
> 示例请参见 [此脚本](https://github.com/PortSwigger/bambdas/blob/main/Filter/Proxy/HTTP/FilterOnCookieValue.bambda)。

您的代码必须满足以下质量标准：

- 在 Burp 中成功编译。
- 可读且格式良好：
  - 避免过长的行。
  - 使用一致的代码风格。
  - 避免使用制表符缩进。推荐使用四个空格。
  - 使用清晰、描述性的变量名，而不是过多的注释。
- 通过避免不必要的复杂性或资源使用来考虑性能，这可能会拖慢 Burp。
- 不要复制 Burp Suite Professional 中已存在的功能。

---

### 🧪 本地运行验证

验证脚本的最简单方法是使用 [验证 Bambdas](https://github.com/PortSwigger/bambdas/actions/workflows/bambda-checker-validate-only.yml) GitHub 工作流。这会自动化流程，是大多数贡献者的推荐选项。

如果您更喜欢在开发过程中本地验证，可以使用 Java 17 或更高版本。

本地运行验证：

1. 打开终端并转到包含您脚本的顶级目录。
2. 运行以下命令之一：
   - 仅验证：  
     ```bash
     java -jar BambdaChecker-1.4.jar validateonly
     ```
   - 验证并生成更新的 `README.md`（仅供您自己参考）：  
     ```bash
     java -jar BambdaChecker-1.4.jar
     ```

> **注意：** 不要在拉取请求中提交本地生成的 `README.md`。

3. 查看结果。成功运行返回退出代码 `0`。
