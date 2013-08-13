/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.lars_albrecht.mdb.main.core.interfaces.abstracts.AInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.WebServerInterface;
import com.lars_albrecht.mdb.main.core.interfaces.web.pages.abstracts.AbstractFileDetailsOutputItem;

/**
 * A WebInterface to control the files and services.
 * 
 * @author lalbrecht
 * 
 */
public class WebInterface extends AInterface {

	final ArrayList<Thread>					threadList				= new ArrayList<Thread>();
	protected AbstractFileDetailsOutputItem	fileDetailsOutputItem	= null;
	private int								port					= 8080;

	public WebInterface() {
		super();
		this.canOpened = true;
	}

	/**
	 * @return the fileDetailsOutputItem
	 */
	public final AbstractFileDetailsOutputItem getFileDetailsOutputItem() {
		return this.fileDetailsOutputItem;
	}

	/**
	 * @return the port
	 */
	public final int getPort() {
		return this.port;
	}

	@Override
	public void openInterface() {
		try {
			Desktop.getDesktop().browse(new URI("http://localhost:" + this.getPort()));
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param fileDetailsOutputItem
	 */
	public void setFileDetailsOutputItem(final AbstractFileDetailsOutputItem fileDetailsOutputItem) {
		this.fileDetailsOutputItem = fileDetailsOutputItem;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public final void setPort(final int port) {
		this.port = port;
	}

	@Override
	public void startInterface() {
		this.threadList.add(new Thread(new WebServerInterface(this.mainController, this)));
		this.threadList.get(this.threadList.size() - 1).start();
	}

}
