import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class LogProcessorTest {
    @Test void appliesThresholdCountsAllValidIpEntriesAndSortsSeverity() throws Exception {
        LogReadResult logs = new LogReader().readLogs(new StringReader(
                line("LOW", "a") + "\n" + line("WARN", "a") + "\n"
                + line("UNKNOWN", "a") + "\nbroken\n" + line("HIGH", "b") + "\n"
                + line("LOW", "c") + "\n" + line("WARN", "b")));
        AnalysisResult result = new LogProcessor().analyze(logs,
                Map.of("LOW", 2, "WARN", 3, "HIGH", 5, "UNUSED", 1));
        assertEquals(Map.of("LOW", 2, "WARN", 2, "HIGH", 1, "UNUSED", 0), result.getActivityCounts());
        assertEquals(Map.of("a", 3, "b", 2), result.getSuspiciousIPs());
        assertEquals(List.of("HIGH", "WARN", "WARN"), result.getFlaggedEntries().stream()
                .map(LogEntry::getLevel).collect(Collectors.toList()));
        assertEquals(List.of(logs.getValidEntries().get(2)), result.getUnknownEntries());
    }

    @Test void matchesRulebookLevelsCaseSensitively() throws Exception {
        String original = line("info", "a");
        LogReadResult logs = new LogReader().readLogs(new StringReader(original));
        AnalysisResult result = new LogProcessor().analyze(logs, Map.of("INFO", 3));
        assertTrue(logs.getMalformedLines().isEmpty());
        assertEquals(Map.of("INFO", 0), result.getActivityCounts());
        assertEquals(logs.getValidEntries(), result.getUnknownEntries());
        assertEquals(original, result.getUnknownEntries().get(0).getOriginal());
        assertTrue(result.getFlaggedEntries().isEmpty());
        assertTrue(result.getSuspiciousIPs().isEmpty());
    }

    private String line(String level, String ip) {
        return "2024-01-01 00:00:00|" + level + "|" + ip + "|server|login";
    }
}
