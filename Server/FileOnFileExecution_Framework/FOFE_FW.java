package FileOnFileExecution_Framework;
import java.io.*;

public class FOFE_FW {
    public static void main(String[] args) {
        System.out.println("File On File Execution Framework");

        try {
            ProcessBuilder command = new ProcessBuilder("python", "Server\\FileOnFileExecution_Framework\\FF_Container\\FF_Python_CTR.py", "Test\\Temp_Code\\QN001A-XXXXX-0000-00-00.cpp", "Test\\Temp_JSON\\QN001A.json", "QN001A-XXXXX-0000-00-00");
            Process process = command.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error starting process: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// python Server\FileOnFileExecution_Framework\FF_Container\FF_Python_CTR.py Test\Temp_Code\QN001A-XXXXX-0000-00-00.cpp Test\Temp_JSON\QN001A.json QN001A-XXXXX-0000-00-00