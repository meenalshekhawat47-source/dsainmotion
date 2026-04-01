package com.dsainmotion.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;
    private String pass;

    @Column(name = "phone")
    private String phone;   

    @Column(name = "streak")
    private Integer streak = 0;

    @Column(name = "last_login_date")
    private java.time.LocalDate lastLoginDate;

    @Column(name = "reset_token")
private String resetToken;

@Column(name = "token_expiry")
private java.time.LocalDateTime tokenExpiry;

    // getters & setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {        
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }

    public java.time.LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(java.time.LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    public String getResetToken() {
    return resetToken;
}

public void setResetToken(String resetToken) {
    this.resetToken = resetToken;
}

public LocalDateTime getTokenExpiry() {
    return tokenExpiry;
}

public void setTokenExpiry(LocalDateTime tokenExpiry) {
    this.tokenExpiry = tokenExpiry;
}
}

