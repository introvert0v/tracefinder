import  java.util.List;

public class LogReadResult{
    private List<LogEntry> validEntries;
    private List<MalformedLine> malformedLines;

    public LogReadResult(List<LogEntry> validEntries, List<MalformedLine> malformedLines){
        this.validEntries = validEntries;
        this.malformedLines = malformedLines;
    }
        public List<LogEntry> getValidEntries(){
        return validEntries;
        }
        public  List<MalformedLine> getMalformedLines(){
            return malformedLines;
        }
}
