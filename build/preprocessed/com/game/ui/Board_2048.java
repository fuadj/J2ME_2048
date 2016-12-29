package com.game.ui;

import com.game.ui.BoardDataProvider.Move;
import java.util.Random;

public class Board_2048 extends BoardDataProvider {
	final int EMPTY = -1;		// place holder for empty cells

	Random random;
	int rows, cols;
	
	int grid[][];
	int newly_created[][];
	
	static int num_generated = 0;

	// the number with its probability of usage
	static final double generated_numbers[][] = {{2, 0.67}, {4, 0.33}};
	
	/**
	 * Initialize the board with the given number
	 * of Rows and Columns.
	 * @param r
	 * @param c 
	 */
	public Board_2048(int r, int c) {
		rows = r;
		cols = c;
		
		random = new Random(System.currentTimeMillis());
		initialize();
	}
	
	private void initialize() {
		grid = new int[rows][cols];
		newly_created = new int[rows][cols];
		fillGridWith(grid, EMPTY);
		fillGridWith(newly_created, EMPTY);
		
		this.addNewNumber();
		this.addNewNumber();
	}
	
	public void clear() {
		initialize();
	}

	private void fillGridWith(int[][] aGrid, int value) {
		for (int i = 0; i < aGrid.length; i++) {
			for (int j = 0; j < aGrid[i].length; j++) {
				aGrid[i][j] = value;
			}
		}
	}
	
	/**
	 * Copy elements from the source grid to the destination grid.
	 * The bounds of the grids should match, no checking is done!
	 * @param dstGrid
	 * @param srcGrid 
	 */
	private void copyGrid(int[][] dstGrid, int[][] srcGrid) {
		for (int i = 0; i < dstGrid.length; i++) {
			System.arraycopy(srcGrid[i], 0, dstGrid[i], 0, dstGrid[i].length);
		}
	}
	
	/**
	 * This cell was chosen randomly and contains a new value:w
	 * @param row
	 * @param col
	 * @return 
	 */
	public boolean isNewlyCreated(int row, int col) {
		return newly_created[row][col] != EMPTY;
	}

	public boolean isOccupied(int row, int col) {
		return grid[row][col] != EMPTY;
	}

	public int valueAt(int row, int col) {
		return grid[row][col];
	}
	
	public int moveTo(int move) {
		int total_moved = 0;
		
		int[][] newGrid = new int[rows][cols];
		fillGridWith(newGrid, EMPTY);		// start out empty
		
		if (move == Move.MOVE_RIGHT || move == Move.MOVE_LEFT) {
			boolean to_left = (move == Move.MOVE_LEFT);
			int dx = to_left ? 1 : -1;
			for (int row = 0; row < getNumRows(); row++) {
				
				int start_col = to_left ? 0 : (getNumCols() - 1);
				int stop_col = to_left ? getNumCols() : -1;
				
				int prev_col = start_col;
				int prev_val = EMPTY;
				
				for (int col = start_col; col != stop_col; col += dx) {
					int val = grid[row][col];
					if (val == EMPTY) continue;
					else if (prev_val == val) {		// join the two tiles
						int newVal = 2 * val;
						total_moved += newVal;
						newGrid[row][prev_col - dx] = newVal;
						prev_val = EMPTY;
					} else {		// else place the element in the tile
						newGrid[row][prev_col] = val;
						prev_val = val;
						prev_col += dx;
					}
				}
			}
		} else if (move == Move.MOVE_UP || move == Move.MOVE_DOWN) {
			boolean to_down = (move == Move.MOVE_DOWN);
			int dy = to_down ? -1 : 1;
			for (int col = 0; col < getNumCols(); col++) {
				
				int start_row = to_down ? (getNumRows() - 1) : 0;
				int stop_row = to_down ? -1 : getNumRows();
				
				int prev_row = start_row;
				int prev_val = EMPTY;
				
				for (int row = start_row; row != stop_row; row += dy) {
					int val = grid[row][col];
					if (val == EMPTY) continue;
					else if (prev_val == val) {		// join the two tiles
						int newVal = 2 * val;
						total_moved += newVal;
						newGrid[prev_row - dy][col] = newVal;
						prev_val = EMPTY;
					} else {
						newGrid[prev_row][col] = val;
						prev_val = val;
						prev_row += dy;
					}
				}
			}
		} else {
			return 0;		// wrong type of "move" was given
		}
		
		copyGrid(grid, newGrid);
		
		return total_moved;
	}
	
	public boolean canMoveTo(int move) {
		if (move == Move.MOVE_RIGHT || move == Move.MOVE_LEFT) {
			boolean to_left = (move == Move.MOVE_LEFT);
			int dx = to_left ? 1 : -1;
			for (int row = 0; row < getNumRows(); row++) {
				
				int start_col = to_left ? 0 : (getNumCols() - 1);
				int stop_col = to_left ? getNumCols() : -1;
				
				int prev_val = EMPTY;
				
				for (int col = start_col; col != stop_col; col += dx) {
					int val = grid[row][col];
					if (val == EMPTY) {
						prev_val = EMPTY;
					} else if (prev_val == val) {		// they can be joined
						return true;
					} else if (prev_val == EMPTY && (col != start_col)) {		// a tile can be moved
						return true;					 
					} else {		// a tile with a new value has come
						prev_val = val;
					}
				}
			}
			
			return false;		// have tried every row, couldn't move anything
		} else if (move == Move.MOVE_UP || move == Move.MOVE_DOWN) {
			boolean to_down = (move == Move.MOVE_DOWN);
			int dy = to_down ? -1 : 1;
			for (int col = 0; col < getNumCols(); col++) {
				
				int start_row = to_down ? (getNumRows() - 1) : 0;
				int stop_row = to_down ? -1 : getNumRows();
				
				int prev_val = EMPTY;
				
				for (int row = start_row; row != stop_row; row += dy) {
					int val = grid[row][col];
					if (val == EMPTY) {
						prev_val = EMPTY;
					} else if (prev_val == val) {		// they can be joined
						return true;
					} else if (prev_val == EMPTY && (row != start_row)) {		// a tile can be moved
						return true;
					} else {		// a tile with a new value has come
						prev_val = val;
					}
				}
			}
			
			return false;		// have tried every col, couldn't move anything
		} else {
			return false;		// wrong type of "move" was given
		}
	}
	
	public int getNumRows() {
		return rows;
	}
	
	public int getNumCols() {
		return cols;
	}
	
	public boolean isBoardFull() {
		for (int move = 0; move < Move.NUM_MOVES; move++) {
			if (canMoveTo(move)) return false;
		}
		return true;
	}
	
	/**
	 * Returns an index into generated_numbers with the given
	 * random distribution
	 */
	private int randomNumberIndexWithDistribution() {
		double r = random.nextDouble();
		
		for (int i = 0; i < generated_numbers.length; i++) {
			if (generated_numbers[i][1] >= r) return i;
			r -= generated_numbers[i][1];
		}
		
		return -1;		// this should make it crash
	}
	
	/**
	 * selected from the generated_numbers with th required
	 * random number distribution
	 * @return 
	 */
	private int getRandomNumber() {
		int index = randomNumberIndexWithDistribution();
		return (int)generated_numbers[index][0];
	}
	
	/**
	 * Adds a number to the grid at a random,
	 * empty location if the board if not currently full.
	 * @return true if a number was added
	 */
	public final boolean addNewNumber() {
		fillGridWith(newly_created, EMPTY);		// clear it, only want 1 new piece 

		int total = getNumCols() * getNumRows();
		int index = Math.abs(random.nextInt()) % total;

		int rowIndex = 0, colIndex = 0;
		for (int i = 0; i < total; i++) {
			rowIndex = index / getNumRows();
			colIndex = (index - rowIndex * getNumCols());
			
			if (grid[rowIndex][colIndex] == EMPTY) break;
			index = (index + 1) % total;		// advance by 1 to the right, go back if fall off edge
		}
		
		if (grid[rowIndex][colIndex] != EMPTY) return false;		// couldn't find any place

		grid[rowIndex][colIndex] = getRandomNumber();
		newly_created[rowIndex][colIndex] = 1;		// we've added a new one
		return true;
	}
}
