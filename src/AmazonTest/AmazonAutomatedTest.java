package AmazonTest;

import AmazonEvaluator.AmazonEvaluator;
import AmazonEvaluator.BestMobilityEvaluator;
import AmazonEvaluator.MaxMobilityEvaluator;
import AmazonEvaluator.RandomEvaluator;
import AmazonGame.AmazonAIPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by D on 2/14/2017.
 */
public class AmazonAutomatedTest {

    static int  maxGames = 10;

    //TODO: have the list of acceptable evaluators generated dynamically
    //TODO: Fix this
    static AmazonEvaluator[] evaluators = null;//{new RandomEvaluator(), new MaxMobilityEvaluator(), new BestMobilityEvaluator()};

    /**
     * Run a number of games will all combination of listed AIs
     *
     * @param args How many games to run
     */
    public static void main(String[] args) {


        if (args.length > 0) maxGames = Integer.parseInt(args[0]);

        AutomatedGameMaker maker = new AutomatedGameMaker(evaluators, maxGames);
        maker.outputResults();

    }

    /**
     * This class will automatically play a number of games between the specified AIs
     */
    public static class AutomatedGameMaker implements ActionListener{

        int maxGames;
        Set<Game> games;
        AmazonEvaluator[] evaluators;

        public AutomatedGameMaker(AmazonEvaluator[] evaluators, int maxGames) {

            this.evaluators = evaluators;
            this.maxGames = maxGames;

            games = createGames();

            for (Game g : games) {
                for (int i = 0; i < maxGames; i++) {
                    playGame(g);
                }
            }
        }

        /**
         * Outputs to console the results of all the games played
         */
        public void outputResults() {
            for (Game g : games)
                System.out.println(g.p1.getClass().getSimpleName() + " vs " + g.p2.getClass().getSimpleName() +
                        ": " + g.wins + " to " + (maxGames - g.wins));
        }

        /**
         * Plays a game between the specified evaluators
         * @param g The game to play
         */
        private void playGame(Game g) {

            AmazonAIPlayer p1 = new AmazonAIPlayer("p1", "p1", g.p1);
            AmazonAIPlayer p2 = new AmazonAIPlayer("p2", "p2", g.p2);

            //TODO: Functionality to detect when game is won, or player has conceded/win is not implemented yet.

            //If player 1 wins, run addWin();
            //If player 2 wins, do nothing, as wins can be calculated from maxGame - p1 wins

        }

        /**
         * Creates all a set of all permutations of the evaluators
         * @return The set of all permutations of the evaluators
         */
        private Set<Game> createGames() {

            Set<Game> set = new HashSet<Game>();
            for (int i = 0; i < evaluators.length; i++)
                for (int j = 0; j < evaluators.length; j++) {

                    if (i == j) continue;
                    set.add(new Game(evaluators[i], evaluators[j]));

                }

            return set;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }

        /**
         * Simple data structure to hold the evaluators that will play each other, and will track the wins between them
         */
        private class Game {

            AmazonEvaluator p1, p2;
            int wins = 0;

            private Game(AmazonEvaluator p1, AmazonEvaluator p2) {
                this.p1 = p1;
                this.p2 = p2;
            }

            public void addWin() {
                wins++;
            }

            @Override
            public boolean equals(Object obj) {

                Game g = (Game) obj;

                if (p1.getClass().getSimpleName() == g.p1.getClass().getSimpleName() && p2.getClass().getSimpleName() == g.p2.getClass().getSimpleName())
                    return true;
                if (p1.getClass().getSimpleName() == g.p2.getClass().getSimpleName() && p2.getClass().getSimpleName() == g.p1.getClass().getSimpleName())
                    return true;

                return false;
            }
        }
    }
}
