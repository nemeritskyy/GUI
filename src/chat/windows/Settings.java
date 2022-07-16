package chat.windows;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings extends JFrame {
    public JTextField settingServer;
    public JTextField settingName;
    public JButton settingSaveButton;
    public JPanel panelSettings;

    public Settings() {
        settingSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
