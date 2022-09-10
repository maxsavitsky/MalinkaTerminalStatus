package com.maxsavitsky;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ProgramArgumentsParser {

	public static ProgramArguments parse(String[] args) throws FileNotFoundException {
		ProgramArguments programArguments = new ProgramArguments();

		for(String arg : args){
			if(arg.startsWith("--tty=")){
				String tty = arg.substring("--tty=".length());
				programArguments.setOutputStream(new FileOutputStream(tty));
				programArguments.setInputStream(new FileInputStream(tty));
			}else if(arg.startsWith("--temp-control-enabled=")){
				programArguments.setEnableTempControl(
						Boolean.parseBoolean(arg.substring("--temp-control-enabled=".length()))
				);
			}else if(arg.startsWith("--execute-after-startup=")) {
				programArguments.setAfterStartupCommand(
						arg.substring("--execute-after-startup=".length())
				);
			}else if(arg.startsWith("--execute-before-shutdown=")) {
				programArguments.setBeforeShutdownCommand(
						arg.substring("--execute-before-shutdown=".length())
				);
			}else if(arg.startsWith("--port=")) {
				programArguments.setPort(
						Integer.parseInt(arg.substring("--port=".length()))
				);
			} else if(arg.startsWith("--services-list=")){
				programArguments.setPathToServicesList(
						arg.substring("--services-list=".length())
				);
			} else if(arg.startsWith("--mail-properties=")) {
				programArguments.setMailPropertiesFile(
						arg.substring("--mail-properties=".length())
				);
			}else if(arg.startsWith("--temp-control-period=")){
				programArguments.setTempControlPeriod(
						Long.parseLong(arg.substring("--temp-control-period=".length()))
				);
			}else if(arg.startsWith("--system-status-update-period=")) {
				programArguments.setSystemStatusUpdatePeriod(
						Long.parseLong(arg.substring("--system-status-update-period=".length()))
				);
			} else{
				throw new IllegalArgumentException("Unknown argument '" + arg + "'");
			}
		}

		return programArguments;
	}

}
