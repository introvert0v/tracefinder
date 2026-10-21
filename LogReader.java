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

// TODO: Исправить импорт Files и объявление класса.
public class LogReader{

    // Метод получает путь к лог-файлу и должен вернуть
    // два списка: правильные и неправильные записи.
    // IOException передаём в Main для обработки ошибки.
    public LogReadResult readLogs(Path logsPath) throws IOException{

        // Список для корректных записей LogEntry.
        List<LogEntry> valiedEntries = new ArrayList<>();

        // Список для некорректных строк MalformedLine.
        List<MalformedLine> malformedLines = new ArrayList<>();

        // Открываем файл для построчного чтения.
        // BufferedReader автоматически закрывается.
        try (BufferedReader reader = Files.newBufferedReader(logsPath)) {

            String line;

            // Счётчик строк исходного файла.
            int lineNumber = 0;


            // Читаем строки до конца файла.
            while ((line = reader.readLine()) != null) {

                // Учитываем каждую строку, даже пустую.
                lineNumber++;

                // Временный вывод для проверки чтения.
                System.out.println(line);

                // TODO: Передать line в LogLineParser.
                
                // Парсер должен вернуть Optional<LogEntry>.
                Optional<LogEntry> parsedLine = logLineParser(line);
                // TODO: Если запись корректная,
                if(parsedLine.isPresent()){
                    parsedLine.get();
                    valiedEntries.add(parsedLine.get());
                }else{
                    MalformedLine malformedLine = new MalformedLine(lineNumber, line);
                    malformedLines.add(malformedLine);
                }
                // добавить LogEntry в validEntries.

                // TODO: Если запись некорректная,
                // создать MalformedLine с номером строки
                // и исходным текстом.
                // Добавить объект в malformedLines.
            }
        }

        // TODO: Вернуть LogReadResult с двумя списками.
        LogReadResult result = new LogReadResult(valiedEntries, malformedLines);
        return result
    }


    private Optional<LogEntry> logLineParser (String line){ // пишу парсер

        if(line.isBlank()){ // проверка на то есть ли полностью пустые строки
            return Optional.empty();
        }

        String[] splited = line.split("\\|");
        
        if(splited.length != 5){ // проверка на целостность, есть ли все елементы входящей информации
            return Optional.empty();
        }

        splited[0] = splited[0].trim(); 
        splited[1] = splited[1].trim(); 
        splited[2] = splited[2].trim();
        splited[3] = splited[3].trim(); 
        splited[4] = splited[4].trim(); //так как трим работает к определенному обьекту -- убираю отступы для дальнейшего обьявления корректного формата для проверки инфы
        
        try{ //проверка даты на формат: 1) все ок и оно сохраняет как хорошую строку со всемы данными. 2) не проходит проверку и улетают в контейнер с плохими строками
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime parsedTimeStamp = LocalDateTime.parse(splited[0], formatter);
            return Optional.of(new LogEntry(splited[0], splited[1], splited[2], splited[3], splited[4], line));
        }catch(DateTimeException e){
            return Optional.empty();
        }
        
    }

    // TODO: Закрыть класс.
}
