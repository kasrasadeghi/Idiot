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
    private JList list1;
    private JButton startButton;
    private JButton lockButton;
    private Font monospacedFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    public ChatFrame(String name, boolean isHost) {
        super("Lobby " + name + ((isHost)?  " - Host": "") );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(chatPanel);

        textArea.setFont(monospacedFont);
        inputField.setFont(monospacedFont);
        //try to get it to recognize enters as well?

        ActionListener listener = ae -> {
            String line = inputField.getText().trim();
            inputField.setText("");
            if (line.length() > 0) {
                if (isHost) Main.sendToClients(name, line);
                else Main.sendToServer(name, line);
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
