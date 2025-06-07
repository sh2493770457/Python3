//go:build !windows

package Plugins

import "tomato/Common"

func DCInfoScan(info *Common.HostInfo) (err error) {
	return nil
}
