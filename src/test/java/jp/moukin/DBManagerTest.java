package jp.moukin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class DBManagerTest {

    public void getResult() {
        String filePath = this.getClass().getClassLoader().getResource("test.csv").getFile();

        try {
        BufferedReader in = new BufferedReader(new FileReader(new File(filePath)));
        String line = null;
        while ((line = in.readLine())!= null) {
            System.out.println(line);
        }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        DBManagerTest ts = new DBManagerTest();
        ts.getResult();
    }
}
