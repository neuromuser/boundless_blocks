package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteBlockItem;
import com.neuromuser.boundless_blocks.recipe.InfiniteCraftingRecipe;
import com.neuromuser.boundless_blocks.recipe.InfiniteUnpackingRecipe;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundlessBlocks implements ModInitializer {
	public static final String MOD_ID = "boundless_blocks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// The ONE infinite item that uses NBT to store which block it represents
	public static final InfiniteBlockItem INFINITE_BLOCK_ITEM =
			new InfiniteBlockItem(new Item.Settings()
					.maxCount(1));

	public static final RecipeSerializer<InfiniteCraftingRecipe> INFINITE_CRAFTING_SERIALIZER =
			new SpecialRecipeSerializer<>(InfiniteCraftingRecipe::new);

	public static final RecipeSerializer<InfiniteUnpackingRecipe> INFINITE_UNPACKING_SERIALIZER =
			new SpecialRecipeSerializer<>(InfiniteUnpackingRecipe::new);

	@Override
	public void onInitialize() {
		BoundlessConfig.load();

		// Register the single infinite item
		Registry.register(Registries.ITEM,
				new Identifier(MOD_ID, "infinite_block"),
				INFINITE_BLOCK_ITEM);

		// Register recipe serializers
		Registry.register(Registries.RECIPE_SERIALIZER,
				new Identifier(MOD_ID, "infinite_crafting"),
				INFINITE_CRAFTING_SERIALIZER);

		Registry.register(Registries.RECIPE_SERIALIZER,
				new Identifier(MOD_ID, "infinite_unpacking"),
				INFINITE_UNPACKING_SERIALIZER);
	}
}