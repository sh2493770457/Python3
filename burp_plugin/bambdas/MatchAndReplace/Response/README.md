<!--
*** 自动生成文件 ***
此文件由BambdaChecker自动生成。
请勿手动编辑此文件，或在拉取请求中包含对此文件的任何更改。
-->
## [RedirectCSPReportsToCollaborator.bambda](https://github.com/PortSwigger/bambdas/blob/main/MatchAndReplace/Response/RedirectCSPReportsToCollaborator.bambda)

### 修改CSP响应头以将CSP报告重定向到协作器服务器。

#### 作者：PortSwigger
```java
var resp = requestResponse.response();
var csp = "Content-Security-Policy";
var collaborator = "https://" + api().collaborator().defaultPayloadGenerator().generatePayload();

if(!resp.hasHeader(csp)) {
	return resp;
}

var cspValue = resp
    .headerValue(csp)
    .replaceAll("(report-uri|report-to)\\s+[^;]+", "report-to csp-reports");

return resp
    .withUpdatedHeader(csp, cspValue)
    .withRemovedHeader("Reporting-Endpoints")
    .withAddedHeader("Reporting-Endpoints", "csp-reports=\""+collaborator+"\"");

```
