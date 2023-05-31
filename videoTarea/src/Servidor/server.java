/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Martín
 */
public class server {

    /**
     * @param args the command line arguments
     * 
     */
    private static final int PORT = 3000;
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor esperando conexiones en el puerto " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());

                // Iniciar un hilo para manejar la solicitud del cliente
                hilo client = new hilo(clientSocket);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
