package kaz.idiot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

public class Main {
    public static JFrame frame;
    public static GamePanel[] gp;
    public static Controller[] controller;
    public static Game game;
    public static final int CARD_X = 79;
    public static final int CARD_Y = 123;
    public static final int playerCount = 2;

    public static void main(String[] args) {
        game = new Game(playerCount);
        controller = new Controller[playerCount];
        gp = new GamePanel[2];
//        SwingUtilities.invokeLater(() -> frame = new StartFrame());
//        SwingUtilities.invokeLater(() -> frame = new IdiotFrame());
        for (int i = 0; i < playerCount; ++i) {
            new IdiotFrame(i);
            //TODO: make players select their own things to swap
            //TODO: controllers need to be separate for each player. array of controllers?
        }
        //TODO: make the rubik's square game.
    }

    static class IdiotFrame extends JFrame {
        public IdiotFrame() {
            this(0);
        }

        public IdiotFrame(int number) {
            super("Idiot: " + number);

            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            //each idiotframe has its own controller and gamepanel
            //TODO: construct GamePanels irrespective of playerNumber, adding them to a game.
            //game should prompt to see if everyone has connected, and then the first player initializes the game and sends it to everyone else

            gp[number] = new GamePanel(game, number, 1920, 1080);
            controller[number] = new Controller(game, gp[number]);
            add(gp[number], BorderLayout.CENTER);
            pack();
            setResizable(true);
            setVisible(true);
        }
    }
}
