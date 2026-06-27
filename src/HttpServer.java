import java.net.*;
import java.io.*;

public class HttpServer {
    private ServerSocket serverSocket;
    private volatile boolean isRunning = false;

    /**
     * Starts the server and has it listen on the given port
     *
     * @param port port to listen on
     */
    public void start(int port) {
        if (isRunning) {
            System.out.println("Server is already running!");
            return;
        }
        isRunning = true;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Listening on port: " + port);

                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    Thread worker = new Thread(new ClientHandler(clientSocket));
                    worker.start();
                }
            } catch (SocketException e) {
                if (!isRunning) {
                    System.out.println("Server interrupted intentionally by shutdown sequence.");
                } else {
                    throw new RuntimeException("Unexpected SocketException", e);
                }
            } catch (IOException e) {
                System.out.println("Server runtime exception: " + e.getMessage());
            } finally {
                cleanUp();
            }
        }).start();
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
        }
    }

    private void cleanUp() {
        isRunning = false;
        System.out.println("Server has successfully shut down.");
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                while (!socket.isClosed()) {
                    String requestLine = in.readLine();
                    if (requestLine == null) break;
                    HttpRequest request = new HttpRequest(requestLine);
                    HttpResponse response = new HttpResponse(200, "text/html", "<h1>Server is Online!</h1>");
                    String header;
                    System.out.println(requestLine);
                    while (!(header = in.readLine()).isBlank()) {
                        System.out.println(header);
                    }

                    response.send(out);

                    // The loop repeats and waits for the next request on the same socket
                }
            } catch (IOException e) {
                System.out.println("Connection closed or error: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }
}
