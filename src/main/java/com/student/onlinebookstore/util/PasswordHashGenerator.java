package com.student.onlinebookstore.util;

import org.mindrot.jbcrypt.BCrypt;
import java.security.SecureRandom;

public class PasswordHashGenerator {
    
    // BCrypt work factor - higher is more secure but slower
    // Recommended value between 10-12 for production
    private static final int LOG_ROUNDS = 10;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    // Hash a password using BCrypt
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }
    
    // Hash a password with custom log rounds

    public String hashPassword(String plainPassword, int logRounds) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (logRounds < 4 || logRounds > 31) {
            throw new IllegalArgumentException("Log rounds must be between 4 and 31");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(logRounds));
    }
    
    // Verify a plain password against a hashed password
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
    
    // Check if a password needs to be re-hashed (for upgrading security)
    public boolean needsRehash(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            return true;
        }
        
        // Check if the hash uses the current work factor
        // Extract the work factor from the BCrypt hash
        // BCrypt hash format: $2a$10$...
        int hashRounds = extractWorkFactor(hashedPassword);
        
        // If the hash uses a lower work factor than current, rehash
        return hashRounds < LOG_ROUNDS;
    }
    
    // Extract work factor from BCrypt hash
    private int extractWorkFactor(String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return 0;
        }
        try {
            String[] parts = hashedPassword.split("\\$");
            if (parts.length >= 3) {
                return Integer.parseInt(parts[2]);
            }
        } catch (NumberFormatException e) {
            // Ignore and return default
        }
        return 0;
    }
    
    // Generate a random secure password (for temporary passwords)
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
    
    // Validate password strength
    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    // Get password strength message
    public String getPasswordStrengthMessage(String password) {
        if (password == null || password.length() < 8) {
            return "Mật khẩu phải có ít nhất 8 ký tự";
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        if (!hasUpper) return "Mật khẩu phải có ít nhất 1 chữ hoa";
        if (!hasLower) return "Mật khẩu phải có ít nhất 1 chữ thường";
        if (!hasDigit) return "Mật khẩu phải có ít nhất 1 chữ số";
        if (!hasSpecial) return "Mật khẩu phải có ít nhất 1 ký tự đặc biệt";
        
        if (password.length() >= 12) {
            return "Mật khẩu mạnh";
        } else if (password.length() >= 10) {
            return "Mật khẩu trung bình";
        } else {
            return "Mật khẩu yếu (nên dài hơn 10 ký tự)";
        }
    }
    
    // Main method for testing
    public static void main(String[] args) {
        PasswordHashGenerator phg = new PasswordHashGenerator();
        
        // Test password hashing
        String password = "Admin@123";
        String hashed = phg.hashPassword(password);
        
        System.out.println("Original password: " + password);
        System.out.println("Hashed password: " + hashed);
        System.out.println("Password verified: " + phg.verifyPassword(password, hashed));
        System.out.println("Wrong password test: " + phg.verifyPassword("wrong", hashed));
        
        // Test password strength
        System.out.println("\n--- Password Strength Check ---");
        String weakPass = "123";
        String mediumPass = "Password123";
        String strongPass = "MyStr0ng!P@ssw0rd";
        
        System.out.println("Weak password: " + phg.getPasswordStrengthMessage(weakPass));
        System.out.println("Medium password: " + phg.getPasswordStrengthMessage(mediumPass));
        System.out.println("Strong password: " + phg.getPasswordStrengthMessage(strongPass));
        
        // Test random password generation
        System.out.println("\n--- Random Password ---");
        String randomPass = phg.generateRandomPassword(12);
        System.out.println("Random password: " + randomPass);
        System.out.println("Strength: " + phg.getPasswordStrengthMessage(randomPass));
    }
}
