package jp.moukin;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CSVParserTest {

    private final String FILE_NAME = "test.csv";

    private final String[][] VALUES = {
            { "1", "2", "3" },
            { "", "", "" },
            { "a", "b", "c" },
            { "", "", "" },
            { "\"Hello, world!\"", "This is \"test\" file.", "\"A,B,C\",\"D,E,F\"" },
            { "This\nis\ntest.", "\"A\"\n\"B\"\n\"C\"", "\"a\",\n\"b\",\n\"c\"" },
            { "あああ", "いいい", "ううう" }
    };

    private String filePath;
    private CSVParser parser;

    @Before
    public void before() {
        filePath = this.getClass().getClassLoader().getResource(FILE_NAME).getFile();
        parser = new CSVParser("MS932");
    }

    @Test
    public void getRecordList() {
        try {
            List<String[]> recordList = parser.getRecordList(filePath);

            assertEquals(7, recordList.size());

            for (int i = 0; i != VALUES.length; i++) {
                String[] values = VALUES[i];
                String[] columns = recordList.get(i);
                for (int j = 0; j != values.length; j++) {
                    assertEquals(values[j], columns[j]);
                }
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
