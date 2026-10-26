import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ReportWriter {

    public void writeReport(
            Path reportPath,
            LogReadResult logResult,
            AnalysisResult analysisResult) throws IOException {


            try (BufferedWriter writer = Files.newBufferedWriter(reportPath)){
                writer.write("========== TRACEFINDER REPORT ==========");
                writer.newLine();
                writer.newLine();
                
                int listEntries = logResult.getValidEntries().size();
                writer.write("VALID ENTRIES: " + listEntries);
                writer.newLine();

                int listMalformed = logResult.getMalformedLines().size();
                writer.write("MALFORMED LINES: " + listMalformed);
                writer.newLine();
                writer.newLine();

                writer.write("ACTIVITY COUNTS: ");
                writer.newLine();
                for (Map.Entry<String, Integer> entry : analysisResult.getActivityCounts().entrySet()) {
                    String action = entry.getKey();
                    Integer count = entry.getValue();
                    writer.write(action + ": " + count);
                    writer.newLine();

                    writer.write("FLAGGED ENTRIES:");
                    writer.newLine();
                    for (LogEntry entry : analysisResult.getFlaggedEntries()){
                        writer.write(entry.getOriginal());
                        writer.newLine();
                    }

                    writer.write("SUSPICIOUS IPS:");
                    writer.newLine();
                    for(Map.Entry<String, Integer> entry : analysisResult.getSuspiciousIPs().entrySet()){
                        writer.write(entry.getKey() + ": " + entry.getValue());
                        writer.newLine();
                    }

                    writer.write("UNKNOWN ENTRIES:");
                    writer.newLine();
                    for(LogEntry entry : analysisResult.getUnknownEntries()){
                        writer.write(entry.getOriginal());
                        writer.newLine();
                    }

                    writer.write("MALFORMED LINES:");
                    writer.newLine();
                    for(MalformedLine line : logResult.getMalformedLines()){
                        writer.write(line.getLineNumber() + ": " + line.getOriginalLine());
                        writer.newLine();
                    }
                }
                
            }

    

        
    }
}
