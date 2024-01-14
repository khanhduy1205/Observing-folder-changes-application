package Server;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ServerLogin extends JFrame implements ActionListener {
    public static JFrame frame;
    private JLabel ipLabel, portLabel, titleLable;
    private JTextField ipField, portField;
    private JButton connectBtn;

    public ServerLogin() {
        initComponents();
    }

    public void initComponents() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Server Login");
        frame.setPreferredSize(new Dimension(300, 300));
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        Border emptyBorder = new EmptyBorder(5, 20, 5, 10);

//        init labels
        titleLable = new JLabel("SERVER");
        titleLable.setFont(new Font("Tahoma", Font.PLAIN, 28));
        ipLabel = new JLabel("IP: ");
        ipLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        ipLabel.setBorder(emptyBorder);

        portLabel = new JLabel("Port: ");
        portLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        portLabel.setBorder(emptyBorder);

//        init text field
        ipField = new JTextField("127.0.0.1",10);
        ipField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        portField = new JTextField( 10);
        portField.setFont(new Font("Tahoma", Font.PLAIN, 14));

//        init button
        connectBtn = new JButton("Start");
        connectBtn.setPreferredSize(new Dimension(70, 35));

//        init top panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(ipLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        topPanel.add(ipField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(portLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        topPanel.add(portField, gbc);

        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.add(titleLable);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(connectBtn);

        titlePanel.setBorder(new EmptyBorder(20,5,0,5));
        topPanel.setBorder(new EmptyBorder(-20, 5,5,5));
        bottomPanel.setBorder(new EmptyBorder(0, 5,40,5));
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(topPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.PAGE_END);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        connectBtn.addActionListener(new addConnectButtonListener());
    }

    class addConnectButtonListener implements ActionListener {
        public void actionPerformed (ActionEvent e) {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());

//            check port
            try {
                if (port > 0 && port < 10000) {
                    ServerUI serverUI = new ServerUI(ip, port);
                    frame.setVisible(false);
                    frame.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(frame,"Invalid port! Please try again.");
                }

            } catch (IOException err) {
                JOptionPane.showMessageDialog(frame,"Can't start server! Please check port again.");
                err.printStackTrace();
            }
        }
    }

    public void actionPerformed (ActionEvent e) {}

    public static void main(String args[]) {
        ServerLogin serverLogin = new ServerLogin();
    }
}
