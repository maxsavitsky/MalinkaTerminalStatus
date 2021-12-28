package com.maxsavitsky.tasks;

import com.maxsavitsky.Line;
import com.maxsavitsky.MessagesController;
import com.maxsavitsky.Utils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public class SystemStatTask extends Task {

	public static final long TIMER_PERIOD = 15000;

	private final com.sun.management.OperatingSystemMXBean osBean;
	private final Runtime runtime;

	public SystemStatTask() {
		runtime = Runtime.getRuntime();
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
		lines.add(
				new Line("ram", "sys-stat",
						((osBean.getTotalMemorySize() - osBean.getFreeMemorySize()) * 100 / osBean.getTotalMemorySize()) + "%",
						"RAM usage"
				)
		);
		if (SystemUtils.IS_OS_LINUX) {
			String result = Utils.exec("vcgencmd measure_temp"); // temp=XX'C
			String temp = result.substring("temp=".length());
			lines.add(
					new Line("Temp", "sys-stat", temp, "Temp")
			);

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
				String state = Utils.exec("systemctl is-active " + services[i]);
				lines.add(new Line(services[i], "sys-stat", state, servicesNames[i]));
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

}
