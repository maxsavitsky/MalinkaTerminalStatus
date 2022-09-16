package com.maxsavitsky.tasks.provider.service;

import com.maxsavitsky.source.NetworkSource;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkServicesInfoProvider implements ServicesInfoProvider {

	private final NetworkSource networkSource;

	public NetworkServicesInfoProvider(NetworkSource networkSource) {
		this.networkSource = networkSource;
	}

	private Map<String, String> servicesInfos;

	@Override
	public void fetch(List<String> servicesIds) throws IOException {
		servicesInfos = new HashMap<>();
		String ids = String.join(",", servicesIds);
		String info = networkSource.request("/services-info?services=[" + ids + "]");
		JSONObject jsonObject = new JSONObject(info);
		for(String id : servicesIds){
			servicesInfos.put(id, jsonObject.getString(id));
		}
	}

	@Override
	public String getServiceStatus(String serviceId) throws IOException {
		return servicesInfos.getOrDefault(serviceId, null);
	}
}
