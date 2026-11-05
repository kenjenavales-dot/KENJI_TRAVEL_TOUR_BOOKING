package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu extends RoleMenu {
    public AdminMenu(UserDao userDao, Scanner scanner, User user) {
        super(userDao, scanner, user);
    }

    public void show() {
        System.out.println("\n=== ADMIN MENU ===");
        System.out.println("Welcome, " + user.username() + "!");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Admin Options ---");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("0. Log out");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUserAccount();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        logLogout();
    }

    private void viewAllUsers() {
        try {
            System.out.println("\n--- All System Users ---");
            printUserTable(userDao.getAllUsers());
        } catch (SQLException e) {
            System.out.println("Could not load users: " + e.getMessage());
        }
    }

    private void viewPendingUsers() {
        try {
            System.out.println("\n--- Pending Registrations ---");
            printUserTable(userDao.getPendingUsers());
        } catch (SQLException e) {
            System.out.println("Could not load pending users: " + e.getMessage());
        }
    }

    private void approveUserAccount() {
        try {
            List<User> pending = userDao.getPendingUsers();
            System.out.println("\n--- Approve User Account ---");
            printUserTable(pending);
            if (pending.isEmpty()) {
                return;
            }

            while (true) {
                System.out.print("Enter Target User ID to approve (0 to cancel): ");
                String input = scanner.nextLine().trim();

                int targetId;
                try {
                    targetId = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a numeric User ID.");
                    continue;
                }

                if (targetId == 0) {
                    System.out.println("Approval cancelled.");
                    return;
                }

                User target = findById(pending, targetId);
                if (target == null) {
                    System.out.println("User ID " + targetId + " is not in the pending list. Try again.");
                    continue;
                }

                if (userDao.approveUser(targetId)) {
                    System.out.println("Success! Account '" + target.username() + "' (ID " + targetId
                            + ", " + target.role() + ") has been APPROVED.");
                    userDao.logActivity(user.userId(), "APPROVE_USER",
                            "Approved user ID " + targetId + " (" + target.username() + ").");
                } else {
                    System.out.println("Approval failed: no account was updated.");
                }
                return;
            }
        } catch (SQLException e) {
            System.out.println("Approval failed: " + e.getMessage());
        }
    }

    /** Prints any list of users as an aligned, bordered console table. */
    private void printUserTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users to display.");
            return;
        }

        String border = "+" + "-".repeat(7) + "+" + "-".repeat(17) + "+"
                + "-".repeat(14) + "+" + "-".repeat(12) + "+";
        String rowFormat = "| %-5s | %-15s | %-12s | %-10s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "Username", "Role", "Status");
        System.out.println(border);
        for (User u : users) {
            System.out.printf(rowFormat,
                    u.userId(), truncate(u.username(), 15), truncate(u.role(), 12), truncate(u.status(), 10));
        }
        System.out.println(border);
        System.out.println("Total: " + users.size() + " user(s)");
    }

    private User findById(List<User> users, int id) {
        for (User u : users) {
            if (u.userId() == id) {
                return u;
            }
        }
        return null;
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }
}
