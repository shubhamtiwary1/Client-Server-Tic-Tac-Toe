package client1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeClient {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    // Constructor initialises the client and starts the game
    public TicTacToeClient(String serverAddress, int port) {
        try {
            // Connect to the server
            socket = new Socket(serverAddress, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            // Start the game loop
            playGame();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Ensure resources are closed
            closeConnections();
        }
    }

    // Main game loop for receiving messages and sending moves
    private void playGame() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                // Read message from the server
                String message = input.readUTF();
                if (message.startsWith("BOARD|")) {
                    // Display the current game board
                    displayBoard(message.substring(6));
                } else if (message.equals("YOUR_TURN")) {
                    // Get the player's move and send it to the server
                    int move = getMoveFromPlayer(scanner);
                    output.writeUTF(String.valueOf(move));
                } else if (message.equals("INVALID_MOVE")) {
                    // Notify player of invalid move
                    System.out.println("Invalid move. Please try again.");
                } else if (message.equals("WIN")) {
                    // Notify player of victory
                    System.out.println("Congratulations! You won!");
                    break; // End the game
                } else if (message.equals("TIE")) {
                    // Notify player of a tie
                    System.out.println("It's a tie! The game is over.");
                    break; // End the game
                } else {
                    // Handle unexpected messages
                    System.out.println("Game result: " + message);
                    break; // End the game
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Display the current state of the game board
    private void displayBoard(String boardString) {
        System.out.println("Current Board (player 1):");
        System.out.println(" " + boardString.charAt(0) + " | " + boardString.charAt(1) + " | " + boardString.charAt(2));
        System.out.println("-----------");
        System.out.println(" " + boardString.charAt(3) + " | " + boardString.charAt(4) + " | " + boardString.charAt(5));
        System.out.println("-----------");
        System.out.println(" " + boardString.charAt(6) + " | " + boardString.charAt(7) + " | " + boardString.charAt(8));
        System.out.println();
    }

    // Get and validate the player's move
    private int getMoveFromPlayer(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Enter your move (1-9): ");
                int move = Integer.parseInt(scanner.nextLine().trim());
                // Validate move
                if (move >= 1 && move <= 9) {
                    return move;
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 9.");
            }
        }
    }

    // Close all open connections
    private void closeConnections() {
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main method to start the client application
    public static void main(String[] args) {
        new TicTacToeClient("localhost", 5555);
    }
}
