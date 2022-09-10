package com.maxsavitsky;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.manager.ContentDispatcher;
import com.maxsavitsky.manager.SectionsManager;
import com.maxsavitsky.manager.TerminalScreensManager;
import com.maxsavitsky.sections.MessagesSection;
import com.maxsavitsky.sections.Section;
import com.maxsavitsky.sections.SystemStatusSection;
import com.maxsavitsky.tasks.SystemStatTask;
import com.maxsavitsky.tasks.TaskManager;
import com.maxsavitsky.tasks.TempControlTask;
import com.maxsavteam.ciconia.annotation.Configuration;
import com.maxsavteam.ciconia.annotation.PostInitialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Configuration
public class ApplicationConfiguration {

	@PostInitialization
	public void initializeTerminalScreensManager(TerminalScreensManager terminalScreensManager) throws IOException {
		List<Section> sections = List.of(
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

	@PostInitialization
	public void initializeTaskManager(TaskManager taskManager, ContentDispatcher contentDispatcher){
		System.out.println("Initializing task manager");
		ProgramArguments programArguments = Main.getProgramArguments();
		taskManager.schedule(programArguments.getSystemStatusUpdatePeriod() * 1000, createSystemStatTask(contentDispatcher));

		if(programArguments.isEnableTempControl()){
			taskManager.schedule(programArguments.getTempControlPeriod() * 1000, createTempControlTask(contentDispatcher));
		}
	}

	private SystemStatTask createSystemStatTask(ContentDispatcher contentDispatcher){
		ProgramArguments programArguments = Main.getProgramArguments();
		List<SystemStatTask.Service> services;
		if(programArguments.getPathToServicesList() == null)
			services = Collections.emptyList();
		else
			services = Main.getServicesFromFile(programArguments.getPathToServicesList());
		return new SystemStatTask(contentDispatcher, programArguments.isEnableServicesStats(), services, Main.getSystemInfoProvider());
	}

	private TempControlTask createTempControlTask(ContentDispatcher contentDispatcher){
		return new TempControlTask(contentDispatcher);
	}

}
