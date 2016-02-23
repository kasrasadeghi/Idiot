package kaz.idiot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static kaz.idiot.Player.HandCARD;
import static kaz.idiot.Main.*;


/**
 * Created by kasra on 2/6/2016.
 */

//THE VIEW
public class GamePanel extends JPanel {
    private Game game;
    private int playerNumber;
    private int width, height;
    private int inspection = -1;

    private final int INSP_MIDDLE = -2;
    private final int INSP_CHAT = -3;
    private final int INSP_EVENT = -4;

    private Font mainNameFont = new Font("SansSerif", Font.PLAIN, 30);
    private Font nameFont = new Font("SansSerif", Font.PLAIN, 22);
    private Color overGrey = new Color(0, 0, 0, 120);
    private Color bg = new Color(255, 255, 255);

    //<editor-fold desc="----Bounds----">

    private HashMap<Bounds, String> bounds2String = new HashMap<>();

    private void addPlayerToBounds(Bounds b, int count) {
        bounds2String.put(b, "box " + count);
    }

    private void addCardToBounds(Bounds b, int num) {
        bounds2String.put(b, "card " + num);
    }

    private void addActionToBounds(Bounds b, String code) {
        bounds2String.put(b, "action " + code);
    }

    static class Bounds {
        double tlx, tly, w, h;

        public Bounds(double tlx, double tly, double w, double h) {
            this.tlx = tlx;
            this.tly = tly;
            this.w = w;
            this.h = h;
        }

        public String toString() {
            return "[ " + tlx + ", " + tly + ": " + w + ", " + h + " ]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Bounds)) return false;

            Bounds bounds = (Bounds) o;

            if (Double.compare(bounds.tlx, tlx) != 0) return false;
            if (Double.compare(bounds.tly, tly) != 0) return false;
            if (Double.compare(bounds.w, w) != 0) return false;
            return Double.compare(bounds.h, h) == 0;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(tlx);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(tly);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(w);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(h);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    //</editor-fold>

    public GamePanel (Game game, int pn, int w, int h) {
        this.game = game;
        this.playerNumber = pn;
        this.width = w;
        this.height = h;

        setMinimumSize(new Dimension(width, height));

        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {
                handleMouseEvent(mouseEvent);
            }
            public void mousePressed(MouseEvent mouseEvent) {}
            public void mouseReleased(MouseEvent mouseEvent) {}
            public void mouseEntered(MouseEvent mouseEvent) {}
            public void mouseExited(MouseEvent mouseEvent) {}
        });
        //TODO: add a pane for an event log and a chat log
        //TODO: make (synchronized?) queue for handling input
    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    // <editor-fold desc="----Painting methods----">

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        paintAllSides(g);
        setBackground(bg);
        bounds2String.clear();
        bounds2String.put(SIDE.MIDDLE.getBounds(), "box " + INSP_MIDDLE);
        bounds2String.put(SIDE.CHAT.getBounds(), "box " + INSP_CHAT);
        bounds2String.put(SIDE.EVENT.getBounds(), "box " + INSP_EVENT);

        paintPlayers(g);
        paintDeck(g);
        paintField(g);
        paintRotating(g);
        paintButtons(g);
        if(inspection != -1)
            paintInspection(g);
    }

    private void paintInspection(Graphics g) {
        if (inspection > -1)
            paintPlayerInspection(g);
    }

    private void paintPlayerInspection(Graphics g) {
        g.setColor(overGrey);
        g.fillRect(0, 0, getWidth(), getHeight());
        paintPlayer(g, game.getPlayer(inspection), getWidth()/4, getHeight()/4, getWidth()/2, getHeight()/2);
    }

    private void paintButtons(Graphics g) {

    }

    private void paintPlay(Graphics g) {

    }

//    private void paint

    private void paintAllSides(Graphics g) {
        for(SIDE side : SIDE.values()) {
            g.setColor(Color.RED);
            Bounds b = side.getBounds();
            g.drawRect((int)(b.tlx * getWidth()),
                    (int)(b.tly * getHeight()),
                    (int)(b.w * getWidth()),
                    (int)(b.h * getHeight()));
            g.drawString(side.name(), (int)(b.tlx * getWidth()), (int)(b.tly * getHeight()) + 20);
//            sideRect(side)
//            g.drawRect();
        }
    }

    private void paintPlayers(Graphics g) {
        paintMainPlayer(g);
        paintSidePlayers(g);
    }

    private void paintMainPlayer(Graphics g) {
        Rectangle bounding = sideRect(SIDE.BOTTOM);
        Bounds bounds = SIDE.BOTTOM.getBounds();
        paintPlayer(g, playerNumber, bounding);
        addPlayerToBounds(bounds, playerNumber);
        //TODO: add stuff for playing selected cards
    }

    private void paintSidePlayers(Graphics g) {
        int count = playerNumber + 1;
        int sideCount = (game.getPlayerCount()-1)/3;
        int topCount = game.getPlayerCount() - 1 - sideCount * 2;
        int tlx, tly, dx, dy;
        SIDE side;

        //TODO: less copypasta please
        //      even the IDE is yelling at me about duplicates and I have no idea what to do
        //      FeelsGoodMan

        //paint right players
        side = SIDE.RIGHT;
        tlx = (int) (getWidth() * side.tlx);
        tly = (int) (getHeight() * side.tly);
        dx = (int) (getWidth() * side.dx);
        dy = (int) (getHeight() * side.dy);

        for (int i = sideCount - 1 ; i >= 0; --i, count = (count+1)%game.getPlayerCount()) {
            Rectangle bounding = new Rectangle( tlx, tly + dy/sideCount * i, dx, dy/sideCount);
            Bounds bounds = new Bounds (
                    (double)tlx/getWidth(),
                    (double)tly/getHeight() + (double)dy/(sideCount*getHeight() )* i,
                    (double)dx/getWidth(),
                    (double)dy/getHeight()/sideCount);
            addPlayerToBounds(bounds, count);
            paintPlayer(g, count, bounding);
        }

        //paint top players
        side = SIDE.TOP;
        tlx = (int) (getWidth() * side.tlx);
        tly = (int) (getHeight() * side.tly);
        dx = (int) (getWidth() * side.dx);
        dy = (int) (getHeight() * side.dy);

        for (int i = topCount - 1; i >= 0; --i, count = (count+1)%game.getPlayerCount()) {
            Rectangle bounding = new Rectangle( tlx + dx/topCount * i, tly, dx/topCount, dy);
            Bounds bounds = new Bounds (
                    (double)tlx/getWidth() + (double)dx/getWidth()/topCount * i,
                    (double)tly/getHeight(),
                    (double)dx/getWidth()/topCount,
                    (double)dy/getHeight());
            addPlayerToBounds(bounds, count);
            paintPlayer(g, count, bounding);
        }

        //paint left players
        side = SIDE.LEFT;
        tlx = (int) (getWidth() * side.tlx);
        tly = (int) (getHeight() * side.tly);
        dx = (int) (getWidth() * side.dx);
        dy = (int) (getHeight() * side.dy);

        for (int i = 0; i < sideCount; ++i, count = (count+1)%game.getPlayerCount()) {
            Rectangle bounding = new Rectangle( tlx, tly + dy/sideCount * i, dx, dy/sideCount);
            Bounds bounds = new Bounds(
                    (double)tlx/getWidth(),
                    (double)tly/getHeight() + (double)dy/getHeight()/sideCount * i,
                    (double)dx/getWidth(),
                    (double)dy/getHeight()/sideCount);
            addPlayerToBounds(bounds, count);
            paintPlayer(g, count, bounding);
        }
    }


    private void paintPlayer(Graphics g, int num, Rectangle rect) {
        paintPlayer(g, num, rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Calls other paintPlayer after getting the player with the correct number from the game.
     *
     * @param g - graphics object
     * @param num - number of painted player
     * @param tlx - top left x coordinate
     * @param tly - top left y coordinate
     * @param w - width
     * @param h - height
     */
    private void paintPlayer(Graphics g, int num, int tlx, int tly, int w, int h) {
        Player p = game.getPlayer(num);
        paintPlayer(g, p, tlx, tly, w, h);
    }

    /**
     * Paints a player with dimenions.
     *      draws a box
     *      paints the name
     *      paints the hand
     *      paint the top and bottom cards
     *
     * Color is red if it's currently this players turn otherwise
     * Color is blue if it's the player this GamePanel belongs to otherwise
     * Color is black.
     *
     * @param og
     * @param p
     * @param tlx
     * @param tly
     * @param w
     * @param h
     */
    private void paintPlayer(Graphics og, Player p, int tlx, int tly, int w, int h) {
        og.setColor(bg);
        og.fillRect(tlx, tly, w, h);
        boolean isPlayerMain = p.equals(game.getPlayer(playerNumber));
        boolean isCurrentPlayer = p.equals(game.getPlayer(game.getCurrentPlayerNumber()));

        Graphics2D g = (Graphics2D) og;

        Font playerNameFont = isPlayerMain? mainNameFont : nameFont;
        Color playerColor = isPlayerMain? Color.BLUE : Color.BLACK;
        if (isCurrentPlayer) {
            playerColor = Color.RED;
            g.setStroke(new BasicStroke(2));
        }

        int playerNameYOffset = playerNameFont.getSize();
        int playerNameXOffset = 10;
        int cardYOffset = 20;
        int cardXOffset = 30;
        int selectedYOffset = -20;

        int otlx = tlx;
        int otly = tly;
        int ow = w;
        int oh = h;

        //paint border
        og.setColor(playerColor);
        og.drawRect(tlx, tly, w, h);

        //paint name
        tlx += playerNameXOffset;
        tly += playerNameYOffset;
        og.setFont(playerNameFont);
        og.drawString(p.getName(), tlx, tly);

        //paint bot cards
        tly = otly + oh - CARD_Y - cardYOffset/2;
        tlx = otlx + ow - 2*CARD_X;
        List<CARD> bot = p.getBot();
        for (int i = 0; i < bot.size(); ++i) {
            paintCard(og, CARD.NULL_CARD, tlx + cardXOffset * i, tly);
        }
        //paint top cards
        tly -= cardYOffset;
        List<CARD> top = p.getTop();
        for (int i = 0; i < top.size(); ++i) {
            paintCard(og, top.get(i), tlx + cardXOffset*i, tly);
        }

        //paint hand
        tly = otly;
        tlx = otlx;
        tly += playerNameXOffset + 10;
        tlx += playerNameYOffset;
        List<HandCARD> hand = p.getHand();
        for (int i = 0; i < hand.size(); ++i) {
            HandCARD hc = hand.get(i);
            paintCard(og, hc.card, tlx + cardXOffset * i, tly + (hc.selected? selectedYOffset : 0));
            Bounds bounds = new Bounds(
                    (double)(tlx + cardXOffset * i)/getWidth(),
                    (double)(tly + (hc.selected? selectedYOffset : 0))/getHeight(),
                    (i == hand.size() - 1)? (double)CARD_X/getWidth() : (double)cardXOffset/getWidth(),
                    (double)CARD_Y/getHeight()
            );

            addCardToBounds(bounds, i);
        }

        if(isCurrentPlayer)
            g.setStroke(new BasicStroke(1));
    }

    /**
     * Paints the rotating symbol in the middle of the game.
     * @param g
     */
    private void paintRotating(Graphics g) {
        try {
            File file = new File((game.isRotatingRight())? "right.png" : "left.png");
            Image image = ImageIO.read(file);
            g.drawImage(image, getWidth()/2 - 50, (int)(SIDE.RIGHT.tly * getHeight() * 1.5), null);
        } catch(IOException e) {
            System.err.println("Missing card images.");
            e.printStackTrace();
        }
    }


    /**
     * Paints the active field of cards
     * @param g
     */
    private void paintField(Graphics g) {
        int tlx = (int) (getWidth()/2 - 2*CARD_X);
        int tly = (int) (SIDE.RIGHT.tly * getHeight());
            for (int i = game.getField().size() - 1; i >=0 ; --i)
                paintCard(g, game.getField().get(i), tlx +2*i, tly+2*i);

    }

    private void paintDeck(Graphics g) {
        int tlx = (int) (getWidth()/2 + CARD_X);
        int tly = (int) (SIDE.LEFT.tly * getHeight());
        for (int i = 0; i <game.getDeck().size() && i < 60 ; ++i)
            paintCard(g, CARD.NULL_CARD, tlx, tly+4*i);
        //TODO: fix appearance. maybe horizontal?
    }

    private void paintCard(Graphics g, CARD card, int tlx, int tly) {
        g.drawImage(card.getImage(), tlx, tly, null);
    }

    // </editor-fold>

    //<editor-fold desc="----SIDE stuff----">

    enum SIDE {
        //name   TLX TLY  DX  DY
        LEFT    (  0, .3, .3, .5),
        TOP     (  0,  0,  1, .3),
        RIGHT   ( .7, .3, .3, .5),
        BOTTOM  ( .3, .7, .4, .3),
        MIDDLE  ( .3, .3, .4, .4),
        EVENT   (  0, .8, .3, .2),
        CHAT    ( .7, .8, .3, .2)
        ;

        public double tlx, tly, dx, dy;

        SIDE(double tlx, double tly, double dx, double dy) {
            this.tlx = tlx;
            this.tly = tly;
            this.dx = dx;
            this.dy = dy;
        }

        Bounds getBounds() {
            return new Bounds(tlx, tly, dx, dy);
        }
    }

    private Rectangle sideRect(SIDE s) {
        return new Rectangle(
                (int)(s.tlx * getWidth()),
                (int)(s.tly * getHeight()),
                (int)(s.dx * getWidth()),
                (int)(s.dy * getHeight())
        );
    }

    //</editor-fold>

    // <editor-fold desc="----Interaction methods----">

    // just in case this is actually useful
    private SIDE findClickRegion(MouseEvent me) {
        for (SIDE side : SIDE.values())
            if (sideRect(side).contains(me.getPoint()))
                return side;
        return null;
    }

    public void handleMouseEvent(MouseEvent me) {

        List<String> codes = new ArrayList<>();
        for(Bounds bounds : bounds2String.keySet()) {
            Rectangle bounding = new Rectangle((int) (bounds.tlx * getWidth()),
                    (int) (bounds.tly * getHeight()),
                    (int) (bounds.w * getWidth()),
                    (int) (bounds.h * getHeight()));
            if (bounding.contains(me.getPoint()))
                codes.add(bounds2String.get(bounds));
        }
        System.out.println("\t" + codes);
        //TODO: actually handle actionCode
        controller.handleCodes(codes);

        //TODO: handle random rounding issue (goes towards the top left)
    }

    public void setInspection( int box ) {
        inspection = box;
    }

    public boolean isInspecting() {
        return inspection != -1;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    // </editor-fold>

}
