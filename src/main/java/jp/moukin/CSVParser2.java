package jp.moukin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser2 {

	private enum Mode {
		Nil, QUOT_DATA, QUOT_DATA_END, NON_QUOT_DATA, CR_SEEK
	}

	private final File file;
	
	private final String charset;
	
	private InputStreamReader input_reader;
	
	private StringBuilder str = new StringBuilder();

	private List<String> record = new ArrayList<>();

	private Mode mode = Mode.Nil;

	public CSVParser2(File file, String charset) {
		this.file = file;
		this.charset = charset;
	}
	
	private void addStr(char[] cbuf, int l) {
		for (int index = 0; index < l; index++) {

			if (mode == Mode.Nil) {
				if (cbuf[index] == '\"') {
					mode = Mode.QUOT_DATA;
					continue;
				} else {
					mode = Mode.NON_QUOT_DATA;
				}
			}

			if (isNoQuotMode() && (cbuf[index] == '\r')) {
				mode = Mode.CR_SEEK;
				continue;
			}

			if (mode == Mode.CR_SEEK) {
				if (cbuf[index] == '\n') {
					// 改行コード
					record.add(str.toString());
					func.func(record);
					record = new ArrayList<>();
					str = new StringBuilder();
					mode = Mode.Nil;
					continue;
				} else {
					throw new Error("改行コード不正");
				}
			}

			if (isNoQuotMode() && cbuf[index] == ',') {
				// 区切り文字
				record.add(str.toString());
				str = new StringBuilder();
				mode = Mode.Nil;
				continue;
			}

			if (mode == Mode.QUOT_DATA && cbuf[index] == '\"') {
				mode = Mode.QUOT_DATA_END;
				continue;
			}

			if (mode == Mode.QUOT_DATA_END && cbuf[index] == '\"') {
				str.append('\"');
				mode = Mode.QUOT_DATA;
				continue;
			}

			str.append(cbuf[index]);
		}
	}

	private boolean isNoQuotMode() {
		return mode == Mode.QUOT_DATA_END || mode == Mode.NON_QUOT_DATA;
	}
	
	private RecordFunction func = record -> {
		for (String column : record)
			System.out.print("'" + column + "'");
		System.out.println(" _ " + record.size());
	};
	
	private void flush() {
		if (mode == Mode.Nil)
			return;
		record.add(str.toString());
		func.func(record);
		mode = Mode.Nil;
		str = new StringBuilder();
	}

	public void parse(RecordFunction func) throws IOException {
		FileInputStream file_in = new FileInputStream(file);
		input_reader = new InputStreamReader(new BufferedInputStream(file_in), charset);

		this.func = func;
		int size = 65535;
		char[] cbuf = new char[size];

		int count = 0;
		while ((count = input_reader.read(cbuf, 0, size)) != -1) {
			addStr(cbuf, count);
		}
		flush();
		input_reader.close();
	}
}
