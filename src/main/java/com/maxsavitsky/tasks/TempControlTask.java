package com.maxsavitsky.tasks;

import com.maxsavitsky.Utils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;

public class TempControlTask extends Task {

	public static final long TIMER_PERIOD = 1000 * 60;

	private static final int SHUTDOWN_TEMP = 80;

	@Override
	public void execute() throws IOException {
		String temperature = Utils.exec("vcgencmd measure_temp").substring(5);
		int temp = (int) Double.parseDouble(temperature.substring(0, temperature.length() - 3));
		System.out.println("Current temperature is " + temp);
		// TODO: 28.12.2021 notify about high temperature some way
		if(temp >= SHUTDOWN_TEMP){
			System.out.println("TEMPERATURE IS HIGH THAN " + SHUTDOWN_TEMP + ". SHUTDOWN");
			if(SystemUtils.IS_OS_LINUX){
				Utils.exec("shutdown -h now");
				System.exit(0);
			}
		}
	}



}
