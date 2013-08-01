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

	public AInterface() {
	}

	@Override
	public final void run() {
		this.startInterface();
		this.controller.getThreadList().remove(Thread.currentThread());
	}

	/**
	 * Starts the interface.
	 */
	public abstract void startInterface();

	public abstract void openInterface();

	/**
	 * @param mainController
	 *            the mainController to set
	 */
	public final void setMainController(final MainController mainController) {
		this.mainController = mainController;
	}

	/**
	 * @param controller
	 *            the controller to set
	 */
	public final void setController(final IController controller) {
		this.controller = controller;
	}

}
