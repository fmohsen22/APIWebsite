package com.website.api.mosi.helper;

import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private static final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long DEFAULT_EXPIRATION_TIME = 5 * 60 * 1000; // Default to 5 minutes

    // Get data from the cache
    public static String get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && (System.currentTimeMillis() - entry.timestamp < entry.expirationTime)) {
            return entry.data;
        }
        return null; // Cache expired or not found
    }

    // Put data in the cache
    public static void put(String key, String data, long expirationTime) {
        cache.put(key, new CacheEntry(data, System.currentTimeMillis(), expirationTime));
    }

    // Overloaded method for default expiration time
    public static void put(String key, String data) {
        put(key, data, DEFAULT_EXPIRATION_TIME);
    }

    static class CacheEntry {
        String data;
        long timestamp;
        long expirationTime;

        CacheEntry(String data, long timestamp, long expirationTime) {
            this.data = data;
            this.timestamp = timestamp;
            this.expirationTime = expirationTime;
        }
    }
}
