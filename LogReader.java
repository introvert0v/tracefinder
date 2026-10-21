import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.nio.file.Files; //импорт 
import tracefinder.model.LogEntry;
import tracefinder.model.MalformedLine;
import tracefinder.model.LogReadResult;

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

                // TODO: Если запись корректная,
                // добавить LogEntry в validEntries.

                // TODO: Если запись некорректная,
                // создать MalformedLine с номером строки
                // и исходным текстом.
                // Добавить объект в malformedLines.
            }
        }

        // TODO: Вернуть LogReadResult с двумя списками.
    }
    private Optional<LogEntry> logLineParser (String line){ // пишу парсер

        String[] splited = line.split("\\|");
        if(line.isBlank()){ // проверка на то есть ли полностью пустые строки
            return Optional.empty();
        }
        if(splited.length != 5){ // проверка на целостность, есть ли все елементы входящей информации
            return Optional.empty();
        }
        splited[0] = splited[0].trim(); 
        splited[1] = splited[1].trim(); 
        splited[2] = splited[2].trim();
        splited[3] = splited[3].trim(); 
        splited[4] = splited[4].trim(); //так как трим работает к определенному обьекту -- убираю отступы для дальнейшей правильноый проверки формата входящих данных
        
        return Optional.of(new LogEntry(splited[0], splited[1], splited[2], splited[3], splited[4], line));
    }

    // TODO: Закрыть класс.
}
