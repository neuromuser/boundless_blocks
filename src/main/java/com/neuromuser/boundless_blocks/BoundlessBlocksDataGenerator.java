package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.datagen.ModLangGenerator;
import com.neuromuser.boundless_blocks.datagen.ModModelGenerator;
import com.neuromuser.boundless_blocks.datagen.ModRecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class BoundlessBlocksDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelGenerator::new); // You already did this
		pack.addProvider(ModRecipeGenerator::new);  // ADD THIS LINE
		pack.addProvider(ModLangGenerator::new);  // ADD THIS LINE


	}
}
