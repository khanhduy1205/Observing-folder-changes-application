package Server;

import Model.DataObject;
import Model.DirectoryPath;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class ServerChat implements Runnable{
//    public Socket s;
    public Socket socket;

    private ObjectInputStream objectInputStream;
    public static ObjectOutputStream objectOutputStream;

    public static DataObject dataSend;
    private String name = null;
    public ServerChat (Socket s) {
            socket = s;
            dataSend = new DataObject("","","","",null);
    }

    @Override
    public void run() {

            DataObject dataReceive = null;
            ClientMonitoring clientMonitoring = null;

            String name, info, message, defaultpath;
            DirectoryPath directoryPath = null;
            try {
                while (socket != null && !socket.isClosed()) {

                    dataReceive = listenForMessage();

                    name = dataReceive.getName();
                    info = dataReceive.getInfo();
                    message = dataReceive.getMessage();
                    defaultpath = dataReceive.getPath();
                    directoryPath = dataSend.getDirectoryPath();

                    String datetime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

                    switch (info) {
                        case "connect": // client connect
                        {
                            if (this.name == null) {
                                this.name = name;
                            }
                            if (!Server.clientNames.contains(this.name)) {

                                System.out.println("connect");
                                clientMonitoring = new ClientMonitoring(this.socket, ServerUI.ip, ServerUI.port, this.name, defaultpath);

                                ServerUI.clientMonitorings.add(clientMonitoring);
                                System.out.println("clientMonitoring " + ServerUI.clientMonitorings.get( ServerUI.clientMonitorings.indexOf(clientMonitoring)));

                                Server.clientNames.add(this.name);
                                ServerUI.addClientNameToTable(this.name);
                                ServerUI.path = defaultpath;
                                ServerUI.addObjectToTable(defaultpath, datetime, "Connect", name, message);

                                clientMonitoring.addObjectToTable(defaultpath, datetime, "Connect", name, message);

                                dataSend.setDirectoryPath(dataReceive.getDirectoryPath());
//                            update new roots to directory tree
                                for (ClientMonitoring currentMonitor: ServerUI.clientMonitorings) {
                                    if (currentMonitor.socket == socket) {
                                        currentMonitor.updateTreeDirectory(dataReceive.getDirectoryPath().getRootDirectories());
                                    }
                                }

                                DataObject dataSend = new DataObject(name, "connect", "", defaultpath, directoryPath);
                                sendMessage(dataSend);
                            } else {
                                System.out.println(name + "," + "Someone is using this name" + "," + datetime);

                                Server.clientList.remove(socket);
                                DataObject dataSend = new DataObject("server", "exist", "", defaultpath, directoryPath);
                                sendMessage(dataSend);
                                closeConnection();
                            }
                            break;
                        }
                        case "quit": // client quit
                        {
                            ServerUI.addObjectToTable(defaultpath, datetime, "Disconnect", name, message);
                            clientMonitoring.addObjectToTable(defaultpath, datetime, "Disconnect", name, message);

                            ServerUI.removeClientNameToTable(this.name);
                            closeConnection();
                            break;
                        }
                        case "expand": {
                            if (dataReceive.getDirectoryPath().getDirectoryNode() != null) {
                                dataSend.setDirectoryPath(dataReceive.getDirectoryPath());

                                for (ClientMonitoring currentMonitor: ServerUI.clientMonitorings) {
                                    if (currentMonitor.socket == socket) {
                                        System.out.println("check mornitoring expand");

                                        System.out.println(" monitoring socket: " + socket);
                                        currentMonitor.addChildrenToCurrentNode(dataSend.getDirectoryPath().getDirectoryNode(), dataSend.getDirectoryPath().getFilesList());
                                    }
                                }
                            }
                            break;
                        }
                        case "create": // client create something
                        {
                            ServerUI.addObjectToTable(defaultpath, datetime, "Create", name, message);
                            clientMonitoring.addObjectToTable(defaultpath, datetime, "Create", name, message);
                            break;
                        }
                        case "modify": // client modify something
                        {
                            ServerUI.addObjectToTable(defaultpath, datetime, "Modify", name, message);
                            clientMonitoring.addObjectToTable(defaultpath, datetime, "Modify", name, message);
                            break;
                        }
                        case "delete": // client delete something
                        {
                            ServerUI.addObjectToTable(defaultpath, datetime, "Delete", name, message);
                            clientMonitoring.addObjectToTable(defaultpath, datetime, "Delete", name, message);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

     public void sendMessage(DataObject dataSend) {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(dataSend);
            objectOutputStream.flush();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public DataObject listenForMessage() {
        DataObject dataReceive = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            dataReceive = (DataObject) objectInputStream.readObject();
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
        return dataReceive;
    }

    public void closeConnection() {
        try {
            if (Server.clientList.contains(socket)) {
                int index = Server.clientList.indexOf(socket);
                Server.clientNames.remove(index);
                Server.clientChats.remove(index);
                ServerUI.clientMonitorings.remove(index);
                Server.clientList.remove(socket);
            }

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
