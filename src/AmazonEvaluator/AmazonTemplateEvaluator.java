package AmazonEvaluator;

import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;
import ygraphs.ai.smart_fox.games.Amazon;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by D on 3/13/2017.
 */
public class AmazonTemplateEvaluator extends AmazonEvaluator {
    @Override
    public AmazonMove evaluateBoard() {

        // All the code between the ***** must be in the evaluateBoard function or nothing will work
        //**********************************************

        double score;
        double runningBestScore = Double.MIN_VALUE;

        double moveScore;
        double runningBestMoveScore = Double.MAX_VALUE;
        AmazonSquare bestQueen = null;
        AmazonSquare bestMove = null;

        int otherPlayerColour = AmazonSquare.PIECETYPE_AMAZON_WHITE == getColor() ? 2 : 1;

        AmazonMove move = null;
        LinkedList<AmazonMove> moveStack = new LinkedList<AmazonMove>();
        AmazonBoard currentBoard = new AmazonBoard(this.board);
        AmazonIterations iterations = new AmazonIterations();
        int depth = 0;
        while (move == null && !kill) { //This is the flag for the thread. Once the timer is up, kill = true, and thread will stop
        /*

        All the code for minimax or whatever algorithm goes here

        Store the best move of all moves that have been tested in bestCurrentMove.
        When thread is stopped, the player will take the bestCurrentMove and compare it to the other evaluators.

        For each node being traversed, calculate scores via board.getBoardCalculator().calculateScore(int type).
           For the return, the score index for the player is [playerColor - 1].
           The type doesn't matter for the function of this method.

         */
            ArrayList<AmazonSquare> queenList = currentBoard.getQueenList(getColor());


            AmazonSquare[] bestPair = getBestQueenAndMove(currentBoard, getColor(), true);
            bestQueen = bestPair[0];
            bestMove = bestPair[1];

            ArrayList<AmazonSquare> bestMoveShots = currentBoard.getBoardCalculator().generateListOfValidShots(bestQueen, bestMove);

            for (AmazonSquare shot : bestMoveShots) {
                move = new AmazonMove(bestQueen, bestMove, shot);
                try {
                    currentBoard.executeMove(move);
                    moveStack.add(move);
                    score = alphaBetaMove(currentBoard, depth, Double.MIN_VALUE, Double.MAX_VALUE, true, moveStack, iterations.increment());
                    currentBoard.undoMove(move);

                    if (score > runningBestScore) {
                        AmazonSquare realQueen = board.getSquare(bestQueen.getPosX(), bestQueen.getPosY());
                        AmazonSquare realMove = board.getSquare(bestMove.getPosX(), bestMove.getPosY());
                        AmazonSquare realShot = board.getSquare(shot.getPosX(), shot.getPosY());

                        System.out.println("New score " + score + " > " + runningBestScore + " means " + move + "" +
                                "replaces " + bestCurrentMove);
                        bestCurrentMove = new AmazonMove(realQueen, realMove, realShot);
                        runningBestScore = score;
                    }
                } catch (InvalidMoveException ignored) {
                }
            }
            depth++;

            // Needs to be like this to combat concurrency exceptions
        }
        System.out.println("Iterations: " + iterations);
        return move; //doesn't do anything, as nothing needs it as a return

        //*************************************************
    }

    private double alphaBeta(AmazonSquare node, int depth, double alpha, double beta, boolean maximizingPlayer, AmazonBoard oldBoard, LinkedList<AmazonMove> moveStack, AmazonIterations iterations) {
        double v;

        long i = System.currentTimeMillis();
        ArrayList<AmazonSquare> children = oldBoard.getBoardCalculator().generateListOfValidMoves(node);
        if (depth == 0 || children.size() == 0 || kill) {
            return oldBoard.getBoardCalculator().calculateDeltaTerrainScore()[playerColor - 1];
        }

        if (maximizingPlayer) {
            v = Integer.MIN_VALUE;
            for (AmazonSquare child : children) {
                ArrayList<AmazonSquare> shots = oldBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare potentialShot : shots) {
                    if (kill)
                        return v;
                    AmazonMove move = new AmazonMove(node, child, potentialShot);
                    try {
                        oldBoard.executeMove(move);
                        moveStack.addFirst(move);
                        v = Math.max(v, alphaBeta(child, depth - 1, alpha, beta, false, oldBoard, moveStack, iterations.increment()));
                        oldBoard.undoMove(moveStack.removeFirst());
                        alpha = Math.max(v, alpha);
                    } catch (InvalidMoveException e) {
//                        e.printStackTrace();
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return v;
        } else {
            v = Integer.MAX_VALUE;
            for (AmazonSquare child : children) {
                ArrayList<AmazonSquare> shots = oldBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare potentialShot : shots) {
                    long f = System.currentTimeMillis() - i;
                    if (kill)
                        return v;
                    AmazonMove move = new AmazonMove(node, child, potentialShot);
                    try {
                        oldBoard.executeMove(move);
                        moveStack.addFirst(move);
                        v = Math.min(v, alphaBeta(child, depth - 1, alpha, beta, true, oldBoard, moveStack, iterations.increment()));
                        oldBoard.undoMove(moveStack.removeFirst());
                        beta = Math.min(v, beta);
                    } catch (InvalidMoveException e) {
//                        e.printStackTrace();
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return v;
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public double alphaBetaMove(AmazonBoard node, int depth, double alpha, double beta, boolean maximizingPlayer, LinkedList<AmazonMove> moveStack, AmazonIterations iterations) {
        double v = 0;
        int otherPlayerColour = AmazonSquare.PIECETYPE_AMAZON_WHITE == getColor() ? 2 : 1;
        int currentPlayerColour = maximizingPlayer ? getColor() : otherPlayerColour;
//        ArrayList<AmazonSquare> queenList = node.getQueenList(currentPlayerColour);
        AmazonSquare[] bestPair = getBestQueenAndMove(node, currentPlayerColour, false);
        AmazonSquare bestQueen = bestPair[0];
        AmazonSquare bestQueenMove = bestPair[1];

        if (kill || depth == 0) {
            node.getBoardCalculator().calculateBoard();
            return node.getBoardCalculator().calculateDeltaTerrainScore()[currentPlayerColour - 1];
        }

        if (maximizingPlayer) {

            ArrayList<AmazonSquare> queenShotList = node.getBoardCalculator().generateListOfValidShots(bestQueen, bestQueenMove);

            v = Double.MIN_VALUE;
            for (AmazonSquare shot : queenShotList) {
                if (kill)
                    break;
                AmazonMove move = new AmazonMove(bestQueen, bestQueenMove, shot);
                try {
                    node.executeMove(move);
                    moveStack.addFirst(move);
                    v = Math.max(v, alphaBetaMove(node, depth - 1, alpha, beta, false, moveStack, iterations.increment()));
                    alpha = Math.max(v, alpha);
                    node.undoMove(moveStack.removeFirst());

                } catch (InvalidMoveException ignored) {
                }
                if (beta <= alpha) {
//                        System.out.print(".");
                    break;
                }

            }
            return v;

        } else {

            ArrayList<AmazonSquare> queenShotList = node.getBoardCalculator().generateListOfValidShots(bestQueen, bestQueenMove);

            v = Double.MAX_VALUE;
            for (AmazonSquare shot : queenShotList) {
                if (kill)
                    break;
                AmazonMove move = new AmazonMove(bestQueen, bestQueenMove, shot);
                try {
                    node.executeMove(move);
                    moveStack.addFirst(move);
                    v = Math.min(v, alphaBetaMove(node, depth - 1, alpha, beta, true, moveStack, iterations.increment()));
                    beta = Math.min(beta, v);
                    node.undoMove(moveStack.removeFirst());
                } catch (InvalidMoveException ignored) {
                }
                if (beta <= alpha) {
//                        System.out.println(alpha  +" " + beta);
                    break;

                }
            }
            return v;
        }
    }

    private AmazonSquare[] getBestQueenAndMove(AmazonBoard currentBoard, int playerColor, boolean log) {
        double moveScore, runningBestMoveScore;
        int otherPlayerColour = playerColor == AmazonSquare.PIECETYPE_AMAZON_WHITE ? 2 : 1;
        runningBestMoveScore = Double.MAX_VALUE;
        ArrayList<AmazonSquare> queenList = currentBoard.getQueenList(playerColor);

        AmazonSquare bestQueen, bestMove;
        bestQueen = bestMove = null;

        for (int i = 0; i < queenList.size(); i++) {

            AmazonSquare queen = queenList.get(i);
            // Find the best move for this queen

            ArrayList<AmazonSquare> moveList = currentBoard.getBoardCalculator().generateListOfValidMoves(queen);
            if (moveList.size() == 0)
                continue;
//            long ini = System.currentTimeMillis();

            for (AmazonSquare tempMove : moveList) {
                currentBoard.moveAmazon(queen, tempMove);
                currentBoard.getBoardCalculator().calculateDistances();
                moveScore = currentBoard.getBoardCalculator().calculateDeltaTerrainScore()[otherPlayerColour - 1];
                currentBoard.moveAmazon(tempMove, queen);
                if (moveScore < runningBestMoveScore) {
                    if (log)
                        System.out.println(moveScore + ": New best move queen combo: " + queen + " " + tempMove + " replaces " +
                                "" + bestQueen + " " + bestMove);
                    bestMove = tempMove;
                    bestQueen = queen;
                    runningBestMoveScore = moveScore;
                }
                if (bestMove == null)
                    bestMove = tempMove;
                if (bestQueen == null)
                    bestQueen = queen;
            }

//            System.out.println("Took:" + (System.currentTimeMillis() - ini) + " milliseconds to pick best move " + bestMove);
            if (kill)
                break;

        }
        return new AmazonSquare[]{bestQueen, bestMove};
    }

}
