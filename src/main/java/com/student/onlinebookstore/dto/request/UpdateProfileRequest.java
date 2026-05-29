package com.student.onlinebookstore.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class UpdateProfileRequest {
    
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Tên đăng nhập chỉ được chứa chữ cái, số, dấu chấm và gạch dưới")
    private String username;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phoneNumber;
    
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;
    
    private String fullName;
    
    private LocalDate dateOfBirth;
    
    private String gender;
    
    private String avatarUrl;
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    // Helper methods
    public boolean hasValidDateOfBirth() {
        if (dateOfBirth == null) return true;
        
        LocalDate minDate = LocalDate.now().minusYears(100);
        LocalDate maxDate = LocalDate.now().minusYears(5);
        
        return !dateOfBirth.isBefore(minDate) && !dateOfBirth.isAfter(maxDate);
    }
    
    public boolean hasValidGender() {
        if (gender == null || gender.isEmpty()) return true;
        return "male".equalsIgnoreCase(gender) || 
               "female".equalsIgnoreCase(gender) || 
               "other".equalsIgnoreCase(gender);
    }
    
    public boolean hasChanges(String currentUsername, String currentEmail, String currentPhone) {
        boolean hasChanges = false;
        
        if (username != null && !username.equals(currentUsername)) {
            hasChanges = true;
        }
        
        if (email != null && !email.equals(currentEmail)) {
            hasChanges = true;
        }
        
        if (phoneNumber != null && !phoneNumber.equals(currentPhone)) {
            hasChanges = true;
        }
        
        return hasChanges;
    }
    
    // Get gender display name
    public String getGenderDisplayName() {
        if (gender == null) return "Chưa cập nhật";
        
        switch (gender.toLowerCase()) {
            case "male": return "Nam";
            case "female": return "Nữ";
            case "other": return "Khác";
            default: return gender;
        }
    }
    
    // Validate all fields
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               phoneNumber != null && !phoneNumber.trim().isEmpty() &&
               hasValidDateOfBirth() &&
               hasValidGender();
    }
}
