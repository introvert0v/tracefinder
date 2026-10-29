import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RulebookReader {

    public Map<String, Integer> readRules(Path rulesPath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(rulesPath)) {
            return readRules(reader);
        }
    }

    // The caller owns the supplied reader.
    public Map<String, Integer> readRules(Reader input) throws IOException {
        Map<String, Integer> rulesMap = new HashMap<>();

        BufferedReader reader = input instanceof BufferedReader
                ? (BufferedReader) input : new BufferedReader(input);

        String header = reader.readLine();
        if (header == null || !header.equals("level,severity_score")) {
            throw new IllegalArgumentException("Error: Invalid CSV header in rules.csv. Expected: level,severity_score");
        }

        int lineNumber = 1;
        String line;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            String[] fields = line.split(",", -1);

            if (fields.length != 2) {
                throw new IllegalArgumentException("Error: Invalid number of CSV fields at line " + lineNumber);
            }

            String level = fields[0].trim();

            if (level.isEmpty()) {
                throw new IllegalArgumentException("Error: Empty level at line " + lineNumber);
            }

            if (rulesMap.containsKey(level)) {
                throw new IllegalArgumentException("Error: Duplicate level '" + level + "' at line " + lineNumber);
            }

            int severityScore;
            try {
                severityScore = Integer.parseInt(fields[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error: Invalid severity score format at line " + lineNumber, e);
            }

            rulesMap.put(level, severityScore);
        }

        return rulesMap;
    }
}
