#coding=UTF-8
import time
import json
import os
import sys
import subprocess

global execTime
execTime = []  # Execution Time
memUsage = []  # Memory Usage

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
    def __init__(self, tempCode, testData, info):
        self.tempCode = tempCode
        self.testData = testData
        self.info = info

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

    def handlerIO(self):  # IO Handler
        pass

    def PCP(self):  # prohibited_checking_process
        if any(header in self.tempCode for header in CPP_CTR.prohibited_header):
            return True
        else:
            return False
        
    def compile(self, tdPath, tempDir):
        exitCode = os.system(f"g++ -o {tempDir}\\{self.info} {tdPath} -std=c++23")
        if exitCode != 0:
            return False
        else:
            return True

    def RC(self):  # resource_counter
        pass
        
    def JSON_Handler(self):
        QN = self.testData['Question_Number']
        TT = self.testData['Test_Type']
        TL = self.testData['Time_Limit']
        ML = self.testData['Memory_Limit']
        TD = self.testData['Test_Data']

        # print("Question Number : ", QN, '\n')
        # print("Test Type : ", TT, '\n')
        # print("Quantity of Test Data : ", QTD, '\n')
        # print("Test Data : ")
        # for item in TD:
        #     print(item)
        # print('\n', "\bTime Limit : ", TL, '\n')
        # print("Memory Limit : ", ML, '\n')

        return TT, TL, ML, TD

    def execute(self, execPath):
        execPath = f"{execPath}{self.info}.exe"
        input = []
        inputType = []
        answer = []
        answerType = []
        output = []
        TT, TL, ML, TD = self.JSON_Handler()
        if TT == "OTEST":
            for item in TD:
                input.append(item['Input']['arg_1'])
                inputType.append(item['Input_Type']['arg_1'])
                answer.append(item['Answer']['arg_1'])
                answerType.append(item['Answer_Type']['arg_1'])
        elif TT == "UEOF":
            for item in TD:
                inputs = list(item['Input'].values())     
                inputType = list(item['Input_Type'].values())
                answers = list(item['Answer'].values())   
                answerType = list(item['Answer_Type'].values())
                input.append(inputs)
                inputType.append(inputType)
                answer.append(answers)
                answerType.append(answerType)

        print("Time Limit : ", TL)
        print("Memory Limit : ", ML)
        print("Input : ", input)
        print("Answer : ", answer)
        print("Exection Path : ", execPath)
        
        if TT == "OTEST":
            for arg in input:
                start_time = time.time()
                process = subprocess.Popen(execPath, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
                try:
                    value, _ = process.communicate(input=arg, timeout=int(TL))
                    end_time = time.time()
                    if process.returncode != 0:
                        print(f"Execution Error: {process.stderr.read()}, Terminated...")
                        return 9  # Error Code 9
                    else:
                        execTime.append(end_time - start_time)
                        output.append(value)
                        process.kill()
                except subprocess.TimeoutExpired:
                    process.kill()
                    return 5  # Error Code 5
                except subprocess.CalledProcessError as e:  # Unexpected execution error
                    print(f"Execution Error: {e}, Terminated...")
                    return 9  # Error Code 9
            
            print("Execution Time : ", execTime)
            print("Output : ", output)
            return 0  if output == answer else 7  # Error Code 7 
        
        elif TT == "UEOF":
            return 10  # UEOF 還不能運作
            for i in range(len(input)):
                start_time = time.time()
                process = subprocess.Popen(execPath, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
                try:
                    value, _ = process.communicate(input=input[i], timeout=int(TL))
                    end_time = time.time()
                    if process.returncode != 0:
                        print(f"Execution Error: {process.stderr.read()}, Terminated...")
                        return 9  # Error Code 9
                    else:
                        execTime.append(end_time - start_time)
                        output.append(value)
                        process.kill()
                except subprocess.TimeoutExpired:
                    process.kill()
                    return 5
                except subprocess.CalledProcessError as e:  # Unexpected execution error
                    print(f"Execution Error: {e}, Terminated...")
                    return 9  # Error Code 9
            
            print("Execution Time : ", execTime)
            print("Output : ", output)
            return 0 if output == answer else 7  # Error Code 7
        
def main():
    if len(sys.argv) != 4:
        print("Invalid args Input, Terminated...")
        return 1  # Error Code 1
    
    currPath = os.path.abspath(os.path.dirname(__file__))
    tempDir = currPath.split('\\')[0:-1]
    tempDir = '\\'.join(tempDir) + '\\Temp\\'
    if not os.path.exists(tempDir):
        os.makedirs(tempDir)

    args = [str(sys.argv[1]), str(sys.argv[2]), str(sys.argv[3])]  # TempCode Path, TestData Path, Info
    TD = parseTD(args[1])  
    if TD == 9:
        print("Unexpected System Error, Terminated...")
        return 9  # Error Code 9
    
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
            
            if CPP_CTR(tempCode, TD, args[2]).PCP():
                print("Prohibited Header Detected, Terminated...")
                return 3  # Error Code 3
            
            if not CPP_CTR(tempCode, TD, args[2]).compile(args[0], tempDir):
                print("Compile Error, Terminated...")
                return 4  # Error Code 4

            # CPP_CTR(tempCode, TD, args[2]).JSON_Handler()
            errCode = CPP_CTR(tempCode, TD, args[2]).execute(tempDir)   
            match errCode:
                case 0:
                    print("Accepted, Terminated...")
                    return 0, execTime  # Success Code
                case 5:
                    print("Time Limit Exceeded, Terminated...")
                    return 5  # Error Code 5
                case 6:
                    print("Memory Limit Exceeded, Terminated...")
                    return 6  # Error Code 6
                case 7:
                    print("Wrong Answer, Terminated...")
                    return 7  # Error Code 7
                case 10:
                    print("Unsupported Test Type, Terminated...")
                    return 10  # Error Code 10
                case _:
                    print("Unexpected System Error, Terminated...")
                    return 9  # Error Code 9             
            
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
Error Code 5 : Time Limit Exceeded
Error Code 6 : Memory Limit Exceeded
Error Code 7 : Wrong Answer 
Error Code 8 : 
Error Code 9 : Unexpected System Error
Error Code 10 : Unsupported Language

python Test\Code\FF_Python_CTR_Test.py Test\Temp_Code\QN001A-XXXXX-0000-00-00.cpp Test\Temp_JSON\QN001A.json QN001A-XXXXX-0000-00-00
python Test\Code\FF_Python_CTR_Test.py Test\Temp_Code\QN008A-XXXXX-0000-00-00.cpp Test\Temp_JSON\QN008A.json QN008A-XXXXX-0000-00-00
'''