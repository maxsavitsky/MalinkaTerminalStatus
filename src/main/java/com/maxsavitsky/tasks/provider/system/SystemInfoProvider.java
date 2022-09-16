package com.maxsavitsky.tasks.provider.system;

import java.io.IOException;

public interface SystemInfoProvider {

	default void fetch() throws IOException {}

	double[] getCoresLoad();

	double getCpuTemperature();

	long getTotalMemorySize();

	long getUsedMemorySize();

	long getUsedCacheSize();

	long getFreeMemorySize();

	long getAvailableMemorySize();

	long getTotalSwapSize();

	long getUsedSwapSize();

	int getThreadCount();

	long getUptime();

	default SystemInfoData toData(){
		SystemInfoData data = new SystemInfoData();
		data.setCoresLoad( getCoresLoad() );
		data.setCpuTemperature( getCpuTemperature() );
		data.setTotalMemorySize( getTotalMemorySize() );
		data.setUsedMemorySize( getUsedMemorySize() );
		data.setUsedCacheSize( getUsedCacheSize() );
		data.setFreeMemorySize( getFreeMemorySize() );
		data.setAvailableMemorySize( getAvailableMemorySize() );
		data.setTotalSwapSize( getTotalSwapSize() );
		data.setUsedSwapSize( getUsedSwapSize() );
		data.setThreadCount( getThreadCount() );
		data.setUptime( getUptime() );
		return data;
	}

}
