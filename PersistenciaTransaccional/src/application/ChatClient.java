package application;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.*;
import java.net.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Cambia esto al IP del servidor
    private static final int SERVER_PORT = 3307; // Cambia esto al puerto correcto
    private static final Logger log = Logger.getLogger(ChatClient.class);

    public static void main(String[] args) {
        // Carga la configuración de Log4j
        PropertyConfigurator.configure("log4j.properties");

        // Crear la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chat Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 2));

            JLabel ipLabel = new JLabel("IP del servidor:");
            JTextField ipField = new JTextField(SERVER_ADDRESS);
            JLabel portLabel = new JLabel("Puerto:");
            JTextField portField = new JTextField(String.valueOf(SERVER_PORT));
            JLabel userLabel = new JLabel("Nombre de usuario:");
            JTextField userField = new JTextField();

            panel.add(ipLabel);
            panel.add(ipField);
            panel.add(portLabel);
            panel.add(portField);
            panel.add(userLabel);
            panel.add(userField);

            JButton connectButton = new JButton("Conectar");
            connectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String ip = ipField.getText();
                    int port = Integer.parseInt(portField.getText());
                    String username = userField.getText();

                    // Lógica para conectar al servidor con los datos ingresados
                    // ...

                    log.info("Conexión establecida con el servidor.");
                }
            });

            panel.add(connectButton);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}