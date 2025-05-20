#include <windows.h>
#include <iostream>

int main() {
    STARTUPINFO si = { sizeof(si) }; // 初始化結構
    PROCESS_INFORMATION pi;

    // 指定要執行的程式
    char commandLine[] = "notepad.exe";

    BOOL result = CreateProcess(
        NULL,             // 應用程式名稱（NULL 會從 commandLine 自動推導）
        commandLine,      // 命令列
        NULL,             // Process security attributes
        NULL,             // Thread security attributes
        FALSE,            // Handle inheritance
        0,                // Creation flags
        NULL,             // 環境變數
        NULL,             // 工作目錄
        &si,              // 指定視窗資訊
        &pi               // 傳回行程資訊
    );

    if (!result) {
        std::cerr << "CreateProcess failed (" << GetLastError() << ").\n";
        return 1;
    }

    std::cout << "Process created successfully!\n";

    // 等待子行程結束
    WaitForSingleObject(pi.hProcess, INFINITE);

    // 關閉 Handle
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);

    return 0;
}
