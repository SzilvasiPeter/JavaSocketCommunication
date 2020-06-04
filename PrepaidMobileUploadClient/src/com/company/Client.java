package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.UUID;

public class Client {
    Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;

    Client() {
    }

    void run() {
        try {
            // 1. socket kapcsolat létrehozása
            requestSocket = new Socket("localhost", 8080);
            // 2. Input and Output streamek
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            // 3: Kommunikáció
            do {
                try {
                    Scanner myObj = new Scanner(System.in);
                    System.out.println("Enter transaction:");
                    String transaction = myObj.nextLine();

                    sendMessage(transaction);
                    message = (String)in.readObject();
                    System.out.println(message);
                    if(message != null){
                        sendMessage(transaction + ":" + message);
                    } else{
                        sendMessage("21412:-1");
                    }

//                    sendMessage("0630658412:5000:");
//                    message = (String)in.readObject();
//                    sendMessage("0630658412:5000:" + message);
//                    sendMessage("0630658412:0:");
//                    message = (String)in.readObject();

                } catch (Exception e) {
                    System.err.println("data received in unknown format");
                }
            } while (message != null);
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            // 4: Kapcsolat zárása
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage(String transaction) {
        try {
            out.writeObject(transaction);
            out.flush();
            System.out.println("client>" + transaction);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


}
