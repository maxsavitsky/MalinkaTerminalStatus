package com.maxsavitsky;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils {

	private static final Runtime runtime = Runtime.getRuntime();

	public static String exec(String cmd) throws IOException {
		Process process = runtime.exec(cmd);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		process.getInputStream().transferTo(bos);
		return bos.toString();
	}

}
