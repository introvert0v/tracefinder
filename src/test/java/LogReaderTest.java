import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

class LogReaderTest {
    @Test void preservesOriginalAndTrimsFields() throws Exception {
        String original = " 2024-02-29 12:30:00 | WARN | 10.0.0.1 | server | login ";
        LogReadResult result = new LogReader().readLogs(new StringReader(original));
        assertEquals(1, result.getValidEntries().size());
        LogEntry entry = result.getValidEntries().get(0);
        assertEquals(original, entry.getOriginal());
        assertEquals("WARN", entry.getLevel());
        assertEquals("10.0.0.1", entry.getSourceIP());
        assertEquals("server", entry.getTarget());
        assertEquals("login", entry.getAction());
    }

    @Test void rejectsInvalidDatesAndFieldCountsAndTracksPhysicalLines() throws Exception {
        String valid = "2024-01-01 00:00:00|INFO|ip|server|login";
        String invalidDate = "2023-02-29 00:00:00|INFO|ip|server|login";
        LogReadResult result = new LogReader().readLogs(new StringReader(
                "\n" + valid + "\n" + invalidDate + "\nmissing|fields\n" + valid + "|extra\n" + valid));
        assertEquals(2, result.getValidEntries().size());
        assertEquals(4, result.getMalformedLines().size());
        assertArrayEquals(new int[]{1, 3, 4, 5}, result.getMalformedLines().stream()
                .mapToInt(MalformedLine::getLineNumber).toArray());
        assertEquals(invalidDate, result.getMalformedLines().get(1).getOriginalLine());
    }

    @Test void acceptsEmptyInput() throws Exception {
        LogReadResult result = new LogReader().readLogs(new StringReader(""));
        assertTrue(result.getValidEntries().isEmpty());
        assertTrue(result.getMalformedLines().isEmpty());
    }
    @Test void rejectsSemicolonSeparatedLine() throws Exception {
        String original = "2024-01-01 00:00:00;INFO;ip;server;login";
        LogReadResult result = new LogReader().readLogs(new StringReader(original));
        assertTrue(result.getValidEntries().isEmpty());
        assertEquals(1, result.getMalformedLines().size());
        assertEquals(1, result.getMalformedLines().get(0).getLineNumber());
        assertEquals(original, result.getMalformedLines().get(0).getOriginalLine());
    }
}
