package com.maxsavitsky.tasks.provider.service;

import java.io.IOException;
import java.util.List;

public interface ServicesInfoProvider {

	default void fetch(List<String> servicesIds) throws IOException {}

	String getServiceStatus(String serviceId) throws IOException;

}
