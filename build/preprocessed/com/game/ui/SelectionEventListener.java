package com.game.ui;

import java.util.Hashtable;

public interface SelectionEventListener {
	
	public static final int OPTION_OK	  = 1;
	public static final int OPTION_CANCEL = 2;
	
	/**
	 * An event was generated, the event type is passed as the first argument.
	 * Any user data will be passed as a dictionary, the keys will be 
	 * class specific and needs to be provided in the API.
	 * @param selectionOption
	 * @param userData 
	 */
	public void selectionEvent(int selectionOption, Hashtable userData);
}
