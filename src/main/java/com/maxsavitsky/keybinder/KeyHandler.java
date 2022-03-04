package com.maxsavitsky.keybinder;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class KeyHandler {

	private static KeyHandler instance;

	public static void start(Terminal terminal){
		instance = new KeyHandler(terminal);
	}

	private KeyHandler(Terminal terminal){
		new Thread(()->{
			while(true){
				try {
					KeyStroke keyStroke = terminal.readInput();
					processKeyStroke(keyStroke);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void processKeyStroke(KeyStroke keyStroke){
		KeyProcessor.getInstance().process(keyStroke);
	}

}
