package application;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    private static final int PORT = 3307; // Cambia esto al puerto correcto
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static final Logger log = Logger.getLogger(Servidor.class);

    public static void main(String[] args) {
        // Carga la configuración de Log4j
        PropertyConfigurator.configure("log4j.properties");

        // Establece la conexión a la base de datos (ajusta los detalles de conexión según tu configuración)
        String dbUrl = "jdbc:mysql://localhost:3307/Chat"; // Cambia la URL de la base de datos
        String dbUser = "root"; // Cambia el usuario de la base de datos
        String dbPassword = "password"; // Cambia la contraseña de la base de datos

        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            log.info("Conexión a la base de datos establecida.");

            ServerSocket serverSocket = new ServerSocket(PORT);
            log.info("Servidor en espera de conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("Cliente conectado desde " + clientSocket.getInetAddress());

                // Crea un hilo para manejar la conexión con el cliente
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                Thread clientThread = new Thread(new ClientHandler(clientSocket, writer, connection));
                clientThread.start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;
        private Connection dbConnection;
        private static final Logger log = Logger.getLogger(ClientHandler.class);

        public ClientHandler(Socket socket, PrintWriter writer, Connection connection) {
            this.clientSocket = socket;
            this.writer = writer;
            this.dbConnection = connection;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String sender;
                String receiver;
                String messageText;

                // Lógica para recibir mensajes del cliente y guardarlos en la base de datos
                while (true) {
                    String clientMessage = reader.readLine();
                    if (clientMessage == null) {
                        break; // El cliente se desconectó
                    }

                    // Parsea el mensaje del cliente (formato: "remitente:destinatario:texto_del_mensaje")
                    String[] messageParts = clientMessage.split(":");
                    if (messageParts.length == 3) {
                        sender = messageParts[0];
                        receiver = messageParts[1];
                        messageText = messageParts[2];

                        // Insertar en la tabla ChatMessages
                        String insertQuery = "INSERT INTO ChatMessages (Sender, Receiver, MessageText) VALUES (?, ?, ?)";
                        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(insertQuery)) {
                            preparedStatement.setString(1, sender);
                            preparedStatement.setString(2, receiver);
                            preparedStatement.setString(3, messageText);
                            preparedStatement.executeUpdate();
                        }
                    } else {
                        log.warn("Mensaje del cliente con formato incorrecto: " + clientMessage);
                    }
                }

                // Resto de la lógica para enviar y recibir mensajes con el cliente
                // ...
            } catch (IOException e) {
                log.error("Error de E/S al manejar la conexión con el cliente: " + e.getMessage());
            } catch (SQLException e) {
                log.error("Error de SQL al insertar en la base de datos: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    log.error("Error al cerrar el socket del cliente: " + e.getMessage());
                }
            }
        }
    }
}