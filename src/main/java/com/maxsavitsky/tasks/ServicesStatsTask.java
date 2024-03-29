package com.maxsavitsky.tasks;

import com.maxsavitsky.Content;
import com.maxsavitsky.manager.ContentDispatcher;
import com.maxsavitsky.tasks.provider.service.ServicesInfoProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServicesStatsTask extends Task {

	private final ContentDispatcher contentDispatcher;
	private final ServicesInfoProvider servicesInfoProvider;
	private final List<Service> services;
	private final List<String> servicesIds;

	public ServicesStatsTask(ContentDispatcher contentDispatcher, ServicesInfoProvider servicesInfoProvider, List<Service> services) {
		this.contentDispatcher = contentDispatcher;
		this.servicesInfoProvider = servicesInfoProvider;
		this.services = services;
		this.servicesIds = services
				.stream()
				.map(Service::getId)
				.collect(Collectors.toList());
	}

	@Override
	public void execute() throws IOException {
		servicesInfoProvider.fetch(servicesIds);

		List<Content> contents = new ArrayList<>();
		for(Service service : services){
			contents.add(
					new Content(
							service.getId(),
							"sys-stat",
							servicesInfoProvider.getServiceStatus(service.getId()),
							service.getName()
					)
			);
		}

		contentDispatcher.dispatch(contents);
	}

	public static class Service {

		private final String id;
		private final String name;

		public Service(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

}
