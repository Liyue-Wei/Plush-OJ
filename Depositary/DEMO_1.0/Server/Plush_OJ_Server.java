import java.io.File;

public class Plush_OJ_Server {
    public static void main(String[] args) {
        System.out.println("Welcome to Plush::OJ Server!");

        File Path = new File("");
        String rootPath = new String(Path.getAbsolutePath());
        System.out.println("Root Path: " + rootPath);
    }
}