package com.vbforge.concierge.track;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MainTesterClass {
    public static void main(String[] args) {

        // In a test or main method
        PasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String encoded = encoder.encode("admin123");
        System.out.println("Encoded: " + encoded);

        // Verify
        boolean matches = encoder.matches("admin123", encoded);
        System.out.println("Matches: " + matches); // true
    }
}
