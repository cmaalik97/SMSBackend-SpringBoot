package com.sms.backend.security;

import org.springframework.security.core.context.SecurityContextHolder;

// Small helper so service classes can grab "who is calling right now"
// without wiring HttpServletRequest everywhere.
public class AuthUtil {

    public static CurrentUser currentUser() {
        return (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long currentUserId() {
        return currentUser().userId();
    }

    public static String currentRole() {
        return currentUser().role();
    }

    public static boolean isAdmin() {
        return "Admin".equals(currentRole());
    }

    public static boolean isTeacher() {
        return "Teacher".equals(currentRole());
    }

    public static boolean isStudent() {
        return "Student".equals(currentRole());
    }
}
