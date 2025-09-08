<!--
*** 自动生成文件 ***
此文件由BambdaChecker自动生成。
请勿手动编辑此文件，或在拉取请求中包含对此文件的任何更改。
-->
## [SignRequest.bambda](https://github.com/PortSwigger/bambdas/blob/main/MatchAndReplace/Request/SignRequest.bambda)
### 签名请求。
#### 作者：PortSwigger
```java
var digest = utilities.cryptoUtils().generateDigest(
    requestResponse.request().body(),
    DigestAlgorithm.SHA_256
);
var signature = HexFormat.of().formatHex(digest.getBytes());

return requestResponse.request().withAddedHeader("Content-Sha256", signature);

```
## [SupportrandomplzPlaceholder.bambda](https://github.com/PortSwigger/bambdas/blob/main/MatchAndReplace/Request/SupportrandomplzPlaceholder.bambda)
### 支持"randomplz"占位符。
#### 作者：PortSwigger
```java
if (!(requestResponse.request().contains("randomplz", true))) {
	return requestResponse.request();
}

var arr = requestResponse.request().toString().replace(
    "randomplz",
    utilities.randomUtils().randomString(8)
);

return HttpRequest.httpRequest(requestResponse.httpService(), arr);

```
