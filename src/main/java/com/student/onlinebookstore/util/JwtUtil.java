package com.student.onlinebookstore.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // Secret key for signing JWT
    private static final String SECRET_KEY_STRING = "bookstoreSecretKey2024ForJWTTokenGenerationAndValidationSecurity";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
    
    // Token expiration times
    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds
    
    // Generate token for user
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
        
        logger.info("Generated token for user: {}, role: {}", email, role);
        return token;
    }
    
    // Generate token with custom expiration (for testing)
    public String generateTokenWithExpiration(String email, String role, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // Extract email from token
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }
    
    // Extract role from token
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }
    
    // Extract expiration date from token
    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }
    
    // Extract issued date from token
    public Date extractIssuedAt(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getIssuedAt();
    }
    
    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    // Check if token is expired
    public boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }
    
    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    
    // Validate token for specific user 
    public boolean validateTokenForUser(String token, String email) {
        if (!validateToken(token)) {
            return false;
        }
        
        String tokenEmail = extractEmail(token);
        if (!tokenEmail.equals(email)) {
            logger.warn("Token email doesn't match: expected {}, got {}", email, tokenEmail);
            return false;
        }
        
        return true;
    }
    
    // Get remaining time of token in milliseconds
    public long getRemainingTime(String token) {
        Date expiration = extractExpiration(token);
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    
    // Get remaining time in human readable format
    public String getRemainingTimeReadable(String token) {
        long remaining = getRemainingTime(token);
        
        long hours = remaining / (60 * 60 * 1000);
        remaining %= (60 * 60 * 1000);
        
        long minutes = remaining / (60 * 1000);
        remaining %= (60 * 1000);
        
        long seconds = remaining / 1000;
        
        if (hours > 0) {
            return String.format("%d hours %d minutes", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d minutes %d seconds", minutes, seconds);
        } else {
            return String.format("%d seconds", seconds);
        }
    }
    
    // Check if user has admin role
    public boolean isAdmin(String token) {
        if (!validateToken(token)) {
            return false;
        }
        String role = extractRole(token);
        return "admin".equals(role);
    }
    
    // Check if user has user role
    public boolean isUser(String token) {
        if (!validateToken(token)) {
            return false;
        }
        String role = extractRole(token);
        return "user".equals(role);
    }
    
    // Get authorization header value
    public String getAuthorizationHeader(String token) {
        return "Bearer " + token;
    }
    
    // Extract token from authorization header
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }
    
    // Main method for testing
    public static void main(String[] args) {
        JwtUtil jwtUtil = new JwtUtil();
        
        // Test token generation
        String email = "user@example.com";
        String role = "user";
        
        String token = jwtUtil.generateToken(email, role);
        
        System.out.println("=== JWT Token Test ===");
        System.out.println("Token: " + token);
        System.out.println();
        
        // Test token validation
        System.out.println("=== Validation ===");
        System.out.println("Validate token: " + jwtUtil.validateToken(token));
        System.out.println();
        
        // Test extraction
        System.out.println("=== Extraction ===");
        System.out.println("Email: " + jwtUtil.extractEmail(token));
        System.out.println("Role: " + jwtUtil.extractRole(token));
        System.out.println("Is expired: " + jwtUtil.isTokenExpired(token));
        System.out.println("Remaining time: " + jwtUtil.getRemainingTimeReadable(token));
        System.out.println();
        
        // Test role check
        System.out.println("=== Role Check ===");
        System.out.println("Is admin: " + jwtUtil.isAdmin(token));
        System.out.println("Is user: " + jwtUtil.isUser(token));
    }
}