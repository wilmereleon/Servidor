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
        String dbUrl = "jdbc:mysql://localhost:3307/chatdb"; // Cambia la URL de la base de datos
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

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;
        private Connection connection;

        public ClientHandler(Socket socket, PrintWriter writer, Connection connection) {
            this.clientSocket = socket;
            this.writer = writer;
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message;
                while ((message = reader.readLine()) != null) {
                    log.info("Mensaje recibido: " + message);
                    // Inserta el mensaje en la base de datos
                    insertMessage(message);
                    broadcast(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientWriters.remove(writer);
            }
        }

        private void insertMessage(String message) {
            String insertQuery = "INSERT INTO ChatMessages (MessageText) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, message);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void broadcast(String message) {
            for (PrintWriter clientWriter : clientWriters) {
                clientWriter.println(message);
            }
        }
    }
}}