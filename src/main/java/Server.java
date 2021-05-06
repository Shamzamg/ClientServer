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

    private void addCom(InetAddress clientAddress, int clientPort, int port){
        try{
            Com com;

            com = new Com(clientAddress, clientPort, port);
            comList.add(port);
            com.start(1000, this);

            String quote = "Welcome to the server !";
            byte[] buffer = quote.getBytes();

            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, com.getComPort());
            socket.send(response);

            System.out.println("Nouveau port utilisé: " + port);
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

            //On cherche un port disponsible
            boolean found = false;
            for(int i=comPortMin;i<comPortMin + maxCom;i++){
                if (!(comList.contains(i)) && (comList.size() < maxCom)){
                    addCom(clientAddress, clientPort, i);
                    found = true;
                    break;
                }
            }
            //si on n'en trouve pas
            if(!found){
                String quote = "Le nombre maximum de clients est atteint ! réessayez plus tard ";
                byte[] buffer = quote.getBytes();

                DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(response);
                System.out.println("Tentative de connexion échouée: Le serveur a atteint son nombre maximal de connexions.");
            }
        }
    }

    public void delete(int port) {
        System.out.println("Port libéré: " + port);
        comList.remove(Integer.valueOf(port));
    }

}
