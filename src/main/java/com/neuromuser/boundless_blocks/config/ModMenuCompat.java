package com.neuromuser.boundless_blocks.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import java.util.Arrays;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("title.boundless_blocks.config"));

            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.boundless_blocks.general"));
            ConfigEntryBuilder entry = builder.entryBuilder();

            general.addEntry(entry.startIntSlider(
                            Text.translatable("option.boundless_blocks.stacks"),
                            BoundlessConfig.craftStacksCount, 1, 9)
                    .setDefaultValue(9)
                    .setSaveConsumer(v -> BoundlessConfig.craftStacksCount = v)
                    .build());

            general.addEntry(entry.startBooleanToggle(
                            Text.translatable("option.boundless_blocks.show_canbeinfinite_tooltips"),
                            BoundlessConfig.showCanBeInfiniteTooltips)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> BoundlessConfig.showCanBeInfiniteTooltips = v)
                    .build());

            general.addEntry(entry.startBooleanToggle(
                            Text.translatable("option.boundless_blocks.allow_unpacking"),
                            BoundlessConfig.allowUnpacking)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> BoundlessConfig.allowUnpacking = v)
                    .build());

            general.addEntry(entry.startBooleanToggle(
                            Text.translatable("option.boundless_blocks.remove_picked"),
                            BoundlessConfig.removePickedBlocks)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> BoundlessConfig.removePickedBlocks = v)
                    .build());

            general.addEntry(entry.startStrList(
                            Text.translatable("option.boundless_blocks.keywords"),
                            BoundlessConfig.allowedKeywords)
                    .setSaveConsumer(v -> BoundlessConfig.allowedKeywords = v)
                    .build());

            general.addEntry(entry.startStrList(
                            Text.translatable("option.boundless_blocks.blacklist"),
                            BoundlessConfig.blacklistedKeywords)
                    .setSaveConsumer(v -> BoundlessConfig.blacklistedKeywords = v)
                    .build());

            builder.setSavingRunnable(BoundlessConfig::save);
            return builder.build();
        };
    }
}