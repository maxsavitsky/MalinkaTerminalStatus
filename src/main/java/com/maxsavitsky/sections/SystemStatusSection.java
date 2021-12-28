package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Line;

import java.io.IOException;
import java.util.ArrayList;

public class SystemStatusSection extends Section {

	private final int offsetRows = 0;
	private final int offsetColumns = 0;

	private final int rowsLimit = Section.HEIGHT;
	private final int columnsLimit = Section.WIDTH / 2;

	private final ArrayList<String> lines = new ArrayList<>();

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
			if (lines.get(i).equals(line.getTag())) {
				lineIndex = i;
				break;
			}
		}
		if (lineIndex == -1) {
			lineIndex = lines.size();
			lines.add(line.getTag());
		}

		terminal.setCursorPosition(new TerminalPosition(0, lineIndex));
		// clear line
		for (int i = 0; i < columnsLimit; i++)
			terminal.putCharacter(' ');
		terminal.setCursorPosition(new TerminalPosition(0, lineIndex));

		if (line.getLabel() != null && line.getMessage() != null) {
			terminal.putString(line.getLabel() + ": " + line.getMessage());
		} else {
			terminal.putString(line.getLabel() == null ? line.getMessage() : line.getLabel());
		}
	}
}
