package com.maxsavitsky.manager;

import com.maxsavitsky.Content;
import com.maxsavteam.ciconia.annotation.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ContentDispatcher {

	private final TerminalScreensManager terminalScreensManager;

	public ContentDispatcher(TerminalScreensManager terminalScreensManager) {
		this.terminalScreensManager = terminalScreensManager;
	}

	public void dispatch(Content content) throws IOException {
		terminalScreensManager.dispatch(content);
	}

	public void dispatch(List<Content> contents){
		terminalScreensManager.dispatch(contents);
	}

}
