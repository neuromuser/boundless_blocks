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
                            2,
                            9
                    )
                    .setDefaultValue(9)
                    .setSaveConsumer(newValue -> BoundlessConfig.craftStacksCount = newValue)
                    .build());


            // 3. Keywords List
            general.addEntry(entryBuilder.startStrList(
                            Text.translatable("option.boundless_blocks.keywords"),
                            BoundlessConfig.allowedKeywords
                    )
                    .setDefaultValue(Arrays.asList(
                            "planks", "log", "wood", "stripped", "bricks", "stone", "ore", "block",
                            "slab", "stairs", "fence", "wall", "glass", "door", "trapdoor", "tile",
                            "cobblestone", "mossy", "smooth", "polished", "chiseled", "cut",
                            "concrete", "terracotta", "wool", "sandstone", "prismarine", "purpur",
                            "quartz", "nether", "end", "blackstone", "deepslate", "copper", "iron",
                            "gold", "diamond", "emerald", "lapis", "redstone", "coal", "amethyst"
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
                            "air", "water", "lava", "fire", "soul_fire",
                            "spawner", "portal", "end_portal", "end_gateway",
                            "flowing", "bubble_column",
                            "potted_", "wall_",
                            "command_block", "structure_block", "jigsaw", "barrier", "light", "waystone"
                    ))
                    .setTooltip(Text.translatable("tooltip.boundless_blocks.blacklist"))
                    .setSaveConsumer(newValue -> BoundlessConfig.blacklistedKeywords = newValue)
                    .build());

            builder.setSavingRunnable(BoundlessConfig::save);

            return builder.build();
        };
    }
}