package AmazonEvaluator;

import AmazonBoard.*;

/**
 * Created by D on 2/12/2017.
 */
public class RandomEvaluator extends AmazonEvaluator implements Runnable {

    public RandomEvaluator() {}

    /**
     * Finds a random queen, selects a move from the list of available moves for that queen, then finds another available
     *  move from that position to shoot the arrow.
     * The function will fail if a queen is selected, but doesn't have any available moves
     * TODO: Add try/catch for handling no movement queens
     *
     * @return The randomized move
     */
    @Override
    public AmazonMove evaluateBoard() {
        this.board = board;

        AmazonMove move = null;

        while (move == null) {

            AmazonSquare sInit = getRandomQueen(playerColor);
            if (sInit == null) continue;
            System.out.println("Selecting queen at " + sInit.toString());

            AmazonSquare sFinal = getRandomMove(sInit);
            if (sFinal == null) continue;
            System.out.println("Moving queen to " + sFinal.toString());

            AmazonSquare arrow = getRandomShot(sInit, sFinal);
            if (arrow == null) continue;
            System.out.println("Shooting arrow to " + arrow.toString());

            move = new AmazonMove(sInit, sFinal, arrow);
           // if (!board.isMoveValid(move)) continue;
        }

        board.getBoardCalculator().calculateAmazonGScore(playerColor);

        bestCurrentMove = move;

        return move;
    }

}
