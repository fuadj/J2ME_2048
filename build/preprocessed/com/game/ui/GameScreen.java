package com.game.ui;

import javax.microedition.lcdui.*;
import com.game.ui.BoardDataProvider.Move;
import com.game.geometry.*;
import java.util.Vector;
import com.game.game_data.*;

public class GameScreen extends Canvas {
	public static final int GAME_OVER = 1;		// this event will be sent to the registered event listener
    
    public static final String KEY_SCORES = "GameScores"; // key for scores in UserData
    
    public static final int SAVE_LIMIT = 5;             // the number of scores saved in UserData
	
	static final int COLOR_SCREEN_BKGND				= 0xFFFBF8EF;		// the white bkground of the game

	public static final int COLOR_TEXT_BKGND_DARK 	= 0xFFFFF2E5;		// the color of a text where the bkgnd is dark
	public static final int COLOR_TEXT_BKGND_LIGHT 	= 0xFF7B7269;		// the color of a text where the bkgnd is whitish

	static final int BOARD_DIMENSION = 4;

	Display display;
	GameBoard gameBoard;

	UserData userData;  // is accessible throughout the class, should be closed at hideNotify
	Locale locale;		// is accessible throughout the class
    
	SelectionEventListener eventListener;

	DisplayBoard currentScoreBoard, highScoreBoard;

	Rect screen_rect;
	Rect game_board_rect, current_score_rect, high_score_rect;
    
	int current_score;		// the current score value
    int high_score;         // holds the current high score

	boolean is_game_over;

	public GameScreen(Display d, SelectionEventListener eventListener) {
		display = d;

		userData = UserData.getUserData();
        
        high_score = getHighScore();
		current_score = 0;

		is_game_over = false;
		this.eventListener = eventListener;

		setFullScreenMode(true);
		screen_rect = new Rect(0, 0, getWidth(), getHeight());
		computeFramesForViews();

		System.gc();		// this might be a good place to clean-up

		gameBoard = new GameBoard(new Board_2048(BOARD_DIMENSION, BOARD_DIMENSION), game_board_rect);

		locale = null;

		String current_score_title = "SCORE:";
		String best_score_title = "BEST:";
		String quit_title = "Quit";

		try {
			locale = new Locale();
			current_score_title = locale.getString("SCORE:");
			best_score_title = locale.getString("BEST:");
			quit_title = locale.getString("Quit");
		} catch (Exception e) {
		}

		currentScoreBoard = new DisplayBoard(current_score_rect, current_score_title);
		highScoreBoard = new DisplayBoard(high_score_rect, best_score_title);

		addCommand(new Command(quit_title, Command.BACK, 1));		// allow the user to go back to menu
	}

	/** 
	 * is the device dimensions suitable for portrait display.
	 * @return true if yes
	 */
	final double HEIGHT_2_WIDTH_RATIO_THRESHOLD = 1.0;
	private boolean is_portrait_mode() {
		double width = getWidth();
		double height = getHeight();

		double ratio = height / width;

		return ratio > HEIGHT_2_WIDTH_RATIO_THRESHOLD;
	}

	// these constants are for portrait
	static final double BOARD_P_SCALE_FACTOR = 0.89;
	static final double BOARD_P_BOTTOM_GAP_FACTOR = 1.4;
	static final double BOARD_P_H_SPACE_FACTOR = 0.75;
	static final double BOARD_P_V_SPACE_FACTOR = 0.25;

	// these constants are for landscape, the game will be on the left
	// and the scores will be on-top of each other on the right
	static final double BOARD_L_SCORE_H_SHRINK_FACTOR = 0.84;
	static final double BOARD_L_SCORE_H_ANGLE_FACTOR = 0.16;

	static final double BOARD_L_SCORE_W_SHRINK_FACTOR = 0.43;
	static final double BOARD_L_SPACE_SIDE_EDGE_FACTOR = 0.04;
	static final double BOARD_L_SPACE_TOP_EDGE_FACTOR = 0.10;
	static final double BOARD_L_GAP_BETWEEN_VIEWS_FACTOR = 0.54;

	private void computeFramesForViews() {
		double width = getWidth();
		double height = getHeight();

		double board_side;
		int x_game_board, y_game_board;

		double score_board_width, score_board_height;
		int x_current_score_board, y_current_score_board;
		int x_high_score_board, y_high_score_board;

		if (is_portrait_mode()) {
			board_side = width * BOARD_P_SCALE_FACTOR;

			double a = (width - board_side) / 2.0;
			double b = BOARD_P_BOTTOM_GAP_FACTOR * a;
			double c = BOARD_P_H_SPACE_FACTOR * a;
			double d = BOARD_P_V_SPACE_FACTOR * a;

			score_board_width = (width - (2 * a + c)) / 2.0;
			score_board_height = (height - (a + d + board_side + b));

			x_current_score_board = (int)a;
			y_current_score_board = (int)a;

			x_high_score_board = (int)(a + score_board_width + c);
			y_high_score_board = y_current_score_board;

			x_game_board = x_current_score_board;
			y_game_board = (int)(a + score_board_height + d);
		} else {		// landscape
			double a = width * BOARD_L_SPACE_SIDE_EDGE_FACTOR;
			double b = a * BOARD_L_GAP_BETWEEN_VIEWS_FACTOR;

			double height_2_width = height / width;

			board_side = (width - (b + 2.0 * a)) / (1.0 + BOARD_L_SCORE_W_SHRINK_FACTOR);
			
			score_board_width = board_side * BOARD_L_SCORE_W_SHRINK_FACTOR;
			score_board_height = ((height - b) / 2.0) * BOARD_L_SCORE_H_SHRINK_FACTOR * Math.cos(BOARD_L_SCORE_H_ANGLE_FACTOR * height_2_width);
			
			x_current_score_board = (int)(a + board_side + b);
			y_current_score_board = (int)((height - (2.0 * score_board_height + b)) / 2.0);

			x_high_score_board = x_current_score_board;
			y_high_score_board = (int)(y_current_score_board + score_board_height + b);

			x_game_board = (int)a;
			y_game_board = (int)((height - board_side) / 2.0);
		}

		game_board_rect = new Rect(x_game_board, y_game_board, (int)board_side, (int)board_side);
		current_score_rect = new Rect(x_current_score_board, y_current_score_board, (int)score_board_width, (int)score_board_height);	
		high_score_rect = new Rect(x_high_score_board, y_high_score_board, (int)score_board_width, (int)score_board_height);
	}
	
    /**
	 * Extract the score values from the text got with a KEY_SCORES
	 * from UserData. Should use this and not try and implement it.
     */
    private static final char SCORE_DELIMITER = ',';     // separates the scores
    public static int[] extractScores(String text) {
		text = text.trim();
		if ("".equals(text)) return new int[]{};

        Vector vec = new Vector();
        
        int prev_index = 0;
        int current_index;
        
		try {
        	while ((current_index = text.indexOf(SCORE_DELIMITER, prev_index)) != -1) {
            	int number = Integer.parseInt(text.substring(prev_index, current_index));
            	vec.addElement(new Integer(number));
            	prev_index = current_index + 1;
        	}
        
        	vec.addElement(new Integer(Integer.parseInt(text.substring(prev_index))));       // append the last one
		} catch (NumberFormatException nfe) { }
        
		if (vec.isEmpty()) return new int[]{};	// there is nothing here

        int[] numbers = new int[vec.size()];
        for (int i = 0; i < vec.size(); i++) {
            Integer val = (Integer)vec.elementAt(i);
            numbers[i] = val.intValue();
        }
        
        return numbers;
    }
    
    private int getHighScore() {
        String text = userData.getValueForKey(GameScreen.KEY_SCORES);
        
		int [] scores = extractScores(text);
		if (scores.length == 0) return 0;
        
        return scores[0];
    }
    
    public void saveScore() {
		if (current_score == 0) return;		// we won't save 0

        String text = userData.getValueForKey(GameScreen.KEY_SCORES);
        int[] scores = extractScores(text);

		if (scores.length != 0) {
			if (scores.length == SAVE_LIMIT && current_score <= scores[scores.length - 1]) {		// we won't save it
				return;
			}
		}
        
        int[] updatedScores = new int[scores.length + 1];
        
		System.arraycopy(scores, 0, updatedScores, 0, scores.length);
        updatedScores[updatedScores.length - 1] = current_score;
        
        // sort using insertion sort
        for (int i = 1; i < updatedScores.length; i++) {
            for (int j = i; j > 0; j--) {
                if (updatedScores[j - 1] < updatedScores[j]) {
                    int tmp = updatedScores[j - 1];
                    updatedScores[j - 1] = updatedScores[j];
                    updatedScores[j] = tmp;
                } else
					break;
			}
        }
        
        // have we passed out limit?
        if (updatedScores.length > SAVE_LIMIT) {
            int tmp[] = new int[SAVE_LIMIT];
			System.arraycopy(updatedScores, 0, tmp, 0, tmp.length);
            updatedScores = tmp;
        }
        
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < updatedScores.length; i++) {
            if (i != 0) sb.append(SCORE_DELIMITER);
            sb.append(updatedScores[i]);
        }
            
        userData.setValueForKey(GameScreen.KEY_SCORES, sb.toString());      // save it to UserData
        userData.flush();
    }
    
	/**
	 * A key was pressed, we take action.
	 * @param keyCode 
	 */
	protected void keyPressed(int keyCode) {
		if (keyCode == Canvas.KEY_NUM0) {		// this quits the current game
			eventListener.selectionEvent(GameScreen.GAME_OVER, null);
			return;
		}

		if (is_game_over) return;		// don't spend anymore resources, the game is over

		int action = getGameAction(keyCode);
		if (action == 0) return;		// we only need the "joystick keys"
		
		Board_2048 dataProvider = gameBoard.getDataProvider();

		int direction;
		switch (action) {
			case LEFT: direction = Move.MOVE_LEFT; break;
			case RIGHT: direction = Move.MOVE_RIGHT; break;
			case DOWN: direction = Move.MOVE_DOWN; break;
			case UP: direction = Move.MOVE_UP; break;
			default: direction = -1;
		}

		if (direction == -1) return;		// we don't want that

		if (dataProvider.canMoveTo(direction)) {
			int summed = dataProvider.moveTo(direction);
			current_score += summed;

            if (current_score > high_score)		
                high_score = current_score;

			dataProvider.addNewNumber();
            
            gameBoard.resetAnimation();		// reset it to start from 0 for the new tile
			repaint();
		}
	}
	
	protected void paint(Graphics g) {
		g.setColor(COLOR_SCREEN_BKGND);
		g.fillRect(0, 0, getWidth(), getHeight());		// color the background of all board
		currentScoreBoard.displayText(g, String.valueOf(current_score));
		highScoreBoard.displayText(g, String.valueOf(high_score));
		gameBoard.paint(g);		// draw the game board	
	}

	/**
	 * Decides whether the color is darkish or whitish
	 * @param color
	 * @return 
	 */
	private boolean isColorDark(int color) {
		int r = (0x00FF0000 & color) >> 16;
		int g = (0x0000FF00 & color) >> 8;
		int b = (0x000000FF & color);

		return b < 200;
	}

	private int textColorForBkgnd(int bkgnd_color) {
		if (isColorDark(bkgnd_color)) return COLOR_TEXT_BKGND_DARK;
		else return COLOR_TEXT_BKGND_LIGHT;
	}


	/**
	 * Can display a title string and a number as a detail.
	 */
	class DisplayBoard {
		Rect bounds;
		Rect titleBounds;
		Rect detailBounds;
		String titleString;

		final int DEFAULT_COLOR_SCORE_BOARD_BKGND = 0xFFBBADA0;

		public int boardColor;
		public int textColor;

		final double TITLE_OFFSET 		= 0.18691;
		final double TITLE_HEIGHT 		= 0.28691;
		final double DETAIL_OFFSET 		= 0.49532;
		final double DETAIL_HEIGHT 		= 0.35514;

		public DisplayBoard(Rect bounds, String title) {
			this.bounds = new Rect(bounds);
			titleString = title;
			titleBounds = bounds.scaleHeightBy(TITLE_HEIGHT);
			titleBounds.moveBy(0, (int)(bounds.height * TITLE_OFFSET));
			detailBounds = bounds.scaleHeightBy(DETAIL_HEIGHT);
			detailBounds.moveBy(0, (int)(bounds.height * DETAIL_OFFSET));

			boardColor = DEFAULT_COLOR_SCORE_BOARD_BKGND;
			textColor = textColorForBkgnd(boardColor);
		}

		void displayText(Graphics g, String text) {
			g.setClip(bounds.x, bounds.y, bounds.width, bounds.height);
			g.setColor(boardColor);
			GraphicsToolkit.fillRectWithRoundEdge(g, bounds, 3 * 0.05);

			g.setColor(textColor);
			GraphicsToolkit.drawStringWithDefaults(g, titleBounds, titleString);
			GraphicsToolkit.drawStringWithDefaults(g, detailBounds, text);
		}
	}
	

	/**
	 * Displays the game board from data obtained from Board_2048
	 */
	class GameBoard implements Runnable {
		Board_2048 dataProvider;
		Rect bounds;
		Rect insetBounds;
		
		double sectionJump;
		double cellW;
		double gap;


		static final double SCALCE_FACTOR		= 0.94;
		static final int COLOR_BOARD_BKGND		= 0xFFBBADA0;

		static final double ANIMATION_DURATION = 350;	// this is in milliseconds
		static final int ANIMATION_LIMIT = 9;		// number of times to animate
		static final int ANIMATION_INTERVAL = (int)ANIMATION_DURATION / ANIMATION_LIMIT;		// the time between each animated frame

		int animationState;		// the current level of animation, 0-indexed goes upto ANIMATION_LIMIT

		Rect game_over_bounds, restart_game_bounds;
        
		public GameBoard(Board_2048 dataProvider, Rect bounds) {
			this.dataProvider = dataProvider;
			
			this.bounds = bounds;
			insetBounds = bounds.scaleBy(SCALCE_FACTOR);
			
			sectionJump = insetBounds.width / (dataProvider.getNumCols() * 1.0f);
			
			cellW = sectionJump * SCALCE_FACTOR;
			gap = sectionJump * (1 - SCALCE_FACTOR) / 2.0;			

			animationState = 0;

			computeBoundsForSubviews();
		}

		/**
		 * Figures out where the GameOver and RestartGame Displays
		 * should go. Results will be stored in instance variables
		 * game_over_bounds and restart_game_bounds
		 */
		static final double SCREEN_GAP_FACTOR = 0.03;
		static final double P_SUB_VIEW_SCALE_FACTOR = 1.6;

		private void computeBoundsForSubviews() {
			if (is_portrait_mode()) {
				double width = getWidth();
				double height = getHeight();

				double a = height * SCREEN_GAP_FACTOR;		// gap below the bottom subview
				double b = 3.0 * a;			// gap above the top subview
				double c = 2.0 * a; 		// gap between the two subviews
				double d = b / 1.2;			// gap between side edge and top subview
				double e = 1.5 * d;			// gap between side edge and bottom subview

				double bottom_view_height = (height - (a + b + c)) / (1.0 + P_SUB_VIEW_SCALE_FACTOR);
				double top_view_height = P_SUB_VIEW_SCALE_FACTOR * bottom_view_height;

				double bottom_view_width = width - (2 * e);
				double top_view_width = width - 2 * d;

				int top_view_x = (int)d;
				int top_view_y = (int)b;

				int bottom_view_x = (int)e;
				int bottom_view_y = (int)(b + top_view_height + c);

				game_over_bounds = new Rect(top_view_x, top_view_y, (int)top_view_width, (int)top_view_height);
				restart_game_bounds = new Rect(bottom_view_x, bottom_view_y, (int)bottom_view_width, (int)bottom_view_height);
			} else {
				double height = getHeight();
				double width = getWidth();

				double width_gap = width * SCREEN_GAP_FACTOR;
				double height_gap = height * SCREEN_GAP_FACTOR;

				double sub_view_height = height - 2.0 * height_gap;
				double sub_view_width = (width - 3 * width_gap) / 2.0;

				int first_view_x = (int)width_gap;
				int first_view_y = (int)height_gap;

				int second_view_x = (int)(sub_view_width + 2.0 * width_gap);
				int second_view_y = (int)height_gap;

				game_over_bounds = new Rect(first_view_x, first_view_y, (int)sub_view_width, (int)sub_view_height);
				restart_game_bounds = new Rect(second_view_x, second_view_y, (int)sub_view_width, (int)sub_view_height);
			}
		}
		
		public Board_2048 getDataProvider() {
			return dataProvider;
		}
		
		void paint(Graphics g) {
			g.setClip(bounds.x, bounds.y, bounds.width, bounds.height);
			drawBorderWithRect(g, bounds);
			
			double animationLevel = (animationState * 1.0) / ANIMATION_LIMIT;

			Tile tile = new Tile();
			Rect tileBounds = new Rect();
			for (int row = 0; row < dataProvider.getNumRows(); row++) {
				for (int col = 0; col < dataProvider.getNumCols(); col++) {
					boundsAtSection(tileBounds, row, col);
					tile.paint(g, dataProvider, row, col, tileBounds, animationLevel);
				}
			}
            
            display.callSerially(this);		// this does the animation

			if (dataProvider.isBoardFull()) {
				if (is_game_over) return;		// we've already been here, nothing to do
				is_game_over = true;		// we set it the first time the board gets full

                saveScore();

				animationState = ANIMATION_LIMIT;		// this makes sure all the tiles are drawn with 100% opacity
				paint(g);		// repaint the last board

				int boardColor = 0xffF02009;
				int textColor = 0xffEEBB0B;

				DisplayBoard gameOverBoard = new DisplayBoard(game_over_bounds, locale.getString("Game Over"));
				DisplayBoard restartGameBoard = new DisplayBoard(restart_game_bounds, locale.getString("Wanna Restart?"));

				gameOverBoard.boardColor = restartGameBoard.boardColor = boardColor;
				gameOverBoard.textColor = restartGameBoard.textColor = textColor;

				gameOverBoard.displayText(g, String.valueOf(current_score));
				restartGameBoard.displayText(g, locale.getString("Restart Process"));
				
				Thread notifyGameOverThread = new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(4100);			// wait until the user can read the messages 
						} catch (InterruptedException ie) { }

						repaint();		// clear the "Game Over" screen
					}
				});

				notifyGameOverThread.start();
			}
		}
		
        public void resetAnimation() {
            animationState = 0;
        }
        
		/**
		 * this is called serially by the event handler, we use it to 
		 * simulate the animation, by repeatedly calling paint with different
		 * animation levels.
		 */
        public void run() {		
            if (!is_game_over && (animationState < ANIMATION_LIMIT)) {       // just incase we are off-screen
				try { Thread.sleep(ANIMATION_INTERVAL); } catch (InterruptedException ie) { }
                animationState++;
                repaint();
            }
		}

        
		/**
		 * Determines the bounds of a tile at a given
		 * row and col
		 * @param rect A reference to a Rect to be populated
		 *			by bounds of a tile.
		 * @param row
		 * @param col 
		 */
		public void boundsAtSection(Rect rect, int row, int col) {
			double x, y;
			
			x = col * sectionJump;
			y = row * sectionJump;
			
			x += gap; y += gap;
			
			rect.x = (int)x;
			rect.y = (int)y;
			
			rect.width = (int)cellW;
			rect.height = (int)cellW;
			
			rect.moveBy(insetBounds.x, insetBounds.y);
		}

		void drawBorderWithRect(Graphics g, Rect rect) {
			g.setColor(COLOR_BOARD_BKGND);
			GraphicsToolkit.fillRectWithRoundEdge(g, rect, 0.05);
		}
	}


	class Tile {
		static final int COLOR_2		= 0xFFEEE4DA;
		static final int COLOR_4		= 0xFFECE0C8;
		static final int COLOR_8		= 0xFFEFB27B;
		static final int COLOR_16		= 0xFFEC8D53;
		static final int COLOR_32		= 0xFFF57C5F;
		static final int COLOR_64		= 0xFFEC5736;
		static final int COLOR_128		= 0xFFF3D96B;
		static final int COLOR_256		= 0xFFF1D04B;
		static final int COLOR_512		= 0xFFE4C02A;
		static final int COLOR_1024		= 0xFFFFD717;
		static final int COLOR_2048		= 0xFFECC400;
		static final int COLOR_4096		= 0xFF800080;
		static final int COLOR_8192		= 0xFF904000;
		static final int COLOR_16384	= 0xFFFF0000;
		static final int COLOR_EMPTY_TILE = 0xFFCCC0B4;
		static final int COLOR_NEW_OCCUPIED = 0xFFA349A4;
        
		static final double C_R_TILE = 33.0;
		static final double C_G_TILE = 36.0;
		static final double C_B_TILE = 31.0;

		static final double C_R_TEXT = -87.0;
		static final double C_G_TEXT = -84.0;
		static final double C_B_TEXT = -80.0;

		int tileColorForAnimationLevel(int initialColor, double animationLevel) {
			int r = (initialColor >> 16) & 0xff;
			int g = (initialColor >> 8) & 0xff;
			int b = initialColor & 0xff;
			
			r += C_R_TILE * animationLevel;
			g += C_G_TILE * animationLevel;
			b += C_B_TILE * animationLevel;
			
			int result = (r << 16) & 0x00ff0000;
			result |= (g << 8) & 0x0000ff00;
			result |= b & 0x000000ff;

			return result;
		}

		int textColorForAnimationLevel(int initialColor, double animationLevel) {
			int r = (initialColor >> 16) & 0xff;
			int g = (initialColor >> 8) & 0xff;
			int b = initialColor & 0xff;
			
			r += C_R_TEXT * animationLevel;
			g += C_G_TEXT * animationLevel;
			b += C_B_TEXT * animationLevel;
			
			int result = (r << 16) & 0x00ff0000;
			result |= (g << 8) & 0x0000ff00;
			result |= b & 0x000000ff;

			return result;
		}
		
		void paint(Graphics g, Board_2048 dataSource, int row, int col, Rect inBounds, double animationLevel) {
			int color = 0;
			String contents = null;
            
			if (!dataSource.isOccupied(row, col)) {
				color = COLOR_EMPTY_TILE;
			} else {
				int value = dataSource.valueAt(row, col);
				contents = String.valueOf(value);
				
				if (!dataSource.isNewlyCreated(row, col)) {
					switch (value) {
						case 2: color = COLOR_2; break;
						case 4: color = COLOR_4; break;
						case 8: color = COLOR_8; break;
						case 16: color = COLOR_16; break;
						case 32: color = COLOR_32; break;
						case 64: color = COLOR_64; break;
						case 128: color = COLOR_128; break;
						case 256: color = COLOR_256; break;
						case 512: color = COLOR_512; break;
						case 1024: color = COLOR_1024; break;
						case 2048: color = COLOR_2048; break;
						case 4096: color = COLOR_4096; break;
						case 8192: color = COLOR_8192; break;
						case 16384:
						default: 
						color = COLOR_16384;		// if you get here, then screw it
					}
				} else {
					color = tileColorForAnimationLevel(COLOR_EMPTY_TILE, animationLevel);
				}
			}
			
			g.setColor(color);
			g.setClip(inBounds.x, inBounds.y, inBounds.width, inBounds.height);     // don't screw anything
			GraphicsToolkit.fillRectWithRoundEdge(g, inBounds, 0.12);

			if (dataSource.isOccupied(row, col)) {
				int text_color;

				if (dataSource.isNewlyCreated(row, col)) 
					text_color = textColorForAnimationLevel(COLOR_EMPTY_TILE, animationLevel);
				else 
					text_color = textColorForBkgnd(color);

				g.setColor(text_color);
				GraphicsToolkit.drawStringWithDefaults(g, inBounds, contents);
			}
		}		
	}

}
