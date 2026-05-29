package com.student.onlinebookstore.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateDiscountRequest {
    
    @NotBlank(message = "Mã giảm giá không được để trống")
    @Size(min = 3, max = 50, message = "Mã giảm giá phải từ 3-50 ký tự")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Mã giảm giá chỉ được chứa chữ hoa, số, gạch dưới và gạch ngang")
    private String code;
    
    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String description;
    
    @NotNull(message = "Loại giảm giá không được để trống")
    @Pattern(regexp = "percent|fixed", message = "Loại giảm giá phải là 'percent' hoặc 'fixed'")
    private String discountType;
    
    @NotNull(message = "Giá trị giảm giá không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm giá phải lớn hơn 0")
    @DecimalMax(value = "100", message = "Giá trị giảm giá phần trăm không được vượt quá 100")
    private BigDecimal discountValue;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;
    
    @Min(value = 1, message = "Số lần sử dụng tối đa phải lớn hơn 0")
    private Integer maxUsage;
    
    @Min(value = 0, message = "Giá trị đơn hàng tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal minOrderValue;
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code.toUpperCase().trim();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }
    
    public BigDecimal getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public Integer getMaxUsage() {
        return maxUsage;
    }
    
    public void setMaxUsage(Integer maxUsage) {
        this.maxUsage = maxUsage;
    }
    
    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }
    
    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }
    
    // Validate that start date is before end date
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return false;
        }
        return startDate.isBefore(endDate);
    }
    
    // Check if discount is percentage type
    public boolean isPercentageType() {
        return "percent".equalsIgnoreCase(discountType);
    }
    
    // Check if discount is fixed amount type
    public boolean isFixedType() {
        return "fixed".equalsIgnoreCase(discountType);
    }
    
    // Validate discount value based on type
    public boolean isValidDiscountValue() {
        if (discountValue == null) {
            return false;
        }
        
        if (isPercentageType()) {
            return discountValue.compareTo(BigDecimal.ZERO) > 0 && 
                   discountValue.compareTo(BigDecimal.valueOf(100)) <= 0;
        } else {
            return discountValue.compareTo(BigDecimal.ZERO) > 0;
        }
    }
}
