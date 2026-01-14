package com.neuromuser.boundless_blocks.item;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class InfiniteItem extends BlockItem {
    public static final Map<Block, InfiniteItem> INFINITE_ITEMS = new HashMap<>();
    private final Block baseBlock;
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    public InfiniteItem(Block block, Settings settings) {
        super(block, settings.maxCount(1));
        this.baseBlock = block;
    }

    public static boolean isInitialized() {
        return INITIALIZED.get();
    }

    public static void initializeInfiniteItems() {
        if (INITIALIZED.getAndSet(true)) {
            return;
        }

        BoundlessBlocks.LOGGER.info("Starting infinite items registration...");
        long startTime = System.currentTimeMillis();

        int minecraftCount = 0;
        int bopCount = 0;
        int totalBlocks = 0;

        // First, count total blocks to process
        for (Block block : Registries.BLOCK) {
            totalBlocks++;
        }

        BoundlessBlocks.LOGGER.info("Processing {} total blocks", totalBlocks);

        // Process all blocks in the registry
        for (Block block : Registries.BLOCK) {
            Identifier id = Registries.BLOCK.getId(block);

            // Skip air and our own mod
            if (id.getNamespace().equals(BoundlessBlocks.MOD_ID)) continue;

            Item vanillaItem = block.asItem();
            if (vanillaItem == Items.AIR) continue;

            // Only include Minecraft and BOP blocks
            if (id.getNamespace().equals("minecraft") || id.getNamespace().equals("biomesoplenty")) {
                // Check if this is a building block we want
                if (isBuildingBlock(id)) {
                    registerInfiniteBlock(block);

                    if (id.getNamespace().equals("minecraft")) {
                        minecraftCount++;
                    } else {
                        bopCount++;
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        BoundlessBlocks.LOGGER.info("Registered {} items ({} Minecraft, {} BOP) in {} ms",
                minecraftCount + bopCount, minecraftCount, bopCount, endTime - startTime);
    }

    private static boolean isBuildingBlock(Identifier id) {
        String path = id.getPath();
        String namespace = id.getNamespace();

        // Common building block patterns
        return path.contains("planks") ||
                path.contains("log") ||
                path.contains("wood") ||
                path.contains("stripped") ||
                path.contains("bricks") ||
                path.contains("stone") ||
                path.contains("ore") ||
                path.contains("block") ||
                path.contains("slab") ||
                path.contains("stairs") ||
                path.contains("fence") ||
                path.contains("wall") ||
                path.contains("glass") ||
                path.contains("door") ||
                path.contains("trapdoor") ||
                path.contains("tile") ||
                path.contains("cobblestone") ||
                path.contains("mossy") ||
                path.contains("smooth") ||
                path.contains("polished") ||
                path.contains("chiseled") ||
                path.contains("cut") ||
                path.contains("concrete") ||
                path.contains("terracotta") ||
                path.contains("wool") ||
                path.contains("sandstone") ||
                path.contains("prismarine") ||
                path.contains("purpur") ||
                path.contains("quartz") ||
                path.contains("nether") ||
                path.contains("end") ||
                path.contains("blackstone") ||
                path.contains("deepslate") ||
                path.contains("copper") ||
                path.contains("iron") ||
                path.contains("gold") ||
                path.contains("diamond") ||
                path.contains("emerald") ||
                path.contains("lapis") ||
                path.contains("redstone") ||
                path.contains("coal") ||
                path.contains("amethyst");
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult result = super.useOnBlock(context);
        if (result.isAccepted()) {
            context.getStack().setCount(1);
        }
        return result;
    }

    public Item getBaseItem() {
        return this.baseBlock.asItem();
    }

    @Override
    public boolean hasRecipeRemainder() { return true; }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(this, 1);
    }

    @Override
    public boolean hasGlint(ItemStack stack) { return true; }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable("item.boundless_blocks.infinite_format",
                super.getName(stack));
    }

    public static void registerInfiniteBlock(Block block) {
        try {
            Identifier id = Registries.BLOCK.getId(block);

            // Create item ID
            String path = "infinite_" + id.getNamespace() + "_" + id.getPath();
            path = path.replace(':', '_').replace('/', '_');

            Identifier itemId = new Identifier(BoundlessBlocks.MOD_ID, path);

            // Skip if already registered
            if (Registries.ITEM.containsId(itemId)) {
                // Get existing item
                Item existing = Registries.ITEM.get(itemId);
                if (existing instanceof InfiniteItem) {
                    INFINITE_ITEMS.put(block, (InfiniteItem) existing);
                }
                return;
            }

            // Create and register
            InfiniteItem item = new InfiniteItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, itemId, item);

            INFINITE_ITEMS.put(block, item);

        } catch (Exception e) {
            BoundlessBlocks.LOGGER.error("Failed to register infinite block: {}", block, e);
        }
    }
}