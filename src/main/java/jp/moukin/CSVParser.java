package jp.moukin;

public class CSVParser {
    
    // TODO
    // xxx = 1024 * 512
    private String codeSet;
    private int maxBufferSize = 1024;
    
    public CSVParser() {
        
    }
    
    public CSVParser(String codeSet, int maxBufferSize) {
        this.codeSet = codeSet;
        this.maxBufferSize = maxBufferSize;
    }
}
