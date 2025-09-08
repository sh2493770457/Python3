<!--
*** 自动生成的文件 ***
此文件由 BambdaChecker 自动生成。
请不要手动编辑此文件，或在拉取请求中包含对此文件的任何更改。
-->
# 代理 HTTP 自定义列
文档：[添加自定义列](https://portswigger.net/burp/documentation/desktop/tools/proxy/http-history#adding-a-custom-column)
## [AddGraphQLOperationNameColumn.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/AddGraphQLOperationNameColumn.bambda)
### 添加 GraphQL 操作名称列。
#### 作者：PortSwigger
```java
String requestBody = requestResponse.request().bodyToString();

if (!utilities.jsonUtils().isValidJson(requestBody)) {
	return "";
}

return utilities.jsonUtils().readString(requestBody, "operationName");

```
## [AddPublicCORSColumn.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/AddPublicCORSColumn.bambda)
### 添加公共 CORS 列。
#### 作者：PortSwigger
```java
return requestResponse.hasResponse()
    && requestResponse.response().hasHeader("Access-Control-Allow-Origin", "*");

```
## [AddRefererHeaderColumn.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/AddRefererHeaderColumn.bambda)
### 添加 Referer 头部列。
#### 作者：PortSwigger
```java
return requestResponse.request().headerValue("Referer");

```
## [DetectCORS.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/DetectCORS.bambda)
### 检查 CORS 漏洞
#### 作者：https://github.com/JaveleyQAQ/
```java
if (requestResponse.hasResponse() && requestResponse.request().hasHeader("Origin") && requestResponse.response().hasHeader("Access-Control-Allow-Origin"))
{
    var requestOrigin = requestResponse.request().headerValue("Origin");
    var responseOrigin = requestResponse.response().headerValue("Access-Control-Allow-Origin");
    return requestOrigin.equals(responseOrigin) ? Character.toString(0x2757).concat("CORS?") : responseOrigin;

} else {
    return "";
}

```
## [JWTAlgorithm.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/JWTAlgorithm.bambda)
### 从 JWT 会话 Cookie 中提取 JWT alg 值
#### 作者：trikster
```java
if (!requestResponse.finalRequest().hasParameter("session", HttpParameterType.COOKIE)) {
    return "";
}

var cookieValue = requestResponse.finalRequest().parameter("session", HttpParameterType.COOKIE).value();

var jwtFrags = cookieValue.split("\\.");

if (jwtFrags.length != 3 ) {
    return "";
}


var headerJson = utilities().base64Utils().decode(jwtFrags[0], Base64DecodingOptions.URL);
var matcher = Pattern.compile(".+?\"alg\":\"(\\w+)\".+").matcher(headerJson.toString());

return matcher.matches() ? matcher.group(1) : "";

```
## [Referer.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/Referer.bambda)
### 提取 Referer 请求头部。用于识别通过 Referer 头部泄露的敏感数据，如 OIDC 授权码。
#### 作者：emanuelduss
```java
return requestResponse.request().hasHeader("Referer") ? requestResponse.request().headerValue("Referer") : "";

```
## [SOAPMethod.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/SOAPMethod.bambda)
### 从 SOAP 请求中提取方法和示例值
#### 作者：Nick Coblentz (https://github.com/ncoblentz)
```java
if(requestResponse.request().hasHeader("Content-Type")
    && requestResponse.request().headerValue("Content-Type").contains("soap+xml"))
{
    StringBuilder builder = new StringBuilder();
    if(requestResponse.request().bodyToString().contains("<s:Body"))
    {
        Matcher m = Pattern.compile("<(?:[a-zA-Z0-9]+:)?Username>([^<]+)</(?:[a-zA-Z0-9]+:)*Username>|<(?:[a-zA-Z0-9]+:)*Body[^>]*><([^ ]+)",Pattern.CASE_INSENSITIVE).matcher(requestResponse.request().bodyToString());
        while(m.find() && m.groupCount()>0) {
            for(int i=1;i<=m.groupCount();i++) {
                if(m.group(i)!=null)
                    builder.append(m.group(i)+" ");
            }
        }
        return builder.toString();
    }
}
return "";

```
## [ServerHeader.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/ServerHeader.bambda)
### 从响应中提取 Server 头部的值
#### 作者：agarri_fr
```java
return requestResponse.hasResponse() && requestResponse.response().hasHeader("Server")
  ? requestResponse.response().headerValue("Server")
  : "";

```
## [SlowResponses.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/SlowResponses.bambda)
### 当超过指定阈值时显示响应时间。
#### 作者：l4n73rn
```java

var delta = requestResponse.timingData().timeBetweenRequestSentAndStartOfResponse();
var threshold = Duration.ofSeconds(3);

if (delta != null && delta.toMillis() >= threshold.toMillis()) {
    return delta.toMillis();
} else {
    return "";
}

```
## [WCFBinarySOAPMethod.bambda](https://github.com/PortSwigger/bambdas/blob/main/CustomColumn/Proxy/HTTP/WCFBinarySOAPMethod.bambda)
### 从请求中提取 WCF SOAP 二进制方法
#### 作者：Nick Coblentz (https://github.com/ncoblentz)
```java
if(requestResponse.request().hasHeader("Content-Type") && requestResponse.request().headerValue("Content-Type").equals("application/soap+msbin1")){
    String body = requestResponse.request().bodyToString();
    String prefix = "www.examplewebsite.com/xmlnamespace/";
    int start = body.indexOf(prefix);
    if(start>0)
    {
        int end = body.indexOf("@",start+prefix.length());
        if(end>0)
        {
            return body.substring(start+prefix.length(), end);
        }

    }
}
return "";

```
