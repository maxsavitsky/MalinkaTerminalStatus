package com.maxsavitsky.tasks;

import com.maxsavitsky.Line;
import com.maxsavitsky.MailSender;
import com.maxsavitsky.Main;
import com.maxsavitsky.MessagesController;
import com.maxsavitsky.Utils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;

public class TempControlTask extends Task {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static final long TIMER_PERIOD = 1000 * 60L;

	private static final int SHUTDOWN_TEMP = 80;

	private static final int NOTIFICATION_TEMP = 70;

	private long lastTemperaturePrintTime = 0;

	@Override
	public void execute() throws IOException {
		double temp = Main.getSystemInfo().getHardware().getSensors().getCpuTemperature();
		if(temp == 0 || Double.isNaN(temp)){
			logger.error("Failed to get temperature");
			MessagesController.handle(new Line(
					null,
					"msg",
					"Failed to get temperature"
			));
			return;
		}
		long time = System.currentTimeMillis();
		if(time - lastTemperaturePrintTime >= 5L * 60 * 1000) {
			logger.info("Current temperature is {}", temp);
			lastTemperaturePrintTime = time;
		}
		if(temp >= NOTIFICATION_TEMP){
			MessagesController.handle(new Line(
					null,
					"msg",
					"Temp warning: " + temp
			));
			try {
				if(MailSender.getInstance() != null)
					MailSender.getInstance().sendToAdmin("TEMPERATURE WARNING", "Current temperature is " + temp + "°C");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		if (temp >= SHUTDOWN_TEMP) {
			logger.warn("TEMPERATURE IS HIGH THAN {}. SHUTDOWN", SHUTDOWN_TEMP);
			try {
				if(MailSender.getInstance() != null)
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
