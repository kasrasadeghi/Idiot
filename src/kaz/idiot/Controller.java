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

        action(box, card, action);

        //TODO: two options: 1. handle each motion(selecting cards, drawing) or
        //TODO: 2. handle each turn(find out what actually changed and then send an update pkg)


        //TODO: implement player inspection
    }

    public void action(String box, String card, String action) {
        if (box.equals(String.valueOf(playerNumber))) {
            if(!card.equals("none")) {
                System.out.println("Selecting " + box + ": " + card);
                game.getPlayer(Integer.valueOf(box)).select(Integer.valueOf(card));
                gamePanel.repaint();
            }
        } else if (!box.equals("none")) {
            if (gamePanel.isInspecting()) gamePanel.setInspection(-1);
            else gamePanel.setInspection(Integer.valueOf(box));
            gamePanel.repaint();
        }

    }
}
