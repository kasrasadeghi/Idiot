package kaz.idiot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by kasra on 4/30/2016.
 */
public class ChatFrame extends JFrame {
    private JPanel chatPanel;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JButton sendButton;
    public JTextField inputField;
    private JList<String> viewedList;
    private DefaultListModel<String> playerNameList;
    private JButton startButton;
    private JButton lockButton;
    private Font monospacedFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    public ChatFrame(String name, boolean isHost) {
        super("Lobby " + name + ((isHost)?  " - Host": "") );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(chatPanel);

        playerNameList = new DefaultListModel<>();
        viewedList.setModel(playerNameList);

        textArea.setFont(monospacedFont);
        inputField.setFont(monospacedFont);
        //try to get it to recognize enters as well?

        ActionListener listener = ae -> {
            String text = inputField.getText().trim();
            inputField.setText("");
            if (text.length() > 0) {
                if (isHost) {
                    Main.handleInput(name + "> " + text);
                    Main.sendToClients(name, text);
                }
                else Main.sendToServer(name, text);
            }
            inputField.requestFocusInWindow();
        };

        sendButton.addActionListener(listener);
        inputField.addActionListener(listener);

        startButton.addActionListener(ae -> {
            //#server TODO: initializing game
        });

        lockButton.addActionListener(ae -> {

        });

        pack();
        setVisible(true);
    }

    public void println(String text) {
        textArea.append(text + "\n");
    }

    public void addPlayerName(String name) {
        playerNameList.addElement(name);
    }

    public void removePlayerName(String name) {
        playerNameList.removeElement(name);
    }
}
