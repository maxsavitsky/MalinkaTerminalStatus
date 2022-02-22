package com.maxsavitsky.tasks;

import com.maxsavitsky.Line;
import com.maxsavitsky.Main;
import com.maxsavitsky.MessagesController;
import com.maxsavitsky.Utils;
import org.apache.commons.lang3.SystemUtils;
import oshi.SystemInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SystemStatTask extends Task {

	public static final long TIMER_PERIOD = 15000;

	private final com.sun.management.OperatingSystemMXBean osBean;
	private final boolean enableServicesStats;

	public SystemStatTask(boolean enableServicesStats) {
		this.enableServicesStats = enableServicesStats;
		osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public void execute() throws IOException {
		String sysStatSecId = "sys-stat";
		ArrayList<Line> lines = new ArrayList<>();
		lines.add(
				new Line(
						"cpu",
						sysStatSecId,
						(int) (osBean.getCpuLoad() * 100) + "%",
						"CPU usage"
				)
		);
		int memoryUsage = (int) ((osBean.getTotalMemorySize() - osBean.getFreeMemorySize()) * 100 / osBean.getTotalMemorySize());
		lines.add(
				new Line("ram", sysStatSecId,
						 memoryUsage + "% "
								 + getFormattedSize(osBean.getTotalMemorySize() - osBean.getFreeMemorySize())
								 + "/"
								 + getFormattedSize(osBean.getTotalMemorySize()),
						"RAM usage"
				)
		);

		if(osBean.getTotalSwapSpaceSize() > 0) {
			int swapUsage = (int) ((osBean.getTotalSwapSpaceSize() - osBean.getFreeSwapSpaceSize()) * 100 / osBean.getTotalSwapSpaceSize());
			lines.add(
					new Line("swap-usage", sysStatSecId,
							swapUsage + "% "
									+ getFormattedSize(osBean.getTotalSwapSpaceSize() - osBean.getFreeSwapSpaceSize())
									+ "/"
									+ getFormattedSize(osBean.getTotalSwapSpaceSize()),
							"Swap usage"
					)
			);
		}

		SystemInfo systemInfo = Main.getSystemInfo();
		long uptime = systemInfo.getOperatingSystem().getSystemUptime();
		String uptimeString = (uptime % 60) + "s";
		uptime /= 60;
		if(uptime > 0){
			uptimeString = (uptime % 60) + "m " + uptimeString;
			uptime /= 60;
		}
		if(uptime > 0){
			uptimeString = (uptime % 24) + "h " + uptimeString;
			uptime /= 24;
		}
		if(uptime > 0){
			uptimeString = uptime + "d " + uptimeString;
		}
		lines.add(new Line("uptime", sysStatSecId, uptimeString, "Uptime"));

		lines.add(new Line("temp", sysStatSecId, systemInfo.getHardware().getSensors().getCpuTemperature() + "'C", "Temp"));

		lines.add(new Line("thread-count", sysStatSecId, "" + systemInfo.getOperatingSystem().getThreadCount(), "Thread count"));

		if (SystemUtils.IS_OS_LINUX && enableServicesStats) {
			lines.addAll(getServicesStats());
		}

		for (var l : lines) {
			try {
				MessagesController.handle(l);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static ArrayList<Line> getServicesStats() throws IOException {
		ArrayList<Line> lines = new ArrayList<>();
		String[] services = new String[]{
				"mstsite",
				"mstauth",
				"mstdrive",
				"gitea",
				"nginx",
				"mysql",
				"postfix",
				"dovecot",
				"clamav-daemon"
		};
		String[] servicesNames = new String[]{
				"Site",
				"Auth",
				"Drive",
				"Gitea",
				"nginx",
				"mysql",
				"Postfix",
				"Dovecot",
				"ClamAV"
		};
		for (int i = 0; i < services.length; i++) {
			String state = Utils.exec("systemctl is-active " + services[i])
					.replace("\n", "");
			String status = Utils.exec("systemctl status %s".formatted(services[i]));
			Process process = Runtime.getRuntime().exec("grep Memory");
			OutputStream os = process.getOutputStream();
			os.write(status.getBytes(StandardCharsets.UTF_8));
			os.flush();
			os.close();

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			process.getInputStream().transferTo(bos);

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
			lines.add(new Line(services[i], "sys-stat", msg, servicesNames[i]));
		}
		return lines;
	}

	public static String getFormattedSize(long size){
		double used = size;
		String v;
		if (used < 1024) { // bytes
			v = "B";
		} else if (used < 1024 * 1024) { // kilobytes
			v = "KB";
			used /= 1024;
		} else if (used < 1024 * 1024 * 1024) { // megabytes
			v = "MB";
			used /= 1024 * 1024;
		} else {
			v = "GB";
			used /= 1024 * 1024 * 1024;
		}
		String usedString = BigDecimal.valueOf(used).setScale(2, RoundingMode.HALF_EVEN).toString();
		if(usedString.endsWith(".00")){
			usedString = usedString.substring(0, usedString.length() - 3);
		}
		return usedString + v;
	}

}
