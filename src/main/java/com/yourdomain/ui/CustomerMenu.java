package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.util.Scanner;

public class CustomerMenu extends RoleMenu {
    public CustomerMenu(UserDao userDao, Scanner scanner, User user) {
        super(userDao, scanner, user);
    }

    public void show() {
        System.out.println("\n=== CUSTOMER MENU ===");
        System.out.println("Welcome, " + user.username() + "!");
        System.out.println("1. Browse tour packages (feature placeholder)");
        System.out.println("2. View my bookings (feature placeholder)");
        System.out.println("Press Enter to log out.");
        scanner.nextLine();
        waitForReturn();
    }
}
