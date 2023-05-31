package Servidor;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class server {

    private static final String VIDEO_DIRECTORY = "Servidor/Videos/";
    private static final int PORT = 3000;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor esperando conexiones en el puerto " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());

                // Obtener la lista de videos disponibles
                List<String> videoList = getVideoList();

                // Enviar la lista de videos al cliente
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                for (String video : videoList) {
                    writer.write(video);
                    writer.newLine();
                }
                writer.flush();

                // Iniciar un hilo para manejar la solicitud del cliente
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                // Leer el nombre del video seleccionado por el cliente
                String selectedVideo = readSelectedVideo();

                // Verificar si el video existe en el servidor
                File videoFile = new File(VIDEO_DIRECTORY + selectedVideo);
                if (!videoFile.exists()) {
                    try (clientSocket) {
                        System.out.println("El video seleccionado no existe: " + selectedVideo);
                    }
                    return;
                }

                // Enviar el video al cliente
                sendVideo(videoFile);
            } catch (IOException e) {
            }
        }

        private String readSelectedVideo() throws IOException {
            BufferedInputStream inputStream = new BufferedInputStream(clientSocket.getInputStream());
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String selectedVideo = new String(buffer, 0, bytesRead).trim();
            System.out.println("Cliente seleccionó el video: " + selectedVideo);
            return selectedVideo;
        }

        private void sendVideo(File videoFile) throws IOException {
            try (clientSocket; BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(videoFile))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                    clientSocket.getOutputStream().write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Video enviado al cliente.");
        }
    }

    private static List<String> getVideoList() {
        List<String> videoList = new ArrayList<>();
        File videoDirectory = new File(VIDEO_DIRECTORY);
        File[] videoFiles = videoDirectory.listFiles();

        if (videoFiles != null && videoFiles.length > 0) {
            for (File videoFile : videoFiles) {
                if (videoFile.isFile()) {
                    videoList.add(videoFile.getName());
                }
            }
        }

        return videoList;
    }
}
