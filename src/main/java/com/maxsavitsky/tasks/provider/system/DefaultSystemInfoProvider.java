package com.maxsavitsky.tasks.provider.system;

import oshi.SystemInfo;

import java.lang.management.ManagementFactory;

public class DefaultSystemInfoProvider implements SystemInfoProvider {

	private final com.sun.management.OperatingSystemMXBean osBean;
	private final SystemInfo systemInfo = new SystemInfo();

	public DefaultSystemInfoProvider(){
		osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public double[] getCoresLoad() {
		return new double[]{
				osBean.getSystemCpuLoad()
		};
	}

	@Override
	public double getCpuTemperature() {
		return systemInfo.getHardware().getSensors().getCpuTemperature();
	}

	@Override
	public long getTotalMemorySize() {
		return osBean.getTotalPhysicalMemorySize();
	}

	@Override
	public long getUsedMemorySize() {
		return osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize();
	}

	@Override
	public long getUsedCacheSize() {
		return -1;
	}

	@Override
	public long getFreeMemorySize() {
		return osBean.getFreePhysicalMemorySize();
	}

	@Override
	public long getAvailableMemorySize() {
		return -1;
	}

	@Override
	public long getTotalSwapSize() {
		return osBean.getTotalSwapSpaceSize();
	}

	@Override
	public long getUsedSwapSize() {
		return osBean.getTotalSwapSpaceSize() - osBean.getFreeSwapSpaceSize();
	}

	@Override
	public int getThreadCount() {
		return systemInfo.getOperatingSystem().getThreadCount();
	}

	@Override
	public long getUptime() {
		return systemInfo.getOperatingSystem().getSystemUptime();
	}
}
