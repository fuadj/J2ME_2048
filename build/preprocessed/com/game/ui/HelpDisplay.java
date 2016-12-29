package com.game.ui;

import com.game.game_data.Locale;
import javax.microedition.lcdui.*;

public class HelpDisplay extends Form implements CommandListener {
	public static final int HELP_READ = 1;

	SelectionEventListener eventListener;
	Display display;

	Command OK;

	public HelpDisplay(Display display, SelectionEventListener listener) {
		super("");
		this.display = display;
		eventListener = listener;

		String helpText = null;
		try {
			Locale locale = new Locale();

			OK = new Command(locale.getString("OK_COMMAND"), Command.OK, 1);
			addCommand(OK);

			helpText = locale.getString("Help_text");
		} catch (Exception e) {
			OK = new Command("OK", Command.OK, 1);
			helpText = "Move tiles around and collide them to join them";
		}

		addCommand(OK);
		append(helpText);

		setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		eventListener.selectionEvent(HELP_READ, null);
	}
}
