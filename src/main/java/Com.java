import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static java.lang.Thread.sleep;

public class Com implements Runnable{

    private DatagramSocket socket;
    InetAddress clientAddress;
    int clientPort;
    int comPort;
    Server server;

    private long pause;
    private boolean isRunning = false;
    private Thread thread;

    public Com(InetAddress address, int portClient, int cPort){
        try{
            clientAddress = address;
            clientPort = portClient;
            comPort = cPort;
            socket = new DatagramSocket(comPort);
        } catch(IOException ex){
            System.out.println("Client error: " + ex.getMessage() + " address:" + clientAddress);
            ex.printStackTrace();
        }
    }

    public void start(long _pause) {
        pause = _pause;
        thread = new Thread(this);
        thread.start();
        isRunning = true;

        //once we start, we tell the Client our address
        String message = "com/" + comPort;
        byte[] buff = message.getBytes();

        DatagramPacket startMessage = new DatagramPacket(buff, buff.length, clientAddress, clientPort);

        try{
            socket.send(startMessage);
        } catch(IOException ex){
            System.out.println("Client error: " + ex.getMessage() + " address:" + clientAddress);
            ex.printStackTrace();
        }

    }

    public void run(){

        while (isRunning) {
            try{

                byte[] buffer = new byte[512];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);

                String message = new String(buffer, 0, response.getLength());

                //if Client wants to disconnect
                if(message == "end"){
                    //we tell him good bye !
                    String byeMessage = "Good bye !";
                    byte[] buff = byeMessage.getBytes();

                    DatagramPacket byeResponse = new DatagramPacket(buff, buff.length, clientAddress, comPort);
                    socket.send(byeResponse);

                    //we then stop the connection
                    server.delete(clientAddress);
                } else {
                    String okMessage = "OK !";
                    byte[] buff = okMessage.getBytes();

                    DatagramPacket okResponse = new DatagramPacket(buff, buff.length, clientAddress, comPort);
                    socket.send(okResponse);
                }

                sleep(pause);

            } catch(IOException | InterruptedException ex) {
                System.out.println("Client error: " + ex.getMessage() + " address:" + clientAddress);
                ex.printStackTrace();
            }
        }
    }

    public int getComPort(){
        return comPort;
    }

}
