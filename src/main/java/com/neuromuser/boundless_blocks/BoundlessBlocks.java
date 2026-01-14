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

	@Override
	public void onInitialize() {
		BoundlessConfig.load();

		// 1. Register Serializer
		Registry.register(Registries.RECIPE_SERIALIZER,
				new Identifier(MOD_ID, "infinite_crafting"),
				INFINITE_CRAFTING_SERIALIZER);

		// 2. IMPORTANT: Run the item registration immediately.
		// We call it here so it happens during the ModInitializer phase.
		InfiniteItem.initializeInfiniteItems(true);

		LOGGER.info("Boundless Blocks: Registered {} items during Init.", InfiniteItem.INFINITE_ITEMS.size());

		// 3. Keep the Server Start event ONLY for logging/debugging, NOT for registering.
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			LOGGER.info("=== Boundless Blocks Registry Check ===");
			LOGGER.info("Final Count: {}", InfiniteItem.INFINITE_ITEMS.size());
		});
	}
}