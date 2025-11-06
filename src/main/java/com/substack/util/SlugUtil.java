package com.substack.util;

public class SlugUtil {
    public static String toSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    public static String sanitizeUsername(String username) {
        return username.toLowerCase()
                .replaceAll("[^a-z0-9_]", "")
                .substring(0, Math.min(username.length(), 20));
    }
}
