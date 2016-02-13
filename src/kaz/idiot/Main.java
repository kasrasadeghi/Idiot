package kaz.idiot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

public class Main {
    public static JFrame frame;
    public static Game game;
    public static final int CARD_X = 79;
    public static final int CARD_Y = 123;

    public static void main(String[] args) {
        game = new Game(6);
        SwingUtilities.invokeLater(() -> frame = new IdiotFrame());
    }

    static class IdiotFrame extends JFrame {
        IdiotFrame() {
            super("Idiot");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            GamePanel gp = new GamePanel(game, 0, 1200, 1200);

            //TODO: Add Listeners
            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent mouseEvent) {}
                public void mousePressed(MouseEvent mouseEvent) {}
                public void mouseReleased(MouseEvent mouseEvent) {}
                public void mouseEntered(MouseEvent mouseEvent) {}
                public void mouseExited(MouseEvent mouseEvent) {}
            });

            add(gp, BorderLayout.CENTER);
            pack();
            setResizable(true);
            setVisible(true);
        }


    }
}
