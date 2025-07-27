package org.example.cleanprj.utils;

import org.example.cleanprj.models.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && "Admin".equals(currentUser.getRole());
    }

    public static boolean isTourist() {
        return currentUser != null && "Tourist".equals(currentUser.getRole());
    }

    public static void logout() {
        currentUser = null;
    }
}
