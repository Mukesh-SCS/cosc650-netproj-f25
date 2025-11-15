# Lets complete the Question 1 : Later will we convert this into PDF
- COSC 650 Project Fall 2025 – Question 1
- Group Name: Tripathi
- ICMPv6 Packet Analysis – Frame 8

### (Type, Code, Checksum, Reserved, Target Address, Option Type, Option Length, Nonce)

**(Mukesh)**
Part 1 – ICMPv6 Basic Header Fields  
Field 1: Type
Name: Type
Actual value: 135 (Neighbor Solicitation)

Explanation:
This field identifies the ICMPv6 message type. A value of 135 means this is a Neighbor Solicitation message. Neighbor Solicitation is part of IPv6 Neighbor Discovery and is used to find the link-layer (MAC) address corresponding to an IPv6 address on the same network link. It is the IPv6 equivalent of an ARP “who has” request in IPv4.

Field 2: Code
Name: Code
Actual value: 0
Explanation:
The Code field gives extra detail for some ICMPv6 message types. For Neighbor Solicitation, the value must always be 0. Any other value would indicate a malformed packet, so receivers expect 0 here and ignore other values.

Field 3: Checksum
Name: Checksum
Actual value: 0xb26a (marked “correct” in Wireshark)
Explanation:
The Checksum field provides basic error detection. It is computed over the ICMPv6 message plus an IPv6 pseudo-header that includes the source and destination IPv6 addresses. Wireshark shows this checksum as “correct,” which means the ICMPv6 data was not corrupted in transit and can be trusted by the receiver.


//please write these two fields in the same style as Part 1.

(Anyone)
Part 2 – #TODO Team

Fields:

Reserved

Target Address

//please handle the option header fields.
(Anyone)
Part 3 – #TODO Team

Fields:

ICMPv6 Option Type 

ICMPv6 Option Length 


//please complete the nonce and final summary.
(Anyone)
Part 4 – #TODO Team

Fields:

Nonce 

Short final summary paragraph of the whole ICMPv6 message  //2–3 summary sentences.
