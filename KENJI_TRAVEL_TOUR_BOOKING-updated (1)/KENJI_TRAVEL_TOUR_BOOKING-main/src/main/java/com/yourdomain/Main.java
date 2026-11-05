package com.yourdomain;

import com.yourdomain.dao.DatabaseInitializer;
import com.yourdomain.dao.UserDao;
import com.yourdomain.ui.Authentication;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            new DatabaseInitializer().initialize();
            System.out.println("Database initialized successfully.");

            UserDao userDao = new UserDao();
            try (Scanner scanner = new Scanner(System.in)) {
                new Authentication(userDao, scanner).start();
            }
        } catch (SQLException e) {
            System.err.println("Unable to initialize or access the database: " + e.getMessage());
        }
    }
}
