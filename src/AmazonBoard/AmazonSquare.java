package AmazonBoard;

/**
 * Created by D on 2/3/2017.
 */
public class AmazonSquare {

    private int posX, posY, squareStrength = 0, pieceType;

    private int distanceQueenWhite, distanceQueenBlack, distanceKingWhite, distanceKingBlack, mobility = 0;

    private boolean captured, counted;

    public static final int PIECETYPE_AVAILABLE = 0;
    public static final int PIECETYPE_AMAZON_WHITE = 1;
    public static final int PIECETYPE_AMAZON_BLACK = 2;
    public static final int PIECETYPE_ARROW = 3;

    public static final int DISTANCE_QUEEN = 100;
    public static final int DISTANCE_KING = 1;

    /**
     * The constructor for a square on the board
     *
     * @param posX      The x position [1,10]
     * @param posY      the y position [1,10]
     * @param pieceType The type of piece (see static variables above)
     */
    public AmazonSquare(int posX, int posY, int pieceType) {

        setPosX(posX);
        setPosY(posY);
        setPieceType(pieceType);
        resetDistances();

    }

    /**
     * A copy constructor for a square on the board
     *
     * @param amazonSquare The section of the board being copied
     */
    public AmazonSquare(AmazonSquare amazonSquare) {
        posX = amazonSquare.posX;
        posY = amazonSquare.posY;
        pieceType = amazonSquare.pieceType;
        distanceQueenWhite = amazonSquare.distanceQueenWhite;
        distanceQueenBlack = amazonSquare.distanceQueenBlack;
        distanceKingWhite = amazonSquare.distanceKingWhite;
        distanceKingBlack = amazonSquare.distanceKingBlack;
        mobility = amazonSquare.mobility;

    }

    /**
     * Generates a detailed string for the particular square in the format:
     * TODO: add all the different variables to this
     *
     * @return the detailed string for the square
     */
    public String toStringDetailed() {

        return "PosX: " + posX +
                ", PosY: " + posY +
                ", PieceType: " + pieceType;

    }

    /**
     * Creates a simple string output for the position of the square:
     * (X,Y)
     *
     * @return the string for the position of the square
     */
    @Override
    public String toString() {

        return "(" + posX + ", " + posY + ")";

    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }


    public int getWhiteQueenDistance() {
        return getQueenDistance(PIECETYPE_AMAZON_WHITE);
    }

    public int getBlackQueenDistance() {
        return getQueenDistance(PIECETYPE_AMAZON_BLACK);
    }

    public int getQueenDistance(int color) {

        if (color == PIECETYPE_AMAZON_WHITE) return distanceQueenWhite;
        if (color == PIECETYPE_AMAZON_BLACK) return distanceQueenBlack;

        return Integer.MAX_VALUE;
    }

    //TODO: remove once I figure out lambdas more
    public int getWhiteKingDistance() {
        return getKingDistance(PIECETYPE_AMAZON_WHITE);
    }

    public int getBlackKingDistance() {
        return getKingDistance(PIECETYPE_AMAZON_BLACK);
    }

    public int getKingDistance(int color) {

        if (color == PIECETYPE_AMAZON_WHITE) return distanceKingWhite;
        if (color == PIECETYPE_AMAZON_BLACK) return distanceKingBlack;

        return Integer.MAX_VALUE;
    }

    public void setQueenDistance(int color, int distance) {

        if (color == PIECETYPE_AMAZON_WHITE) distanceQueenWhite = distance;
        if (color == PIECETYPE_AMAZON_BLACK) distanceQueenBlack = distance;

    }

    public void setKingDistance(int color, int distance) {

        if (color == PIECETYPE_AMAZON_WHITE) distanceKingWhite = distance;
        if (color == PIECETYPE_AMAZON_BLACK) distanceKingBlack = distance;

    }

    public int getDistance(int color, int queenOrKing) {

        if (queenOrKing == DISTANCE_QUEEN) return getQueenDistance(color);
        if (queenOrKing == DISTANCE_KING) return getKingDistance(color);

        return Integer.MAX_VALUE;
    }

    public void setDistance(int color, int distance, int queenOrKing) {

        if (queenOrKing == DISTANCE_QUEEN) setQueenDistance(color, distance);
        if (queenOrKing == DISTANCE_KING) setKingDistance(color, distance);

    }

    public void resetDistances() {

        int reset = Integer.MAX_VALUE;

        setQueenDistance(PIECETYPE_AMAZON_WHITE, reset);
        setQueenDistance(PIECETYPE_AMAZON_BLACK, reset);
        setKingDistance(PIECETYPE_AMAZON_WHITE, reset);
        setKingDistance(PIECETYPE_AMAZON_BLACK, reset);
    }

    public int getSquareStrength() {
        return squareStrength;
    }

    public void setSquareStrength(int squareStrength) {
        this.squareStrength = squareStrength;
    }

    public int getPieceType() {
        return pieceType;
    }

    public void setPieceType(int pieceType) {
        this.pieceType = pieceType;
    }

    public boolean isCaptured() {

        return (distanceQueenWhite == Integer.MAX_VALUE || distanceQueenBlack == Integer.MAX_VALUE);

    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public int getMobility() {
        return mobility;
    }

    public void setMobility(int mobility) {
        this.mobility = mobility;
    }

    @Override
    public boolean equals(Object o) {

        boolean equals = false;
        if (o instanceof AmazonSquare) {

            AmazonSquare s = (AmazonSquare) o;
            equals = posX == s.posX;
            equals &= posY == s.posY;
        }

        return equals;
    }

    @Override
    public AmazonSquare clone() {

        return new AmazonSquare(getPosX(), getPosY(), getPieceType());

    }

}