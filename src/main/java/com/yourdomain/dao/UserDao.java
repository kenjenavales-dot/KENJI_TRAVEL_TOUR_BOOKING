package com.yourdomain.dao;

import com.yourdomain.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Optional;

public class UserDao {

    public boolean register(String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.setString(3, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
                return false;
            }
            throw e;
        }
    }

    public Optional<User> authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role, status FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && MessageDigest.isEqual(
                        rs.getString("password").getBytes(StandardCharsets.UTF_8),
                        hashPassword(password).getBytes(StandardCharsets.UTF_8))) {
                    return Optional.of(new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status")));
                }
            }
        }
        return Optional.empty();
    }

    public void logActivity(int userId, String action, String description) throws SQLException {
        String sql = "INSERT INTO activity_logs (user_id, action, description) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, description);
            ps.executeUpdate();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) result.append(String.format("%02x", b));
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
    }
}
