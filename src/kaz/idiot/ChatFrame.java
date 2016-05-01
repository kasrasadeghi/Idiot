package kaz.idiot;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Created by kasra on 4/30/2016.
 */
public class ChatFrame extends JFrame {
    private JPanel rootPanel;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JButton sendButton;
    public JTextField inputField;

    public ChatFrame(String name, boolean isHost) {
        super("Lobby " + name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);

        //try to get it to recognize enters as well?

        ActionListener listener = ae -> {
            String line = inputField.getText().trim();
            inputField.setText("");
            if (line.length() > 0) {
                if (isHost) Main.sendToClient(line);
                else Main.sendToServer(line);
            }
            inputField.requestFocusInWindow();
        };

        sendButton.addActionListener(listener);
        inputField.addActionListener(listener);

        pack();
        setVisible(true);
    }

    public void println(String text) {
        textArea.append(text + "\n");
    }
}
