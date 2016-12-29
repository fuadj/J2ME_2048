package com.game.ui;

import com.game.game_data.UserData;
import com.game.ui.*;

public class ScoresDisplay extends ButtonList {
	SelectionEventListener eventListener;
	boolean sentSignal;

	public ScoresDisplay(SelectionEventListener eventListener) {
		super();

		this.eventListener = eventListener;

		super.selectedColor = super.normalColor;
		super.selectedTextColor = super.normalTextColor;

		UserData userData = UserData.getUserData();

		String text = userData.getValueForKey(GameScreen.KEY_SCORES);
		userData.close();

		int[] scores = GameScreen.extractScores(text);
		if (scores.length == 0) scores = new int[] {0};

		for (int i = 0; i < scores.length; i++)
			addButton(String.valueOf(scores[i]));

		reloadData();
	}

	protected void initial_setup() {
		sentSignal = false;
	}

	protected void keyPressed(int keyCode) {
		if (!sentSignal) {
			sentSignal = true;
			eventListener.selectionEvent(0, null);
		}
	}
}
