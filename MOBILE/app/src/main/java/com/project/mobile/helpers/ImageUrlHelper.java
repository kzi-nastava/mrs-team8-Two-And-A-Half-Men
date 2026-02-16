package com.project.mobile.helpers;

import com.project.mobile.BuildConfig;

public class ImageUrlHelper {

    // Base URL without /api/v1/
    private static final String IMAGE_BASE_URL = "http://" + BuildConfig.BASE_URL + ":8080";

    /**
     * Converts relative image path to absolute URL
     * @param imagePath Relative path from API (e.g., "/uploads/profile/image.jpg")
     * @return Full URL (e.g., "http://192.168.1.1:8080/uploads/profile/image.jpg")
     */
    public static String getFullImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // If already a full URL, return as-is
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }

        // Add leading slash if missing
        if (!imagePath.startsWith("/")) {
            imagePath = "/" + imagePath;
        }

        return IMAGE_BASE_URL + imagePath;
    }
}