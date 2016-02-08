package kaz.idiot;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kasra on 2/6/2016.
 */
public enum CARD {
    SPADE_A,
    SPADE_2,
    SPADE_3,
    SPADE_4,
    SPADE_5,
    SPADE_6,
    SPADE_7,
    SPADE_8,
    SPADE_9,
    SPADE_10,
    SPADE_J,
    SPADE_Q,
    SPADE_K,

    CLUB_A,
    CLUB_2,
    CLUB_3,
    CLUB_4,
    CLUB_5,
    CLUB_6,
    CLUB_7,
    CLUB_8,
    CLUB_9,
    CLUB_10,
    CLUB_J,
    CLUB_Q,
    CLUB_K,

    DIAMOND_A,
    DIAMOND_2,
    DIAMOND_3,
    DIAMOND_4,
    DIAMOND_5,
    DIAMOND_6,
    DIAMOND_7,
    DIAMOND_8,
    DIAMOND_9,
    DIAMOND_10,
    DIAMOND_J,
    DIAMOND_Q,
    DIAMOND_K,

    HEART_A,
    HEART_2,
    HEART_3,
    HEART_4,
    HEART_5,
    HEART_6,
    HEART_7,
    HEART_8,
    HEART_9,
    HEART_10,
    HEART_J,
    HEART_Q,
    HEART_K,

    NULL_CARD;

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

    public static void Shuffle() {
        //TODO: implement shuffle
    }

    @Override
    public String toString() {
        return name();
    }
}
