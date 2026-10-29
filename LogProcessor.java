import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class LogProcessor {

    public AnalysisResult analyze(
            LogReadResult logResult,
            Map<String, Integer> rules) {

        Map<String, Integer> activityCounts = new HashMap<>();
        List<LogEntry> flaggedEntries = new ArrayList<>();
        Map<String, Integer> suspiciousIPs = new HashMap<>();
        List<LogEntry> unknownEntries = new ArrayList<>();

        Map<String, Integer> ipCounts = new HashMap<>();

        for (String level : rules.keySet()) {
            activityCounts.put(level, 0);
        }

        for (LogEntry entry : logResult.getValidEntries()) {
            String ip = entry.getSourceIP();
            int ipCount = ipCounts.getOrDefault(ip, 0);
            ipCounts.put(ip, ipCount + 1);

            String currentLevel = entry.getLevel();
            Integer severityScore = rules.get(currentLevel);

            if (severityScore == null) {
                unknownEntries.add(entry);
                continue;
            }

            int count = activityCounts.get(currentLevel);
            activityCounts.put(currentLevel, count + 1);

            if (severityScore >= 3) {
                flaggedEntries.add(entry);
                suspiciousIPs.put(ip, 0);
            }
        }

        for (String ip : suspiciousIPs.keySet()) {
            suspiciousIPs.put(ip, ipCounts.get(ip));
        }

        flaggedEntries.sort((first, second) ->
                Integer.compare(
                        rules.get(second.getLevel()),
                        rules.get(first.getLevel())
                )
        );

        return new AnalysisResult(
                activityCounts,
                flaggedEntries,
                suspiciousIPs,
                unknownEntries
        );
    }
}
