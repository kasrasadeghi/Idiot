package kaz.idiot;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static JFrame activeFrame;
    public static GamePanel[] gp;
    public static Controller[] controller;
    public static Game game;
    public static JFrame[] frames;
    public static final int CARD_X = 79;
    public static final int CARD_Y = 123;
    public static final int playerCount = 2;

    public static String address;
    public static int port;
    public static ServerSocket hostServerSocket;
    public static Socket hostSocket;

    public static void main(String[] args) {
        //#devmode TODO: maybe implement a developer multiViewer:
        // one main view in the center and all the other views on the right bar
        // or maybe just the generic viewer and then all the other viewers in a tiny thing on another window,
        // and then accessible by clicking on that other window
        game = new Game(playerCount);
        controller = new Controller[playerCount];
        gp = new GamePanel[playerCount];
        frames = new JFrame[playerCount]; //temporary.
        //in the final version of the game, delete the JFrame array and make it present only the controlling player's JFrame.
        //also need to only have one GamePanel and one Controller, so they can interface with the network and the Game.

//        SwingUtilities.invokeLater(() -> frame = new StartFrame());
//        SwingUtilities.invokeLater(() -> frame = new IdiotFrame());
        for (int i = 0; i < playerCount; ++i) {
            frames[i] = new IdiotFrame(i);
        }
        activeFrame = frames[game.getCurrentPlayerNumber()];
        activeFrame.setVisible(true);

        //#after TODO: make the rubik's square game.
    }

    //region public static void hostServer(String name) {...}
    //#server TODO: work on server stuff
   /* public static void hostServer(String name) {
        try {
            hostServerSocket = new ServerSocket(port);
            hostSocket = hostServerSocket.accept();
            PrintWriter out = new PrintWriter(hostSocket.getOutputStream(), true);
            Scanner in = new Scanner(hostSocket.getInputStream());

            out.println("server: hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connectToServer() {

    }*/
    //endregion

    static class IdiotFrame extends JFrame {
        public IdiotFrame() {
            this(0);
        }

        public IdiotFrame(int number) {
            super("Idiot: " + number);

            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            //each IdiotFrame has its own Controller and GamePanel
            //constructs GamePanels irrespective of playerNumber, adding them to a game.
            //game should prompt to see if everyone has connected, and then the first player initializes the game and sends it to everyone else

            gp[number] = new GamePanel(game, number, 1920, 1080);
            controller[number] = new Controller(game, gp[number]);
            add(gp[number], BorderLayout.CENTER);
            pack();
            setResizable(true);
        }
    }
}
