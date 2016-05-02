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
    public static final int CARD_X = 79;
    public static final int CARD_Y = 123;
    public static GamePanel[] gps;
    public static GamePanel gp;
    public static Controller[] controllers;
    public static Controller controller;
    public static Game game;
    public static JFrame[] frames;
    public static JFrame activeFrame;

    public static List<PrintWriter> clientPrinters;
    public static List<String> clientNames;
    public static PrintWriter serverPrinter;
    public static ChatFrame chatFrame;
    public static boolean accepting = true;
    public static long seed = 8912;
    public static int playerNumber = -1;

    public static void main(String[] args) {
        clientPrinters = new ArrayList<>();
        SwingUtilities.invokeLater(() -> activeFrame = StartFrame.instance());
    }

    public static void setupServer(String port, String name) {
//        System.out.println("I'm the Server");

        activeFrame.setVisible(false);
        chatFrame = new ChatFrame(name, true);
        clientNames = new ArrayList<>();

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
                        if (clientReader.hasNextLine()) {
                            String clientName = clientReader.nextLine();
                            clientName = clientName.replace(' ', '_');
                            while (clientNames.contains(clientName) || name.equals(clientName)) {
                                clientName += "'";
                            }
                            clientPrinter.println(clientName);
                            clientNames.add(clientName);
                            chatFrame.println(clientName + " has connected!");

                            //TODO: you can only start adding people once the lobby is closed,
                            // because everyone might not get the full playerList
                        }
                        //TODO: figure out why people are getting shown the wrong frames.

                        new Thread(() -> {
                            while(clientReader.hasNextLine()) {
                                String input = clientReader.nextLine();
                                String[] splits = input.split("> ");
                                sendToClients(splits[0], splits[1]);
                                //echo the client input to all other clients.
                                handleInput(input);
                            }
                        }).start();
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }).start();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void setupClient(String address, String port, String name) {

        activeFrame.setVisible(false);
        chatFrame = new ChatFrame(name, false);

        try {
            chatFrame.println("Connecting to " + address + ":" + port);
            Socket toServer = new Socket(address, Integer.parseInt(port));
            Scanner serverReader = new Scanner(toServer.getInputStream());
            serverPrinter = new PrintWriter(toServer.getOutputStream(), true);

            if (serverReader.hasNextLine()) {
                //handles handshake
                chatFrame.println(serverReader.nextLine());
            }

            //sends name
            serverPrinter.println(name);

            //checks for name confirmation and changes name if need be
            if (serverReader.hasNextLine())
                chatFrame.setClientName(serverReader.nextLine());

            new Thread(() -> {
                while(serverReader.hasNextLine())
                    handleInput(serverReader.nextLine());
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void lockServer() {
        accepting = false;
        chatFrame.enableStartButton();
        chatFrame.disableLockButton();
    }

    public static void sendToClients(String name, String text) {
        clientPrinters.forEach(printer -> printer.println(name + "> " + text));
    }

    public static void sendToServer(String name, String text) {
        serverPrinter.println(name + "> " + text);
    }

    public static void handleInput(String input) {
        String[] inputSplit = input.split("> ");
        String name = inputSplit[0];
        if (inputSplit[1].startsWith("/")) {
            chatFrame.println(input); //TODO: temporary
            //#server TODO: check that the player sending a command is in devMode
            //#server TODO: maybe only send some commands to people that submit them. like help.
            String[] cmd = inputSplit[1].substring(1).split(" ");
            switch (cmd[0]) {
                case "num":
                    chatFrame.println(playerNumber + "");
                    break;
                case "repaint":
                    gp.repaint();
                    break;
                //TODO: fix the need to repaint
                case "event":
                    assert game != null;
                    controller.handleEvent(cmd[1] + " " + cmd[2] + " " + cmd[3]);
                    //TODO: change handleEvent so that it takes two args,
                    // 1. the event, 2. the eventArg
                    break;
                case "add":
                    chatFrame.println("Adding " + cmd[1] + " to the game.");
                    chatFrame.addPlayerName(cmd[1]);
                    break;
                case "remove":
                    chatFrame.println("Removing " + cmd[1] + " from the game.");
                    chatFrame.removePlayerName(cmd[1]);
                    break;
                case "lock":
                    chatFrame.println("Lobby Locked.");
                    lockServer();
                    break;
                case "start":
                    if (!accepting)
                        init(seed, name);
                    break;
                case "help":
                    String help;
                    if (cmd.length == 1) {
                        //#server TODO: ready up system, lockButton.setText("Ready");
                        // does completely different thing if you aren't the host.
                        help = "  Commands: \n" +
                                "- add\n" +
                                "- event\n" +
                                "- genRandom\n" +
                                "- help\n" +
                                "- lock\n" +
                                "- remove\n" +
                                "- start";
                        chatFrame.println(help);
                    } else switch (cmd[1]) {
                        case "add":
                            break;
                        case "event":
                            break;
                        case "genRandom":
                            break;
                        case "lock":
                            break;
                        case "remove":
                            break;
                        case "start":
                            break;
                    }
                    break;
            }
        } else {
            chatFrame.println(input);
        }
    }

    public static void init(long seed, String name) {

        List<String> playerNames = chatFrame.getPlayerNames();

        int playerCount = playerNames.size();

        game = new Game(playerNames, seed);
        controllers = new Controller[playerCount];
        gps = new GamePanel[playerCount];
        frames = new JFrame[playerCount]; //temporary.
        //in the final version of the game, delete the JFrame array and make it present only the controlling player's JFrame.
        //also need to only have one GamePanel and one Controller, so they can interface with the network and the Game.

        for (int i = 0; i < playerCount; ++i) {
            frames[i] = new IdiotFrame(i);
        }

        playerNumber = chatFrame.getPlayerNames().indexOf(chatFrame.getClientName());
        if (playerNumber > -1)
            activeFrame = frames[playerNumber];
        else activeFrame = new IdiotFrame();
        activeFrame.setVisible(true);

        gp = gps[playerNumber];
        controller = controllers[playerNumber];

        //#after TODO: make the rubik's square game.
    }

    static class IdiotFrame extends JFrame {
        public IdiotFrame() {
            this(-1);
        }

        public IdiotFrame(int number) {
            super("Idiot: " + chatFrame.getClientName());

            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            //each IdiotFrame has its own Controller and GamePanel
            //constructs GamePanels irrespective of playerNumber, adding them to a game.
            //game should prompt to see if everyone has connected, and then the first player initializes the game and sends it to everyone else

            gps[number] = new GamePanel(game, number, 1920, 1080);
            controllers[number] = new Controller(game, gps[number]);
            add(gps[number], BorderLayout.CENTER);
            pack();
            setResizable(true);
        }
    }
}
