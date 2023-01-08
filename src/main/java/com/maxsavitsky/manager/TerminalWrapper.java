package com.maxsavitsky.manager;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.maxsavitsky.sections.Section;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class TerminalWrapper implements Terminal {

	private final Terminal terminal;
	private final Section.SectionSize sectionSize;

	public TerminalWrapper(Terminal terminal, Section.SectionSize sectionSize) {
		this.terminal = terminal;
		this.sectionSize = sectionSize;
	}

	@Override
	public void enterPrivateMode() throws IOException {
		terminal.enterPrivateMode();
	}

	@Override
	public void exitPrivateMode() throws IOException {
		terminal.exitPrivateMode();
	}

	@Override
	public void clearScreen() throws IOException {
		terminal.clearScreen();
	}

	@Override
	public void setCursorPosition(int x, int y) throws IOException {
		int nx = x + sectionSize.getX();
		int ny = y + sectionSize.getY();
		terminal.setCursorPosition(nx, ny);
	}

	@Override
	public void setCursorPosition(TerminalPosition position) throws IOException {
		setCursorPosition(position.getColumn(), position.getRow());
	}

	@Override
	public TerminalPosition getCursorPosition() throws IOException {
		TerminalPosition position = terminal.getCursorPosition();
		return new TerminalPosition(position.getColumn() - sectionSize.getX(), position.getRow() - sectionSize.getY());
	}

	@Override
	public void setCursorVisible(boolean visible) throws IOException {
		terminal.setCursorVisible(visible);
	}

	@Override
	public void putCharacter(char c) throws IOException {
		TerminalPosition position = terminal.getCursorPosition();
		if(Section.isInSection(position.getColumn(), position.getRow(), sectionSize))
			terminal.putCharacter(c);
	}

	@Override
	public void putString(String string) throws IOException {
		TerminalPosition position = terminal.getCursorPosition();
		int x = position.getColumn();
		int y = position.getRow();
		if(y < sectionSize.getY() || y >= sectionSize.getY() + sectionSize.getHeight())
			return;
		int distX = sectionSize.getX() + sectionSize.getWidth() - x;
		if(distX <= 0)
			return;
		String substring = string.substring(0, Math.min(distX, string.length()));
		terminal.putString(substring);
	}

	@Override
	public TextGraphics newTextGraphics() throws IOException {
		return terminal.newTextGraphics();
	}

	@Override
	public void enableSGR(SGR sgr) throws IOException {
		terminal.enableSGR(sgr);
	}

	@Override
	public void disableSGR(SGR sgr) throws IOException {
		terminal.disableSGR(sgr);
	}

	@Override
	public void resetColorAndSGR() throws IOException {
		terminal.resetColorAndSGR();
	}

	@Override
	public void setForegroundColor(TextColor color) throws IOException {
		terminal.setForegroundColor(color);
	}

	@Override
	public void setBackgroundColor(TextColor color) throws IOException {
		terminal.setBackgroundColor(color);
	}

	@Override
	public void addResizeListener(TerminalResizeListener listener) {
		terminal.addResizeListener(listener);
	}

	@Override
	public void removeResizeListener(TerminalResizeListener listener) {
		terminal.removeResizeListener(listener);
	}

	@Override
	public TerminalSize getTerminalSize() throws IOException {
		return terminal.getTerminalSize();
	}

	@Override
	public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) throws IOException {
		return terminal.enquireTerminal(timeout, timeoutUnit);
	}

	@Override
	public void bell() throws IOException {
		terminal.bell();
	}

	@Override
	public void flush() throws IOException {
		terminal.flush();
	}

	@Override
	public void close() throws IOException {
		terminal.close();
	}

	@Override
	public KeyStroke pollInput() throws IOException {
		return terminal.pollInput();
	}

	@Override
	public KeyStroke readInput() throws IOException {
		return terminal.readInput();
	}

}
