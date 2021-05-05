import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class Com{

    private DatagramSocket socket;
    InetAddress clientAddress;
    int clientPort;
    int comPort;
    long threadPause;
    Server srv;

    public Com(InetAddress address, int portClient, int cPort, Server server, long _pause){
        try{
            clientAddress = address;
            clientPort = portClient;
            comPort = cPort;
            socket = new DatagramSocket(comPort);
            srv = server;

            start(_pause);
        } catch(IOException ex){
            System.out.println("Client error: " + ex.getMessage() + " address:" + clientAddress);
            ex.printStackTrace();
        }
    }

    public void start(long _pause) {

        //once we start, we tell the Client our address
        String comMsg = "^^!-°)°6§è+=4-°%communication/" + comPort;
        byte[] buff = comMsg.getBytes();

        threadPause = _pause;

        DatagramPacket startMessage = new DatagramPacket(buff, buff.length, clientAddress, clientPort);

        try{
            socket.send(startMessage);
        } catch(IOException ex){
            System.out.println("Client error: " + ex.getMessage() + " address:" + clientAddress);
            ex.printStackTrace();
        }

        Thread receive = new Thread(() -> {
            try {
                while(true){
                    byte[] buffer = new byte[512];
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                    socket.receive(response);

                    String message = new String(buffer, 0, response.getLength());

                    //if Client wants to disconnect
                    if(message.contains("end")){
                        //we tell him good bye !
                        String byeMessage = "Good bye !";
                        byte[] byeBuff = byeMessage.getBytes();

                        DatagramPacket byeResponse = new DatagramPacket(byeBuff, byeBuff.length, clientAddress, clientPort);
                        socket.send(byeResponse);

                        //we then stop the connection

                        socket.close();
                        srv.delete(comPort);
                        currentThread().stop();

                    } else {
                        byte[] pongBuffer = message.getBytes();
                        DatagramPacket pongResponse = new DatagramPacket(pongBuffer, pongBuffer.length, clientAddress, clientPort);
                        socket.send(pongResponse);
                    }
                    sleep(_pause);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        receive.start();

    }

    public int getComPort(){
        return comPort;
    }

}
