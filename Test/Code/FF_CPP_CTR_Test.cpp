#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include <cstdlib>

struct TempCase {
    std::string tcPath;  // Temp Code Path
    int tdQTY;  // Test DATA Quantity
    std::string tdPath;  // Test DATA Path

    void parseArgs(char* argv[]) {
        tcPath = argv[1];
        tdQTY = std::atoi(argv[2]);
        tdPath = argv[3];
    }
};

std::vector<std::string> prohibited_header = {
        "<fstream>", 
        "<cstdlib>",
        "<stdlib.h>",
        "<filesystem>",
        "<process.h>", 
        "<unistd.h>",   
        "<winsock2.h>", 
        "<sys/socket.h>",
        "<netinet/in.h>",
};

bool PCP(const std::string& Temp_Code, const std::vector<std::string>& prohibited_header) {  // prohibited_checking_process
    for (const auto& header : prohibited_header) {
        if (Temp_Code.find(header) != std::string::npos) { return false; }
    }
    return true;
}

int main(int argc, char* argv[]) {
    if (argc != 4) {
        std::cout << "Invalid args Input, Terminated...\n";
        return 1;  // Error Code 1
    }

    TempCase TC;
    TC.parseArgs(argv);

    std::ifstream inFile_TC(TC.tcPath, std::ios::in);
    std::ifstream inFile_TD(TC.tdPath, std::ios::in);
    if (!inFile_TC || !inFile_TD) {
        std::cout << "An Error occured when reading Temp Code, Terminated...\n";
        return 2;  // Error Code 2
    } 

    try {
        std::string Temp_Code((std::istreambuf_iterator<char>(inFile_TC)), (std::istreambuf_iterator<char>()));
        inFile_TC.close();
        if (!PCP(Temp_Code, prohibited_header)) {
            std::cout << "Prohibited Header Detected, Terminated...\n";
            return 3;  // Error Code 3    
    }

    /*
    std::string Temp_Code((std::istreambuf_iterator<char>(inFile_TC)), (std::istreambuf_iterator<char>()));
    inFile_TC.close();
    if (!PCP(Temp_Code, prohibited_header)) {
        std::cout << "Prohibited Header Detected, Terminated...\n";
        return 3;  // Error Code 3
    }

    // 先將Test DATA讀進來，再進行分裝
    std::string TestDATA((std::istreambuf_iterator<char>(inFile_TD)), (std::istreambuf_iterator<char>()));
    inFile_TD.close();
    */ 

    try {
        std::string command_compile = "g++ -o " + std::string("C:\\Users\\eric2\\Desktop\\Plush-OJ\\Test\\Temp_Code\\Hello") + TC.tcPath;
        std::system(command_compile.c_str());
    } catch (std::exception e) {
        std::cout << "Compile Error, Terminated...\n";
        return 4;  // Error Code 4
    }

    std::string command = std::string("C:\\Users\\eric2\\Desktop\\Plush-OJ\\Test\\Temp_Code\\Hello.exe") + " < " + TC.tdPath;  // 這邊要先把測資獨立成一個檔案，一次一次測
    std::system(command.c_str());
    
    return 0;
}

/*
Error Code 1 : Invalid args Input
Error Code 2 : Reading Temp Code
Error Code 3 : Prohibited Header Detected
Error Code 4 : Compile Error

g++ -o C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test.cpp
C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test.exe 
*/