/***********************************************************************************************
 *  FILE NAME: tripathiclient.java
 *
 *  DESCRIPTION:
 *      UDP client program for COSC 650 Fall 2025 project.
 *      Sends a message "T WS" to the server, where T is timeout and WS is a webserver name.
 *      The client receives webpage data over UDP in 1024-byte chunks, prints it as received,
 *      and sends an "ACK" message to the server if all data is received within the timeout.
 *
 *  FUNCTIONS:
 *      - main(String[] args)
 *
 *  AUTHOR:  Leon Dhoska, Mayeesha Humaira, Mukesh Mani Tripathi, Samuel Maldonado
 *
 ***********************************************************************************************/
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

//Implementated by Mukesh Mani Tripathi
public class tripathiclient {

    public static void main(String[] args) {
        final int SERVER_PORT = 21200;   // server UDP port
        final int PACKET_SIZE = 1024;    // packet buffer size

        // Create scanner for user input and UDP socket for communication
        try (Scanner sc = new Scanner(System.in);
             DatagramSocket clientSocket = new DatagramSocket()) {

            // STEP 1: Get user input for timeout and website 
            System.out.println("Enter an integer timeout value T in milliseconds such as 100");
            int timeout = sc.nextInt();
            sc.nextLine(); // clear newline from buffer
            
            System.out.println("Enter a webserver name WS such as www.ietf.org");
            String website = sc.nextLine().trim();

            //= STEP 2: Build and send request message "T WS" to server
            String requestMessage = timeout + " " + website;
            byte[] sendData = requestMessage.getBytes(StandardCharsets.UTF_8);
            
            InetAddress serverAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
                                                          serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);
            System.out.println("Request sent to server: " + requestMessage);

            //  STEP 3: Set socket timeout 
            clientSocket.setSoTimeout(timeout);

            //=========================IMPLEMENTATION by Mayeesha=========================
        
            // (4) Receive initial packet containing N (total bytes) 
            byte[] sizeBuf = new byte[64]; // enough for ASCII length like "123456"
            DatagramPacket sizePacket = new DatagramPacket(sizeBuf, sizeBuf.length);

            clientSocket.receive(sizePacket); // may throw SocketTimeoutException
            // Use the address/port we actually heard from (more robust than hardcoding)
            InetAddress respAddr = sizePacket.getAddress();
            int respPort = sizePacket.getPort();

            String sizeStr = new String(sizePacket.getData(), 0, sizePacket.getLength(),
                                        StandardCharsets.UTF_8).trim();
            int totalBytes = Integer.parseInt(sizeStr); //N

            // (5) Allocate a buffer to hold the whole content-------
            ByteArrayOutputStream content = new ByteArrayOutputStream(totalBytes);

            // (6) Initialize counter 
            int totalReceived = 0;

            // (7) Receive webpage chunks until totalReceived == N 
            while (totalReceived < totalBytes) {
                try {
                    byte[] buf = new byte[PACKET_SIZE];
                    DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                    clientSocket.receive(pkt); // times out after 'timeout' ms

                    int len = pkt.getLength();
                    content.write(buf, 0, len);
                    totalReceived += len;

                    // Print chunk as text (as bytes arrive), matching the spec
                    System.out.print(new String(buf, 0, len, StandardCharsets.UTF_8));
                    System.out.flush();
                } catch (SocketTimeoutException te) {
                    // Timeout while waiting for data: print FAIL and exit (no ACK sent)
                    System.out.println("\nFAIL");
                    return;
                }
            }

            // (8) All data received: send "ACK" back to the server 
            byte[] ack = "ACK".getBytes(StandardCharsets.UTF_8);
            DatagramPacket ackPkt =
                new DatagramPacket(ack, ack.length, respAddr, respPort);
            clientSocket.send(ackPkt);

            System.out.println("\nAll data received successfully.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}