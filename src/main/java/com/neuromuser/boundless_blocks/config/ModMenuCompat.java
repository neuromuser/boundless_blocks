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
                    .setTitle(Text.translatable("title.mymod.config"));

            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.mymod.general"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // 1. Craft Stacks Count
            general.addEntry(entryBuilder.startIntSlider(Text.translatable("option.mymod.stacks"), BoundlessConfig.craftStacksCount, 2, 9)
                    .setDefaultValue(9)
                    .setSaveConsumer(newValue -> BoundlessConfig.craftStacksCount = newValue)
                    .build());

            // 2. Items Per Stack
            general.addEntry(entryBuilder.startIntField(Text.translatable("option.mymod.stack_size"), BoundlessConfig.itemsPerStack)
                    .setDefaultValue(64)
                    .setSaveConsumer(newValue -> BoundlessConfig.itemsPerStack = newValue)
                    .build());

            // 3. Keywords List
            general.addEntry(entryBuilder.startStrList(Text.translatable("option.mymod.keywords"), BoundlessConfig.allowedKeywords)
                    .setDefaultValue(Arrays.asList(
                            "planks", "log", "wood", "stripped", "bricks", "stone", "ore", "block",
                            "slab", "stairs", "fence", "wall", "glass", "door", "trapdoor", "tile",
                            "cobblestone", "mossy", "smooth", "polished", "chiseled", "cut",
                            "concrete", "terracotta", "wool", "sandstone", "prismarine", "purpur",
                            "quartz", "nether", "end", "blackstone", "deepslate", "copper", "iron",
                            "gold", "diamond", "emerald", "lapis", "redstone", "coal", "amethyst"
                    ))
                    .setSaveConsumer(newValue -> BoundlessConfig.allowedKeywords = newValue)
                    .build());

            builder.setSavingRunnable(() -> {
                // IMPORTANT: Ensure you have a method to save BoundlessConfig to a file!
                BoundlessConfig.save();
            });

            return builder.build();
        };
    }
}