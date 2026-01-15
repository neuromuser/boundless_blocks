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
        translationBuilder.add("option.boundless_blocks.allow_unpacking", "Allow Unpacking");
        translationBuilder.add("option.boundless_blocks.remove_picked", "Remove Picked Blocks");
        translationBuilder.add("option.boundless_blocks.keywords", "Allowed Keywords");
        translationBuilder.add("option.boundless_blocks.blacklist", "Blacklisted Keywords");

        // Tooltips for config options
        translationBuilder.add("tooltip.boundless_blocks.stacks", "How many stacks needed to craft (2-9)");
        translationBuilder.add("tooltip.boundless_blocks.stack_size", "Items per stack required (1-64)");
        translationBuilder.add("tooltip.boundless_blocks.allow_unpacking", "Allow converting infinite blocks back to regular blocks");
        translationBuilder.add("tooltip.boundless_blocks.remove_picked", "Auto-remove picked blocks if you have infinite version");
        translationBuilder.add("tooltip.boundless_blocks.keywords", "Blocks containing these words can be crafted");
        translationBuilder.add("tooltip.boundless_blocks.blacklist", "Blocks containing these words CANNOT be crafted (takes priority)");
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
            translationBuilder.add("tooltip.boundless_blocks.infinite_item.line2", "§bМожна розміщувати і дублювати цей тип блоку без витрат!");

            // Tooltip for blocks that can become infinite
            translationBuilder.add("tooltip.boundless_blocks.can_be_infinite", "§aМожна зробити безкінечним!");

            // Mod Menu config screen translations
            translationBuilder.add("title.boundless_blocks.config", "Налаштування Boundless Blocks");
            translationBuilder.add("category.boundless_blocks.general", "Загальні налаштування");
            translationBuilder.add("option.boundless_blocks.stacks", "Кількість стаків для крафту");
            translationBuilder.add("option.boundless_blocks.allow_unpacking", "Дозволити розпакування");
            translationBuilder.add("option.boundless_blocks.remove_picked", "Видаляти зібрані блоки");
            translationBuilder.add("option.boundless_blocks.keywords", "Дозволені ключові слова");
            translationBuilder.add("option.boundless_blocks.blacklist", "Заблоковані ключові слова");

            // Tooltips for config options
            translationBuilder.add("tooltip.boundless_blocks.stacks", "Скільки стаків потрібно для крафту (2-9)");
            translationBuilder.add("tooltip.boundless_blocks.stack_size", "Предметів у стаку (1-64)");
            translationBuilder.add("tooltip.boundless_blocks.allow_unpacking", "Дозволити перетворення безкінечних блоків назад у звичайні");
            translationBuilder.add("tooltip.boundless_blocks.remove_picked", "Автоматично видаляти зібрані блоки, якщо є безкінечна версія");
            translationBuilder.add("tooltip.boundless_blocks.keywords", "Блоки з цими словами можна крафтити");
            translationBuilder.add("tooltip.boundless_blocks.blacklist", "Блоки з цими словами ЗАБОРОНЕНІ (пріоритет)");
        }
    }
}