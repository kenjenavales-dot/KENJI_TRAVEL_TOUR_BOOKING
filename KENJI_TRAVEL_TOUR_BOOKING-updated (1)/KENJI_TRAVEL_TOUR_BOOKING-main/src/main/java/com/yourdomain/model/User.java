package com.yourdomain.model;

public record User(int userId, String username, String passwordHash, String role, String status) {}
