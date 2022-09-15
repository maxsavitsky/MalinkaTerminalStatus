package com.maxsavitsky.tasks.provider.service;

import com.maxsavitsky.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LinuxServicesInfoProvider implements ServiceInfoProvider {

	@Override
	public String getServiceStatus(String serviceId) throws IOException {
		String state = Utils.exec("systemctl is-active " + serviceId)
				.replace("\n", "");
		String status = Utils.exec(String.format("systemctl status %s", serviceId));
		Process process = Runtime.getRuntime().exec("grep Memory");
		OutputStream os = process.getOutputStream();
		os.write(status.getBytes(StandardCharsets.UTF_8));
		os.flush();
		os.close();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Utils.transferTo(process.getInputStream(), bos);

		String memoryStr = bos.toString();

		String msg = state;
		if (memoryStr.length() > 0) {
			String[] p = memoryStr.split(":");
			String s = p[1].replace("\n", "").trim();
			try {
				Double.parseDouble(s.substring(0, s.length() - 1));
				msg += " " + s;
			} catch (NumberFormatException ignored) {
				// ignore
			}
		}

		return msg;
	}

}
