import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ReportWriterTest {
    private static final String[] HEADINGS = {"--- Activity Summary ---", "--- Flagged Entries ---",
            "--- Suspicious Activity by IP ---", "--- Unknown Patterns ---", "--- Malformed Lines ---"};

    @Test void rendersFiveEmptySectionsInOrder() throws Exception {
        String report = report("", Map.of("INFO", 1));
        int previous = -1;
        for (String heading : HEADINGS) {
            int index = report.indexOf(heading);
            assertTrue(index > previous);
            assertTrue(report.contains(heading + "\nNone found\n"));
            previous = index;
        }
        assertEquals(5, report.split("None found", -1).length - 1);
    }

    @Test void preservesFlaggedTextSortsIpsAndReportsOriginalLineNumbers() throws Exception {
        String flagged = " 2024-01-01 00:00:00 | WARN | z | server | login ";
        String unknown = "2024-01-01 00:00:00|OTHER|z|server|login";
        String high = "2024-01-01 00:00:00|HIGH|a|server|login";
        String report = report("bad\n" + flagged + "\n\n" + unknown + "\n" + high + "\n" + unknown,
                Map.of("WARN", 3, "HIGH", 5, "INFO", 1));
        assertTrue(report.contains("--- Flagged Entries ---\n" + high + "\n" + flagged + "\n"));
        assertTrue(report.contains("--- Suspicious Activity by IP ---\nz: 3 entries\na: 1 entries\n"));
        assertTrue(report.contains("Line 4: " + unknown + "\nLine 6: " + unknown));
        assertTrue(report.contains("--- Malformed Lines ---\nLine 1: bad\nLine 3: \n"));
        assertTrue(report.contains("HIGH: 1\nINFO: 0\nWARN: 1\n"));
    }

    @Test void rendersOnlyMalformedInputWithAllSectionsAndPhysicalLineNumbers() throws Exception {
        String first = "broken line";
        String third = "2024-01-01 00:00:00;INFO;ip;server;login";
        String report = report(first + "\n\n" + third, Map.of("INFO", 1));
        int previous = -1;
        for (int i = 0; i < HEADINGS.length; i++) {
            int index = report.indexOf(HEADINGS[i]);
            assertTrue(index > previous);
            assertEquals(index, report.lastIndexOf(HEADINGS[i]));
            if (i < 4) {
                assertTrue(report.contains(HEADINGS[i] + "\nNone found\n"));
            }
            previous = index;
        }
        assertEquals(4, report.split("None found", -1).length - 1);
        assertTrue(report.endsWith(HEADINGS[4] + "\nLine 1: " + first
                + "\nLine 2: \nLine 3: " + third + "\n"));
    }

    private String report(String input, Map<String, Integer> rules) throws Exception {
        LogReadResult logs = new LogReader().readLogs(new StringReader(input));
        return new ReportWriter().formatReport(logs, new LogProcessor().analyze(logs, rules));
    }
}
