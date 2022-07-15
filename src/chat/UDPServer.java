package chat;

import java.io.IOException;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class UDPServer extends Thread {
    private Map<Integer, InetAddress> clientList = new LinkedHashMap();
    public final static int SERVICE_PORT = 50001;
    public DatagramSocket serverSocket = null;
    byte[] receivingDataBuffer = new byte[1024];
    byte[] sendingDataBuffer = new byte[1024];
    DatagramPacket inputPacket;

    public static void main(String[] args) throws IOException {
        UDPServer server = new UDPServer();
        server.start();
    }

    @Override
    public void run() {
        try {
            serverSocket = new DatagramSocket(SERVICE_PORT);
            System.out.println("Waiting for a client connect...");

            while (true) {
                inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                serverSocket.receive(inputPacket);
                clientList.put(inputPacket.getPort(), inputPacket.getAddress());
                String receivedData = new String(inputPacket.getData(), 0, inputPacket.getLength());
                System.out.println("Sent from client: " + receivedData);
                sendingDataBuffer = receivedData.toUpperCase().getBytes();
                for (Map.Entry<Integer, InetAddress> client : clientList.entrySet()) {
                    System.out.println(client.getKey() + " " + client.getValue());
                    DatagramPacket outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, client.getValue(), client.getKey());
                    serverSocket.send(outputPacket);
                }
                System.out.println(clientList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
