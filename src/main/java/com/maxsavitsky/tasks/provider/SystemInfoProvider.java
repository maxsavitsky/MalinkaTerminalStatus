package com.maxsavitsky.tasks.provider;

public interface SystemInfoProvider {

	default void fetch(){}

	double[] getCoresLoad();

	double getCpuTemperature();

	long getTotalMemorySize();

	long getUsedMemorySize();

	long getUsedCacheSize();

	long getFreeMemorySize();

	long getAvailableMemorySize();

	long getTotalSwapSize();

	long getUsedSwapSize();

}
