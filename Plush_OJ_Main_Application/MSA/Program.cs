using System;
using System.Diagnostics;

namespace Plush_OJ
{
    class MSA
    {
        static void Processor_SET()
        {
#pragma warning disable CA1416
            int countProcessors = Environment.ProcessorCount;
            Console.WriteLine($"Number of processors: {countProcessors}");
            if (countProcessors < 8)
            {
                Console.WriteLine("Under 8 Processors, Terminated...\n");
            }

            Process currentProcess = Process.GetCurrentProcess();
            currentProcess.ProcessorAffinity = (IntPtr)0xFF;    // 0xF = 1111 in binary

            Console.WriteLine($"Affinity Mask: {currentProcess.ProcessorAffinity}");
#pragma warning restore CA1416
        }
        public static void Main(string[] args)
        {
            if (!OperatingSystem.IsLinux() && !OperatingSystem.IsWindows())
            {
                Console.WriteLine("System Unsupported, Terminated...\n");
                return;
            }
            Processor_SET();
            Console.WriteLine(@"
============================================================= 
      Welcome to Plush::OJ Server Commandline Interface
=============================================================
            ");
        }
    }
}