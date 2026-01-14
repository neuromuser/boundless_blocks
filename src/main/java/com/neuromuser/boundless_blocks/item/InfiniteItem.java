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

    /**
     * Initialize infinite items from all registered blocks.
     * Safe to call multiple times - will only initialize once.
     */
    public static void initializeInfiniteItems() {
        initializeInfiniteItems(false);
    }

    /**
     * Initialize infinite items from all registered blocks.
     * @param force If true, will re-scan all blocks even if already initialized
     */
    public static void initializeInfiniteItems(boolean force) {
        if (!force && INITIALIZED.getAndSet(true)) {
            BoundlessBlocks.LOGGER.debug("Infinite items already initialized, skipping");
            return;
        }

        if (force) {
            INITIALIZED.set(true);
            BoundlessBlocks.LOGGER.info("Force re-initializing infinite items");
        }

        int beforeCount = INFINITE_ITEMS.size();

        for (Block block : Registries.BLOCK) {
            Identifier id = Registries.BLOCK.getId(block);

            // Skip air and our own mod
            if (id.getNamespace().equals(BoundlessBlocks.MOD_ID)) continue;

            Item vanillaItem = block.asItem();
            if (vanillaItem == Items.AIR) continue;

            // Register blocks from minecraft and biomesoplenty
            if (id.getNamespace().equals("minecraft") || id.getNamespace().equals("biomesoplenty")) {
                registerInfiniteBlock(block);
            }
        }

        int newCount = INFINITE_ITEMS.size() - beforeCount;
        BoundlessBlocks.LOGGER.info("Initialized {} infinite items (total: {})", newCount, INFINITE_ITEMS.size());
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
