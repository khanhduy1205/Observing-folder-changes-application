package Client;

import Model.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClientChat implements Runnable {

    public static Client client;
    public static Socket socket;
    public static ObjectInputStream objectInputStream;
    public static ObjectOutputStream objectOutputStream;
    public static DirectoryPath directoryPath;
    public static String defaultPath;
    public ClientChat()  {}

    public String getName() {
        return client.getName();
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setPathDirectory(String defaultPath) {
        ClientChat.defaultPath = defaultPath;
    }

    public int connect(String ip, int port, String name) {
        try {
            client = new Client(ip, name, port);
            socket = new Socket(ip, port);
            directoryPath = new DirectoryPath();

            Path path = Paths.get("").toAbsolutePath();
            defaultPath = path.getParent().toString();
            ClientUI.path = defaultPath;

            DataObject dataSend = new DataObject(client.getName(), "connect",
                    client.getName() + " has connected", defaultPath, directoryPath);
            sendMessage(dataSend);

//            receive first responding from data
            DataObject dataReceive = listenForMessage();

            if (dataReceive.getInfo().equals("exist")) {
                JOptionPane.showMessageDialog(ClientLogin.frame, "Someone is using this name");
                ClientLogin.frame.setVisible(true);
                return 0;
            } else if (dataReceive.getInfo().equals("connect")) {
                if (client.getName() == null) {
                    client.setName(name);
                }
            }

//           start to watch folder
            WatchFolder watchFolder = new WatchFolder(this);
            new Thread(watchFolder).start();
        } catch (Exception e) {
            closeConnection();
//            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    public void run() {
        DataObject dataReceive = null;
        while (socket != null && !socket.isClosed()) {
            try {
            dataReceive = listenForMessage();
                if (dataReceive != null) {

                    String datetime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                    switch (dataReceive.getInfo()) {
                        case "close": // server quit
                        {
                            System.out.println(dataReceive.getName() + "," + "Disconnected to server" + "," + datetime);
                            ClientUI.addObjectToTable(defaultPath, datetime, "Disconnect", "Server", "Server closed");
                            JOptionPane.showMessageDialog(ClientUI.frame, "Server has closed");
                            closeConnection();

                            ClientUI.frame.dispose();
                            ClientLogin clientLogin = new ClientLogin();
                            break;
                        }
                        case "expand": {
                            System.out.println("server open " + dataReceive.getDirectoryPath().getDirectoryNode());

                            DefaultMutableTreeNode curNode = dataReceive.getDirectoryPath().getDirectoryNode();

                            if (curNode != null) {
                                File directory = new File(curNode.getUserObject().toString());
                                File[] files = directory.listFiles();

                                if (files != null) {
                                    ArrayList<File> newLstFile = new ArrayList<>();
                                    for (File child : files) {
//                                      select only folders
                                        if (child.isDirectory()) {
                                            newLstFile.add(child);
                                        }
                                    }

//                                    convert array of files to file[]
                                    File[] finalFiles = (File[]) newLstFile.toArray(new File[newLstFile.size()]);
                                    dataReceive.getDirectoryPath().setFilesList(finalFiles);
                                }

//                                send new folder list to server
                                sendMessage(dataReceive);
                            }
                            break;
                        }

                        case "change": {
                            System.out.println(dataReceive.getName() + "," + "Change path" + "," + dataReceive.getPath() + "," + datetime);

                            defaultPath = dataReceive.getPath();
                            ClientUI.pathLabel.setText("Path: " + defaultPath);

                            try {
                                WatchFolder.watchService.close();
                                new Thread(new WatchFolder(this)).start();
                            } catch (IOException err) {
                                err.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendMessage(DataObject dataSend) {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(dataSend);
            objectOutputStream.flush();
        } catch (IOException err) {
            err.printStackTrace();
            closeConnection();
        }
    }

    public DataObject listenForMessage() {
        DataObject dataReceive = null;
        try {

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            dataReceive = (DataObject) objectInputStream.readObject();
            System.out.println("receive: " + dataReceive.getInfo());

        } catch (Exception err) {
            err.printStackTrace();
            closeConnection();
            return null;
        }
        return dataReceive;
    }

    public static void closeConnection() {
        try {

            WatchFolder.watchService.close();

            if (objectInputStream != null) {
                objectInputStream.close();
            }

            if (objectOutputStream != null) {
                objectOutputStream.close();
            }

            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException err) {
            err.getStackTrace();
        }
    }
}