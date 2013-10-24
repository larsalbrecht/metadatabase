/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.telnet;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.lars_albrecht.mdb.main.core.controller.MainController;

/**
 * @author lalbrecht
 * 
 *         This is a testintegration for lanterna. If this works well, replace
 *         this with "TelnetRunner".
 * 
 */
public class Telnet2Runner implements Runnable {

	@SuppressWarnings("unused")
	private MainController	mainController	= null;
	private Socket			client			= null;

	public Telnet2Runner(final MainController mainController, final Socket client) {
		this.mainController = mainController;
		this.client = client;
		System.out.println("created new runner");
	}

	@Override
	public void run() {
		Terminal terminal = null;
		try {
			terminal = TerminalFacade.createTerminal(this.client.getInputStream(), this.client.getOutputStream(), Charset.forName("UTF-8"));
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final Screen screen = new Screen(terminal);
		final GUIScreen gui = new GUIScreen(screen);
		if (gui == null) {
			System.err.println("Couldn't allocate a terminal!");
			return;
		}
		gui.getScreen().startScreen();

	}

}
