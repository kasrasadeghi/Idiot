package kaz.idiot;

import javax.swing.*;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

    public static List<PrintWriter> clientPrinters;
    public static PrintWriter serverPrinter;
    public static ChatFrame chatFrame;
    public static boolean accepting = true;

    public static void main(String[] args) {
        clientPrinters = new ArrayList<>();
        SwingUtilities.invokeLater(() -> activeFrame = StartFrame.instance());
    }

    public static void setupServer(String port, String name) {
//        System.out.println("I'm the Server");

        activeFrame.setVisible(false);
        chatFrame = new ChatFrame(name, true);

        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
            chatFrame.println("Hosting on " + port);

            new Thread(() -> {
                while (accepting) {
                    Socket toClient = null;
                    try {
                        toClient = serverSocket.accept();

                        PrintWriter clientPrinter = new PrintWriter(toClient.getOutputStream(), true);
                        clientPrinters.add(clientPrinter);
                        clientPrinter.println("Connected!");

                        Scanner clientReader = new Scanner(toClient.getInputStream());
                        new Thread(() -> {
                            while(clientReader.hasNextLine()) {
                                String clientMessage = clientReader.nextLine();
                                String[] split = clientMessage.split("> ");
                                sendToClients(split[0], split[1]);
                            }
                        }).start();
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }).start();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void setupClient(String address, String port, String name) {
//        System.out.println("I'm the Client");

        activeFrame.setVisible(false);
        chatFrame = new ChatFrame(name, false);

        try {
            chatFrame.println("Connecting to " + address + ":" + port);
            Socket toServer = new Socket(address, Integer.parseInt(port));
            Scanner serverReader = new Scanner(toServer.getInputStream());
            serverPrinter = new PrintWriter(toServer.getOutputStream(), true);

            new Thread(() -> {
                while(serverReader.hasNextLine())
                    chatFrame.println(serverReader.nextLine());
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void sendToClients(String name, String text) {
        clientPrinters.forEach(printer -> printer.println(name + "> " + text));
        chatFrame.println(name + "> " + text);
    }

    public static void sendToServer(String name, String text) {
        serverPrinter.println(name + "> " + text);
    }

    public static void init() {
        game = new Game(playerCount);
        controller = new Controller[playerCount];
        gp = new GamePanel[playerCount];
        frames = new JFrame[playerCount]; //temporary.
        //in the final version of the game, delete the JFrame array and make it present only the controlling player's JFrame.
        //also need to only have one GamePanel and one Controller, so they can interface with the network and the Game.

        for (int i = 0; i < playerCount; ++i) {
            frames[i] = new IdiotFrame(i);
        }
        activeFrame = frames[game.getCurrentPlayerNumber()];
        activeFrame.setVisible(true);

        //#after TODO: make the rubik's square game.
    }

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
