package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Content;

import java.io.IOException;

public abstract class Section {

	private SectionSize lastDeclaredSize;

	protected SectionSize getLastDeclaredSize() {
		return lastDeclaredSize;
	}

	public abstract String getIdentifier();

	public abstract void write(Content content, Terminal terminal) throws IOException;

	public final void notifyTerminalSizeChanged(Terminal terminal, TerminalSize terminalSize) throws IOException {
		lastDeclaredSize = declareSize(terminalSize);
		onTerminalSizeChange(terminal, terminalSize);
	}

	protected void onTerminalSizeChange(Terminal terminal, TerminalSize newTerminalSize) throws IOException{
		lastDeclaredSize = declareSize(newTerminalSize);
	}

	protected void clearLine(Terminal terminal, int width) throws IOException {
		for(int i = 0; i < width; i++)
			terminal.putCharacter(' ');
	}

	public abstract SectionSize declareSize(TerminalSize terminalSize);

	// class, which describes section size and position on the screen
	public static class SectionSize {
		private final int width;
		private final int height;
		private final int x;
		private final int y;

		public SectionSize(int width, int height, int x, int y) {
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

}
