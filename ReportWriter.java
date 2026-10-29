import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReportWriter {

    public void writeReport(
            Path reportPath,
            LogReadResult logResult,
            AnalysisResult analysisResult) throws IOException {

        Map<String, Integer> levelCounts = new HashMap<>();

        for (LogEntry entry : logResult.getValidEntries()) {
            levelCounts.merge(entry.getLevel(), 1, Integer::sum);
        }

        Set<Integer> malformedNumbers = new HashSet<>();

        for (MalformedLine line : logResult.getMalformedLines()) {
            malformedNumbers.add(line.getLineNumber());
        }

        Map<LogEntry, Integer> lineNumbers = new IdentityHashMap<>();
        int currentLine = 1;

        for (LogEntry entry : logResult.getValidEntries()) {
            while (malformedNumbers.contains(currentLine)) {
                currentLine++;
            }

            lineNumbers.put(entry, currentLine++);
        }

        Map<String, Integer> levelPriority = Map.of(
                "ALERT", 0,
                "ERROR", 1,
                "WARN", 2,
                "INFO", 3
        );

        List<LogEntry> flagged =
                new ArrayList<>(analysisResult.getFlaggedEntries());

        flagged.sort(Comparator
                .comparingInt((LogEntry entry) ->
                        levelPriority.getOrDefault(entry.getLevel(), 4))
                .thenComparing(LogEntry::getTimestamp));

        List<Map.Entry<String, Integer>> suspiciousIPs =
                new ArrayList<>(analysisResult.getSuspiciousIPs().entrySet());

        suspiciousIPs.sort((first, second) -> {
            int byCount = second.getValue().compareTo(first.getValue());
            return byCount != 0
                    ? byCount
                    : first.getKey().compareTo(second.getKey());
        });

        try (BufferedWriter writer = Files.newBufferedWriter(reportPath)) {

            writer.write("=== Log Analysis Report ===");
            writer.newLine();

            writer.write("Generated: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
            writer.newLine();
            writer.newLine();

            writer.write("--- Activity Summary ---");
            writer.newLine();

            for (String level : new String[]{"INFO", "WARN", "ERROR", "ALERT"}) {
                writer.write(level + ":  " +
                        levelCounts.getOrDefault(level, 0));
                writer.newLine();
            }

            writer.newLine();

            writer.write("--- Flagged Entries ---");
            writer.newLine();

            for (LogEntry entry : flagged) {
                writer.write(String.format(
                        "%s | %-5s | %-13s | %-13s | %s",
                        entry.getTimestamp(),
                        entry.getLevel(),
                        entry.getSourceIP(),
                        entry.getTarget(),
                        entry.getAction()
                ));
                writer.newLine();
            }

            writer.newLine();

            writer.write("--- Suspicious Activity by IP ---");
            writer.newLine();

            for (Map.Entry<String, Integer> entry : suspiciousIPs) {
                writer.write(entry.getKey() + ": "
                        + entry.getValue() + " entries");
                writer.newLine();
            }

            writer.newLine();

            writer.write("--- Unknown Patterns ---");
            writer.newLine();

            for (LogEntry entry : analysisResult.getUnknownEntries()) {
                writer.write("Line " + lineNumbers.get(entry)
                        + ": " + entry.getOriginal());
                writer.newLine();
            }

            writer.newLine();

            writer.write("--- Malformed Lines ---");
            writer.newLine();

            for (MalformedLine line : logResult.getMalformedLines()) {
                writer.write("Line " + line.getLineNumber()
                        + ": " + line.getOriginalLine());
                writer.newLine();
            }
        }
    }
}
