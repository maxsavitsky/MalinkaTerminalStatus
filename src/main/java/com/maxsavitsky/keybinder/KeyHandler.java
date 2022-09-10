package com.maxsavitsky.keybinder;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.maxsavteam.ciconia.annotation.Component;

import java.io.IOException;

@Component
public class KeyHandler {

	public void listen(TerminalScreen terminalScreen, OnTerminalShutdownCallback onTerminalShutdownCallback) {
		new Thread(()->{
			while(true){
				try {
					KeyStroke keyStroke = terminalScreen.getTerminal().readInput();
					processKeyStroke(keyStroke, terminalScreen, onTerminalShutdownCallback);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void processKeyStroke(KeyStroke keyStroke, TerminalScreen terminalScreen, OnTerminalShutdownCallback onTerminalShutdownCallback){
		if(isShutdownKeyStroke(keyStroke)){
			onTerminalShutdownCallback.onTerminalShutdown(terminalScreen);
		}
	}

	private boolean isShutdownKeyStroke(KeyStroke keyStroke) {
		return keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == 'x' && keyStroke.isCtrlDown()
				|| keyStroke.getKeyType() == KeyType.Escape;
	}

	public interface OnTerminalShutdownCallback {
		void onTerminalShutdown(TerminalScreen terminalScreen);
	}

}
