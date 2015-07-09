package jp.moukin;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CSVParser2Test {

	private File getFile(String fileName) {
		String filePath = this.getClass().getClassLoader().getResource(fileName).getFile();
		File file = new File(filePath);
		return file;
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void readFile() throws Exception {
		CSVParser2 parser = new CSVParser2(getFile("readFile.csv"), "UTF-8");
		assertNotNull(parser);
	}

	private RecordFunction func = record -> {
		for (String column : record)
			System.out.print("" + column + "\t");

		System.out.println("size:" + record.size());
	};

	@Test
	public void setFunc() throws Exception {
		CSVParser2 parser = new CSVParser2(getFile("readFile.csv"), "UTF-8");
		parser.parse(func);
	}

	@Test
	public void setFunc_sjis() throws Exception {
		CSVParser2 parser = new CSVParser2(getFile("readFile_sjis.csv"), "Shift_JIS");
		parser.parse(func);
	}

	@Test
	public void setFunc_sjis_big() throws Exception {
		CSVParser2 parser = new CSVParser2(getFile("somebig.csv"), "Shift_JIS");

		final StringBuilder sb = new StringBuilder(50000);

		parser.parse(new RecordFunction() {
			@Override
			public void func(List<String> record) {
				for (String column : record)
					sb.append(column).append("\t");
				sb.append("size:").append(record.size()).append("\r\n");
			}
		});
		
		System.out.println(sb.length());
	}

	@Test
	public void setFunc_sjis_verybig() throws Exception {
		CSVParser2 parser = new CSVParser2(getFile("somebig.csv"), "Shift_JIS");

		FileWriter fstream;
		fstream = new FileWriter("TEST-jp.moukin.bigfile_out.csv");
		BufferedWriter out = new BufferedWriter(fstream);

		parser.parse(new RecordFunction() {

			@Override
			public void func(List<String> record) {
				try {
					StringBuilder sb = new StringBuilder();
					for (String column : record) {
						sb.append(column).append("\t");
					}
					out.write(sb.append("\r\n").toString());
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
		out.close();
	}

}
