# GameOfTheAmazons - An AI version of the chess variant 'Game of the Amazons'. 

This was programmed for the [COSC 322](http://www.calendar.ubc.ca/okanagan/courses.cfm?code=COSC
) (Introduction to Artificial Intelligence) at the University of British Columbia. Unfortunately, it was run on a private game server (see AmazonGameClient.java) and the source code is not available.

<img src="https://github.com/knacko/GameOfTheAmazons/blob/f94ffe94be6537fe880a346caafd99f084c136a5/GameOfTheAmazons_Game.png" width="500"/>

For full details, see the [project report](https://github.com/knacko/GameOfTheAmazons/blob/main/GameOfTheAmazons_Report.pdf). 

# Approach Used
1. A tree structure was constructed in order to store possible states of the game for evaluation

2. For short term move optimization, a Minimax algorithm used in order to pick the best possible move. This is done by maximizing your best possible move (see h, while minimizing the opponent's best move. Alpha-Beta pruning was also used in order to eliminate nodes that do not need to be evaluated due to their ranking from the heuristic value given by the score heuristic.

3. For long term move optimzation, a Monte Carlo tree search was later added. 

4. Multiple cores were used, with one performing Minimax and three performing Monte Carlo

# Scoring Heuristic
The relative terrain score was used as the heuristic function. In simple terms, the relative distance to a tile to the nearest player pawn is generated for both players (i.e. if a tile takes 2 moves for you to reach (as per Queen's distance), but the opponent takes 5, the tile has a score of (5-2) = 3 points. For every possible move a player can take, this score is generated for all tiles, and the highest score is taken as the optimal move (or lowest score, if calculated for an opponents move).

A static score is given for tiles where the relative distance is equal between players (K), and another score is give for captured tiles where one player can no longer access them (J). Weighting of this heuristic can be done by adjusting both of these scores. In general, we observed that capture score should be higher than the contested score, however, high capture values cause a hyper-defensive behavior to immediately start to enclose small areas at the risk of being enclosed by the opponent. This number could also be varied based on phases of the game.
