package kaz.idiot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
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

    /**
     * All painting methods, including paintComponent()
     *
     */
    // <editor-fold desc="Painting methods">

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        paintPlayers(g);
        paintDeck(g);
        paintField(g);
        paintRotating(g);
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
        boolean isPlayerMain = p.equals(game.getPlayer(playerNumber));
        Font playerNameFont = isPlayerMain? mainNameFont : nameFont;
        Color playerColor = isPlayerMain? Color.BLUE : Color.BLACK;

        int playerNameYOffset = playerNameFont.getSize();
        int playerNameXOffset = 10;
        int cardYOffset = 40;
        int cardXOffset = 30;

        //paint border
        g.setColor(playerColor);
        g.drawRect(tlx, tly, w, h);

        //paint name
        tlx += playerNameXOffset;
        tly += playerNameYOffset;
        g.setFont(playerNameFont);
        g.drawString(p.getName(), tlx, tly);

        //paint hand
        tly += cardYOffset;
        List<CARD> hand = p.getHand();
        for (int i = 0; i < hand.size(); ++i)
            paintCard(g, hand.get(i), tlx + cardXOffset*i, tly);

        //paint bot cards
        tly += cardYOffset;
        tlx += 3* cardXOffset + CARD_X;

        List<CARD> bot = p.getBot();
        for (int i = 0; i < bot.size(); ++i)
            paintCard(g, CARD.NULL_CARD, tlx + cardXOffset * i, tly);

        //paint top cards
        tly -= cardYOffset;
        List<CARD> top = p.getTop();
        for (int i = 0; i < top.size(); ++i) {
            paintCard(g, top.get(i), tlx + cardXOffset*i, tly);
        }
    }

    private void paintRotating(Graphics g) {
        Image image;
        try {
            File file = new File((game.isRotatingRight())? "right.png" : "left.png");
            image = ImageIO.read(file);
            g.drawImage(image, getWidth()/2 - 50, (int)(SIDE.RIGHT.tly * getWidth()), null);
        } catch(IOException e) {
            System.err.println("Missing card images.");
            e.printStackTrace();
        }
    }

    private void paintField(Graphics g) {
        int tlx = (int) (getWidth()/2 - 2*CARD_X);
        int tly = (int) (SIDE.RIGHT.tly * getHeight());
        if ((game.getField().size() > 0))
            for (int i = game.getField().size() - 1; i >=0 ; --i)
                paintCard(g, game.getField().get(i), tlx +2*i, tly+2*i);

    }

    private void paintDeck(Graphics g) {
        int tlx = (int) (getWidth()/2 + CARD_X);
        int tly = (int) (SIDE.LEFT.tly * getHeight());
        for (int i = 0; i <game.getDeck().size() && i < 60 ; ++i)
            paintCard(g, CARD.NULL_CARD, tlx, tly+4*i);
        //TODO: for loop to show size
    }

    private void paintCard(Graphics g, CARD card, int tlx, int tly) {
        g.drawImage(card.getImage(), tlx, tly, null);
    }

    // </editor-fold>

    enum SIDE {
        LEFT(0, .33, .3, .5), TOP(0, .03, 1, .27), RIGHT(.7, .4, .3, .5), BOTTOM(.3, .7, .4, .3);

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

    public Rectangle getRectofSide(SIDE s) {
        return new Rectangle(
                (int)(s.tlx * getWidth()),
                (int)(s.tly * getHeight()),
                (int)(s.dx * getWidth()),
                (int)(s.dy * getHeight())
        );
    }

    /**
     * Handles clicking and interaction.
     */
    // <editor-fold desc="Interaction methods">

    public void handleMouseEvent(MouseEvent me) {

    }

    public void handleClickRight(MouseEvent me) {

    }

    public void handleClickLeft(MouseEvent me) {

    }

    public void handleClickTop(MouseEvent me) {

    }

    public void handleClickBottom(MouseEvent me) {

    }

    public void handleClickMiddle(MouseEvent me) {

    }
    // </editor-fold>

}
