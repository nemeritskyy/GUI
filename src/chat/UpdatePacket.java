package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class UpdatePacket extends Chat implements Runnable {
    byte[] receivingDataBuffer = new byte[1024];
    DatagramPacket receivePacket;
    String receivedData;
    String userName;

    public UpdatePacket() throws SocketException {
    }

    @Override
    public void run() {
        receivePacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        while (true) {
            try {
                getClientSocket().receive(receivePacket);
                receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                userName = receivedData.split(":")[0];
                if (!getModel().contains(userName)) {
                    setTextChat("new user connected (" + userName.toUpperCase() + ")");
                    setModel(userName);
                    setChatUsers(getModel());
                }
                setTextChat(receivedData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
