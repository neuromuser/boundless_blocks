package com.neuromuser.boundless_blocks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
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

    private static final List<String> DEFAULT_KEYWORDS = Arrays.asList(
            "planks", "log", "wood", "stripped", "bricks", "stone", "block",
            "slab", "stairs", "fence", "wall", "glass", "door", "trapdoor", "tile",
            "cobblestone", "mossy", "smooth", "polished", "chiseled", "cut",
            "concrete", "terracotta", "wool", "sandstone", "prismarine", "purpur",
            "quartz", "nether", "end", "blackstone", "deepslate", "sand"
    );

    // GSON Instance fields
    private int savedCraftStacksCount = 9;
    private int savedItemsPerStack = 64;
    private List<String> savedAllowedKeywords = new ArrayList<>(DEFAULT_KEYWORDS);

    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("boundless_config.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            allowedKeywords = new ArrayList<>(DEFAULT_KEYWORDS);
            save();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            BoundlessConfig data = GSON.fromJson(reader, BoundlessConfig.class);
            if (data != null) {
                craftStacksCount = data.savedCraftStacksCount;
                itemsPerStack = data.savedItemsPerStack;
                // Ensure list isn't null if JSON is corrupted
                allowedKeywords = data.savedAllowedKeywords != null ? data.savedAllowedKeywords : new ArrayList<>(DEFAULT_KEYWORDS);
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

