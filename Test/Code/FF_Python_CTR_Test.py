#coding=UTF-8
import time
import json
import os
import sys
import subprocess

def main():
    currPath = os.path.abspath(os.path.dirname(__file__))
    args = [str(sys.argv[1]), str(sys.argv[2]), str(sys.argv[3])]  # TempCode Path, TestData Path, Info

if __name__ == '__main__':
    main()