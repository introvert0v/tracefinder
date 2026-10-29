import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Error: Expected exactly 3 file paths.");
            System.err.println(
                    "Usage: java -jar target/tracefinder.jar logs.txt rules.csv report.txt"
            );
            System.exit(1);
        }

        Path logsPath;
        Path rulesPath;
        Path reportPath;

        try {
            logsPath = Paths.get(args[0]);
            rulesPath = Paths.get(args[1]);
            reportPath = Paths.get(args[2]);
        } catch (InvalidPathException e) {
            System.err.println("Error: Invalid path: " + e.getInput());
            System.err.println(e.getReason());
            System.exit(1);
            return;
        }

        if (!Files.exists(logsPath)) {
            System.err.println("Error: File not found: " + logsPath);
            System.exit(1);
        }

        if (!Files.exists(rulesPath)) {
            System.err.println("Error: File not found: " + rulesPath);
            System.exit(1);
        }

        if (!Files.isRegularFile(logsPath) || !Files.isReadable(logsPath)) {
            System.err.println("Error: Cannot read log file: " + logsPath);
            System.exit(1);
        }

        if (!Files.isRegularFile(rulesPath) || !Files.isReadable(rulesPath)) {
            System.err.println("Error: Cannot read rulebook: " + rulesPath);
            System.exit(1);
        }

        String currentStep = "reading rulebook";
        Path currentPath = rulesPath;

        try {
            RulebookReader rulebookReader = new RulebookReader();
            Map<String, Integer> rules = rulebookReader.readRules(rulesPath);

            currentStep = "reading log";
            currentPath = logsPath;

            LogReader logReader = new LogReader();
            LogReadResult logResult = logReader.readLogs(logsPath);

            LogProcessor logProcessor = new LogProcessor();
            AnalysisResult analysisResult =
                    logProcessor.analyze(logResult, rules);

            currentStep = "writing report";
            currentPath = reportPath;

            ReportWriter reportWriter = new ReportWriter();
            reportWriter.writeReport(reportPath, logResult, analysisResult);

            System.out.println("Report saved to: " + reportPath);

        } catch (IOException e) {
            System.err.println(
                    "Error while " + currentStep + ": " + currentPath
            );
            System.err.println(e.getMessage());
            System.exit(1);

        } catch (IllegalArgumentException e) {
            System.err.println(
                    "Error: Invalid rulebook " + rulesPath
                            + ": " + e.getMessage()
            );
            System.exit(1);
        }
    }
}
