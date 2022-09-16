package com.maxsavitsky.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxsavitsky.collector.SystemInfoCollector;
import com.maxsavitsky.tasks.provider.system.SystemInfoData;
import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;

import java.io.IOException;

@Mapping
@Component
public class SystemInfoController {

	private final SystemInfoCollector systemInfoCollector;

	public SystemInfoController(SystemInfoCollector systemInfoCollector){
		this.systemInfoCollector = systemInfoCollector;
	}

	@Mapping("/system-info")
	public String getSystemInfo() throws IOException {
		SystemInfoData data = systemInfoCollector.collect();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(data);
	}

}
