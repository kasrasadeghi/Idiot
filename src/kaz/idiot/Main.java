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
        game = new Game(3);
        SwingUtilities.invokeLater(() -> frame = new IdiotFrame());
        //TODO: make the rubik's square game.
    }

    static class IdiotFrame extends JFrame {
        IdiotFrame() {
            super("Idiot");
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            gp = new GamePanel(game, 0, 1920, 1080);
            controller = new Controller(game, gp);
            add(gp, BorderLayout.CENTER);
            pack();
            setResizable(true);
            setVisible(true);
        }
    }
}
