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
import java.util.Scanner;

/**
 * Simple console Reversi, also known as Othello, for use in testing
 * game search algorithm implementations.  Game is too large to search entire game tree
 * in real time during game play, even with alpha beta pruning.  Thus, limiting search
 * depth is necessary, utilizing a heuristic to estimate game theoretic values at that 
 * depth limit.  
 * 
 * @author Vincent A. Cicirello
 * @version 3.15.2018
 */
public class ReversiGame {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter 0 for simple piece count heuristic, or 1 for weighted corners heuristic");
		int which = scan.nextInt();
		scan.nextLine();
		GameLoop game = new GameLoop(new ReversiState());
		game.consolePlay(which == 0 ? 
				new ReversiState.PieceCountHeuristic() : new ReversiState.CornersHeuristic());
	}
}


/**
 * A representation of the game Reversi, also known as Othello, for use in testing
 * game search algorithm implementations.  Game is too large to search entire game tree
 * in real time during game play, even with alpha beta pruning.  Thus, limiting search
 * depth is necessary, utilizing a heuristic to estimate game theoretic values at that 
 * depth limit.
 * 
 * Two heuristics are provided as inner classes: (1) a simple heuristic that just counts pieces,
 * and (2) a heuristic that counts pieces, but adds significant extra weight for captured corners,
 * and penalizes capturing spaces adjacent to corners if corners are still open.
 * 
 * @author Vincent A. Cicirello
 * @version 3.15.2018
 */
class ReversiState implements GameState {

	private int[][] board;
	private int player;  // 0 or 1
	private boolean isTerminal;
	private boolean isTerminalCalled;
	
	/**
	 * Generates the start state for Reversi (also known as Othello).
	 */
	public ReversiState() {
		board = new int[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = -1;
			}
		}
		board[3][3] = board[4][4] = 0;
		board[3][4] = board[4][3] = 1;
		player = 0;
	}
	
	private ReversiState(ReversiState s, int row, int col) {
		board = new int[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = s.board[i][j];
			}
		}
		player = s.player;
		applyMove(new int[] {row, col});
	}
	
	@Override
	public boolean isTerminalState() {
		if (isTerminalCalled) return isTerminal;
		isTerminalCalled = true;
		return isTerminal = (!hasLegalMoves(0) && !hasLegalMoves(1));
	}
	
	private boolean isValidSpace(int row, int col, int player) {
		return board[row][col] < 0 && 
				(checkSequence(row, col, 0, 1, player) || checkSequence(row, col, 0, -1, player) ||
				checkSequence(row, col, 1, 0, player) || checkSequence(row, col, -1, 0, player) ||
				checkSequence(row, col, 1, 1, player) || checkSequence(row, col, -1, -1, player) ||
				checkSequence(row, col, 1, -1, player) || checkSequence(row, col, -1, 1, player));
	}
	
	private boolean checkSequence(int row, int col, int rInc, int cInc, int player) {
		int otherPlayer = (player==0) ? 1 : 0;
		int i = row + rInc;
		int j = col + cInc;
		boolean adjacent = false;
		while (i < 8 && j < 8 && i >= 0 && j >= 0 && board[i][j] == otherPlayer) {
			i += rInc;
			j += cInc;
			adjacent = true;
		}
		return adjacent && i < 8 && j < 8 && i >= 0 && j >= 0 && board[i][j] == player;
	}
	
	private boolean hasLegalMoves(int player) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (isValidSpace(i,j,player)) return true;
			}
		}
		return false;
	}

	@Override
	public double value() {
		if (!isTerminalState()) throw new IllegalStateException("Trying to compute value of non-terminal state.");
		int[] counts = {0, 0, 0};
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				counts[board[i][j]+1]++;
			}
		}
		if (counts[1] > counts[2]) return 1.0;
		else if (counts[1] < counts[2]) return -1.0;
		else return 0;
	}

	@Override
	public int whoseTurn() {
		return player;
	}

	@Override
	public ArrayList<GameState> getSuccessors() {
		ArrayList<GameState> successors = new ArrayList<GameState>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (isValidSpace(i,j,player)) {
					successors.add(new ReversiState(this,i,j));
				}
			}
		}
		return successors;
	}

	@Override
	public boolean isLegalMove(int[] params) {
		return isValidSpace(params[0], params[1], player);
	}

	@Override
	public boolean applyMove(int[] params) {
		if (!isLegalMove(params)) return false;
		int otherPlayer = (player==0) ? 1 : 0;
		int row = params[0];
		int col = params[1];
		
		if (col < 7 && board[row][col+1] == otherPlayer) {
			int i = col+2;
			for ( ; i < 8 && board[row][i] == otherPlayer; i++);
			if (i < 8 && board[row][i] == player) fillSequence(row, col+1, row, i, player);
		}
		if (col > 0 && board[row][col-1] == otherPlayer) {
			int i = col-2;
			for ( ; i >= 0 && board[row][i] == otherPlayer; i--);
			if (i >= 0 && board[row][i] == player) fillSequence(row, col-1, row, i, player);
		}
		if (row < 7 && board[row+1][col] == otherPlayer) {
			int i = row+2;
			for ( ; i < 8 && board[i][col] == otherPlayer; i++);
			if (i < 8 && board[i][col] == player) fillSequence(row+1, col, i, col, player);
		}
		if (row > 0 && board[row-1][col] == otherPlayer) {
			int i = row-2;
			for ( ; i >= 0 && board[i][col] == otherPlayer; i--);
			if (i >= 0 && board[i][col] == player) fillSequence(row-1, col, i, col, player);
		}
		if (row < 7 && col < 7 && board[row+1][col+1] == otherPlayer) {
			int i = row+2;
			int j = col+2;
			for ( ; i < 8 && j < 8 && board[i][j] == otherPlayer; i++, j++);
			if (i < 8 && j < 8 && board[i][j] == player) fillSequence(row+1, col+1, i, j, player);
		}
		if (row > 0 && col > 0 && board[row-1][col-1] == otherPlayer) {
			int i = row-2;
			int j = col-2;
			for ( ; i >= 0 && j >= 0 && board[i][j] == otherPlayer; i--, j--);
			if (i >= 0 && j >= 0 && board[i][j] == player) fillSequence(row-1, col-1, i, j, player);
		}
		if (row < 7 && col > 0 && board[row+1][col-1] == otherPlayer) {
			int i = row+2;
			int j = col-2;
			for ( ; i < 8 && j >= 0 && board[i][j] == otherPlayer; i++, j--);
			if (i < 8 && j >= 0 && board[i][j] == player) fillSequence(row+1, col-1, i, j, player);
		}
		if (row > 0 && col < 7 && board[row-1][col+1] == otherPlayer) {
			int i = row-2;
			int j = col+2;
			for ( ; j < 8 && i >= 0 && board[i][j] == otherPlayer; i--, j++);
			if (j < 8 && i >= 0 && board[i][j] == player) fillSequence(row-1, col+1, i, j, player);
		}
		board[row][col] = player;
		if (hasLegalMoves(otherPlayer)) {
			isTerminal = false;
			isTerminalCalled = true;
			player = otherPlayer;
		} else if (hasLegalMoves(player)) {
			isTerminal = false;
			isTerminalCalled = true;
		} else {
			player = otherPlayer;
			isTerminal = isTerminalCalled = true;
		}
		return true;
	}
	
	private void fillSequence(int r1, int c1, int r2, int c2, int player) {
		if (r1 > r2) {
			int temp = r1;
			r1 = r2;
			r2 = temp;
			temp = c1;
			c1 = c2;
			c2 = temp;
		}
		if (r1 < r2) {
			int cInc = (c1==c2) ? 0 : (c2 > c1 ? 1 : -1);
			for (int r = r1, c = c1; r <= r2; r++, c += cInc) {
				board[r][c] = player;
			}
		} else {
			if (c1 > c2) {
				int temp = r1;
				r1 = r2;
				r2 = temp;
				temp = c1;
				c1 = c2;
				c2 = temp;
			}
			if (c1 < c2) {
				int rInc = (r1==r2) ? 0 : (r2 > r1 ? 1 : -1);
				for (int c = c1, r = r1; c <= c2; c++, r += rInc) {
					board[r][c] = player;
				}
			}
		}
	}

	@Override
	public String turnInstructions() {
		return "Enter row and column separated by spaces:";
	}

	@Override
	public String toString() {
		String s = "   0 1 2 3 4 5 6 7\n   _______________\n";
		for (int i = 0; i < 8; i++) {
			s += i + " |";
			for (int j = 0; j < 8; j++) {
				if (j > 0) s += " ";
				if (board[i][j] < 0) s += "-";
				else s += board[i][j];
			}
			s += "|\n";
		}
		s += "   _______________\n";
		return s;
	}
	
	static class PieceCountHeuristic implements GameHeuristic {

		public double h(GameState s) {
			ReversiState r = (ReversiState)s;
			int[] counts = {0, 0, 0};
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					counts[r.board[i][j]+1]++;
				}
			}
			double numPieces = counts[1] + counts[2];
			return (counts[1] - counts[2]) / numPieces;
		}
		
	}
	
	static class CornersHeuristic implements GameHeuristic {

		public double h(GameState s) {
			ReversiState r = (ReversiState)s;
			int[] counts = {0, 0, 0};
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					counts[r.board[i][j]+1]++;
				}
			}
			if (r.board[0][0] >= 0) counts[r.board[0][0]+1] *= 2;
			if (r.board[0][7] >= 0) counts[r.board[0][7]+1] *= 2;
			if (r.board[7][0] >= 0) counts[r.board[7][0]+1] *= 2;
			if (r.board[7][7] >= 0) counts[r.board[7][7]+1] *= 2;
			double score0 = counts[1];
			double score1 = counts[2];
			if (r.board[0][0] < 0) {
				if (r.board[1][1] == 0) score0 *= 0.75;
				else if (r.board[1][1] == 1) score1 *= 0.75;
				if (r.board[0][1] == 0) score0 *= 0.9;
				else if (r.board[0][1] == 1) score1 *= 0.9;
				if (r.board[1][0] == 0) score0 *= 0.9;
				else if (r.board[1][0] == 1) score1 *= 0.9;
			}
			if (r.board[7][7] < 0) {
				if (r.board[6][6] == 0) score0 *= 0.75;
				else if (r.board[6][6] == 1) score1 *= 0.75;
				if (r.board[7][6] == 0) score0 *= 0.9;
				else if (r.board[7][6] == 1) score1 *= 0.9;
				if (r.board[6][7] == 0) score0 *= 0.9;
				else if (r.board[6][7] == 1) score1 *= 0.9;
			}
			if (r.board[0][7] < 0) {
				if (r.board[1][6] == 0) score0 *= 0.75;
				else if (r.board[1][6] == 1) score1 *= 0.75;
				if (r.board[0][6] == 0) score0 *= 0.9;
				else if (r.board[0][6] == 1) score1 *= 0.9;
				if (r.board[1][7] == 0) score0 *= 0.9;
				else if (r.board[1][7] == 1) score1 *= 0.9;
			}
			if (r.board[7][0] < 0) {
				if (r.board[6][1] == 0) score0 *= 0.75;
				else if (r.board[6][1] == 1) score1 *= 0.75;
				if (r.board[7][1] == 0) score0 *= 0.9;
				else if (r.board[7][1] == 1) score1 *= 0.9;
				if (r.board[6][0] == 0) score0 *= 0.9;
				else if (r.board[6][0] == 1) score1 *= 0.9;
			}
				
			double total = score0 + score1;
			return (score0 - score1) / total;
		}
		
	}
}