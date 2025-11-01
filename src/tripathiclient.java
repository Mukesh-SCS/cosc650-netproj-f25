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
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class tripathiclient {

    public static void main(String[] args) {
        final int SERVER_PORT = 21200;   // server UDP port
        final int PACKET_SIZE = 1024;    // packet buffer size

        // Create scanner for user input and UDP socket for communication
        try (Scanner sc = new Scanner(System.in);
             DatagramSocket clientSocket = new DatagramSocket()) {

            // Prompt user for timeout and web server name
            System.out.println("Enter an integer timeout value T in milliseconds such as 100");
            int timeout = sc.nextInt();
            sc.nextLine(); // clear newline
            System.out.println("Enter a webserver name WS such as www.ietf.org");
            String website = sc.nextLine().trim();

            // Build request message "T WS"
            String requestMessage = timeout + " " + website;

            // Step 1: Convert request to bytes
            byte[] sendData = requestMessage.getBytes(StandardCharsets.UTF_8);

            // Step 2: Send request to server (localhost:21200)
            InetAddress serverAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            // Step 3: Set socket timeout
            clientSocket.setSoTimeout(timeout);

            // --- PSEUDOCODE SECTION ---

            // 4. Receive initial packet containing N (total bytes)
            //    byte[] sizeBuf = new byte[64]
            //    DatagramPacket sizePacket = new DatagramPacket(sizeBuf, sizeBuf.length)
            //    clientSocket.receive(sizePacket)
            //    parse N = Integer.parseInt(new String(sizeBuf))

            // 5. Allocate byte array 'content' of size N

            // 6. Initialize counter: totalReceived = 0

            // 7. While loop to receive webpage chunks until totalReceived == N:
            //       try:
            //           receive UDP packet
            //           copy packet bytes into content buffer
            //           increment totalReceived
            //           print received data as string
            //       catch timeout:
            //           print "FAIL"
            //           exit program

            // 8. When all data received:
            //       send "ACK" packet to server
            //       print "All data received successfully."

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}