
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

        for (LogEntry entry : logResult.getValidEntries()) {
            String action = entry.getAction();
            int count = activityCounts.getOrDefault(action, 0);
            activityCounts.put(action, count +1);

            String currentLevel = entry.getLevel();
            Integer NumericalValue = rules.get(currentLevel); 
        if(NumericalValue == null){
            unknownEntries.add(entry);
            continue;
        }
        if(NumericalValue >= 5){
            flaggedEntries.add(entry);
            String ip = entry.getSourceIP();
            int ipCount = suspiciousIPs.getOrDefault(ip, 0);
            suspiciousIPs.put(ip, ipCount +1);
        }
        }


        return new AnalysisResult(
                activityCounts,
                flaggedEntries,
                suspiciousIPs,
                unknownEntries
        );
    }
} 
