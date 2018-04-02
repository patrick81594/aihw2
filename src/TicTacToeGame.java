/* 
 * Copyright (C) 2018, Vincent A. Cicirello.  All rights reserved.
 * 
 * License agreement:
 * 
 * This Java source file is licensed strictly for use in Stockton University course
 * CSIS 4463, Artificial Intelligence.  Students who are or were enrolled in CSIS 4463 in 
 * the Spring 2018 semester at Stockton University may:
 * (1) use this Java source file to complete any relevant homework assignments for CSIS 4463, and
 * (2) retain a private copy among course assignments indefinitely.
 * 
 * Prohibitions include the following:
 * (a) You must not remove, or change, the copyright notice.
 * (b) You must not remove, or change, the license agreement.
 * (c) You must not redistribute this source file, including but not limited to
 * 		online code repositories on GitHub or other similar sites.
 * (d) You must not change the contents of this Java source file, including comments.
 * 
 * This license is non-transferable.
 * 
 */

import java.util.ArrayList;

/**
 * Simple console Tic Tac Toe game, for use in initial testing
 * of game search algorithm implementations.  Game is small enough that complete
 * DFS of search space through minimax search is possible in real time during game play.
 * AI player uses Alpha Beta Pruning to select move.
 * 
 * @author Vincent A. Cicirello
 * @version 3.15.2018
 */
public class TicTacToeGame {
	public static void main(String[] args) {
		GameLoop game = new GameLoop(new TicTacToeState());
		game.consolePlay();
	}
}

/**
 * A representation of a simple game, Tic Tac Toe, for use in initial testing
 * of game search algorithm implementations.  Game is small enough that complete
 * DFS of search space through minimax search is possible in real time during game play.
 * 
 * @author Vincent A. Cicirello
 * @version 3.15.2018
 */
class TicTacToeState implements GameState {
	
	private char[][] board;
	private int player;  // 0 for x and 1 for o
	private boolean isTerminal;
	private boolean isTerminalCalled;
	
	/**
	 * Generates the start state for TicTacToe.
	 */
	public TicTacToeState() {
		board = new char[3][3];
		player = 0;
	}
	
	private TicTacToeState(TicTacToeState s, int row, int col) {
		board = new char[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = s.board[i][j];
			}
		}
		player = s.player==0 ? 1 : 0;
		if (s.player == 0) {
			board[row][col] = 'X';
		} else {
			board[row][col] = 'O';
		}
	}

	@Override
	public boolean isTerminalState() {
		if (isTerminalCalled) return isTerminal;
		isTerminalCalled = true;
		int count = 0;
		for (int i = 0; i < 3; i++) {
			if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
				return isTerminal = true;
			}
			if (board[0][i] != 0 && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
				return isTerminal = true;
			}
			for (int j = 0; j < 3; j++) {
				if (board[i][j] != 0) count++;
			}
		}
		if (board[0][0] != 0 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
			return isTerminal = true;
		}
		if (board[0][2] != 0 && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
			return isTerminal = true;
		}
		return isTerminal = (count==9);
	}

	@Override
	public double value() {
		if (!isTerminalState()) throw new IllegalStateException("Trying to compute value of non-terminal state.");
		for (int i = 0; i < 3; i++) {
			if (board[i][0] == 'X' && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
				return 1.0;
			}
			if (board[i][0] == 'O' && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
				return -1.0;
			}
			if (board[0][i] == 'X' && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
				return 1.0;
			}
			if (board[0][i] == 'O' && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
				return -1.0;
			}
		}
		if (board[0][0] == 'X' && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
			return 1.0;
		}
		if (board[0][0] == 'O' && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
			return -1.0;
		}
		if (board[0][2] == 'X' && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
			return 1.0;
		}
		if (board[0][2] == 'O' && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
			return -1.0;
		}
		return 0;
	}

	@Override
	public int whoseTurn() {
		return player;
	}

	@Override
	public ArrayList<GameState> getSuccessors() {
		ArrayList<GameState> successors = new ArrayList<GameState>();
		if (!isTerminalState()) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (board[i][j] == 0) {
						successors.add(new TicTacToeState(this,i,j));
					}
				}
			}
		}
		return successors;
	}

	@Override
	public boolean applyMove(int[] params) {
		if (!isLegalMove(params)) return false;
		if (player == 0) {
			board[params[0]][params[1]] = 'X';
			player = 1;
		} else {
			board[params[0]][params[1]] = 'O';
			player = 0;
		}
		isTerminal = isTerminalCalled = false;
		return true;
	}

	@Override
	public boolean isLegalMove(int[] params) {
		return !isTerminalState() && board[params[0]][params[1]] == 0;
	}
	
	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < 3; i++) {
			s += board[i][0] != 0 ? board[i][0] : " ";
			for (int j = 1; j < 3; j++) {
				s += " | ";
				s += board[i][j] != 0 ? board[i][j] : " ";
			}
			s += "\n";
			if (i<2) s += "--+---+--\n";
		}
		return s;
	}

	@Override
	public String turnInstructions() {
		return "Enter row and column separated by spaces (top row is 0, left column is 0):";
	}
	
}