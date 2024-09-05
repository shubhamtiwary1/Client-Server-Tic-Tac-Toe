package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacToeServer {
    // Declare server socket and player sockets
    private ServerSocket serverSocket;
    private Socket player1;
    private Socket player2;
    private DataInputStream inputPlayer1;
    private DataOutputStream outputPlayer1;
    private DataInputStream inputPlayer2;
    private DataOutputStream outputPlayer2;

    // Tic-Tac-Toe game board and the current player indicator
    private char[] board;
    private int currentPlayer;

    // Constructor to initialize the server and wait for two players
    public TicTacToeServer(int port) {
        try {
            // Create a server socket at the specified port
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running and waiting for players...");

            // Accept connection from player 1
            player1 = serverSocket.accept();
            System.out.println("Player 1 connected.");
            inputPlayer1 = new DataInputStream(player1.getInputStream());
            outputPlayer1 = new DataOutputStream(player1.getOutputStream());

            // Accept connection from player 2
            player2 = serverSocket.accept();
            System.out.println("Player 2 connected.");
            inputPlayer2 = new DataInputStream(player2.getInputStream());
            outputPlayer2 = new DataOutputStream(player2.getOutputStream());

            // Initialize the game board with numbers 1-9 and set current player to 1
            board = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
            currentPlayer = 1;

            // Start the game
            playGame();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Ensure all connections are closed after the game
            closeConnections();
        }
    }

    // Method to handle the game logic
    private void playGame() {
        try {
            // Send the initial board state to both players
            sendBoardToPlayers();

            // Continue the game loop until there is a winner or tie
            while (true) {
                // Get the current player's move
                int move = getPlayerMove();

                // If the move is valid, update the board
                if (isValidMove(move)) {
                    updateBoard(move);
                    sendBoardToPlayers();

                    // Check if the current player has won the game
                    if (checkWinner()) {
                        sendGameResult("WIN", currentPlayer);
                        break;
                    // Check if the game is a tie
                    } else if (checkTie()) {
                        sendGameResult("TIE", 0);
                        break;
                    }

                    // Switch to the next player
                    switchPlayer();
                } else {
                    // If the move is invalid, notify the player
                    sendInvalidMove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send the game result to both players
    private void sendGameResult(String result, int player) throws IOException {
        if (player == 1) {
            outputPlayer1.writeUTF(result); // Player 1 wins
            outputPlayer2.writeUTF("Sorry, you lose."); // Player 2 loses
        } else if (player == 2) {
            outputPlayer1.writeUTF("Sorry, you lose."); // Player 1 loses
            outputPlayer2.writeUTF(result); // Player 2 wins
        } else {
            // For a tie
            outputPlayer1.writeUTF(result);
            outputPlayer2.writeUTF(result);
        }
    }

    // Send the current board state to both players
    private void sendBoardToPlayers() throws IOException {
        String boardString = "BOARD|" + new String(board);
        outputPlayer1.writeUTF(boardString);
        outputPlayer2.writeUTF(boardString);
    }

    // Get the current player's move from their input
    private int getPlayerMove() throws IOException {
        if (currentPlayer == 1) {
            outputPlayer1.writeUTF("YOUR_TURN");
            return Integer.parseInt(inputPlayer1.readUTF()) - 1; // Convert to 0-based index
        } else {
            outputPlayer2.writeUTF("YOUR_TURN");
            return Integer.parseInt(inputPlayer2.readUTF()) - 1; // Convert to 0-based index
        }
    }

    // Check if the move is valid (not already taken)
    private boolean isValidMove(int move) {
        return move >= 0 && move < 9 && board[move] == (char) ('1' + move);
    }

    // Update the board with the current player's symbol (X or O)
    private void updateBoard(int move) {
        char currentPlayerSymbol = (currentPlayer == 1) ? 'X' : 'O';
        board[move] = currentPlayerSymbol;
    }

    // Check if the current player has won the game
    private boolean checkWinner() {
        return (checkLine(0, 1, 2) || checkLine(3, 4, 5) || checkLine(6, 7, 8) ||  // Check rows
                checkLine(0, 3, 6) || checkLine(1, 4, 7) || checkLine(2, 5, 8) ||  // Check columns
                checkLine(0, 4, 8) || checkLine(2, 4, 6));  // Check diagonals
    }

    // Helper method to check if three board positions have the same symbol
    private boolean checkLine(int pos1, int pos2, int pos3) {
        return (board[pos1] == board[pos2] && board[pos2] == board[pos3]);
    }

    // Check if the game is a tie (no more valid moves)
    private boolean checkTie() {
        for (int i = 0; i < 9; i++) {
            if (board[i] == (char) ('1' + i)) {
                return false; // There is still an available move
            }
        }
        return true; // No more moves, it's a tie
    }

    // Switch the current player after each valid move
    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    // Notify the current player if their move is invalid
    private void sendInvalidMove() throws IOException {
        if (currentPlayer == 1) {
            outputPlayer1.writeUTF("INVALID_MOVE");
        } else {
            outputPlayer2.writeUTF("INVALID_MOVE");
        }
    }

    // Close all connections after the game ends
    private void closeConnections() {
        try {
            if (player1 != null) player1.close();
            if (inputPlayer1 != null) inputPlayer1.close();
            if (outputPlayer1 != null) outputPlayer1.close();

            if (player2 != null) player2.close();
            if (inputPlayer2 != null) inputPlayer2.close();
            if (outputPlayer2 != null) outputPlayer2.close();

            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main method to start the server
    public static void main(String[] args) {
        new TicTacToeServer(5555); // Start the server on port 5555
    }
}
