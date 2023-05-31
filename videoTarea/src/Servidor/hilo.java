package Servidor;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class hilo extends Thread{
    
    private static final String Directorio = "videos/";
    
    private final Socket clientSocket;

    public hilo(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            // Obtener la lista de videos disponibles
                List<String> videoList = getVideoList();

                // Enviar la lista de videos al cliente
                for (String video : videoList) {
                    writer.write(video);
                    writer.newLine();
                }
                writer.flush();
            // Leer el nombre del video seleccionado por el cliente
            String selectedVideo = readSelectedVideo();

            // Verificar si el video existe en el servidor
            File videoFile = new File(Directorio + selectedVideo);
            if (!videoFile.exists()) {
                System.out.println("El video seleccionado no existe: " + selectedVideo);
                clientSocket.close();
                return;
            }

            // Enviar el video al cliente
            sendVideo(videoFile);
        } catch (IOException e) {
            e.printStackTrace();
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
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(videoFile));
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
            clientSocket.getOutputStream().write(buffer, 0, bytesRead);
        }
        bufferedInputStream.close();
        clientSocket.close();
        System.out.println("Video enviado al cliente.");
    }
    
    private List<String> getVideoList() {
            List<String> videoList = new ArrayList<>();
            File videoDirectory = new File(Directorio);
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