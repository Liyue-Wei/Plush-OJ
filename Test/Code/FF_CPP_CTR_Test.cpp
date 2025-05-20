/*
2025/05/20 - FF_CPP_CTR 暫停開發，轉向 FF_Python_CTR
*/

#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include <cstdlib>
#include <sstream>
#include <windows.h>

struct TempCase {
    std::string QN;  // Question Number
    std::string UID;  // UserID
    std::string Time;
    std::string tcPath;  // Temp Code Path
    std::string tdPath;  // Test DATA Path
    std::string currPath;  // Current Path
    std::string TT;  // Test Type, OTEST/UEOF, One TestDATA every single time/Until EOF
    std::string taPath;  // Test Answer Path
    // int tdQTY;  // Test DATA Quantity

    void parseArgs(char* argv[]) {  // currPath, tcPath, TT, tdPath, taPath, QN, UID, Time
        currPath = argv[0];
        tcPath = argv[1];
        TT = argv[2];
        tdPath = argv[3];
        taPath = argv[4];
        QN = argv[5];
        UID = argv[6];
        Time = argv[7];
    }

    std::string parsePath(std::string QN, std::string UID, std::string Time) {
        size_t lastSlash = currPath.find_last_of("\\/");
        std::string parentPath = currPath.substr(0, lastSlash);
        lastSlash = parentPath.find_last_of("\\/");
        parentPath = parentPath.substr(0, lastSlash);
        std::string TCFP = parentPath + "\\Temp_Code\\" + QN + "-" + UID + "-" + Time;
        // std::cout << TCFP << std::endl;
        return TCFP;  // Temp Code File Path
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
        "<unistd.h>",
        "<windows.h>", 
        "<signal.h>", 
        "<ctime>"
};

bool PCP(const std::string& Temp_Code, const std::vector<std::string>& prohibited_header) {  // prohibited_checking_process
    for (const auto& header : prohibited_header) {
        if (Temp_Code.find(header) != std::string::npos) { return false; }
    }
    return true;
}

bool checkShell(FILE* shell) {
    if (shell == nullptr) {
        std::cout << "System Error, Terminated...\n";
        return false;
    }
    return true;
}

std::string Judge(std::string TCFP, std::string TD, std::string TAP) {  // Temp Code File Path, TestDATA, Test Answer Path
    std::string command_judge = TCFP + ".exe";
    std::FILE* shell = _popen(command_judge.c_str(), "w");

    return nullptr;
}

int main(int argc, char* argv[]) {
    if (argc != 8) {
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

    std::string Temp_Code((std::istreambuf_iterator<char>(inFile_TC)), (std::istreambuf_iterator<char>()));
    std::string TestDATA((std::istreambuf_iterator<char>(inFile_TD)), (std::istreambuf_iterator<char>()));
    inFile_TC.close();
    inFile_TD.close(); 
    if (!PCP(Temp_Code, prohibited_header)) {
        std::cout << "Prohibited Header Detected, Terminated...\n";
        return 3;  // Error Code 3
    }

    std::string TCFP = TC.parsePath(TC.QN, TC.UID, TC.Time);  // Temp Code File Path
    std::string command_compile = "g++ -o " + TCFP + " " + TC.tcPath;
    int compile_result = std::system(command_compile.c_str());
    if (compile_result != 0) {
        std::cout << "Compile Error, Terminated...\n";
        return 4;  // Error Code 4
    }

    /*
    std::string command_judge = TCFP + ".exe";
    std::FILE *shell;
    std::istringstream iss(TestDATA);
    std::string line;
    if (TC.TT == "OTEST") {
        while (std::getline(iss, line)) {  
            shell = _popen(TCFP.c_str(), "w");
            if (!checkShell(shell)) return 5;  // Error Code 5
            // std::cout << line << std::endl;
            fwrite(line.c_str(), 1, line.size(), shell);
            _pclose(shell);
        }
    } else if (TC.TT == "UEOF") {
        shell = _popen(TCFP.c_str(), "w");
        if (!checkShell(shell)) return 5;  // Error Code 5
        fwrite(TestDATA.c_str(), 1, TestDATA.size(), shell);
        _pclose(shell);
    } else {
        std::cout << "Unknow Error, Terminated...\n";
        return 6;  // Error Code 6
    }
    */
    
    return 0;
}

/*
Error Code 1 : Invalid args Input
Error Code 2 : Reading Temp Code
Error Code 3 : Prohibited Header Detected
Error Code 4 : Compile Error
Error Code 5 : System Error - Shell can't be opened
Error Code 6 : Unknow Test Type
Error Code 7 : 
Error Code 8 : 
Error Code 9 : 

g++ -o C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test.cpp
C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test.exe C:\Users\eric2\Desktop\Plush-OJ\Test\Temp_Code\Hello.cpp OTEST C:\Users\eric2\Desktop\Plush-OJ\Test\Temp_JSON\PL.json QN001A 00001 0000-00-00
C:\Users\eric2\Desktop\Plush-OJ\Test\Code\FF_CPP_CTR_Test.exe C:\Users\eric2\Desktop\Plush-OJ\Test\Temp_Code\EOF_Test.cpp UEOF C:\Users\eric2\Desktop\Plush-OJ\Test\Temp_JSON\PL.json QN002A 00001 0000-00-00
*/