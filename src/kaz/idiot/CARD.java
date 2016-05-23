package kaz.idiot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by kasra on 2/6/2016.
 *
 * This is the proprietary card class for Idiot.
 */
public enum CARD{
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

    DIAMOND_A(0,1),
    DIAMOND_2(1,1),
    DIAMOND_3(2,1),
    DIAMOND_4(3,1),
    DIAMOND_5(4,1),
    DIAMOND_6(5,1),
    DIAMOND_7(6,1),
    DIAMOND_8(7,1),
    DIAMOND_9(8,1),
    DIAMOND_10(9,1),
    DIAMOND_J(10,1),
    DIAMOND_Q(11,1),
    DIAMOND_K(12,1 ),

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

    NULL_CARD(2, 4 );

    public static final int CARD_Y = 123;
    public static final int CARD_X = 79;

    private Image image = null;

    CARD(int tlx, int tly) {
        try {
            //#new TODO: Read image from disk once, then make subImages. Maybe look into cached images?
            ClassLoader cl = this.getClass().getClassLoader();
            image = ImageIO.read(cl.getResource("cards.png")).getSubimage(tlx*CARD_X, tly*CARD_Y, CARD_X, CARD_Y);
        } catch(IOException e) {
            System.err.println("Could not find the cards.png. Check the working directory of the jar file.");
        }
    }

    /**
     * @return a list of a normal deck of playing cards. not shuffled.
     */
    public static List<CARD> fullDeck() {
        return Arrays.asList(values()).subList(0, values().length - 1);
    }

    @Override
    public String toString() {
        return name();
    }

    public Image getImage() {
        return image;
    }

    public String getSuit() {
        return name().split("_")[0];
    }

    public int getSuitValue() {
        switch(getSuit()) {
            case "SPADE": return 1;
            case "CLUB": return 2;
            case "DIAMOND": return 3;
            case "HEART": return 4;
            default: return 0;
        }
    }

    public String getRank() {
        return name().split("_")[1];
    }

    public int getRankValue() {
        switch(getRank()) {
            case "A": return 14;
            case "2": return 30;
            case "3": return 3;
            case "4": return 4;
            case "5": return 5;
            case "6": return 6;
            case "7": return 50;
            case "8": return 8;
            case "9": return 9;
            case "10": return 100;
            case "J": return 11;
            case "Q": return 12;
            case "K": return 13;
            default: return 0;
        }
    }

    public static Comparator<CARD> getComp() {
        return (card1, card2) -> {
            int comp = card1.getRankValue() - card2.getRankValue();
            if (comp == 0) comp = card1.getSuitValue() - card2.getSuitValue();
            return comp;
        };
    }
}
