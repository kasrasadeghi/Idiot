package kaz.idiot;

import java.util.*;
import java.util.stream.Collectors;

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
    private LinkedList<CARD> field = new LinkedList<>();
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
            discard.add(field.remove());
    }

    public List<CARD> getDiscard() {
        return discard;
    }

    public LinkedList<CARD> getField() {
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

    public void setCurrentPlayerToNext() {
        currentPlayerNumber = (currentPlayerNumber + (rotatingRight? 1:players.size()-1))%players.size();
    }

    public void setCurrentPlayer(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public Player getNextPlayer() {
        return getPlayer((currentPlayerNumber + (rotatingRight? 1:players.size()-1))%players.size());
    }

    public void play() {
        //TODO: when setting the next turn we have to compute whether or not someone can play.
        // check that the cards that are being played are the same card rank
        // check that the card rank that is being played is viable with the underlying condition
        Player current = players.get(currentPlayerNumber);
        if (!canPlay())  {
            current.pickUp(field);
            return;
        }
        if (!checkCurrentPlay()) return;

        //Implement play code
        field.addAll(current.play());
        //

        while (current.getHand().size() < 3 && !deck.isEmpty()) {
            current.draw(draw());
        }
        setCurrentPlayerToNext();
    }

    public boolean checkCurrentPlay() {
        List<CARD> currentCards = players.get(currentPlayerNumber).getHand()
                .stream().map(hc -> hc.card).collect(Collectors.toList());
        return checkRankEquality(currentCards);
    }

    /**
     * checkPlay
     * Checks whether the card follows the card playing rules.
     *
     * Card playing rules:
     * after a red two, anything is playable.
     * after a black two, if playerCount > 2 then reverse turn order
     *                        else set the next player to the current player.
     *                    then play with the underlying conditions.
     * after a ten, the stack burns and the next player is the current player.
     * after a red seven, check the conditions of the card below
     * after a black seven, play lower than or equal to the next conditional card.
     *     a conditional card is a non-magic card.
     *
     * the default behaviour of a card is to player equal to or greater than the current playRank of the card.
     * Ace's are high.
     *
     * @return whether or not the currently selected hand cards of the current player are a valid move
     */
    public boolean checkRankEquality(List<CARD> cards) {
        //TODO: check the selected cards to see if they are valid
        // cards are valid if they are all the same number.
        // if they aren't all the same number, return false.


        // cards also have to follow the rules laid out by the field.
        return checkField(field.size() - 1, cards);
    }

    public boolean checkField(int index, List<CARD> cards) {
        return true;
    }



    /**
     * @return true when current player has a card in hand that can be played to the field.
     */
    public boolean canPlay() {
        for( Player.HandCARD handCARD : players.get(currentPlayerNumber).getHand()) {
            CARD card = handCARD.card;

        }
        return true;
    }
}
