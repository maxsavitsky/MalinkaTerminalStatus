package com.maxsavitsky;

import com.maxsavitsky.manager.ContentDispatcher;
import com.maxsavitsky.source.NetworkSource;
import com.maxsavitsky.tasks.ServicesStatsTask;
import com.maxsavitsky.tasks.SystemStatTask;
import com.maxsavitsky.tasks.TempControlTask;
import com.maxsavitsky.tasks.provider.service.DefaultServicesInfoProvider;
import com.maxsavitsky.tasks.provider.service.LinuxServicesInfoProvider;
import com.maxsavitsky.tasks.provider.service.ServiceInfoProvider;
import com.maxsavitsky.tasks.provider.system.DefaultSystemInfoProvider;
import com.maxsavitsky.tasks.provider.system.LinuxSystemInfoProvider;
import com.maxsavitsky.tasks.provider.system.NetworkSystemInfoProvider;
import com.maxsavitsky.tasks.provider.system.SystemInfoProvider;
import com.maxsavteam.ciconia.annotation.Configuration;
import com.maxsavteam.ciconia.annotation.ObjectFactory;
import org.apache.commons.lang3.SystemUtils;

import java.util.Collections;
import java.util.List;

@Configuration
public class ApplicationConfiguration {

	@ObjectFactory
	public SystemStatTask createSystemStatTask(ContentDispatcher contentDispatcher){
		return new SystemStatTask(contentDispatcher, getSystemInfoProvider());
	}

	private SystemInfoProvider getSystemInfoProvider(){
		if(Main.getProgramArguments().getSource() instanceof NetworkSource)
			return new NetworkSystemInfoProvider((NetworkSource) Main.getProgramArguments().getSource());
		return Main.getLocalSystemInfoProvider();
	}

	@ObjectFactory
	public TempControlTask createTempControlTask(ContentDispatcher contentDispatcher){
		return new TempControlTask(contentDispatcher);
	}

	@ObjectFactory
	public ServicesStatsTask createServicesStatsTask(ContentDispatcher contentDispatcher){
		ProgramArguments programArguments = Main.getProgramArguments();
		List<ServicesStatsTask.Service> services;
		if(programArguments.getPathToServicesList() == null || !SystemUtils.IS_OS_LINUX)
			services = Collections.emptyList();
		else
			services = Main.getServicesFromFile(programArguments.getPathToServicesList());

		return new ServicesStatsTask(contentDispatcher, getServiceInfoProvider(), services);
	}

	private static ServiceInfoProvider getServiceInfoProvider(){
		if(SystemUtils.IS_OS_LINUX)
			return new LinuxServicesInfoProvider();
		return new DefaultServicesInfoProvider();
	}

}
