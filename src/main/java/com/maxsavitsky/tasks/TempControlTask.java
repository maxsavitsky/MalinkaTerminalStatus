package com.maxsavitsky.tasks;

import com.maxsavitsky.Line;
import com.maxsavitsky.MailSender;
import com.maxsavitsky.Main;
import com.maxsavitsky.MessagesController;
import com.maxsavitsky.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;

import javax.mail.MessagingException;
import java.io.IOException;

public class TempControlTask extends Task {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final int SHUTDOWN_TEMP = 80;

	private static final int NOTIFICATION_TEMP = 70;

	private long lastTemperaturePrintTime = 0;

	private void printMessage(String message) throws IOException {
		MessagesController.handle(new Line(
				null,
				"msg",
				message
		));
	}

	private void sendMail(String title, String message) {
		try {
			if (MailSender.getInstance() != null)
				MailSender.getInstance().sendToAdmin(title, message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() throws IOException {
		double temp = Main.getSystemInfo().getHardware().getSensors().getCpuTemperature();
		if (temp == 0 || Double.isNaN(temp)) {
			logger.error("Failed to get temperature");
			printMessage("Failed to get temperature");
			return;
		}
		long time = System.currentTimeMillis();
		if (time - lastTemperaturePrintTime >= 5L * 60 * 1000) {
			logger.info("Current temperature is {}", temp);
			lastTemperaturePrintTime = time;
		}

		if (temp >= SHUTDOWN_TEMP) {
			logger.warn("TEMPERATURE IS HIGH THAN {}. SHUTDOWN", SHUTDOWN_TEMP);
			printMessage("Overheating! Temp: " + temp);
			sendMail("OVERHEATING", "Temperature is " + temp + "°C. Limit is " + SHUTDOWN_TEMP + "°C. SHUTDOWN");
			String shutdownCommand = getShutdownCommand();
			if (shutdownCommand == null) {
				printMessage("Cannot get shutdown command for current OS: " + SystemInfo.getCurrentPlatform());
			} else {
				Utils.exec(shutdownCommand);
				System.exit(0);
			}
		}else if (temp >= NOTIFICATION_TEMP) {
			printMessage("Temp warning: " + temp);
			sendMail("TEMPERATURE WARNING", "Current temperature is " + temp + "°C");
		}
	}

	private String getShutdownCommand() {
		return switch (SystemInfo.getCurrentPlatform()) {
			case LINUX, MACOS, FREEBSD, KFREEBSD, NETBSD, OPENBSD -> "shutdown -h +5";
			case WINDOWS, WINDOWSCE -> "shutdown /s /f /t 5";
			default -> null;
		};
	}


}
