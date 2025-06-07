# GitHub项目高级检索工具 🔍

![Python版本](https://img.shields.io/badge/Python-3.8%2B-blue)
![开源协议](https://img.shields.io/badge/License-MIT-green)
![依赖库](https://img.shields.io/badge/依赖-requests%2Copenpyxl-orange)

## 🌟 核心功能

### 智能搜索能力
- **多维度过滤**：
  - 按星标数/分支数筛选
  - 指定项目最后更新时间
  - 必须包含README文件
- **智能排序**：
  - 支持星标/分支/更新时间排序
  - 正序/倒序自由切换

### 灵活输出格式
| 格式     | 特性                 | 适用场景          |
| -------- | -------------------- | ----------------- |
| JSON     | 结构化数据，完整字段 | 数据分析/二次开发 |
| Excel    | 表格格式，支持超链接 | 项目汇报/归档     |
| Markdown | 美观展示，直接可发布 | 技术文档/博客     |
| TXT      | 简洁文本，快速浏览   | 快速查阅          |

## 📦 快速开始

### 环境准备

访问[GitHub开发者设置](https://github.com/settings/tokens)页面。

```bash
# 安装依赖库
pip install requests openpyxl
# 配置GitHub令牌（必需）
set GITHUB_TOKEN=你的个人访问令牌
```

### 基础使用示例
```bash
# 搜索包含"机器学习"的项目（交互模式）
python github_search.py

# 命令行模式（导出前100个Python项目）
python github_search.py --word "Python" --min-stars 1000 --output md
```

## 🛠️ 参数详解

### 主要参数
| 参数              | 说明                         | 示例值             |
| ----------------- | ---------------------------- | ------------------ |
| `--word`          | 搜索关键词（支持空格分隔）   | "machine learning" |
| `--min-stars`     | 最小星标数                   | 500                |
| `--updated-after` | 最后更新时间（YYYY-MM-DD）   | 2023-01-01         |
| `--output`        | 输出格式（json/xlsx/md/txt） | xlsx               |

### 高级选项
```bash
# 获取星标榜TOP500项目
python github_search.py --top 500 --output json

# 组合条件搜索（Java项目，500+星标，含README）
python github_search.py --word Java --min-stars 500 --has-readme
```

## 📊 输出示例

### Markdown效果预览
| 排名 | 项目名称                                               | 描述         | Stars | Forks | 更新时间   | 链接                                             |
| ---- | ------------------------------------------------------ | ------------ | ----- | ----- | ---------- | ------------------------------------------------ |
| 1    | [tensorflow](https://github.com/tensorflow/tensorflow) | 机器学习框架 | 179k  | 88.4k | 2023-12-15 | [访问](https://github.com/tensorflow/tensorflow) |

### Excel功能亮点
- 自动调整列宽
- 带格式的超链接
- 星标/分支数排序
- 时间格式化显示

## ⚠️ 注意事项

1. **API速率限制**：
   - 基础令牌：每小时500请求
   - 令牌认证后：每小时5000请求
   - 程序自动处理速率限制，触发时会显示等待时间
2. **数据过滤规则**：
   - 自动过滤描述中含特殊字符的项目
   - 排除半年未更新的陈旧项目（可关闭）
   - 关键词相关性智能匹配
---

**提示**：使用`--help`查看完整参数列表，获取实时帮助信息：
```bash
python github_search.py --help
```