package com.neuromuser.boundless_blocks.config;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side cache of server config
 * This is synchronized from the server when the player joins
 */
public class ClientConfigCache {
    private static List<String> allowedKeywords = new ArrayList<>();
    private static List<String> blacklistedKeywords = new ArrayList<>();
    private static boolean hasServerConfig = false;

    /**
     * Update cache with config from server
     */
    public static void updateFromServer(List<String> allowed, List<String> blacklisted) {
        allowedKeywords = new ArrayList<>(allowed);
        blacklistedKeywords = new ArrayList<>(blacklisted);
        hasServerConfig = true;
    }

    /**
     * Clear cache when disconnecting from server
     */
    public static void clear() {
        allowedKeywords.clear();
        blacklistedKeywords.clear();
        hasServerConfig = false;
    }

    /**
     * Check if a block is allowed for crafting (uses server config if available)
     */
    public static boolean isBlockAllowedForCrafting(Identifier blockId) {
        if (!hasServerConfig) {
            // No server config yet, use local config as fallback
            return BoundlessConfig.isBlockAllowedForCrafting(blockId);
        }

        String path = blockId.getPath();

        // Check blacklist first (priority)
        boolean isBlacklisted = blacklistedKeywords.stream().anyMatch(path::contains);
        if (isBlacklisted) {
            return false;
        }

        // Check allowed keywords
        return allowedKeywords.stream().anyMatch(path::contains);
    }

    /**
     * Check if we have received server config
     */
    public static boolean hasServerConfig() {
        return hasServerConfig;
    }
}