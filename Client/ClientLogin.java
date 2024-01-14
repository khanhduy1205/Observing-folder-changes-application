package Client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientLogin extends JFrame implements ActionListener {

    public static JFrame frame;
    private JLabel ipLabel, portLabel, nameLabel, titleLable;
    private JTextField ipField, portField, nameField;
    private JButton connectBtn;
    public ClientLogin() {
        initComponents();
    }

    public void initComponents() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Client Login");
        frame.setPreferredSize(new Dimension(300, 300));
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        Border emptyBorder = new EmptyBorder(5, 20, 5, 10);

//        init labels
        titleLable = new JLabel("Client");
        titleLable.setFont(new Font("Tahoma", Font.PLAIN, 28));
        ipLabel = new JLabel("IP: ");
        ipLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        ipLabel.setBorder(emptyBorder);
        portLabel = new JLabel("Port: ");
        portLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        portLabel.setBorder(emptyBorder);
        nameLabel = new JLabel("Name: ");
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        nameLabel.setBorder(emptyBorder);

//        init text field
        ipField = new JTextField(10);
        ipField.setFont(new Font("Tahoma", Font.PLAIN, 14));

        portField = new JTextField(10);
        portField.setFont(new Font("Tahoma", Font.PLAIN, 14));

        nameField = new JTextField(10);
        nameField.setFont(new Font("Tahoma", Font.PLAIN, 14));

        ipField.setText("localhost");

//        init button
        connectBtn = new JButton("Connect");
        connectBtn.setPreferredSize(new Dimension(90, 35));

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
        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        topPanel.add(nameField, gbc);

        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.add(titleLable);
        titlePanel.setBorder(new EmptyBorder(20,5,0,5));

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(new EmptyBorder(0, 5,40,5));
        bottomPanel.add(connectBtn);

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
            String name = nameField.getText();

            if (name.equals("")){
                JOptionPane.showMessageDialog(frame,"Please enter your name");
            }
            else {
                try {
                    ClientUI clientUI = new ClientUI(ip, port, name);
                    frame.setVisible(false);
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(frame,"Can't connect to Server");
                }
            }
        }
    }

    public void actionPerformed (ActionEvent e) {}

    public static void main(String args[]) {
        ClientLogin clientLogin = new ClientLogin();
    }
}
