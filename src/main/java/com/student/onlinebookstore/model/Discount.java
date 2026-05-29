package com.student.onlinebookstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer discountId;
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;
    
    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "max_usage")
    private Integer maxUsage;
    
    @Column(name = "used_count")
    private Integer usedCount = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToMany(mappedBy = "discounts")
    private Set<Book> books = new HashSet<>();
    
    public enum DiscountType {
        percent, fixed
    }
    
    public Discount() {}
    
    // Getters and Setters
    public Integer getDiscountId() { return discountId; }
    public void setDiscountId(Integer discountId) { this.discountId = discountId; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
    
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public String getDiscountValueFormatted() {
        if (discountValue == null) return "0";
        
        if (discountType == DiscountType.percent) {
            return String.format("%.0f%%", discountValue);
        } else {
            return String.format("%,.0f", discountValue).replace(",", ".");
        }
    }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public Integer getMaxUsage() { return maxUsage; }
    public void setMaxUsage(Integer maxUsage) { this.maxUsage = maxUsage; }
    
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Set<Book> getBooks() { return books; }
    public void setBooks(Set<Book> books) { this.books = books; }
}