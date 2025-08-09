// ...existing code...
import time
import json
import os
import sys
import subprocess
import psutil # 新增导入
import threading # 新增导入

global execTime
// ...existing code...
class CPP_CTR:
// ...existing code...
    def execute(self, execPath):
        execPath = f"{execPath}{self.info}.exe"
        input_list = [] # 避免与内置 input 冲突，重命名
        inputType = []
        answer_list = [] # 避免与未来可能的内置 output 冲突，重命名
        answerType = []
        output_list = [] # 避免与内置 output 冲突，重命名
        
        # 清空上一轮测试可能遗留的数据
        global execTime, memUsage
        execTime = []
        memUsage = []

        TT, TL_str, ML_str, TD = self.JSON_Handler()

        try:
            timeout_seconds = float(TL_str)
        except ValueError:
            print(f"错误：无效的时间限制格式 '{TL_str}'。必须是数字。已终止...")
            return 9

        try:
            # 假设 ML_str 是以 MB 为单位的字符串
            memory_limit_mb = float(ML_str)
            memory_limit_bytes = memory_limit_mb * 1024 * 1024
        except ValueError:
            print(f"错误：无效的内存限制格式 '{ML_str}'。必须是数字 (MB)。已终止...")
            return 9

        if TT == "OTEST":
            for item in TD:
                input_list.append(item['Input']['arg_1'])
                inputType.append(item['Input_Type']['arg_1'])
                answer_list.append(str(item['Answer']['arg_1']).strip()) # 转换为字符串并去除空白
                answerType.append(item['Answer_Type']['arg_1'])
        elif TT == "UEOF":
            for item in TD:
                inputs_ueof = [str(v).strip() for v in item['Input'].values()]
                inputType_ueof = list(item['Input_Type'].values()) # 假设类型不需要转换
                answers_ueof = [str(v).strip() for v in item['Answer'].values()]
                answerType_ueof = list(item['Answer_Type'].values())
                input_list.append(inputs_ueof) # UEOF 的输入本身可能是一个列表
                inputType.append(inputType_ueof)
                answer_list.append(answers_ueof) # UEOF 的答案本身可能是一个列表
                answerType.append(answerType_ueof)
        
        print("Time Limit : ", timeout_seconds, "s")
        print("Memory Limit : ", memory_limit_mb, "MB")
        print("Input : ", input_list)
        print("Answer : ", answer_list)
        print("Exection Path : ", execPath)
        
        if TT == "OTEST":
            for arg_input_data in input_list:
                process = None
                monitor_data = {
                    'pid': None,
                    'peak_memory': 0,
                    'stop_event': threading.Event(),
                    'error': None
                }

                def _memory_monitor_worker(data):
                    try:
                        p = psutil.Process(data['pid'])
                        while not data['stop_event'].is_set():
                            if not p.is_running() or p.status() == psutil.STATUS_ZOMBIE:
                                break
                            try:
                                # RSS (Resident Set Size) 是一个常用的跨平台指标
                                current_mem = p.memory_info().rss
                                if current_mem > data['peak_memory']:
                                    data['peak_memory'] = current_mem
                            except (psutil.NoSuchProcess, psutil.AccessDenied):
                                break 
                            except Exception as e_mem:
                                data['error'] = f"内存轮询错误: {e_mem}"
                                break
                            time.sleep(0.02) # 轮询间隔，可以调整
                    except psutil.NoSuchProcess:
                        pass 
                    except Exception as e_thread_init:
                        data['error'] = f"内存监控线程初始化错误: {e_thread_init}"

                start_time = time.time()
                monitor_thread = None
                try:
                    process = subprocess.Popen(
                        execPath,
                        stdin=subprocess.PIPE,
                        stdout=subprocess.PIPE,
                        stderr=subprocess.PIPE,
                        text=True,
                        creationflags=subprocess.CREATE_NO_WINDOW if os.name == 'nt' else 0
                    )
                    monitor_data['pid'] = process.pid
                    
                    monitor_thread = threading.Thread(target=_memory_monitor_worker, args=(monitor_data,))
                    monitor_thread.start()

                    stdout_value, stderr_value = process.communicate(input=str(arg_input_data), timeout=timeout_seconds)
                    end_time = time.time()

                except subprocess.TimeoutExpired:
                    monitor_data['stop_event'].set()
                    if process:
                        process.kill()
                    if monitor_thread and monitor_thread.is_alive():
                        monitor_thread.join(timeout=0.5)
                    
                    current_peak_memory_bytes = monitor_data.get('peak_memory', 0)
                    memUsage.append(current_peak_memory_bytes)
                    print(f"Time Limit Exceeded. Peak memory during run: {current_peak_memory_bytes / (1024*1024):.2f} MB. Terminated...")
                    return 5
                
                except Exception as e_comm:
                    monitor_data['stop_event'].set()
                    if process and process.poll() is None:
                        process.kill()
                    if monitor_thread and monitor_thread.is_alive():
                        monitor_thread.join(timeout=0.5)
                    
                    current_peak_memory_bytes = monitor_data.get('peak_memory', 0)
                    memUsage.append(current_peak_memory_bytes)
                    print(f"执行或通信错误: {e_comm}. Peak memory: {current_peak_memory_bytes / (1024*1024):.2f} MB. Terminated...")
                    return 9

                finally:
                    monitor_data['stop_event'].set()
                    if monitor_thread and monitor_thread.is_alive():
                        monitor_thread.join(timeout=0.5)
                    # communicate() 之后，如果没超时，进程应该已经结束
                    # 如果进程仍然存在 (不应该发生)，则确保终止
                    if process and process.poll() is None:
                        process.kill()
                        process.wait(timeout=0.5)


                current_peak_memory_bytes = monitor_data['peak_memory']
                if monitor_data['error']:
                    print(f"警告：内存监控遇到问题: {monitor_data['error']}")
                memUsage.append(current_peak_memory_bytes)

                if current_peak_memory_bytes > memory_limit_bytes:
                    print(f"Memory Limit Exceeded: Used {current_peak_memory_bytes / (1024*1024):.2f} MB, Limit {memory_limit_mb:.2f} MB. Terminated...")
                    return 6

                if process.returncode != 0:
                    err_msg = stderr_value.strip() if stderr_value else "无错误输出"
                    print(f"Execution Error (return code {process.returncode}): {err_msg}, Terminated...")
                    return 9 
                else:
                    execTime.append(end_time - start_time)
                    output_list.append(stdout_value.strip()) # 去除输出末尾的空白
            
            print("Execution Time per run: ", [f"{t:.4f}s" for t in execTime])
            print("Peak Memory Usage per run: ", [f"{m/(1024*1024):.2f}MB" for m in memUsage])
            print("Output : ", output_list)
            
            if output_list == answer_list:
                return 0 
            else:
                print(f"Wrong Answer. Expected: {answer_list}, Got: {output_list}")
                return 7
        
        elif TT == "UEOF":
            # UEOF 的内存监控逻辑类似，如果它也执行子进程
            print("UEOF test type - memory monitoring not fully implemented in this example for UEOF.")
            # 示例：为 UEOF 的每个输入集执行，并进行内存监控
            # for input_set in input_list:
            #   ... (类似的 Popen, memory monitor thread, communicate, check logic) ...
            #   if error: return error_code
            # if all_ueof_outputs_match_answers: return 0 else return 7
            return 10 # 暂时返回 UEOF 未完全支持
        
        return 9 # 意外的执行路径

# ...existing code...
def main():
# ...existing code...
            errCode = CPP_CTR(tempCode, TD, args[2]).execute(tempDir)   
            match errCode:
                case 0:
                    print("Accepted, Terminated...")
                    # main 函数现在可以访问全局的 execTime 和 memUsage
                    print(f"Final Execution Times: {execTime}")
                    print(f"Final Memory Usages: {memUsage}")
                    return 0 # Success Code, execTime 和 memUsage 是全局的
                case 5:
                    print("Time Limit Exceeded, Terminated...")
                    print(f"Final Execution Times: {execTime}")
                    print(f"Final Memory Usages: {memUsage}")
                    return 5  # Error Code 5
                case 6: # 新增 MLE 处理
                    print("Memory Limit Exceeded, Terminated...")
                    print(f"Final Execution Times: {execTime}")
                    print(f"Final Memory Usages: {memUsage}")
                    return 6  # Error Code 6
                case 7:
                    print("Wrong Answer, Terminated...")
// ...existing code...
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
Error Code 10 : Unsupported Language / Test Type

python Test\Code\FF_Python_CTR_Test.py Test\Temp_Code\QN001A-XXXXX-0000-00-00.cpp Test\Temp_JSON\QN001A.json QN001A-XXXXX-0000-00-00
python Test\Code\FF_Python_CTR_Test.py Test\Temp_Code\QN008A-XXXXX-0000-00-00.cpp Test\Temp_JSON\QN008A.json QN008A-XXXXX-0000-00-00
'''