import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class RulebookReaderTest {
    @Test void readsTrimmedLevelsAndScores() throws Exception {
        assertEquals(Map.of("INFO", 1, "WARN", 3), new RulebookReader().readRules(
                new StringReader("level,severity_score\nINFO,1\n WARN , 3 \n")));
    }

    @Test void rejectsInvalidRulebooksWithUsefulMessages() {
        String[] inputs = {"", "wrong\n", "level,severity_score\nINFO,1,2",
                "level,severity_score\n,1", "level,severity_score\nINFO,no",
                "level,severity_score\nINFO,1\nINFO,3"};
        String[] expected = {"header", "header", "line 2", "line 2", "line 2", "line 3"};
        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> new RulebookReader().readRules(new StringReader(input)));
            assertTrue(error.getMessage().contains(expected[i]), error.getMessage());
        }
    }
}
