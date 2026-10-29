import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main{
    public static void main(String[] args) {
                if (args.length != 3){
            System.err.println("Error: Expected exactly 3 file paths as arguments.");
            System.err.println("use again: java -jar tracefinder.jar logs.txt rules.csv report.txt");
            System.exit(1);
        }
           Path logsPath = Paths.get(args[0]);
           Path rulesPath = Paths.get(args[1]);
           Path reportPath = Paths.get(args[2]);
            if (!Files.exists(logsPath) ){
                System.err.println("Error: file not found: " + logsPath);
                System.exit(1);
            }
            if (!Files.exists(rulesPath)){
                System.err.println("Error: file not found: " + rulesPath);
                System.exit(1);
            }
             if (!Files.isReadable(logsPath) ){
                System.err.println("Error: Can't read: " + logsPath);
                System.exit(1);
            }
            if (!Files.isReadable(rulesPath)){
                System.err.println("Error: Can't read: " + rulesPath);
                System.exit(1);
            }
}
}
