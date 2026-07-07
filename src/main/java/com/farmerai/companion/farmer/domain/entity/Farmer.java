package com.farmerai.companion.farmer.domain.entity;

import com.farmerai.companion.farmer.domain.valueobject.Address;

import java.time.Instant;
import java.util.UUID;

public class Farmer {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String preferredLanguage;
    private Address address;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private Farmer(UUID userId, String firstName, String lastName, String phoneNumber, String preferredLanguage, Address address) {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name cannot be blank");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name cannot be blank");

        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.preferredLanguage = preferredLanguage != null ? preferredLanguage : "en";
        this.address = address;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public static Farmer create(UUID userId, String firstName, String lastName, String phoneNumber, String preferredLanguage, Address address) {
        return new Farmer(userId, firstName, lastName, phoneNumber, preferredLanguage, address);
    }

    public void updateProfile(String firstName, String lastName, String phoneNumber, String preferredLanguage, Address address) {
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name cannot be blank");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name cannot be blank");
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        if (preferredLanguage != null) {
            this.preferredLanguage = preferredLanguage;
        }
        this.address = address;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public Address getAddress() { return address; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    // Setters for reconstitution by persistence framework (e.g. MapStruct)
    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    public void setAddress(Address address) { this.address = address; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setVersion(Long version) { this.version = version; }
}
