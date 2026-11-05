package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu extends RoleMenu {
    private static final List<String> VALID_ROLES = List.of("ADMIN", "STAFF", "CUSTOMER");
    private static final List<String> VALID_STATUSES = List.of("APPROVED", "PENDING");

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
            System.out.println("4. Update User Role and Status");
            System.out.println("0. Log out");
            int choice = readIntInput("Choose: ");

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> viewPendingUsers();
                case 3 -> approveUserAccount();
                case 4 -> updateUserAccount();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        logLogout();
    }

    /**
     * Reads an integer without allowing malformed input to terminate the application.
     * Entering 0 is treated as cancellation by the calling operation when applicable.
     */
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value (0 to cancel).");
            }
        }
    }

    /** Returns null when the administrator chooses 0 to cancel. */
    private String readValidRole() {
        while (true) {
            System.out.println("Available roles: ADMIN, STAFF, CUSTOMER");
            System.out.print("Enter new role (0 to cancel): ");
            String role = scanner.nextLine().trim().toUpperCase();
            if ("0".equals(role)) {
                return null;
            }
            if (VALID_ROLES.contains(role)) {
                return role;
            }
            System.out.println("Invalid role. Please enter ADMIN, STAFF, or CUSTOMER.");
        }
    }

    /** Returns null when the administrator chooses 0 to cancel. */
    private String readValidStatus() {
        while (true) {
            System.out.println("Available statuses: APPROVED, PENDING");
            System.out.print("Enter new status (0 to cancel): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if ("0".equals(status)) {
                return null;
            }
            if (VALID_STATUSES.contains(status)) {
                return status;
            }
            System.out.println("Invalid status. Please enter APPROVED or PENDING.");
        }
    }

    private void updateUserAccount() {
        try {
            List<User> users = userDao.getAllUsers();
            System.out.println("\n--- Update User Role and Status ---");
            printUserTable(users);
            if (users.isEmpty()) {
                return;
            }

            int targetId = readIntInput("Enter Target User ID (0 to cancel): ");
            if (targetId == 0) {
                System.out.println("Update cancelled.");
                return;
            }

            // Do not allow the currently logged-in administrator to change their own account.
            if (targetId == user.userId()) {
                System.out.println("WARNING: Modifying the currently active admin account is restricted.");
                return;
            }

            User target = findById(users, targetId);
            if (target == null) {
                System.out.println("User ID " + targetId + " was not found. Update cancelled.");
                return;
            }

            System.out.println("Selected user: " + target.username()
                    + " (Current role: " + target.role()
                    + ", Current status: " + target.status() + ")");

            String newRole = readValidRole();
            if (newRole == null) {
                System.out.println("Update cancelled.");
                return;
            }

            String newStatus = readValidStatus();
            if (newStatus == null) {
                System.out.println("Update cancelled.");
                return;
            }

            if (userDao.updateUserRoleAndStatus(targetId, newRole, newStatus)) {
                System.out.println("SUCCESS: User '" + target.username() + "' (ID " + targetId
                        + ") updated successfully. New role: " + newRole
                        + ", New status: " + newStatus + ".");
                userDao.logActivity(user.userId(), "UPDATE_USER",
                        "Updated user ID " + targetId + " (" + target.username()
                                + ") to role " + newRole + " and status " + newStatus + ".");
            } else {
                System.out.println("Update failed: no account was updated.");
            }
        } catch (SQLException e) {
            System.out.println("User update failed: " + e.getMessage());
        }
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
                int targetId = readIntInput("Enter Target User ID to approve (0 to cancel): ");
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
