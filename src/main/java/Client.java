import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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

                String message = new String(buffer, 0, response.getLength());

                //if we receive a new communication port
                if(message.contains("com")){
                    String [] s = message.split("/");
                    System.out.println(message);
                    port = Integer.parseInt(s[1]);
                }

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
