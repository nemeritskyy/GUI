package chat;

import chat.windows.Settings;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;

public class Chat extends JFrame {
    public final static int SERVICE_PORT = 50001;

    private JTextField inputChat;
    private JButton sendMessageButton;
    private JTextArea textChat;
    private JList chatUsers;
    private JPanel content;
    private JButton clearButton;
    private DatagramSocket clientSocket;
    private InetAddress IPAddress;

    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;

    private DefaultListModel<String> model = new DefaultListModel<>();
    private String serverIp;
    private String userName = "";

    public Chat() {
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userName.isEmpty()) {
                    textChat.setText(textChat.getText() + "PLEASE SET YOUR NICKNAME" + "\n");
                } else {
                    if (!inputChat.getText().isEmpty()) {
                        String sendTo = String.valueOf(chatUsers.getSelectedValue());
                        try {
                            sendToServer(userName + ": " + inputChat.getText());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        inputChat.setText("");
                    }
                }

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
            IPAddress = InetAddress.getByName(serverIp);
            byte[] sendingDataBuffer = new byte[1024];
            sendingDataBuffer = text.getBytes(Charset.forName("UTF-8"));
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
        chat.setResizable(false);
        chat.setLocationRelativeTo(null);
        chat.clearButton.setMnemonic('X');
        chat.sendMessageButton.getRootPane().setDefaultButton(chat.sendMessageButton);

        chat.menuBar = new JMenuBar();
        chat.menu = new JMenu("Settings");
        chat.menuBar.add(chat.menu);
        chat.menu.setMnemonic(KeyEvent.VK_S);
        chat.menuItem = new JMenuItem("Server / Nickname",
                KeyEvent.VK_T);

        chat.menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings settings = new Settings();
                if (chat.serverIp != null) {
                    settings.settingServer.setText(chat.serverIp);
                }
                settings.settingName.setText(chat.userName);
                settings.setTitle("Settings");
                settings.setContentPane(settings.panelSettings);
                settings.setSize(300, 150);
                settings.setResizable(false);
                settings.setLocationRelativeTo(null);
                settings.setVisible(true);
                settings.settingSaveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        chat.setUserName(settings.settingName.getText());
                        chat.setServerIp(settings.settingServer.getText());
                        chat.model.removeAllElements();
                        chat.model.addElement(settings.settingName.getText().toUpperCase());
                        chat.chatUsers.setModel(chat.model);
                        try {
                            chat.sendToServer(settings.settingName.getText().toUpperCase() + ": connect to SERVER");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

            }
        });
        chat.menu.add(chat.menuItem);
        chat.setJMenuBar(chat.menuBar);
        chat.setDefaultCloseOperation(EXIT_ON_CLOSE);
        chat.setVisible(true);

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

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
