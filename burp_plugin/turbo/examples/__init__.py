"""
Turbo Intruder 示例脚本包

这个包包含了Turbo Intruder的各种示例脚本，涵盖了不同的攻击场景和技术。

脚本分类：

基础攻击脚本：
- basic.py: 基础字典攻击
- default.py: 默认攻击模板
- debug.py: 调试和故障排除

竞态条件攻击：
- race-single-packet-attack.py: 单包竞态攻击
- race-multi-endpoint.py: 多端点竞态攻击

性能基准测试：
- benchmark-h1-race.py: HTTP/1.1竞态基准测试
- benchmark-h2-race.py: HTTP/2竞态基准测试

高级攻击技术：
- timing.py: 时序攻击
- 0cl-poc.py: 0-Click漏洞概念验证
- 0cl-find-offset.py: 0-Click偏移量查找
- 0cl-exploit.py: 0-Click漏洞利用

协议特定：
- http2.py: HTTP/2协议攻击
- apis.py: Burp Suite API集成

特殊功能：
- misc.py: 杂项功能演示
- ratelimit.py: 限流绕过攻击

使用说明：
1. 根据攻击目标选择合适的脚本
2. 修改脚本中的目标信息和参数
3. 在Burp Suite的Turbo Intruder中加载脚本
4. 运行攻击并分析结果

注意事项：
- 仅用于授权的安全测试
- 在使用前请仔细阅读每个脚本的说明
- 根据目标环境调整参数
- 遵守负责任的漏洞披露原则
"""