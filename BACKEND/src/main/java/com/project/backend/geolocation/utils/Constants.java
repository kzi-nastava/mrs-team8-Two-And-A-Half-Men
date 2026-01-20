package com.project.backend.geolocation.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
    public static final double EARTH_RADIUS_M = 6371088;
    public static int MAX_CACHE_SIZE = 100;

    static {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("application.properties")) {
            prop.load(input);
            String value = prop.getProperty("location_transformer_max_cache_size");
            if (value != null) {
                MAX_CACHE_SIZE = Integer.parseInt(value);
            }
        } catch (Exception ignored) {
        }
        try (InputStream input = Constants.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                prop.load(input);
                String value = prop.getProperty("location_transformer_max_cache_size");
                if (value != null) {
                    MAX_CACHE_SIZE = Integer.parseInt(value);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
