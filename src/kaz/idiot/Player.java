package kaz.idiot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static kaz.idiot.CARD.*;
/**
 * Created by kasra on 2/6/2016.
 */
public class Player {
    static class HandCARD {
        CARD card;
        boolean selected;

        public HandCARD(CARD card) {
            this.card = card;
            this.selected = false;
        }

        public HandCARD(CARD card, boolean selected) {
            this.card = card;
            this.selected = selected;
        }
    }

    private boolean isPlaying;
    private List<CARD> bot;
    private List<CARD> top;
    private List<HandCARD> hand;
    private String name;
    private boolean epicMode; //when you've gotten to your bottom cards

    public Player(List<CARD> stack9, String name){
        this.name = name;
        start();
        epicMode = false;

        bot = new ArrayList<>(stack9.subList(0, 3));
        top = new ArrayList<>(stack9.subList(3, 6));
        hand = stack9.subList(6, 9)
                .stream()
                .map(c -> new HandCARD(c, false))
                .collect(Collectors.toCollection(LinkedList::new));
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
    public boolean isEpic() {
        return epicMode;
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

    public List<HandCARD> getHand() {
        return hand;
    }

    public CARD getLeastInHand() {
        CARD least = hand.get(0).card;
        for (HandCARD hc: hand)
            if (CARD.getComp().compare(least, hc.card) > 0)
                least = hc.card;
        return least;
    }

    public void select(int i) {
        hand.get(i).selected = true;
        //TODO: handle selection logic
        // check for same number
        // check for card continuity (is it still your turn after the first card?)
        // deselt those that don't match selection rules
    }

    public void deselect(int i) {
        hand.get(i).selected = false;
    }

    public void draw(CARD c) {
        hand.add(new HandCARD(c));
    }

    public void pickUp(List<CARD> cards) {
        cards.forEach(this::draw);
    }

    public void removeToHand(List<CARD> cards) {
        for (int i = 0; i < cards.size(); ++i) {
            hand.add(new HandCARD(cards.remove(i)));
        }
    }

    public List<CARD> play() {
        List<CARD> output = new LinkedList<>();
        for (int i = 0; i < hand.size(); ++i) {
            HandCARD hc = hand.get(i);
            if (hc.selected)
                output.add(hand.remove(i).card);
        }
        return output;
    }

    public void topToHand() {
        removeToHand(top);
    }

    public void botToHand(int i) {
        draw(bot.get(i));
        epicMode = true;
    }
}
