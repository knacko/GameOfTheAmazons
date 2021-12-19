package AmazonGame;

import AmazonEvaluator.AmazonEvaluator;
import AmazonTest.AmazonAutomatedTest;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by D on 2/15/2017.
 */
public class AmazonAutoAIPlayer extends AmazonAIPlayer {

    AmazonAutomatedTest.AutomatedGameMaker maker;

    /**
     * Constructor for use with the AmazonAutomatedTest
     * @param name
     * @param password
     * @param evaluator
     * @param maker
     */
    public AmazonAutoAIPlayer(String name, String password, AmazonEvaluator evaluator, AmazonAutomatedTest.AutomatedGameMaker maker) {

        super(name, password, evaluator);
        this.maker = maker;

        amazonUI.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // maker.playNextGame();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println("Window is closed");
            }
        });
    }

    /**
     * Dispatches an event to the UI to signify that it should close the window
     */
    @Override
    public boolean endGame() {
        boolean didIWin = super.endGame();
        amazonUI.dispatchEvent(new WindowEvent(amazonUI, WindowEvent.WINDOW_CLOSING));
       // maker.dispatchEvent();
        return didIWin;
    }
}
