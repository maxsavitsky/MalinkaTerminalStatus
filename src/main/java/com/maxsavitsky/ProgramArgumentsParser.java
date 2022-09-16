package com.maxsavitsky;

import com.maxsavitsky.source.NetworkSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ProgramArgumentsParser {

	public static ProgramArguments parse(String[] args) throws FileNotFoundException {
		ProgramArguments programArguments = new ProgramArguments();

		for(String arg : args){
			if(!arg.startsWith("--"))
				throw new IllegalArgumentException("Invalid argument: " + arg);

			String argName;
			if(arg.contains("=")){
				argName = arg.substring(2, arg.indexOf('='));
			}else{
				argName = arg.substring(2);
			}

			String value = null;
			if(arg.contains("=")){
				value = arg.substring(arg.indexOf('=') + 1);
			}

			if(argName.equals("tty")){
				programArguments.setOutputStream(new FileOutputStream(value));
				programArguments.setInputStream(new FileInputStream(value));
			}else if(argName.equals("temp-control-enabled")){
				programArguments.setEnableTempControl(
						Boolean.parseBoolean(value)
				);
			}else if(argName.equals("execute-after-startup")) {
				programArguments.setAfterStartupCommand(
						value
				);
			}else if(argName.equals("execute-before-shutdown")) {
				programArguments.setBeforeShutdownCommand(
						value
				);
			}else if(argName.equals("port")) {
				if(value == null)
					throw new IllegalArgumentException("Port must be specified");
				programArguments.setPort(
						Integer.parseInt(value)
				);
			} else if(argName.equals("services-list")){
				programArguments.setPathToServicesList(
						value
				);
			} else if(argName.equals("mail-properties")) {
				programArguments.setMailPropertiesFile(
						value
				);
			}else if(argName.equals("temp-control-period")){
				if(value == null)
					throw new IllegalArgumentException("Temp control period must be specified");
				programArguments.setTempControlPeriod(
						Long.parseLong(value)
				);
			}else if(argName.equals("system-status-update-period")) {
				if(value == null)
					throw new IllegalArgumentException("System status update period must be specified");
				programArguments.setSystemStatusUpdatePeriod(
						Long.parseLong(value)
				);
			} else if(argName.equals("source")) {
				if (value != null && !"local".equals(value)) {
					String[] parts = value.split(":");
					programArguments.setSource(new NetworkSource(parts[0], Integer.parseInt(parts[1])));
				}
			} else if(argName.equals("api-port")) {
				if (value == null)
					throw new IllegalArgumentException("API port must be specified");
				programArguments.setApiPort(Integer.parseInt(value));
			} else if(argName.equals("messages-broadcasting-port")) {
				if(value == null)
					throw new IllegalArgumentException("Messages broadcasting port must be specified");
				programArguments.setMessagesBroadcastingPort(Integer.parseInt(value));
			} else {
				throw new IllegalArgumentException("Unknown argument '" + argName + "'");
			}
		}

		return programArguments;
	}

}
