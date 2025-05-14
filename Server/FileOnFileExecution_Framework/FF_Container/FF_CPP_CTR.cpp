#include <iostream>
#include <string>
#include <cstdlib>
#include <cstdio>

int main() {
    std::string hello_source_file = "hello.cpp";
    std::string hello_executable = "hello_program";

    // 編譯階段
    std::cout << "正在編譯 " << hello_source_file << "..." << std::endl;
    std::string compile_command = "g++ " + hello_source_file + " -o " + hello_executable;

    #ifdef _WIN32
        compile_command = "g++ " + hello_source_file + " -o " + hello_executable + ".exe";
    #endif
    
        int compile_result = std::system(compile_command.c_str());
    
        if (compile_result != 0) {
            std::cerr << "錯誤：編譯 " << hello_source_file << " 失敗！" << std::endl;
            return 1;
        }
        std::cout << "編譯成功，生成了可執行檔。" << std::endl;
    
        // 執行階段
        std::cout << "\n正在執行編譯好的程式 (" << hello_executable << ")..." << std::endl;
        std::string execute_command = "./" + hello_executable;
    
    #ifdef _WIN32
        execute_command = hello_executable + ".exe";
    #endif
    
        int execute_result = std::system(execute_command.c_str());
    
        if (execute_result != 0) {
            std::cerr << "錯誤：執行 " << hello_executable << " 失敗！返回碼: " << execute_result << std::endl;
        } else {
            std::cout << "\n程式執行完畢。" << std::endl;
        }
    
        // 清理階段
        std::cout << "\n正在清理可執行檔..." << std::endl;
        std::string remove_exec_command = "rm " + hello_executable;
    
    #ifdef _WIN32
        remove_exec_command = "del " + hello_executable + ".exe";
    #endif
    
        std::system(remove_exec_command.c_str());
    
        std::cout << "\ncontainer.cpp 執行結束。" << std::endl;

    return 0;
}
