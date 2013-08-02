/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces;

import java.awt.Desktop;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import com.lars_albrecht.mdb.main.core.interfaces.abstracts.AInterface;
import com.lars_albrecht.mdb.main.core.interfaces.telnet.TelnetRunner;

/**
 * @author lalbrecht
 * 
 */
public class TelnetInterface extends AInterface {

	public TelnetInterface() {
		super();
		this.canOpened = true;
	}

	@Override
	public void startInterface() {
		final int port = 23;
		final int maxConnections = 100;
		// Listen for incoming connections and handle them
		int i = 0;

		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			Socket client;

			while ((i++ < maxConnections) || (maxConnections == 0)) {
				client = server.accept();
				final TelnetRunner runner = new TelnetRunner(this.mainController, client);
				final Thread t = new Thread(runner);
				t.start();
			}
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				server.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void openInterface() {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI("telnet://localhost:23"));
			} catch (final IOException | URISyntaxException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Die Telnetverbindung konnte nicht geöffnet werden!", "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
