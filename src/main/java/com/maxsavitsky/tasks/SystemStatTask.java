package com.maxsavitsky.tasks;

import com.maxsavitsky.Line;
import com.maxsavitsky.MessagesController;
import com.maxsavitsky.Utils;
import org.apache.commons.lang3.SystemUtils;

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
		ArrayList<Line> lines = new ArrayList<>();
		lines.add(
				new Line(
						"cpu",
						"sys-stat",
						(int) (osBean.getCpuLoad() * 100) + "%",
						"CPU usage"
				)
		);
		int memoryUsage = (int) ((osBean.getTotalMemorySize() - osBean.getFreeMemorySize()) * 100 / osBean.getTotalMemorySize());
		lines.add(
				new Line("ram", "sys-stat",
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
					new Line("swap-usage", "sys-stat",
							swapUsage + "% "
									+ getFormattedSize(osBean.getTotalSwapSpaceSize() - osBean.getFreeSwapSpaceSize())
									+ "/"
									+ getFormattedSize(osBean.getTotalSwapSpaceSize()),
							"Swap usage"
					)
			);
		}

		if (SystemUtils.IS_OS_LINUX) {
			String result = Utils.exec("vcgencmd measure_temp"); // temp=XX'C
			String temp = result.substring("temp=".length());
			lines.add(
					new Line("Temp", "sys-stat", temp, "Temp")
			);

			if(enableServicesStats) {
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
							.replaceAll("\n", "");
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
						String s = p[1].replaceAll("\n", "").trim();
						try {
							Double.parseDouble(s.substring(0, s.length() - 1));
							msg += " " + s;
						} catch (NumberFormatException ignored) {
						}
					}
					lines.add(new Line(services[i], "sys-stat", msg, servicesNames[i]));
				}
			}
		}

		for (var l : lines) {
			try {
				MessagesController.handle(l);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		String usedString = new BigDecimal(used).setScale(2, RoundingMode.HALF_EVEN).toString();
		if(usedString.endsWith(".00")){
			usedString = usedString.substring(0, usedString.length() - 3);
		}
		return usedString + v;
	}

}
