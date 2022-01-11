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

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Main {

	public static long startTime;

	public static void main(String[] args) throws IOException {
		startTime = System.currentTimeMillis();
		System.out.println(System.getProperty("user.dir"));

		MessagesListener.getInstance(); // start
		MailSender.getInstance();

		InputStream is = System.in;
		OutputStream os = System.out;
		if (args.length >= 1) {
			os = new FileOutputStream(args[0]);
			is = new FileInputStream(args[0]);
			System.out.println("Input stream from " + args[0]);
		}
		if (args.length >= 2) {
			is = new FileInputStream(args[1]);
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

		TaskManager.getInstance().schedule(SystemStatTask.TIMER_PERIOD, new SystemStatTask());

		if (SystemUtils.IS_OS_LINUX) {
			TaskManager.getInstance().schedule(TempControlTask.TIMER_PERIOD, new TempControlTask());
		}
	}

}
