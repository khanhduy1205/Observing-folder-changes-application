package Server;

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
import java.io.IOException;
import java.util.ArrayList;

public class ServerUI extends JFrame implements ActionListener {

    private JFrame frame;
    private JLabel ipLabel, portLabel, clientsLabel;
    private JButton disconnectBtn, monitorItemBtn;
    public static JTable trackingTable;
    public static DefaultTableModel tableModel;
    private static JList <String> clients;
    private static DefaultListModel listModel;
    private JScrollPane trackingTableJScrollPane;
    private String[] trackingAttributes = new String[] { "NO", "Client", "Directory", "Time", "Action", "Description"};
    public static Object trackingData = new Object[][]{};
    public static String path = "", ip;
    public static int port;
    public static ArrayList<ClientMonitoring> clientMonitorings = new ArrayList<>();
    public JFrame getFrame() {
        return frame;
    }
    public ServerUI(String inIP, int inPort) throws IOException {

        if (Server.serverSocket != null && !Server.serverSocket.isClosed()) {
            JOptionPane.showMessageDialog(frame, "Server is running!");
        }
        else {
            ip = inIP;
            port = inPort;
            Server server = new Server(port);
            Thread thread =  new Thread(new ServerHandler(server));
            thread.start();
            initComponents(ip, port);
        }
    }

    public void initComponents(String ip, int port) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("Server");
        frame.setPreferredSize(new Dimension(1000, 500));
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        Border emptyBorder = new EmptyBorder(5, 20, 5, 10);

        //        init labels
        ipLabel = new JLabel("IP: " + ip);
        ipLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        ipLabel.setBorder(emptyBorder);
        portLabel = new JLabel("Port: " + port);
        portLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        portLabel.setBorder(emptyBorder);
        clientsLabel = new JLabel("Clients");
        clientsLabel.setBorder(new EmptyBorder(5, 0, 5, 10));

        //        init button
        disconnectBtn = new JButton("Close");
        disconnectBtn.setPreferredSize(new Dimension(70, 35));
        monitorItemBtn = new JButton("Monitor");

        //        init tables
        trackingTable = new JTable();
        trackingTableJScrollPane = new JScrollPane();
        tableModel = new DefaultTableModel((Object[][]) trackingData, trackingAttributes);
        trackingTable.setModel(tableModel);
        trackingTableJScrollPane.setViewportView(trackingTable);
        trackingTableJScrollPane.setPreferredSize(new Dimension(800, 380));
        TableColumnModel tableColumnModel = trackingTable.getColumnModel();
        tableColumnModel.getColumn(0).setPreferredWidth(30);
        tableColumnModel.getColumn(1).setPreferredWidth(100);
        tableColumnModel.getColumn(2).setPreferredWidth(250);
        tableColumnModel.getColumn(3).setPreferredWidth(120);
        tableColumnModel.getColumn(4).setPreferredWidth(100);
        tableColumnModel.getColumn(5).setPreferredWidth(200);


        clients = new JList<String>();
        listModel = new DefaultListModel();
        clients.setModel(listModel);
        JScrollPane clientJScrollpane = new JScrollPane(clients);
        clientJScrollpane.setPreferredSize(new Dimension(150, 360));

        JPanel clientsPanel = new JPanel(new GridBagLayout());
        clientsPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        clientsPanel.add(clientsLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        clientsPanel.add(monitorItemBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        clientsPanel.add(clientJScrollpane, gbc);

        JPanel tablePanel = new JPanel(new FlowLayout());

        tablePanel.add(clientsPanel);
        tablePanel.add(trackingTableJScrollPane);

        //        init top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel labelPanel = new JPanel(new FlowLayout());

        //        init closePanel
        JPanel closePanel = new JPanel(new FlowLayout());
        closePanel.setBorder(new EmptyBorder(5, 0, 10, 10));
        labelPanel.add(ipLabel);
        labelPanel.add(portLabel);
        closePanel.add(disconnectBtn);
        topPanel.add(labelPanel, BorderLayout.CENTER);
        topPanel.add(closePanel, BorderLayout.LINE_END);

        frame.add(topPanel, BorderLayout.PAGE_START);
        frame.add(tablePanel, BorderLayout.PAGE_END);

        frame.pack();
        frame.setVisible(true);

        disconnectBtn.addActionListener(new addCloseServerListener());
        monitorItemBtn.addActionListener(new addMonitorItemListener());
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Close server?", "Notice", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    ServerHandler.server.serverClose();
                    frame.dispose();
                    ServerLogin serverLogin = new ServerLogin();
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

    public static void addClientNameToTable(String name) {
        listModel.addElement(name);
        clients.setModel(listModel);
    }

    public static void removeClientNameToTable(String name) {
        listModel.removeElement(name);
        clients.setModel(listModel);
    }

    public void actionPerformed(ActionEvent e) {}

    class addCloseServerListener implements ActionListener {

        public void actionPerformed (ActionEvent e) {

            int result = JOptionPane.showConfirmDialog(frame, "Close server?", "Notice", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                ServerHandler.server.serverClose();
                frame.dispose();
                ServerLogin serverLogin = new ServerLogin();
            }
        }
    }

    class addMonitorItemListener implements ActionListener {
        public void actionPerformed (ActionEvent e) {

            String name = clients.getSelectedValue();
            int index = Server.clientNames.indexOf(name);

            if (name == null) {
                JOptionPane.showMessageDialog(frame, "Nothing selected! Please try again");
            }
            else {
                clientMonitorings.get(index).showGUI();
            }
        }
    }
}