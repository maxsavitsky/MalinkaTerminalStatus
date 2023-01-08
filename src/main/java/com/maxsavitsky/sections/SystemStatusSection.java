package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Content;

import java.io.IOException;
import java.util.ArrayList;

public class SystemStatusSection extends Section {

	public static final String ERROR_TAG = "ERROR";

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
	public void write(Content content, Terminal terminal) throws IOException {

		if(content.getTag().equals(ERROR_TAG)){
			for(int i = 1; i < getLastDeclaredSize().getHeight(); i++)
				clearLine(terminal, i);
			terminal.setCursorPosition(new TerminalPosition(0, 1));
			terminal.putString("ERROR: " + content.getMessage());
		}

		if (content.getMessage() == null)
			throw new UnsupportedOperationException("Message is null");
		int lineIndex = -1;
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).getTag().equals(content.getTag())) {
				lineIndex = i;
				break;
			}
		}
		if (lineIndex == -1) {
			lineIndex = contents.size();
			contents.add(content);
		}

		String s;
		if (content.getLabel() != null && content.getMessage() != null) {
			s = content.getLabel() + ": " + content.getMessage();
		} else {
			s = content.getLabel() == null ? content.getMessage() : content.getLabel();
		}
		if(s.length() > getLastDeclaredSize().getWidth())
			s = s.substring(0, getLastDeclaredSize().getWidth());

		terminal.setCursorPosition(new TerminalPosition(0, lineIndex));
		terminal.putString(s);
		clearLine(terminal, getLastDeclaredSize().getWidth() - s.length());
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
