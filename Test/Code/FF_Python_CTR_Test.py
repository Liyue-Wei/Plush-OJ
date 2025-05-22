#coding=UTF-8
import time
import json
import os
import sys
import subprocess

def parseTD(TDPath):
    try:
        with open(TDPath, 'r') as tdIN:
            pass
    except FileNotFoundError:
        print(f"Test data file not found: {TDPath}")
        return None
    except json.JSONDecodeError:
        print(f"Error decoding JSON from test data file: {TDPath}")
        return None


class CPP_CTR:
    def __init__(self, tempCodePath, testDataPath, info):
        self.tempCodePath = tempCodePath
        self.testDataPath = testDataPath
        self.info = info

    def run(self):
        # Run the C++ code with the provided arguments
        command = f"g++ {self.tempCodePath} -o output && ./output {self.testDataPath} {self.info}"
        process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, stderr = process.communicate()
        return stdout.decode('utf-8'), stderr.decode('utf-8')

def main():
    currPath = os.path.abspath(os.path.dirname(__file__))
    args = [str(sys.argv[1]), str(sys.argv[2]), str(sys.argv[3])]  # TempCode Path, TestData Path, Info

if __name__ == '__main__':
    main()