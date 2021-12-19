package AmazonBoard;

import AmazonEvaluator.AmazonMove;
import AmazonEvaluator.InvalidMoveException;

import java.util.*;

/**
 * Created by Drew on 2/4/2017.
 * .
 * This class represents the game board for Game of the Amazons
 * .
 * The structure uses a 12x12 array to hold individual tiles
 * The outer row/column will be all arrows to reduce complexity of checking algorithms (movement/capture)
 * .
 * Similar to the 10x12 board in chess:
 * https://chessprogramming.wikispaces.com/10x12+Board
 */
public class AmazonBoard implements Cloneable {

    //For NxM board:
    public static final int minX = 0;   // N = maxX - minX - 1
    public static final int maxX = 11;
    public static final int minY = 0;   // M = maxY - minY - 1
    public static final int maxY = 11;

    private AmazonSquare[][] board = new AmazonSquare[maxY + 1][maxX + 1];
    private ArrayList<AmazonSquare> whitePieces = new ArrayList<AmazonSquare>();
    private ArrayList<AmazonSquare> blackPieces = new ArrayList<AmazonSquare>();
    private ArrayList<AmazonSquare> boardSquares = new ArrayList<AmazonSquare>();

    AmazonBoardCalculator boardCalculator;
    double[][] score;

    /**
     * Create the game board object, and set the initial positions of all the amazons
     */
    public AmazonBoard() {

        resetBoard();
        boardCalculator = new AmazonBoardCalculator(this);
        boardCalculator.calculateBoard();
    }

    public AmazonBoard(AmazonBoard amazonBoard) {
        whitePieces = new ArrayList<AmazonSquare>();
        blackPieces = new ArrayList<AmazonSquare>();
        for (int i = 0; i < amazonBoard.board.length; i++) {
            for (int j = 0; j < amazonBoard.board[i].length; j++) {
                AmazonSquare square = new AmazonSquare(amazonBoard.board[i][j]);
                if (square.getPieceType() == AmazonSquare.PIECETYPE_AMAZON_WHITE)
                    whitePieces.add(square);
                else if (square.getPieceType() == AmazonSquare.PIECETYPE_AMAZON_BLACK)
                    blackPieces.add(square);
                this.board[i][j] = square;
            }
        }
        this.boardSquares = new ArrayList<AmazonSquare>(amazonBoard.boardSquares);
        boardCalculator = new AmazonBoardCalculator(this);
//        boardCalculator.calculateBoard();
    }

    /**
     * Resets the board to the initial state
     */
    public void resetBoard() {

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {

                if (x == minX || x == maxX || y == minY || y == maxY)
                    board[y][x] = new AmazonSquare(x, y, AmazonSquare.PIECETYPE_ARROW);
                else board[y][x] = new AmazonSquare(x, y, AmazonSquare.PIECETYPE_AVAILABLE);

            }
        }

        //TODO: change the positions to scale with the board
        whitePieces.add(setSquare(minX + 1, minY + 4, AmazonSquare.PIECETYPE_AMAZON_WHITE));
        whitePieces.add(setSquare(minX + 4, minY + 1, AmazonSquare.PIECETYPE_AMAZON_WHITE));
        whitePieces.add(setSquare(maxX - 4, minY + 1, AmazonSquare.PIECETYPE_AMAZON_WHITE));
        whitePieces.add(setSquare(maxX - 1, minY + 4, AmazonSquare.PIECETYPE_AMAZON_WHITE));

        blackPieces.add(setSquare(minX + 1, maxY - 4, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(minX + 4, maxY - 1, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(maxX - 4, maxY - 1, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(maxX - 1, maxY - 4, AmazonSquare.PIECETYPE_AMAZON_BLACK));

    }

    /**
     * Gets a list of all the squares in the game.
     * Probably should just use this for testing
     * <p>
     * TODO: Order this based on the ASCII output of board
     *
     * @return ArrayList of AmazonSquares
     */
    public ArrayList<AmazonSquare> getListOfSquares() {

        if (boardSquares.size() != 0) return boardSquares; //hacky singleton

        ArrayList<AmazonSquare> list = new ArrayList<AmazonSquare>();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                list.add(getSquare(x, y));

        return list;
    }

    /**
     * Get the list of queens
     *
     * @param color The color of which to retrieve the queens for
     * @return The list of queen squares for a particular color
     */
    public ArrayList<AmazonSquare> getQueenList(int color) {
        return (color == AmazonSquare.PIECETYPE_AMAZON_WHITE ? whitePieces : blackPieces);
    }

    /**
     * Returns the contents of a particular square
     * Orients from the bottom left, with a range of [1,10]
     *
     * @param xPos The x positions of the square to get
     * @param yPos The y position of the square to get
     * @return The string representing the selected position
     */
    public AmazonSquare getSquare(int xPos, int yPos) {

        //TODO: remove assertions
        assert xPos >= minX;
        assert xPos <= maxX;
        assert yPos >= minY;
        assert yPos <= maxY;

        return board[yPos][xPos];
    }

    /**
     * Sets the type of square at a particular location
     * Should only use this directly when setting positions on the board
     *
     * @param xPos      The x position of the square to set
     * @param yPos      The y position of the square to set
     * @param pieceType 0 - available, 1 - white, 2 - black, 3 - arrow
     * @return The square that had been set
     */
    private AmazonSquare setSquare(int xPos, int yPos, int pieceType) {

        getSquare(xPos, yPos).setPieceType(pieceType);
        return getSquare(xPos, yPos);
    }

    /**
     * Executes a game move, must be valid, or bad things happen
     *
     * @param move The move to be executed
     */
    public void executeMove(AmazonMove move) throws InvalidMoveException {

        //TODO: do something with this in UI. Maybe have a button to allow the move after displaying the potential move on the board

        boardCalculator.isMoveValid(move); //will throw exception if the move is not valid

        moveAmazon(move.getInitial(), move.getFinal());
        shootArrow(move.getFinal(), move.getArrow());
        boardCalculator.calculateBoard();
    }

    /**
     * Undoes a previous move on the board
     * Caution: Does not do any error checking, as the move should be valid, so the undo move should also be valid.
     *
     * @param move The move to undo
     */
    public void undoMove(AmazonMove move) { //throws InvalidUndoException {
/*
        if (move.getInitial().getPieceType() != AmazonSquare.PIECETYPE_AVAILABLE)
            throw new InvalidUndoException("The initial square is not available.");

        if (move.getFinal().getPieceType() != AmazonSquare.PIECETYPE_AMAZON_WHITE || move.getFinal().getPieceType() != AmazonSquare.PIECETYPE_AMAZON_BLACK)
            throw new InvalidUndoException("The final square is not an amazon.");

        if (move.getArrow().getPieceType() != AmazonSquare.PIECETYPE_ARROW)
            throw new InvalidUndoException("The arrow square is not an arrow.");*/
        if (move.getFinal().getPieceType() == AmazonSquare.PIECETYPE_AMAZON_WHITE) {
            whitePieces.remove(move.getFinal());
            whitePieces.add(move.getInitial());
        } else {
            blackPieces.remove(move.getFinal());
            blackPieces.add(move.getInitial());
        }
        if (move.getInitial() == move.getArrow()) {
            // After moving, the arrow was fired into the initial spot
            move.getInitial().setPieceType(move.getFinal().getPieceType());
            move.getFinal().setPieceType(AmazonSquare.PIECETYPE_AVAILABLE);
        } else {
            move.getInitial().setPieceType(move.getFinal().getPieceType());
            move.getArrow().setPieceType(AmazonSquare.PIECETYPE_AVAILABLE);
            move.getFinal().setPieceType(AmazonSquare.PIECETYPE_AVAILABLE);
        }
        boardCalculator.calculateBoard();
    }

    /**
     * Moves a queen from a particular space to another
     * TODO: move to GameAction class
     *
     * @param sInit  The initial position
     * @param sFinal The final position
     * @return Whether the move was successful
     */
    public boolean moveAmazon(AmazonSquare sInit, AmazonSquare sFinal) {

        switch (sInit.getPieceType()) {
            case AmazonSquare.PIECETYPE_AMAZON_WHITE:
                whitePieces.remove(sInit);
                whitePieces.add(sFinal);
                break;
            case AmazonSquare.PIECETYPE_AMAZON_BLACK:
                blackPieces.remove(sInit);
                blackPieces.add(sFinal);
                break;
        }

        sFinal.setPieceType(sInit.getPieceType());
        sInit.setPieceType(AmazonSquare.PIECETYPE_AVAILABLE);

        return true;
    }

    /**
     * Test function, don't use in game - forces an arrow into a square
     *
     * @param arrow The
     */
    public void forceArrow(AmazonSquare arrow) {

        if (arrow.getPieceType() == AmazonSquare.PIECETYPE_AVAILABLE) {
            arrow.setPieceType(AmazonSquare.PIECETYPE_ARROW);
            boardCalculator.calculateBoard();
        }
    }

    /**
     * Shoots an arrow from a particular space to another
     * TODO: move to GameAction class
     *
     * @param amazon The square with the amazon
     * @param arrow  The square with the arrow
     * @return Whether the shot was successful
     */

    public boolean shootArrow(AmazonSquare amazon, AmazonSquare arrow) {

        arrow.setPieceType(AmazonSquare.PIECETYPE_ARROW);

        return true;
    }

    /**
     * Creates a ASCII representation of the piece types on the board
     * X = Arrow
     * O = White
     * * = Black
     * '  ' = Available
     *
     * @return The string representation of the piece types on the board
     */
    public String getPieceString() {
        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                switch (getSquare(x, y).getPieceType()) {
                    case AmazonSquare.PIECETYPE_AVAILABLE:
                        s += " ";
                        break;
                    case AmazonSquare.PIECETYPE_ARROW:
                        s += "X";
                        break;
                    case AmazonSquare.PIECETYPE_AMAZON_BLACK:
                        s += "*";
                        break;
                    case AmazonSquare.PIECETYPE_AMAZON_WHITE:
                        s += "O";
                        break;
                    default:
                        s += "E";
                }
            }
            s += "\n";
        }
        return s;
    }

    /**
     * Creates a numerical version of the board with each square listing its strength value
     *
     * @return The string representation of the strength values on the board
     */
    public String getStrengthString() {

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++)
                s += (getSquare(x, y).getSquareStrength());
            s += "\n";
        }

        return s;

    }

    /**
     * Creates a numerical version of the board with each square listing it's distance to a color
     * Will only display a max of 9 distance
     * <p>
     * Shows an X for any unavailable spaces
     * <p>
     * TODO: Implement an extended hex to calculate > 9, if necessary
     *
     * @param color The color of player in which to calculate for
     * @return The string representation of the distance value on the board
     */
    public String getQueenDistanceString(int color) {

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                switch (getSquare(x, y).getPieceType()) {
                    case AmazonSquare.PIECETYPE_AVAILABLE:
                        int distance = getSquare(x, y).getQueenDistance(color);
                        s += (Integer.toHexString(Math.min(15, distance))).toUpperCase();
                        break;
                    default:
                        s += "X";
                        break;
                }
            }
            s += "\n";
        }

        return s;
    }

    /**
     * Creates a numerical version of the board with each square listing it's distance to a color
     * Will only display a max of 9 distance
     * <p>
     * Shows an X for any unavailable spaces
     * <p>
     * TODO: Implement an extended hex to calculate > 9, if necessary
     * TODO: should be combined somehow with the queen distance string method to avoid code duplication
     *
     * @param color The color of player in which to calculate for
     * @return The string representation of the distance value on the board
     */
    public String getKingDistanceString(int color) {

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                switch (getSquare(x, y).getPieceType()) {
                    case AmazonSquare.PIECETYPE_AVAILABLE:
                        int distance = getSquare(x, y).getKingDistance(color);
                        s += (Integer.toHexString(Math.min(15, distance))).toUpperCase();
                        break;
                    default:
                        s += "X";
                        break;
                }
            }
            s += "\n";
        }

        return s;
    }

    /**
     * Creates a Hex version of the mobility value, scaled based on the max mobility value: (value/maxvalue)*15
     * TODO: Create an extended hex system to have a better range
     *
     * @return The ASCII representation of the mobility values for each square
     */
    public String getMobilityString() {

        List<AmazonSquare> list = getListOfSquares();
        int max = 0;

        for (AmazonSquare s : list) if (s.getMobility() > max) max = s.getMobility();

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                double val = ((double) getSquare(x, y).getMobility() / (max + 1)) * 16; //put max+1 to avoid divide by zero error
                s += Integer.toHexString(Math.min((int) val, 15)).toUpperCase();
            }
            s += "\n";
        }

        return s;

    }

    /**
     * Prints a ASCII version of the board to the console
     */
    @Override
    public String toString() {

        String[] pieceTypes = getPieceString().split("\n");
        String[] strengthValues = getStrengthString().split("\n");
        String[] whiteQueenDistance = getQueenDistanceString(AmazonSquare.PIECETYPE_AMAZON_WHITE).split("\n");
        String[] blackQueenDistance = getQueenDistanceString(AmazonSquare.PIECETYPE_AMAZON_BLACK).split("\n");
        String[] whiteKingDistance = getKingDistanceString(AmazonSquare.PIECETYPE_AMAZON_WHITE).split("\n");
        String[] blackKingDistance = getKingDistanceString(AmazonSquare.PIECETYPE_AMAZON_BLACK).split("\n");
        String[] mobilityValue = getMobilityString().split("\n");

        String s = "Piece types:   Strength:      Mobility:      White Q Dis:   Black Q Dis:   White K Dis:   Black K Dis:   " + "\n";

        for (int i = 0; i <= maxY; i++)
            s += (pieceTypes[i] + "   " +
                    strengthValues[i] + "   " +
                    mobilityValue[i] + "   " +
                    whiteQueenDistance[i] + "   " +
                    blackQueenDistance[i] + "   " +
                    whiteKingDistance[i] + "   " +
                    blackKingDistance[i] + "\n"
            );

        return s;

    }

    public AmazonBoardCalculator getBoardCalculator() {
        return boardCalculator;
    }


}