package com.game.ui;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class entryPoint extends MIDlet {
	boolean first_time = true;
	Display display;

    public void startApp() {
		if (first_time) {
			first_time = false;

			display = Display.getDisplay(this);

			MainMenu mainMenu = new MainMenu(this, display);
			display.setCurrent(mainMenu);
		}
	}

	public void pauseApp() {
		
	}

	public void destroyApp(boolean unconditional) {

	}
}
