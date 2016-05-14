package kaz.idiot;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Created by kasra on 5/14/2016.
 */
public class SpectatorPanel extends JPanel {
    private Game game;
    private List<GamePanel> gps;
    private List<Controller> controllers;
    private boolean devmode;

    public SpectatorPanel(Game game, boolean devmode) {
        this.game = game;
        this.gps = new ArrayList<>();
        this.devmode = devmode;
        this.controllers = new ArrayList<>();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(dim.getSize());
        dim.height -= 100;
        dim.width -= 100;
        setMinimumSize(dim);

        //Create GamePanels
        for (int i = 0; i < game.getPlayerCount(); ++i) {
            GamePanel gp = new GamePanel(i, game);
            gps.add(gp);
            controllers.add(new Controller(game, gp));
        }
        ClickPanel clickPanel = new ClickPanel(this);
        //#easy TODO: show player names instead of numbers
        //#moderate TODO: check if the name you're adding actually is connected to the server

        setLayout(new BorderLayout());
        add(clickPanel, BorderLayout.EAST);
        add(gps.get(game.getCurrentPlayerNumber()), BorderLayout.CENTER);
    }

    public void handleCodes(List<String> codes, int playerNumber) {
//        controllers.get(playerNumber).handleCodes(codes);
        for (Controller c : controllers)
            c.handleCodes(codes);
    }

    public void handleEvent(String numString, String ev, String arg) {
        for (Controller controller : controllers)
            controller.handleEvent(numString, ev, arg);
        gps.forEach(GamePanel::repaint);
    }

    public void printEvent(String s) {
        //#easy TODO: maybe printEvent for every /event
        gps.forEach( gp -> gp.printEvent(s));
    }

    public boolean isDevMode() {
        return devmode;
    }

    private class ClickPanel extends JPanel {
        private int playerCount;
        private int rows, cols;
        private HashMap<GamePanel.Bounds, Integer> bounds2Integer = new HashMap<>();
        private int playerNumber;

        public ClickPanel(SpectatorPanel sp) {
            Dimension dim = sp.getSize();
            dim.width = 400;
            setMinimumSize(dim);
            setPreferredSize(dim);
            playerCount = game.getPlayerCount();
            playerNumber = game.getCurrentPlayerNumber();

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (GamePanel.Bounds bounds : bounds2Integer.keySet()) {
                        Rectangle bounding = new Rectangle((int) (bounds.tlx * getWidth()),
                                (int) (bounds.tly * getHeight()),
                                (int) (bounds.w * getWidth()),
                                (int) (bounds.h * getHeight()));
                        if (bounding.contains(e.getPoint()))
                            setViewing(sp, bounds2Integer.get(bounds));
                    }
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
            });
        }

        public void setViewing(SpectatorPanel sp, int number) {
            gps.get(playerNumber).repaint();
            sp.remove(gps.get(playerNumber));//TODO CHECK
            playerNumber = number;
            sp.add(gps.get(playerNumber), BorderLayout.CENTER);

            revalidate();
            repaint();
            gps.get(playerNumber).revalidate();
            gps.get(playerNumber).repaint();
            sp.revalidate();
            sp.repaint();
            //TODO check if the game itself is actually doing the thing with a chatFrame.println
            //TODO try repainting everything...
            //TODO try revalidate()ing everything
        }

        private void addActionToBounds(GamePanel.Bounds bounds, int playerNumber) {
            bounds2Integer.put(bounds, playerNumber);
        }

        @SuppressWarnings("Duplicates")
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int height = getHeight();
            int width = getWidth();

            g.setFont(new Font("SansSerif", Font.PLAIN, getHeight()/15));
            rows = 1;
            cols = 1;

            double ratioWide = 1 / 2d; // to have wide buttons. should be around twice as tall as they are wide
            double ratioSq = 1 / 1d; // to have square buttons.

            while (rows * cols < playerCount) {
                double ratio = ratioWide;

                //calculate the ratio if you add one to the rows = A
                //calculate the ratio if you add one to the cols = B
                //if A closer to the ratio than B then add one to rows.
                //otherwise, add one to cols.
                double colIncRatio, rowIncRatio;
                {
                    ++cols;

                    int buttonWidth = width / cols;
                    int buttonHeight = height / rows;

                    colIncRatio = (double) buttonHeight / buttonWidth;
                    --cols;
                }
                {
                    ++rows;

                    int buttonWidth = width / cols;
                    int buttonHeight = height / rows;
                    rowIncRatio = (double) buttonHeight / buttonWidth;
                    --rows;
                }
                if ((Math.abs(colIncRatio - ratio)) < Math.abs(rowIncRatio - ratio))
                    ++cols;
                else ++rows;
            }
            int buttonWidth = width / cols;
            int buttonHeight = height / rows;

            g.setColor(Color.BLACK);
            int playerCounter = playerCount;
            for (int i = 0; i < rows; ++i)
                for (int j = 0; j < cols; ++j)
                    paintClickBox(g,
                            (playerCount - playerCounter >= playerCount)? -1: playerCount - playerCounter--,
                            j* buttonWidth, i * buttonHeight, buttonWidth, buttonHeight);
        }

        private void paintClickBox(Graphics g, int playerNumber, int tlx, int tly, int w, int h) {
            g.setColor(Color.WHITE);
            g.fillRect(tlx, tly, w, h);
            g.setColor(Color.BLACK);
            g.drawRect(tlx, tly, w, h);
            if (playerNumber == this.playerNumber) {
                Color gold = new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen() - 30, Color.YELLOW.getBlue());
                g.setColor(gold);
                g.drawRect(tlx + 2, tly + 2, w - 4, h - 4);
            }
            if (playerNumber != -1) {
                GamePanel.Bounds bounds = new GamePanel.Bounds(tlx, tly, w, h, getWidth(), getHeight());
                addActionToBounds(bounds, playerNumber);
                tlx = tlx + w / 2 - SwingUtilities.computeStringWidth(g.getFontMetrics(), playerNumber + "") / 2;
                tly = tly + h / 2 + g.getFontMetrics().getHeight() * 2 / 7;
                g.drawString(playerNumber + "", tlx, tly);
            }
        }
    }
}
