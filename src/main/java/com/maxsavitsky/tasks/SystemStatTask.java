package com.maxsavitsky.tasks;

import com.maxsavitsky.Content;
import com.maxsavitsky.Main;
import com.maxsavitsky.Utils;
import com.maxsavitsky.manager.ContentDispatcher;
import com.maxsavitsky.tasks.provider.SystemInfoProvider;
import org.apache.commons.lang3.SystemUtils;
import oshi.SystemInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SystemStatTask extends Task {

	private final boolean enableServicesStats;
	private final List<Service> services;

	private final SystemInfoProvider provider;

	private final ContentDispatcher contentDispatcher;

	public SystemStatTask(ContentDispatcher contentDispatcher, boolean enableServicesStats, List<Service> services, SystemInfoProvider provider) {
		this.services = services;
		this.enableServicesStats = enableServicesStats;
		this.provider = provider;
		this.contentDispatcher = contentDispatcher;
	}

	@Override
	public void execute() throws IOException {
		provider.fetch();

		String sysStatSecId = "sys-stat";
		ArrayList<Content> contents = new ArrayList<>();

		contents.add(
				new Content("time",
						sysStatSecId,
						new SimpleDateFormat("HH:mm:ss").format(new Date()),
						"Time"
				)
		);

		double[] cpuUsage = provider.getCoresLoad();
		for(int i = 0; i < cpuUsage.length; i++){
			contents.add(
					new Content("cpu-" + i,
							sysStatSecId,
							(int) (cpuUsage[i] * 100) + "%",
							i == 0 ? "CPU usage" : "CPU " + i + " usage"
					)
			);
		}

		long memoryUsage = provider.getUsedMemorySize() * 100L / provider.getTotalMemorySize();
		contents.add(
				new Content("ram", sysStatSecId,
						 memoryUsage + "% "
								 + getFormattedSize(provider.getUsedMemorySize())
								 + "/"
								 + getFormattedSize(provider.getTotalMemorySize()),
						"RAM usage"
				)
		);

		if(provider.getAvailableMemorySize() >= 0) {
			long availableMemoryPercentage = provider.getAvailableMemorySize() * 100L / provider.getTotalMemorySize();
			contents.add(
					new Content("ram-available", sysStatSecId,
							availableMemoryPercentage + "% "
									+ getFormattedSize(provider.getAvailableMemorySize()),
							"RAM available"
					)
			);
		}

		long usedCacheSize = provider.getUsedCacheSize();
		if(usedCacheSize > 0){
			contents.add(
					new Content("ram-cache", sysStatSecId,
							 getFormattedSize(usedCacheSize),
							 "RAM cache"
					)
			);
		}

		if(provider.getTotalSwapSize() > 0) {
			int swapUsage = (int) ((provider.getUsedSwapSize()) * 100 / provider.getTotalSwapSize());
			contents.add(
					new Content("swap-usage", sysStatSecId,
							swapUsage + "% "
									+ getFormattedSize(provider.getUsedSwapSize())
									+ "/"
									+ getFormattedSize(provider.getTotalSwapSize()),
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
		contents.add(new Content("uptime", sysStatSecId, uptimeString, "Uptime"));

		contents.add(new Content("temp", sysStatSecId, systemInfo.getHardware().getSensors().getCpuTemperature() + "'C", "Temp"));

		contents.add(new Content("thread-count", sysStatSecId, "" + systemInfo.getOperatingSystem().getThreadCount(), "Thread count"));

		if (SystemUtils.IS_OS_LINUX && enableServicesStats) {
			contents.addAll(getServicesStats());
		}

		for (var l : contents) {
			try {
				contentDispatcher.dispatch(l);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ArrayList<Content> getServicesStats() throws IOException {
		ArrayList<Content> contents = new ArrayList<>();
		for(var service : services){
			String state = Utils.exec("systemctl is-active " + service.id())
					.replace("\n", "");
			String status = Utils.exec("systemctl status %s".formatted(service.id()));
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
			contents.add(new Content(service.id(), "sys-stat", msg, service.name()));
		}
		return contents;
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

	public record Service(String id, String name){
	}

}
