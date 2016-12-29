
	/*
	static final double BOARD_P_SCALE_FACTOR 			= 0.89;		// shrink-in from the sides
	static final double BOARD_P_BOTTOM_GAP_FACTOR 		= 1.4;		// the space below the game board
	static final double BOARD_P_H_SPACE_FACTOR 		= 0.75;		// space between the score boards
	static final double BOARD_P_V_SPACE_FACTOR 		= 0.25;		// gap between the score board and game boards

	private void computePortraitFrames() {
		double width = getWidth();
		double height = getHeight();
		double board_side = width * BOARD_P_SCALE_FACTOR;

		double a = (width - board_side) / 2.0;		// space between both edges and the game board
		double b = BOARD_P_BOTTOM_GAP_FACTOR * a;		// space in the bottom
		double c = BOARD_P_H_SPACE_FACTOR * a;		// space between the two score boards
		double d = BOARD_P_V_SPACE_FACTOR * a;		// space between score boards and game board

		double score_board_width = (width - (2 * a + c)) * 0.5;
		double score_board_height = (height - (a + d + board_side + b));

		int x_first_score_board = (int)a;
		int y_first_score_board = (int)a;

		int x_second_score_board = (int)(a + score_board_width + c);
		int y_second_score_board = (int)a;

		int x_game_board = (int)a;
		int y_game_board = (int)(a + score_board_height + d);

		game_board_rect = new Rect(x_game_board, y_game_board, (int)board_side, (int)board_side);
		current_score_rect = new Rect(x_first_score_board, y_first_score_board, (int)score_board_width, (int)score_board_height);	
		high_score_rect = new Rect(x_second_score_board, y_second_score_board, (int)score_board_width, (int)score_board_height);
	}

	final double BOARD_L_SCALE_FACTOR 			= 0.79;		// shrink-in from the top
	final double BOARD_L_H_SPACE_FACTOR			= 0.11;		// space between each of the elements in the view
	final double BOARD_L_V_SPACE_FACTOR 		= 1.0/3.0;	// where the score board should go in the screen

	private void computeLandscapeFrames() {
		double width = getWidth();
		double height = getHeight();
		double board_side = height * BOARD_L_SCALE_FACTOR;

		double a = (height - board_side) * 0.5;		// the gap between the upper edge and the game board
		double b = BOARD_L_H_SPACE_FACTOR * a;		// gap between each view elements
		double c = height * BOARD_L_V_SPACE_FACTOR;	// the "big" gap in either side of the game board, above the score boards

		double score_board_width = (width - (board_side + 4 * b)) * 0.5;
		double score_board_height = height - 2.0 * c;

		int x_first_score_board = (int) b;
		int y_first_score_board = (int) c;

		int x_second_score_board = (int)(3 * b + score_board_width + board_side);
		int y_second_score_board = (int) c;

		int x_game_board = (int) (2 * b + score_board_width);
		int y_game_board = (int) (a);

		game_board_rect = new Rect(x_game_board, y_game_board, (int)board_side, (int)board_side);
		current_score_rect = new Rect(x_first_score_board, y_first_score_board, (int)score_board_width, (int)score_board_height);	
		high_score_rect = new Rect(x_second_score_board, y_second_score_board, (int)score_board_width, (int)score_board_height);
	}
	*/

