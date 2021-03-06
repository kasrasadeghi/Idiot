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

    private STATE state;

    private List<CARD> bot;
    private List<CARD> top;
    private List<HandCARD> hand;
    private String name;

    private int handSetupSelect = -1;
    private int topSetupSelect = -1;

    public Player(List<CARD> stack9, String name){
        state = STATE.INIT;
        this.name = name;

        bot = new ArrayList<>(stack9.subList(0, 3));
        top = new ArrayList<>(stack9.subList(3, 6));
        hand = stack9.subList(6, 9)
                .stream()
                .map(c -> new HandCARD(c, false))
                .collect(Collectors.toCollection(LinkedList::new));
        state = STATE.SETUP;
    }

    public boolean isReady() {
        return state == STATE.READY;
    }

    public void setReady(boolean r) {
        state = STATE.READY;
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

    public STATE getState() {
        return state;
    }

    public void setState(STATE s) {
        this.state = s;
    }

    public Player start() {
        assert state == STATE.READY;
        state = STATE.PLAYING;
        topSetupSelect = -1;
        topSetupSelect = -1;
        return this;
    }

    public void end() {
        state = STATE.SPECTATING;
        bot.clear();
    }

    public boolean isPlaying() {
        return state == STATE.PLAYING;
    }

    public boolean isEpic() {
        return hand.isEmpty() && top.isEmpty() && !bot.isEmpty();
    }

    public boolean isEmpty() { return hand.isEmpty() && top.isEmpty() && bot.isEmpty(); }

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

    public List<CARD> getSelected() {
        return getHand().stream()
                .filter(hc -> hc.selected)
                .map(hc -> hc.card)
                .collect(Collectors.toList());
    }

    public void setupSwap() {
        int h = handSetupSelect;
        int t = topSetupSelect;
        if (h == -1 || t == -1) return;
        CARD handSwap = hand.get(h).card;
        CARD topSwap = top.get(t);
        hand.set(h, new HandCARD(topSwap));
        top.set(t, handSwap);
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
    }

    public void draw(CARD c) {
        hand.add(new HandCARD(c));
    }

    public void pickUp(List<CARD> cards) {
        removeToHand(cards);
    }

    public void removeToHand(List<CARD> cards) {
        for (int i = cards.size() - 1; i >= 0; --i) {
            CARD card = cards.remove(i);
            hand.add(new HandCARD(card));
        }
    }

    public List<CARD> playSelectedCards() {
        List<CARD> output = new LinkedList<>();
        for (int i = hand.size() - 1; i > -1; --i) {
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
        assert state == STATE.EPICMODE;
        draw(bot.set(i, CARD.NULL_CARD));
        hand.get(0).selected = true;
        state = STATE.PLAYING;
    }
}
