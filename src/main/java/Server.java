import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.ArrayList;

public class Server{

    private DatagramSocket socket;
    int maxCom = 2;
    ArrayList<Integer> comList = new ArrayList<>();
    private int comPortMin = 15000;
    private int comPort = 15000;

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

    private void addCom(InetAddress clientAddress, int clientPort){
        try{
            Com com;

            com = new Com(clientAddress, clientPort, comPort, this, 1000);
            comList.add(comPort);

            String quote = "Welcome to the server !";
            byte[] buffer = quote.getBytes();

            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, com.getComPort());
            socket.send(response);

            comPort++;
            System.out.println("Port inutilisé");
        } catch(IOException e){
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    private void start() throws IOException {
        while (true) {
            DatagramPacket request = new DatagramPacket(new byte[1], 1);
            socket.receive(request);

            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();

            //if the client is unknown from the server and the number of client is not reached yet
            if (!(comList.contains(comPort)) && (comList.size() < maxCom)){
                addCom(clientAddress, clientPort);
            }
            //sinon, on cherche un port disponsible
            else {
                boolean found = false;
                for(int i=comPortMin;i<comPortMin + maxCom;i++){
                    if (!(comList.contains(comPort)) && (comList.size() < maxCom)){
                        addCom(clientAddress, clientPort);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    System.out.println("Le nombre maximum de clients est atteint ! réessayez plus tard ");
                }
            }
        }
    }

    public void delete(int port) {
        System.out.println("deleting" + port);
        comList.remove(port);
    }

}
