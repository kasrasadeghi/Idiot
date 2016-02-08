package kaz.idiot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static kaz.idiot.CARD.*;
/**
 * Created by kasra on 2/6/2016.
 */

// THE MODEL
public class Game {
    private int currentPlayerNumber;

    private List<Player> players = new LinkedList<>();
    private List<CARD> field = new ArrayList<>();
    private List<CARD> deck = new LinkedList<>();

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

        deck.addAll(fullDeck());
        if (playerNames.size() > 5) deck.addAll(fullDeck());

        for (int i = 0; i < playerNames.size(); i++) {
            String name = playerNames.get(i);
            List<CARD> stack9 = new LinkedList<>();
            for (int j = 0; j < 9; j++) {
                stack9.add(draw());
            }

            players.add(new Player(stack9, name));
        }

        currentPlayerNumber = 0;
        players.get(0).start();
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

    public List<CARD> getField() {
        return field;
    }

    public List<CARD> getDeck() {
        return deck;
    }

    public void rightPlayer() {
        currentPlayerNumber = (currentPlayerNumber + 1)%players.size();
    }

    public void leftPlayer() {
        currentPlayerNumber = (currentPlayerNumber - 1)%players.size();
    }

    public void play(int i) {
        players.get(currentPlayerNumber).play(i);
    }
}
