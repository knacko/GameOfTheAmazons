package ygraphs.ai.smart_fox.games;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import AmazonBoard.*;
import AmazonUI.*;
import ygraphs.ai.smart_fox.GameMessage;

/**
 * For testing and demo purposes only. An GUI Amazon client for human players
 * @author yong.gao@ubc.ca
 */
public class BetterAmazon extends GamePlayer{

    private GameClient gameClient;
    private JFrame guiFrame = null;
    private GameBoard board = null;
    private boolean gameStarted = false;
    public String usrName = null;

    /**
     * Constructor
     * @param name
     * @param passwd
     */
    public BetterAmazon(String name, String passwd){

        this.usrName = name;
       // setupGUI();

        //connectToServer(name, passwd);

        //AmazonUI amazonUI = new AmazonUI(game);
        AmazonBoard board = new AmazonBoard();

    }

    private void connectToServer(String name, String passwd){
        // create a client and use "this" class (a GamePlayer) as the delegate.
        // the client will take care of the communication with the server.
        gameClient = new GameClient(name, passwd, this);
    }

    @Override
    /**
     * Implements the abstract method defined in GamePlayer. Will be invoked by the GameClient
     * when the server says the login is successful
     */
    public void onLogin() {

        //once logged in, the gameClient will have  the names of available game rooms
        ArrayList<String> rooms = gameClient.getRoomList();
        this.gameClient.joinRoom(rooms.get(5));
    }


    /**
     * Implements the abstract method defined in GamePlayer. Once the user joins a room,
     * all the game-related messages will be forwarded to this method by the GameClient.
     *
     * See GameMessage.java
     *
     * @param messageType - the type of the message
     * @param msgDetails - A HashMap info and data about a game action
     */
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails){

        if(messageType.equals(GameMessage.GAME_ACTION_START)){

            if(((String) msgDetails.get("player-black")).equals(this.userName())){
                System.out.println("Game State: " +  msgDetails.get("player-black"));
            }

        }
        else if(messageType.equals(GameMessage.GAME_ACTION_MOVE)){
            handleOpponentMove(msgDetails);
        }
        return true;
    }

    //handle the event that the opponent makes a move.
    private void handleOpponentMove(Map<String, Object> msgDetails){
        System.out.println("OpponentMove(): " + msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR));
        ArrayList<Integer> qcurr = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
        ArrayList<Integer> qnew = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
        ArrayList<Integer> arrow = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
        System.out.println("QCurr: " + qcurr);
        System.out.println("QNew: " + qnew);
        System.out.println("Arrow: " + arrow);

        board.markPosition(qnew.get(0), qnew.get(1), arrow.get(0), arrow.get(1),
                qcurr.get(0), qcurr.get(1), true);

    }


    /**
     * handle a move made by this player --- send the info to the server.
     * @param x queen row index
     * @param y queen col index
     * @param arow arrow row index
     * @param acol arrow col index
     * @param qfr queen original row
     * @param qfc queen original col
     */
    public void playerMove(int x, int y, int arow, int acol, int qfr, int qfc){

        int[] qf = new int[2];
        qf[0] = qfr;
        qf[1] = qfc;

        int[] qn = new int[2];
        qn[0] = x;
        qn[1] = y;

        int[] ar = new int[2];
        ar[0] = arow;
        ar[1] = acol;

        //To send a move message, call this method with the required data
        this.gameClient.sendMoveMessage(qf, qn, ar);

    }

    //set up the game board
    private void setupGUI(){
        guiFrame = new JFrame();

        guiFrame.setSize(800, 600);
        guiFrame.setTitle("Game of the Amazons (COSC 322, UBCO)");

        guiFrame.setLocation(200, 200);
        guiFrame.setVisible(true);
        guiFrame.repaint();
        guiFrame.setLayout(null);

        Container contentPane = guiFrame.getContentPane();
        contentPane.setLayout(new  BorderLayout());

        contentPane.add(Box.createVerticalGlue());

        board = createGameBoard();
        contentPane.add(board,  BorderLayout.CENTER);
    }

    private GameBoard createGameBoard(){
        return new GameBoard(this);
    }

    public boolean handleMessage(String msg) {
        System.out.println("Time Out ------ " + msg);
        return true;
    }

    @Override
    public String userName() {
        return usrName;
    }


    /**
     * The game board
     *
     * @author yongg
     *
     */
    public class GameBoard extends JPanel{

        private static final long serialVersionUID = 1L;
        private  int rows = 10;
        private  int cols = 10;

        int width = 500;
        int height = 500;
        int cellDim = width / 10;
        int offset = width / 20;

        int posX = -1;
        int posY = -1;

        int r = 0;
        int c = 0;


        BetterAmazon game = null;
        private BoardGameModel gameModel = null;

        boolean playerAMove;

        public GameBoard(BetterAmazon game){
            this.game = game;
            gameModel = new BoardGameModel(this.rows + 1, this.cols + 1);

            //if(!game.isGamebot){
            addMouseListener(new  GameEventHandler());
            //}
            init(true);
        }

        public void init(boolean isPlayerA){
            String tagB = null;
            String tagW = null;

            tagB = BoardGameModel.POS_MARKED_BLACK;
            tagW = BoardGameModel.POS_MARKED_WHITE;

            gameModel.gameBoard[1][4] = tagW;
            gameModel.gameBoard[1][7] = tagW;
            gameModel.gameBoard[3][1] = tagW;
            gameModel.gameBoard[3][10] = tagW;

            gameModel.gameBoard[8][1] = tagB;
            gameModel.gameBoard[8][10] = tagB;
            gameModel.gameBoard[10][4] = tagB;
            gameModel.gameBoard[10][7] = tagB;


            System.out.println(toString());
        }



        /**
         * Returns the contents of a particular square
         * Orients from the bottom left, with a range of [1,10]
         *
         * @param xPos The x positions of the square to get
         * @param yPos The y position of the square to get
         * @return The string representing the selected position
         */
        private String getSquare(int xPos, int yPos) {

            return gameModel.gameBoard[yPos][xPos];

        }

        /**
         * @return A string output of the board
         */
        public String toString () {

            String s = "";

            if (true) return s; //TODO: remove this

            for (int y = 10; y >= 1; y--) { //needs to create s from top to bottom
                for (int x = 1; x <= 10; x++) {
                    switch (getSquare(x, y)) {
                        case BoardGameModel.POS_AVAILABLE:
                            s += " ";
                            break;
                        case BoardGameModel.POS_MARKED_ARROW:
                            s += "X";
                            break;
                        case BoardGameModel.POS_MARKED_BLACK:
                            s += "*";
                            break;
                        case BoardGameModel.POS_MARKED_WHITE:
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
         *
         * @param posInitXQueen
         * @param posInitYQueen
         * @param posEndXQueen
         * @param posEndYQueen
         * @param posXArrow
         * @param posYArrow
         * @param opponentMove
         * @return
         */
        public boolean markPosition(int posInitXQueen, int posInitYQueen, int posEndXQueen, int posEndYQueen, int posXArrow, int posYArrow, boolean  opponentMove){

            System.out.println(posEndYQueen + ", " + posEndXQueen + ", " + posYArrow + ", " + posXArrow + ", " + posInitYQueen + ", " + posInitXQueen);

            boolean valid = gameModel.positionMarked(posEndYQueen, posEndXQueen, posYArrow, posXArrow, posInitYQueen, posInitXQueen, opponentMove);
            repaint();
            return valid;
        }

        // JCmoponent method
        protected void paintComponent(Graphics gg){
            Graphics g = (Graphics2D) gg;

            for(int i = 0; i < rows + 1; i++){
                g.drawLine(i * cellDim + offset, offset, i * cellDim + offset, rows * cellDim + offset);
                g.drawLine(offset, i*cellDim + offset, cols * cellDim + offset, i*cellDim + offset);
            }

            for(int r = 0; r < rows; r++){
                for(int c = 0; c < cols; c++){

                    posX = c * cellDim + offset;
                    posY = r * cellDim + offset;

                    posY = (9 - r) * cellDim + offset;

                    switch (getSquare(c+1,r+1)) {

                        case BoardGameModel.POS_AVAILABLE:
                            g.clearRect(posX + 1, posY + 1, 49, 49);
                            break;

                        case BoardGameModel.POS_MARKED_BLACK:
                            g.clearRect(posX + 1, posY + 1, 49, 49);
                            g.fillOval(posX, posY, 50, 50);
                            break;

                        case BoardGameModel.POS_MARKED_ARROW:
                            g.clearRect(posX + 1, posY + 1, 49, 49);
                            g.clearRect(posX + 1, posY + 1, 49, 49);
                            g.drawLine(posX, posY, posX + 50, posY + 50);
                            g.drawLine(posX, posY + 50, posX + 50, posY);
                            break;

                        case BoardGameModel.POS_MARKED_WHITE:
                            g.clearRect(posX + 1, posY + 1, 49, 49);
                            g.drawOval(posX, posY, 50, 50);
                            break;
                    }
                }
            }
        }//method

        //JComponent method
        public Dimension getPreferredSize() {
            return new Dimension(500,500);
        }

        /**
         * Handle mouse events
         *
         * @author yongg
         */
        public class GameEventHandler extends MouseAdapter{

            int counter = 0;

            ArrayList validMoveList;

            int posInitXQueen = 0, posInitYQueen = 0;
            int posEndXQueen = 0, posEndYQueen = 0;
            int posXArrow = 0, posYArrow;

            public void mousePressed(MouseEvent e) {

                if(!gameStarted){
                    //return;
                }

                int posXClick = e.getX();
                int posYClick = e.getY();

                int posX = (posXClick - offset) / cellDim + 1;
                int posY = 9 - (posYClick - offset) / cellDim + 1;

                if (posX < 1 || posX > 10) return;
                if (posY < 1 || posY > 10) return;

                if(counter == 0){  //initial click of the queen

                    // make sure that you click a queen
                    if (!(getSquare(posX, posY) == BoardGameModel.POS_MARKED_WHITE || getSquare(posX, posY) == BoardGameModel.POS_MARKED_BLACK)) return;

                    System.out.println("Clicked unit at " + posX + ", " + posY);

                    posInitXQueen = posX;
                    posInitYQueen= posY;

                    validMoveList = generateListOfValidMoves(posX, posY);

                    counter++;
                }
                else if(counter == 1){

                    // make sure that you click a queen
                    if (getSquare(posX, posY) != BoardGameModel.POS_AVAILABLE) return;

                    int[] a = {posX, posY};

                    if (!isMoveValid(validMoveList, a)) return;

                    System.out.println("Moved unit to " + (posX) + ", " + posY);

                    posEndXQueen = posX;
                    posEndYQueen = posY;

                    validMoveList = generateListOfValidMoves(posX, posY);

                    counter++;
                }
                else if (counter == 2){

                    if (getSquare(posX, posY) != BoardGameModel.POS_AVAILABLE) return;

                    int[] a = {posX, posY};
                    if (!isMoveValid(validMoveList, a)) return;

                    System.out.println("Shot arrow to " + (posX) + ", " + posY);
                    posXArrow = posX;
                    posYArrow = posY;

                    counter++;
                }

                if(counter == 3){
                    counter = 0;
                    boolean validMove = markPosition(posInitXQueen, posInitYQueen, posEndXQueen, posEndYQueen, posXArrow, posYArrow, false); // update itself

                    if(validMove){
                        game.playerMove(posInitXQueen, posInitYQueen, posEndXQueen, posEndYQueen, posXArrow, posYArrow); //to server
                    }

                    //posInitXQueen = posInitYQueen = posEndXQueen = posEndYQueen = posXArrow = posYArrow = 0;

                }
            }
        }//end of GameEventHandler

        /**
         * Checks all 6 possible directions of movement/shooting for potential open squares.
         * It will iterate away from the position
         *
         * @param posX The x-position of the square being checked, must be within [1,10]
         * @param posY The y-postion of the square being checked, must be within [1,10]
         * @return A list of available moves in the form of arrays as [X,Y]
         */
        private ArrayList generateListOfValidMoves(int posX, int posY) {

            ArrayList list = new ArrayList();

            for (int moveX = -1; moveX <= 1 ; moveX++) {
                for (int moveY = -1; moveY <= 1 ; moveY++) {

                    if (moveX == 0 && moveY == 0) continue; // skip the center square

                    list.addAll(checkLineOfMoves(posX, posY, moveX, moveY));
                }
            }

            return list;
        }

        /**
         * Checks in a direction based on the increment of moveX, moveY and returns a list of available moves
         * ie. from (0,0), if moveX = 1 and moveY = 0, it will increment through (0,1) to (0,2) to (0,3), etc
         *      until an invalid move is found.
         *
         * @param posX The x-position of the square being checked
         * @param posY The y-postion of the square being checked
         * @param moveX The amount to increment X when checking
         * @param moveY The amount to increment Y when checking
         * @return A list of available moves in the form of arrays as [X,Y]
         */
        private ArrayList checkLineOfMoves(int posX, int posY, int moveX, int moveY) {

            ArrayList list = new ArrayList();

            int n = 0;

            System.out.println("\nChecking direction " + moveX + ", " + moveY);

            do {

                posX += moveX;
                posY += moveY;

                if (posX < 1 || posX > 10) break;
                if (posY < 1 || posY > 10) break;

                System.out.println("Unit '" + getSquare(posX, posY) + "' at " + posX + ", " + posY );

                if(!(getSquare(posX, posY).equalsIgnoreCase(BoardGameModel.POS_AVAILABLE))) break;

                list.add(new int[]{posY, posX});

            } while (++n < 10); // just to prevent rogue infinity loop

            return list;
        }

    private boolean isMoveValid (ArrayList<int[]> list, int[] pos) {

            for(int[] a : list) {

                System.out.println("Checking: " + Arrays.toString(a) + " to " + Arrays.toString(pos));

                if (Arrays.equals(a, pos)) return true;
            }

            return false;
    }





    }//end of AmazonBoard.AmazonBoard








    /**
     * Constructor
     * @param args
     */
    public static void main(String[] args) {
        //Amazon game = new Amazon("yong.gao", "cosc322");
        BetterAmazon game = new BetterAmazon(args[0], args[1]);
    }

}//end of Amazon
