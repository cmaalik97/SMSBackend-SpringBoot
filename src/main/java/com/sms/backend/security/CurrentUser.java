package com.sms.backend.security;

// The authenticated principal we attach to Spring Security's context on every
// request - decoded straight from the JWT claims (see JwtAuthFilter).
public record CurrentUser(Long userId, String email, String role, String fullName) {
}
