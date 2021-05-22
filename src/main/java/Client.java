import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private static InetAddress address;
    private static DatagramSocket socket;
    private static int port;
    private static String hostname;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: Client <hostname> <port>");
            return;
        }

        //get main arguments
        hostname = args[0];
        port = Integer.parseInt(args[1]);

        try {
            //initialize the connexion with the server
            address = InetAddress.getByName(hostname);
            //initial port
            socket = new DatagramSocket();


            String heyMessage = "hello serveur RX302";
            byte[] heyBuff = heyMessage.getBytes();

            DatagramPacket request = new DatagramPacket(heyBuff, heyBuff.length, address, port);
            socket.send(request);

            Thread send = new Thread(() -> {
                while(true){
                    try{
                        byte[] sendBuffer;

                        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                        String outMessage = in.readLine();

                        String outString = "Received: " + outMessage;
                        sendBuffer = outString.getBytes();

                        DatagramPacket out = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
                        socket.send(out);

                        if (outMessage.equals("end"))
                            break;
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            });
            send.start();

            Thread receive = new Thread(() -> {
                try {
                    while(true){
                        byte[] responseBuffer = new byte[512];

                        DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);
                        socket.receive(response);

                        String message = new String(responseBuffer, 0, response.getLength());

                        //if we receive a new communication port
                        if(message.contains("^^!-°)°6§è+=4-°%communication")){
                            String [] s = message.split("/");
                            port = Integer.parseInt(s[1]);
                            System.out.println("Connected on port: " + port);
                        }else{
                            System.out.println(message + " @ip: " +response.getAddress() + " port: " + response.getPort());
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receive.start();

        } catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
