package kaz.idiot;

import java.util.List;

/**
 * Created by kasra on 2/20/2016.
 */
public class Controller {
    private Game game;
    private GamePanel gamePanel;
    private boolean isItYourTurn;
    private int playerNumber;

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
        this.gamePanel = gp;
        this.isItYourTurn = false;
        this.playerNumber = gp.getPlayerNumber();
    }

    public void handleCodes(List<String> codes) {
        isItYourTurn = game.getCurrentPlayerNumber() == gamePanel.getPlayerNumber();

        //TODO: other controllers have a handle update method.

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
        if (game.getState() == Game.SETUP_STATE) {
            //if there's an action
            if (!action.equals("none")) {
                handleSetupAction(action);
            }

            //and the player clicks on a card
            if(!card.equals("none")) {
                try {
                    //see which card and then select it in the special player indexes for the setup state
                    int cardVal = Integer.parseInt(card);

                    //if it's a hand card
                    if (cardVal > -1 && cardVal < 3) {
                        game.getPlayer(playerNumber)
                                .setHandSetupSelect(Integer.valueOf(card));
                        gamePanel.repaint();
                    //if it's a top card
                    } else if (cardVal > 2 && cardVal < 6) {
                        game.getPlayer(playerNumber)
                                .setTopSetupSelect(cardVal - 3);
                        gamePanel.repaint();
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        } else if (game.getState() == Game.GAME_STATE) {
            if (!action.equals("none"))
                handleGameAction(action);

            if (!box.equals("none")) {
                try {
                    int boxVal = Integer.parseInt(box);
                    if (card.equals("none")) {
                        handleInspectionCode( boxVal );
                    } else {
                        int cardval = Integer.parseInt(card);
                        handleCardSelection( boxVal, cardval);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }



        //TODO: two options: 1. handle each motion(selecting cards, drawing) or
        //TODO: 2. handle each turn(find out what actually changed and then send an update pkg)


        //TODO: implement player inspection
    }

    private void handleCardSelection(int boxval, int cardval) {

    }

    private void handleInspectionCode(int boxval) {

    }

    private void handleGameAction(String action) {

    }

    private void handleSetupAction(String action) {
        switch(action) {
            case "SWAP":
                game.getPlayer(playerNumber).setupSwap();
                gamePanel.repaint();
                break;
        }
    }
}
