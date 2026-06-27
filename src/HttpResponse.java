import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class HttpResponse {
    private static final String VERSION = "HTTP/1.1 ";
    private int statusCode;
    private String contentType;
    private boolean keepAlive = true;
    private byte[] body;

    public HttpResponse(int statusCode, String contentType, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body.getBytes();
    }

    public HttpResponse(int statusCode, String contentType, String body, boolean keepAlive) {
        this(statusCode, contentType, body);
        this.keepAlive = keepAlive;
    }

    public HttpResponse(int statusCode, String contentType, byte[] body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public HttpResponse(int statusCode, String contentType, byte[] body, boolean keepAlive) {
        this(statusCode, contentType, body);
        this.keepAlive = keepAlive;
    }

    public void send(OutputStream out) {
        String header = getStatusHeader(statusCode) + "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length + "\r\n" + "Connection: " +
                (keepAlive ? "keep-alive\r\n" : "close\r\n") + "\r\n\r\n";

        try {
            out.write(header.getBytes());
            out.write(body);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStatusHeader(int statusCode) {
        return switch (statusCode) {
            case 200 -> VERSION + statusCode + "OK\r\n";
            case 500 -> VERSION + statusCode + "INTERNAL SERVER ERROR\r\n";
            case 404 -> VERSION + statusCode + "PAGE NOT FOUND\r\n";
            default -> getStatusHeader(500);
        };
    }

    public static String getMimeType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream"; // Default for unknown types
    }
}
