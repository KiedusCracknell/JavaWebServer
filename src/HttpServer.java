import java.net.*;
import java.io.*;

public class HttpServer {
    private ServerSocket serverSocket;

    /**
     * Starts the server and has it listen on the given port
     *
     * @param port port to listen on
     */
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port: " + port);

            Socket clientSocket = serverSocket.accept();
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null && !inputLine.isBlank()) {
                    System.out.println(inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Server closed: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
        }
    }
}
