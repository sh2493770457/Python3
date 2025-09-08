<!--
*** 自动生成的文件 ***
此文件由 BambdaChecker 自动生成。
请不要手动编辑此文件，或在拉取请求中包含对此文件的任何更改。
-->
# 日志查看器过滤器
文档：[Burp 日志查看器过滤器](https://portswigger.net/burp/documentation/desktop/tools/logger/filter-view#bambda-mode)
## [HighlightToolType.bambda](https://github.com/PortSwigger/bambdas/blob/main/Filter/Logger/View/HighlightToolType.bambda)
### 根据工具类型高亮显示消息。
#### 作者：ps-porpoise
```java
var highlights = Map.of(
        ToolType.TARGET,     HighlightColor.RED,
        ToolType.PROXY,      HighlightColor.BLUE,
        ToolType.INTRUDER,   HighlightColor.CYAN,
        ToolType.REPEATER,   HighlightColor.MAGENTA,
        ToolType.EXTENSIONS, HighlightColor.ORANGE,
        ToolType.SCANNER,    HighlightColor.GREEN,
        ToolType.SEQUENCER,  HighlightColor.PINK
);

requestResponse.annotations().setHighlightColor(
        highlights.getOrDefault(requestResponse.toolSource().toolType(), HighlightColor.NONE)
);

return true;

```
## [SlowResponses.bambda](https://github.com/PortSwigger/bambdas/blob/main/Filter/Logger/View/SlowResponses.bambda)
### 查找慢响应。
#### 作者：ps-porpoise
```java
var delta = requestResponse.timingData().timeBetweenRequestSentAndStartOfResponse();
var threshold = Duration.ofSeconds(3);

return delta != null && delta.toMillis() >= threshold.toMillis();

```
