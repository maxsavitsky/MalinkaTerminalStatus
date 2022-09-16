package com.maxsavitsky;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.manager.MessagesDispatcher;
import com.maxsavitsky.manager.SectionsManager;
import com.maxsavitsky.manager.TerminalScreensManager;
import com.maxsavitsky.sections.MessagesSection;
import com.maxsavitsky.sections.Section;
import com.maxsavitsky.sections.SystemStatusSection;
import com.maxsavitsky.source.NetworkSource;
import com.maxsavitsky.source.Source;
import com.maxsavitsky.tasks.ServicesStatsTask;
import com.maxsavitsky.tasks.SystemStatTask;
import com.maxsavitsky.tasks.TaskManager;
import com.maxsavitsky.tasks.TempControlTask;
import com.maxsavteam.ciconia.annotation.Configuration;
import com.maxsavteam.ciconia.annotation.PostInitialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
public class PostInitializationConfiguration {

	@PostInitialization
	public void initializeTerminalScreensManager(TerminalScreensManager terminalScreensManager) throws IOException {
		System.out.println("Initializing terminal screens manager...");
		List<Section> sections = Arrays.asList(
				new SystemStatusSection(),
				new MessagesSection()
		);
		SectionsManager sectionsManager = new SectionsManager(sections);
		TerminalScreen terminalScreen = createDefaultTerminalScreen();
		terminalScreensManager.addScreen(sectionsManager, terminalScreen);
	}

	private TerminalScreen createDefaultTerminalScreen() throws IOException {
		ProgramArguments programArguments = Main.getProgramArguments();
		TerminalScreen terminalScreen = new DefaultTerminalFactory(programArguments.getOutputStream(), programArguments.getInputStream(), StandardCharsets.UTF_8)
				.createScreen();
		terminalScreen.startScreen();
		terminalScreen.doResizeIfNecessary();
		Terminal terminal = terminalScreen.getTerminal();
		terminal.setCursorVisible(false);
		return terminalScreen;
	}

	@PostInitialization(order = 1)
	public void startMessageBroadcastsListenerIfNeeded(MessagesDispatcher dispatcher) throws IOException {
		Source source = Main.getProgramArguments().getSource();
		if(!(source instanceof NetworkSource))
			return;

		NetworkSource networkSource = (NetworkSource) source;

		int broadcastingPort = Integer.parseInt(networkSource.request("/messages-broadcasting-port"));
		new MessagesBroadcastsListener(dispatcher, broadcastingPort);
	}

	@PostInitialization(order = 1)
	public void runTasksOnce(SystemStatTask systemStatTask, ServicesStatsTask servicesStatsTask, TempControlTask tempControlTask) throws IOException {
		System.out.println("Running tasks once...");
		systemStatTask.execute();
		servicesStatsTask.execute();
		if(Main.getProgramArguments().isEnableTempControl())
			tempControlTask.execute();
	}

	@PostInitialization(order = 2)
	public void initializeTaskManager(TaskManager taskManager, SystemStatTask systemStatTask, TempControlTask tempControlTask){
		System.out.println("Initializing task manager...");
		ProgramArguments programArguments = Main.getProgramArguments();
		taskManager.schedule(programArguments.getSystemStatusUpdatePeriod() * 1000, systemStatTask);

		if(programArguments.isEnableTempControl()){
			taskManager.schedule(programArguments.getTempControlPeriod() * 1000, tempControlTask);
		}
	}

}
