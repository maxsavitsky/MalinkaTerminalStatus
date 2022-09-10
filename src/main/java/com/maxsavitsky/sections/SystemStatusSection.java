package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Content;

import java.io.IOException;
import java.util.ArrayList;

public class SystemStatusSection extends Section {

	private final ArrayList<Content> contents = new ArrayList<>();

	@Override
	public void onTerminalSizeChange(Terminal terminal, TerminalSize newTerminalSize) throws IOException {
		rewrite(terminal);
	}

	@Override
	public SectionSize declareSize(TerminalSize terminalSize) {
		int columns = terminalSize.getColumns();
		int rows = terminalSize.getRows();
		int offsetX = 0;
		int offsetY = 0;
		int width = columns / 2;
		return new SectionSize(
				width,
				rows,
				offsetX,
				offsetY
		);
	}

	@Override
	public String getIdentifier() {
		return "sys-stat";
	}

	@Override
	public void write(Content line, Terminal terminal) throws IOException {
		if (line.getMessage() == null)
			throw new UnsupportedOperationException("Message is null");
		int lineIndex = -1;
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).getTag().equals(line.getTag())) {
				lineIndex = i;
				break;
			}
		}
		if (lineIndex == -1) {
			lineIndex = contents.size();
			contents.add(line);
		}

		String content;
		if (line.getLabel() != null && line.getMessage() != null) {
			content = line.getLabel() + ": " + line.getMessage();
		} else {
			content = line.getLabel() == null ? line.getMessage() : line.getLabel();
		}
		if(content.length() > getLastDeclaredSize().getWidth())
			content = content.substring(0, getLastDeclaredSize().getWidth());

		terminal.setCursorPosition(new TerminalPosition(0, lineIndex));
		terminal.putString(content);
	}

	private void rewrite(Terminal terminal) throws IOException {
		for(Content content : contents){
			write(content, terminal);
		}
		for(int j = contents.size(); j < getLastDeclaredSize().getHeight(); j++){
			terminal.setCursorPosition(new TerminalPosition(0, j));
			clearLine(terminal, getLastDeclaredSize().getWidth());
		}
	}
}
