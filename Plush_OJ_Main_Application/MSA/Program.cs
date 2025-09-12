using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Net;

namespace Plush_OJ
{
    class MSA    // Main Server Application
    {
        static void ProcessorSet()
        {
#pragma warning disable CA1416
            int countProcessors = Environment.ProcessorCount;
            Process currentProcess = Process.GetCurrentProcess();
            Console.WriteLine($"Number of processors: {countProcessors}");
            if (countProcessors < 8)
            {
                Console.WriteLine("Error : Under 8 Processors, Terminated...\n");
                Environment.Exit(1);
            }

            string? lowPerfModeWarning = countProcessors < 16 ? "Warning : Under 16 Processors, Application running on low performance mode." : null;
            if (lowPerfModeWarning != null) Console.WriteLine(lowPerfModeWarning);
            currentProcess.ProcessorAffinity = (lowPerfModeWarning != null) ? (IntPtr)0xFF : (IntPtr)0xFFFF;    // 0xFF = 11111111 in binary

            Console.WriteLine($"Affinity Mask: {currentProcess.ProcessorAffinity}");
#pragma warning restore CA1416
        }

        public static void Main(string[] args)
        {
            if (!OperatingSystem.IsLinux() && !OperatingSystem.IsWindows())
            {
                Console.WriteLine("Error : System Unsupported, Terminated...\n");
                return;
            }
            ProcessorSet();
            Console.WriteLine(@"
================================================================= 
        Welcome to Plush::OJ Server Commandline Interface
=================================================================
            ");
        }
    }

    class API
    {
        
    }
}