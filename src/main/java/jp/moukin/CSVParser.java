package jp.moukin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    private final byte COMMA = 44;
    private final byte W_QUOTATION_MARK = 34;
    private final byte CR = 13;
    private final byte LF = 10;

    private String codeSet = "UTF-8";
    // 524288 byte = 512 MB
    private int maxBufferSize = 524288;

    public CSVParser() {}

    public CSVParser(String codeSet) {
        this.codeSet = codeSet;
    }

    public CSVParser(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    public CSVParser(String codeSet, int maxBufferSize) {
        this.codeSet = codeSet;
        this.maxBufferSize = maxBufferSize;
    }

    public List<String[]> getRecordList(String filePath)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try (
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(filePath)));
        ) {
            List<String[]> recordList = new ArrayList<>();

            byte[] buffer = new byte[maxBufferSize];
            int count = 0;
            byte[] bytes = new byte[0];
            while ((count = in.read(buffer)) != -1) {
                // merge
                byte[] tmp = new byte[bytes.length + count];
                System.arraycopy(bytes, 0, tmp, 0, bytes.length);
                System.arraycopy(buffer, 0, tmp, bytes.length, count);
                bytes = tmp;

                int startI = 0;
                int eorI = -1;
                while ((eorI = findEORecordIndex(bytes, startI)) != -1) {
                    int length = eorI - startI + 1;
                    byte[] line = new byte[length];
                    System.arraycopy(bytes, startI, line, 0, length);
                    recordList.add(getColumns(line));
                    startI = eorI + 1;
                }
            }

            return recordList;
        }
    }

    private int findEORecordIndex(byte[] bytes, int startI) {
        for (int i = startI, wqmCount = 0; i != bytes.length; i++) {
            if (bytes[i] == W_QUOTATION_MARK) {
                wqmCount++;
            } else if (bytes[i] == LF && wqmCount % 2 == 0) {
                return i;
            }
        }
        return -1;
    }

    private String[] getColumns(byte[] line) throws UnsupportedEncodingException {
        int length = line.length;
        if (line[length - 1] == LF)
            length--;
        if (line[length - 1] == CR)
            length--;

        List<String> list = new ArrayList<>();
        for (int i = 0, wqmCount = 0, startI = 0; i <= length; i++) {
            if (i == length || (line[i] == COMMA && wqmCount % 2 == 0)) {
                String column = new String(new String(line, startI, i - startI, codeSet));
                list.add(column);
                startI = i + 1;
                wqmCount = 0;
            } else if (line[i] == W_QUOTATION_MARK) {
                wqmCount++;
            }
        }

        String[] columns = new String[list.size()];
        for (int i = 0; i != columns.length; i++) {
            String column = list.get(i);
            if (column.startsWith("\"") && column.endsWith("\"")) {
                column = column.substring(1, column.length() - 1);
            }
            column = column.replaceAll("\"{2}?", "\"");
            columns[i] = column;
        }

        return columns;
    }

}
