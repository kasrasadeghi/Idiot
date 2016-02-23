package kaz.idiot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    private boolean isPlaying; //whether the player is still in the game
    private List<CARD> bot; //
    private List<CARD> top;
    private List<HandCARD> hand;
    private String name;
    private boolean epicMode; //when you've gotten to your bottom cards
    private int handSetupSelect = -1;
    private int topSetupSelect = -1;

    public Player(List<CARD> stack9, String name){
        this.name = name;
        epicMode = false;

        bot = new ArrayList<>(stack9.subList(0, 3));
        top = new ArrayList<>(stack9.subList(3, 6));
        hand = stack9.subList(6, 9)
                .stream()
                .map(c -> new HandCARD(c, false))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public void setHandSetupSelect(int handSetupSelect) {
        this.handSetupSelect = handSetupSelect;
    }

    public void setTopSetupSelect(int botSetupSelect) {
        this.topSetupSelect = botSetupSelect;
    }

    public int getHandSetupSelect() {
        return handSetupSelect;
    }

    public int getTopSetupSelect() {
        return topSetupSelect;
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

    public void swapBotAndHand(int h, int b) {
        CARD handswap = hand.get(h).card;
        CARD botswap = bot.get(b);
        hand.set(h, new HandCARD(botswap));
        bot.set(b, handswap);
    }

    public CARD getLeastInHand() {
        CARD least = hand.get(0).card;
        for (HandCARD hc: hand)
            if (CARD.getComp().compare(least, hc.card) > 0)
                least = hc.card;
        return least;
    }

    public void select(int i) {
        hand.get(i).selected = !hand.get(i).selected;
        //TODO: handle selection logic
        // check for same number
        // check for card continuity (is it still your turn after the first card?)
        // deselect those that don't match selection rules
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
