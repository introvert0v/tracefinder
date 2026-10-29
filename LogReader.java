import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LogReader {

    public LogReadResult readLogs(Path logsPath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(logsPath)) {
            return readLogs(reader);
        }
    }

    // The caller owns the supplied reader.
    public LogReadResult readLogs(Reader input) throws IOException {

        List<LogEntry> validEntries = new ArrayList<>();
        List<MalformedLine> malformedLines = new ArrayList<>();

        BufferedReader reader = input instanceof BufferedReader
                ? (BufferedReader) input : new BufferedReader(input);

        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            Optional<LogEntry> parsedLine = logLineParser(line);

            if (parsedLine.isPresent()) {
                validEntries.add(parsedLine.get());
            } else {
                MalformedLine malformedLine =
                        new MalformedLine(lineNumber, line);

                malformedLines.add(malformedLine);
            }
        }

        return new LogReadResult(validEntries, malformedLines);
    }

    Optional<LogEntry> logLineParser(String line) {

        if (line.isBlank()) {
            return Optional.empty();
        }

        String[] splited = line.split("\\|", -1);

        if (splited.length != 5) {
            return Optional.empty();
        }

        splited[0] = splited[0].trim();
        splited[1] = splited[1].trim();
        splited[2] = splited[2].trim();
        splited[3] = splited[3].trim();
        splited[4] = splited[4].trim();

        try {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("uuuu-MM-dd HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDateTime.parse(splited[0], formatter);

            return Optional.of(new LogEntry(
                    splited[0],
                    splited[1],
                    splited[2],
                    splited[3],
                    splited[4],
                    line
            ));

        } catch (DateTimeException e) {
            return Optional.empty();
        }
    }
}
