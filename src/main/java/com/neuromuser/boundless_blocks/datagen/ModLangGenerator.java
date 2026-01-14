package com.neuromuser.boundless_blocks.datagen;

import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModLangGenerator extends FabricLanguageProvider {
    public ModLangGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        // The ONLY line you need for names.
        // %s is where the block name (like "Oak Planks") goes.
        translationBuilder.add("item.boundless_blocks.infinite_format", "∞ %s ∞");
    }
}