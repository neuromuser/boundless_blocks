package com.neuromuser.boundless_blocks.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class BoundlessConfig {
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("boundless_blocks.properties").toFile();

    public static int craftStacksCount = 9;
    public static boolean showCanBeInfiniteTooltips = true;
    public static boolean allowUnpacking = true;
    public static boolean removePickedBlocks = true;
    public static List<String> allowedKeywords;
    public static List<String> blacklistedKeywords;

    private static final String DEFAULT_ALLOWED = "planks,log,wood,stripped,stem,hyphae,bamboo,stone,cobblestone,mossy,smooth,polished,chiseled,cut,bricks,tile,terracotta,concrete,wool,sandstone,prismarine,purpur,quartz,blackstone,deepslate,tuff,calcite,granite,diorite,andesite,basalt,netherrack,soul_sand,mud,clay,sand,gravel,slab,stairs,fence,wall,glass,dirt,grass_block,podzol,mycelium,coarse_dirt,rooted_dirt,snow,ice,packed_ice,blue_ice,end_stone,nylium,packed_mud,mud_bricks";
    private static final String DEFAULT_BLACKLIST = "diamond,netherite,gold,iron,emerald,lapis,redstone,coal,copper,amethyst,raw_,debris,obsidian,crying_obsidian,lodestone,chest,shulker,barrel,hopper,dispenser,dropper,furnace,blast_furnace,smoker,anvil,enchanting_table,beacon,conduit,stone_cutter,end_portal,kelp,potted_,wall_,waystone";

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            setDefaults();
            save();
            return;
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(CONFIG_FILE.toPath())) {
            props.load(in);
            craftStacksCount = Integer.parseInt(props.getProperty("craftStacksCount", "9"));
            showCanBeInfiniteTooltips = Boolean.parseBoolean(props.getProperty("showCanBeInfiniteTooltips", "true"));
            allowUnpacking = Boolean.parseBoolean(props.getProperty("allowUnpacking", "true"));
            removePickedBlocks = Boolean.parseBoolean(props.getProperty("removePickedBlocks", "true"));
            allowedKeywords = Arrays.asList(props.getProperty("allowedKeywords", DEFAULT_ALLOWED).split(","));
            blacklistedKeywords = Arrays.asList(props.getProperty("blacklistedKeywords", DEFAULT_BLACKLIST).split(","));
        } catch (Exception e) {
            setDefaults();
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("craftStacksCount", String.valueOf(craftStacksCount));
        props.setProperty("showCanBeInfiniteTooltips", String.valueOf(showCanBeInfiniteTooltips));
        props.setProperty("allowUnpacking", String.valueOf(allowUnpacking));
        props.setProperty("removePickedBlocks", String.valueOf(removePickedBlocks));
        props.setProperty("allowedKeywords", String.join(",", allowedKeywords));
        props.setProperty("blacklistedKeywords", String.join(",", blacklistedKeywords));

        try (OutputStream out = Files.newOutputStream(CONFIG_FILE.toPath())) {
            props.store(out, "Boundless Blocks Configuration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setDefaults() {
        allowedKeywords = Arrays.asList(DEFAULT_ALLOWED.split(","));
        blacklistedKeywords = Arrays.asList(DEFAULT_BLACKLIST.split(","));
    }

    public static boolean isBlockAllowed(Identifier blockId) {
        String path = blockId.getPath();
        if (blacklistedKeywords.stream().anyMatch(path::contains)) return false;
        return allowedKeywords.stream().anyMatch(path::contains);
    }
}