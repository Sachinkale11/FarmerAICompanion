package com.farmerai.companion.auth.infrastructure.security.adapter;

import com.farmerai.companion.auth.application.port.output.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements the application-layer {@link PasswordEncoderPort}
 * using Spring Security's {@link BCryptPasswordEncoder}.
 *
 * <p>Strength is set to 12 (higher than the default of 10) for stronger
 * brute-force resistance. Each hash takes ~250ms.</p>
 */
@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder encoder;

    public PasswordEncoderAdapter() {
        this.encoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
