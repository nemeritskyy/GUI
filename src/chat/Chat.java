package chat;

import chat.windows.Settings;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;

public class Chat extends JFrame {
    private final static int SERVICE_PORT = 50001;
    private JTextField inputChat;
    private JButton sendMessageButton;
    private JTextArea textChat;
    private JList chatUsers;
    private JPanel content;
    private JButton clearButton;
    private static DatagramSocket clientSocket;
    private InetAddress IPAddress;

    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;

    private DefaultListModel<String> model = new DefaultListModel<>();
    private String serverIp;
    private String userName = "";

    public static void main(String[] args) throws IOException {
        Thread thread = new Thread(new UpdatePacket());
        thread.start();
    }

    public Chat() throws SocketException {
        clientSocket = new DatagramSocket();
        setTitle("Chat");
        setContentPane(content);
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        clearButton.setMnemonic('X');
        sendMessageButton.getRootPane().setDefaultButton(sendMessageButton);

        menuBar = new JMenuBar();
        menu = new JMenu("Settings");
        menuBar.add(menu);
        menu.setMnemonic(KeyEvent.VK_S);
        menuItem = new JMenuItem("Server / Nickname", KeyEvent.VK_T);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings settings = new Settings();
                if (serverIp != null) {
                    settings.settingServer.setText(serverIp);
                }
                settings.settingName.setText(userName);
                settings.setTitle("Settings");
                settings.setContentPane(settings.panelSettings);
                settings.setSize(300, 150);
                settings.setResizable(false);
                settings.setLocationRelativeTo(null);
                settings.setVisible(true);
                settings.settingSaveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setUserName(settings.settingName.getText());
                        setServerIp(settings.settingServer.getText());
                        model.removeAllElements();
                        model.addElement(settings.settingName.getText().toUpperCase());
                        chatUsers.setModel(model);
                        try {
                            sendToServer(settings.settingName.getText().toUpperCase() + ": connect to SERVER");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

            }
        });
        menu.add(menuItem);
        setJMenuBar(menuBar);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userName.isEmpty()) {
                    setTextChat("PLEASE SET YOUR NICKNAME");
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
            setTextChat("Please enter correct SERVER IP");
        }
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTextChat(String s) {
        this.textChat.setText(textChat.getText() + s + "\n");
    }

    public DatagramSocket getClientSocket() {
        return clientSocket;
    }

    public DefaultListModel getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model.addElement(model);
    }

    public void setChatUsers(DefaultListModel model) {
        this.chatUsers.setModel(model);
    }
}
