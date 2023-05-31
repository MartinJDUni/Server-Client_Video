package Cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class client {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 3000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Se estableció la conexión con IP: " + SERVER_IP + ", puerto: " + SERVER_PORT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Leer la lista de videos disponibles del servidor
            String videoList;
            while ((videoList = reader.readLine()) != null) {
                System.out.println(videoList);
            }

            // Leer la selección del video del usuario
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Ingrese el nombre del video que desea reproducir (o 'exit' para salir): ");
            String selectedVideo;

            while ((selectedVideo = userInputReader.readLine()) != null) {
                if (selectedVideo.equalsIgnoreCase("exit")) {
                    break;
                }

                // Enviar la selección del video al servidor
                writer.write(selectedVideo);
                writer.newLine();
                writer.flush();

                // Leer y mostrar el video recibido del servidor
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
                    // Procesar los datos del video recibidos
                    // Aquí puedes realizar la lógica para reproducir el video o guardar los datos recibidos
                    System.out.println("Datos recibidos: " + new String(buffer, 0, bytesRead));
                }

                System.out.print("Ingrese el nombre del video que desea reproducir (o 'exit' para salir): ");
            }

            // Cerrar la conexión con el servidor
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
