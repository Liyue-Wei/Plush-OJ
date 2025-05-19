#include <windows.h>
#include <stdio.h>
#include <tchar.h>

#define BUFFER_SIZE 4096

int _tmain( int argc, TCHAR *argv[] )
{
    STARTUPINFO si;
    PROCESS_INFORMATION pi;
    SECURITY_ATTRIBUTES sa;

    HANDLE hChildStd_IN_Rd = NULL;
    HANDLE hChildStd_IN_Wr = NULL;
    HANDLE hChildStd_OUT_Rd = NULL;
    HANDLE hChildStd_OUT_Wr = NULL;

    ZeroMemory( &sa, sizeof(sa) );
    sa.nLength = sizeof(SECURITY_ATTRIBUTES);
    sa.bInheritHandle = TRUE; // 允许句柄被继承
    sa.lpSecurityDescriptor = NULL;

    // 为子进程的 STDOUT 创建管道
    if ( !CreatePipe(&hChildStd_OUT_Rd, &hChildStd_OUT_Wr, &sa, 0) ) {
        printf("StdoutRd CreatePipe failed (%d).\n", GetLastError());
        return 1;
    }
    // 确保子进程的 STDOUT 管道的读取句柄不是可继承的
    if ( !SetHandleInformation(hChildStd_OUT_Rd, HANDLE_FLAG_INHERIT, 0) ) {
        printf("Stdout SetHandleInformation failed (%d).\n", GetLastError());
        return 1;
    }

    // 为子进程的 STDIN 创建管道
    if ( !CreatePipe(&hChildStd_IN_Rd, &hChildStd_IN_Wr, &sa, 0) ) {
        printf("Stdin CreatePipe failed (%d).\n", GetLastError());
        return 1;
    }
    // 确保子进程的 STDIN 管道的写入句柄不是可继承的
    if ( !SetHandleInformation(hChildStd_IN_Wr, HANDLE_FLAG_INHERIT, 0) ) {
        printf("Stdin SetHandleInformation failed (%d).\n", GetLastError());
        return 1;
    }

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    si.hStdError = hChildStd_OUT_Wr; // 可以重定向到与 stdout 相同的管道，或创建新管道
    si.hStdOutput = hChildStd_OUT_Wr;
    si.hStdInput = hChildStd_IN_Rd;
    si.dwFlags |= STARTF_USESTDHANDLES; // 关键：告诉 CreateProcess 使用这些标准句柄

    ZeroMemory( &pi, sizeof(pi) );

    if( argc != 2 )
    {
        printf("Usage: %s [cmdline]\n", argv[0]);
        CloseHandle(hChildStd_OUT_Rd);
        CloseHandle(hChildStd_OUT_Wr);
        CloseHandle(hChildStd_IN_Rd);
        CloseHandle(hChildStd_IN_Wr);
        return 1;
    }

    // Start the child process.
    if( !CreateProcess( NULL,
        argv[1],
        NULL,
        NULL,
        TRUE,          // 设置为 TRUE 以继承句柄
        0,
        NULL,
        NULL,
        &si,
        &pi )
    )
    {
        printf( "CreateProcess failed (%d).\n", GetLastError() );
        CloseHandle(hChildStd_OUT_Rd);
        CloseHandle(hChildStd_OUT_Wr);
        CloseHandle(hChildStd_IN_Rd);
        CloseHandle(hChildStd_IN_Wr);
        return 1;
    }

    // 父进程关闭子进程不再需要的管道句柄
    // 子进程会继承 hChildStd_IN_Rd 和 hChildStd_OUT_Wr
    // 父进程使用 hChildStd_IN_Wr 写入，使用 hChildStd_OUT_Rd 读取
    CloseHandle(hChildStd_OUT_Wr); // 父进程不需要写入子进程的 stdout
    CloseHandle(hChildStd_IN_Rd);  // 父进程不需要读取子进程的 stdin

    // --- 向子进程写入数据 ---
    // 示例：向子进程发送 "hello\n"
    // CHAR szInput[] = "hello\n";
    // DWORD dwWritten;
    // if (!WriteFile(hChildStd_IN_Wr, szInput, strlen(szInput), &dwWritten, NULL)) {
    //     printf("WriteFile to child stdin failed (%d).\n", GetLastError());
    // }
    // 关闭子进程的输入管道写入端，表示输入结束
    // if (!CloseHandle(hChildStd_IN_Wr)) {
    //    printf("CloseHandle for hChildStd_IN_Wr failed (%d).\n", GetLastError());
    // }


    // --- 从子进程读取数据 ---
    DWORD dwRead;
    CHAR chBuf[BUFFER_SIZE];
    BOOL bSuccess = FALSE;
    printf("\n--- Output from child process ---\n");
    for (;;) // 持续读取直到管道关闭或出错
    {
        bSuccess = ReadFile( hChildStd_OUT_Rd, chBuf, BUFFER_SIZE -1, &dwRead, NULL);
        if( ! bSuccess || dwRead == 0 ) break; // 管道已关闭或读取错误

        chBuf[dwRead] = '\0'; // 添加字符串结束符
        printf("%s", chBuf);
    }
    printf("\n--- End of child process output ---\n");


    // Wait until child process exits.
    WaitForSingleObject( pi.hProcess, INFINITE );

    // Close process and thread handles.
    CloseHandle( pi.hProcess );
    CloseHandle( pi.hThread );

    // 关闭剩余的管道句柄
    CloseHandle(hChildStd_OUT_Rd);
    if (hChildStd_IN_Wr != INVALID_HANDLE_VALUE) { // 如果之前没有因为写入完成而关闭
        CloseHandle(hChildStd_IN_Wr);
    }


    return 0;
}