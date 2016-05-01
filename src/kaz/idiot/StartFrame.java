package kaz.idiot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by kasra on 2/20/2016.
 */
public class StartFrame extends JFrame{
    private JTextField ipField;
    private JTextField portField;
    private JLabel empty;
    private JCheckBox hostingCheckBox;
    private JTextField nameField;
    private JButton connectButton;
    private JPanel rootPanel;
    private JPanel titlePanel;
    private JPanel bottomPanel;
    private final Color bg = Color.WHITE;
    private static StartFrame instance = new StartFrame();

    public static StartFrame instance() { return instance;}
    private StartFrame() {
        super("Start");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        rootPanel.setBackground(bg);

        connectButton.addActionListener(ae -> {
            String address = ipField.getText();
            String port = portField.getText();
            String name = nameField.getText();

            if (hostingCheckBox.isSelected())
                name = "host";
            else name = "client";

            if (hostingCheckBox.isSelected())
                Main.setupServer(port, name);
            else Main.setupClient(address, port, name);
        });

        pack();
        setVisible(true);
    }

    private void createUIComponents() {
        titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(bg);
                try {
                    File file = new File("title.png");
                    Image image = ImageIO.read(file);
                    g.drawImage(image, 0, 0, null);
                } catch(IOException e) {
                    System.err.println("Missing title image.");
                    e.printStackTrace();
                }
            }
        };
        bottomPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(bg);
            }
        };
    }
}
