package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Line;

import java.io.IOException;
import java.util.ArrayList;

public class SystemStatusSection extends Section {

	private int rowsOffset;
	private int columnsOffset;

	private int rowsLimit;
	private int columnsLimit;

	private final ArrayList<Line> lines = new ArrayList<>();

	public SystemStatusSection(TerminalScreen terminalScreen){
		changeSize(terminalScreen.getTerminalSize());
	}

	@Override
	public void onTerminalSizeChange(Terminal terminal, TerminalSize newTerminalSize) throws IOException {
		changeSize(newTerminalSize);
		rewrite(terminal);
	}

	private void changeSize(TerminalSize terminalSize){
		int columns = terminalSize.getColumns();
		int rows = terminalSize.getRows();
		rowsOffset = 0;
		columnsOffset = 0;
		rowsLimit = rows;
		columnsLimit = columns / 2;
	}

	@Override
	public String getIdentifier() {
		return "sys-stat";
	}

	@Override
	public void write(Line line, Terminal terminal) throws IOException {
		if (line.getMessage() == null)
			throw new UnsupportedOperationException("Message is null");
		int lineIndex = -1;
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).getTag().equals(line.getTag())) {
				lineIndex = i;
				break;
			}
		}
		if (lineIndex == -1) {
			lineIndex = lines.size();
			lines.add(line);
		}
		lineIndex += rowsOffset;

		terminal.setCursorPosition(new TerminalPosition(columnsOffset, lineIndex));

		String content;
		if (line.getLabel() != null && line.getMessage() != null) {
			content = line.getLabel() + ": " + line.getMessage();
		} else {
			content = line.getLabel() == null ? line.getMessage() : line.getLabel();
		}
		if(content.length() > columnsLimit)
			content = content.substring(0, columnsLimit);
		terminal.putString(content);
		clearLine(terminal, columnsLimit - content.length());
	}

	private void rewrite(Terminal terminal) throws IOException {
		for(Line line : lines){
			write(line, terminal);
		}
		for(int j = lines.size(); j < rowsLimit; j++){
			terminal.setCursorPosition(new TerminalPosition(columnsOffset, j));
			clearLine(terminal, columnsLimit);
		}
	}
}
