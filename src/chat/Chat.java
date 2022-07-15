package chat;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.*;

public class Chat extends JFrame {
    private JTextField inputChat;
    private JButton sendMessageButton;
    private JTextArea textChat;
    private JList chatUsers;
    private JTextField userName;
    private JPanel content;
    private JButton clearButton;
    private JTextField serverIP;
    private DefaultListModel<String> model = new DefaultListModel<String>();
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;

    public final static int SERVICE_PORT = 50001;

    public Chat() throws SocketException, UnknownHostException {
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userName.getText().isEmpty()) {
                    textChat.setText(textChat.getText() + "PLEASE SET YOUR NICKNAME" + "\n");
                } else {
                    if (!inputChat.getText().isEmpty()) {
                        String sendTo = String.valueOf(chatUsers.getSelectedValue());
                        try {
                            sendToServer(userName.getText() + ": " + inputChat.getText());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        inputChat.setText("");
                    }
                }

            }
        });
        userName.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                model.removeAllElements();
                model.addElement(userName.getText().toUpperCase());
                chatUsers.setModel(model);
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textChat.setText("");
            }
        });
    }

    private void sendToServer(String text) throws IOException {
        try {
            IPAddress = InetAddress.getByName(serverIP.getText());
            byte[] sendingDataBuffer = new byte[1024];
            sendingDataBuffer = text.getBytes();
            DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, SERVICE_PORT);
            clientSocket.send(sendingPacket);
        } catch (IOException e) {
            textChat.setText(textChat.getText() + "Please enter correct SERVER IP\n");
        }
    }

    public static void main(String[] args) throws IOException {
        Chat chat = new Chat();
        chat.clientSocket = new DatagramSocket();
        chat.setTitle("Chat");
        chat.setContentPane(chat.content);
        chat.setSize(600, 400);
        chat.setLocationRelativeTo(null);
        chat.clearButton.setMnemonic('X');
        chat.sendMessageButton.getRootPane().setDefaultButton(chat.sendMessageButton);
        chat.setDefaultCloseOperation(EXIT_ON_CLOSE);
        chat.setVisible(true);
//        for (int i = 0; i < 14; i++) {
//            chat.textChat.setText(chat.textChat.getText() + i + "\n");
//        }
        Thread t1 = new Thread(chat.new UpdatePacket());
        t1.start();
    }

    private class UpdatePacket implements Runnable {
        byte[] receivingDataBuffer = new byte[1024];
        DatagramPacket receivePacket;
        String receivedData;
        String userName;

        @Override
        public void run() {
            receivePacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            while (true) {
                try {
                    clientSocket.receive(receivePacket);
                    receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    userName = receivedData.split(":")[0];
                    if (!model.contains(userName)) {
                        textChat.setText(textChat.getText() + "new user connected (" + userName.toUpperCase() + ")" + "\n");
                        model.addElement(userName);
                        chatUsers.setModel(model);
                    }
                    textChat.setText(textChat.getText() + receivedData + "\n");
                    System.out.println(receivedData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
