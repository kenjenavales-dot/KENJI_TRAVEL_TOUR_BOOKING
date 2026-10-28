package com.yourdomain.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public void initialize() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL CHECK (role IN ('ADMIN','STAFF','CUSTOMER')),
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL UNIQUE,
                    customer_name TEXT NOT NULL,
                    contact_number TEXT,
                    email TEXT,
                    address TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tour_packages (
                    tour_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    tour_name TEXT NOT NULL,
                    destination TEXT NOT NULL,
                    description TEXT,
                    price REAL NOT NULL CHECK (price >= 0),
                    tour_date TEXT NOT NULL,
                    available_slots INTEGER NOT NULL DEFAULT 0 CHECK (available_slots >= 0),
                    status TEXT NOT NULL DEFAULT 'Available'
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS bookings (
                    booking_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id INTEGER NOT NULL,
                    tour_id INTEGER NOT NULL,
                    booking_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    number_of_people INTEGER NOT NULL CHECK (number_of_people > 0),
                    total_amount REAL NOT NULL CHECK (total_amount >= 0),
                    booking_status TEXT NOT NULL DEFAULT 'Pending'
                        CHECK (booking_status IN ('Pending','Confirmed','Cancelled')),
                    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
                    FOREIGN KEY (tour_id) REFERENCES tour_packages(tour_id)
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action TEXT NOT NULL,
                    description TEXT,
                    date_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
                )
                """);
        }
    }
}
