package Plugins

import (
	"tomato/Common"
	"tomato/WebScan"
)

// WebPoc 直接执行Web漏洞扫描
func WebPoc(info *Common.HostInfo) error {
	if Common.DisablePocScan {
		return nil
	}
	WebScan.WebScan(info)
	return nil
}
