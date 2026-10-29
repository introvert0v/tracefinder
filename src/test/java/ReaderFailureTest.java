import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.Reader;
import static org.junit.jupiter.api.Assertions.*;

class ReaderFailureTest {
    @Test void propagatesReadErrors() {
        Reader failing = new Reader() {
            @Override public int read(char[] buffer, int offset, int length) throws IOException {
                throw new IOException("simulated read failure");
            }
            @Override public void close() {}
        };
        assertEquals("simulated read failure", assertThrows(IOException.class,
                () -> new LogReader().readLogs(failing)).getMessage());
        assertEquals("simulated read failure", assertThrows(IOException.class,
                () -> new RulebookReader().readRules(failing)).getMessage());
    }
}
