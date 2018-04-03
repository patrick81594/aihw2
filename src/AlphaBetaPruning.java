/* 
 * Java class structure, including method signatures, and javadoc comments, including
 * homework instructions, Copyright (C) 2018, Vincent A. Cicirello.  All rights reserved.
 * 
 * License agreement:
 * 
 * The license agreement for this source code file differs slightly from the license
 * of the other related source code files.
 * 
 * This Java source file is licensed strictly for use in Stockton University course
 * CSIS 4463, Artificial Intelligence.  Students who are or were enrolled in CSIS 4463 in 
 * the Spring 2018 semester at Stockton University may:
 * (1) use this Java source file to complete any relevant homework assignments for CSIS 4463,
 * (2) retain a private copy among course assignments indefinitely, and
 * (3) implement the functionality of the methods whose signatures have been provided.
 * 
 * Prohibitions include the following:
 * (a) You must not remove, or change, the copyright notice.
 * (b) You must not remove, or change, the license agreement.
 * (c) You must not redistribute this source file in its original form, including but not limited to
 * 		online code repositories on GitHub or other similar sites.
 * (d) You must not redistribute the completed homework assignment, including but not limited to
 * 		online code repositories on GitHub or other similar sites.
 * 
 * This license is non-transferable.  
 * 
 */

/**
 * Implementation of Alpha Beta Pruning.
 * 
 * CSIS 4463, Spring 2018, Homework 8/9:
 * 
 * (1) You are allowed to work individually or in pairs on this assignment.
 * (2) Do not change the names, parameters, or comments of this class.
 * (3) Implement the 4 methods in this class.  Read the comments carefully.
 *    (a) maxValue and minValue (the versions with 3 parameters) should implement
 *    		game search with alpha beta pruning as described in class.
 *    (b) maxValue and minValue (the versions with 5 parameters) should likewise
 *          implement game search with alpha beta pruning, but limiting the depth of the
 *          search as specified by the 4th parameter.  At that depth, the heuristic specified
 *          by the 5th parameter should be used to estimate the game theoretic value of the state.
 * (4) One slight modification must be made from the version seen in class.  In class, we assumed
 *     that the two players strictly take turns.  This is mostly true here as well.  However, one
 *     of the games provided for testing has the property that certain moves made by a player lead
 *     to a successor where that same player gets another turn.  So in your implementations below,
 *     make sure you check whose turn it is from a successor state, so that you make the correct 
 *     recursive call.  See the comments for the methods of the GameState interface, which document
 *     what those methods do.
 * (5) Don't assume that the only terminal values are 1.0, -1.0, and 0.0.  Use the appropriate method
 *     of the GameState interface to get the value of a terminal state.  For the two games provided,
 *     those are the only three values it will give, but I may test your implementation with other
 *     games that have a variety of possible terminal values.  Also, when you implement the version
 *     that limits depth, the heuristic values will vary in the interval [-1.0, 1.0] and thus
 *     are not simply limited to 1.0, -1.0, and 0.0.
 * (6) Once you implement the first two methods below, you will be able to run the main method of
 *     the TicTacToeState class to play a game of TicTacToe.  TicTacToe is small enough that the AI
 *     can use Alpha Beta Pruning to search the entire game tree in real time during game play.
 *     If it seems to get stuck thinking about what to do, then there is something wrong with
 *     your Alpha Beta Pruning implementation.  Also, if you are able to beat the AI, then you also
 *     did something wrong in your implementation of Alpha Beta Pruning.  Since the AI can, in real time,
 *     run Alpha Beta Pruning on the entire game tree, it should play perfectly (i.e., no mistakes),
 *     which means the game should always end in a draw (unless the human player makes a mistake).
 *     You might also try making a bad move on purpose (e.g., starting off with the middle of one of
 *     the sides, such as 1 0 or 1 2).  If you do that, then the AI should beat you.
 * (7) Once you implement the last 2 methods below, you will be able to run the main method of the
 *     Reversi class to play a game of Reversi.  This game is too large to search the entire game tree
 *     in real time, even with alpha beta pruning.  When you run the program, it will start by asking 
 *     which of two heuristics to use.  One of them is much better than the other.  It will also
 *     ask you how deep to search, this is in number of ply.  One ply is one player taking a turn.
 *     Try playing against your AI with different search depths, and with the two different heuristics.
 *     IMPORTANT: Take note of item (4) in this list.  Your search won't work correctly if you get that wrong
 *     since it is possible for a successor of a state of Reversi to have the same player two or more
 *     turns in a row.
 * (8) When you are done, upload and submit only the AlphaBetaPruning.java file.  It is the only of the
 *     files that you will be changing.
 *      
 * 
 * @author Student(s) name(s) go here.
 * @version Date goes here
 */
public class AlphaBetaPruning {

	/**
	 * The maxValue and minValue methods are mutually recursive, and implement
	 * alpha beta pruning game search.  maxValue is used to evaluate player 0 states,
	 * and minValue is used to evaluate player 1 states.  Player 0 is the player who starts the game. 
	 * 
	 * @param s The game state to evaluate.
	 * @param alpha The value of alpha.
	 * @param beta The value of beta.
	 * 
	 * @return The game-theoretic value of s.
	 */
	public static double maxValue(GameState s, double alpha, double beta) {
		if(s.isTerminalState()) {
			return s.value();
		}
		else {
			for(GameState sucStates: s.getSuccessors()) {
				if(sucStates.whoseTurn() == 1) {
				alpha = Math.max(alpha, minValue(sucStates, alpha, beta));
				if(alpha >= beta) {return beta;}
				}
				else {
					beta = Math.min(beta, maxValue(sucStates, alpha, beta));
					if(beta <= alpha) {return alpha;}
				}
			}
		}
		return alpha;
	}
	
	/**
	 * The maxValue and minValue methods are mutually recursive, and implement
	 * alpha beta pruning game search.  maxValue is used to evaluate player 0 states,
	 * and minValue is used to evaluate player 1 states.  Player 0 is the player who starts the game. 
	 * 
	 * @param s The game state to evaluate.
	 * @param alpha The value of alpha.
	 * @param beta The value of beta.
	 * 
	 * @return The game-theoretic value of s.
	 */
	public static double minValue(GameState s, double alpha, double beta) {
		if(s.isTerminalState()) {
			return s.value();
		}
		else {
			for(GameState sucStates: s.getSuccessors()) {
				if(sucStates.whoseTurn() == 0) {
				beta = Math.min(beta, maxValue(sucStates, alpha, beta));
				if(beta <= alpha) {return alpha;}
				}
				else {
					
				}
			}
		}
		return beta;
	}
	
	/**
	 * The maxValue and minValue methods are mutually recursive, and implement
	 * alpha beta pruning game search, limiting the search to a specified depth.
	 * For states s for which all reachable terminal states are shallower than searchDepth,
	 * the true game theoretic value is returned.  Otherwise, the game theoretic value of 
	 * states at depth searchDepth are approximated with heuristic h. 
	 * maxValue is used to evaluate player 0 states,
	 * and minValue is used to evaluate player 1 states.  Player 0 is the player who starts the game. 
	 * 
	 * @param s The game state to evaluate.
	 * @param alpha The value of alpha.
	 * @param beta The value of beta.
	 * @param searchDepth The depth to limit the search.
	 * @param h A heuristic to approximate the game-theoretic value of s.
	 * 
	 * @return The estimated game-theoretic value of s.
	 */
	public static double maxValue(GameState s, double alpha, double beta, int searchDepth, GameHeuristic h) {
		if(searchDepth == 0 || s.isTerminalState()) {
			
			return h.h(s);
		}
		else {
			for(GameState sucStates: s.getSuccessors()) {
				if(sucStates.whoseTurn() == 1) {
					alpha = Math.max(alpha, minValue(sucStates, alpha, beta, searchDepth -1, h));
					if(alpha >= beta) {return beta;}
				}
				else {
					beta = Math.min(beta, maxValue(sucStates, alpha, beta, searchDepth -1, h));
					if(beta <= alpha) {return alpha;}
				}
			}
		}
		return alpha;
	}
	
	/**
	 * The maxValue and minValue methods are mutually recursive, and implement
	 * alpha beta pruning game search, limiting the search to a specified depth.
	 * For states s for which all reachable terminal states are shallower than searchDepth,
	 * the true game theoretic value is returned.  Otherwise, the game theoretic value of 
	 * states at depth searchDepth are approximated with heuristic h. 
	 * maxValue is used to evaluate player 0 states,
	 * and minValue is used to evaluate player 1 states.  Player 0 is the player who starts the game. 
	 * 
	 * @param s The game state to evaluate.
	 * @param alpha The value of alpha.
	 * @param beta The value of beta.
	 * @param searchDepth The depth to limit the search.
	 * @param h A heuristic to approximate the game-theoretic value of s.
	 * 
	 * @return The estimated game-theoretic value of s.
	 */
	public static double minValue(GameState s, double alpha, double beta, int searchDepth, GameHeuristic h) {
		if(searchDepth == 0 || s.isTerminalState()) {
			return h.h(s);
		}
		else {
			for(GameState sucStates: s.getSuccessors()) {
				if(sucStates.whoseTurn() == 1) {
					beta = Math.min(beta, maxValue(sucStates, alpha, beta, searchDepth -1, h));
					if(beta <= alpha) {return alpha;}
				}
				else {
					alpha = Math.max(alpha, minValue(sucStates, alpha, beta, searchDepth -1, h));
					if(alpha >= beta) {return beta;}
					
				}
			}
		}
		return alpha;
	}
}