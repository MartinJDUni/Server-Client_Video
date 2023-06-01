package Servidor;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;


public class hilo extends Thread{
    
    private static String Directorio = "C:\\React\\Server-Client_Video"
            + "\\videoTarea\\src\\Videos";
    private BufferedOutputStream out;
    private DataInputStream input;
    
    private  Socket clientSocket;
    

    public hilo(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            out = new BufferedOutputStream(clientSocket.getOutputStream());
            input = new DataInputStream(clientSocket.getInputStream());
            VideoList();
            String name = input.readUTF();
            Send(name);
            
        } catch (IOException ex) {
            System.out.println("Error en el servidor");
        }
     
    }
    //Envia el video al cliente
    private void Send(String nameFile) throws IOException {
        System.out.println(nameFile);
        File video = new File(Directorio+nameFile);
        byte[] Buff = new byte[1024];
        
        FileInputStream in = new FileInputStream(video);
        
        int bytesRead;
        while((bytesRead = in.read(Buff)) != -1){
            out.write(Buff, 0, bytesRead);
        }
    }
    //Metodo el cual envia la lista para ser vista por el usuario
    private void VideoList() throws IOException {
        //El StringBuidel es para concatenar cadenas de texto
        StringBuilder videoList = new StringBuilder();
        File videoDirectory = new File(Directorio);
        File[] videoFiles = videoDirectory.listFiles();
        //Aca es donde se concatenan los nombre de videos
        if (videoFiles != null && videoFiles.length > 0) {
            for (File videoFile : videoFiles) {
                if (videoFile.isFile()) {
                    videoList.append(videoFile.getName()).append("\n");
                }
            }
        }
        String List = videoList.toString();
        System.out.println(List);
        DataOutputStream output = new DataOutputStream(
                clientSocket.getOutputStream());
        output.writeUTF(List);
        output.close();
        
     }
    
}
