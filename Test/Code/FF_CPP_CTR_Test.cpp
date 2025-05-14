#include <iostream>
#include <string>
#include <fstream>
#include <cstdlib>

struct TempCase {
    std::string tcPath;  // Temp Code Path
    std::string tdPath;  // Test DATA Path

    void parseArgs(char* argv[]) {
        tcPath = argv[1];
        tdPath = argv[2];
    }
};

int main(int argc, char* argv[]) {
    if (argc != 3) {
        std::cout << "Invalid args Input\nTerminated...\n";
        return 0;
    }

    TempCase TC;
    TC.parseArgs(argv);

    // std::ifstream inFile_TC(TC.tcPath, std::ios::in);
    // std::ifstream inFile_TD(TC.tdPath, std::ios::in);

    std::string command = TC.tcPath + " < " + TC.tdPath;  // 這邊要先把測資獨立成一個檔案，一次一次測
    std::system(command.c_str());
    
    return 0;
}