# Custom Java HTTP Server

A lightweight, multithreaded HTTP server written entirely in Java. This server acts as a self-contained application capable of serving static web assets and dynamically executing server-side APIs.

## Features

*   **Multithreaded Architecture:** Handles concurrent client connections by assigning each accepted socket to an independent worker thread.
*   **Interactive CLI:** Features a built-in command-line interface for starting the server on a custom port, stopping it, and gracefully shutting down the application.
*   **Static Asset Serving:** Automatically resolves and serves files with the correct MIME types (HTML, CSS, JS, PNG, JPEG, ICO).
*   **Dynamic SSR / API Execution (In progress):** Routes requests to `/api/` files to an external `ProcessBuilder`, executing them via recognized web APIs. HTTP methods and query strings are passed to the script as environment variables (`HTTP_METHOD`, `QUERY_STRING`).
*   **Directory Auto-Indexing (In progress):** Dynamically generates an HTML list of directory contents if no specific file is requested and auto-indexing is enabled.
*   **Robust Security:** Implements canonical path validation to prevent directory traversal attacks (returns 403 Forbidden).
*   **Timeout & Process Management:** Enforces a 5000ms socket timeout and strictly limits external Node.js script execution to 5 seconds before forcing process destruction.
*   **Keep-Alive Support:** Maintains persistent connections for sequential requests over the same socket.

## Planned features
*   **Support full suite of HTTP methods**
*   **GUI**
*   **Support for a templating framework**
*   **Support to act as a reverse proxy**

## Getting Started

### Prerequisites
*   **Java Development Kit (JDK):** Version 17 or higher recommended.

### Installation & Execution
1. Clone this repository to your local machine.
2. Ensure your web assets are placed in the designated web root folder (`www/test-site/`).
3. Compile the Java files:
   ```bash
   javac *.java
