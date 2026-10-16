import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;


// Собственные типы данных
public class LogEntry {
    private String timestamp;
    private String level;
    private String sourceIP;
    private String target;
    private String action;

// Создал констуктор для записи инфы в каждый класс
public LogEntry(String timestamp, String level, String sourceIP, String target, String action) {
    this.timestamp = timestamp;
    this.level = level;
    this.sourceIP = sourceIP;
    this.target = target;
    this.action = action;
}
}