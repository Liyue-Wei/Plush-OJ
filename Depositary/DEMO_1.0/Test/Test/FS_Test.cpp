#include <iostream>
#include <filesystem>

int main(int argc, char* argv[]) {
    std::filesystem::path exePath = std::filesystem::absolute(argv[0]); // 当前可执行文件完整路径
    std::filesystem::path parent = exePath.parent_path(); 
    std::cout << parent;

    return 0;
}