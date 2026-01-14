package com.neuromuser.boundless_blocks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoundlessConfig {
    // 1. Define your variables with default values
    public static int craftStacksCount = 9;
    public static int itemsPerStack = 64;
    public static List<String> allowedKeywords = new ArrayList<>(Arrays.asList(
            "planks", "log", "wood", "stripped", "bricks", "stone", "ore", "block",
            "slab", "stairs", "fence", "wall", "glass", "door", "trapdoor", "tile",
            "cobblestone", "mossy", "smooth", "polished", "chiseled", "cut",
            "concrete", "terracotta", "wool", "sandstone", "prismarine", "purpur",
            "quartz", "nether", "end", "blackstone", "deepslate", "copper", "iron",
            "gold", "diamond", "emerald", "lapis", "redstone", "coal", "amethyst"
    ));

    // File location: /config/boundless_config.json
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("boundless_config.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 2. Load method (Call this in your ModInitializer)
    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save(); // Create default file if it doesn't exist
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            BoundlessConfig data = GSON.fromJson(reader, BoundlessConfig.class);
            if (data != null) {
                craftStacksCount = data.craftStacksCount;
                itemsPerStack = data.itemsPerStack;
                allowedKeywords = data.allowedKeywords;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 3. Save method (Call this in the Cloth Config 'setSavingRunnable')
    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new BoundlessConfig(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}