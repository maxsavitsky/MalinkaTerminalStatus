package com.maxsavitsky;

import com.maxsavitsky.manager.ContentDispatcher;
import com.maxsavitsky.manager.MessagesDispatcher;
import com.maxsavitsky.source.NetworkSource;
import com.maxsavitsky.tasks.ServicesStatsTask;
import com.maxsavitsky.tasks.SystemStatTask;
import com.maxsavitsky.tasks.TempControlTask;
import com.maxsavitsky.tasks.provider.service.NetworkServicesInfoProvider;
import com.maxsavitsky.tasks.provider.service.ServicesInfoProvider;
import com.maxsavitsky.tasks.provider.system.NetworkSystemInfoProvider;
import com.maxsavitsky.tasks.provider.system.SystemInfoProvider;
import com.maxsavteam.ciconia.annotation.Configuration;
import com.maxsavteam.ciconia.annotation.ObjectFactory;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.util.ArrayList;
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
	public TempControlTask createTempControlTask(MessagesDispatcher messagesDispatcher){
		return new TempControlTask(messagesDispatcher);
	}

	@ObjectFactory
	public ServicesStatsTask createServicesStatsTask(ContentDispatcher contentDispatcher) throws IOException {
		ProgramArguments programArguments = Main.getProgramArguments();

		List<ServicesStatsTask.Service> services;
		String pathToServicesList = programArguments.getPathToServicesList();

		if (programArguments.getSource() instanceof NetworkSource && "from-remote".equals(pathToServicesList))
			services = getServicesFromRemote((NetworkSource) programArguments.getSource());
		else if(programArguments.getPathToServicesList() == null || !SystemUtils.IS_OS_LINUX)
			services = Collections.emptyList();
		else
			services = Main.getServicesFromFile(programArguments.getPathToServicesList());

		return new ServicesStatsTask(contentDispatcher, getServiceInfoProvider(), services);
	}

	private List<ServicesStatsTask.Service> getServicesFromRemote(NetworkSource source) throws IOException {
		String services = source.request("/services-list");
		String[] parts = services.split("\n");
		List<ServicesStatsTask.Service> serviceList = new ArrayList<>();
		for(String part : parts){
			String[] serviceParts = part.split(":");
			serviceList.add(new ServicesStatsTask.Service(serviceParts[0], serviceParts[1]));
		}
		return serviceList;
	}

	private static ServicesInfoProvider getServiceInfoProvider(){
		if(Main.getProgramArguments().getSource() instanceof NetworkSource)
			return new NetworkServicesInfoProvider((NetworkSource) Main.getProgramArguments().getSource());
		return Main.getLocalServicesInfoProvider();
	}

}
