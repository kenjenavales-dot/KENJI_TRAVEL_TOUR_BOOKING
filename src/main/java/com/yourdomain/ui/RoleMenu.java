package com.yourdomain.ui;

import com.yourdomain.dao.UserDao;
import com.yourdomain.model.User;

import java.sql.SQLException;
import java.util.Scanner;

abstract class RoleMenu {
    protected final UserDao userDao;
    protected final Scanner scanner;
    protected final User user;

    protected RoleMenu(UserDao userDao, Scanner scanner, User user) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.user = user;
    }

    protected void waitForReturn() {
        System.out.println("Press Enter to return to the main authentication menu.");
        scanner.nextLine();
        try {
            userDao.logActivity(user.userId(), "LOGOUT", "User left the role menu.");
        } catch (SQLException e) {
            System.out.println("Could not record activity: " + e.getMessage());
        }
    }
}
