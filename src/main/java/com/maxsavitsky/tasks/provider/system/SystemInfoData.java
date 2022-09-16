package com.maxsavitsky.tasks.provider.system;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Getter
@Setter
public class SystemInfoData {

	private double[] coresLoad;
	private double cpuTemperature;
	private long totalMemorySize;
	private long usedMemorySize;
	private long usedCacheSize;
	private long freeMemorySize;
	private long availableMemorySize;
	private long totalSwapSize;
	private long usedSwapSize;

	public static SystemInfoData parse(JSONObject jsonObject){
		SystemInfoData systemInfoData = new SystemInfoData();
		systemInfoData.coresLoad = new double[jsonObject.getJSONArray( "coresLoad" ).length()];
		for(int i = 0; i < systemInfoData.coresLoad.length; i++){
			systemInfoData.coresLoad[i] = jsonObject.getJSONArray( "coresLoad" ).getDouble( i );
		}
		systemInfoData.cpuTemperature = jsonObject.getDouble( "cpuTemperature" );
		systemInfoData.totalMemorySize = jsonObject.getLong( "totalMemorySize" );
		systemInfoData.usedMemorySize = jsonObject.getLong( "usedMemorySize" );
		systemInfoData.usedCacheSize = jsonObject.getLong( "usedCacheSize" );
		systemInfoData.freeMemorySize = jsonObject.getLong( "freeMemorySize" );
		systemInfoData.availableMemorySize = jsonObject.getLong( "availableMemorySize" );
		systemInfoData.totalSwapSize = jsonObject.getLong( "totalSwapSize" );
		systemInfoData.usedSwapSize = jsonObject.getLong( "usedSwapSize" );
		return systemInfoData;
	}

}
