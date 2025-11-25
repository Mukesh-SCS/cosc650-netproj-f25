/***********************************************************************************************
 *  FILE NAME: tripathiserver.java
 *
 *  DESCRIPTION:
 *      UDP server program for COSC 650 Fall 2025 project.
 *      Listens on port 21200 and receives requests from clients in the form "T WS".
 *      The server fetches the webpage from the given host using HTTPS and sends the data
 *      back to the client in UDP packets. After sending, it waits for an "ACK" message.
 *
 *  FUNCTIONS:
 *      - main(String[] args)
 *      - ClientHandler.run()
 *
 *  AUTHOR: Leon Dhoska, Mayeesha Humaira, Mukesh Mani Tripathi, Samuel Maldonado
 *
 ***********************************************************************************************/
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tripathiserver {

    private static final int PORT = 21200;             // UDP port number for the server
    private static final int PACKET_SIZE = 1024;      // Maximum UDP packet payload size

    public static void main(String[] args) {

        ExecutorService pool = Executors.newCachedThreadPool();

        try (DatagramSocket mainSocket = new DatagramSocket(PORT)) {

            System.out.println("Server running on UDP port " + PORT);

            while (true) {

                // Receive “T WS”
                byte[] buffer = new byte[PACKET_SIZE];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                mainSocket.receive(requestPacket);

                String request = new String(
                        requestPacket.getData(),
                        0,
                        requestPacket.getLength(),
                        StandardCharsets.UTF_8
                ).trim();

                InetAddress clientAddr = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();

                // Process client in its own thread
                pool.execute(() -> handleClient(request, clientAddr, clientPort));
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // --------------------- Client Thread -------------------------
    private static void handleClient(String request, InetAddress clientAddr, int clientPort) {

        try {
            // Parse "T WS"
            String[] parts = request.split("\\s+", 2);
            if (parts.length < 2) {
                System.out.println("Invalid request: " + request);
                return;
            }

            int timeout = Integer.parseInt(parts[0]);
            String website = parts[1];

            System.out.println("Client " + clientAddr + ":" + clientPort +
                    " requested " + website + " with timeout " + timeout + " ms");

            // -------- Create new UDP socket for this thread --------
            DatagramSocket serverSocket = new DatagramSocket(); 
            serverSocket.setSoTimeout(timeout);

            // -------------- Connect via HTTPS using required classes --------------
            URI uri = new URI("https://" + website);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);

            //webpage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream is = conn.getInputStream()) {
                byte[] temp = new byte[4096];
                int r;
                while ((r = is.read(temp)) != -1) {
                    baos.write(temp, 0, r);
                }
            }

            byte[] content = baos.toByteArray();
            int N = content.length;

            // ---------------- Send N first ----------------
            byte[] nBytes = Integer.toString(N).getBytes(StandardCharsets.UTF_8);
            DatagramPacket nPacket = new DatagramPacket(
                    nBytes, nBytes.length, clientAddr, clientPort);
            serverSocket.send(nPacket);

            // ---------------- Send 1024-byte chunks ----------------
            for (int offset = 0; offset < N; offset += PACKET_SIZE) {
                int chunkSize = Math.min(PACKET_SIZE, N - offset);

                DatagramPacket dataPacket = new DatagramPacket(
                        content, offset, chunkSize, clientAddr, clientPort);

                serverSocket.send(dataPacket);
            }

            // ---------------- Wait for ACK ----------------
            try {
                byte[] ackBuf = new byte[32];
                DatagramPacket ackPacket = new DatagramPacket(ackBuf, ackBuf.length);

                serverSocket.receive(ackPacket);

                String msg = new String(
                        ackBuf, 0, ackPacket.getLength(), StandardCharsets.UTF_8);

                if (msg.trim().equals("ACK")) {
                    System.out.println(clientAddr + ":" + clientPort + " OK");
                } else {
                    System.out.println(clientAddr + ":" + clientPort + " FAIL");
                }

            } catch (SocketTimeoutException e) {
                // No ACK within T
                System.out.println(clientAddr + ":" + clientPort + " FAIL");
            }

            serverSocket.close();

        } catch (Exception e) {
            System.out.println("Handler error: " + e.getMessage());
        }
    }
}