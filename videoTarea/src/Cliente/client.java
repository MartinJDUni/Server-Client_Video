package Cliente;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class client {
    private static final String SERVER_IP = "127.0.0.1"; // IP del servidor
    private static final int SERVER_PORT = 3000; // Puerto del servidor
    private static final String LOCAL_DIRECTORY = "videos/"; // Directorio local para almacenar los videos

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Conectado al servidor " + SERVER_IP + ":" + SERVER_PORT);

            // Leer la lista de videos disponibles desde el servidor
            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] buffer = new byte[1024];
            int bytesRead = bufferedInputStream.read(buffer);

            System.out.println("Videos disponibles:");

            while (bytesRead != -1) {
                String videoName = new String(buffer, 0, bytesRead);
                System.out.println(videoName);

                // Descargar el video desde el servidor
                downloadVideo(socket, videoName);

                // Leer el siguiente nombre de video
                bytesRead = bufferedInputStream.read(buffer);
            }

            // Cerrar la conexión
            socket.close();
            System.out.println("Conexión cerrada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadVideo(Socket socket, String videoName) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(LOCAL_DIRECTORY + videoName);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        fileOutputStream.close();
        System.out.println("Video descargado: " + videoName);
    }
}

