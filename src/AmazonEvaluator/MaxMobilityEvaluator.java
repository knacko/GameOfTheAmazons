package AmazonEvaluator;

import AmazonBoard.*;

import java.util.ArrayList;

/**
 * Created by D on 2/12/2017.
 */
public class MaxMobilityEvaluator extends AmazonEvaluator implements Runnable {

    public MaxMobilityEvaluator() {}

    /**
     * Evaluates the board based on maximum mobility. It will move the queen with the highest potential mobility to that spot
     * @return
     */


    @Override
    public AmazonMove evaluateBoard() {
        this.board = board;

        AmazonMove move = null;

        while (move == null && !kill) {

            AmazonSquare sInit = null, sFinal = null, arrow;

            //Find the position with the highest mobility, and the respective queen

            ArrayList<AmazonSquare> queens = board.getQueenList(getColor());

            int highValue = 0;

            for(AmazonSquare queen : queens) {

                ArrayList<AmazonSquare> moves = board.getBoardCalculator().generateListOfValidMoves(queen);

                if (moves.size() < 1) continue; //Ignore queen if there are not valid moves

                for (AmazonSquare square : moves) {
                    //System.out.println("Mobility of " + square.toString() + ": " + square.getMobility() +" vs " + highValue);

                    if (square.getMobility() > highValue) { //Save the squares with the highest potential mobility
                        highValue = square.getMobility();
                        sFinal = square;
                        sInit = queen;
                    }
                }
            }

            System.out.println("Max mobility found for " + sInit.toString() + " at " + sFinal.toString() + " with " + sFinal.getMobility());

            arrow = getRandomShot(sInit, sFinal);

            if (arrow == null) continue;

            System.out.println("Shooting arrow to " + arrow.toString());

            move = new AmazonMove(sInit, sFinal, arrow);
            // if (!board.isMoveValid(move)) continue;

        }

        bestCurrentMove = move;

        return move;
    }
}
