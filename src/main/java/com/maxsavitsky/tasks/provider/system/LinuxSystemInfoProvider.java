package com.maxsavitsky.tasks.provider.system;

import com.maxsavitsky.Utils;
import oshi.SystemInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LinuxSystemInfoProvider implements SystemInfoProvider {

	private final SystemInfo systemInfo = new SystemInfo();

	private RamData ramData;

	private SwapData swapData;

	private double[][] cpuLoadAtFetch;

	private long fetchTime;

	@Override
	public void fetch() {
		fetchTime = System.currentTimeMillis();
		try {
			fetchMemory();
			fetchCpu();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void fetchMemory() throws IOException {
		String freeCommandResult = Utils.exec("free");
		String[] lines = freeCommandResult.split("\n");
		String ramLine = lines[1];
		ramData = parseRamData(ramLine);

		String swapLine = lines[2];
		swapData = parseSwapData(swapLine);
	}

	private void fetchCpu() throws IOException {
		cpuLoadAtFetch = parseCpuFromProcStat();
	}

	private double[][] parseCpuFromProcStat() throws IOException {
		// see about /proc/stat
		List<String> list = Files.readAllLines(new File("/proc/stat").toPath())
				.stream()
				.filter(line -> line.startsWith("cpu"))
				.collect(Collectors.toList());
		double[][] cpuLoad = new double[list.size()][10];
		for (int i = 0; i < list.size(); i++) {
			String[] parts = list.get(i).split("\\s+");
			for(int j = 0; j < 10; j++)
				cpuLoad[i][j] = Double.parseDouble(parts[j + 1]);
		}
		return cpuLoad;
	}

	@Override
	public double[] getCoresLoad() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		double[][] cpuLoad2;
		try {
			cpuLoad2 = parseCpuFromProcStat();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		double[] coresLoad = new double[cpuLoad2.length];
		for(int i = 0; i < coresLoad.length; i++){
			double[] coreLoad1 = cpuLoadAtFetch[i];
			double[] coreLoad2 = cpuLoad2[i];
			double total1 = Arrays.stream(coreLoad1).sum();
			double total2 = Arrays.stream(coreLoad2).sum();
			double delta = total2 - total1;
			double idleTimeDelta = coreLoad2[3] - coreLoad1[3];
			coresLoad[i] = 1 - idleTimeDelta / delta;
		}

		return coresLoad;
	}

	@Override
	public double getCpuTemperature() {
		return systemInfo.getHardware().getSensors().getCpuTemperature();
	}

	@Override
	public long getTotalMemorySize() {
		return ramData.total;
	}

	@Override
	public long getUsedMemorySize() {
		return ramData.used;
	}

	@Override
	public long getUsedCacheSize() {
		return ramData.usedCache;
	}

	@Override
	public long getFreeMemorySize() {
		return ramData.free;
	}

	@Override
	public long getAvailableMemorySize() {
		return ramData.available;
	}

	@Override
	public long getTotalSwapSize() {
		return swapData.total;
	}

	@Override
	public long getUsedSwapSize() {
		return swapData.used;
	}

	@Override
	public int getThreadCount() {
		return systemInfo.getOperatingSystem().getThreadCount();
	}

	@Override
	public long getUptime() {
		return systemInfo.getOperatingSystem().getSystemUptime();
	}

	private RamData parseRamData(String line){
		final int totalIndex = 1;
		final int usedIndex = 2;
		final int freeIndex = 3;
		final int sharedIndex = 4;
		final int buffersAndCacheIndex = 5;
		final int availableIndex = 6;
		String[] parts = line.split("\\s+");
		return new RamData(
				Long.parseLong(parts[totalIndex]) * 1000L,
				Long.parseLong(parts[usedIndex]) * 1000L,
				Long.parseLong(parts[freeIndex]) * 1000L,
				Long.parseLong(parts[buffersAndCacheIndex]) * 1000L,
				Long.parseLong(parts[availableIndex]) * 1000L
		);
	}

	private static class RamData {

		private final long total, used, free, usedCache, available;

		public RamData(long total, long used, long free, long usedCache, long available) {
			this.total = total;
			this.used = used;
			this.free = free;
			this.usedCache = usedCache;
			this.available = available;
		}
	}

	private SwapData parseSwapData(String line){
		final int totalIndex = 1;
		final int usedIndex = 2;
		String[] parts = line.split("\\s+");
		return new SwapData(
				Long.parseLong(parts[totalIndex]) * 1000L,
				Long.parseLong(parts[usedIndex]) * 1000L
		);
	}

	private static class SwapData {

		private final long total, used;

		public SwapData(long total, long used) {
			this.total = total;
			this.used = used;
		}

	}

}
