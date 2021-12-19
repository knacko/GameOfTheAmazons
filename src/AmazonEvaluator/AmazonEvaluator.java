package AmazonEvaluator;
import AmazonBoard.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by D on 2/12/2017.
 */
public abstract class AmazonEvaluator implements Runnable {

    AmazonBoard board;
    int playerColor = AmazonSquare.PIECETYPE_AMAZON_WHITE;

    public AmazonEvaluator() {}

    //****************Start of Threading Stuff

    AmazonMove bestCurrentMove;
    volatile boolean kill = false;

    /**
     * Just need to run this to evaluate the board
     */
    @Override
    public void run() {

        kill = false;
        evaluateBoard();

    }

    public AmazonMove getBestMove() {return bestCurrentMove;}

    /**
     * You must
     */
    public void stop() {kill = true;}

    //****************End of Threading Stuff


    /**
     * This function does all the magic. Must implement in while(!kill) loop
     * @return The optimal move for the board
     */
    public abstract AmazonMove evaluateBoard();

    public void loadBoard(AmazonBoard board) {
        this.board = board;
    }
    /**
     * Sets the color for the evaluator
     * TODO: should eventually be put in the constructor
     * @param color the color of the player
     */
    public void setColor(int color) {
        playerColor = color;
    }

    /**
     * Gets the color of the evaluator (player)
     * @return Color of the player
     */
    public int getColor() {
        return playerColor;
    }


    /**
     * Finds a random queen on the board
     * @param color The color to get
     * @return The square of the random queen
     */
    public AmazonSquare getRandomQueen(int color) {

        ArrayList<AmazonSquare> list = board.getQueenList(color);

        if (list.size() < 1) return null;

        return list.get((new Random()).nextInt(list.size()));

    }

    /**
     * Gets a random move (queen distance) from a square
     * @param square The square to get the move from
     * @return The randomly selected square from the list of available moves for that square
     */
    public AmazonSquare getRandomMove(AmazonSquare square) {

        ArrayList<AmazonSquare> list = board.getBoardCalculator().generateListOfValidMoves(square);

        if (list.size() < 1) return null;

        return list.get((new Random()).nextInt(list.size()));
    }

    /**
     * Gets a random shot from the final position, including the start position of the move
     * @param sInit The initial position of the queen
     * @param sFinal The square in which to check for shots from
     * @return
     */
    public AmazonSquare getRandomShot(AmazonSquare sInit, AmazonSquare sFinal) {

        System.out.println("Checking for random shot from " + sInit + " to " + sFinal);

        ArrayList<AmazonSquare> list = board.getBoardCalculator().generateListOfValidShots(sInit, sFinal);

        System.out.println("Found "+ list.size() + " shots.");

        if (list.size() < 1) return null;

        return list.get((new Random()).nextInt(list.size()));
    }



}
