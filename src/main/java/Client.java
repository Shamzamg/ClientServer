import java.io.IOException;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: Client <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            InetAddress address = InetAddress.getByName(hostname);
            DatagramSocket socket = new DatagramSocket();

            DatagramPacket request = new DatagramPacket(new byte[1], 1, address, port);
            socket.send(request);

            while (true) {

                byte[] buffer = new byte[512];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                System.out.println(response.getAddress() + " ; " + response.getPort());

                String serverMessage = new String(buffer, 0, response.getLength());

                System.out.println(serverMessage);
                System.out.println();

                Thread.sleep(1000);
            }

        } catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
