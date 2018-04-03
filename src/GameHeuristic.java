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

/**
 * Interface for specifying a heuristic to estimate game theoretic value
 * of game states.
 * 
 * @author Vincent A. Cicirello
 * @version 3.15.2018
 */
public interface GameHeuristic {
	double h(GameState s);
}