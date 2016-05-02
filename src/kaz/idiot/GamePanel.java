package kaz.idiot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static kaz.idiot.Player.HandCARD;
import static kaz.idiot.Main.*;


/**
 * Created by Kasra Sadeghi on 2/6/2016.
 */

//THE VIEW
@SuppressWarnings("Duplicates")
class GamePanel extends JPanel {
    private Game game;
    private int playerNumber;
    private int startWidth, startHeight;
    private int inspection = INSP_GAME;

    public static final int INSP_GAME = -1;
    public static final int INSP_MIDDLE = -2;
    public static final int INSP_EVENT = -3;
    public static final int INSP_CHAT = -4;

    private Font mainNameFont, nameFont;
    private Color overGrey = new Color(0, 0, 0, 120);
    private Color bg = new Color(255, 255, 255);

    private ChatPanel chatPanel;
    private EventLogPanel eventLogPanel;

    private static Image left;
    private static Image right;


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

        public Bounds(int tlx, int tly, int w, int h, int wid, int hei) {
            this.tlx = (double)tlx/wid;
            this.tly = (double)tly/hei;
            this.w = (double)w/wid;
            this.h = (double)h/hei;
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
        this.startWidth = w;
        this.startHeight = h;

        ClassLoader cl = this.getClass().getClassLoader();
        try {
            left = ImageIO.read(cl.getResource("left.png"));
            right = ImageIO.read(cl.getResource("right.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setMinimumSize(new Dimension(startWidth, startHeight));

        chatPanel = new ChatPanel();
        eventLogPanel = new EventLogPanel();
        chatPanel.setVisible(false);
        eventLogPanel.setVisible(false);
        add(chatPanel);
        add(eventLogPanel);

        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {
                handleMouseEvent(mouseEvent);
            }
            public void mousePressed(MouseEvent mouseEvent) {}
            public void mouseReleased(MouseEvent mouseEvent) {}
            public void mouseEntered(MouseEvent mouseEvent) {}
            public void mouseExited(MouseEvent mouseEvent) {}
        });
    }

    public class ChatPanel extends JPanel {
        public ChatPanel() {
            //#server TODO: once done with a chat panel or a console or whatever, see if read user input "\n" spawns a new line
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(bg);
            paintInspection(g);
        }
    }

    private class EventLogPanel extends JPanel {
        private JScrollPane scrollPane;
        private JTextArea eventLog;
        private EventLogPanel() {
            setBackground(bg);
            setBorder(null);


            eventLog = new JTextArea();
            eventLog.setEditable(false);

            eventLog.setBackground(bg);
            eventLog.setBorder(BorderFactory.createTitledBorder("Event Log"));
            eventLog.setText("Game Start! \n");

            scrollPane = new JScrollPane(eventLog);
            resizeScrollPane();
            scrollPane.setBackground(bg);
            add(scrollPane);
        }

        public void resizeScrollPane() {
            eventLog.setMargin(new Insets(0, 0, 0, 0));
            scrollPane.setBounds(0, 0, this.getWidth(), this.getHeight());
            eventLog.setBounds(0, 0, this.getWidth(), this.getHeight());
        }

        public void println(String text) {
            eventLog.append(text + "\n");
        }

        public void setCaretAtEnd() {
            eventLog.setCaretPosition(eventLog.getDocument().getLength());
            //#server TODO: maintain the caret at last line, or switch it so that new input comes at the beginning.
            // i'd prefer the former
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //#server TODO: work on graphics for the event log
            paintInspection(g);
        }
    }

    /**
     * Sets the preferred size for the window to the dimensions it was constructed with.
     * @return
     */
    public Dimension getPreferredSize() {
        return new Dimension(startWidth, startHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        mainNameFont = new Font("SansSerif", Font.PLAIN, getHeight()/36); // 1440/48 = 30, and 30 is the default size.
        nameFont = new Font("SansSerif", Font.BOLD, getHeight()/60); // 1440/60 = 24, and 24 is the default size.

        setBackground(bg);
        bounds2String.clear();
        switch (game.getState()) {
            case SETUP:
                paintSetup(g);
                break;
            case PLAYING:
                updatePanels();
                paintGame(g);
                break;
        }
    }

    private void updatePanels() {
        eventLogPanel.resizeScrollPane();
        bounds2String.put(SIDE.MIDDLE.getBounds(), "box " + INSP_MIDDLE);
        bounds2String.put(SIDE.CHAT.getBounds(), "box " + INSP_CHAT);
        bounds2String.put(SIDE.EVENT.getBounds(), "box " + INSP_EVENT);

        chatPanel.setVisible(true);
        eventLogPanel.setVisible(true);
        chatPanel.setBounds(sideRect(SIDE.CHAT));
        eventLogPanel.setBounds(sideRect(SIDE.EVENT));
        eventLogPanel.setCaretAtEnd();
    }

    public void printEvent(String text) {
        eventLogPanel.println(text);
    }

    // <editor-fold desc="----Painting methods----">

    private void paintSetup(Graphics g) {
        Player me = game.getPlayer(playerNumber);
        inspection = playerNumber;
        paintInspection(g);
        paintSetupButtons(g);
        if (me.isReady()) {
            g.setColor(new Color(255, 255, 0, 80));
            g.fillRect(getWidth()/4, getHeight()/4, getWidth()/2, getHeight()/2);
        }
    }

    private void paintSetupButtons(Graphics g) {
        Color front = Color.BLACK;
        Color back = bg;
        int buttonW = getWidth()/20;
        int buttonH = getHeight()/30;
        int tlx = getWidth()*3/4 - 20 - buttonW;
        int tly = getHeight()/4 + 20;

        paintClickableButton(g, "SWAP", tlx, tly, buttonW, buttonH, back, front);
        paintClickableButton(g, "READY", tlx, tly += buttonH + 20, buttonW, buttonH, back, front);
    }

    /**
     * Adds the middle, the chat area, and the event log to the bounds HashMap.
     * Then paints the following:
     *      the panels
     *      players
     *      deck
     *      field
     *      rotating symbol
     *      game buttons
     *      and if we're inspecting something
     *          the inspection
     *
     * @param g
     */
    private void paintGame(Graphics g) {
//        paintAllSides(g);

        paintPlayers(g);
        paintDeck(g);
        paintField(g);
        paintDiscard(g);
        paintRotation(g);
        paintGameButtons(g);
        if(inspection != -1)
            paintInspection(g);
        if(game.checkRoundOver())
            paintGameOver(g);
    }

    private void paintGameOver(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        g.setColor(Color.RED);
        g.fillRect(w/4, h/4, w/2, h/2);
        g.setColor(Color.WHITE);
        Font oldFont = g.getFont();
        g.setFont(mainNameFont);
        String s = "GAME OVER";
        int tlx = w/2 - SwingUtilities.computeStringWidth(g.getFontMetrics(), s)/2;
        int tly = h/4 + h/6 + g.getFontMetrics().getHeight()*2/7;
        g.drawString(s, tlx, tly);
        int margin = w/10;
        int buttonHeight = h/12;
        int buttonWidth = w/6;
        int ltlx = w/2 - margin/2 - buttonWidth;
        int btly = h/4 + h/3 - buttonHeight/2;
        int rtlx = w/2 + margin/2;
        g.setFont(oldFont);
        paintClickableButton(g, "HOST_REMATCH",
                ltlx, btly, buttonWidth, buttonHeight,
                bg, Color.BLACK);
        paintClickableButton(g, "RETURN_TO_MAIN_MENU",
                rtlx, btly, buttonWidth, buttonHeight,
                bg, Color.BLACK);
    }

    /**
     * Paints everything dealing with inspection.
     *
     * @param g
     */
    private void paintInspection(Graphics g) {
        switch (inspection) {
            case INSP_GAME:
                //paint game
                break;
            case INSP_MIDDLE:
                paintMiddleInspection(g);
                break;
            case INSP_CHAT:
                paintChatInspection(g);
                break;
            case INSP_EVENT:
                paintEventInspection(g);
                break;
            default:
                paintPlayerInspection(g);
                break;
        }
    }


    private void paintEventInspection(Graphics g) {
        //#devmode 1 TODO: console
    }

    private void paintChatInspection(Graphics g) {
        //#server TODO: chat
    }


    private void paintMiddleInspection(Graphics g) {
        g.setColor(overGrey);
        g.fillRect(0, 0, getWidth(), getHeight());

        int w = getWidth();
        int h = getHeight();

        g.setColor(bg);
        g.fillRect(w/4, h/4, w/2, h/2);
        //paint deck, field, rotation, discard
        paintDeck(g);
        paintRotation(g);
        paintDiscard(g);
        paintField(g);
    }

    /**
     * Greys out the screen and highlights the player in the middle of the screen.
     *
     * @param g
     */
    private void paintPlayerInspection(Graphics g) {
        g.setColor(overGrey);
        g.fillRect(0, 0, getWidth(), getHeight());
        paintPlayer(g, game.getPlayer(inspection), getWidth()/4, getHeight()/4, getWidth()/2, getHeight()/2);
    }

    private void paintGameButtons(Graphics g) {
        Color front = Color.BLACK;
        Color back = bg;
        int tlx = (int) ((SIDE.BOTTOM.tlx + SIDE.BOTTOM.dx)*getWidth());
        int tly = (int) ((SIDE.BOTTOM.tly)*getHeight());
        int w = getWidth()/20;
        int h = getHeight()/30;
        tly += 1;
        tlx -= w + 3;
        if (game.canPlay())
            paintClickableButton(g, "PLAY", tlx, tly, w, h, back, front);
        else paintClickableButton(g, "PICKUP", tlx, tly, w, h, back, front);
    }

    /**
     * Paints a button and gives it an action
     *
     * @param g - graphics object of this pane.
     * @param s - string of the name that's going to be put on the button and the name of the action code.
     * @param tlx - top left x-coordinate.
     * @param tly - top left y-coordinate.
     * @param w - startWidth.
     * @param h - startHeight.
     * @param back - background color for the button.
     * @param front - foreground color for the button. The text and the border are this color.
     */
    private void paintClickableButton(Graphics g, String s, int tlx, int tly, int w, int h, Color back, Color front) {
        g.setColor(back);
        g.fillRect(tlx, tly, w, h);
        g.setColor(front);
        g.drawRect(tlx, tly, w, h);
        Bounds bounds = new Bounds(tlx, tly, w, h, getWidth(), getHeight());
        addActionToBounds(bounds, s);
        s = s.replace('_',' ');
        tlx = tlx + w/2 - SwingUtilities.computeStringWidth(g.getFontMetrics(), s)/2;
        tly = tly + h/2 + g.getFontMetrics().getHeight()*2/7;
        g.drawString(s, tlx, tly);
    }

    /**
     * Testing function for moving around the dimensions of the SIDE enum.
     *
     * @param g
     */
    private void paintAllSides(Graphics g) {
        for(SIDE side : SIDE.values()) {
            g.setColor(Color.RED);
            Bounds b = side.getBounds();
            g.drawRect((int)(b.tlx * getWidth()),
                    (int)(b.tly * getHeight()),
                    (int)(b.w * getWidth()),
                    (int)(b.h * getHeight()));
            g.drawString(side.name(), (int)(b.tlx * getWidth()), (int)(b.tly * getHeight()) + 20);
        }
    }

    private void paintPlayers(Graphics g) {
        paintMainPlayer(g);
        paintSidePlayers(g);
    }

    private void paintMainPlayer(Graphics g) {
        Rectangle bounding = sideRect(SIDE.BOTTOM);
        bounding.x += 1;
        bounding.width -= 3;
        Bounds bounds = SIDE.BOTTOM.getBounds();
        paintPlayer(g, playerNumber, bounding);
        addPlayerToBounds(bounds, playerNumber);
    }

    /**
     * Paints players other than the main player on the pane.
     *
     * @param g
     */
    private void paintSidePlayers(Graphics g) {
        int count =( playerNumber + 1 )%game.getPlayerCount();
        int sideCount = (game.getPlayerCount()-1)/3;
        int topCount = game.getPlayerCount() - 1 - sideCount * 2;
        int tlx, tly, dx, dy;
        SIDE side;

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
     * @param w - startWidth
     * @param h - startHeight
     */
    private void paintPlayer(Graphics g, int num, int tlx, int tly, int w, int h) {
        Player p = game.getPlayer(num);
        paintPlayer(g, p, tlx, tly, w, h);
    }

    /**
     * Paints a player with dimensions.
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
        Stroke ogStroke = g.getStroke();
        Font playerNameFont = isPlayerMain? mainNameFont : nameFont;
        Color playerColor = isPlayerMain? Color.BLUE : Color.BLACK;
        if (isCurrentPlayer && game.getState() != STATE.SETUP) {
            playerColor = Color.RED;
            g.setStroke(new BasicStroke(3));
        }

        int playerNameYOffset = playerNameFont.getSize();
        int playerNameXOffset = 10;
        int cardYOffset = 20;
        int cardXOffset = 30;
        int selectedYOffset = -20;
        if (game.getState() == STATE.SETUP) {
            cardXOffset += CARD_X;
            cardYOffset += CARD_Y;
        }

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
        if (game.getState() == STATE.SETUP) {
            tlx -= 2 * CARD_X + cardYOffset/4;
        }
        List<CARD> bot = p.getBot();
        for (int i = 0; i < bot.size(); ++i) {
            if (bot.get(i) != CARD.NULL_CARD) {
                if (p.getState() != STATE.EPICMODE)
                    paintCard(og, CARD.NULL_CARD, tlx + cardXOffset * i, tly);
                else
                    paintCard(og, CARD.NULL_CARD, otlx + ow / 3 * i - CARD_X / 2 + ow / 6, otly + oh / 2 - CARD_Y / 2);
            }
        }
        if (p.getState() == STATE.EPICMODE)
        {
            Color front = Color.BLACK;
            Color back = bg;
            int btly = otly + oh/2 + CARD_Y;
            int bw = getWidth()/20;
            int bh = getHeight()/30;
            int i = 0;
            int btlx = otlx + ow/6 - bw/2 + ow/3 * i;
            if (bot.get(0) != CARD.NULL_CARD)
                paintClickableButton(og, "LEFT", btlx, btly, bw, bh, back, front);
            i = 1;
            btlx = otlx + ow/6 - bw/2 + ow/3 * i;
            if (bot.get(1) != CARD.NULL_CARD)
                paintClickableButton(og, "CENTER", btlx, btly, bw, bh, back, front);
            i = 2;
            btlx = otlx + ow/6 - bw/2 + ow/3 * i;
            if (bot.get(2) != CARD.NULL_CARD)
                paintClickableButton(og, "RIGHT", btlx, btly, bw, bh, back, front);
        }

        //paint top cards
        tly -= cardYOffset;
        List<CARD> top = p.getTop();
        for (int i = 0; i < top.size(); ++i) {
            paintCard(og, top.get(i), tlx + cardXOffset*i, tly);
            if (game.getState() == STATE.SETUP) {
                Player me = game.getPlayer(playerNumber);
                if (i == me.getTopSetupSelect()) {
                    Stroke s = g.getStroke();
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.ORANGE);
                    g.drawRect(tlx + cardXOffset * i - 1, tly - 1, CARD_X + 2, CARD_Y + 2);
                    g.setColor(playerColor);
                    g.setStroke(s);
                }
                Bounds bounds = new Bounds(
                        (double)(tlx + cardXOffset * i)/getWidth(),
                        (double)(tly)/getHeight(),
                        (i == top.size() - 1)? (double)CARD_X/getWidth() : (double)cardXOffset/getWidth(),
                        (double)CARD_Y/getHeight()
                );
                addCardToBounds(bounds, i + 3);
            }

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
            if (game.getState() == STATE.SETUP) {
                Player me = game.getPlayer(playerNumber);
                if (i == me.getHandSetupSelect()) {
                    Stroke s = g.getStroke();
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.ORANGE);
                    g.drawRect(tlx + cardXOffset * i - 1, tly - 1, CARD_X + 2, CARD_Y + 2);
                    g.setColor(playerColor);
                    g.setStroke(s);
                }
            }
            addCardToBounds(bounds, i);
        }

        g.setStroke(ogStroke);
    }

    /**
     * Paints the rotating symbol in the middle of the game.
     * @param g
     */
    private void paintRotation(Graphics g) {
        Image image = (game.isRotatingRight())? right : left;
        g.drawImage(image, getWidth()/2 - 50, getHeight()/2 -50, null);
    }

    /**
     * Paints the active field of cards
     * @param g
     */
    private void paintField(Graphics g) {
        int tlx = getWidth()/2 - 2*CARD_X;
        int tly = getHeight()/2 - CARD_Y/2 - 10;

        int delta = 20;
        for (int i = 0; i < game.getField().size() ; ++i) {
            if (i > game.getField().size() - 10)
                paintCard(g, game.getField().get(i), tlx + delta*i - game.getField().size()*delta, tly);
        }
    }

    /**
     * Paints the discard stack.
     *
     * @param g
     */
    private void paintDiscard(Graphics g) {
        int tlx = (getWidth()/2 + CARD_X);
        int tly = getHeight()/2 - CARD_Y/2 - 10;
        if (!game.getDiscard().isEmpty())
            paintCard(g, CARD.NULL_CARD, tlx, tly);
    }

    /**
     * Paints the deck of cards.
     * @param g
     */
    private void paintDeck(Graphics g) {
        int tlx = getWidth()/2 + CARD_X;
        int tly = getHeight()/2 + CARD_Y/2;
        for (int i = 0; i <game.getDeck().size() && i < 60 ; ++i)
            paintCard(g, CARD.NULL_CARD, tlx-4*i, tly);
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
        Collections.sort(codes);
        controller[playerNumber].handleCodes(codes);
        controller[playerNumber].println(playerNumber + ": " + codes);
    }

    public void setInspection( int box ) {
        inspection = box;
    }

    public boolean isInspecting() {
        return inspection != INSP_GAME;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    // </editor-fold>

}
