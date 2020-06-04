package com.company;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class QueryClient {
    Connection databaseConnection;
    Statement stmt = null;

    QueryClient(Connection connection) {
        this.databaseConnection = connection;
    }
    void run() throws SQLException {
        System.out.println("Creating statement...");
        stmt = databaseConnection.createStatement();

        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter phone number:");
        String phonenumber = myObj.nextLine();

        String sql = "SELECT * FROM upload WHERE phonenumber=" + phonenumber+";";
        ResultSet rs = stmt.executeQuery(sql);
        //STEP 5: Extract data from result set
        while(rs.next()){
            //Retrieve by column name
            int id  = rs.getInt("id");
            String displayPhoneNumber = rs.getString("phonenumber");
            int amount = rs.getInt("amount");
            String transaction = rs.getString("transaction");

            //Display values
            System.out.print("ID: " + id);
            System.out.print(", phonenumber: " + displayPhoneNumber);
            System.out.print(", amount: " + amount);
            System.out.println(", transaction: " + transaction);
        }
        rs.close();
    }
}
