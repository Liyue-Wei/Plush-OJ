#coding=UTF-8
import time
import json
import os
import sys
import subprocess

def parseTD(TDPath):
    try:
        with open(TDPath, 'r', encoding='utf-8') as TD:
            data = json.load(TD)
            return data
    except FileNotFoundError:
        print("Test Data File Not Found, Terminated...")
        return 9  # Error Code 9
    except json.JSONDecodeError:
        print("JSON Decode Error, Terminated...")
        return 9  # Error Code 9

class CPP_CTR:
    def __init__(self, tempCode, testData):
        self.tempCode = tempCode
        self.testData = testData

    prohibited_header = [
        "<fstream>",
        "<cstdlib>",
        "<stdlib.h>",
        "<filesystem>",
        "<process.h>",
        "<unistd.h>",
        "<winsock2.h>",
        "<sys/socket.h>",
        "<netinet/in.h>",
        "<windows.h>",
        "<signal.h>",
        "<ctime>"
    ]

    def PCP(self):  # prohibited_checking_process
        if any(header in self.tempCode for header in CPP_CTR.prohibited_header):
            print("Prohibited Header Detected, Terminated...")
            return 3  # Error Code 3
        else:
            return 0

def main():
    if len(sys.argv) != 4:
        print("Invalid args Input, Terminated...")
        return 1  # Error Code 1
    
    currPath = os.path.abspath(os.path.dirname(__file__))
    args = [str(sys.argv[1]), str(sys.argv[2]), str(sys.argv[3])]  # TempCode Path, TestData Path, Info
    TD = parseTD(args[1])  
    lang = args[0].split('.')[-1]  
    match lang:
        case 'cpp':
            try:
                with open(args[0], 'r', encoding='utf-8') as TC:
                    tempCode = TC.read()
                    TC.close()
            except FileNotFoundError:
                print("An Error occured when reading Temp Code, Terminated...")
                return 2  # Error Code 2
            
            CPP_CTR(tempCode, TD).PCP()
            
        case 'c':
            pass

        case 'py':
            pass

        case _:
            print("Unsupported Language, Terminated...")
            return 10  # Error Code 10

if __name__ == '__main__':
    main()

'''
Error Code 1 : Invalid args Input
Error Code 2 : Reading Temp Code
Error Code 3 : Prohibited Header Detected
Error Code 4 : Compile Error
Error Code 5 : 
Error Code 6 : 
Error Code 7 : 
Error Code 8 : 
Error Code 9 : Unexpected System Error
Error Code 10: Unsupported Language

python Test\Code\FF_Python_CTR_Test.py Test\Temp_Code\QN001A-XXXXX-0000-00-00.cpp Test\Temp_JSON\QN001A.json User
'''