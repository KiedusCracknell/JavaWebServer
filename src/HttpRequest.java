public class HttpRequest {
    private String method, path, version;

    public HttpRequest(String requestLine) {
        String[] parts = requestLine.split(" ");
        if (parts.length >= 3) {
            this.method = parts[0];
            this.path = parts[1];
            this.version = parts[2];
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
