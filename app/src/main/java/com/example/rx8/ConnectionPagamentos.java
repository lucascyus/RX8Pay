package com.example.rx8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPagamentos {
    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.0.15:1433/salary_sync";
    private static final String DB_USER = "fishitest";
    private static final String DB_PASSWORD = "luvinhas23";

    public static Connection connect() {
        Connection connection = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}

