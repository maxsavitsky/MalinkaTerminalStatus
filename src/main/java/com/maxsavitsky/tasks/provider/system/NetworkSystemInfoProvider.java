package com.maxsavitsky.tasks.provider.system;

import com.maxsavitsky.source.NetworkSource;
import org.json.JSONObject;

import java.io.IOException;

public class NetworkSystemInfoProvider implements SystemInfoProvider {

	private final NetworkSource networkSource;

	private SystemInfoData lastFetchedData;

	public NetworkSystemInfoProvider(NetworkSource networkSource) {
		this.networkSource = networkSource;
	}

	@Override
	public void fetch() throws IOException {
		String json = networkSource.request("/system-info");
		lastFetchedData = SystemInfoData.parse(new JSONObject(json));
	}

	@Override
	public double[] getCoresLoad() {
		return lastFetchedData.getCoresLoad();
	}

	@Override
	public double getCpuTemperature() {
		return lastFetchedData.getCpuTemperature();
	}

	@Override
	public long getTotalMemorySize() {
		return lastFetchedData.getTotalMemorySize();
	}

	@Override
	public long getUsedMemorySize() {
		return lastFetchedData.getUsedMemorySize();
	}

	@Override
	public long getUsedCacheSize() {
		return lastFetchedData.getUsedCacheSize();
	}

	@Override
	public long getFreeMemorySize() {
		return lastFetchedData.getFreeMemorySize();
	}

	@Override
	public long getAvailableMemorySize() {
		return lastFetchedData.getAvailableMemorySize();
	}

	@Override
	public long getTotalSwapSize() {
		return lastFetchedData.getTotalSwapSize();
	}

	@Override
	public long getUsedSwapSize() {
		return lastFetchedData.getUsedSwapSize();
	}

	@Override
	public int getThreadCount() {
		return lastFetchedData.getThreadCount();
	}

	@Override
	public long getUptime() {
		return lastFetchedData.getUptime();
	}
}
