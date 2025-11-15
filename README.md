# COSC 650 Network Project – Fall 2025

## Overview
This repository contains the source code and analysis files for the COSC 650 Fall 2025 networking project.  
The project includes two main parts:

1. **UDP Socket Programming (Java)**  
   - Implementation of a local UDP server (`tripathiserver.java`) and client (`tripathiclient.java`).
   - The client requests a webpage through the server using `HttpURLConnection`.
   - The server sends back the webpage data in UDP packets and waits for an acknowledgment (`ACK`) from the client.

2. **ICMPv6 Analysis (Wireshark)**  
   - Analysis of the ICMPv6 section of frame 8 from the provided capture file (`c650projectf25wscap.pcapng`).
   - Explanation of the key fields and their meanings in a separate report.
   - submit by tripathians.pdf


## Repository Structure

```bash
cosc650-netproj-f25/
│
├── src/
│ ├── tripathiserver.java
│ └── tripathiclient.java
│
├── analysis/
│ └── tripathi_draft.md
│
├── captures/
│ └── c650projectf25wscap.pcapng
│
└── README.md

```

## How to Compile

From the project root directory, run:

```bash
javac src/tripathiserver.java
javac src/tripathiclient.java

```
## How to Run

Open two terminals:

- Terminal 1 – Server:

```bash
java -cp src tripathiserver
```

- Terminal 2 – Client:

```bash
java -cp src tripathiclient
```

### Sample Interaction

#### Client:
```bash
Enter an integer timeout value T in milliseconds such as 100
100
Enter a webserver name WS such as www.ietf.org
www.ietf.org
```

#### Client Output:
```bash
(received web page bytes printed as packets arrive)
```

#### Server Output:
```bash
127.0.0.1 58924 OK

or, if timeout occurs:

127.0.0.1 58924 FAIL
```

#### ICMPv6 Analysis (Question 1)

- The file analysis/tripathians.md contains a three-column table describing ICMPv6 header fields from frame 8 in the provided capture:

- Field/Subfield	Actual Value	Meaning
Type	128	Echo Request (used for ping)
Code	0	Indicates no specific sub-type
Checksum	0x7c2d	Ensures integrity of the ICMPv6 message
Identifier	0x0001	Matches request/reply pairs
Sequence Number	0x0001	Incremented for each echo request
(Example values shown; actual values must be taken from the capture.)

- You will export this markdown file as tripathi.pdf for submission.

## Final Submission Checklist
```bash
#Confirm both compile and run with no extra dependencies. 
###  Submit only:  ###

tripathians.pdf

tripathiserver.java

tripathiclient.java

```

## Notes

- Both server and client communicate over UDP port 21200.
- The server handles multiple clients using threads.
- The client must send "ACK" before its timeout value to receive an “OK” from the server.
- ICMPv6 analysis must only focus on frame 8 of the capture.


Author:
- Leon Dhoska
- Mayeesha Humaira
- Mukesh Mani Tripathi
- Samuel Maldonado
