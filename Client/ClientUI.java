package Client;

import Model.DataObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientUI extends JFrame implements ActionListener {
    public static JFrame frame;
    public static JLabel ipLabel, portLabel, nameLabel, pathLabel;
    public static JButton disconnectBtn;
    public static JTable trackingTable;
    public static DefaultTableModel tableModel;
    private JScrollPane trackingTableJScrollPane;
    private String[] trackingAttributes = new String[] { "NO", "Client", "Directory", "Time", "Action", "Description"};
    public static Object trackingData = new Object[][]{};
    public static String path = "";

    public ClientUI(String ip, int port, String name) {

        ClientChat clientChat = new ClientChat();
        int result = clientChat.connect(ip, port, name);

        // Thong bao khi khong the ket noi
        if (result == -1) {
            System.out.println("Can't connect to server");
            JOptionPane.showMessageDialog(frame,"Can't connect to Server");
            ClientLogin.frame.setVisible(true);
        }
        else if (result == 1) {
            initComponents(ip, port, name); // khoi tao giao dien
            String datetime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            ClientUI.addObjectToTable(path.toString(), datetime, "Connect", name, name + " has connected");

            new Thread(clientChat).start();
        }
    }

//    Tao giao dien cho client
    public void initComponents(String ip, int port, String name) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Client");
        frame.setPreferredSize(new Dimension(900, 500));
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setBounds(300,150, 1000, 500);

        Border emptyBorder = new EmptyBorder(5, 20, 5, 10);

//        init top panel
        JPanel topPanel = new JPanel(new FlowLayout());

//        init labels
        ipLabel = new JLabel("IP: " + ip);
        ipLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        ipLabel.setBorder(emptyBorder);
        portLabel = new JLabel("Port: " + port);
        portLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        portLabel.setBorder(emptyBorder);
        nameLabel = new JLabel("Name: " + name);
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        nameLabel.setBorder(emptyBorder);
        pathLabel = new JLabel("Path: " + path);
        pathLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        pathLabel.setBorder(emptyBorder);

//        init button
        disconnectBtn = new JButton("Disconnect");

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(disconnectBtn);

//        init tables
        trackingTable = new JTable();
        trackingTableJScrollPane = new JScrollPane();
        tableModel = new DefaultTableModel((Object[][]) trackingData, trackingAttributes);
        trackingTable.setModel(tableModel);
        trackingTableJScrollPane.setViewportView(trackingTable);
        trackingTableJScrollPane.setPreferredSize(new Dimension(800, 350));
        TableColumnModel tableColumnModel = trackingTable.getColumnModel();
        tableColumnModel.getColumn(0).setPreferredWidth(30);
        tableColumnModel.getColumn(1).setPreferredWidth(100);
        tableColumnModel.getColumn(2).setPreferredWidth(250);
        tableColumnModel.getColumn(3).setPreferredWidth(120);
        tableColumnModel.getColumn(4).setPreferredWidth(100);
        tableColumnModel.getColumn(5).setPreferredWidth(200);


        JPanel tablePanel = new JPanel(new FlowLayout());
        tablePanel.add(trackingTableJScrollPane);

        topPanel.add(ipLabel);
        topPanel.add(portLabel);
        topPanel.add(nameLabel);
        topPanel.add(pathLabel);

        frame.add(topPanel, BorderLayout.PAGE_START);
        frame.add(btnPanel, BorderLayout.CENTER);
        frame.add(tablePanel, BorderLayout.PAGE_END);

        frame.pack();
        frame.setVisible(true);

        disconnectBtn.addActionListener(new addDisconectedListener());
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Close Client?", "Notice", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
//                  send close option to server
                    DataObject DataClose = new DataObject(ClientChat.client.getName(), "quit",
                            ClientChat.client.getName() + " has disconnected", ClientChat.defaultPath, ClientChat.directoryPath);
                    ClientChat.sendMessage(DataClose);

                    ClientChat.closeConnection();
                    frame.dispose();
                    ClientLogin clientLogin = new ClientLogin();
                }
                else {
                    frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    public static void addObjectToTable(String path, String date, String action, String name, String message) {
        Object[] obj = new Object[] { tableModel.getRowCount() + 1, name, path,
                date, action, message };
        tableModel.addRow(obj);
        trackingTable.setModel(tableModel);
    }

    class addDisconectedListener implements ActionListener {
        public void actionPerformed (ActionEvent e) {

            int result = JOptionPane.showConfirmDialog(frame, "Close client?", "Notice", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {

                DataObject DataClose = new DataObject(ClientChat.client.getName(), "quit",
                        ClientChat.client.getName() + " has disconnected", ClientChat.defaultPath, ClientChat.directoryPath);
                ClientChat.sendMessage(DataClose);

                ClientChat.closeConnection();
                frame.dispose();
                ClientLogin clientLogin = new ClientLogin();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {}
}
