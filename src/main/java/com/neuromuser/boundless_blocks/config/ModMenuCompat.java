package com.neuromuser.boundless_blocks.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.Arrays;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("title.boundless_blocks.config"));

            ConfigCategory general = builder.getOrCreateCategory(
                    Text.translatable("category.boundless_blocks.general")
            );
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // 1. Craft Stacks Count
            general.addEntry(entryBuilder.startIntSlider(
                            Text.translatable("option.boundless_blocks.stacks"),
                            BoundlessConfig.craftStacksCount,
                            1,
                            9
                    )
                    .setDefaultValue(9)
                    .setSaveConsumer(newValue -> BoundlessConfig.craftStacksCount = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(
                            Text.translatable("option.boundless_blocks.show_canbeinfinite_tooltips"),
                            BoundlessConfig.showCanBeInfiniteTooltips
                    )
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("tooltip.boundless_blocks.show_canbeinfinite_tooltips"))
                    .setSaveConsumer(newValue -> BoundlessConfig.showCanBeInfiniteTooltips = newValue)
                    .build());


            general.addEntry(entryBuilder.startBooleanToggle(
                            Text.translatable("option.boundless_blocks.allow_unpacking"),
                            BoundlessConfig.allowUnpacking
                    )
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("tooltip.boundless_blocks.allow_unpacking"))
                    .setSaveConsumer(newValue -> BoundlessConfig.allowUnpacking = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(
                            Text.translatable("option.boundless_blocks.remove_picked"),
                            BoundlessConfig.removePickedBlocks
                    )
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("tooltip.boundless_blocks.remove_picked"))
                    .setSaveConsumer(newValue -> BoundlessConfig.removePickedBlocks = newValue)
                    .build());

            // 3. Keywords List
            general.addEntry(entryBuilder.startStrList(
                            Text.translatable("option.boundless_blocks.keywords"),
                            BoundlessConfig.allowedKeywords
                    )
                    .setDefaultValue(Arrays.asList(
                            "planks", "log", "wood", "stripped", "stem", "hyphae", "bamboo",
                            "willow", "cherry", "mahogany", "ebony", "redwood", "baobab",

                            // --- Standard & Modded Stone/Earth ---
                            "stone", "cobblestone", "mossy", "smooth", "polished", "chiseled",
                            "cut", "bricks", "tile", "terracotta", "concrete", "wool",
                            "sandstone", "prismarine", "purpur", "quartz", "blackstone",
                            "deepslate", "tuff", "calcite", "granite", "diorite", "andesite",
                            "basalt", "scoria", "scoria_bricks", "limestone", "shale", "slate",
                            "netherrack", "soul_sand", "mud", "clay", "sand", "gravel",

                            // --- Common Modded Decorative Terms ---
                            "casing", "andesite_alloy", "girder", "panel", "sheet_metal",
                            "bracket", "window", "scaffolding", "frame", "pillar", "column",
                            "plating", "shingle", "paving", "ornate", "layered", "embossed",

                            // --- Shapes & Structures ---
                            "block", "slab", "stairs", "fence", "wall", "glass", "door",
                            "trapdoor", "gate", "ladder", "vertical"
                    ))
                    .setTooltip(Text.translatable("tooltip.boundless_blocks.keywords"))
                    .setSaveConsumer(newValue -> BoundlessConfig.allowedKeywords = newValue)
                    .build());

            // 4. Blacklisted Keywords List
            general.addEntry(entryBuilder.startStrList(
                            Text.translatable("option.boundless_blocks.blacklist"),
                            BoundlessConfig.blacklistedKeywords
                    )
                    .setDefaultValue(Arrays.asList(
                            "diamond", "netherite", "gold", "iron", "emerald", "lapis",
                            "redstone", "coal", "copper", "amethyst", "raw_", "debris",
                            "obsidian", "crying_obsidian", "lodestone",
                            "steel", "bronze", "tin", "lead", "silver", "nickel", "zinc",
                            "platinum", "uranium", "osmium", "aluminum", "brass", "electrum",
                            "invar", "constantan", "signalum", "lumium", "enderium",

                            "chest", "shulker", "barrel", "hopper", "dispenser",
                            "dropper", "furnace", "blast_furnace", "smoker", "anvil",
                            "enchanting_table", "beacon", "conduit", "tank", "battery",
                            "generator", "energy", "machine", "processor", "engine",
                            "stone_cutter","end_portal",
                            "potted_", "wall_", "waystone", "sharestone"
                    ))
                    .setTooltip(Text.translatable("tooltip.boundless_blocks.blacklist"))
                    .setSaveConsumer(newValue -> BoundlessConfig.blacklistedKeywords = newValue)
                    .build());

            builder.setSavingRunnable(BoundlessConfig::save);

            return builder.build();
        };
    }
}