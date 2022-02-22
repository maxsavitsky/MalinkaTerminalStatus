package com.maxsavitsky;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.sections.MessagesSection;
import com.maxsavitsky.sections.SystemStatusSection;
import com.maxsavitsky.tasks.SystemStatTask;
import com.maxsavitsky.tasks.TaskManager;
import com.maxsavitsky.tasks.TempControlTask;
import org.apache.commons.lang3.SystemUtils;
import oshi.SystemInfo;

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Main {

	private static final SystemInfo SYSTEM_INFO = new SystemInfo();

	public static SystemInfo getSystemInfo() {
		return SYSTEM_INFO;
	}

	public static long startTime;

	public static void main(String[] args) throws IOException {
		startTime = System.currentTimeMillis();
		System.out.println(System.getProperty("user.dir"));

		MessagesListener.getInstance(); // start
		MailSender.getInstance();

		boolean enableTempControl = true;
		boolean enableServicesStats = true;

		InputStream is = System.in;
		OutputStream os = System.out;
		for(String arg : args){
			if(arg.startsWith("--tty=")){
				String tty = arg.substring(6);
				os = new FileOutputStream(tty);
				is = new FileInputStream(tty);
				System.out.println("tty set to " + tty);
			}else if(arg.equals("--disable-temp-control")){
				enableTempControl = false;
				System.out.println("WARNING! Temperature control disabled");
			}else if(arg.equals("--disable-services-stats")){
				enableServicesStats = false;
			} else{
				throw new IllegalArgumentException("Unknown argument '" + arg + "'");
			}
		}

		TerminalScreen terminalScreen = new DefaultTerminalFactory(os, is, StandardCharsets.UTF_8).createScreen();
		terminalScreen.startScreen();
		Terminal terminal = terminalScreen.getTerminal();
		terminal.setCursorVisible(false);
		MessagesController.addSection(new SystemStatusSection());
		MessagesController.addSection(MessagesSection.getInstance());
		MessagesController.start(terminal);

		String msg = "Messages controller started after " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds";
		System.out.println(msg);
		MessagesSection.getInstance().write(
				new Line("t", "msg", msg),
				terminal
		);

		MessagesSection.getInstance().write(
				new Line("t", "msg", "Started as " + Utils.exec("whoami")),
				terminal
		);

		TaskManager.getInstance().schedule(SystemStatTask.TIMER_PERIOD, new SystemStatTask(enableServicesStats));

		if (SystemUtils.IS_OS_LINUX && enableTempControl) {
			TaskManager.getInstance().schedule(TempControlTask.TIMER_PERIOD, new TempControlTask());
		}
	}

}
