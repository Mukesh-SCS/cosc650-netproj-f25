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
import java.util.Scanner;

public class tripathiclient {

    public static void main(String[] args) {
        final int SERVER_PORT = 21200;
        final int PACKET_SIZE = 1024;

        try (Scanner sc = new Scanner(System.in);
             DatagramSocket clientSocket = new DatagramSocket()) {

            System.out.println("Enter an integer timeout value T in milliseconds such as 100");
            int timeout = sc.nextInt();
            sc.nextLine();  // Consume newline

            System.out.println("Enter a webserver name (e.g., www.example.com):");
            String webserver = sc.nextLine();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}