import java.io.PrintWriter;

public class HttpResponse {
    private static final String VERSION = "HTTP/1.1 ";
    private int statusCode;
    private String contentType, body;
    private boolean keepAlive = true;

    public HttpResponse(int statusCode, String contentType, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public HttpResponse (int statusCode, String contentType, String body, boolean keepAlive) {
        this(statusCode, contentType, body);
        this.keepAlive = keepAlive;
    }

    public void send(PrintWriter out) {
        try {
            out.print(getStatusHeader(statusCode));
            out.print("Content-Type: " + contentType + "\r\n");
            out.print("Content-Length: " + body.length() + "\r\n");
            out.print("Connection: " + (keepAlive ? "keep-alive\r\n" : "close\r\n"));
            out.print("\r\n");
            out.print(body);
            out.flush();
        } catch (UnsupportedOperationException e) {
            out.print(VERSION + "500 INTERNAL SERVER ERROR");
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
}
