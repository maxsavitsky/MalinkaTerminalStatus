package com.maxsavitsky.source;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.IOException;
import java.util.Objects;

public class NetworkSource implements Source {

	private final OkHttpClient httpClient;
	private final String address;

	public NetworkSource(String source, int port) {
		validate(source, port);
		address = "http://" + source + ":" + port;
		httpClient = new OkHttpClient.Builder().build();
	}

	public String request(String path) throws IOException {
		String url = address + path;
		Request request = new Request.Builder()
				.url(url)
				.build();
		try(Response response = httpClient.newCall(request).execute()) {
			return Objects.requireNonNull(response.body()).string();
		}
	}

	private static void validate(String address, int port){
		if(port < 0 || port > 65535){
			throw new IllegalArgumentException("Port must be in range [0, 65535]");
		}
		if(!InetAddressValidator.getInstance().isValid(address)){
			throw new IllegalArgumentException("Invalid address");
		}
	}

}
