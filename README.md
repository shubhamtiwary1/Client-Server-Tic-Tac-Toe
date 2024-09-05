# Client-Server-Tic-Tac-Toe

This project is a network-based Tic-Tac-Toe game developed using Java. It allows two players to connect over a network and play the classic game of Tic-Tac-Toe in real-time. The game is designed to demonstrate key concepts in Java network programming, including client-server communication and data handling.

## Features

- **Real-time Communication:** Utilizes Java's `Socket` and `ServerSocket` classes to establish connections between the server and clients.
- **Game Logic:** Implements game rules to validate moves, check for win conditions, and determine ties.
- **Message Handling:** Uses `DataInputStream` and `DataOutputStream` for sending and receiving game-related messages.
- **Sequential Client Handling:** Processes one client at a time due to the lack of multi-threading.



