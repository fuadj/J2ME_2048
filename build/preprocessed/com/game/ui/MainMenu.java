package com.game.ui;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import com.game.game_data.*;
import java.io.IOException;
import java.util.Hashtable;

public class MainMenu extends ButtonList implements SelectionEventListener {
	static final int BUTTON_NEW_GAME = 0;
	static final int BUTTON_LANGUAGE = 1;
	static final int BUTTON_SCORES   = 2;
	static final int BUTTON_HELP     = 3;
	static final int BUTTON_EXIT 	 = 4;

	Display display;
	boolean should_shift_screen = false;
	LanguageSelector languageSelector = null;
	HelpDisplay helpDisplay = null;

	MIDlet theApp;

	public MainMenu(MIDlet theApp, Display display) {
		super();
		this.display = display;		// 
		this.theApp = theApp;
		shouldRotateBack(true);
		populateWithMenus();
	}
	
	private void populateWithMenus() {
		if (UserData.isFirstTime()) {
			should_shift_screen = true;
		} else {
			removeAllButtons();

			Locale locale = null;
			try {
				locale = new Locale();
			} catch (IOException ie) {
				displayErrorUsingLanguage();
			}

			String new_game_text = locale.getString("New Game");
			String language_text = locale.getString("Language");
			String scores_text = locale.getString("Scores");
			String help_text = locale.getString("Help");
			String exit_text = locale.getString("Exit");

			addButton(new_game_text);
			addButton(language_text);
			addButton(scores_text);
			addButton(help_text);
			addButton(exit_text);
		}
	}

	private void killApp() {
		theApp.notifyDestroyed();
	}

	private void displayErrorUsingLanguage() {
		killApp();
	}

	protected void initial_setup() {
		if (should_shift_screen)
			display.setCurrent(getLanguageSelector());
		else 
			reloadData();
	}
	
	/**
	 * Get the languageSelector instance, there only is one.
	 * @return 
	 */
	private LanguageSelector getLanguageSelector() {
		if (languageSelector == null)
			languageSelector = new LanguageSelector(display, this);
		return languageSelector;
	}

	/**
	 * The language selector selected a particular language.
	 * the options and data are documented in languageSelector.
	 * @param selectionOption
	 * @param userData 
	 */
	public void selectionEvent(int selectionOption, Hashtable userData) {
		populateWithMenus();
		reloadData();
		display.setCurrent(this);
	}

	protected void buttonAtIndexSelected(int buttonIndex, String buttonTitle) {
		switch (buttonIndex) {
			case BUTTON_NEW_GAME: {
				GameScreen gameScreen = new GameScreen(display, this);
				display.setCurrent(gameScreen);
				break;
			}
			case BUTTON_SCORES: {
				ScoresDisplay scoreDisplay = new ScoresDisplay(this);
				display.setCurrent(scoreDisplay);
				break;
			}
			case BUTTON_LANGUAGE: {
				display.setCurrent(getLanguageSelector());
				break;
			}
			case BUTTON_HELP: {
				HelpDisplay helpDisplay = new HelpDisplay(display, this);
				display.setCurrent(helpDisplay);
				break;
			}
			case BUTTON_EXIT: {
				killApp();
				break;
			}
		}
	}
}
