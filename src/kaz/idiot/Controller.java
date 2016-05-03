package kaz.idiot;

import java.util.List;

/**
 * Created by kasra on 2/20/2016.
 */
public class Controller {
    private Game game;
    private GamePanel gp;
    private boolean isItYourTurn;
    private int playerNumber;
    private Player me;

    //      Kasra's understanding of MVC, 05.02.2016
    //The model contains the information, presenting the same information to all players.
    //     ^ updates               /-> other models - - - - - - - - - - - - - - -\  |
    //     |                       |                                              \ | information
    //The controllers updates everyone's game depending on the user's input.       \|
    //     ^ interaction (player unique)         | view-based interaction           | interpretation
    //     |                                     v                                  v
    //The view interacts with the player, showing what's in the game, but never directly changing what's in the game.

    public Controller(Game game, GamePanel gp) {
        this.game = game;
        this.gp = gp;
        this.isItYourTurn = false;
        this.playerNumber = gp.getPlayerNumber();
        this.me = game.getPlayer(playerNumber);
    }

    public void handleCodes(List<String> codes) {
        isItYourTurn = game.getCurrentPlayerNumber() == gp.getPlayerNumber();

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
                sendEvent("setupCard " + card);

        } else if (game.getState() == STATE.PLAYING) {
            if (!action.equals("none"))
                sendEvent("gameAction " + action);

            else if (!box.equals("none")) {
                if (card.equals("none"))
                    handleInspectionCode(box);

                else if ( box.equals(playerNumber + "") && isItYourTurn)
                    sendEvent("gameCard " + card);
            }
        }

        //two options for handling inter-controllers updates.
        // 1. handle each motion(selecting cards, drawing) or
        // 2. handle each turn(find out what actually changed and then sendToClients an update pkg)
        // 3. filter down to either a view action that doesn't require server echo, because it doesn't affect game state,
        //      or a [setup|game][card|action]

        //#1 is easier, and probably looks better. might be slower.
        //#3 is what i'm going to try to implement
    }

    public void sendEvent(String ev) {
        Main.chatFrame.send("/event " + playerNumber + " " + ev);
    }

    public void handleEvent(String numString, String ev, String arg) {
        try {
            int num = Integer.parseInt(numString);
            switch (ev) {
                case "setupCard":
                    handleSetupCard(num, arg);
                    break;
                case "setupAction":
                    handleSetupAction(num, arg);
                    break;
                case "gameCard":
                    handleGameCard(num, arg);
                    break;
                case "gameAction":
                    handleGameAction(num, arg);
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleInspectionCode( String box) {
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

    private void handleSetupCard(int num, String card) {
        try {
            //see which card and then select it in the special player indexes for the setup state
            int cardVal = Integer.parseInt(card);

            Player player = game.getPlayer(num);
            //if it's a hand card
            if (cardVal > -1 && cardVal < 3) {
                player.setHandSetupSelect(cardVal);
                gp.repaint();
                //if it's a top card
            } else if (cardVal > 2 && cardVal < 6) {
                player.setTopSetupSelect(cardVal - 3);
                gp.repaint();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleSetupAction(int num, String action) {
        switch(action) {
            case "SWAP":
                game.getPlayer(num).setupSwap();
                gp.repaint();
                break;
            case "READY":
                game.getPlayer(num).setReady(true);
                if (game.allReady()) {
//                    for (int i = 0; i < Main.gps.length; ++i) {
//                        GamePanel gp = Main.gps[i];
//                        gp.setInspection(-1);
//                        gp.repaint();
//                    }
                    gp.setInspection(-1);
                    gp.repaint();
                }
                else gp.repaint();
                break;
        }
    }

    private void handleGameCard(int num, String card) {
        try {
            int cardVal = Integer.parseInt(card);
            game.getPlayer(num).select(cardVal);
            gp.repaint();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        //TODO: implement willful pickup
    }

    private void handleGameAction(int num, String action) {
        Player me = game.getPlayer(num);
        Main.chatFrame.println("The round is over: " + game.checkRoundOver());
        if (!game.checkRoundOver())
            switch (action) {
                case "PLAY":
                    game.play(num);
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
        Main.gp.repaint();
    }
}
