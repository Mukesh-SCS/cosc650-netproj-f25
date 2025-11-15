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

// Implementated by Leon Dhoska
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
                        // Create a new socket for this client thread
                        DatagramSocket threadSocket = new DatagramSocket();
                        
                        // 1. Parse request string: "T WS"
                        String[] parts = request.split(" ", 2);
                        int timeout = Integer.parseInt(parts[0]);
                        String website = parts[1];

                        // 2. Build HTTPS URL string: "https://" + website
                        String urlString = "https://" + website;
                        URI uri = new URI(urlString);
                        URL url = uri.toURL();
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();

                        // 3. Read all bytes of the webpage into a byte array (content[])
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        byte[] chunk = new byte[PACKET_SIZE];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(chunk)) != -1) {
                            byteStream.write(chunk, 0, bytesRead);
                        }
                        inputStream.close();
                        byte[] content = byteStream.toByteArray();

                        // 4. Get number of bytes (N = content.length)
                        int N = content.length;

                        // 5. Send N to client as a UDP packet.

                        // 6. Send content[] in chunks of 1024 bytes.
                        //    Loop:
                        //       for (int offset = 0; offset < N; offset += PACKET_SIZE)
                        //          send each UDP packet to client.

                        // 7. Wait for ACK message:
                        //    - Set socket timeout using setSoTimeout(timeout).
                        //    - Try to receive a packet from client.
                        //    - If message == "ACK" → print OK, else print FAIL.

                        // 8. Handle timeout exceptions or errors properly.

                    } 
                    catch (Exception e) 
                    {
                        // Print exception for this client thread
                        System.out.println("Error handling client: " + e.getMessage());
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