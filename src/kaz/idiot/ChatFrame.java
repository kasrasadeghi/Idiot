package kaz.idiot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.awt.event.ActionListener;

/**
 * Created by kasra on 4/30/2016.
 */
public class ChatFrame extends JFrame {
    private JPanel chatPanel;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JButton sendButton;
    private JTextField inputField;
    private JList<String> viewedList;
    private DefaultListModel<String> playerNameList;
    private JButton startButton;
    private JButton lockButton;
    private Font monospacedFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private boolean host;
    private String clientName;

    public ChatFrame(String name, boolean isHost) {
        super("Lobby " + name + ((isHost)?  " - Host": "") );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(chatPanel);
        this.host = isHost;
        this.clientName = name;

        playerNameList = new DefaultListModel<>();
        viewedList.setModel(playerNameList);

        startButton.setEnabled(false);

        textArea.setFont(monospacedFont);
        inputField.setFont(monospacedFont);
        //try to get it to recognize enters as well?

        ActionListener listener = ae -> {
            String text = inputField.getText().trim();
            inputField.setText("");
            if (text.length() > 0) {
                send( text);
            }
            inputField.requestFocusInWindow();
        };

        sendButton.addActionListener(listener);
        inputField.addActionListener(listener);

        startButton.addActionListener(ae -> {
            send("/start");
        });

        lockButton.addActionListener(ae -> {
            send("/lock");
        });

        pack();
        setVisible(true);
    }

    public void println(String text) {
        textArea.append(text + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void addPlayerName(String name) {
        if (Main.canAddPlayers)
            playerNameList.addElement(name);
        else println("Lock the server before adding players.");
    }

    public void removePlayerName(String name) {
        playerNameList.removeElement(name);
    }

    public void enableStartButton() {
        startButton.setEnabled(true);
    }

    public void send(String text) {
        if (host) {
            Main.handleInput(clientName + "> " + text);
            Main.sendToClients(clientName, text);
        }
        else Main.sendToServer(clientName, text);
    }

    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        Enumeration<String> playerNameEnumeration = playerNameList.elements();
        while(playerNameEnumeration.hasMoreElements())
            names.add(playerNameEnumeration.nextElement());
        return names;
    }

    public void setClientName(String name) {
        this.clientName = name;
        setTitle("Lobby " + name + ((host)?  " - Host": "") );
    }

    public String getClientName() {
        return clientName;
    }

    public void disableLockButton() {
        lockButton.setEnabled(false);
    }

    public boolean isHosting() {
        return host;
    }
}
