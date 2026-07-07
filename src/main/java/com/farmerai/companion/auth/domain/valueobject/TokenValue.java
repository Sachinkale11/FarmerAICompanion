package com.farmerai.companion.auth.domain.valueobject;

import java.util.Objects;

/**
 * Value Object wrapping an opaque refresh token string.
 *
 * <p>Validates that the token is non-null and non-blank.</p>
 */
public final class TokenValue {

    private final String value;

    public TokenValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Token value must not be null or blank");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenValue that = (TokenValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TokenValue{****}";
    }
}
