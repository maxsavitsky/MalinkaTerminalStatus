package com.maxsavitsky.manager;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.maxsavitsky.Content;
import com.maxsavitsky.sections.Section;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SectionsManager {

	private final List<Section> sections;

	public SectionsManager(List<Section> sections) {
		this.sections = sections;
	}

	public void dispatch(Content content, Terminal terminal) throws IOException {
		for(Section section : sections){
			if(section.getIdentifier().equals(content.getSectionId())){
				Terminal terminalWrapper = createWrapper(terminal, section);
				section.write(content, terminalWrapper);
			}
		}
	}

	public void onTerminalSizeChanged(Terminal terminal, TerminalSize newSize) throws IOException {
		for(Section section : sections){
			section.notifyTerminalSizeChanged(terminal, newSize);
		}
	}

	private Terminal createWrapper(Terminal terminal, Section section) throws IOException {
		return new TerminalWrapper(terminal, section.declareSize(terminal.getTerminalSize()));
	}

}
