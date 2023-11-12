import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Server is running. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Blocking call, waiting for client connection
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle communication with the client
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    String message = new String(buffer, 0, bytesRead);
                    System.out.println("Received from client: " + message);

                    // Echo the message back to the client
                    output.write(message.getBytes());
                }

                // Close the connection with the client
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}