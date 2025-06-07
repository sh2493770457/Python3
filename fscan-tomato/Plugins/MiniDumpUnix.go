//go:build !windows

package Plugins

import "tomato/Common"

func MiniDump(info *Common.HostInfo) (err error) {
	return nil
}
