/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Cliente;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
/**
 *
 * @author Martín
 */
public class client extends JFrame{

    private static final String SERVER_IP = "192.168.0.25";
    private static final int SERVER_PORT = 3000;
    private static final String VideoV = "Video.mkv";
    
    
    private JList<String> videoList;
    private JButton selectButton;
    private JFileChooser fileChooser;
    
    public static void main(String[] args)  {
        SwingUtilities.invokeLater(() -> {
            client clientGUI = new client();
            clientGUI.setVisible(true);
            clientGUI.loadVideoList();
        });
    }
   

    public client() {
        setTitle("Video Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        videoList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(videoList);

        selectButton = new JButton("Ver video seleccionado");
        selectButton.setEnabled(false);
        selectButton.addActionListener(e -> playSelectedVideo());

        JPanel panel = new JPanel();
        panel.add(selectButton);

        add(scrollPane);
        add(panel, "South");

        fileChooser = new JFileChooser();
    }
    
    private void loadVideoList() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Conectado al servidor en " + SERVER_IP + ":" + SERVER_PORT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Leer la lista de videos disponibles
            StringBuilder videoListBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                videoListBuilder.append(line).append("\n");
            }

            // Actualizar el modelo de la lista
            DefaultListModel<String> listModel = new DefaultListModel<>();
            String[] videos = videoListBuilder.toString().split("\n");
            for (String video : videos) {
                listModel.addElement(video);
            }
            videoList.setModel(listModel);
            selectButton.setEnabled(true);

            reader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener la lista de videos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void playSelectedVideo() {
        String selectedVideo = videoList.getSelectedValue();
        if (selectedVideo != null) {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                System.out.println("Conectado al servidor en " + SERVER_IP + ":" + SERVER_PORT);
                
                //Enviar el nombre del video seleccionado al servidor
                socket.getOutputStream().write((selectedVideo + "\n").getBytes());

                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream( VideoV);

                // Recibir el video del servidor
                long fileSize = inputStream.readLong();
                byte[] buffer = new byte[1024];
                int bytesRead;
                long total = 0;
                
                while(total<fileSize && (bytesRead = inputStream.read(buffer)) != -1){
                    fileOutputStream.write(buffer, 0, bytesRead);
                    total +=bytesRead;
                }
                System.out.println("Video recibido y guardado temporalmente.");
                
                
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al descargar el video.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/*
// Abrir el archivo de video con el reproductor multimedia predeterminado
                File videoFile = new File(selectedVideo);
                Desktop.getDesktop().open(videoFile);

                // Esperar hasta que el reproductor multimedia termine de reproducir el video
                try {
                    while (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Thread.sleep(100);
                }
                } catch (Exception e) {
                    System.out.println("Error");
                }

                // Eliminar el archivo de video temporal después de la reproducción
                Files.deleteIfExists(Paths.get(selectedVideo));

                
                System.out.println("Reproducción de video finalizada.");*/