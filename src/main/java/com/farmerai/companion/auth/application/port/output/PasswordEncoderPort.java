package com.farmerai.companion.auth.application.port.output;

/**
 * Output port for password encoding operations.
 *
 * <p>Decouples the application layer from Spring Security's
 * {@code BCryptPasswordEncoder}.</p>
 */
public interface PasswordEncoderPort {

    /**
     * Hashes a raw password using BCrypt.
     *
     * @param rawPassword the raw password string
     * @return the BCrypt hash
     */
    String encode(String rawPassword);

    /**
     * Checks if a raw password matches a BCrypt hash.
     *
     * @param rawPassword     the raw password string
     * @param encodedPassword the BCrypt hash to compare against
     * @return true if they match
     */
    boolean matches(String rawPassword, String encodedPassword);
}
