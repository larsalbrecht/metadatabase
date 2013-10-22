/**
 * 
 */
package com.lars_albrecht.general.utilities.objects.EventArrayList.event;

import java.util.EventListener;

/**
 * @author lalbrecht
 * 
 */
public interface ArrayListListener extends EventListener {

	void arrayListenerAdd(ArrayListEvent e);

	void arrayListenerClear(ArrayListEvent e);

	void arrayListenerRemove(ArrayListEvent e);

}
