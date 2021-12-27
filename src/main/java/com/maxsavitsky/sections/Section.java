package com.maxsavitsky.sections;

import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Line;

import java.io.IOException;

public abstract class Section {

	public static final int WIDTH = 99;
	public static final int HEIGHT = 30;

	private int offsetLines;
	private int offsetSymbols;

	private int limit;

	public abstract String getIdentifier();

	public abstract void write(Line line, Terminal terminal) throws IOException;

}
