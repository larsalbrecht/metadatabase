/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.abstracts;

import com.lars_albrecht.mdb.main.core.controller.MainController;
import com.lars_albrecht.mdb.main.core.controller.interfaces.IController;

/**
 * 
 * @author lalbrecht
 * 
 */
public abstract class AInterface implements Runnable {

	protected MainController	mainController	= null;
	protected IController		controller		= null;
	public boolean				canOpened		= false;

	public AInterface(final MainController mainController) {
		this.mainController = mainController;
	}

	public abstract void openInterface();

	@Override
	public final void run() {
		this.startInterface();
		this.controller.getThreadList().remove(Thread.currentThread());
	}

	/**
	 * @param controller
	 *            the controller to set
	 */
	public final void setController(final IController controller) {
		this.controller = controller;
	}

	/**
	 * Starts the interface.
	 */
	public abstract void startInterface();

}
