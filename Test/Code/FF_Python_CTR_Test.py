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

    except FileNotFoundError:
        return None
    except json.JSONDecodeError:
        return None

class CPP_CTR:
    def __init__(self):
        pass

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

    def PCP():  # prohibited_checking_process
        pass

def main():
    if len(sys.argv) != 4:
        print("Invalid args Input, Terminated...")
        return 1  # Error Code 1
    
    currPath = os.path.abspath(os.path.dirname(__file__))
    args = [str(sys.argv[1]), str(sys.argv[2]), str(sys.argv[3])]  # TempCode Path, TestData Path, Info
    lang = args[0].split('.')[-1]  

    match lang:
        case 'cpp':
            try:
                with open(args[0], 'r', encoding='utf-8') as TC:
                    tempCode = TC.read()
                    print(tempCode)
                    # for line in tempCode.splitlines():
                    #     print(line)

            except FileNotFoundError:
                print("An Error occured when reading Temp Code, Terminated...")
                return 2  # Error Code 2
            
        case 'c':
            pass

        case 'py':
            pass

        case _:
            print("Unknown Test Type, Terminated...")
            return 6  # Error Code 6

if __name__ == '__main__':
    main()

'''
Error Code 1 : Invalid args Input
Error Code 2 : Reading Temp Code
Error Code 3 : Prohibited Header Detected
Error Code 4 : Compile Error
Error Code 5 : System Error - Shell can't be opened
Error Code 6 : Unknow Test Type
Error Code 7 : 
Error Code 8 : 
Error Code 9 : 
Error Code 10: 

python Test\Code\FF_Python_CTR_Test.py Test\Temp_Code\QN001A-XXXXX-0000-00-00.cpp Test\Temp_JSON\QN001A.json User
'''