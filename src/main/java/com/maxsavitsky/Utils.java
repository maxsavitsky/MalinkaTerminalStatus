package com.maxsavitsky;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

	private static final Runtime runtime = Runtime.getRuntime();

	public static void transferTo(InputStream is, ByteArrayOutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) != -1) {
			os.write(buffer, 0, len);
		}
	}

	public static String exec(String cmd) throws IOException {
		Process process = runtime.exec(cmd);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		transferTo(process.getInputStream(), bos);

		return bos.toString();
	}

	public static String getHelpString() {
		try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("help.txt");
		     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			return reader.lines().reduce((r, s) -> r + "\n" + s).orElse("");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
