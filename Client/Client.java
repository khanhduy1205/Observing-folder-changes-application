package Client;

public class Client {
    private String IP;
    private String name;
    private int port;

    public Client(String IP, String name, int port) {
        this.IP = IP;
        this.name = name;
        this.port = port;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}