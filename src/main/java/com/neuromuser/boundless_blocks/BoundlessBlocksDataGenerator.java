package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.datagen.ModLangGenerator;
import com.neuromuser.boundless_blocks.datagen.ModModelGenerator;
import com.neuromuser.boundless_blocks.datagen.ModRecipeGenerator;
import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BoundlessBlocksDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		// CRITICAL: Initialize infinite items BEFORE creating providers
		// This ensures all mod blocks are registered and available
		System.out.println("=== Initializing Infinite Items for Data Generation ===");
		InfiniteItem.initializeInfiniteItems();
		System.out.println("Total blocks registered: " + InfiniteItem.INFINITE_ITEMS.size());

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelGenerator::new);
		pack.addProvider(ModRecipeGenerator::new);
		pack.addProvider(ModLangGenerator::new);
	}
}