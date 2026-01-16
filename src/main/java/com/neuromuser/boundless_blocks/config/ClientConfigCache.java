package com.neuromuser.boundless_blocks.config;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ClientConfigCache {
    private static List<String> allowedKeywords = new ArrayList<>();
    private static List<String> blacklistedKeywords = new ArrayList<>();
    private static boolean showCanBeInfiniteTooltips = true;
    private static boolean allowUnpacking = true;
    private static boolean removePickedBlocks = true;
    private static boolean hasServerConfig = false;

    public static void updateFromServer(List<String> allowed, List<String> blacklisted,
                                        boolean showTooltips, boolean unpacking, boolean removePicked) {
        allowedKeywords = new ArrayList<>(allowed);
        blacklistedKeywords = new ArrayList<>(blacklisted);
        showCanBeInfiniteTooltips = showTooltips;
        allowUnpacking = unpacking;
        removePickedBlocks = removePicked;
        hasServerConfig = true;
    }

    public static void clear() {
        allowedKeywords.clear();
        blacklistedKeywords.clear();
        showCanBeInfiniteTooltips = true;
        allowUnpacking = true;
        removePickedBlocks = true;
        hasServerConfig = false;
    }

    public static boolean isBlockAllowedForCrafting(Identifier blockId) {
        if (!hasServerConfig) {
            return BoundlessConfig.isBlockAllowedForCrafting(blockId);
        }

        String path = blockId.getPath();

        boolean isBlacklisted = blacklistedKeywords.stream().anyMatch(path::contains);
        if (isBlacklisted) {
            return false;
        }

        return allowedKeywords.stream().anyMatch(path::contains);
    }

    public static boolean hasServerConfig() {
        return hasServerConfig;
    }

    public static boolean shouldShowCanBeInfiniteTooltips() {
        return hasServerConfig ? showCanBeInfiniteTooltips : BoundlessConfig.showCanBeInfiniteTooltips;
    }

    public static boolean isUnpackingAllowed() {
        return hasServerConfig ? allowUnpacking : BoundlessConfig.allowUnpacking;
    }

    public static boolean shouldRemovePickedBlocks() {
        return hasServerConfig ? removePickedBlocks : BoundlessConfig.removePickedBlocks;
    }
}