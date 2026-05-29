package com.student.onlinebookstore.dto.response;

public class AddressResponse {
    private Integer addressId;
    private String fullName;
    private String phone;
    private String addressLine;
    private String ward;
    private String district;
    private String city;
    private Boolean isDefault;
    private String fullAddress;
    
    // Constructors
    public AddressResponse() {}
    
    public AddressResponse(Integer addressId, String fullName, String phone, 
                          String addressLine, String ward, String district, String city, Boolean isDefault) {
        this.addressId = addressId;
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
        this.fullAddress = buildFullAddress();
    }
    
    // Build full address string
    private String buildFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine != null && !addressLine.isEmpty()) {
            sb.append(addressLine);
        }
        if (ward != null && !ward.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ward);
        }
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        return sb.toString();
    }
    
    // Getters and Setters
    public Integer getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddressLine() {
        return addressLine;
    }
    
    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
        this.fullAddress = buildFullAddress();
    }
    
    public String getWard() {
        return ward;
    }
    
    public void setWard(String ward) {
        this.ward = ward;
        this.fullAddress = buildFullAddress();
    }
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
        this.fullAddress = buildFullAddress();
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
        this.fullAddress = buildFullAddress();
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public String getFullAddress() {
        return fullAddress;
    }
    
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
