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
    public static GamePanel[] gps;
    public static GamePanel gp;
    public static Controller[] controllers;
    public static Controller controller;
    public static Game game;
    public static JFrame[] gameFrames;
    public static JFrame gameFrame;
    //TODO: reduce to one GamePanel
    //TODO: reduce to one Controller
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
            //TODO: handle closing the game.
            serverSocketListenerThread.start();
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
                    if (canAddPlayers) {
                        chatFrame.println("Adding all players to the game.");
                    }
                    if (chatFrame.isHosting() && canAddPlayers) {
                        sendToClients(name, "/add-s " + chatFrame.getClientName());
                        handleInput(name + "> " + "/add-s " + chatFrame.getClientName());
                        for (String clientName : clientNames) {
                            sendToClients(name, "/add-s " + clientName);
                            handleInput(name + "> " + "/add-s " + clientName);
                        }
                    }
                    //TODO: make order randomizer for game start. maybe use seed?
                    break;
                case "seed":
                    //TODO: generate seed;
                    chatFrame.println("Generating new seed.");
                    try {
                        seed = new Random(Long.valueOf(cmd[1])).nextLong();
                    } catch (NumberFormatException e) {
                        chatFrame.println("See /help seed for information on how to use this command.");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        seed = new Random(seed).nextLong();
                    }
                    break;
                case "printSeed":
                    chatFrame.println(seed + "");
                    break;
                case "currentPlayer":
                    assert game != null;
                    chatFrame.println(game.getCurrentPlayerNumber() + "");
                    break;
                case "repaint": //TODO: organize command cases alphabetically
                    Main.chatFrame.println("This is a dangerous function to use.");
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
                    chatFrame.println("Adding " + cmd[1] + " to the game.");//TODO: do try-catch for cases that use cmd to output "see help".
                case "add-s":
                    chatFrame.addPlayerName(cmd[1]);
                    break;
                case "remove":
                    chatFrame.println("Removing " + cmd[1] + " from the game.");
                    chatFrame.removePlayerName(cmd[1]);
                    break;
                case "lock":
                    chatFrame.println("Lobby locked.");
                    lockServer();
                    break;
                case "start":
                    if (!accepting) {
                        if (chatFrame.getPlayerNames().size() < 0)
                            chatFrame.println("Add at least 2 players to the game.");
                        else init(seed, name);
                    }
                    break;
                case "help":
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
                default:
                    chatFrame.println(input);
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
        gameFrames = new JFrame[playerCount]; //temporary.
        //in the final version of the game, delete the JFrame array and make it present only the controlling player's JFrame.
        //also need to only have one GamePanel and one Controller, so they can interface with the network and the Game.

        for (int i = 0; i < playerCount; ++i) {
            gameFrames[i] = new IdiotFrame(i);
        }

        playerNumber = chatFrame.getPlayerNames().indexOf(chatFrame.getClientName());
        if (playerNumber > -1)
            gameFrame = gameFrames[playerNumber];
        else gameFrame = new IdiotFrame();
        gameFrame.setVisible(true);

        gp = gps[playerNumber];
        controller = controllers[playerNumber];

        //#after TODO: make the rubik's square game.
    }

    static class IdiotFrame extends JFrame {
        public IdiotFrame() {
            //TODO: create spectator idiotFrames
            this(-1);
            //TODO: when players are in spectate mode give them spectator idiotFrames
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
