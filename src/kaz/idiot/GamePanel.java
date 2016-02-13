package kaz.idiot;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;
import static kaz.idiot.Main.*;


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

        setMinimumSize(new Dimension(width, height));

        //TODO: add a pane for an event log and a chat log
    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        paintPlayers(g);

//        paintCard(g, CARD.SPADE_A, 50, 50, 1);
    }

    private void paintPlayers(Graphics g) {
        paintMainPlayer(g);
        paintSidePlayers(g);

    }

    private void paintMainPlayer(Graphics g) {
        SIDE side = SIDE.BOTTOM;
        int tlx = (int) (getWidth() * side.tlx);
        int tly = (int) (getHeight() * side.tly);
        int w = (int) (getWidth() * side.dx);
        int h = (int) (getHeight() * side.dy);
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
        tlx = (int) (getWidth() * side.tlx);
        tly = (int) (getHeight() * side.tly);
        w = (int) (getWidth() * side.dx);
        h = (int) (getHeight() * side.dy);

        for (int i = sideCount - 1 ; i >= 0; --i) {
            paintPlayer(g, game.getPlayer(countInc(count)), tlx, tly + h/sideCount * i, w, h/sideCount);
        }

        //paint top players
        side = SIDE.TOP;
        tlx = (int) (getWidth() * side.tlx);
        tly = (int) (getHeight() * side.tly);
        w = (int) (getWidth() * side.dx);
        h = (int) (getHeight() * side.dy);

        for (int i = topCount - 1; i >= 0; --i) {
            paintPlayer(g, game.getPlayer(countInc(count)), tlx + w/topCount * i, tly, w/topCount, h);
        }

        //paint left players
        side = SIDE.LEFT;
        tlx = (int) (getWidth() * side.tlx);
        tly = (int) (getHeight() * side.tly);
        w = (int) (getWidth() * side.dx);
        h = (int) (getHeight() * side.dy);

        for (int i = 0; i < sideCount; ++i) {
            paintPlayer(g, game.getPlayer(countInc(count)), tlx, tly + h/sideCount * i, w, h/sideCount);
        }
    }

    private int countInc(int[] count) {
        return ++count[0]%game.getPlayerCount();
    }

    private void paintPlayer(Graphics g, Player p, int tlx, int tly, int w, int h) {
        int cardOffset = 10;
        int cardMove = 30;
        //paint name and border
        g.setFont(p.equals(game.getPlayer(playerNumber))? mainNameFont : nameFont);
        g.setColor(p.equals(game.getPlayer(game.getCurrentPlayerNumber()))? Color.BLUE : Color.BLACK);
        g.drawRect(tlx, tly, w, h);
        g.drawString(p.getName(), tlx, tly);

        //paint hand
        tlx += cardMove;
        tly += cardOffset;
        g.setFont(p.equals(game.getPlayer(game.getCurrentPlayerNumber()))? mainCardFont : cardFont);
        List<CARD> hand = p.getHand();
        for (int i = 0; i < hand.size(); ++i) {
            paintCard(g, hand.get(i), tlx + cardMove*i, tly);
        }

        //paint top cards
        tlx += (hand.size()-1)*CARD_X + cardMove;
        List<CARD> top = p.getTop();
        for (int i = 0; i < hand.size(); ++i) {
            paintCard(g, top.get(i), tlx + cardMove*i, tly);
        }

        //paint bottom cards
        tly += CARD_Y + cardOffset;
        List<CARD> bot = p.getBot();
        for (int i = 0; i < hand.size(); ++i) {
            paintCard(g, bot.get(i), tlx + cardMove*i, tly);
        }
    }

    private void paintCard(Graphics g, CARD card, int tlx, int tly) {
        g.drawImage(card.getImage(), tlx, tly, null);
    }

    enum SIDE {
        LEFT(0, .4, .3, .5), TOP(0, .03, 1, .3), RIGHT(.7, .4, .3, .5), BOTTOM(.3, .7, .4,.3);

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
