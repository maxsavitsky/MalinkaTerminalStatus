package com.maxsavitsky.manager;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.maxsavitsky.Content;
import com.maxsavitsky.keybinder.KeyHandler;
import com.maxsavteam.ciconia.annotation.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TerminalScreensManager {

	private final List<Entity> entities = new ArrayList<>();

	private final KeyHandler keyHandler;

	public TerminalScreensManager(KeyHandler keyHandler) {
		this.keyHandler = keyHandler;
	}

	public void addScreen(SectionsManager sectionsManager, TerminalScreen terminalScreen) throws IOException {
		entities.add(new Entity(sectionsManager, terminalScreen));

		sectionsManager.onTerminalSizeChanged(terminalScreen.getTerminal(), terminalScreen.getTerminalSize());

		terminalScreen.getTerminal().addResizeListener((terminal, newSize) -> {
			try {
				sectionsManager.onTerminalSizeChanged(terminal, newSize);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		keyHandler.listen(terminalScreen, ts->{
			ts.clear();
			try {
				ts.refresh(Screen.RefreshType.COMPLETE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void dispatch(Content content) throws IOException {
		synchronized (entities){
			dispatchInternal(content);
		}
	}

	public void dispatch(List<Content> contents){
		synchronized (entities){
			for(Content content : contents){
				try {
					dispatchInternal(content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void dispatchInternal(Content content) throws IOException {
		for(Entity entity : entities){
			TerminalScreen screen = entity.terminalScreen;
			SectionsManager manager = entity.sectionsManager;
			manager.dispatch(content, screen.getTerminal());
			screen.refresh(Screen.RefreshType.DELTA);
		}
	}

	private static class Entity {

		private final SectionsManager sectionsManager;
		private final TerminalScreen terminalScreen;

		public Entity(SectionsManager sectionsManager, TerminalScreen terminalScreen) {
			this.sectionsManager = sectionsManager;
			this.terminalScreen = terminalScreen;
		}
	}

}
