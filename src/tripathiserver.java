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
import javax.net.ssl.HttpsURLConnection;

public class tripathiserver 
{

    private static final int PORT = 21200;          // UDP port number for the server
    private static final int PACKET_SIZE = 1024;    // Maximum UDP packet payload size
    public static void main(String[] args) 
    {
       
        // Try and Catch ensures socket is closed automatically
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) 
        {
            System.out.println("Server running on port " + PORT);

            // Infinite loop to keep listening for client requests
            while (true) 
            {
                // Step 1: Wait to receive a UDP packet from client
                byte[] buffer = new byte[PACKET_SIZE];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(requestPacket); // receive client request

                // Step 2: Extract client info and request string
                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();
                String request = new String(requestPacket.getData(), 0, requestPacket.getLength()).trim();

                // Step 3: Handle each client in a separate thread
                Thread worker = new Thread(() -> 
                {
                    try {
                        // --- PSEUDOCODE SECTION ---

                        // 1. Parse request string: "T WS"
                        //    Split request by space into timeout (int) and website (String).
                        String[] parts = request.split("\\s+", 2);
                        if (parts.length < 2) {
                            System.out.println("Invalid request from client: " + request);
                            return;
                        }
                        int timeout = Integer.parseInt(parts[0]);  // T in ms
                        String website = parts[1];                 // WS

                        System.out.println("Client " + clientAddress + ":" + clientPort +
                                           " requested " + website + " with timeout " + timeout + " ms");

                        // 2. Build HTTPS URL string: "https://" + website
                        //    Open an HttpsURLConnection to the URL.
                        String urlStr = "https://" + website;
                        URL url = new URL(urlStr);
                        // Open an HttpsURLConnection to the URL.
                        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
                        // Optional: reuse timeout for HTTP connection as well
                        httpsConn.setConnectTimeout(timeout);
                        httpsConn.setReadTimeout(timeout);

                        // 3. Read all bytes of the webpage into a byte array (content[]).
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try (InputStream in = httpsConn.getInputStream()) {
                            byte[] httpBuf = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = in.read(httpBuf)) != -1) {
                                baos.write(httpBuf, 0, bytesRead);
                            }
                        }
                        byte[] content = baos.toByteArray();

                        // 4. Get number of bytes (N = content.length).
                        int N = content.length;
                        System.out.println("Fetched " + N + " bytes from " + urlStr);

                        // 5. Send N to client as a UDP packet.
                        String lenStr = Integer.toString(N);
                        byte[] lenBytes = lenStr.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket lenPacket = new DatagramPacket(
                                lenBytes,
                                lenBytes.length,
                                clientAddress,
                                clientPort
                        );
                        serverSocket.send(lenPacket);

                        // 6. Send content[] in chunks of 1024 bytes.
                        //    Loop:
                        //       for (int offset = 0; offset < N; offset += PACKET_SIZE)
                        //          send each UDP packet to client.
                        for (int offset = 0; offset < N; offset += PACKET_SIZE) {
                            int chunkSize = Math.min(PACKET_SIZE, N - offset);
                            DatagramPacket dataPacket = new DatagramPacket(
                                    content,
                                    offset,
                                    chunkSize,
                                    clientAddress,
                                    clientPort
                            );
                            serverSocket.send(dataPacket);
                        }

                        // 7. Wait for ACK message:
                        //    - Set socket timeout using setSoTimeout(timeout).
                        //    - Try to receive a packet from client.
                        //    - If message == "ACK" → print OK, else print FAIL.
                        serverSocket.setSoTimeout(timeout);

                        try {
                            byte[] ackBuf = new byte[32];
                            DatagramPacket ackPacket = new DatagramPacket(ackBuf, ackBuf.length);

                            serverSocket.receive(ackPacket); // may throw SocketTimeoutException

                            String ackMsg = new String(
                                    ackPacket.getData(),
                                    0,
                                    ackPacket.getLength(),
                                    StandardCharsets.UTF_8
                            ).trim();

                            if ("ACK".equals(ackMsg)) {
                                System.out.println("OK: ACK received from " +
                                                   clientAddress + ":" + clientPort);
                            } else {
                                System.out.println("FAIL: Unexpected message from " +
                                                   clientAddress + ":" + clientPort +
                                                   " -> \"" + ackMsg + "\"");
                            }
                        } catch (SocketTimeoutException ste) {
                        

                        // 8. Handle timeout exceptions or errors properly.
                        System.out.println("FAIL: Timeout waiting for ACK from " +
                                               clientAddress + ":" + clientPort);
                        } finally {
                            // Reset to "no timeout" for the main accept loop
                            serverSocket.setSoTimeout(0);
                        }

                    } catch (Exception e) {
                        // Print exception for this client thread
                        System.out.println("Error handling client " +
                                           clientAddress + ":" + clientPort + " - " + e.getMessage());
                    }
                });

                // Start thread
                worker.start();
            }

        } 
        catch (IOException e) 
        {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}