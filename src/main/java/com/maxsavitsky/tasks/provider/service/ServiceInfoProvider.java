package com.maxsavitsky.tasks.provider.service;

import java.io.IOException;
import java.util.List;

public interface ServiceInfoProvider {

	default void fetch(List<String> servicesIds){}

	String getServiceStatus(String serviceId) throws IOException;

}
