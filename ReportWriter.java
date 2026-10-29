import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

        String report = formatReport(logResult, analysisResult);
        Files.writeString(reportPath, report);
    }

    public String formatReport(
            LogReadResult logResult,
            AnalysisResult analysisResult) {

        StringBuilder report = new StringBuilder();

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

            lineNumbers.put(entry, currentLine);
            currentLine++;
        }

        report.append("=== Log Analysis Report ===\n");
        report.append("Generated: ");
        report.append(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ));
        report.append("\n\n");

        report.append("--- Activity Summary ---\n");

        Map<String, Integer> activityCounts =
                analysisResult.getActivityCounts();

        boolean hasKnownEntries = false;

        for (int count : activityCounts.values()) {
            if (count > 0) {
                hasKnownEntries = true;
                break;
            }
        }

        if (!hasKnownEntries) {
            report.append("None found\n");
        } else {
            List<String> levels = new ArrayList<>(activityCounts.keySet());
            levels.sort(String::compareTo);

            for (String level : levels) {
                report.append(level);
                report.append(": ");
                report.append(activityCounts.get(level));
                report.append("\n");
            }
        }

        report.append("\n--- Flagged Entries ---\n");

        if (analysisResult.getFlaggedEntries().isEmpty()) {
            report.append("None found\n");
        } else {
            for (LogEntry entry : analysisResult.getFlaggedEntries()) {
                report.append(entry.getOriginal());
                report.append("\n");
            }
        }

        report.append("\n--- Suspicious Activity by IP ---\n");

        List<Map.Entry<String, Integer>> suspiciousIPs =
                new ArrayList<>(
                        analysisResult.getSuspiciousIPs().entrySet()
                );

        suspiciousIPs.sort((first, second) ->
                Integer.compare(second.getValue(), first.getValue())
        );

        if (suspiciousIPs.isEmpty()) {
            report.append("None found\n");
        } else {
            for (Map.Entry<String, Integer> entry : suspiciousIPs) {
                report.append(entry.getKey());
                report.append(": ");
                report.append(entry.getValue());
                report.append(" entries\n");
            }
        }

        report.append("\n--- Unknown Patterns ---\n");

        if (analysisResult.getUnknownEntries().isEmpty()) {
            report.append("None found\n");
        } else {
            for (LogEntry entry : analysisResult.getUnknownEntries()) {
                report.append("Line ");
                report.append(lineNumbers.get(entry));
                report.append(": ");
                report.append(entry.getOriginal());
                report.append("\n");
            }
        }

        report.append("\n--- Malformed Lines ---\n");

        if (logResult.getMalformedLines().isEmpty()) {
            report.append("None found\n");
        } else {
            for (MalformedLine line : logResult.getMalformedLines()) {
                report.append("Line ");
                report.append(line.getLineNumber());
                report.append(": ");
                report.append(line.getOriginalLine());
                report.append("\n");
            }
        }

        return report.toString();
    }
}
