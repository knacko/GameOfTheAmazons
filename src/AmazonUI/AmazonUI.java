package AmazonUI;

import AmazonGame.*;
import AmazonGame.AmazonGameClient;
import AmazonGame.AmazonPlayer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by D on 2/8/2017.
 */
public class AmazonUI extends JFrame {

    private AmazonPlayer player;

    public AmazonUI(AmazonPlayer player) {

        this.player = player;

        setSize(900, 800);
        setLocation(100, 100);

        setTitle("Game of the Amazons (COSC 322, UBCO). Player: " + player.userName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        //contentPane.add(Box.createVerticalGlue());

        AmazonBoardUI amazonBoardUI = new AmazonBoardUI(player);
        //AmazonSideUI amazonSideUI = new AmazonSideUI(player.gameClient);

        //createGameBoard();
        contentPane.add(amazonBoardUI);
        //contentPane.add(amazonSideUI);

        setVisible(true);
        repaint();
    }
}
