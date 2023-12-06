package com.example.smartcontactmanager.com.smart.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import com.example.smartcontactmanager.com.smart.entities.Contact;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Entity

public class User {

    @Transient
    private final String patternFormat = "Password must follow the following pattern :  \n1. At least one Capital Letter\n2.At least one small alphabet\n3.At least one special character(@, $, #, %)\n4.At least one digit\n5. Password length should be between 8-15 characters";


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Please enter your name")
    private String name;
    @Column(unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid Email Format")
    @NotBlank(message = "Please enter your email")
    private String email;
//    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[@#\\$%])(?=.*\\d)[A-Za-z@#\\$%\\d]{8,15}$", message = patternFormat)
    @NotBlank(message="Please enter the password")
    private String password;
    private String imageUrl;

    @Column(length = 500)
    private String description;
    @NotBlank(message = "selecting role is mandatory")
    private String role;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Contact> contacts = new ArrayList<>();

    public List<Contact> getcontacts() {
        return contacts;
    }

    public void setcontacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public User(int id, String name, String email, String password, String imageUrl, String description, String role, Boolean enabled) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.description = description;
        this.role = role;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Column(columnDefinition = "DEFAULT 1")
    private Boolean enabled;

    public User() {

    }
}
