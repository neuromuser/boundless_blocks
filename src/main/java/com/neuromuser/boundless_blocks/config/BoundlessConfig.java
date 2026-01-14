package com.neuromuser.boundless_blocks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoundlessConfig {
    // Standard Fabric Logger
    private static final Logger LOGGER = LoggerFactory.getLogger("BoundlessBlocks");

    public static int craftStacksCount = 9;
    public static int itemsPerStack = 64;
    public static List<String> allowedKeywords = new ArrayList<>();
    public static List<String> blacklistedKeywords = new ArrayList<>();

    private static final List<String> DEFAULT_KEYWORDS = Arrays.asList(
            // Building blocks
            "planks", "log", "wood", "stripped", "bricks", "stone", "block",
            "slab", "stairs", "fence", "wall", "glass", "door", "trapdoor", "tile",
            "cobblestone", "mossy", "smooth", "polished", "chiseled", "cut",
            "concrete", "terracotta", "wool", "sandstone", "prismarine", "purpur",
            "quartz", "nether", "end", "blackstone", "deepslate", "sand",
            // Ores and minerals
            "ore", "copper", "iron", "gold", "diamond", "emerald", "lapis",
            "redstone", "coal", "amethyst",
            // Create mod blocks
            "casing", "andesite_alloy", "zinc", "brass", "railway",
            "girder", "metal", "panel"
    );

    private static final List<String> DEFAULT_BLACKLIST = Arrays.asList(
            // Technical/non-placeable blocks
            "air", "water", "lava", "fire", "soul_fire",
            // Spawners and technical
            "spawner", "portal", "end_portal", "end_gateway",
            // Liquids
            "flowing", "bubble_column",
            // Plants that need special conditions
            "potted_", "wall_",
            // Other technical
            "command_block", "structure_block", "jigsaw", "barrier", "light"
    );

    // GSON Instance fields
    private int savedCraftStacksCount = 9;
    private int savedItemsPerStack = 64;
    private List<String> savedAllowedKeywords = new ArrayList<>(DEFAULT_KEYWORDS);
    private List<String> savedBlacklistedKeywords = new ArrayList<>(DEFAULT_BLACKLIST);

    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("boundless_config.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            allowedKeywords = new ArrayList<>(DEFAULT_KEYWORDS);
            blacklistedKeywords = new ArrayList<>(DEFAULT_BLACKLIST);
            LOGGER.info("Created new config with {} allowed keywords and {} blacklisted keywords",
                    allowedKeywords.size(), blacklistedKeywords.size());
            save();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            BoundlessConfig data = GSON.fromJson(reader, BoundlessConfig.class);
            if (data != null) {
                craftStacksCount = data.savedCraftStacksCount;
                itemsPerStack = data.savedItemsPerStack;
                // Ensure lists aren't null if JSON is corrupted
                allowedKeywords = data.savedAllowedKeywords != null ? data.savedAllowedKeywords : new ArrayList<>(DEFAULT_KEYWORDS);
                blacklistedKeywords = data.savedBlacklistedKeywords != null ? data.savedBlacklistedKeywords : new ArrayList<>(DEFAULT_BLACKLIST);

                LOGGER.info("Loaded config: {} allowed keywords, {} blacklisted keywords",
                        allowedKeywords.size(), blacklistedKeywords.size());
                LOGGER.debug("Blacklist: {}", blacklistedKeywords);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load BoundlessConfig!", e);
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            BoundlessConfig dataToSave = new BoundlessConfig();
            dataToSave.savedCraftStacksCount = craftStacksCount;
            dataToSave.savedItemsPerStack = itemsPerStack;
            dataToSave.savedAllowedKeywords = allowedKeywords;
            dataToSave.savedBlacklistedKeywords = blacklistedKeywords;

            GSON.toJson(dataToSave, writer);

            try {
                if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
                    REICompat.updateFilter();
                }
            } catch (Exception e) {
                // Log error if REI API is missing or fails
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save BoundlessConfig!", e);
        }
    }

    /**
     * Check if a block is allowed for crafting based on config keywords
     * First checks blacklist (takes priority), then checks allowed keywords
     */
    public static boolean isBlockAllowedForCrafting(Identifier blockId) {
        String path = blockId.getPath();

        // Check blacklist first - if it matches, block is NOT allowed
        boolean isBlacklisted = blacklistedKeywords.stream().anyMatch(path::contains);
        if (isBlacklisted) {
            LOGGER.debug("Block {} is BLACKLISTED (matches: {})", blockId,
                    blacklistedKeywords.stream().filter(path::contains).findFirst().orElse(""));
            return false;
        }

        // Check if the path contains any of the allowed keywords
        boolean isAllowed = allowedKeywords.stream().anyMatch(path::contains);
        if (!isAllowed) {
            LOGGER.debug("Block {} is NOT ALLOWED (no keyword match)", blockId);
        }
        return isAllowed;
    }

    /**
     * Getter methods for config values
     */
    public static int getCraftStacksCount() {
        return craftStacksCount;
    }

    public static int getItemsPerStack() {
        return itemsPerStack;
    }

    private static class REICompat {
        private static void updateFilter() {
            try {
                me.shedaniel.rei.api.client.registry.entry.EntryRegistry.getInstance().refilter();
            } catch (Throwable t) {
                LOGGER.error("Failed to refresh REI filter", t);
            }
        }
    }
}