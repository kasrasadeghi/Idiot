package kaz.idiot;

import java.util.List;

/**
 * Created by kasra on 2/20/2016.
 */
public class Controller {
    private Game game;
    private GamePanel gamePanel;
    private boolean isItYourTurn;


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
    }

    public void handleCodes(List<String> codes) {
        isItYourTurn = game.getCurrentPlayerNumber() == gamePanel.getPlayerNumber();

        String box = "none";
        String card = "none";
        String action = "none";

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
                    break;
            }
        }


        //TODO: two options: 1. handle each motion(selecting cards, drawing) or
        //TODO: 2. handle each turn(find out what actually changed and then send an update pkg)



    }

    public void action(String box, String card, String action) {
        switch(box) {
            case "":
        }
    }

    public void option2(String box, String card, String action) {

    }
}
