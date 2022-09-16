package com.maxsavitsky.collector;

import com.maxsavitsky.Main;
import com.maxsavitsky.tasks.provider.service.ServicesInfoProvider;
import com.maxsavteam.ciconia.annotation.Component;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@Component
public class ServicesInfoCollector {

	public String collect(List<String> servicesIds) throws IOException {
		ServicesInfoProvider provider = Main.getLocalServicesInfoProvider();
		provider.fetch(servicesIds);

		JSONObject jsonObject = new JSONObject();
		for(String serviceId : servicesIds){
			jsonObject.put(serviceId, provider.getServiceStatus(serviceId));
		}

		return jsonObject.toString();
	}

}
