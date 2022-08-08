package com.maxsavitsky.tasks.provider;

import java.lang.management.ManagementFactory;

public class DefaultSystemInfoProvider implements SystemInfoProvider {

	private final com.sun.management.OperatingSystemMXBean osBean;

	public DefaultSystemInfoProvider(){
		osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public double[] getCoresLoad() {
		return new double[]{
				osBean.getCpuLoad()
		};
	}

	@Override
	public double getCpuTemperature() {
		return -1;
	}

	@Override
	public long getTotalMemorySize() {
		return osBean.getTotalMemorySize();
	}

	@Override
	public long getUsedMemorySize() {
		return osBean.getTotalMemorySize() - osBean.getFreeMemorySize();
	}

	@Override
	public long getUsedCacheSize() {
		return -1;
	}

	@Override
	public long getFreeMemorySize() {
		return osBean.getFreeMemorySize();
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
}
