import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.classfile.instruction.LineNumber;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.nio.file.Files; //импорт 
import tracefinder.model.LogEntry;
import tracefinder.model.MalformedLine;
import tracefinder.model.LogReadResult;

import java.time.DateTimeException;
import java.time.LocalDateTime; // импорты для обработки timestamp
import java.time.format.DateTimeFormatter;

public class LogReader{

    public LogReadResult readLogs(Path logsPath) throws IOException{

        List<LogEntry> valiedEntries = new ArrayList<>();

        List<MalformedLine> malformedLines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(logsPath)) {

            String line;

            int lineNumber = 0;


            while ((line = reader.readLine()) != null) {

                lineNumber++;

                System.out.println(line);
                
                Optional<LogEntry> parsedLine = logLineParser(line);
                if(parsedLine.isPresent()){
                    parsedLine.get();
                    valiedEntries.add(parsedLine.get());
                }else{
                    MalformedLine malformedLine = new MalformedLine(lineNumber, line);
                    malformedLines.add(malformedLine);
                }
            }
        }

        LogReadResult result = new LogReadResult(valiedEntries, malformedLines);
        return result;
    }


    private Optional<LogEntry> logLineParser (String line){ // пишу парсер

        if(line.isBlank()){ 
            return Optional.empty();
        }

        String[] splited = line.split("\\|");
        
        if(splited.length != 5){ 
            return Optional.empty();
        }

        splited[0] = splited[0].trim(); 
        splited[1] = splited[1].trim(); 
        splited[2] = splited[2].trim();
        splited[3] = splited[3].trim(); 
        splited[4] = splited[4].trim(); 
        
        try{ 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime parsedTimeStamp = LocalDateTime.parse(splited[0], formatter);
            return Optional.of(new LogEntry(splited[0], splited[1], splited[2], splited[3], splited[4], line));
        }catch(DateTimeException e){
            return Optional.empty();
        }
        
    }

}
