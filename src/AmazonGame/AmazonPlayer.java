package AmazonGame;

import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;
import AmazonEvaluator.AmazonEvaluator;
import AmazonEvaluator.AmazonMove;
import AmazonUI.AmazonUI;
import ygraphs.ai.smart_fox.games.AmazonsGameMessage;
import ygraphs.ai.smart_fox.games.GamePlayer;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by D on 2/12/2017.
 */
public abstract class AmazonPlayer extends GamePlayer {

    AmazonGameClient gameClient;

    public int gameMoveTime; //Max length of move in seconds
    public long turnStartTime; // Current start time of latest move - if 0 then not
    public long turnEndTime;
    public int playerColor;

    AmazonUI amazonUI;
    AmazonBoard board;
    String name = "team6", password = "team6";
    ArrayList<AmazonMove> moveHistory = new ArrayList<AmazonMove>();
    //AmazonEvaluator evaluator;

    public AmazonPlayer(String name, String password) {

        this.name = name;
        this.password = password;
        board = new AmazonBoard();
        amazonUI = new AmazonUI(this);
        connectToServer(name, password);

    }

    /**
     * When the player connects to the server, it will pick room 6 (designated for our group) and join it
     * TODO: have the room number selectable when creating the game
     */
    @Override
    public void onLogin() {

        ArrayList<String> rooms = gameClient.getRoomList();

        System.out.println(gameClient.getRoomList().size() + " rooms available");
        for (String room : gameClient.getRoomList()) System.out.println(room);

        String room = rooms.get(1);

        gameClient.joinRoom(room);
        System.out.println(userName() + " joined room " + room);

    }

    /**
     * Passes the player info and the player itself as a delegate to the server
     *
     * @param name   The name of the player
     * @param passwd The password of the player (unused)
     */
    public void connectToServer(String name, String passwd) {
        System.out.println("Attempting to connect to server...");
        gameClient = new AmazonGameClient(name, passwd, this);
        System.out.println("Connected to server.");
    }

    /**
     * Gets the game board
     *
     * @return The game board
     */
    public AmazonBoard getBoard() {
        return board;
    }

    /**
     * Gets the username
     *
     * @return the username
     */
    @Override
    public String userName() {
        return name;
    }

    /**
     * Interprets the data representing a move into the data used in the program
     *
     * @param msgDetails the details for the move, check GameClient for formatting info
     * @return the move in an easier to use format
     */
    //handle the event that the opponent makes a move.
    public AmazonMove generateMoveFromMsg(Map<String, Object> msgDetails) {
        ArrayList<Integer> qcurr = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
        ArrayList<Integer> qnew = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
        ArrayList<Integer> arrow = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);

        AmazonSquare sInit = getBoard().getSquare(qcurr.get(1), qcurr.get(0));
        AmazonSquare sFinal = getBoard().getSquare(qnew.get(1), qnew.get(0));
        AmazonSquare sArrow = getBoard().getSquare(arrow.get(1), arrow.get(0));

        AmazonMove move = new AmazonMove(sInit, sFinal, sArrow);

        return move;
    }

    /**
     * Only used by AmazonUI to get the name of the evaluator to display in the title
     * @return The evaluator
     */

    public abstract String getAIType();

    public abstract long getTurnStartTime();
    public abstract long getTurnEndTime();
    public abstract int getGameMoveTime();
    public abstract int getPlayerColor();
}
