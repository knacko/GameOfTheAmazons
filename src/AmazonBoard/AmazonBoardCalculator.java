package AmazonBoard;

import AmazonEvaluator.AmazonMove;
import AmazonEvaluator.InvalidMoveException;

import java.util.*;

import static AmazonBoard.AmazonBoard.*;

/**
 * Created by D on 2/14/2017.
 */
public class AmazonBoardCalculator {

    AmazonBoard board;

    public AmazonBoardCalculator(AmazonBoard board) {
        this.board = board;
    }

    /**
     * Calculates the square strength, mobility and distances on the board
     */
    public void calculateBoard() {
        generateStrengthValues();
        calculateDistances();
        generateMobilityValues();
    }

    /**
     * Checks the board to see if a player has won
     * Win condition = all squares have a distance from white or black as MAX_INT
     *
     * @return True if won, false if not
     */
    public boolean checkForWinCondition() {
        board.getBoardCalculator().calculateBoard();
        //don't iterate the outer perimeter
//        if (board.getQueenList())
        for (int x = minX + 1; x <= maxX - 1; x++)

            for (int y = minY + 1; y <= maxY - 1; y++)
                if (!getSquare(x, y).isCaptured()) {
                    return false;
                }

        return true;
    }

    /**
     * Checks all 6 possible directions of movement/shooting for potential open squares.
     * It will iterate away from the position
     *
     * @param color    equal to 1 if white, equal to 2 if black
     * @param distance How far to check away from the position (typically 10 for queen, 1 for king)
     * @return A list of available squares
     */
    private Set<AmazonSquare> generateListOfValidMoves(int color, int distance) {

        //TODO: remove assertions
        assert (color == AmazonSquare.PIECETYPE_AMAZON_WHITE || color == AmazonSquare.PIECETYPE_AMAZON_BLACK);

        Set<AmazonSquare> list = new HashSet<AmazonSquare>();

        for (AmazonSquare s : board.getQueenList(color))
            list.addAll(generateListOfValidMoves(s, distance));

        return list;

    }

    /**
     * Checks all 6 possible directions of movement/shooting for potential open squares.
     * It will iterate away from the position
     *
     * @param square The square to check
     * @return A list of available squares
     */
    public ArrayList<AmazonSquare> generateListOfValidMoves(AmazonSquare square) {

        return generateListOfValidMoves(square, maxX);
    }

    /**
     * Returns a list of valid shots from a particular position.
     * The initial position is included in the list
     *
     * @param sInit  The initial queen position
     * @param sFinal The final queen position
     * @return The list of valid shots from a position
     */
    public ArrayList<AmazonSquare> generateListOfValidShots(AmazonSquare sInit, AmazonSquare sFinal) {

        ArrayList<AmazonSquare> list = generateListOfValidMoves(sFinal, maxX);
        list.add(sInit);

        return list;
    }

    /**
     * Checks all 6 possible directions within a possible distance of movement/shooting for potential open squares
     * It will iterate away from the position
     *
     * @param square  The square to check
     * @param maxStep The max step size for movement (typically 10 for queen, 1 for king)
     * @return A list of available squares
     */
    private ArrayList<AmazonSquare> generateListOfValidMoves(AmazonSquare square, int maxStep) {

        ArrayList<AmazonSquare> list = new ArrayList<AmazonSquare>();

        for (int moveX = -1; moveX <= 1; moveX++)
            for (int moveY = -1; moveY <= 1; moveY++) {

                if (moveX == 0 && moveY == 0) continue; // skip the center square
                list.addAll(checkLineOfMoves(square.getPosX(), square.getPosY(), moveX, moveY, maxStep));

            }

        return list;
    }

    /**
     * Checks in a direction based on the increment of moveX, moveY and returns a list of available moves
     * ie. from (0,0), if moveX = 1 and moveY = 0, it will increment through (1,0) to (2,0) to (3,0), etc
     * until an invalid move is found.
     *
     * @param posX  The x-position of the square being checked
     * @param posY  The y-position of the square being checked
     * @param moveX The amount to increment X when checking
     * @param moveY The amount to increment Y when checking
     * @return A list of available moves in the form of arrays as [X,Y]
     */
    private ArrayList<AmazonSquare> checkLineOfMoves(int posX, int posY, int moveX, int moveY, int maxStep) {

        ArrayList<AmazonSquare> list = new ArrayList<AmazonSquare>();

        int n = 0;

        // System.out.println("\nChecking direction " + moveX + ", " + moveY);

        do {

            posX += moveX;
            posY += moveY;

            // System.out.println("Unit '" + getSquare(posX, posY) + "' at " + posX + ", " + posY );

            // Exit if a non-possible move if found
            if (!(getSquare(posX, posY).getPieceType() == AmazonSquare.PIECETYPE_AVAILABLE)) break;

            list.add(getSquare(posX, posY));

        } while (++n < maxStep);

        return list;
    }

    /**
     * Checks to see if a game move is valid
     *
     * @param move A game move
     * @return Boolean Whether the movement is valid
     */
    public boolean isMoveValid(AmazonMove move) throws InvalidMoveException {
/*
        AmazonSquare sInit = board.getSquare(move.getInitial().getPosX(), move.getInitial().getPosY());
        AmazonSquare sFinal = board.getSquare(move.getFinal().getPosX(), move.getFinal().getPosY());
        AmazonSquare sArrow = board.getSquare(move.getArrow().getPosX(), move.getArrow().getPosY());

        //Fail if the sInit is not one of the queens
        if (sInit.getPieceType() != AmazonSquare.PIECETYPE_AMAZON_WHITE
                && sInit.getPieceType() != AmazonSquare.PIECETYPE_AMAZON_BLACK) {
            System.out.println("Can't move " + sInit + " to " + sFinal);
            System.out.println("Exception queen list: " + board.getQueenList(0));
            System.out.println("Exception other queen list: " + board.getQueenList(1));
            throw new InvalidMoveException(move, "Initial piece is not an amazon, piece type " + sInit.getPieceType());
        }
        if (sFinal.getPieceType() != AmazonSquare.PIECETYPE_AVAILABLE)
            throw new InvalidMoveException(move, "Final position is not available.");

        if (sArrow.getPieceType() != AmazonSquare.PIECETYPE_AVAILABLE)
            if (!sArrow.equals(sInit))
                throw new InvalidMoveException(move, "Arrow position is not available.");

        //Fail is the move is not valid
        if (!isAmazonMoveValid(sInit, sFinal))
            throw new InvalidMoveException(move, "Amazon movement is not valid.");

        //Fail is the move is not valid
        if (!isShotValid(sInit, sFinal, sArrow))
            throw new InvalidMoveException(move, "Arrow shot is not valid.");*/

        return true;

    }

    /**
     * Checks to see if a move is on the list of acceptable moves
     *
     * @param sInit  The starting square
     * @param sFinal The final square
     * @return Boolean Whether the movement is valid
     */
    public boolean isAmazonMoveValid(AmazonSquare sInit, AmazonSquare sFinal) {

        ArrayList<AmazonSquare> moves = generateListOfValidMoves(sInit);

        //System.out.println(Arrays.toString(moves.toArray()));

        return moves.contains(sFinal);
    }

    /**
     * Checks to see if a move is on the list of acceptable moves
     *
     * @param sInit  The starting square
     * @param sFinal The final square
     * @return Boolean Whether the movement is valid
     */
    public boolean isShotValid(AmazonSquare sInit, AmazonSquare sFinal, AmazonSquare sArrow) {

        ArrayList<AmazonSquare> moves = generateListOfValidMoves(sFinal);
        moves.add(sInit); //need to add, as the amazon isn't removed from the board yet

        //System.out.println(Arrays.toString(moves.toArray()));

        return moves.contains(sArrow);
    }

    /**
     * Using queen movement, calculates the minimum distances between all squares and the closest amazon on a particular team
     *
     * @param color The color of player in which to calculate min distances from
     */
    public void calculateQueenDistances(int color) {
        calculateDistances(color, AmazonSquare.DISTANCE_QUEEN);
    }

    /**
     * Using king movement, calculates the minimum distances between all squares and the closest amazon on a particular team
     *
     * @param color The color of player in which to calculate min distances from
     */
    public void calculateKingDistances(int color) {
        calculateDistances(color, AmazonSquare.DISTANCE_KING);
    }

    /**
     * Calculates the minimum distances between all squares and the closest amazon on a particular team
     * TODO: Make this actually efficient
     * TODO: Use different variable for queenOrKing - is not particularly descriptive or helpful
     *
     * @param color       The color of player in which to calculate min distances from
     * @param queenOrKing The max step size for movement (10 for queen, 1 for king)
     */
    public void calculateDistances(int color, int queenOrKing) {

        //TODO: remove assertions
        assert (color == AmazonSquare.PIECETYPE_AMAZON_WHITE || color == AmazonSquare.PIECETYPE_AMAZON_BLACK);

        Set<AmazonSquare> list = new HashSet<AmazonSquare>();
        List<AmazonSquare> tempList = new ArrayList<AmazonSquare>();

        list.addAll(generateListOfValidMoves(color, queenOrKing));

        for (int n = 1; n <= (maxX * maxY); n++) { // maxX * maxY is the theoretical max moves, but will never actually be hit

            Iterator<AmazonSquare> iterator = list.iterator(); // Have to use an iterator, since you can't add/remove from a list in a for loop

            while (iterator.hasNext()) {

                AmazonSquare s = iterator.next();

                // System.out.println("Square " + s.toString() + " distance = " + s.getDistance(color, queenOrKing) + " vs " + n);

                if (s.getDistance(color, queenOrKing) > n) { //if the square has a higher distance than is being checked
                    s.setDistance(color, n, queenOrKing);    // then set to the new distance
                    tempList.addAll(generateListOfValidMoves(s, queenOrKing)); // and add all of the possible moves from the square
                }

                iterator.remove(); //remove the square that was just checked from the set
            }

            list.addAll(tempList); //add all of the new squares, if any
            tempList.clear();

            // System.out.println("Calculate Distance: List length = " + list.size());

            if (list.size() == 0) break; //once all squares are at the minimum possible distance, exit

        }
    }

    /**
     * Calculates the queen and king distances for both players
     */
    public void calculateDistances() {

        resetDistances();

        calculateQueenDistances(AmazonSquare.PIECETYPE_AMAZON_WHITE);
        calculateKingDistances(AmazonSquare.PIECETYPE_AMAZON_WHITE);

        calculateQueenDistances(AmazonSquare.PIECETYPE_AMAZON_BLACK);
        calculateKingDistances(AmazonSquare.PIECETYPE_AMAZON_BLACK);

    }

    /**
     * Will set all distances to the max value
     */
    public void resetDistances() {

        for (AmazonSquare s : board.getListOfSquares()) s.resetDistances();

    }

    /**
     * Run only after calculateDistances has been run, otherwise all squares will show as captured
     * If distance = Max_int, then a particular color isn't able to reach that square, meaning the square is
     * captured, and we don't need to do anything to it anymore
     * <p>
     * TODO: Haven't actually tested this yet
     */
    public void calculateCapturedSquares() {

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)

                if (getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_WHITE) == Integer.MAX_VALUE
                        || getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_BLACK) == Integer.MAX_VALUE)
                    getSquare(x, y).setCaptured(true);
    }

    /**
     * Iterates though all of the squares on the board to calculate the strength
     * Strength = the number of adjacent open squares
     */
    public void generateStrengthValues() {

        //don't iterate the outer perimeter
        for (int x = minX + 1; x <= maxX - 1; x++)
            for (int y = minY + 1; y <= maxY - 1; y++) {

                if (getSquare(x, y).getPieceType() != AmazonSquare.PIECETYPE_AVAILABLE)
                    getSquare(x, y).setSquareStrength(0);
                else
                    getSquare(x, y).setSquareStrength(calculateSquareStrength(getSquare(x, y)));

            }
    }

    /**
     * Calculates the strength of an individual square
     * Iterates through all adjacent squares, and increments the strength if that square is free
     *
     * @param square The square to calculate the strength of
     * @return The calculated strength value
     */
    public int calculateSquareStrength(AmazonSquare square) {

        int n = 0;

        for (int moveX = -1; moveX <= 1; moveX++)
            for (int moveY = -1; moveY <= 1; moveY++)
                if (getSquare(square.getPosX() + moveX, square.getPosY() + moveY).getPieceType() == AmazonSquare.PIECETYPE_AVAILABLE)
                    n++;

        return n;
    }

    /**
     * Calculates the total mobility of the adjacent squares
     *
     * @param square The square being evaluated
     * @return The calculated surrounding mobility value
     */
    public int calculateTotalMobility(AmazonSquare square) {
        int score = 0;
        int otherPlayerColour = 3 - square.getPieceType();
        ArrayList<AmazonSquare> moveList = generateListOfValidMoves(square);
        for (AmazonSquare move : moveList) {
            if (move.getDistance(otherPlayerColour, 1) < Integer.MAX_VALUE) {
                int distance = Math.max(
                        Math.abs(move.getPosX() - square.getPosX()),
                        Math.abs(move.getPosY() - square.getPosY()));
                score += move.getSquareStrength() / Math.pow(2, distance);
            }
        }

        return score;
    }


    public static final int MOBILITY_SCORE = 1;
    public static final int TERRAIN_SCORE = 2;
    public static final int RELATIVE_TERRAIN_SCORE = 3;
    static final int DELTA_MOBILITY_SCORE = 4;


    /**
     * Based on : https://project.dke.maastrichtuniversity.nl/games/files/msc/Hensgens_thesis.pdf
     *
     * @param type
     * @return
     */
    public int[] calculateScore(int type) {

        switch (type) {
            case TERRAIN_SCORE:
                return calculateTerrainScore();
            case RELATIVE_TERRAIN_SCORE:
                return calculateRelativeTerrainScore();
//            case DELTA_MOBILITY_SCORE:
//                return calculateDeltaTerrainScore();
            case MOBILITY_SCORE:
            default:
                return calculateMovementScore();
        }


    }


    /**
     * Calculates the overall score of the board
     * Should be called after calling getDistances methods, otherwise score will be zero
     * Score = total number of possible moves for a player
     *
     * @return An int array where a[0] = whiteScore and a[1] = blackScore
     */
    public int[] calculateMovementScore() {

        int whiteMoves = generateListOfValidMoves(AmazonSquare.PIECETYPE_AMAZON_WHITE, maxX).size();
        int blackMoves = generateListOfValidMoves(AmazonSquare.PIECETYPE_AMAZON_BLACK, maxX).size();

        return new int[]{whiteMoves, blackMoves};

    }

    /**
     * Calculates the overall score of the board
     * Should be called after calling getDistances methods, otherwise score will be zero
     * Score = the number of squares where player distance < opponent distance
     *
     * @return An int array where a[0] = whiteScore and a[1] = blackScore
     */
    public int[] calculateTerrainScore() {

        int whiteScore = 0;
        int blackScore = 0;

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++) {
                int diff = getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_WHITE) - getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_BLACK);

                if (diff < 0) whiteScore++;
                else if (diff > 0) blackScore++;
                else continue;
            }

        return new int[]{whiteScore, blackScore};

    }

    /**
     * Minimal Distance Delta Calculator
     * Calculates the difference in moves for each colour for each square and sums them
     *
     * @return Integer value for the score of the current table
     */

    public double[] calculateDeltaTerrainScore() {
        double whiteScore = 0;
        double blackScore = 0;
        double k = 1.5;

        for (int x = minX + 1; x <= maxX - 1; x++)
            for (int y = minY + 1; y <= maxY - 1; y++) {
                AmazonSquare square = getSquare(x, y);

//                int diff = getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_WHITE) - getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_BLACK);
                // Only work on open pieces
                if (square.getPieceType() != 0) {
                    continue;
                }

                int whiteDistance = square.getWhiteQueenDistance();
                int blackDistance = square.getBlackQueenDistance();

                if (whiteDistance == blackDistance) {
                    whiteScore += k;
                    blackScore += k;
                } else if (whiteDistance < blackDistance) {
                    whiteScore += 1;
//                    blackScore -= 1;
                } else if (blackDistance < whiteDistance) {
//                    whiteScore -= 1;
                    blackScore += 1;
                }
                //ignore if difference is 0
            }

        return new double[]{whiteScore, blackScore};
    }

    /**
     * Calculates the overall score of the board
     * Should be called after calling getDistances methods, otherwise score will be zero
     * Score = sum of differences of distances in all squares where player distance < opponent distance
     * TODO: int[] return is gross, should change it
     *
     * @return An int array where a[0] = whiteScore and a[1] = blackScore
     */
    public int[] calculateRelativeTerrainScore() {
        int whiteScore = 0;
        int blackScore = 0;

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++) {

                int diff = getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_WHITE) - getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_BLACK);

                if (Math.abs(diff) > AmazonBoard.maxX)
                    diff /= Math.abs(diff); //if one player can't reach the spot, give difference of 1 TODO: play with weightings of captured squares

                if (diff < 0)
                    whiteScore += -1 * diff;

                else if (diff > 0) blackScore += diff;
                //ignore if difference is 0
            }

        return new int[]{whiteScore, blackScore};
    }

    public static final double k = 1 / 5;
    public static final double f1 = 1;
    public static final double f2 = 1;
    public static final double f3 = 1;
    public static final double f4 = 1;

    public double calculateAmazonGScore(int playerColor) {

        double t1 = 0, t2 = 0, c1 = 0, c2 = 0, t = 0, w = 0, m = 0, playerDis, opponentDis;
        int opponentColor = (playerColor == AmazonSquare.PIECETYPE_AMAZON_WHITE ? AmazonSquare.PIECETYPE_AMAZON_BLACK : AmazonSquare.PIECETYPE_AMAZON_WHITE);

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++) {

                AmazonSquare s = getSquare(x, y);
                if (s.getPieceType() != AmazonSquare.PIECETYPE_AVAILABLE) continue;

                //Calculate T1
                playerDis = s.getQueenDistance(playerColor);
                opponentDis = s.getQueenDistance(opponentColor);

                if (playerDis == opponentDis && playerDis < AmazonBoard.maxX) t1 += k;
                else if (playerDis < opponentDis) t1 += 1;
                else if (playerDis > opponentDis) t1 += -1;

                //Calculate T2
                playerDis = s.getKingDistance(playerColor);
                opponentDis = s.getKingDistance(opponentColor);

                if (playerDis == opponentDis && playerDis < AmazonBoard.maxX) t2 += k;
                else if (playerDis < opponentDis) t2 += 1;
                else if (playerDis > opponentDis) t2 += -1;

                //Calculate C1
                c1 += 2 * (
                        -1 * Math.pow(2, s.getQueenDistance(playerColor)) -
                                -1 * Math.pow(2, s.getQueenDistance(opponentColor))
                );

                //Calculate C2
                c2 += Math.min(
                        1, Math.max(
                                -1, (s.getKingDistance(opponentColor) - s.getKingDistance(playerColor)) / 6d
                        )
                );

                //Calculate W
                w += Math.pow(2, Math.abs(
                        s.getKingDistance(playerColor) - s.getKingDistance(opponentColor)
                        )
                );
            }

        //Calculate t TODO: the function in the paper doesn't really make sense, if fi(w) is partitioned to 1, why bother use w?

        double f = f1 + f2 + f3 + f4;

        t = ((f1 / f) * t1) +
                ((f2 / f) * t2) +
                ((f3 / f) * c1) +
                ((f4 / f) * c2);

        //Calculate m TODO: should use w in the calculation
        for (AmazonSquare queen : board.getQueenList(opponentColor)) {
            m += queen.getMobility();
        }

        for (AmazonSquare queen : board.getQueenList(playerColor)) {
            m -= queen.getMobility();
        }

        System.out.printf("t1: %f\nt2: %f\nc1: %f\nc2: %f\nt: %f\nm: %f\nt+m: %f\n", t1, t2, c1, c2, t, m, t + m);

        return t + m;
    }


    /**
     * Calculates mobility of a queen, defined as the
     * As per An evaluation function for the game of amazons by Jens Lieberum:
     * http://ac.els-cdn.com/S0304397505005979/1-s2.0-S0304397505005979-main.pdf?_tid=d829bf2c-edc3-11e6-9fba-00000aab0f27&acdnat=1486533788_fbd052d744bf4a318972608ab142ac17
     * <p>
     * For the length of each queens move, it calculates the (square strength) / 2^(kings move distance) and sums it to get mobility
     *
     * @param amazon The queen in which to check
     * @return The mobility value
     */
    public int calculateSquareMobility(AmazonSquare amazon) {

        //assert (whitePieces.contains(amazon) || blackPieces.contains(amazon));

        double mobility = 0;
        ArrayList<AmazonSquare> list;

        for (int moveX = -1; moveX <= 1; moveX++)
            for (int moveY = -1; moveY <= 1; moveY++) {
                list = checkLineOfMoves(amazon.getPosX(), amazon.getPosY(), moveX, moveY, maxX);
                for (int i = 0; i < list.size(); i++)
                    mobility += (list.get(i).getSquareStrength() / (Math.pow(2, i)));
            }

        return (int) mobility;
    }

    /**
     * Iterates through the board and calculates the mobility scores for each square     *
     * TODO: Should combine this with the strength calculation
     */
    public void generateMobilityValues() {

        //don't iterate the outer perimeter
        for (int x = minX + 1; x <= maxX - 1; x++)
            for (int y = minY + 1; y <= maxY - 1; y++) {

                if (getSquare(x, y).getPieceType() == AmazonSquare.PIECETYPE_ARROW) //ignore arrow squares
                    getSquare(x, y).setMobility(0);
                else
                    getSquare(x, y).setMobility(calculateSquareMobility(getSquare(x, y)));

                //System.out.println("Mobility for (" + x + " , " + y + "): " + getSquare(x, y).getMobility());
            }
    }

    public AmazonSquare getSquare(int xPos, int yPos) {
        return board.getSquare(xPos, yPos);
    }

}
