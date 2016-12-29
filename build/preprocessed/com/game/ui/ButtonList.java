package com.game.ui;

import javax.microedition.lcdui.*;
import java.util.Vector;
import com.game.geometry.*;

abstract public class ButtonList extends Canvas {
	static final double EDGE_SPACE_FACTOR 		= 2.3;
	static final double BUTTON_SIZE_FACTOR		= 1.3;

	static final double WIDTH_FACTOR_PORTRAIT 			= 0.62;		// the button width will be 82% of the width
	static final double WIDTH_FACTOR_LANDSCAPE 			= 0.72;		// the button width will be 72% of the width

	Vector buttonTitles;
	Vector buttons;

	int currentSelection;

	protected int backgroundColor;
	protected int normalColor;
	protected int selectedColor;

	protected int normalTextColor;
	protected int selectedTextColor;

	boolean shouldRollBack;
	boolean shouldUnderlineText;

	boolean is_first_time;

	public ButtonList() {
		setFullScreenMode(true);
		initializeWithDefaults();
	}

	private void initializeWithDefaults() {
		buttonTitles = new Vector();
		buttons = new Vector();
		currentSelection = 0;

		shouldRollBack = false;
		shouldUnderlineText = false;

		backgroundColor = 0xffF1E8C9;
		//backgroundColor = 0xffFBF8EF;
		normalColor = 0xFFBBADA0;
		selectedColor = 0xFFECC400;

		//normalTextColor = 0xFFFFF2E5;
		//normalTextColor = 0xFF880015;
		normalTextColor = 0xFF3F48CC;
		//selectedTextColor = 0xFFE0E0A3;
		//selectedTextColor = 0xFF3F48CC;
		selectedTextColor = 0xFF880015;
		is_first_time = true;
	}

	/**
	 * By calling this, the screen will be updated with the new buttons.
	 * Users should call this after they add buttons to make their change
	 * noticed.
	 */
	public void reloadData() {
		calculateButtonFrames();
		repaint();
	}

	private void calculateButtonFrames() {
		if (buttonTitles.isEmpty()) return;		// we have no buttons to begin with

		double current_height = getHeight();

		double gap_factor = 2 * EDGE_SPACE_FACTOR + buttonTitles.size() * (EDGE_SPACE_FACTOR * BUTTON_SIZE_FACTOR + 1) - 1;		// this is a formula

		double gap = current_height / gap_factor;

		double edge_space = EDGE_SPACE_FACTOR * gap;
		double button_height = BUTTON_SIZE_FACTOR * edge_space;

		double width_factor = (getHeight() > getWidth()) ? WIDTH_FACTOR_PORTRAIT : WIDTH_FACTOR_LANDSCAPE;
		int button_width = (int)(getWidth() * width_factor);
		int horizontal_space = (getWidth() - button_width) / 2;

		buttons = new Vector();		// start out with a fresh batch

		for (int i = 0; i < buttonTitles.size(); i++) {		
			int button_y = (int)(edge_space + i * (button_height + gap));

			Rect button_i_bounds = new Rect(horizontal_space, button_y, button_width, (int)button_height);
			Button button_i = new Button(button_i_bounds, (String)buttonTitles.elementAt(i));

			buttons.addElement(button_i);
		}	

	}

	public void shouldRotateBack(boolean shouldRotate) {
		shouldRollBack = shouldRotate;
	}

	public void shouldUnderlineText(boolean shouldUnderline) {
		shouldUnderlineText = shouldUnderline;
	}

	/**
	 * Subclass should use this if they want to set the current displayable
	 * to another window when the view first loads
	 */
	protected void initial_setup() {

	}

	protected void showNotify() {
		if (is_first_time) {
			is_first_time = false;
			initial_setup();
		}
	}

	protected void hideNotify() {
		is_first_time = false;		// save it for our first time
	}

	/**
	 * Use this to add new buttons to this view
	 * @param buttonTitle 
	 */
	public void addButton(String buttonTitle) {
		buttonTitles.addElement(buttonTitle);
	}

	/**
	 * removes all the buttons in this view
	 */
	public void removeAllButtons() {
		buttonTitles.removeAllElements();
		currentSelection = 0;
	}


	/**
	 * Subclasses might want to override this, this notifies the current
	 * ButtonList view that a button at the particular index was selected.
	 * This is just a convenience method, so they shouldn't go through
	 * keyPressed and checking the key values.
	 * @param index
	 * @param buttonTitle 
	 */
	protected void buttonAtIndexSelected(int index, String buttonTitle) {

	}

	protected void keyPressed(int keyCode) {
		int gameAction = getGameAction(keyCode);

		if (gameAction == FIRE) {		// the user has selected a button, we need to notify our handler
			String title = (String)buttonTitles.elementAt(currentSelection);
			buttonAtIndexSelected(currentSelection, title);
		} else {
			int direction;

			switch (gameAction) {
				case UP: direction = -1; break;		// we are moving up, so decrease the currentSelection index
				case DOWN: direction = 1; break;	
				default: direction = 0;
			}

			if (direction == 0) return;		// we only want up/down

			int newSelectionIndex = currentSelection + direction;

			if (newSelectionIndex == buttons.size()) {		// we've gone off from below, should we roll back or stay there?
				if (shouldRollBack) newSelectionIndex = 0;
				else newSelectionIndex = currentSelection;
			} else if (newSelectionIndex < 0) {			// we've gone off from top
				if (shouldRollBack) newSelectionIndex = (buttons.size() - 1);
				else newSelectionIndex = currentSelection;
			}

			if (currentSelection != newSelectionIndex) {		// just for efficiency reasons
				currentSelection = newSelectionIndex;
				repaint();	
			}
		}
	}

	/**
	 * This is where the view will be drawn.
	 * @param g 
	 */
	protected void paint(Graphics g) {
		g.setClip(0, 0, getWidth(), getHeight());
		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());

		for (int i = 0; i < buttons.size(); i++) {
			boolean is_selected = (i == currentSelection);
			Button button_i = (Button)buttons.elementAt(i);

			button_i.paint(g, is_selected);
		}
	}

	class Button {
		Rect buttonBounds;
		String buttonTitle;

		static final double ROUND_RECT_ARC = 0.1;

		public Button(Rect bounds, String title) {
			buttonBounds = bounds;
			buttonTitle = title;
		}

		public void paint(Graphics g, boolean is_selected) {
			int button_color = (is_selected ? selectedColor : normalColor);
			int text_color = (is_selected ? selectedTextColor : normalTextColor);

			g.setColor(button_color);
			GraphicsToolkit.fillRectWithRoundEdge(g, buttonBounds, ROUND_RECT_ARC);

			int font_style = Font.STYLE_BOLD;
			if (is_selected && shouldUnderlineText) font_style |= Font.STYLE_UNDERLINED;

			g.setColor(text_color);
			GraphicsToolkit.drawStringInRectWithStyle(g, buttonBounds, buttonTitle, font_style, 0.78);
		}
	}
}
