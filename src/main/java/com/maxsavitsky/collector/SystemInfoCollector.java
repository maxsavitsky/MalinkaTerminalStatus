package com.maxsavitsky.collector;

import com.maxsavitsky.Main;
import com.maxsavitsky.tasks.provider.system.SystemInfoData;
import com.maxsavitsky.tasks.provider.system.SystemInfoProvider;
import com.maxsavteam.ciconia.annotation.Component;

import java.io.IOException;

@Component
public class SystemInfoCollector {

	public SystemInfoData collect() throws IOException {
		SystemInfoProvider provider = Main.getLocalSystemInfoProvider();
		provider.fetch();
		return provider.toData();
	}

}
