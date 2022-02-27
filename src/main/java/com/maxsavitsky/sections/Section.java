package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Line;

import java.io.IOException;

public abstract class Section {

	public abstract String getIdentifier();

	public abstract void write(Line line, Terminal terminal) throws IOException;

	public void onTerminalSizeChange(Terminal terminal, TerminalSize newTerminalSize) throws IOException{
	}

	protected void clearLine(Terminal terminal, int width) throws IOException {
		for(int i = 0; i < width; i++)
			terminal.putCharacter(' ');
	}

}
