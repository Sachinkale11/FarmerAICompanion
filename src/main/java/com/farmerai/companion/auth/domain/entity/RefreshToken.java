package com.farmerai.companion.auth.domain.entity;

import com.farmerai.companion.auth.domain.valueobject.TokenValue;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a refresh token.
 *
 * <p>Refresh tokens are opaque, stored in the database, and support
 * revocation for logout and security scenarios.</p>
 *
 * <p>This is a pure domain entity — no framework annotations.</p>
 */
public class RefreshToken {

    private UUID id;
    private TokenValue token;
    private UUID userId;
    private String deviceId;
    private String deviceName;
    private String platform;
    private Instant lastUsedAt;
    private Instant expiryDate;
    private boolean revoked;
    private Instant createdAt;
    private Instant updatedAt;

    // ======================== Factory Methods ========================

    /**
     * Creates a new non-revoked refresh token.
     *
     * @param userId     the user this token belongs to
     * @param tokenValue the opaque token string
     * @param expiryDate when the token expires
     * @param deviceId   unique device identifier
     * @param deviceName human readable device name
     * @param platform   device platform
     * @return new RefreshToken instance
     */
    public static RefreshToken create(UUID userId, TokenValue tokenValue, Instant expiryDate,
                                      String deviceId, String deviceName, String platform) {
        RefreshToken rt = new RefreshToken();
        rt.id = UUID.randomUUID();
        rt.token = Objects.requireNonNull(tokenValue, "Token value must not be null");
        rt.userId = Objects.requireNonNull(userId, "User ID must not be null");
        rt.expiryDate = Objects.requireNonNull(expiryDate, "Expiry date must not be null");
        rt.deviceId = Objects.requireNonNull(deviceId, "Device ID must not be null");
        rt.deviceName = deviceName;
        rt.platform = platform;
        rt.lastUsedAt = Instant.now();
        rt.revoked = false;
        rt.createdAt = Instant.now();
        rt.updatedAt = Instant.now();
        return rt;
    }

    /**
     * Reconstitutes a RefreshToken from persistence.
     */
    public static RefreshToken reconstitute(UUID id, TokenValue token, UUID userId,
                                             String deviceId, String deviceName, String platform, Instant lastUsedAt,
                                             Instant expiryDate, boolean revoked,
                                             Instant createdAt, Instant updatedAt) {
        RefreshToken rt = new RefreshToken();
        rt.id = id;
        rt.token = token;
        rt.userId = userId;
        rt.deviceId = deviceId;
        rt.deviceName = deviceName;
        rt.platform = platform;
        rt.lastUsedAt = lastUsedAt;
        rt.expiryDate = expiryDate;
        rt.revoked = revoked;
        rt.createdAt = createdAt;
        rt.updatedAt = updatedAt;
        return rt;
    }

    // ======================== Business Methods ========================

    /**
     * Revokes this refresh token, making it unusable.
     */
    public void revoke() {
        this.revoked = true;
        this.updatedAt = Instant.now();
    }

    /**
     * Checks if this token has expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    /**
     * Checks if this token is still usable (not revoked AND not expired).
     */
    public boolean isUsable() {
        return !this.revoked && !isExpired();
    }

    /**
     * Updates the last used timestamp.
     */
    public void recordUsage() {
        this.lastUsedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // ======================== Getters ========================

    public UUID getId() {
        return id;
    }

    public TokenValue getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPlatform() {
        return platform;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ======================== Equality ========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RefreshToken{id=" + id + ", userId=" + userId + ", revoked=" + revoked + "}";
    }
}
