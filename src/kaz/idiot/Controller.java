package kaz.idiot;

import java.util.List;

/**
 * Created by kasra on 2/20/2016.
 */
public class Controller {
    private Game game;
    private GamePanel gamePanel;
    public Controller(Game game, GamePanel gp) {
        this.game = game;
        this.gamePanel = gp;
    }
    public void handleCodes(List<String> codes) {
        String box = "no box found";
        String card = "no card found";
        String action = "no action found";

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


        //TODO: make the rubik's square game.
    }

    public void option1(String box, String card, String action) {
        switch(box) {
            case "":
                switch
        }
    }

    public void option2(String box, String card, String action) {

    }
}
