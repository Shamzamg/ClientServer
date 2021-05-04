import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Server{

    private DatagramSocket socket;
    private HashMap<InetAddress, Com> comList;

    public Server(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try {
            Server server = new Server(port);
            server.start();
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private void start() throws IOException {
        while (true) {
            Com com;
            DatagramPacket request = new DatagramPacket(new byte[1], 1);
            socket.receive(request);

            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();

            //if the client is unknown from the server
            if (comList.get(clientAddress) == null){
                com = new Com(clientAddress, clientPort);
                com.start(1000);
                comList.put(clientAddress, com);

                String quote = "Welcome to the server !";
                byte[] buffer = quote.getBytes();

                DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(response);
            } else {
                com = comList.get(clientAddress);
            }
        }
    }

    public void delete(InetAddress add) {
        comList.remove(add);
    }

}
