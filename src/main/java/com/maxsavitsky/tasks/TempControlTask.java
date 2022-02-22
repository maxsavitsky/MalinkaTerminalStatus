package com.maxsavitsky.tasks;

import com.maxsavitsky.Line;
import com.maxsavitsky.MailSender;
import com.maxsavitsky.Main;
import com.maxsavitsky.MessagesController;
import com.maxsavitsky.Utils;
import org.apache.commons.lang3.SystemUtils;

import javax.mail.MessagingException;
import java.io.IOException;

public class TempControlTask extends Task {

	public static final long TIMER_PERIOD = 1000 * 60L;

	private static final int SHUTDOWN_TEMP = 80;

	private static final int NOTIFICATION_TEMP = 70;

	@Override
	public void execute() throws IOException {
		String temperature = Utils.exec("vcgencmd measure_temp").substring(5);
		double temp = Main.getSystemInfo().getHardware().getSensors().getCpuTemperature();
		if(temp == 0 || Double.isNaN(temp)){
			System.out.println("Failed to get temperature");
			MessagesController.handle(new Line(
					null,
					"msg",
					"Failed to get temperature"
			));
			return;
		}
		System.out.println("Current temperature is " + temp);
		if(temp >= NOTIFICATION_TEMP){
			MessagesController.handle(new Line(
					null,
					"msg",
					"Temp warning: " + temperature
			));
			try {
				MailSender.getInstance().sendToAdmin("TEMPERATURE WARNING", "Current temperature is " + temp + "°C");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		if (temp >= SHUTDOWN_TEMP) {
			System.out.println("TEMPERATURE IS HIGH THAN " + SHUTDOWN_TEMP + ". SHUTDOWN");
			try {
				MailSender.getInstance().sendToAdmin("OVERTEMPERATURE", "Temperature is " + temp + "°C. Limit is " + SHUTDOWN_TEMP + "°C. SHUTDOWN");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			if (SystemUtils.IS_OS_LINUX) {
				Utils.exec("shutdown -h now");
				System.exit(0);
			}
		}
	}


}
