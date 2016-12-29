package com.game.ui;

public abstract class BoardDataProvider {
	
	public interface Move {
		public final int MOVE_RIGHT = 0;
		public final int MOVE_LEFT	= 1;
		public final int MOVE_UP	= 2;
		public final int MOVE_DOWN	= 3;
		
		public final int NUM_MOVES = 4;
	}
	
	abstract public int getNumRows();
	abstract public int getNumCols();

	/**
	 * Gets the value at the given row and column index
	 * @param row
	 * @param col
	 * @return a Cell object
	 */
	abstract public int valueAt(int row, int col);
	
	/**
	 * check the current board at that position
	 * @param row
	 * @param col
	 * @return true if not empty.
	 */
	abstract public boolean isOccupied(int row, int col);

	/**
	 * Tries to move the board if it can.
	 * Returns 0 if no movement.
	 * @param move The direction of movement.
	 * @return The number of summed tiles.
	 */
	abstract public int moveTo(int move);
	
	/**
	 * Checks to see if a move in a particular 
	 * direction creates any change.
	 * Returns true when there is a valid move
	 * 
	 * @param move The direction of movement.
	 * @return Whether there can be any movement.
	 */
	abstract public boolean canMoveTo(int move);
		
	/**
	 * clears the current state of the board to default
	 */
	abstract public void clear();
}
