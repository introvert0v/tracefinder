
import java.util.List;
import java.util.Map;



public class AnalysisResult {

    private Map <String, Integer> activityCounts;
    private List <LogEntry> flaggedEntries;
    private Map <String, Integer> suspiciousIPs;
    private List <LogEntry> unknownEntries;

    public AnalysisResult(Map <String, Integer> activityCounts,List <LogEntry> flaggedEntries,Map  <String, Integer> suspiciousIPs,List <LogEntry> unknownEntries){
        this.activityCounts = activityCounts;
        this.flaggedEntries = flaggedEntries;
        this.suspiciousIPs = suspiciousIPs;
        this.unknownEntries = unknownEntries;
    }

    public Map <String, Integer> getActivityCounts(){
        return activityCounts;
    }
    public List <LogEntry> getFlaggedEntries(){
        return flaggedEntries;
    }
    public Map <String, Integer> getSuspiciousIPs(){
        return suspiciousIPs;
    }
    public List <LogEntry> getUnknownEntries(){
        return unknownEntries;
    }
}
