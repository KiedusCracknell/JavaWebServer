import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class HttpServer {
    private ServerSocket serverSocket;
    private volatile boolean isRunning = false;
    private File webRoot = new File("www/test-site/");
    private boolean autoIndexing = true;

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

    /**
     * Helper method to get HttpRequest on GET requests
     *
     * @param request HttpRequest
     * @return correct HttpResponse object
     */
    public HttpResponse get(HttpRequest request) throws IOException {
        String requestedPath = request.getPath();
        if (requestedPath.startsWith("/api/")) {
            String endpointName = requestedPath.substring(5);
            File apiDir = new File(webRoot, "api");
            if (apiDir.exists() && apiDir.isDirectory()) {
                // Find ANY file that starts with "time."
                File[] matches = apiDir.listFiles((dir, name) -> name.startsWith(endpointName + "."));

                if (matches != null && matches.length > 0) {
                    // We found a script! Pass it to the executor.
                    return executeScript(matches[0], request);
                }
                return new HttpResponse(404, "text/html", "404 - API endpoint not found");
            }
        } else if (requestedPath.equals("/")) {
            requestedPath = "/index.html";
        }


        File requestedFile = new File(webRoot, requestedPath);

        // security check
        String canonicalRoot = webRoot.getCanonicalPath() + File.separator;
        String canonicalRequested = requestedFile.getCanonicalPath();
        if (!canonicalRequested.startsWith(canonicalRoot)) {
            return new HttpResponse(403, "text/html", "403 - Forbidden: Access Denied");
        }

        if (requestedFile.exists()) {
            if (requestedFile.isFile()) {
                return new HttpResponse(200, HttpResponse.getMimeType(requestedFile.getPath()),
                        Files.readAllBytes(requestedFile.toPath()));
            } else if (requestedFile.isDirectory()) {
                if (autoIndexing) {
                    return generateDirectoryListing(requestedFile);
                } else return new HttpResponse(403, "text/html", "403 - Forbidden Request");
            }
        } else if (requestedFile.isDirectory()) {
            return new HttpResponse(404, "text/html", "404 - Directory not found");
        }

        return new HttpResponse(404, "text/html", "404 - File not found");
    }

    private HttpResponse executeScript(File scriptFile, HttpRequest request) {
        try {
            ProcessBuilder pb = new ProcessBuilder("node", scriptFile.getCanonicalPath());

            pb.environment().put("HTTP_METHOD", request.getMethod());
            pb.environment().put("QUERY_STRING", request.getQueryMap());

            Process process = pb.start();

            byte[] outputBytes = process.getInputStream().readAllBytes();

            boolean finished = process.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return new HttpResponse(500, "text/html", "<h1>500 - Script timeout</h1>");
            }

            return new HttpResponse(200, "application/json", outputBytes);

        } catch (Exception e) {
            return new HttpResponse(500, "text/html", "<h1>500 - Execution Error: " + e.getMessage() + "</h1>");
        }
    }

    private HttpResponse generateDirectoryListing(File requestedDirectory) {
        File[] files = requestedDirectory.listFiles(File::isFile);

        return null;
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                socket.setSoTimeout(5000);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream out = socket.getOutputStream();

                while (!socket.isClosed()) {
                    String requestLine = in.readLine();
                    if (requestLine == null) break;
                    HttpRequest request = new HttpRequest(requestLine);
                    HttpResponse response;
                    String header;
                    System.out.println(requestLine);
                    while ((header = in.readLine()) != null && !header.isBlank()) {
                        request.addHeader(header);
                    }
                    if (request.getMethod().equals("GET")) {
                        response = get(request);
                        response.send(out);
                    }

                    // The loop repeats and waits for the next request on the same socket
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Connection timed out.");
            } catch (IOException e) {
                System.out.println("Connection closed or error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
