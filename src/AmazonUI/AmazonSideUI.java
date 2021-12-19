package AmazonUI;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import AmazonGame.*;

/**
 * Created by D on 2/9/2017.
 */
public class AmazonSideUI extends JPanel implements ActionListener {

    private AmazonGameClient client;

    public AmazonSideUI(AmazonGameClient client) {

        this.client = client;

        setPreferredSize(new Dimension(500, 800));

        JoinGameUI joinGameUI = new JoinGameUI();
        JoinRoomUI joinRoomUI = new JoinRoomUI();

        add(joinGameUI);
        add(joinRoomUI);

        joinRoomUI.setEnabled(false);

    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public class JoinGameUI extends JPanel implements ActionListener {

        List<String> playerList = Arrays.asList("HumanPlayer", "RandomAI", "SmartAI");

        JComboBox playerListCB;
        JButton joinGameB;

        public JoinGameUI() {
            //playerList = Arrays.asList("Human player", "Random AI", "Smart AI");

            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            playerListCB = new JComboBox(playerList.toArray(new String[0]));
            playerListCB.setPreferredSize(new Dimension(150, getHeight()));
           // playerListCB.setSelectedIndex(0);
           // playerListCB.addActionListener(this);

            joinGameB = new JButton("Join Game");
            joinGameB.addActionListener(this);
            joinGameB.setActionCommand("pressed");

            add(playerListCB);
            add(joinGameB);
        }

        /**
         * Sends the selected player name to the game, which will let you log in to the server
         * @param e Holds the string contained in playerListCB, equal to the class name of the player selected
         */
        @Override
        public void actionPerformed(ActionEvent e) {
           // client.joinGame(playerListCB.getSelectedItem().toString());
        }
    }

    /**
     * TODO: Make this look disable before the rooms are loaded
     */
    public class JoinRoomUI extends JPanel implements ActionListener {

        JComboBox roomCB;
        JButton joinRoomB;

        public JoinRoomUI() {
            //playerList = Arrays.asList("Human player", "Random AI", "Smart AI");

            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            roomCB = new JComboBox(new String[]{""});
            roomCB.setPreferredSize(new Dimension(150, getHeight()));
            // playerListCB.setSelectedIndex(0);
            // playerListCB.addActionListener(this);

            joinRoomB = new JButton("Join Room");
            joinRoomB.setActionCommand("pressed");

            add(roomCB);
            add(joinRoomB);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            client.joinRoom(roomCB.getSelectedItem().toString());
        }

        public void enableThis(List<String> l) {

            roomCB.setModel(new DefaultComboBoxModel( l.toArray(new String[0])));
            joinRoomB.addActionListener(this);
        }
    }
}