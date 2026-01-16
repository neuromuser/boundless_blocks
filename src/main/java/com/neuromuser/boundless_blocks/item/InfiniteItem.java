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
        int skippedCount = 0;
        int registeredCount = 0;

        BoundlessBlocks.LOGGER.info("Starting block scan. Total blocks in registry: {}",
                Registries.BLOCK.stream().count());

        for (Block block : Registries.BLOCK) {
            Identifier id = Registries.BLOCK.getId(block);

            // Skip our own mod's items
            if (id.getNamespace().equals(BoundlessBlocks.MOD_ID)) {
                continue;
            }

            Item vanillaItem = block.asItem();
            if (vanillaItem == Items.AIR) {
                skippedCount++;
                continue;
            }

            String path = id.getPath();
            if (shouldSkipBlock(path)) {
                skippedCount++;
                continue;
            }

            if (registerInfiniteBlock(block)) {registeredCount++;}
        }

        int newCount = INFINITE_ITEMS.size() - beforeCount;
        BoundlessBlocks.LOGGER.info("Initialized {} infinite items (total: {}, skipped: {}, registered: {})",
                newCount, INFINITE_ITEMS.size(), skippedCount, registeredCount);
    }

    private static boolean shouldSkipBlock(String path) {
        if (path.contains("wall_sign") && !path.equals("wall_sign")) {
            return true;
        }

        if (path.contains("potted_") || path.contains("_cauldron")) {
            return true;
        }

        return path.equals("water") || path.equals("lava") || path.equals("fire") ||
                path.equals("portal") || path.equals("end_portal") || path.equals("end_gateway") ||
                path.equals("air") || path.equals("cave_air") || path.equals("void_air");
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

    public static boolean registerInfiniteBlock(Block block) {
        try {
            Identifier id = Registries.BLOCK.getId(block);

            Item blockItem = block.asItem();
            if (blockItem == Items.AIR) {
                BoundlessBlocks.LOGGER.debug("Skipping block without item: {}", id);
                return false;
            }

            String path = "infinite_" + id.getNamespace() + "_" + id.getPath();
            path = path.replace(':', '_').replace('/', '_');

            Identifier itemId = Identifier.of(BoundlessBlocks.MOD_ID, path);

            if (Registries.ITEM.containsId(itemId)) {
                Item existing = Registries.ITEM.get(itemId);
                if (existing instanceof InfiniteItem) {
                    INFINITE_ITEMS.put(block, (InfiniteItem) existing);
                }
                return false;
            }

            InfiniteItem item = new InfiniteItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, itemId, item);

            INFINITE_ITEMS.put(block, item);
            BoundlessBlocks.LOGGER.debug("Registered infinite item: {}", itemId);
            return true;

        } catch (Exception e) {
            BoundlessBlocks.LOGGER.error("Failed to register infinite block: {}",
                    Registries.BLOCK.getId(block), e);
            return false;
        }
    }

    public static void ensureInitialized() {
        if (!INITIALIZED.get()) {
            initializeInfiniteItems(false);
        }
    }
}