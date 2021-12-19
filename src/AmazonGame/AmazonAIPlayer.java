package AmazonGame;

import AmazonBoard.AmazonBoardCalculator;
import AmazonBoard.AmazonSquare;
import AmazonEvaluator.*;
import ygraphs.ai.smart_fox.GameMessage;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by D on 1/26/2017.
 */
public class AmazonAIPlayer extends AmazonPlayer {

    int gameMoveTime; //Max length of move in seconds
    long turnStartTime;
    long turnEndTime;

    AmazonEvaluator[] evaluators;
    double[] weightMatrix;
    int playerColor = AmazonSquare.PIECETYPE_AMAZON_WHITE;

    /**
     *
     * @param name - Player name
     * @param password - Player password (unused by server)
     * @param evaluators - The list of evaluators
     * @param weightMatrix - The weight matrix for selecting the evaluators, needs to be in same order as evaluators
     */
    public AmazonAIPlayer(String name, String password, AmazonEvaluator[] evaluators, double[] weightMatrix) {

        super(name, password);
        this.evaluators = evaluators;

        //If the weight matrix doesn't match the evaluators length, ignore and fill with equal chance
        //TODO: should normalize array
        if (weightMatrix.length != evaluators.length) Arrays.fill(weightMatrix, 1 / evaluators.length);
        else this.weightMatrix = weightMatrix;

        this.turnStartTime = 0;
        this.turnEndTime = 0;
        this.gameMoveTime = 10;

        amazonUI.setTitle(amazonUI.getTitle() + ", Type: " + getAIType());

    }

    /**
     * Same constructor, but will take only 1 evaluator as the input
     *
     * @param name
     * @param password
     * @param evaluator
     */
    public AmazonAIPlayer(String name, String password, AmazonEvaluator evaluator) {

        super(name, password);

        //Create a single item array for evaluators and weight matrix
        AmazonEvaluator[] evals = new AmazonEvaluator[1];
        evals[0] = evaluator;
        this.evaluators = evals;
        weightMatrix = new double[]{1.0};

        this.turnStartTime = 0;
        this.turnEndTime = 0;
        this.gameMoveTime = 10;

        amazonUI.setTitle(amazonUI.getTitle() + ", Type: " + getAIType());

    }


    /**
     * Responds to the messages sent by the server.
     * This class is only concerned with the game start and the move messages
     *
     * @param messageType The string of the message, from the GameClient class
     * @param msgDetails  The data contained in the string
     * @return Not sure, probably intended to say whether the event was consumed
     */
    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {

        //TODO: total turn time is 30s, but timer doesn't actually start until takeTurn() is called. Should figure out how long it takes to do the checking for game moves

        System.out.println("Got message: " + messageType);

        if (messageType.equals(GameMessage.GAME_ACTION_START)) {

            //Set the evaluator to color, and execute first move if white
            if (msgDetails.get("player-black").equals(this.userName())) {
                playerColor = AmazonSquare.PIECETYPE_AMAZON_BLACK;
                for (AmazonEvaluator e : evaluators) e.setColor(playerColor);
                amazonUI.setTitle(amazonUI.getTitle() + ", Black Player");
            } else {
                System.out.println("Is first player, finding move.");
                playerColor = AmazonSquare.PIECETYPE_AMAZON_WHITE;
                for (AmazonEvaluator e : evaluators) e.setColor(playerColor);
                amazonUI.setTitle(amazonUI.getTitle() + ", White Player");
                takeTurn(); //This is the first move of the game
            }

        } else if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {

            respondToMove(msgDetails);
//            if (checkForWinCondition()) return true;
            takeTurn();
            // if (checkForWinCondition()) return true;

        } else if (messageType.equals(GameMessage.GAME_STATE_PLAYER_LOST)) {

            System.out.println("Other player has conceded. Terminating Client");
            gameClient.logout();
            return true;

        }

        return true;

    }

    /**
     * Checks the board to see if a player has won, then logs out.
     * TODO: Need to change this based on the procedures for the competition
     * TODO: Kinda gross how it handles everything, should put the log out stuff on a button
     *
     * @return true for win, false for not
     */
    private boolean checkForWinCondition() {
        /*
        if (board.getBoardCalculator().checkForWinCondition()) {
            System.out.println(board);
            endGame();
            return true;
        }
        */

        return false;
    }

    //TODO: fix all of this, will not show who won, due to the evaluators names not being easily accessible
    public boolean endGame() {
        int[] score = board.getBoardCalculator().calculateScore(AmazonBoardCalculator.RELATIVE_TERRAIN_SCORE);

        System.out.println("No more valid moves remain.");
        System.out.println("Final score: White - " + score[0] + ", Black - " + score[1]);

        // boolean didIWin = score[evaluator.getColor() - 1] > score[Math.abs((evaluator.getColor() - 1) - 1)];

        // if (didIWin) System.out.println(evaluator.getClass().getSimpleName() + " wins.");
        // else System.out.println(evaluator.getClass().getSimpleName() + " lost.");

        System.out.println("Terminating client");
        gameClient.logout();

        return false;//didIWin;

    }

    /**
     * Take the move data and applies it to the board
     *
     * @param msgDetails The data taken from the move event sent from the server
     */

    private void respondToMove(Map<String, Object> msgDetails) {
        AmazonMove gotMove = generateMoveFromMsg(msgDetails);
        System.out.println(System.currentTimeMillis() + ": Got move: " + gotMove.toString());

        try {
            if (gotMove.getInitial().getPieceType() == playerColor)
                throw new InvalidMoveException(gotMove, "Opponent has selected your pawn");

            board.executeMove(gotMove);

        } catch (InvalidMoveException e) {
            //TODO: implement error handling if the other player sends a faulty move
            e.printStackTrace();
            return;
        }

        amazonUI.repaint();
        moveHistory.add(gotMove);
    }

    /**
     * Starts the timer, and iterates through all of the evaluators, loading the board and starting the evaluation function
     */

    private void takeTurn() {

        this.turnStartTime = System.currentTimeMillis();
        System.out.println(turnStartTime / 1000);

        Executors.newSingleThreadScheduledExecutor().schedule(
                this::sendMove,
                gameMoveTime,
                TimeUnit.SECONDS);

        for (AmazonEvaluator e : evaluators) {
            e.loadBoard(board);
            e.run();
        }

    }

    /**
     * Gets the best move from all of the evaluators, and sends it to the other player
     */
    private void sendMove() {

        System.out.println("Time for move has elapsed, getting final move");
        ArrayList<AmazonMove> bestMoves = new ArrayList<AmazonMove>();
//        System.out.println("Board before evaluating: " + board);

        //Iterates through all of the evaluators, stops them, and get the best move from all of them
        for (AmazonEvaluator e : evaluators) {

            e.stop();
            while(e.getBestMove() == null)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            bestMoves.add(e.getBestMove());
            System.out.println("Best move from " + e.getClass().getSimpleName() + ": " + e.getBestMove().toString() + " piece type: " + e.getBestMove().getInitial().getPieceType());

        }

        //TODO: Put some logic here, right now just selects from the weight matrix

        double random = new Random().nextDouble();

        AmazonMove bestMove = evaluators[0].getBestMove();

        for (int i = 0; i < weightMatrix.length; i++) {

            if (random < weightMatrix[i]) {
                bestMove = evaluators[i].getBestMove();
                break;
            }

            random -= weightMatrix[i];
        }

        this.turnStartTime = 0;
        this.turnEndTime = System.currentTimeMillis();

        try {
            board.executeMove(bestMove);
        } catch (InvalidMoveException e) {
            e.printStackTrace();
            return;
        }

        System.out.println(System.currentTimeMillis() + ": Sending move: " + bestMove.toString());
        moveHistory.add(bestMove);
        amazonUI.repaint();
        gameClient.sendMoveMessage(bestMove);
    }

    /**
     * Run this method twice to create two instances of players
     * If run without command line arguments, will default to the random AI
     * For command line arguments, look at the order of evaluators. On the command line, specify the index value for
     * the AI you wish to use.
     *
     * @param args int value of the evaluator you want to use
     */
    public static void main(String[] args) {

        String uuid = UUID.randomUUID().toString().substring(0, 10);

        //TODO: have the list of acceptable evaluators generated dynamically
//        AmazonEvaluator[] evaluators = {new RandomEvaluator(), new MaxMobilityEvaluator(), new BestMobilityEvaluator()};
        AmazonEvaluator[] evaluators = {new NodeMinimaxEvaluator()};
        int evaluator = 0; //Default is the random evaluator

        if (args.length != 0) evaluator = Integer.parseInt(args[0]);

        //TODO: replace this with a window that will allow you to select a different player
        AmazonAIPlayer p1 = new AmazonAIPlayer(uuid, uuid, evaluators[evaluator]);
        //AmazonAIPlayer p2 = new AmazonAIPlayer(uuid + "2", uuid + "2", evaluators, new double[] {0.1,0.3,0.6});
        //AmazonAIPlayer p3 = new AmazonAIPlayer(uuid+"3", uuid+"3", new BestMobilityEvaluator());
    }

    /**
     * Gets the list of AI types for the player
     *
     * @return A string of evaluator class names
     */
    @Override
    public String getAIType() {

        String s = "";

        //TODO: fix this so that the last ", " isn't there
        for (AmazonEvaluator e : evaluators) s += e.getClass().getSimpleName() + ", ";

        return s;

    }

    @Override
    public long getTurnStartTime() {
        return turnStartTime;
    }

    @Override
    public int getGameMoveTime() {
        return gameMoveTime;
    }

    @Override
    public long getTurnEndTime() {
        return turnEndTime;
    }

    @Override
    public int getPlayerColor() {
        return playerColor;
    }

}
