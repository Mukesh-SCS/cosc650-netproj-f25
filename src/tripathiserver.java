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

public class tripathiserver
{
    private static final int PORT = 21200;          // UDP port number for the server
    private static final int PACKET_SIZE = 1024;    // Maximum UDP packet payload size

    public static void main(String[] args) 
    {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) 
        {
            System.out.println("Server is running on port " + PORT);
            //
        } 
        catch (IOException e) 
        {
            // Catch server-level exceptions
            e.printStackTrace();
        }
    }
}

