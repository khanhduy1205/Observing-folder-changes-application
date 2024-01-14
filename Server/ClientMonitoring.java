package Server;

import Model.DataObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;

public class ClientMonitoring implements ActionListener {
    public Socket socket;
    private String path = "";
    private JFrame frame;
    private JLabel ipLabel, portLabel, nameLabel, pathLabel;
    private JButton closeBtn, browseBtn;
    private JTable trackingTable;
    private DefaultTableModel tableModel;
    private JScrollPane trackingTableJScrollPane;
    private String[] trackingAttributes = new String[] { "NO", "Client", "Directory", "Time", "Action", "Description"};
    private Object trackingData = new Object[][]{};

// Browse dialog
//    private JFrame subFrame;
    private JDialog dirJDialog;
    private JPanel dirTreePanel;
    private JTree dirJTree;
    private JButton chooseDirBtn, cancelBtn;
    private JLabel chosenPathLabel;
    private JTextField chosenPathField;
    public static JScrollPane dirJScrollPane;

    public ClientMonitoring(Socket s, String ip, int port, String name, String path) {
        this.socket = s;
        this.path = path;
        initComponents(ip, port, name, path);
        initBrowseDialog();
    }

    public void initBrowseDialog() {
//        subFrame = new JFrame();
        dirJDialog = new JDialog();
        dirTreePanel = new JPanel();
        chosenPathField = new JTextField();
        chosenPathLabel = new JLabel("Path: ");
        chooseDirBtn = new JButton("Open");
        cancelBtn = new JButton("Cancel");
        dirJTree = new JTree();
        dirJScrollPane = new JScrollPane();
        dirJScrollPane.setViewportView(dirJTree);

        dirJDialog.setTitle("Select Directory");
        dirJDialog.setResizable(false);
        dirJDialog.setMinimumSize(new Dimension(450, 400));
        dirJDialog.setModal(true);


        GroupLayout DirectoryTreeLayout = new GroupLayout(dirTreePanel);
        dirTreePanel.setLayout(DirectoryTreeLayout);
        DirectoryTreeLayout.setHorizontalGroup(
                DirectoryTreeLayout.createParallelGroup()
                        .addGroup(DirectoryTreeLayout.createSequentialGroup()
                                .addComponent(dirJScrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        DirectoryTreeLayout.setVerticalGroup(
                DirectoryTreeLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(DirectoryTreeLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(dirJScrollPane)
                                .addContainerGap())
        );

        GroupLayout subFrameLayout = new GroupLayout(dirJDialog.getContentPane());
        dirJDialog.getContentPane().setLayout(subFrameLayout);

        subFrameLayout.setHorizontalGroup(
                subFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(subFrameLayout.createSequentialGroup()
                                .addGap(16,16,16)
                                .addComponent(dirTreePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(subFrameLayout.createParallelGroup()
                                .addGap(16,16,16)
                                .addGroup(subFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(subFrameLayout.createSequentialGroup()
                                                .addGap(216,216,216)
                                                .addComponent(chooseDirBtn, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                                .addGap(16, 16, 16)
                                                .addComponent(cancelBtn,GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(subFrameLayout.createSequentialGroup()
                                                .addGap(16,16,16)
                                                .addComponent(chosenPathLabel, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(chosenPathField, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE))))
        );

        subFrameLayout.setVerticalGroup(
                subFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(subFrameLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(dirTreePanel)
                                .addGroup(subFrameLayout.createSequentialGroup()
                                        .addGroup(subFrameLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(chosenPathLabel)
                                                .addComponent(chosenPathField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(subFrameLayout.createParallelGroup()
                                                .addComponent(chooseDirBtn)
                                                .addComponent(cancelBtn))
                                        .addContainerGap())));

        dirJTree.addTreeExpansionListener(new DirectoryExpansion());
        dirJTree.addTreeSelectionListener(new DirJTreeValueChanged());

        chooseDirBtn.addActionListener(new addOpenFolderListener());
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dirJDialog.setVisible(false);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dirJDialog.setVisible(false);
            }
        });
    }

    public void initComponents(String ip, int port, String name, String path) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame(name);
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
        nameLabel = new JLabel("Name: " + name);
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        nameLabel.setBorder(emptyBorder);
        pathLabel = new JLabel("Path: " + path);
        pathLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        pathLabel.setBorder(emptyBorder);

        //        init button
        closeBtn = new JButton("Close");
        browseBtn = new JButton("Browse");

        //        init tables
        trackingTable = new JTable();
        trackingTableJScrollPane = new JScrollPane();
        tableModel = new DefaultTableModel((Object[][]) trackingData, trackingAttributes);
        trackingTable.setModel(tableModel);
        trackingTableJScrollPane.setViewportView(trackingTable);
        trackingTableJScrollPane.setPreferredSize(new Dimension(800, 400));
        TableColumnModel tableColumnModel = trackingTable.getColumnModel();
        tableColumnModel.getColumn(0).setPreferredWidth(30);
        tableColumnModel.getColumn(1).setPreferredWidth(100);
        tableColumnModel.getColumn(2).setPreferredWidth(250);
        tableColumnModel.getColumn(3).setPreferredWidth(120);
        tableColumnModel.getColumn(4).setPreferredWidth(100);
        tableColumnModel.getColumn(5).setPreferredWidth(200);

        JPanel tablePanel = new JPanel(new FlowLayout());
        tablePanel.add(trackingTableJScrollPane);

        JPanel labelPanel = new JPanel(new FlowLayout());
        //        labelPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        labelPanel.add(ipLabel);
        labelPanel.add(portLabel);
        labelPanel.add(nameLabel);
        labelPanel.add(pathLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(browseBtn);
        buttonPanel.add(closeBtn);

        //        init top panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(labelPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(buttonPanel, gbc);

        frame.add(topPanel, BorderLayout.PAGE_START);
        frame.add(tablePanel, BorderLayout.PAGE_END);

        browseBtn.addActionListener(new addBrowseListener());
        closeBtn.addActionListener(new addCloseListener());
    }

    public void addObjectToTable(String path, String date, String action, String name, String message) {
        Object[] obj = new Object[] { tableModel.getRowCount() + 1, name, path,
                date, action, message };
        tableModel.addRow(obj);
        trackingTable.setModel(tableModel);
    }

    public void updateTreeDirectory(File[] dirRoots) throws IOException {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        for (File folder : dirRoots) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(folder);
            childNode.insert(new DefaultMutableTreeNode(), 0);
            root.add(childNode);
        }

        DefaultTreeModel model = (DefaultTreeModel) dirJTree.getModel();
        model.setRoot(root);
        dirJTree.setModel(model);
    }

    public void addChildrenToCurrentNode(DefaultMutableTreeNode currentNode, File[] folders) {
        try {
            if (currentNode == null || currentNode.getUserObject() == null || folders == null)
                return;

            for (File folder : folders) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(folder);
                childNode.insert(new DefaultMutableTreeNode(), 0);
                currentNode.add(childNode);
            }

            if (currentNode.getChildAt(0) != null) {
                DefaultMutableTreeNode firstChildNode =  (DefaultMutableTreeNode) currentNode.getChildAt(0);
                if (firstChildNode.getUserObject() == null) {
                   currentNode.remove(firstChildNode);
                }
            }

            DefaultMutableTreeNode root = (DefaultMutableTreeNode) dirJTree.getModel().getRoot();
            Enumeration bfs = root.breadthFirstEnumeration();

            boolean isFound = false;
            DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode();
            while (bfs.hasMoreElements() && !isFound) {
                tempNode = (DefaultMutableTreeNode) bfs.nextElement();
                if (tempNode.getUserObject() != null &&
                    tempNode.getUserObject().toString().equals(currentNode.getUserObject().toString())) {
                    isFound = true;
                }
            }

//            delete and replace new node
            if (isFound) {
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) tempNode.getParent();
                int index = parentNode.getIndex(tempNode);

                DefaultTreeModel model = (DefaultTreeModel) dirJTree.getModel();
                model.removeNodeFromParent(tempNode);
                model.insertNodeInto(currentNode, parentNode, index);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    class DirJTreeValueChanged implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) dirJTree.getLastSelectedPathComponent();
            if (currentNode != null) {
                chosenPathField.setText(currentNode.getUserObject().toString());
            }
        }
    }

    class DirectoryExpansion implements TreeExpansionListener {
        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            DefaultMutableTreeNode selectedFolder = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

           if (selectedFolder.getChildCount() == 1 ) {

               DataObject newDataSend = new DataObject("server", "expand","",
                       "", ServerChat.dataSend.getDirectoryPath());
               newDataSend.getDirectoryPath().setDirectoryNode(selectedFolder);

               for (ServerChat currenChat: Server.clientChats) {
                   if (currenChat.socket == socket) {
                       System.out.println("expand");
                       currenChat.sendMessage(newDataSend);
                   }
               }

           }
        }
        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
        }
    }

    public void actionPerformed(ActionEvent e) {}

    public void showGUI() {
        frame.pack();
        frame.setVisible(true);
    }

    class addBrowseListener implements ActionListener {
        public void actionPerformed (ActionEvent e) {
            dirJDialog.setVisible(true);
        }
    }

    class addOpenFolderListener implements ActionListener {
        public void actionPerformed (ActionEvent e) {

            String newPath = chosenPathField.getText();

            if(newPath != "") {
                path = newPath;
                pathLabel.setText("Path: " + newPath);

                DataObject dataSend = new DataObject(ServerChat.dataSend.getName(), "change", "",
                        path, ServerChat.dataSend.getDirectoryPath());

                for (ServerChat currenChat: Server.clientChats) {
                    if (currenChat.socket == socket) {
                       currenChat.sendMessage(dataSend);
                    }
                }
                dirJDialog.setVisible(false);
            }
        }
    }

    class addCancelDialogListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dirJDialog.setVisible(false);
        }
    }

    class addCloseListener implements ActionListener {
        public void actionPerformed (ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(frame, "Do you want to close?", "Inform", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                frame.dispose();
            }
        }
    }
}