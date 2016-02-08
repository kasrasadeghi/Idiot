package kaz.idiot;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Main {
    public static JFrame frame;
    public static Game game;

    public static void main(String[] args) {
        game = new Game(2);
        SwingUtilities.invokeLater(() -> frame = new IdiotFrame());
    }

    static class IdiotFrame extends JFrame {
        IdiotFrame() {
            super("Idiot");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GamePanel gp = new GamePanel(game, 0, 1000, 680);

            //TODO: Add Listeners
            addMouseListener(new MouseListener() {

                public void mouseClicked(MouseEvent mouseEvent) {

                }

                public void mousePressed(MouseEvent mouseEvent) {

                }

                public void mouseReleased(MouseEvent mouseEvent) {

                }

                public void mouseEntered(MouseEvent mouseEvent) {

                }

                public void mouseExited(MouseEvent mouseEvent) {

                }
            });

            // no need for an updating thread
            // maybe a thread that listens to server updates?

            add(gp);
            pack();
            setResizable(true);
            setVisible(true);
        }


    }
}
