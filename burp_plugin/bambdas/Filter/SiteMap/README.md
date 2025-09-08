<!--
*** 自动生成文件 ***
此文件由BambdaChecker自动生成。
请勿手动编辑此文件，或在拉取请求中包含对此文件的任何更改。
-->
## [HideMissingResponses.bambda](https://github.com/PortSwigger/bambdas/blob/main/Filter/SiteMap/HideMissingResponses.bambda)
### 过滤站点地图以隐藏任何没有响应的请求。
#### 作者：Robin Wood (@digininja)
```java

return node.requestResponse().hasResponse();

```
## [ShowInjectionIssues.bambda](https://github.com/PortSwigger/bambdas/blob/main/Filter/SiteMap/ShowInjectionIssues.bambda)
### 仅显示名称中包含"injection"（注入）一词的问题。
#### 作者：Nicolas Grégoire
```java
return node.issues().stream().anyMatch(e -> e.name().contains("injection"));

```
