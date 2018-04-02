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
import java.util.StringTokenizer;

/**
 * Simple text-based console game main loop for a 2-player zero-sum deterministic game
 * of perfect information.  Allows human player to decide who goes first.
 * AI player uses alpha beta pruning to select move.
 * 
 * @author Vincent A. Cicirello
 * @version 3.15.2018
 */
public class GameLoop {

	private GameState current;
	
	/**
	 * Initializes the game.
	 * 
	 * @param startState The start state of the game.
	 */
	public GameLoop(GameState startState) {
		current = startState;
	}
	
	/**
	 * The game loop for the game, which handles all console I/O, etc.
	 * AI does complete search of game tree with alpha beta pruning to select move.
	 * Only applicable for relatively small games.
	 */
	public void consolePlay() {
		consolePlay(null);
	}
	
	/**
	 * The game loop for the game, which handles all console I/O, etc.
	 * AI does limits depth of search, using a heuristic to estimate game theoretic value 
	 * of states at that limit.  AI does use alpha beta pruning.
	 * 
	 * @param h A heuristic to estimate game theoretic value of game states.  If h is null,
	 * 				then doesn't limit by depth.
	 */
	public void consolePlay(GameHeuristic h) {
		Scanner in = new Scanner(System.in);
		System.out.println("Player 0 or 1?");
		int player = in.nextInt();
		in.nextLine();
		while (player > 1 || player < 0) {
			System.out.println(player + " is invalid response. Player 0 or 1?");
			player = in.nextInt();
			in.nextLine();
		}
		int ply = Integer.MAX_VALUE;
		if (h != null) {
			System.out.println("Enter search depth limit in number of ply:");
			ply = in.nextInt();
			in.nextLine();
		}
		while (!current.isTerminalState()) {
			System.out.println("BOARD");
			System.out.println(current);
			if (player == current.whoseTurn()) {
				// human turn
				System.out.println("Human's Turn");
				boolean hasValidMove = false;
				while (!hasValidMove) {
					hasValidMove = true;
					System.out.println(current.turnInstructions());
					String line = in.nextLine();
					StringTokenizer tokens = new StringTokenizer(line);
					int[] turnParams = new int[tokens.countTokens()];
					for (int i = 0; i < turnParams.length; i++) {
						try{
							turnParams[i] = Integer.parseInt(tokens.nextToken());
						} catch (NumberFormatException e) {
							System.out.println("Invalid move format.");
							hasValidMove = false;
						}
					}
					if (hasValidMove) {
						hasValidMove = current.isLegalMove(turnParams);
						if (!hasValidMove) {
							System.out.println("Invalid move given current board.");
						} else {
							current.applyMove(turnParams);
						}
					}
				}
			} else {
				// AI turn
				System.out.println("AI's Turn.  AI is thinking....");
				ArrayList<GameState> successors = current.getSuccessors();
				int bestMove = 0;
				double alpha = Double.NEGATIVE_INFINITY;
				double beta = Double.POSITIVE_INFINITY;
				for (int i = 0; i < successors.size(); i++) {
					double temp = (h == null) ? 
							(successors.get(i).whoseTurn() == 0 ? 
							AlphaBetaPruning.maxValue(successors.get(i), alpha, beta) : 
								AlphaBetaPruning.minValue(successors.get(i), alpha, beta))
							:
								(successors.get(i).whoseTurn() == 0 ? 
										AlphaBetaPruning.maxValue(successors.get(i), alpha, beta, ply-1, h) : 
											AlphaBetaPruning.minValue(successors.get(i), alpha, beta, ply-1, h));
					if (current.whoseTurn()==0) {
						if (temp > alpha) {
							alpha = temp;
							bestMove = i;
						}
					} else {
						if (temp < beta) {
							beta = temp;
							bestMove = i;
						}
					}
				}
				current = successors.get(bestMove);
			}
		}
		System.out.println("GAME OVER");
		System.out.println(current);
		double v = current.value();
		if (v > 0.0) {
			if (player == 0) System.out.println("Human won.  Game value = " + v);
			else System.out.println("AI won.  Game value = " + v);
		} else if (v < 0.0) {
			if (player == 1) System.out.println("Human won.  Game value = " + v);
			else System.out.println("AI won.  Game value = " + v);
		} else {
			System.out.println("Game is a draw.  Game value = " + v);
		}
		in.close();
	}
}