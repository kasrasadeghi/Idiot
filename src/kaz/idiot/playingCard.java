package kaz.idiot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kasra on 2/6/2016.
 *
 * This is the proprietary card class for Card Games.
 * This card class is not actually used in the Idiot project.
 *
 * Changelog:
 *      1.0 - copied CARD from idiot, added comments, and took out functions specific to Idiot.
 *      2.0 - updated constructor to be independent of the idiot game and implemented drawCard
 */
public enum PlayingCard {
    SPADE_A(0, 3),
    SPADE_2(1, 3),
    SPADE_3(2, 3),
    SPADE_4(3, 3),
    SPADE_5(4, 3),
    SPADE_6(5, 3),
    SPADE_7(6, 3),
    SPADE_8(7, 3),
    SPADE_9(8, 3),
    SPADE_10(9, 3),
    SPADE_J(10, 3),
    SPADE_Q(11, 3),
    SPADE_K(12, 3),

    CLUB_A(0, 0),
    CLUB_2(1, 0),
    CLUB_3(2, 0),
    CLUB_4(3, 0),
    CLUB_5(4, 0),
    CLUB_6(5, 0),
    CLUB_7(6, 0),
    CLUB_8(7, 0),
    CLUB_9(8, 0),
    CLUB_10(9, 0),
    CLUB_J(10, 0),
    CLUB_Q(11, 0),
    CLUB_K(12, 0),

    DIAMOND_A(0, 1),
    DIAMOND_2(1, 1),
    DIAMOND_3(2, 1),
    DIAMOND_4(3, 1),
    DIAMOND_5(4, 1),
    DIAMOND_6(5, 1),
    DIAMOND_7(6, 1),
    DIAMOND_8(7, 1),
    DIAMOND_9(8, 1),
    DIAMOND_10(9, 1),
    DIAMOND_J(10, 1),
    DIAMOND_Q(11, 1),
    DIAMOND_K(12, 1),

    HEART_A(0, 2),
    HEART_2(1, 2),
    HEART_3(2, 2),
    HEART_4(3, 2),
    HEART_5(4, 2),
    HEART_6(5, 2),
    HEART_7(6, 2),
    HEART_8(7, 2),
    HEART_9(8, 2),
    HEART_10(9, 2),
    HEART_J(10, 2),
    HEART_Q(11, 2),
    HEART_K(12, 2),

    NULL_CARD(2, 4);

    private Image image = null;

    public static final int CARD_X = 79;
    public static final int CARD_Y = 123;

    PlayingCard(int tlx, int tly) {
        try {
            image = ImageIO.read(new File("cards.png")).getSubimage(tlx * CARD_X, tly * CARD_Y, CARD_X, CARD_Y);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return a random card that isn't NULL_CARD;
     */
    public static PlayingCard random() {
        return values()[(int)(Math.random() * values().length - 1)];
    }

    /**
     * @return a list of a normal deck of playing cards. not shuffled.
     */
    public static List<PlayingCard> fullDeck() {
        return Arrays.asList(values()).subList(0, values().length - 1);
    }

    /**
     *
     * @return the name
     */
    @Override
    public String toString() {
        return name();
    }

    /**
     *
     * @return graphics image for use with Graphics.drawImage(image, tlx, tly, null);
     */
    public Image getImage() {
        return image;
    }

    /**
     * Draws this card to a graphics object.
     * Usage Ex.:
     *  CARD.NULL_CARD.drawCard(g, 100, 100);
     * @param g the graphics object to be draw to
     * @param tlx the top-left x-coordinate of the card
     * @param tly the top-left y-coordinate of the card
     */
    public void drawCard(Graphics g, int tlx, int tly) { g.drawImage(image, tlx, tly, null); }

    /**
     *
     * @return the suit of the card: SPADE, CLUB, DIAMOND, HEART
     */
    public String getSuit() {
        return name().split("_")[0];
    }

    /**
     *
     * @return the rank of the card: A, 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K
     */
    public String getRank() {
        return name().split("_")[1];
    }
}
