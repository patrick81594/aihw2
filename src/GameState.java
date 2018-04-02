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
 * Interface for specifying a simple 2-player zero-sum deterministic game
 * of perfect information.
 * 
 * @author Vincent A. Cicirello
 * @version 3.15.2018
 */
public interface GameState {
	
	/**
	 * Checks if the state is a terminal state (i.e., game over state).
	 * @return true if terminal state, false otherwise
	 */
	boolean isTerminalState();
	
	/**
	 * Computes the value of a terminal state.  Behavior is undefined if called upon
	 * a non-terminal state.  Recommended that implementations throw an IllegalStateException
	 * if called on a non-terminal state.
	 * 
	 * @return game-theoretic value of state if the state is a terminal state.  For a simple 
	 * 		game where there are 3 possible outcomes, win, lose, or draw, then recommended
	 * 		return values are 1.0 if win for player 0, -1.0 if loss for player 0, and 0.0 for a draw.
	 * 		Critical requirement for implementations is that this method must return the value of the
	 * 		game in terms of gains for player 0 (positive value if player 0 won, negative if player 0 lost,
	 * 		and 0 if it is a draw).
	 */
	double value();
	
	/**
	 * Accesses the id of current player.
	 * 
	 * @return 0 if it is player 0's turn, and 1 if it is player 1's turn.  Player 0 is
	 * 		the player who begins the game.
	 */
	int whoseTurn();
	
	/**
	 * Generates the successors of this game state (i.e., the states that are 1 move away).
	 * 
	 * @return
	 */
	ArrayList<GameState> getSuccessors();
	
	/**
	 * Verifies if a move is legal for the current player given the game state.
	 * 
	 * @param params An array of integers specifying the move.  Length and meaning of the
	 * 			array is game dependent.
	 * 
	 * @return true if the move is legal and false otherwise
	 */
	boolean isLegalMove(int[] params);
	
	/**
	 * Makes a move in the game, updating the game state to the correct successor.
	 * 
	 * @param params An array of integers specifying the move.  Length and meaning of the
	 * 			array is game dependent.
	 * 
	 * @return true if the game state changed (i.e., if move is legal) and false otherwise.
	 */
	boolean applyMove(int[] params);
	
	/**
	 * A text string describing expected input from a human player (i.e., describing
	 * the meaning behind a list of integers indicating the move they want to make).
	 * For use in text-based console game.
	 * 
	 * @return A text string describing expected input from a human player.
	 */
	String turnInstructions();
	
	/**
	 * To support text-based console games, the toString method of the Object class
	 * should be overridden to generate a string describing the game state.
	 * 
	 * @return String describing the game state.
	 */
	String toString();
}