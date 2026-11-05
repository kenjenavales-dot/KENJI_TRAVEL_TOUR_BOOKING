package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.util.Scanner;

public class StaffMenu extends RoleMenu {
    public StaffMenu(UserDao userDao, Scanner scanner, User user) {
        super(userDao, scanner, user);
    }

    public void show() {
        System.out.println("\n=== STAFF MENU ===");
        System.out.println("Welcome, " + user.username() + "!");
        System.out.println("1. View bookings (feature placeholder)");
        System.out.println("2. Assist customers (feature placeholder)");
        System.out.println("Press Enter to log out.");
        scanner.nextLine();
        waitForReturn();
    }
}
