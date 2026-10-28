package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Travel Tour Booking System ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> register();
                case "2" -> login();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private void register() {
        try {
            System.out.println("\n--- Register Account ---");
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (username.isBlank() || password.isBlank()) {
                System.out.println("Username and password are required.");
                return;
            }

            System.out.println("Select role: 1. CUSTOMER  2. STAFF  3. ADMIN");
            System.out.print("Role: ");
            String roleChoice = scanner.nextLine().trim();
            String role = switch (roleChoice) {
                case "1" -> "CUSTOMER";
                case "2" -> "STAFF";
                case "3" -> "ADMIN";
                default -> null;
            };
            if (role == null) {
                System.out.println("Invalid role.");
                return;
            }

            boolean created = userDao.register(username, password, role);
            if (created) {
                System.out.println("Registration successful. Your account status is PENDING approval.");
            } else {
                System.out.println("That username already exists.");
            }
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void login() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            Optional<User> result = userDao.authenticate(username, password);
            if (result.isEmpty()) {
                System.out.println("Invalid username or password.");
                return;
            }

            User user = result.get();
            if (!"ACTIVE".equalsIgnoreCase(user.status())) {
                System.out.println("Your account is " + user.status() + ". Please contact an administrator.");
                return;
            }

            userDao.logActivity(user.userId(), "LOGIN", "User logged in.");
            routeByRole(user);
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void routeByRole(User user) {
        switch (user.role().toUpperCase()) {
            case "ADMIN" -> new AdminMenu(userDao, scanner, user).show();
            case "STAFF" -> new StaffMenu(userDao, scanner, user).show();
            case "CUSTOMER" -> new CustomerMenu(userDao, scanner, user).show();
            default -> System.out.println("Unknown role. Please contact support.");
        }
    }
}
