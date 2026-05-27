package com.student.onlinebookstore.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugGenerator {
    
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");
    
    // Generate SEO-friendly slug from title
    public static String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "unknown";
        }
        
        String slug = title.toLowerCase();
        
        // Convert Vietnamese characters to non-accented
        slug = removeAccents(slug);
        
        // Replace spaces with hyphens
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        
        // Remove non-word characters (keep only letters, numbers, hyphens)
        slug = NON_LATIN.matcher(slug).replaceAll("");
        
        // Remove leading/trailing hyphens
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        
        // Replace multiple hyphens with single hyphen
        slug = slug.replaceAll("-+", "-");
        
        // Add timestamp to ensure uniqueness if needed
        // slug = slug + "-" + System.currentTimeMillis();
        
        return slug;
    }
    
    // Generate unique slug with ID appended
    public static String generateUniqueSlug(String title, Integer id) {
        String slug = generateSlug(title);
        return slug + "-" + id;
    }
    
    // Remove accents from Vietnamese text
    private static String removeAccents(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccents = pattern.matcher(normalized).replaceAll("");
        
        // Special character replacements
        withoutAccents = withoutAccents
            .replace("đ", "d")
            .replace("Đ", "d");
        
        return withoutAccents;
    }
    
    // Check if string is valid slug format
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.isEmpty()) {
            return false;
        }
        
        // Slug should only contain lowercase letters, numbers, and hyphens
        return slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    }
    
    // Example usage and test
    public static void main(String[] args) {
        String[] titles = {
            "Clean Code: A Handbook of Agile Software Craftsmanship",
            "Dế Mèn Phiêu Lưu Ký - Tô Hoài",
            "Nhà Giả Kim (Bản Đặc Biệt)",
            "JavaScript: The Good Parts",
            "Cấu Trúc Dữ Liệu & Giải Thuật"
        };
        
        for (String title : titles) {
            String slug = generateSlug(title);
            System.out.println("Title: " + title);
            System.out.println("Slug:  " + slug);
            System.out.println("Valid: " + isValidSlug(slug));
            System.out.println("---");
        }
    }
}
