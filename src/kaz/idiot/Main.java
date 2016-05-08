package kaz.idiot;

import javax.swing.*;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static final int CARD_X = 79;
    public static final int CARD_Y = 123;
    public static GamePanel gp;
    public static Controller controller;
    public static Game game;
    public static JFrame gameFrame;
    public static StartFrame startFrame;

    public static List<PrintWriter> clientPrinters;
    public static List<String> clientNames;
    public static PrintWriter serverPrinter;
    public static ChatFrame chatFrame;
    public static boolean accepting = true;
    public static long seed = 8912;
    public static int playerNumber = -1;
    public static Thread serverSocketListenerThread;
    public static boolean canAddPlayers = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> startFrame = StartFrame.instance());
    }

    public static void setupServer(String port, String name) {

        startFrame.setVisible(false);
        chatFrame = new ChatFrame(name, true);
        clientPrinters = new ArrayList<>();
        clientNames = new ArrayList<>();

        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
            chatFrame.println("Hosting on " + port);

            serverSocketListenerThread = new Thread(() -> {
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
                        }

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
            });
            serverSocketListenerThread.start();
            //TODO: handle closing the game.
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void setupClient(String address, String port, String name) {

        startFrame.setVisible(false);
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
            //TODO: make thread that checks if clients are still even there. something to do with .open()?
            //TODO: learn more about sockets

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
        //TODO: AI
        //1. pick the rightmost playable card
        //2. pick the best play (gets rid of the most cards with the least value)
        //3. neural network/deep learning nonsense
        //TODO: sort hand button
    }

    public static void lockServer() {
        accepting = false;
        canAddPlayers = true;
        chatFrame.enableStartButton();
        chatFrame.disableLockButton();
        serverSocketListenerThread = null;
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
            //#server TODO: maybe only send some commands to people that submit them. like help.
            String[] cmd = inputSplit[1].substring(1).split(" ");
            switch (cmd[0]) {
                //TODO: make more commands silence-able
                //TODO: maybe make commands printable "-p"
                case "all":
                    if (chatFrame.isHosting() && canAddPlayers) {
                        sendToClients(name, "/add-s " + chatFrame.getClientName());
                        handleInput(name + "> " + "/add-s " + chatFrame.getClientName());
                        for (String clientName : clientNames) {
                            sendToClients(name, "/add-s " + clientName);
                            handleInput(name + "> " + "/add-s " + clientName);
                        }
                    }
                    if (canAddPlayers) {
                        chatFrame.println("Adding all players to the game.");
                    }
                    //TODO: make order randomizer for game start. maybe use seed?
                    break;
                case "listPlayers":
                    for (String playerName : chatFrame.getPlayerNames())
                        chatFrame.println(playerName);
                    break;
                case "seed-s":
                    try {
                        seed = new Random(Long.valueOf(cmd[1])).nextLong();
                    } catch (NumberFormatException e) {
                        chatFrame.println("See /help seed for information on how to use this command.");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        seed = new Random(seed).nextLong();
                    }
                case "seed":
                    //TODO: generate seed;
                    chatFrame.println("Generating new seed.");
                    break;
                case "printSeed":
                    chatFrame.println(seed + "");
                    break;
                case "currentPlayer":
                    assert game != null;
                    chatFrame.println(game.getCurrentPlayerNumber() + "");
                    break;
                case "repaint":
                    //TODO: organize command cases alphabetically
                    Main.chatFrame.println("Use of the repaint function is not advised.");
                    gp.repaint();
                    break;
                case "checkWin":
                    chatFrame.println(game.checkRoundOver() + "");
                    break;
                case "event":
                    assert game != null;
                    chatFrame.println("event handling: " + cmd[1] + ", " + cmd[2] + ", " + cmd[3] + ".");
                    controller.handleEvent(cmd[1], cmd[2], cmd[3]);
                    break;
                case "add":
                    chatFrame.println("Adding " + cmd[1] + " to the game.");
                    //TODO: do try-catch for cases that use cmd to output "see help".
                case "add-s":
                    chatFrame.addPlayerName(cmd[1]);
                    break;
                case "remove":
                    try {
                        chatFrame.println("Removing " + cmd[1] + " from the game.");
                        chatFrame.removePlayerName(cmd[1]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        chatFrame.println("See /help remove for how to use the remove command.");
                    }
                    break;
                case "lock":
                    chatFrame.println("Lobby locked.");
                    lockServer();
//                    handleInput(name + "> " + "/all"); //TODO add all players at beginning
                    break;
                case "start":
                    if (!accepting) {
                        if (chatFrame.getPlayerNames().size() < 0)
                            chatFrame.println("Add at least 2 players to the game.");
                        else init(seed, name);
                    }
                    break;
                case "help":
                    if (chatFrame.getClientName().equals(name)) {
                        String help = "";
                        if (cmd.length == 1) {
                            //#server TODO: ready up system, lockButton.setText("Ready");
                            //TODO: finish help command
                            // does completely different thing if you aren't the host.
                            help = "  Commands: \n" +
                                    "- add\n" +
                                    "- event\n" +
                                    "- genRandom\n" +
                                    "- help\n" +
                                    "- lock\n" +
                                    "- remove\n" +
                                    "- start";

                        } else switch (cmd[1]) {
                            case "add":
                                break;
                            case "event":
                                help += "Don't call this unless you know what you're doing.\n";
                                break;
                            case "genRandom":
                                break;
                            case "lock":
                                break;
                            case "remove":
                                break;
                            case "start":
                                break;
                            default:
                                help = "  Commands: \n" +
                                        "- add\n" +
                                        "- event\n" +
                                        "- genRandom\n" +
                                        "- help\n" +
                                        "- lock\n" +
                                        "- remove\n" +
                                        "- start";
                        }
                        chatFrame.println(help);
                        break;
                    }
                default:
                    chatFrame.println(input);
            }
        } else {
            chatFrame.println(input);
        }
    }

    public static void init(long seed, String name) {

        List<String> playerNames = chatFrame.getPlayerNames();


        game = new Game(playerNames, seed);
        playerNumber = playerNames.indexOf(chatFrame.getClientName());
        //may be useful for spectators

        /*
        int playerCount = playerNames.size();
        controllers = new Controller[playerCount];
        gps = new GamePanel[playerCount];
        gameFrames = new JFrame[playerCount];
        for (int i = 0; i < playerCount; ++i) {
            gameFrames[i] = new IdiotFrame(i);
        }
        gp = gps[playerNumber];
        controller = controllers[playerNumber];
        */

        if (playerNumber > -1)
            gameFrame = new IdiotFrame(playerNumber);
        else gameFrame = new IdiotFrame();
        gameFrame.setVisible(true);



        //#after TODO: make the rubik's square game.
    }

    static class IdiotFrame extends JFrame {
        public IdiotFrame() {
            super("Idiot - " + chatFrame.getClientName() + " - Spectator");
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            initGamePanel(0);
            //TODO: finish spectator mode.
            // maybe have thread update current player?
            pack();
            setResizable(true);
        }

        public IdiotFrame(int number) {
            super("Idiot - " + chatFrame.getClientName());

            setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            initGamePanel(number);
//            gps[number] = new GamePanel(game, number, 1920, 1080);
//            controllers[number] = new Controller(game, gps[number]);
//            add(gps[number], BorderLayout.CENTER);

            pack();
            setResizable(true);
        }

        private void initGamePanel(int number) {
            gp = new GamePanel(game, number, 100, 100);
            controller = new Controller(game, gp);
            add(gp, BorderLayout.CENTER);
        }
    }
}
