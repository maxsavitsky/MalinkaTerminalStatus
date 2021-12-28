package com.maxsavitsky;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.sections.MessagesSection;
import com.maxsavitsky.sections.SystemStatusSection;
import com.maxsavitsky.tasks.SystemStatTask;
import com.maxsavitsky.tasks.Task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

	private static final long TIMER_PERIOD = 5000;

	public static long startTime;

	public static void main(String[] args) throws IOException {
		startTime = System.currentTimeMillis();
		System.out.println(System.getProperty("user.dir"));

		MessagesListener.getInstance(); // start

		InputStream is = System.in;
		OutputStream os = System.out;
		if(args.length >= 1){
			os = new FileOutputStream(args[0]);
			is = new FileInputStream(args[0]);
			System.out.println("Input stream from " + args[0]);
		}
		if(args.length >= 2){
			is = new FileInputStream(args[1]);
		}

		TerminalScreen terminalScreen = new DefaultTerminalFactory(os, is, StandardCharsets.UTF_8).createScreen();
		terminalScreen.startScreen();
		Terminal terminal = terminalScreen.getTerminal();
		terminal.setCursorVisible(false);
		MessagesController.addSection(new SystemStatusSection());
		MessagesController.addSection(MessagesSection.getInstance());
		MessagesController.start(terminal);

		String msg = "Messages controller started after " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds";
		System.out.println(msg);
		MessagesSection.getInstance().write(
				new Line("t", "msg", msg),
				terminal
		);

		ArrayList<Task> tasks = new ArrayList<>();
		tasks.add(new SystemStatTask());

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				for(Task t : tasks){
					try {
						t.execute();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, 2000, TIMER_PERIOD);

		new Thread(()->{
			while(!Thread.currentThread().isInterrupted()) {
				try {
					KeyStroke keyStroke = terminal.pollInput();
					if (keyStroke != null) {
						if (keyStroke.isCtrlDown()
								&& keyStroke.getKeyType() == KeyType.Character
								&& keyStroke.getCharacter() == 'c') {
							System.exit(0);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
