package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class Server {
    ServerSocket providerSocket;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;

    Connection databaseConnection;
    PreparedStatement updateUpload = null;

    Server(Connection connection) {
        this.databaseConnection = connection;
    }

    void run() {
        try {
            // 1. szerver socket létrehozása
            providerSocket = new ServerSocket(8080, 10);
            // 2. kapcsolódásra várakozás
            connection = providerSocket.accept();
            // 3. Input és Output streamek megadása
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            // 4. socket kommunikáció
            String[] messageSplits = new String[3];
            do {
                try {
                    message = (String)in.readObject();
                    messageSplits = message.split(":");
                    if(!messageSplits[1].equals("-1")){
                        sendMessage(messageSplits);
                    }
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } while (!messageSplits[1].equals("-1"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            // 4: kapcsolat lezárása
            try {
                in.close();
                out.close();
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage(String[] transaction) {
        try {
            int amount = Integer.parseInt(transaction[1]);
            if(isPossibleAmount(amount)){
                if(transaction.length == 2){
                    UUID transactionID = randomUUID();
                    // Write to database
                    System.out.println("Inserting records into the table...");
                    insertToDatabase(transaction[0], amount, transactionID);

                    out.writeObject(transactionID.toString());
                    out.flush();
                    System.out.println("server> OK:" + transactionID.toString());
                } else {
                    // Check transaction ID
                    System.out.println("Upload transaction");
                }

            } else {
                out.writeObject(null);
                out.flush();
                System.out.println("server> ERROR");
            }
        } catch (IOException | SQLException ioException) {
            ioException.printStackTrace();
        }
    }

    private void insertToDatabase(String s, int amount, UUID transactionID) throws SQLException {
        String updateString = "INSERT INTO upload VALUES (0,?,?,?)";
        updateUpload = databaseConnection.prepareStatement(updateString);
        updateUpload.setString(1, s);
        updateUpload.setInt(2, amount);
        updateUpload.setString(3, transactionID.toString());
        updateUpload.executeUpdate();

        updateUpload.close();
    }

    boolean isPossibleAmount(int amount){
        boolean isValidAmount = false;
        switch (amount){
            case 3000:
            case 5000:
            case 10000:
            case 15000:
                isValidAmount = true;
                break;
            default:
                break;
        }

        return isValidAmount;
    }
}