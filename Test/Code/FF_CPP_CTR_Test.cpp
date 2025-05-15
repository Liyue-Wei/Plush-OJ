#include <iostream>
#include <string>
#include <vector>
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

std::vector<std::string> prohibited_header = {
        "<fstream>",
        "<cstdio>", 
        "<stdio.h>",
        "<filesystem>",
        "<process.h>", 
        "<unistd.h>",   
        "<winsock2.h>", 
        "<sys/socket.h>",
        "<netinet/in.h>",
};

bool prohibited_check(const std::string& Temp_Code, const std::vector<std::string>& prohibited_header) {
    for (const auto& header : prohibited_header) {
        if (Temp_Code.find(header) != std::string::npos) {
            return false;  // Prohibited Header Detected
        }
    }
    return true;
}

int main(int argc, char* argv[]) {
    if (argc != 3) {
        std::cout << "Invalid args Input\nTerminated...\n";
        return 1;  // Error Code 1
    }

    TempCase TC;
    TC.parseArgs(argv);

    std::ifstream inFile_TC(TC.tcPath, std::ios::in);
    if (!inFile_TC) {
        std::cout << "An Error occured when reading Temp Code, Terminated...\n";
        return 2;  // Error Code 2
    } else {
        std::string str;
        while (std::getline(inFile_TC, str)) {
            std::cout << str << std::endl;
        }
    }

    // std::string command = TC.tcPath + " < " + TC.tdPath;  // 這邊要先把測資獨立成一個檔案，一次一次測
    // std::system(command.c_str());
    
    return 0;
}

// g++ -o C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test.cpp
// C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test.exe C:\Users\eric2\Desktop\Plush-OJ\Test\Temp_Code\Hello.cpp C:\Users\eric2\Desktop\Plush-OJ\Test\Temp_JSON\PL.json