import java.util.HashMap;

public class HttpRequest {
    private String method, path, version, queryMap;
    private HashMap<String, String> headers = new HashMap<>();

    public HttpRequest(String requestLine) {
        String[] parts = requestLine.split(" ");
        if (parts.length >= 3) {
            this.method = parts[0];
            this.version = parts[2];

            String[] pathParts = parts[1].split("\\?");
            this.path = pathParts[0];

            if (pathParts.length > 1) {
                this.queryMap = pathParts[1];
            } else {
                this.queryMap = "";
            }
        }
    }

    public void addHeader(String headerLine) {
        String[] header = headerLine.split(": ");
        headers.put(header[0], header[1]);
    }

    public String getHeaderValue(String type) {
        return headers.get(type);
    }

    public String getQueryMap(){
        return this.queryMap;
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
