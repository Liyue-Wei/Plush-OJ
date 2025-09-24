using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.IO;

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

        #region Password Hashing
        private protected static string GenerateSalt()
        {
            byte[] saltBytes = new byte[32];
            using (var rng = RandomNumberGenerator.Create())
            {
                rng.GetBytes(saltBytes);
            }
            return Convert.ToBase64String(saltBytes);
        }

        private protected static string ComputeHash(string password, string salt)
        {
            using (var sha256 = SHA256.Create())
            {
                byte[] saltedPassword = Encoding.UTF8.GetBytes(password + salt);
                byte[] hashBytes = sha256.ComputeHash(saltedPassword);
                return Convert.ToBase64String(hashBytes);
            }
        }
        #endregion
        private protected static bool LoginMGT()
        {
            AppConfig cfg;
            string configPath = "Plush_OJ_Main_Application/Config/config.json";
            if (!File.Exists(configPath))
            {
                Console.WriteLine("Configuration file not found, creating...");
                string defSalt = GenerateSalt();
                cfg = new AppConfig
                {
                    AdminACC = "admin",
                    PasswdHash = ComputeHash("admin", defSalt),
                    Salt = defSalt
                };

                string? directoryPath = Path.GetDirectoryName(configPath);
                if (!string.IsNullOrEmpty(directoryPath) && !Directory.Exists(directoryPath))
                {
                    Directory.CreateDirectory(directoryPath);
                }
                
                string jsonString = JsonSerializer.Serialize(cfg, new JsonSerializerOptions { WriteIndented = true });
                File.WriteAllText(configPath, jsonString);
            }

            string? adminACC, passWD = string.Empty;
            Console.Write("Enter Administrator Account: ");
            adminACC = Console.ReadLine();
            Console.Write("\nEnter Password: ");

            ConsoleKeyInfo key;
            do
            {
                key = Console.ReadKey(true);
                if (!char.IsControl(key.KeyChar))
                {
                    passWD += key.KeyChar;
                    Console.Write("*");
                }
                else
                {
                    if (key.Key == ConsoleKey.Backspace && passWD.Length > 0)
                    {
                        passWD = passWD.Substring(0, passWD.Length - 1);
                        Console.Write("\b \b");
                    }
                }
            } while (key.Key != ConsoleKey.Enter);

            if (string.IsNullOrEmpty(adminACC) || string.IsNullOrEmpty(passWD))
            {
                Console.WriteLine("\nAdministrator Account or Password cannot be Null or Empty, Login access denied...\n");
                return false;
            }

            return false;
        } 

        public static void Main(string[] args)
        {
            if (!OperatingSystem.IsLinux() && !OperatingSystem.IsWindows())
            {
                Console.WriteLine("Error : System Unsupported, Terminated...\n");
                return;
            }
            ProcessorSet();

            #region initScreen 
            Console.WriteLine(@"
========================================================================================================================
             ______________   ___                                                                                       
            /_________    /  /  /                                                                                       
                     /   /  /  /                                                                                        
                    /   /  /  /                                                                                         
         __________/   /  /  /     ________   __                           __                  __________         __    
        /  ___________/  /  /     |   __   | |  |                         |  |         _  _   |   ____   |       |  |   
       /  /   _______   /  /      |  |__|  | |  |  __    __       ______  |  |        |_||_|  |  |    |  |       |  |   
      /  /   /  __  /  /  /       |   _____| |  | |  |  |  |     |   ___| |  |_____           |  |    |  |       |  |   
     /  /   /  / /_/  /  /        |  |       |  | |  |  |  |     |  |     |   __   |   _  _   |  |    |  |       |  |   
    /  /   /  /______/  /         |  |       |  | |  |__|  |  ___|  |     |  |  |  |  |_||_|  |  |____|  |  _____|  |   
   /__/   /____________/          |__|       |__| |________| |______|     |__|  |__|          |__________| |________|   
                                                                                                                        
                                   Welcome to Plush::OJ Server Commandline Interface                                    
======================================================================================================================== 
            ");
            Thread.Sleep(1000);
        MainMenu:
            Console.WriteLine(@"
    ┌──────────────────────────────────┐
    │            MAIN MENU             │
    ├──────────────────────────────────┤
    │ [1] Administrator Login          │
    │                                  │
    │ [0] Exit                         │
    └──────────────────────────────────┘
            ");

            string? initFunc = Console.ReadLine();
            switch (initFunc)
            {
                case "1":
                    Console.WriteLine("Administrator Login: \n");
                    if (LoginMGT())
                    {
                        Console.WriteLine("Login succeed.");
                    }
                    else
                    {
                        Console.WriteLine("Login failed, Terminated...");
                        return;
                    }
                    break;

                case "0":
                    Console.WriteLine("Exit...");
                    return;

                default:
                    Console.WriteLine("Unknow command, Please try again.");
                    Thread.Sleep(750);
                    Console.Clear();
                    goto MainMenu;
            }
            #endregion
        }
    }

    public class AppConfig
    {
        public string? AdminACC { get; set; }
        public string? PasswdHash { get; set; }
        public string? Salt { get; set; }
    }

    class API
    {
        private static void ConnDB()    // Database Connecter
        {

        }

        private static void ConnFF()    // FOFE FW Connecter
        {

        }

        static void ConnWE()    // Web Engine Connecter
        {

        }
    }
}