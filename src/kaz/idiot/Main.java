package kaz.idiot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

public class Main {
    public static JFrame frame;
    public static GamePanel gp;
    public static Controller controller;
    public static Game game;
    public static final int CARD_X = 79;
    public static final int CARD_Y = 123;

    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> frame = new StartFrame());
        SwingUtilities.invokeLater(() -> frame = new IdiotFrame());
        //TODO: make the rubik's square game.
    }

    static class IdiotFrame extends JFrame {
        public IdiotFrame() {
            this(0);
        }

        public IdiotFrame(int number) {
            super("Idiot");

            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            //TODO: construct GamePanels irrespective of playerNumber, adding them to a game.
            //game should prompt to see if everyone has connected, and then the first player initializes the game and sends it to everyone else

            game = new Game(12);
            gp = new GamePanel(game, number, 1920, 1080);
            controller = new Controller(game, gp);
            add(gp, BorderLayout.CENTER);
            pack();
            setResizable(true);
            setVisible(true);
        }
    }
}
