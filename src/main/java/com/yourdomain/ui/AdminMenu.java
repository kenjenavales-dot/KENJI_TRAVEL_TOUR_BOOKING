package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.util.Scanner;

public class AdminMenu extends RoleMenu {
    public AdminMenu(UserDao userDao, Scanner scanner, User user) {
        super(userDao, scanner, user);
    }

    public void show() {
        System.out.println("\n=== ADMIN MENU ===");
        System.out.println("Welcome, " + user.username() + "!");
        System.out.println("1. Manage users (feature placeholder)");
        System.out.println("2. Manage tour packages (feature placeholder)");
        System.out.println("Press Enter to log out.");
        scanner.nextLine();
        waitForReturn();
    }
}
