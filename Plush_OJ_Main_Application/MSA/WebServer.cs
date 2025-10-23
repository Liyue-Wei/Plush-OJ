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