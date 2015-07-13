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

	private final File file;

	private final String charset;

	/**
	 * RFC4180 に従ったCSVのパースを行います。<br>
	 * ただしヘッダーの有無、レコード内のカラム数が異なるなどは無視します。<br>
	 * フォーマットに従わない場合などのエラー検知は不十分です。
	 * 
	 * @param file
	 *            読込み対象のファイル
	 * @param charset
	 *            対象ファイルの文字コード
	 */
	public CSVParser2(File file, String charset) {
		this.file = file;
		this.charset = charset;
	}

	private int read_buf_size = 65535;

	private StringBuilder str = new StringBuilder();

	private List<String> record = new ArrayList<>();

	private enum Mode {
		START, QUOT_DATA, QUOT_DATA_END, NON_QUOT_DATA, CR_
	}

	/**
	 * 一文字ずつ見る（実体はreadOnCHAR()）。<br>
	 * 現在の状態と文字によって状態が遷移する。
	 */
	private Mode addStr(char[] cbuf, int l, Mode mode) {
		for (int i = 0; i < l; i++) {
			mode = readOneCHAR(cbuf[i], mode);
		}
		return mode;
	}

	private Mode readOneCHAR(char c, Mode mode) {
		if (mode == Mode.START) {
			if (c == '\"') {
				return Mode.QUOT_DATA;
			} else {
				mode = Mode.NON_QUOT_DATA;
			}
		}
		
		if (isNotEscapeMode(mode) && (c == '\r')) {
			return Mode.CR_;

		} else if (mode == Mode.CR_) {
			if (c == '\n') {
				// 改行（レコード区切り）
				record.add(str.toString());
				func.func(record);
				record = new ArrayList<>();
				str = new StringBuilder();
				return Mode.START;
			} else {
				throw new Error("改行コード不正");
			}

		} else if (isNotEscapeMode(mode) && c == ',') {
			// カラム区切り
			record.add(str.toString());
			str = new StringBuilder();
			return Mode.START;

		} else if (mode == Mode.QUOT_DATA && c == '\"') {
			return Mode.QUOT_DATA_END;

		} else if (mode == Mode.QUOT_DATA_END && c == '\"') {
			str.append('\"');
			return Mode.QUOT_DATA;

		}

		str.append(c);
		return mode;
	}

	private boolean isNotEscapeMode(Mode mode) {
		return mode == Mode.QUOT_DATA_END || mode == Mode.NON_QUOT_DATA;
	}

	private RecordFunction func = record -> {
		for (String column : record)
			System.out.print("'" + column + "'");
		System.out.println(" _ " + record.size());
	};

	/**
	 * コンストラクタで与えられたファイルを読込み、パースします。<br>
	 * 全体を読込むのではなく、１レコードを読込むたびにそれを引数とした<br>
	 * RecordFunctionが実行されます。<br>
	 * 利用者はレコードに対する処理を自由に記述してparseに渡すことが出来ます。
	 * 
	 * @param func
	 *            １レコードに対する処理を自由に記述したメソッドを渡します。<br>
	 *            レコードとしてStringの入ったArrayListが渡されます。<br>
	 * @throws IOException
	 *             コンストラクタで与えられたファイルが見つからない場合など。<br>
	 */
	public void parse(RecordFunction func) throws IOException {

		try (FileInputStream file_in = new FileInputStream(file);
				BufferedInputStream bufed_in = new BufferedInputStream(file_in);
				InputStreamReader input_reader = new InputStreamReader(bufed_in, charset);) {

			this.func = func;
			final char[] cbuf = new char[read_buf_size];			
			int count = 0;
			Mode mode = Mode.START;
			
			while ((count = input_reader.read(cbuf, 0, read_buf_size)) != -1) {
				mode = addStr(cbuf, count, mode);
			}

			flush(mode);
		}
	}

	private void flush(Mode mode) {
		if (mode == Mode.START)
			return;
		record.add(str.toString());
		func.func(record);
		mode = Mode.START;
		str = new StringBuilder();
		record = new ArrayList<>();
	}

	/**
	 * 何文字ずつ読込むかを設定します。
	 * 
	 * @param read_buf_size
	 */
	public void setReadBufSize(int read_buf_size) {
		this.read_buf_size = read_buf_size;
	}
}
