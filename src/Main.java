import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Application active.");
        System.out.println(" - Type 'start' to launch on default port (8080).");
        System.out.println(" - Type 'start <port>' to specify a port (e.g., start 9000).");
        System.out.println(" - Type 'stop' to shut down the server.");
        System.out.println(" - Type 'quit' to exit the program.");

        while (true) {
            System.out.print("\nAdmin > ");

            // 1. Read the whole line and split it by spaces
            String[] input = scanner.nextLine().trim().split(" ");

            // If they just hit enter, skip the loop and ask again
            if (input[0].isEmpty()) continue;

            String command = input[0].toLowerCase();

            switch (command) {
                case "quit":
                    server.stop();
                    System.out.println("Exiting application...");
                    System.exit(0);
                    break;

                case "stop":
                    server.stop();
                    break;

                case "start":
                    if (input.length > 1) {
                        try {
                            int port = Integer.parseInt(input[1]);
                            if (port > 1024 && port < 10000) {
                                server.start(port);
                            } else {
                                System.out.println("Port must be between 1025 and 9999. Defaulting to 8080.");
                                server.start(8080);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid port format. Defaulting to 8080.");
                            server.start(8080);
                        }
                    } else {
                        server.start(8080);
                    }
                    break;

                default:
                    System.out.println("Unknown command.");
            }
        }
    }
}