package application;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    private static final int PORT = 12345;
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static final Logger log = Logger.getLogger(Servidor.class);

    public static void main(String[] args) {
        // Carga la configuración de Log4j
        PropertyConfigurator.configure("log4j.properties");

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            log.info("Servidor en espera de conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("Cliente conectado desde " + clientSocket.getInetAddress());

                // Crea un hilo para manejar la conexión con el cliente
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                Thread clientThread = new Thread(new ClientHandler(clientSocket, writer));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;

        public ClientHandler(Socket socket, PrintWriter writer) {
            this.clientSocket = socket;
            this.writer = writer;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message;
                while ((message = reader.readLine()) != null) {
                    log.info("Mensaje recibido: " + message);
                    broadcast(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientWriters.remove(writer);
            }
        }

        private void broadcast(String message) {
            for (PrintWriter clientWriter : clientWriters) {
                clientWriter.println(message);
            }
        }
    }
}

