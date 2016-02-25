package kaz.idiot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import static kaz.idiot.Main.*;

/**
 * Created by kasra on 2/6/2016.
 */
public enum CARD{
    SPADE_A(0*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_2(1*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_3(2*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_4(3*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_5(4*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_6(5*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_7(6*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_8(7*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_9(8*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_10(9*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_J(10*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_Q(11*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),
    SPADE_K(12*CARD_X, 3*CARD_Y, CARD_X, CARD_Y),

    CLUB_A(0*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_2(1*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_3(2*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_4(3*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_5(4*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_6(5*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_7(6*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_8(7*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_9(8*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_10(9*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_J(10*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_Q(11*CARD_X, 0, CARD_X, CARD_Y),
    CLUB_K(12*CARD_X, 0, CARD_X, CARD_Y),

    DIAMOND_A(0*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_2(1*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_3(2*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_4(3*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_5(4*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_6(5*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_7(6*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_8(7*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_9(8*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_10(9*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_J(10*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_Q(11*CARD_X, CARD_Y, CARD_X, CARD_Y),
    DIAMOND_K(12*CARD_X, CARD_Y, CARD_X, CARD_Y),

    HEART_A(0*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_2(1*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_3(2*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_4(3*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_5(4*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_6(5*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_7(6*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_8(7*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_9(8*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_10(9*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_J(10*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_Q(11*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),
    HEART_K(12*CARD_X, 2*CARD_Y, CARD_X, CARD_Y),

    NULL_CARD(2 * CARD_X, 4 * CARD_Y, CARD_X, CARD_Y);

    private Image image = null;


    CARD(int tlx, int tly, int w, int h) {
        try {
            image = ImageIO.read(new File("cards.png")).getSubimage(tlx, tly, w, h);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return a random card that isn't NULL_CARD;
     */
    public static CARD random() {
        return values()[(int)(Math.random() * values().length - 1)];
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

    public static Comparator<CARD> moveComp() {
        return (card1, card2) -> {
            int comp = card1.getRankValue() - card2.getRankValue();
            return comp;
        };
    }
}
