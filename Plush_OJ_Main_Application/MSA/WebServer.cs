/* Copyright (c) 2025 Zhang Zhewei (Liyue-Wei) - Licensed under the MIT License. */

using System;
using System.Net;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace MSA
{
    class WebServer
    {
        private readonly HttpListener _listener = new HttpListener();
        private readonly string? urlPath;    // Web Pages URL Path

        public WebServer(string prefix, string url)
        {
            _listener.Prefixes.Add(prefix);
            urlPath = url;
        }
    }
}

/*
    TODO:
    1. URL 重定向
       - localhost → Plush-OJ
       - /Home.html → /Home
       - 自动添加 .html 扩展名
    2. Start() 方法 - 启动监听
    3. Stop() 方法 - 停止服务器
    4. ProcessRequest() - 处理请求
    5. GetContentType() - 返回 MIME 类型
*/