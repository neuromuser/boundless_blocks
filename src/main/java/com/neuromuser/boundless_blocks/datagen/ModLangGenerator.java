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
        // Infinite item name format (%s is replaced with the block name)
        translationBuilder.add("item.boundless_blocks.infinite_format", "∞ %s ∞");

        // Tooltip for infinite items
        translationBuilder.add("tooltip.boundless_blocks.infinite_item.line1", "§bInfinite - never runs out!");
        translationBuilder.add("tooltip.boundless_blocks.infinite_item.line2", "§7Right-click to place without consuming");

        // Tooltip for blocks that can become infinite
        translationBuilder.add("tooltip.boundless_blocks.can_be_infinite", "§aCan be crafted into an infinite block");

        // Mod Menu config screen translations
        translationBuilder.add("title.boundless_blocks.config", "Boundless Blocks Configuration");
        translationBuilder.add("category.boundless_blocks.general", "General Settings");
        translationBuilder.add("option.boundless_blocks.stacks", "Craft Stacks Count");
        translationBuilder.add("option.boundless_blocks.stack_size", "Items Per Stack");
        translationBuilder.add("option.boundless_blocks.keywords", "Allowed Keywords");
    }

    public static class Ukrainian extends FabricLanguageProvider {
        public Ukrainian(FabricDataOutput dataOutput) {
            super(dataOutput, "uk_ua");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            // Infinite item name format
            translationBuilder.add("item.boundless_blocks.infinite_format", "∞ %s ∞");

            // Tooltip for infinite items
            translationBuilder.add("tooltip.boundless_blocks.infinite_item.line1", "§bБезкінечний - ніколи не закінчується!");
            translationBuilder.add("tooltip.boundless_blocks.infinite_item.line2", "§7Клацніть правою кнопкою миші, щоб розмістити без витрат");

            // Tooltip for blocks that can become infinite
            translationBuilder.add("tooltip.boundless_blocks.can_be_infinite", "§aМожна скрафтити в безкінечний блок");

            // Mod Menu config screen translations
            translationBuilder.add("title.boundless_blocks.config", "Налаштування Boundless Blocks");
            translationBuilder.add("category.boundless_blocks.general", "Загальні налаштування");
            translationBuilder.add("option.boundless_blocks.stacks", "Кількість стеків для крафту");
            translationBuilder.add("option.boundless_blocks.stack_size", "Предметів у стеку");
            translationBuilder.add("option.boundless_blocks.keywords", "Дозволені ключові слова");
        }
    }
}