package chat;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

public class UDPServer extends Thread {
    private static Map<Integer, InetAddress> clientList = new LinkedHashMap();
    private final static int SERVICE_PORT = 50001;
    private static DatagramSocket serverSocket = null;
    private byte[] receivingDataBuffer = new byte[1024];
    private byte[] sendingDataBuffer = new byte[1024];
    private DatagramPacket inputPacket;


    public static void main(String[] args) throws IOException {
        UDPServer server = new UDPServer();
        serverSocket = new DatagramSocket(SERVICE_PORT);
        Thread checkServer = new Thread(new CheckServerMessage());
        checkServer.start();
        server.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting for a client connect...");
            while (true) {
                inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                serverSocket.receive(inputPacket);
                clientList.put(inputPacket.getPort(), inputPacket.getAddress());
                String receivedData = new String(inputPacket.getData(), 0, inputPacket.getLength());
                System.out.println("Sent from client: " + receivedData);
                sendingDataBuffer = receivedData.toUpperCase().getBytes(Charset.forName("UTF-8"));
                sendAll(sendingDataBuffer);
                System.out.println(clientList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAll(byte[] message) throws IOException {
        for (Map.Entry<Integer, InetAddress> client : clientList.entrySet()) {
            DatagramPacket outputPacket = new DatagramPacket(message, message.length, client.getValue(), client.getKey());
            serverSocket.send(outputPacket);
        }
    }

    public Map<Integer, InetAddress> getClientList() {
        return clientList;
    }
}
