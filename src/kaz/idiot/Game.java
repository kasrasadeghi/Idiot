package kaz.idiot;

import java.util.*;

import static kaz.idiot.CARD.*;
/**
 * Created by kasra on 2/6/2016.
 */

// THE MODEL
public class Game {
    private int currentPlayerNumber;
    //TODO: use enum STATE

    private STATE state;
    private List<Player> players = new ArrayList<>();
    private List<CARD> field = new ArrayList<>();
    private List<CARD> deck = new LinkedList<>();
    private List<CARD> discard = new ArrayList<>();
    private boolean rotatingRight;

    public void setState(STATE s) {
        state = s;
    }

    public STATE getState() {
        return state;
    }

    public Game(ArrayList<String> playerNames){
        init(playerNames);
    }

    public Game(int count) {
        List<String> names = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            names.add(String.valueOf(i));
        }
        init(names);
    }

    private void init(List<String> playerNames) {
        if (playerNames.isEmpty()) {
            System.err.println("There are no players to initialize.");
            return;
        }

        state = STATE.SETUP;
        dealSetupCards(playerNames);

    }

    public boolean checkReady() {
        int counter = 0;
        for (Player p : players) {
            if (!p.isReady())
                counter++;
        }
        if (counter == 0)
            start();
        return counter == 0;
    }

    private void start() {
        initTurnOrder();
        state = STATE.PLAYING;

        players.forEach(Player::start);//why are we start()ing twice
    }

    private void dealSetupCards(List<String> playerNames) {
        //TODO: make alternate dealer that completely randomizes cards. works against card counting
        //  doesn't have decks. uses individual randomized cards. drawing is merely getting a random card.
        for (int i = 0; i < playerNames.size()/5 + 1; ++i)
            deck.addAll(fullDeck());
        for (int i = 0; i < playerNames.size(); ++i) {
            String name = playerNames.get(i);
            List<CARD> stack9 = new LinkedList<>();
            for (int j = 0; j < 9; j++) {
                stack9.add(draw());
            }

            players.add(new Player(stack9, name).start());//this is the second time we're starting?
        }
    }

    private void initTurnOrder() {
        List<CARD> leastCards = new ArrayList<>();
        for (Player player : players) {
            leastCards.add(player.getLeastInHand());
        }
        CARD cardOfFirst = leastCards.get(0);
        for (int i = 0; i < leastCards.size(); i++) {
            CARD card = leastCards.get(i);
            if (CARD.getComp().compare(card, cardOfFirst) < 0) {
                cardOfFirst = card;
                currentPlayerNumber = i;
            }
        }
        Player left = getLeftPlayer();
        Player right = getRightPlayer();
        rotatingRight = CARD.getComp().compare(left.getLeastInHand(), right.getLeastInHand()) > 0;
    }

    public Player getPlayer(int i) {
        return players.get(i);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    private CARD draw() {
        return deck.remove((int) (Math.random() * deck.size()));
    }

    private boolean isDiscardEmpty() {
        return discard.isEmpty();
    }

    private void burn() {
        while (!field.isEmpty())
            discard.add(field.remove(0));
    }

    public List<CARD> getDiscard() {
        return discard;
    }

    public List<CARD> getField() {
        return field;
    }

    public List<CARD> getDeck() {
        return deck;
    }

    public boolean isRotatingRight() {
        return rotatingRight;
    }

    public void switchRotation() {
        rotatingRight = !rotatingRight;
    }

    public void setRightPlayer() {
        currentPlayerNumber = (currentPlayerNumber + 1)%players.size();
    }

    public Player getRightPlayer() {
        return getPlayer((currentPlayerNumber + 1)%players.size());
    }

    public void setLeftPlayer() {
        currentPlayerNumber = (currentPlayerNumber  + players.size() - 1)%players.size();
    }

    public Player getLeftPlayer() {
        return getPlayer((currentPlayerNumber + players.size() - 1)%players.size());
    }

    public void setNextPlayer() {
        currentPlayerNumber = (currentPlayerNumber + (rotatingRight? 1:players.size()-1))%players.size();
    }

    public Player getNextPlayer() {
        return getPlayer((currentPlayerNumber + (rotatingRight? 1:players.size()-1))%players.size());
    }

    public List<CARD> getValidMoves() {
        //if red seven then equal to or higher than beneath
        //if black seven then equal to or lower than
        //if nothing then all cards
        //if normal card then all cards equal to or higher than that card
        //
        ArrayList<CARD> cards = new ArrayList<>();
        cards.addAll(CARD.fullDeck());
        return cards;
    }

    public void play() {
        //TODO: i guess i have to check the play somehow. I dunno, we'll see.
        if (checkPlay()) {
            Player current = players.get(currentPlayerNumber);
            field.addAll(current.play());
            while (current.getHand().size() < 3) {
                current.draw(draw());
            }
            setNextPlayer();
        }
    }

    public boolean checkPlay() {
        //TODO: check the selected cards to see if they are valid
        // this will suffice as the selection checker because you are playing the selected cards for the current player
        return true;
    }
}
