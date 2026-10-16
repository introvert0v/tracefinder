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

// Создал констуктор для записи инфы. Принимает 1 входящую строку и ее 5 частей раскидвает по нужным полкам, типам информации. Сортировщик
public LogEntry(String timestamp, String level, String sourceIP, String target, String action) {
    this.timestamp = timestamp;
    this.level = level;
    this.sourceIP = sourceIP;
    this.target = target;
    this.action = action;
}
// Возврат данных из закрытого доступа. Геттеры
public String getTimestamp(){
    return timestamp;
}
public String getLevel(){
    return level;
}
public String getSourceIP(){
    return sourceIP;
}
public String getTarget(){
    return target;
}
public String getAction(){
    return action;
}
}
