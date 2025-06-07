# import http.server
# import socketserver
#
# PORT = 8000  # 你可以更改端口号
#
# class Handler(http.server.SimpleHTTPRequestHandler):
#     pass
#
# with socketserver.TCPServer(("", PORT), Handler) as httpd:
#     print(f"Serving at http://localhost:{PORT}")
#     httpd.serve_forever()
