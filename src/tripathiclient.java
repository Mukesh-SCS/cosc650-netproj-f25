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

public class tripathiclient 
{

    public static void main(String[] args)
    {
        final int SERVER_PORT = 21200;   // server UDP port
        final int PACKET_SIZE = 1024;    // packet buffer size

        try (Scanner sc = new Scanner(System.in);
             DatagramSocket clientSocket = new DatagramSocket()) 
            {

            // Prompt user for timeout and web server name
            System.out.println("Enter an integer timeout value T in milliseconds such as 100");
            int timeout = sc.nextInt();
            sc.nextLine(); // clearnewline

            
            System.out.println("Enter a webserver name WS such as www.ietf.org");
            String website = sc.nextLine().trim();

            
            String requestMessage = timeout + " " + website;
            byte[] sendData = requestMessage.getBytes(StandardCharsets.UTF_8);

            InetAddress serverAddress = InetAddress.getByName("localhost");

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, serverAddress, SERVER_PORT);

            clientSocket.send(sendPacket);

            // Set timeout for receiving packets
            clientSocket.setSoTimeout(timeout);

           
            byte[] sizeBuf = new byte[64];
            DatagramPacket sizePacket = new DatagramPacket(sizeBuf, sizeBuf.length);

            try 
            {
                clientSocket.receive(sizePacket);
            } 
            catch (SocketTimeoutException te) 
            {
                System.out.println("FAIL");
                return;
            }

            // Server may respond from a thread using its own source port
            InetAddress respAddr = sizePacket.getAddress();
            int respPort = sizePacket.getPort();

            String sizeStr = new String(
                    sizePacket.getData(), 0, sizePacket.getLength(),
                    StandardCharsets.UTF_8).trim();

            int totalBytes = Integer.parseInt(sizeStr);

            // Storage for incoming webpage content
            ByteArrayOutputStream content = new ByteArrayOutputStream(totalBytes);
            int totalReceived = 0;

            // ---------------- Receive 1024-byte chunks ----------------
            while (totalReceived < totalBytes) 
            {
                try {
                    byte[] buf = new byte[PACKET_SIZE];
                    DatagramPacket pkt = new DatagramPacket(buf, buf.length);

                    clientSocket.receive(pkt); // wait for data (may timeout)

                    int len = pkt.getLength();
                    content.write(buf, 0, len);
                    totalReceived += len;

                    // Print chunk contents as required
                    System.out.print(new String(buf, 0, len, StandardCharsets.UTF_8));
                    System.out.flush();

                } catch (SocketTimeoutException te) {
                    // Timeout while receiving the webpage packets
                    System.out.println("\nFAIL");
                    return;
                }
            }

            // ---------------- Send ACK if all packets received ----------------
            byte[] ack = "ACK".getBytes(StandardCharsets.UTF_8);

            DatagramPacket ackPkt = new DatagramPacket(
                    ack, ack.length, respAddr, respPort);

            clientSocket.send(ackPkt);

            System.out.println("\nAll data received successfully.");

        }
        catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
}