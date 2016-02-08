package kaz.idiot;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;


/**
 * Created by kasra on 2/6/2016.
 */

//THE VIEW
public class GamePanel extends JPanel {
    private Game game;
    private int playerNumber;
    private int width, height;

    private Font mainNameFont = new Font("SansSerif", Font.PLAIN, 30);
    private Font nameFont = new Font("SansSerif", Font.PLAIN, 22);
    private Font mainCardFont = new Font("SansSerif", Font.PLAIN, 22);
    private Font cardFont = new Font("SansSerif", Font.PLAIN, 18);

    public GamePanel (Game game, int pn, int w, int h) {
        this.game = game;
        this.playerNumber = pn;
        this.width = w;
        this.height = h;
    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        paintPlayers(g);
        //TODO: add a pane for an event log and a chat log

    }

    private void paintPlayers(Graphics g) {
        paintMainPlayer(g);
        paintSidePlayers(g);

    }

    private void paintMainPlayer(Graphics g) {
        SIDE side = SIDE.BOTTOM;
        int tlx = (int) (width * side.tlx);
        int tly = (int) (height * side.tly);
        int w = (int) (width * side.dx);
        int h = (int) (height * side.dy);
        paintPlayer(g, game.getPlayer(playerNumber), tlx, tly, w, h);
    }

    private void paintSidePlayers(Graphics g) {
        int[] count = {playerNumber};
        int sideCount = (game.getPlayerCount()-1)/3;
        int topCount = game.getPlayerCount() -1 - sideCount * 2;
        int tlx, tly, w, h;
        SIDE side;

        //TODO: less copypasta please
        //paint right players
        side = SIDE.RIGHT;
        tlx = (int) (width * side.tlx);
        tly = (int) (height * side.tly);
        w = (int) (width * side.dx);
        h = (int) (height * side.dy);

        for (int i = sideCount - 1 ; i >= 0; --i) {
            paintPlayer(g, game.getPlayer(countInc(count)), tlx, tly + h/sideCount * i, w, h/sideCount);
        }

        //paint top players
        side = SIDE.TOP;
        tlx = (int) (width * side.tlx);
        tly = (int) (height * side.tly);
        w = (int) (width * side.dx);
        h = (int) (height * side.dy);

        for (int i = topCount - 1; i >= 0; --i) {
            paintPlayer(g, game.getPlayer(countInc(count)), tlx + w/topCount * i, tly, w/topCount, h);
        }

        //paint left players
        side = SIDE.LEFT;
        tlx = (int) (width * side.tlx);
        tly = (int) (height * side.tly);
        w = (int) (width * side.dx);
        h = (int) (height * side.dy);

        for (int i = 0; i < sideCount; ++i) {
            paintPlayer(g, game.getPlayer(countInc(count)), tlx, tly + h/sideCount * i, w, h/sideCount);
        }
    }

    private int countInc(int[] count) {
        return ++count[0]%game.getPlayerCount();
    }

    private void paintPlayer(Graphics g, Player p, int tlx, int tly, int w, int h) {

        //paint name and border
        g.setFont(p.equals(game.getPlayer(playerNumber))? mainNameFont : nameFont);
        g.setColor(p.equals(game.getPlayer(game.getCurrentPlayerNumber()))? Color.BLUE : Color.BLACK);
        g.drawRect(tlx, tly, w, h);
        g.drawString(p.getName(), tlx, tly);

        //paint hand
        g.setFont(p.equals(game.getPlayer(game.getCurrentPlayerNumber()))? mainCardFont : cardFont);
        List<CARD> hand = p.getHand();
        for (int i = 0; i < hand.size(); ++i) {
            g.drawString(hand.get(i).toString(), tlx, tly + 30 + 25*i);
        }

        //paint top cards
        List<CARD> top = p.getTop();
        for (int i = 0; i < hand.size(); ++i) {
            g.drawString(top.get(i).toString(), tlx + w/2, tly + 30 + 25*i);
        }

        //paint bottom cards
        List<CARD> bot = p.getBot();
        for (int i = 0; i < hand.size(); ++i) {
            g.drawString(bot.get(i).toString(), tlx + w/2, tly + h/2 + 30 + 25*i);
        }
    }

    enum SIDE {
        LEFT(0, .4, .3, .5), TOP(0, .03, 1, .3), RIGHT(.7, .4, .3, .5), BOTTOM(.3, .6, .4,.4);

        public double tlx, tly, dx, dy;

        static List<SIDE> getOtherSides() {
            return Arrays.asList(values()).subList(0, values().length - 1);
        }

        SIDE(double tlx, double tly, double dx, double dy) {
            this.tlx = tlx;
            this.tly = tly;
            this.dx = dx;
            this.dy = dy;
        }
    }
}
