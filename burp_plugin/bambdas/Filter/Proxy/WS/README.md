<!--
*** 自动生成文件 ***
此文件由BambdaChecker自动生成。
请勿手动编辑此文件，或在拉取请求中包含对此文件的任何更改。
-->
# 代理WebSockets过滤器
文档：[使用Bambdas过滤WebSockets历史记录](https://portswigger.net/burp/documentation/desktop/tools/proxy/websockets-history/bambdas)
## [ExtractPayloadToNotes.bambda](https://github.com/PortSwigger/bambdas/blob/main/Filter/Proxy/WS/ExtractPayloadToNotes.bambda)
### 从WebSocket消息中提取JSON元素并在WebSocket历史记录选项卡的"备注"列中显示
#### 作者：Nick Coblentz (https://github.com/ncoblentz)
```java
//The bambda will search for json elements with the following keys. The keys below are just examples. Add the keys you want to include here:
List<String> terms = List.of("target","error");

if (!message.annotations().hasNotes()) {
  StringBuilder builder = new StringBuilder();
  String payload = utilities().byteUtils().convertToString(message.payload().getBytes());
  terms.forEach(term -> {
    Matcher m = Pattern.compile("\"" + term + "\":\"([^\"]+)\"", Pattern.CASE_INSENSITIVE).matcher(payload);
    while (m.find() && m.groupCount() > 0) {
      for (int i = 1; i <= m.groupCount(); i++) {
        if (m.group(i) != null)
          builder.append(term + ": " + m.group(i) + " ");
      }
    }
  });
  message.annotations().setNotes(builder.toString());
}
return true;

```
