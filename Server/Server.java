package Server;

import Client.ClientChat;
import Model.DataObject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server
{
    public static ServerSocket serverSocket = null;
    public static ArrayList<Socket> clientList =  new ArrayList<>();
    public static ArrayList<String> clientNames = new ArrayList<>();
    public static ArrayList<ServerChat> clientChats = new ArrayList<>();

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Something went wrong! Cannot create a Server");
            err.getStackTrace();
        }
    }

    public void broadcastMessage(DataObject dataSend) {
        for (ServerChat serverChat : clientChats) {
            serverChat.sendMessage(dataSend);
        }
    }

    public void serverClose(){
        DataObject dataSend = new DataObject("","close","","", null);
        broadcastMessage(dataSend);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
}