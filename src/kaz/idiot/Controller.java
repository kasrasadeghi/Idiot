package kaz.idiot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kasra on 2/20/2016.
 */
public class Controller {
    private Game game;
    private GamePanel gp;
    private boolean isItYourTurn;
    private int playerNumber;
    private Player me;

    //      Kasra's understanding of MVC, 02.20.2016
    //The model contains the information, presenting the same information to all players.
    //     ^ updates               /-> other models - - - - - - - - - - - - - - -\  |
    //     |                       |                                              \ | information
    //The controller updates everyone's game depending on the user's input.        \|
    //     ^ interaction (player unique)                                            | interpretation
    //     |                                                                        v
    //The view interacts with the player, showing what's in the game, but never directly changing what's in the game.

    public Controller(Game game, GamePanel gp) {
        this.game = game;
        this.gp = gp;
        this.isItYourTurn = false;
        this.playerNumber = gp.getPlayerNumber();
        this.me = game.getPlayer(playerNumber);
    }

    public void println(String text) {
        //this has to send the text to the server, and then the server has to echo it to everyone.
        //then it has to make it into a List<String> of codes, so the handleCodes method can parse through it.

        //sending to server
        for (GamePanel gamePanel : Main.gp) {
            gamePanel.printEvent(text);
        }
    }

    /**
     * The almost immediately deprecated implementation of Option #1 for handling inter-controller updates for the game.
     *
     * @param println - the println to be turned into a codelist for the original handleCodes to handle.
     * @return a list of codes that was equivalent to the one output by the GamePanel bounds checker.
     */
    public List<String> println2codeList(String println) {
        // code for INTER-CONTROLLER UPDATES CHOICE #1
        //well actually this bit of code is actually useless, because we want to see if the mouseClicks are useful
        // before they are sent over.
        //so first we need to run them through the case matcher, then send a message like 0: gameAction PLAY
        // or something. That way we can use regex's and have them actually be decent.
        //parse output into list of codes
        Pattern pattern = Pattern.compile("(?<playerNumber>\\d+): \\[(?<codes>.*)\\]");
        Matcher matcher = pattern.matcher(println);

        // this doesn't use playerNumber because it's deprecated. it was supposed to be useful for servers.
        int playerNumber;
        String codeList= null;
        if (matcher.matches()) {
            playerNumber = Integer.parseInt(matcher.group("playerNumber"));
            codeList = matcher.group("codes");
        }
        assert codeList != null;
        List<String> codes = new ArrayList<>();
        Collections.addAll(codes, codeList.split(","));
        return codes;
    }

    public void handleCodes(List<String> codes) {
        isItYourTurn = game.getCurrentPlayerNumber() == gp.getPlayerNumber();

        //#server TODO: other controllers have a handle update method to update their separate games.

        String box = "none";
        String card = "none";
        String action = "none";

        //figure out what codes are available with this mouse click
        for ( String code : codes ) {
            String[] split = code.split(" ");
            switch(split[0]) {
                case "box":
                    box = split[1];
                    break;
                case "card":
                    card = split[1];
                    break;
                case "action":
                    action = split[1];
                    break;
            }
        }

        //if we're still setting up the game
        if (game.getState() == STATE.SETUP && !me.isReady()) {
            if (!action.equals("none"))
                sendEvent("setupAction " + action);

            if(!card.equals("none"))
                sendEvent("setupCard " + action);

        } else if (game.getState() == STATE.PLAYING) {
            if (!action.equals("none"))
                sendEvent("gameAction " + action);

            else if (!box.equals("none")) {
                if (card.equals("none"))
                    handleInspectionCode(box);

                else if ( box.equals(playerNumber + ""))
                    sendEvent("gameCard " + card);

            }
        }

        //#server TODO: two options for handling inter-controller updates.
        // 1. handle each motion(selecting cards, drawing) or
        // 2. handle each turn(find out what actually changed and then send an update pkg)
        // 3. filter down to either a view action that doesn't require server echo, because it doesn't affect game state,
        //      or a [setup|game][card|action]

        //#1 is easier, and probably looks better. might be slower.
        //#3 is what i'm going to try to implement
    }

    public void sendEvent(String ev) {
        handleEvent(ev);
    }

    public void handleEvent(String ev) {
        String[] event = ev.split(" ");
        switch (event[0]) {
            case "setupCard":
                handleSetupCard(event[1]);
                break;
            case "setupAction":
                handleSetupAction(event[1]);
                break;
            case "gameCard":
                handleGameCard(event[1]);
                break;
            case "gameAction":
                handleGameAction(event[1]);
                break;
        }
    }

    private void handleInspectionCode(String box) {
        try {
            int boxVal = Integer.parseInt(box);
            if (gp.isInspecting())
                gp.setInspection(GamePanel.INSP_GAME);
            else gp.setInspection(boxVal);

            gp.repaint();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleSetupCard(String card) {
        try {
            //see which card and then select it in the special player indexes for the setup state
            int cardVal = Integer.parseInt(card);
            //if it's a hand card
            if (cardVal > -1 && cardVal < 3) {
                me.setHandSetupSelect(cardVal);
                gp.repaint();
                //if it's a top card
            } else if (cardVal > 2 && cardVal < 6) {
                me.setTopSetupSelect(cardVal - 3);
                gp.repaint();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleSetupAction(String action) {
        switch(action) {
            case "SWAP":
                game.getPlayer(playerNumber).setupSwap();
                gp.repaint();
                break;
            case "READY":
                game.getPlayer(playerNumber).setReady(true);
                if (game.allReady()) {
                    for (int i = 0; i < Main.gp.length; ++i) {
                        GamePanel gp = Main.gp[i];
                        gp.setInspection(-1);
                        gp.repaint();
                    }
                }
                else gp.repaint();
                break;
        }
    }

    private void handleGameCard(String card) {
        try {
            int cardVal = Integer.parseInt(card);
            game.getPlayer(playerNumber).select(cardVal);
            gp.repaint();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleGameAction(String action) {
        if (!isItYourTurn) return;
        if (!game.checkRoundOver())
            switch (action) {
                case "PLAY":
                    game.play();
                    break;
                case "PICKUP":
                    game.pickUp();
                    break;
                case "LEFT":
                    me.botToHand(0);
                    break;
                case "CENTER":
                    me.botToHand(1);
                    break;
                case "RIGHT":
                    me.botToHand(2);
                    break;
            }
        else switch (action) {
            case "HOST_REMATCH":
                //#server TODO: make it go to the play menu
                Main.activeFrame = StartFrame.instance();
                break;
            case "RETURN_TO_MAIN_MENU":
                //#server TODO: make it go to the main menu
                Main.activeFrame = StartFrame.instance();
                break;
        }
        for (GamePanel gamePanel : Main.gp)
            gamePanel.repaint();
    }
}
