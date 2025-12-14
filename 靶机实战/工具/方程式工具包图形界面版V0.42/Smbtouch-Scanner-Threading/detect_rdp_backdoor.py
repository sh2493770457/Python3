# -*- coding: GB2312 -*-

import binascii
import socket
import argparse
import threading
import ssl


# Packets
ssl_negotiation_request = binascii.unhexlify("030000130ee000000000000100080001000000")
non_ssl_negotiation_request = binascii.unhexlify("030000130ee000000000000100080000000000")
non_ssl_client_data = binascii.unhexlify("030001ac02f0807f658201a00401010401010101ff30190201220201020201000201010201000201010202ffff020102301902010102010102010102010102010002010102020420020102301c0202ffff0202fc170202ffff0201010201000201010202ffff0201020482013f000500147c00018136000800100001c00044756361812801c0d800040008000005000401ca03aa09080000b01d0000000000000000000000000000000000000000000000000000000000000000000007000000000000000c0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001ca01000000000018000f0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000004c00c00110000000000000002c00c001b0000000000000003c0380004000000726470647200000000008080726470736e640000000000c0647264796e766300000080c0636c6970726472000000a0c0")
ssl_client_data = binascii.unhexlify("030001ac02f0807f658201a00401010401010101ff30190201220201020201000201010201000201010202ffff020102301902010102010102010102010102010002010102020420020102301c0202ffff0202fc170202ffff0201010201000201010202ffff0201020482013f000500147c00018136000800100001c00044756361812801c0d800040008000005000401ca03aa09080000b01d0000000000000000000000000000000000000000000000000000000000000000000007000000000000000c0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001ca01000000000018000f0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000100000004c00c00110000000000000002c00c001b0000000000000003c0380004000000726470647200000000008080726470736e640000000000c0647264796e766300000080c0636c6970726472000000a0c0")
ping_packet = binascii.unhexlify("0300000e02f0803c443728190200")

# Arguments
parser = argparse.ArgumentParser(description="RDP DOUBLEPULSAR后门批量探测脚本\n\nAuthor: Luke Jennings\nTopsec天融信安全服务北京中心修改\nWebsite: https://countercept.com\nTwitter: @countercept", formatter_class=argparse.RawTextHelpFormatter)
group = parser.add_mutually_exclusive_group(required=True)
group.add_argument('--ip', help='指定单个IP')
group.add_argument('--file', help='指定一个ip地址列表文件')
group.add_argument('--net', help='指定一个网段，格式如192.168.237.1/24')
parser.add_argument('--timeout', help="指定socket连接超时时间，单位秒", default="5")
parser.add_argument('--verbose', help="输出扫描的详细过程", action='store_true')
parser.add_argument('--threads', help="指定线程数目", default="30")

args = parser.parse_args()
ip = args.ip
filename = args.file
net = args.net
timeout = args.timeout
verbose = args.verbose
num_threads = int(args.threads)
semaphore = threading.BoundedSemaphore(value=num_threads)
print_lock = threading.Lock()
all_text = ""

def print_status(ip, message):
    global print_lock

    with print_lock:
        print "[*] [%s] %s" % (ip, message)


def check_ip(ip):
    global ssl_negotiation_request, non_ssl_negotiation_request, non_ssl_client_data, ssl_client_data, ping_packet, timeout, verbose, all_text

    # Connect to socket
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(float(timeout) if timeout else None)
    host = ip
    port = 3389
    s.connect((host, port))

    # Send/receive negotiation request
    if verbose:
        print_status(ip, "Sending negotiation request")
    s.send(ssl_negotiation_request)
    negotiation_response = s.recv(1024)

    # Determine if server has chosen SSL
    if len(negotiation_response) >= 19 and negotiation_response[11] == "\x02" and negotiation_response[15] == "\x01":
        if verbose:
            print_status(ip, "Server chose to use SSL - negotiating SSL connection")
        sock = ssl.wrap_socket(s)
        s = sock

        # Send/receive ssl client data
        if verbose:
            print_status(ip, "Sending SSL client data")
        s.send(ssl_client_data)
        s.recv(1024)

    # Server explicitly refused SSL
    elif len(negotiation_response) >= 19 and negotiation_response[11] == "\x03" and negotiation_response[15] == "\x02":
        if verbose:
            print_status(ip, "Server explicitly refused SSL, reconnecting")

        # Re-connect
        s.close()
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.settimeout(float(timeout) if timeout else None)
        s.connect((host, port))

        # Send/receive non-ssl negotiation request
        if verbose:
            print_status(ip, "Sending non-ssl negotiation request")
        s.send(non_ssl_negotiation_request)
        s.recv(1024)

    # Server requires NLA which implant does not support
    elif len(negotiation_response) >= 19 and negotiation_response[11] == "\x03" and negotiation_response[15] == "\x05":
        with print_lock:
            print "[-] [%s] 服务端需要NLA验证,RDP DOUBLEPULSAR后门不支持" % ip

        s.close()
        return

    # Carry on non-ssl
    else:
        # Send/receive non-ssl client data
        if verbose:
            print_status(ip, "Sending client data")
        s.send(non_ssl_client_data)
        s.recv(1024)

    # Send/receive ping
    if verbose:
        print_status(ip, "Sending ping packet")
    s.send(ping_packet)

    # Non-infected machines terminate connection, infected send a response
    try:
        ping_response = s.recv(1024)

        with print_lock:
            if len(ping_response) == 288:
                print "[+] [%s] 该IP发现RDP DOUBLEPULSAR后门!!!" % ip
                all_text=all_text+ip+"\r\n"
            else:
                print "[-] [%s] 返回未知错误,length was %d not 288" % (ip, len(ping_response))
        s.close()
    except socket.error as e:
        with print_lock:
            print "[-] [%s] 未发现RDP DOUBLEPULSAR后门" % ip


def threaded_check(ip_address):
    global semaphore

    try:
        check_ip(ip_address)
    except Exception as e:
        with print_lock:
            print "[ERROR] [%s] - %s" % (ip_address, e)
    finally:
        semaphore.release()


if ip:
    check_ip(ip)

elif filename:
    with open(filename, "r") as fp:
        for line in fp:
            semaphore.acquire()
            ip_address = line.strip()
            t = threading.Thread(target=threaded_check, args=(ip_address,))
            t.start()
elif net:
    from netaddr import IPNetwork
    network = IPNetwork(net)
    for addr in network:
        # Skip the network and broadcast addresses
        if ((network.size != 1) and ((addr == network.network) or (addr == network.broadcast))):
            continue
        semaphore.acquire()
        ip_address = str(addr)
        t = threading.Thread(target=threaded_check, args=(ip_address,))
        t.start()

for a in range(num_threads):
    semaphore.acquire()
print "扫描结束,存在RDP DOUBLEPULSAR的主机如下:"
print all_text
