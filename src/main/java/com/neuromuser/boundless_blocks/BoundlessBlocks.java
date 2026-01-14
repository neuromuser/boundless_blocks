package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteItem;
import com.neuromuser.boundless_blocks.recipe.InfiniteCraftingRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BoundlessBlocks implements ModInitializer {
	public static final String MOD_ID = "boundless_blocks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RecipeSerializer<InfiniteCraftingRecipe> INFINITE_CRAFTING_SERIALIZER =
			new SpecialRecipeSerializer<>(InfiniteCraftingRecipe::new);

	private static boolean itemsRegistered = false;

	@Override
	public void onInitialize() {
		BoundlessConfig.load();
		// 1. Register the Recipe Serializer
		Registry.register(Registries.RECIPE_SERIALIZER,
				new Identifier(MOD_ID, "infinite_crafting"),
				INFINITE_CRAFTING_SERIALIZER);

		LOGGER.info("Boundless Blocks initializing...");

		// 2. Register items immediately but use a different approach
		registerAllInfiniteItems();

		// 3. Also register on server start in case new blocks were added
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			if (!itemsRegistered) {
				registerAllInfiniteItems();
			}

			LOGGER.info("=== Boundless Blocks Server Start ===");
			LOGGER.info("Total infinite items: {}", InfiniteItem.INFINITE_ITEMS.size());

			// Log breakdown by namespace
			Map<String, Integer> namespaceCount = new HashMap<>();
			InfiniteItem.INFINITE_ITEMS.forEach((block, item) -> {
				Identifier id = Registries.BLOCK.getId(block);
				namespaceCount.merge(id.getNamespace(), 1, Integer::sum);
			});

			namespaceCount.forEach((namespace, count) -> {
				LOGGER.info("  {}: {} items", namespace, count);
			});
		});
	}

	private void registerAllInfiniteItems() {
		if (itemsRegistered) return;

		LOGGER.info("Registering all infinite items...");
		InfiniteItem.initializeInfiniteItems();
		itemsRegistered = true;
	}
}