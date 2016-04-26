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

    public boolean allReady() {
        int counter = 0;
        for (Player p : players)
            if (!p.isReady())
                counter++;
        //region TODO: temp code
        setCurrentPlayerToNext();
        //endregion
        if (counter == 0)
            start();
        return counter == 0;
    }

    private void start() {
        initTurnOrder();
        state = STATE.PLAYING;

        players.forEach(Player::start);

        //TODO: temp code
        Main.activeFrame.setVisible(false);
        Main.activeFrame = Main.frames[currentPlayerNumber];
        Main.activeFrame.setVisible(true);
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
                stack9.add(drawFromDeck());
            }

            players.add(new Player(stack9, name));
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

    private CARD drawFromDeck() {
        return deck.remove((int) (Math.random() * deck.size()));
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

    public void reverseTurnOrder() {
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
        //region TODO: temp change
        Main.activeFrame.setVisible(false);
        Main.activeFrame = Main.frames[currentPlayerNumber];
        Main.activeFrame.setVisible(true);
        //endregion temp change
        //make the next player enter epic mode if they don't have top cards or hands cards.
        if (getCurrentPlayer().getHand().isEmpty() && getCurrentPlayer().getTop().isEmpty())
            getCurrentPlayer().setState(STATE.EPICMODE);

        //TODO: the current player should be set to STATE.SPECTATING after they play.
        // it sees if they are all out of cards and then it sets the player to
        //TODO: make a dev mode kinda thing that's separate from this nonsense.
        //be able to discard cards
        //be able to chose current player
        //be able to get a chosen card.
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerNumber);
    }

    public void setCurrentPlayer(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public Player getNextPlayer() {
        return getPlayer((currentPlayerNumber + (rotatingRight? 1:players.size()-1))%players.size());
    }

    public void pickUp(){
        players.get(currentPlayerNumber).pickUp(field);
        setCurrentPlayerToNext();
    }

    public void play() {
        // when setting the next turn we have to compute whether or not someone can play.
        // check that the cards that are being played are the same card rank
        // check that the card rank that is being played is viable with the underlying condition
        Player current = players.get(currentPlayerNumber);
        //if the player doesn't have currently selected cards
        if (current.getSelected().isEmpty())
            return;
        //if the current player's selected cards aren't a valid move then return
        if (!checkCurrentPlay()) {
            //send message to console. "illegal move"
            System.out.println("Illegal Move");
            return;
        }

        //  Implement play code
        //add selected cards to the field.
        List<CARD> lastPlay = current.play();
        field.addAll(lastPlay);

        //post-move logic.

        //Player Actions
        if (!deck.isEmpty())    //if the current player has less than 3 cards and the deck isn't empty
            while (current.getHand().size() < 3 )
                current.draw(drawFromDeck());           //drawFromDeck until they maintain 3
        else if (current.getHand().isEmpty()) { //if the hand is empty
            if (!current.getTop().isEmpty())    //and the top is NOT empty
                current.topToHand();            //move the top to the hand
            else if (!current.getBot().isEmpty())
                current.setState(STATE.EPICMODE);
            else current.setState(STATE.SPECTATING);
        }

        //Field Actions
        boolean again = false;
        //if a black two has been played, reverse turn order.
        if (lastPlay.stream().anyMatch( c -> c == CARD.CLUB_2 || c == CARD.SPADE_2)) {
            //if there are only two people playing, then current player goes again.
            again = true;
            reverseTurnOrder();
        }
        //if the top card is a ten, burn all the cards.
        if (field.getLast().getRank().equals("10")) {
            burn();
            again = true;
        }
        //last four cards are the same rank = burn, even with magic cards.
        if (fourCardBurnCheck()) {
            burn();
            again = true;
        }
        if (!again)
            setCurrentPlayerToNext();
    }

    private boolean fourCardBurnCheck() {
        if (field.size() < 4) return false;
        return fourCardBurnDig(field.size() - 1, 0, field.getLast().getRank());
    }

    private boolean fourCardBurnDig(int index, int count, String rankCheck) {
        if (count == 4) {
            return true;
        }
        if (index < 0) {
            return false;
        }
        String rank = field.get(index).getRank();
        if (rank.equals(rankCheck)) {
            return fourCardBurnDig(index - 1, count + 1, rankCheck);
        } else {
            switch (field.get(index)) {
                //if the card is a black two or a 7, then it's invisible, so we can dig further.
                case SPADE_2:
                case CLUB_2:
                case HEART_7:
                case DIAMOND_7:
                case SPADE_7:
                case CLUB_7:
                    return fourCardBurnDig(index - 1, count, rankCheck);
                default: return false;
            }
        }
    }

    public boolean checkCurrentPlay() {
        List<CARD> currentCards = players.get(currentPlayerNumber).getSelected();
        return checkPlay(currentCards);
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
    public boolean checkPlay(List<CARD> selected) {
        // cards are valid if they are all the same number.
        // if they aren't all the same number, return false.
        int rank = selected.get(0).getRankValue();
        if (!selected.stream().allMatch(c -> c.getRankValue() == rank))
            return false;

        // cards also have to follow the rules laid out by the field.
        return checkField(field.size() - 1, selected);
    }

    public boolean checkField(int index, List<CARD> selected) {
        String rank = selected.get(0).getRank();
        return getValidRanks(index, true).contains(rank);
    }

    public List<String> getValidRanks(int index, boolean red) {
        List<String> validRanks = new ArrayList<>();
        if (index < 0) {
            Collections.addAll(validRanks, "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
            return validRanks;
        }
        Collections.addAll(validRanks, "10", "7", "2");
        CARD conditional = field.get(index);
        if (red) {
            //red 7 switch
            switch(conditional.getRank()) {
                case "3":
                    validRanks.add("3");
                case "4":
                    validRanks.add("4");
                case "5":
                    validRanks.add("5");
                case "6":
                    validRanks.add("6");
                case "8":
                    validRanks.add("8");
                case "9":
                    validRanks.add("9");
                case "J":
                    validRanks.add("J");
                case "Q":
                    validRanks.add("Q");
                case "K":
                    validRanks.add("K");
                case "A":
                    validRanks.add("A");
                    return validRanks;

                case "2":
                    if (conditional == CARD.HEART_2 || conditional == CARD.DIAMOND_2){
                        Collections.addAll(validRanks, "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
                        return validRanks;
                    }
                    else {
                        return getValidRanks(index - 1, true);
                    }
                case "7":
                    //check redness switch
                    if (conditional == CARD.CLUB_7 || conditional == CARD.SPADE_7)
                        red = false;
                    return getValidRanks(index - 1, red);
                case "10":
                    System.err.println("Unburned Stack Error");
                    break;
            }
        } else {
            //black 7 switch
            switch(conditional.getRank()) {

                case "A":
                    validRanks.add("A");
                case "K":
                    validRanks.add("K");
                case "Q":
                    validRanks.add("Q");
                case "J":
                    validRanks.add("J");
                case "9":
                    validRanks.add("9");
                case "8":
                    validRanks.add("8");
                case "6":
                    validRanks.add("6");
                case "5":
                    validRanks.add("5");
                case "4":
                    validRanks.add("4");
                case "3":
                    validRanks.add("3");
                    return validRanks;

                case "2":
                    if (conditional == CARD.HEART_2 || conditional == CARD.DIAMOND_2){
                        validRanks = new ArrayList<>();
                        Collections.addAll(validRanks, "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
                        return validRanks;
                    }
                    else {
                        return getValidRanks(index - 1, false);
                    }
                case "7":
                    return getValidRanks(index - 1, false);
                case "10":
                    System.err.println("Unburned Stack Error");
                    break;
            }
        }

        return null;
    }

    /**
     * @return true when current player has a card in hand that can be played to the field.
     */
    public boolean canPlay() {
        for( Player.HandCARD handCARD : players.get(currentPlayerNumber).getHand()) {
            CARD card = handCARD.card;
            if (checkField(field.size() - 1, new ArrayList<CARD>() {{ add(card);}} ))
                //what if field.size() == 0;
                return true;
        }
        return false;
    }
}
