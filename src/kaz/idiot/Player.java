package kaz.idiot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static kaz.idiot.CARD.*;
/**
 * Created by kasra on 2/6/2016.
 */
public class Player {
    private boolean isPlaying;
    private List<CARD> bot;
    private List<CARD> top;
    private List<CARD> hand;
    private String name;

    public Player(List<CARD> stack9, String name){
        isPlaying = true;

        bot = new ArrayList<>();
        bot.addAll(stack9.subList(0, 3));

        top = new ArrayList<>();
        top.addAll(stack9.subList(3, 6));

        hand = new LinkedList<>();
        hand.addAll(stack9.subList(6, 9));

        this.name = name;
    }

    public void start() {
        isPlaying = true;
    }

    public void end() {
        isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getName() {
        return name;
    }

    public List<CARD> getBot() {
        return bot;
    }

    public List<CARD> getTop() {
        return top;
    }

    public List<CARD> getHand() {
        return hand;
    }

    public void draw(CARD c) {
        hand.add(c);
    }

    public void pickUp(List<CARD> cards) {
        hand.addAll(cards);
    }

    public void removeToHand(List<CARD> cards) {
        for (int i = 0; i < cards.size(); i++) {
            hand.add(cards.remove(i));
        }
    }

    public CARD play(int i) {
        return hand.remove(i);
    }

    public void topToHand() {
        removeToHand(top);
    }

    public void botToHand() {
        removeToHand(bot);
    }
}
