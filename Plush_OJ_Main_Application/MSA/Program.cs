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
        static void ProcessorSet()    // Only for Windows Platform
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
#pragma warning disable CS8600
#pragma warning disable CS8602
#pragma warning disable CS8604

            AppConfig cfg;
            string configPath = "Config/config.json";
            if (!File.Exists(configPath))
            {
                Console.WriteLine("Configuration file not found, creating...");
                const string defPassWD = "Plush_OJ_Default_Password";
                string defSalt = GenerateSalt();
                cfg = new AppConfig
                {
                    AdminACC = "admin",
                    PasswdHash = ComputeHash(defPassWD, defSalt),
                    Salt = defSalt
                };

                string? directoryPath = Path.GetDirectoryName(configPath);
                if (!string.IsNullOrEmpty(directoryPath) && !Directory.Exists(directoryPath))
                {
                    Directory.CreateDirectory(directoryPath);
                }

                string jsonString = JsonSerializer.Serialize(cfg, new JsonSerializerOptions { WriteIndented = true });    // formate as json file
                File.WriteAllText(configPath, jsonString);    // write into json file
                Console.WriteLine($"\nDefault Administrator Account created: admin\nDefault Password created: {defPassWD}\nPlease change the password after your first login for security reasons.\n\n");
            }
            else
            {
                string jsonString = File.ReadAllText(configPath);
                cfg = JsonSerializer.Deserialize<AppConfig>(jsonString);
            }

            string? adminACC, passWD;
            Console.Write("Enter Administrator Account: ");
            adminACC = Console.ReadLine();
            Console.Write("\nEnter Password: ");
            passWD = ReadPassword();
            Console.WriteLine();

            if (string.IsNullOrEmpty(adminACC) || string.IsNullOrEmpty(passWD))
            {
                Console.WriteLine("\nAdministrator Account or Password cannot be Null or Empty, Login access denied...\n");
                return false;
            }

            string inputHash = ComputeHash(passWD, cfg.Salt);
            if (adminACC == cfg.AdminACC && inputHash == cfg.PasswdHash)
            {
                Console.WriteLine("\n");
                return true;
            }
            else
            {
                Console.WriteLine("Wrong Administrator Account or Password, Login access denied...\n");
                return false;
            }

#pragma warning restore CS8600
#pragma warning restore CS8602
#pragma warning restore CS8604
        }

        private static void ResetAdmin()
        {
            Console.Clear();
            Console.WriteLine("--- Reset Administrator ---");

            string configPath = "Config/config.json";
            AppConfig cfg;
            try
            {
                string jsonString = File.ReadAllText(configPath);
                cfg = JsonSerializer.Deserialize<AppConfig>(jsonString)!;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\nAn error occurred while loading configuration: {ex.Message}");
                Thread.Sleep(750);
                return;
            }

            Console.Write("Enter CURRENT password to continue: ");
            string currentPassword = ReadPassword();
            Console.WriteLine();

            string currentInputHash = ComputeHash(currentPassword, cfg.Salt!);
            if (currentInputHash != cfg.PasswdHash)
            {
                Console.WriteLine("\nIncorrect current password. Reset failed.");
                Thread.Sleep(750);
                return;
            }

            Console.WriteLine("\nVerification successful. Please enter new credentials.");
            Console.Write("Enter new Administrator Account: ");
            string? newAdminACC = Console.ReadLine();

            Console.Write("Enter new password: ");
            string newPassword1 = ReadPassword();

            Console.Write("\nConfirm new password: ");
            string newPassword2 = ReadPassword();
            Console.WriteLine();
            if (string.IsNullOrEmpty(newAdminACC) || string.IsNullOrEmpty(newPassword1))
            {
                Console.WriteLine("\nAdministrator Account and Password cannot be empty. Reset failed.");
                Thread.Sleep(750);
                return;
            }

            if (newPassword1 != newPassword2)
            {
                Console.WriteLine("\nPasswords do not match. Reset failed.");
                Thread.Sleep(750);
                return;
            }

            try
            {
                string newSalt = GenerateSalt();
                string newHash = ComputeHash(newPassword1, newSalt);

                cfg.AdminACC = newAdminACC;
                cfg.Salt = newSalt;
                cfg.PasswdHash = newHash;

                string updatedJsonString = JsonSerializer.Serialize(cfg, new JsonSerializerOptions { WriteIndented = true });
                File.WriteAllText(configPath, updatedJsonString);

                Console.WriteLine("\nAdministrator account has been reset successfully.");
                Thread.Sleep(1500);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\nAn error occurred while resetting the administrator account: {ex.Message}");
                Thread.Sleep(1500);
            }
        }

        private static void SetAdminEmail()
        {
            Console.Clear();
            Console.WriteLine("--- Set Administrator Email ---");

            string configPath = "Config/config.json";
            AppConfig cfg;
            try
            {
                string jsonString = File.ReadAllText(configPath);
                cfg = JsonSerializer.Deserialize<AppConfig>(jsonString)!;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\nAn error occurred while loading configuration: {ex.Message}");
                Thread.Sleep(750);
                return;
            }

            Console.Write("Enter password to continue: ");
            string Password = ReadPassword();
            Console.WriteLine();

            string InputHash = ComputeHash(Password, cfg.Salt!);
            if (InputHash != cfg.PasswdHash)
            {
                Console.WriteLine("\nIncorrect password. Operation failed...");
                Thread.Sleep(750);
                return;
            }

            Console.WriteLine("\nVerification successful.");
            Console.Write("Enter new Administrator Email: ");
            string? newAdminEmail = Console.ReadLine();

            Console.Write("Enter Email Application Password: ");
            string newEmailPassword = ReadPassword();
            Console.WriteLine();

            if (string.IsNullOrEmpty(newAdminEmail) || string.IsNullOrEmpty(newEmailPassword))
            {
                Console.WriteLine("\nEmail and Email Password cannot be empty. Operation failed.");
                Thread.Sleep(750);
                return;
            }

            try
            {
                byte[] passwordBytes = Encoding.UTF8.GetBytes(newEmailPassword);
                string encodedPassword = Convert.ToBase64String(passwordBytes);    // Encrypted by Base64

                cfg.AdminEmail = newAdminEmail;
                cfg.EmailPasswd = encodedPassword;

                string updatedJsonString = JsonSerializer.Serialize(cfg, new JsonSerializerOptions { WriteIndented = true });
                File.WriteAllText(configPath, updatedJsonString);

                Console.WriteLine("\nAdministrator email has been set successfully.");
                Thread.Sleep(750);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"\nAn error occurred while saving the configuration: {ex.Message}");
                Thread.Sleep(750);
            }
        }
        
        private static string? GetDecryptedEmailPassword(AppConfig cfg)
        {
            if (string.IsNullOrEmpty(cfg.EmailPasswd))
            {
                return null;
            }

            try
            {
                byte[] encodedPasswordBytes = Convert.FromBase64String(cfg.EmailPasswd);
                return Encoding.UTF8.GetString(encodedPasswordBytes);
            }
            catch (FormatException)
            {
                return cfg.EmailPasswd;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Failed to decode email password: {ex.Message}");
                return null;
            }
        }

        private static string ReadPassword()
        {
            string password = string.Empty;
            ConsoleKeyInfo key;
            do
            {
                key = Console.ReadKey(true);
                if (!char.IsControl(key.KeyChar))
                {
                    password += key.KeyChar;
                    Console.Write("*");
                }
                else if (key.Key == ConsoleKey.Backspace && password.Length > 0)
                {
                    password = password.Substring(0, password.Length - 1);
                    Console.Write("\b \b");
                }
            } while (key.Key != ConsoleKey.Enter);
            return password;
        }

        private static void ShowAdminMenu()
        {
            bool exitAdminMenu = false;
            while (!exitAdminMenu)
            {
                Console.Clear();
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
                                                                                                                        
======================================================================================================================== 

    ┌──────────────────────────────────┐
    │        ADMINISTRATOR MENU        │
    ├──────────────────────────────────┤
    │ [1] Reset Administrator          │
    | [2] Set Administrator Email      |
    | [3] Change System Configuration  |
    | [4] Add Moderator                |
    │                                  │
    │ [0] Logout                       │
    └──────────────────────────────────┘
                ");
                Console.Write("Select an option: ");
                string? choice = Console.ReadLine();

                switch (choice)
                {
                    case "1":
                        ResetAdmin();
                        break;
                    case "2":
                        SetAdminEmail();
                        break;
                    case "3":
                        Console.WriteLine("\nNot available...");
                        Thread.Sleep(750);
                        break;
                    case "4":
                        Console.WriteLine("\nNot available...");
                        Thread.Sleep(750);
                        break;
                    case "0":
                        exitAdminMenu = true;
                        Console.WriteLine("\nLogging out...");
                        Thread.Sleep(750);
                        break;
                    default:
                        Console.WriteLine("\nUnknown command, Please try again.");
                        Thread.Sleep(750);
                        break;
                }
            }
        }

        public static void Main(string[] args)
        {
            if (!OperatingSystem.IsWindows())
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
    | [2] Start Server                 |
    │                                  │
    │ [0] Exit                         │
    └──────────────────────────────────┘
            ");

            Console.Write("Select an option: ");
            string? initFunc = Console.ReadLine();
            switch (initFunc)
            {
                case "1":
                    Console.WriteLine("Administrator Login: \n");
                    if (LoginMGT())
                    {
                        Console.WriteLine("Login succeed.\n");
                        Thread.Sleep(750);
                        ShowAdminMenu();
                        Console.Clear();
                        goto MainMenu;
                    }
                    else
                    {
                        Console.WriteLine("Login failed, Terminated...");
                        return;
                    }

                case "2":
                    Console.WriteLine("Server not avaliable...");
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

    public class AppConfig    // DTO
    {
        public string? AdminACC { get; set; }
        public string? PasswdHash { get; set; }
        public string? Salt { get; set; }
        public string? AdminEmail { get; set; }
        public string? EmailPasswd { get; set; }
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

        static void Cluster()    // Cluster Connector
        {

        }
    }
}