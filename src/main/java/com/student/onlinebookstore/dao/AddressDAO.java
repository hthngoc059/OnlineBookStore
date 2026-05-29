package com.student.onlinebookstore.dao;

import com.student.onlinebookstore.model.Address;
import com.student.onlinebookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AddressDAO {
    // SQL Queries
    private static final String SQL_CREATE_ADDRESS = 
        "INSERT INTO addresses (user_id, full_name, phone, address_line, ward, district, city, is_default) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_GET_ADDRESSES_BY_USER = 
        "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC, address_id DESC";
    
    private static final String SQL_GET_ADDRESS_BY_ID = 
        "SELECT * FROM addresses WHERE address_id = ?";
    
    private static final String SQL_UPDATE_ADDRESS = 
        "UPDATE addresses SET full_name = ?, phone = ?, address_line = ?, ward = ?, district = ?, city = ? WHERE address_id = ?";
    
    private static final String SQL_DELETE_ADDRESS = 
        "DELETE FROM addresses WHERE address_id = ?";
    
    private static final String SQL_SET_DEFAULT = 
        "UPDATE addresses SET is_default = false WHERE user_id = ?";
    
    private static final String SQL_SET_DEFAULT_ADDRESS = 
        "UPDATE addresses SET is_default = true WHERE address_id = ?";
    
    public boolean createAddress(Address address) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE_ADDRESS)) {
            
            // If this is default, reset other addresses
            if (address.getIsDefault()) {
                resetDefaultAddresses(address.getUser().getUserId());
            }
            
            pstmt.setInt(1, address.getUser().getUserId());
            pstmt.setString(2, address.getFullName());
            pstmt.setString(3, address.getPhone());
            pstmt.setString(4, address.getAddressLine());
            pstmt.setString(5, address.getWard());
            pstmt.setString(6, address.getDistrict());
            pstmt.setString(7, address.getCity());
            pstmt.setBoolean(8, address.getIsDefault());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Address> getAddressesByUserId(int userId) {
        List<Address> addresses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ADDRESSES_BY_USER)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return addresses;
    }
    
    public Address getAddressById(int addressId) {
        Address address = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ADDRESS_BY_ID)) {
            
            pstmt.setInt(1, addressId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    address = mapResultSetToAddress(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return address;
    }
    
    public boolean updateAddress(Address address) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_ADDRESS)) {
            
            pstmt.setString(1, address.getFullName());
            pstmt.setString(2, address.getPhone());
            pstmt.setString(3, address.getAddressLine());
            pstmt.setString(4, address.getWard());
            pstmt.setString(5, address.getDistrict());
            pstmt.setString(6, address.getCity());
            pstmt.setInt(7, address.getAddressId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteAddress(int addressId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_ADDRESS)) {
            
            pstmt.setInt(1, addressId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean setDefaultAddress(int userId, int addressId) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Reset all addresses
            try (PreparedStatement pstmt1 = conn.prepareStatement(SQL_SET_DEFAULT)) {
                pstmt1.setInt(1, userId);
                pstmt1.executeUpdate();
            }
            
            // Set this address as default
            try (PreparedStatement pstmt2 = conn.prepareStatement(SQL_SET_DEFAULT_ADDRESS)) {
                pstmt2.setInt(1, addressId);
                pstmt2.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void resetDefaultAddresses(int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SET_DEFAULT)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
    
    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setAddressId(rs.getInt("address_id"));
        address.setFullName(rs.getString("full_name"));
        address.setPhone(rs.getString("phone"));
        address.setAddressLine(rs.getString("address_line"));
        address.setWard(rs.getString("ward"));
        address.setDistrict(rs.getString("district"));
        address.setCity(rs.getString("city"));
        address.setIsDefault(rs.getBoolean("is_default"));
        return address;
    }
}