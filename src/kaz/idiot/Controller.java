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

    //      Kasra's understanding of MVC, 02.20.2016
    //The model contains the information, presenting the same information to all players.
    //     ^ updates                /-> other models                                |
    //     |                        |                                               | information
    //The controller updates everyone's game depending on the user's input.         |
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

        //#after TODO: reorganize case matching below

        //if we're still setting up the game
        if (game.getState() == STATE.SETUP && !me.isReady()) {

            //if there's an action
            if (!action.equals("none")) {
                handleSetupAction(action);
            }

            //if the player clicks on a card
            if(!card.equals("none")) {
                try {
                    //see which card and then select it in the special player indexes for the setup state
                    int cardVal = Integer.parseInt(card);
                    //if it's a hand card
                    if (cardVal > -1 && cardVal < 3) {
                        me.setHandSetupSelect(Integer.valueOf(card));
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
        //if we're actually playing the game
        } else if (game.getState() == STATE.PLAYING) {
            if (!action.equals("none"))
                handleGameAction(action);

            else if (!box.equals("none")) {
                try {
                    int boxVal = Integer.parseInt(box);
                    if ( boxVal == playerNumber && isItYourTurn) {
                        if (!card.equals("none")) {
                            int cardVal = Integer.parseInt(card);
                            handleCardSelection(cardVal);
                        }
                    }
                    else if (card.equals("none"))
                        handleInspectionCode( boxVal );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }


        //#server TODO: two options for handling inter-controller updates.
        // 1. handle each motion(selecting cards, drawing) or
        // 2. handle each turn(find out what actually changed and then send an update pkg)

        //#1 is easier, and probably looks better. might be slower. let's see...

    }

    private void handleCardSelection( int cardVal) {
        game.getPlayer(playerNumber).select(cardVal);
        gp.repaint();
    }

    private void handleInspectionCode(int boxVal) {
        if (gp.isInspecting())
            gp.setInspection(GamePanel.INSP_GAME);
        else gp.setInspection(boxVal);

        gp.repaint();
    }

    private void handleGameAction(String action) {
        if (!isItYourTurn) return;
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
        for (GamePanel gamePanel : Main.gp)
            gamePanel.repaint();
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
}
