package com.maxsavitsky;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.io.OutputStream;

@Getter
@Setter
public class ProgramArguments {

	private boolean enableTempControl = true;
	private boolean enableServicesStats = true;
	private String afterStartupCommand = null;
	private String beforeShutdownCommand = null;
	private int port = 8000;
	private String pathToServicesList = null;
	private String mailPropertiesFile = null;

	private long tempControlPeriod = 15;
	private long systemStatusUpdatePeriod = 1;

	private InputStream inputStream = System.in;
	private OutputStream outputStream = System.out;

}