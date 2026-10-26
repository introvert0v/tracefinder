
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
                    
                    int listFlaggedEntries = analysisResult.getFlaggedEntries().size();
                        
                }

            }

       

        
    }
}

ТЗ № 4 — Подсчёт неправильных строк
Теперь самостоятельно реализуй вывод количества некорректных строк.

Ожидаемый результат:
VALID ENTRIES: 100
MALFORMED LINES: 3


Твоё задание:
1. Обратись к logResult.
2. Найди в классе LogReadResult.java метод, который возвращает список неправильных строк.
3. Получи количество элементов этого списка.
4. Запиши в отчёт MALFORMED LINES:  и полученное число.
5. Добавь переход на новую строку.
Здесь практически такая же логика, как при подсчёте правильных записей.

Напиши самостоятельно эти три строки и присылай на проверку.





========== TRACEFINDER REPORT ==========

VALID ENTRIES: 100
MALFORMED LINES: 3

ACTIVITY COUNTS:
LOGIN: 50
LOGOUT: 20
FAILED_LOGIN: 30

FLAGGED ENTRIES:
...

SUSPICIOUS IPS:
192.168.1.5: 7

UNKNOWN ENTRIES:
...

========================================