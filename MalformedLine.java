public class MalformedLine{
    private int lineNumber;
    private String originalLine;
    public MalformedLine(int lineNumber, String originalLine){
        this.lineNumber = lineNumber;
        this.originalLine = originalLine;
    }
        public int getLineNumber(){
        return lineNumber;
        }
        public String getOriginalLine(){
            return originalLine;
        }
}
