package application;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final Logger log = Logger.getLogger(ChatClient.class);

    public static void main(String[] args) {
        // Carga la configuración de Log4j
        PropertyConfigurator.configure("log4j.properties");

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            log.info("Conexión establecida con el servidor.");

            // Lógica para enviar mensajes al servidor
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Ejemplo: Enviar un mensaje al servidor
            out.println("¡Hola, servidor!");

            // Ejemplo: Leer la respuesta del servidor
            String response = in.readLine();
            log.info("Respuesta del servidor: " + response);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
