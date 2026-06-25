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

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread worker = new Thread(new ClientHandler(clientSocket));
                worker.start();
            }
        } catch (IOException e) {
            System.out.println("Server closed: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    socket;
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null && !inputLine.isBlank()) {
                    System.out.println(inputLine);
                }
                out.print("HTTP/1.1 200 OK\r\n");
                out.print("Content-Type: text/html\r\n");
                out.print("\r\n");
                out.print("<h1 style='color: red;'>Server is Online!</h1>");
                out.print("<p>The production line is running continuously.</p>");
            } catch (IOException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}
