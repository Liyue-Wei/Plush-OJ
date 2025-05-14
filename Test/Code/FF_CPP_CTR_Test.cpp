#include <iostream>
#include <cstring>
#include <vector>
#include <fstream>
#include <cstdlib>

struct TempCase {
    std::string tcPath;  // Temp Code Path
    std::string tdPath;  // Test DATA Path

    void parseArgs(char* argv[]) {
        tcPath = argv[1];
        tdPath = argv[2];

        std::cout << tcPath << std::endl << tdPath <<std::endl;
    }
};

int main(int argc, char* argv[]) {
    if (argc != 3) {
        std::cout << "Invalid args Input\nTerminated...\n";
        return 0;
    }

    TempCase TC;
    TC.parseArgs(argv);
    
    return 0;
}