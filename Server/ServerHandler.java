package Server;

import java.io.*;
import java.net.Socket;

public class ServerHandler implements Runnable {
    public static Server server;

    public ServerHandler(Server s) {
        server = s;
    }

//    to process requirements
    @Override
    public void run() {

        try {
            while(!Server.serverSocket.isClosed()) {
                Socket socket = Server.serverSocket.accept();
                Server.clientList.add(socket);

//               create and save clientchat for each client
                Server.clientChats.add(new ServerChat(socket));

                System.out.print("A new client has connected " + socket + ": ");
                ServerChat currentChat = Server.clientChats.get(Server.clientChats.size()-1);
                new Thread(currentChat).start();
            }
        } catch (IOException err) {
            System.out.println("Server has closed!");
        }
    }
}
