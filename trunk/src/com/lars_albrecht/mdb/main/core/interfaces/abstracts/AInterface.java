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
	 * @param mainController
	 *            the mainController to set
	 */
	public final void setMainController(final MainController mainController) {
		this.mainController = mainController;
	}

	/**
	 * Starts the interface.
	 */
	public abstract void startInterface();

}
