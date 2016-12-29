package com.game.ui;

import javax.microedition.lcdui.Display;
import com.game.game_data.*;

public class LanguageSelector extends ButtonList {
	SelectionEventListener buttonSelectionListener;

	public LanguageSelector(Display display, SelectionEventListener listener) {
		super();
		buttonSelectionListener = listener;
		init();
	}
		
	private void init() {
		String []languages = Locale.getSupportedLanguages();
		for (int i = 0; i < languages.length; i++) 
			addButton(languages[i]);

		shouldRotateBack(true);
		shouldUnderlineText(true);

		reloadData();
	}

	protected void buttonAtIndexSelected(int index, String buttonTitle) {
		UserData userData = UserData.getUserData();

		userData.setValueForKey(Locale.KEY_LANGUAGE, buttonTitle);
		userData.flush();
		userData.close();
		buttonSelectionListener.selectionEvent(SelectionEventListener.OPTION_OK, null);
	}
}
