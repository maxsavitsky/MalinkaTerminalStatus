package com.maxsavitsky.controller;

import com.maxsavitsky.Main;
import com.maxsavitsky.Utils;
import com.maxsavitsky.tasks.provider.service.ServicesInfoProvider;
import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.Param;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mapping
@Component
public class ServicesInfoController {

	@Mapping("/services-info")
	public String getServicesInfo(@Param("services") String ids) throws IOException {
		JSONArray jsonArray = new JSONArray(ids);

		List<String> servicesIds = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			servicesIds.add(jsonArray.getString(i));
		}

		ServicesInfoProvider provider = Main.getLocalServicesInfoProvider();
		provider.fetch(servicesIds);

		JSONObject jsonObject = new JSONObject();
		for (String serviceId : servicesIds) {
			jsonObject.put(serviceId, provider.getServiceStatus(serviceId));
		}

		return jsonObject.toString();
	}

	@Mapping("/services-list")
	public String getServicesList() throws IOException {
		try (FileInputStream fis = new FileInputStream("services-list.txt");
		     ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			Utils.transferTo(fis, bos);
			return bos.toString();
		}
	}

}
