package jp.moukin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class CSVParserTest {

    public void getRecordList() {
        String filePath = this.getClass().getClassLoader().getResource("test.csv").getFile();
        CSVParser parser = new CSVParser("MS932");
        try {
            List<String[]> recordList = parser.getRecordList(filePath);
            System.out.println(recordList.size());

            for (String[] columns : recordList) {
                System.out.println("# ----- #");
                for (String column : columns) {
                    System.out.print("@" + column + "@ ");
                }
                System.out.println("\n# ----- #\n");
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CSVParserTest test = new CSVParserTest();
        test.getRecordList();
    }

}
