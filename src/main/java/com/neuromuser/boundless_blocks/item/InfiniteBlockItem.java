package com.neuromuser.boundless_blocks.item;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Infinite item using Polymer to display as the base block item
 */
public class InfiniteBlockItem extends Item implements PolymerItem {
    private static final String BLOCK_ID_KEY = "StoredBlockId";

    public InfiniteBlockItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        // Tell clients to render this as the base block's item
        Block block = getStoredBlock(itemStack);
        if (block != null) {
            return block.asItem();
        }
        return Items.BARRIER; // Fallback
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        // Create a client-side stack that looks like the base block
        Block block = getStoredBlock(itemStack);
        if (block != null) {
            ItemStack clientStack = new ItemStack(block.asItem());

            // Add enchantment glint by adding a fake enchantment
            NbtCompound displayNbt = clientStack.getOrCreateSubNbt("display");
            clientStack.addEnchantment(net.minecraft.enchantment.Enchantments.UNBREAKING, 1);
            // Hide the enchantment from tooltip
            clientStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);

            // Set custom name with infinity symbols
            Text baseName = block.getName();
            Text customName = Text.translatable("item.boundless_blocks.infinite_format", baseName);
            clientStack.setCustomName(customName);

            // Add custom lore for tooltips
            NbtCompound display = clientStack.getOrCreateSubNbt("display");
            net.minecraft.nbt.NbtList lore = new net.minecraft.nbt.NbtList();

            // Add infinite item tooltips
            lore.add(net.minecraft.nbt.NbtString.of(
                    Text.Serializer.toJson(Text.translatable("tooltip.boundless_blocks.infinite_item.line1"))
            ));
            lore.add(net.minecraft.nbt.NbtString.of(
                    Text.Serializer.toJson(Text.translatable("tooltip.boundless_blocks.infinite_item.line2"))
            ));

            // Add block ID
            Identifier id = Registries.BLOCK.getId(block);
            lore.add(net.minecraft.nbt.NbtString.of(
                    Text.Serializer.toJson(Text.literal("§7" + id.toString()))
            ));

            display.put("Lore", lore);

            return clientStack;
        }
        return PolymerItem.super.getPolymerItemStack(itemStack, context, player);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Block block = getStoredBlock(context.getStack());
        if (block == null) {
            return ActionResult.FAIL;
        }

        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();

        // Get the base block's item
        Item baseBlockItem = block.asItem();
        if (!(baseBlockItem instanceof BlockItem blockItem)) {
            return ActionResult.FAIL;
        }

        // Create a temporary stack of the base block item
        ItemStack tempStack = new ItemStack(blockItem, 1);

        // Create a new context with the temporary stack
        // This makes it look like the player is placing a regular block
        BlockPos pos = context.getBlockPos();
        Direction side = context.getSide();

        BlockHitResult hitResult = new BlockHitResult(
                context.getHitPos(),
                side,
                pos,
                context.hitsInsideBlock()
        );

        ItemUsageContext tempContext = new ItemUsageContext(
                player,
                context.getHand(),
                hitResult
        ) {
            @Override
            public ItemStack getStack() {
                return tempStack;
            }
        };

        // Use the BlockItem's place method - this triggers all vanilla events
        // JobsAddon listens to these events!
        ActionResult result = blockItem.useOnBlock(tempContext);

        if (result.isAccepted()) {
            // Block was placed successfully
            // Don't consume the infinite item - keep it at 1
            context.getStack().setCount(1);
        }

        return result;
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return stack.copy();
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public Text getName(ItemStack stack) {
        // This is for server-side display in logs
        Block block = getStoredBlock(stack);
        if (block != null) {
            Text baseName = block.getName();
            return Text.translatable("item.boundless_blocks.infinite_format", baseName);
        }
        return super.getName(stack);
    }

    /**
     * Create an infinite block stack for any block
     */
    public static ItemStack createInfiniteStack(Block block) {
        ItemStack stack = new ItemStack(BoundlessBlocks.INFINITE_BLOCK_ITEM, 1);
        setStoredBlock(stack, block);
        return stack;
    }

    /**
     * Store which block this infinite item represents
     */
    public static void setStoredBlock(ItemStack stack, Block block) {
        if (stack.getItem() instanceof InfiniteBlockItem) {
            NbtCompound nbt = stack.getOrCreateNbt();
            Identifier blockId = Registries.BLOCK.getId(block);
            nbt.putString(BLOCK_ID_KEY, blockId.toString());
        }
    }

    /**
     * Get the block this infinite item represents
     */
    public static Block getStoredBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof InfiniteBlockItem)) {
            return null;
        }

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(BLOCK_ID_KEY)) {
            return null;
        }

        String blockIdStr = nbt.getString(BLOCK_ID_KEY);
        Identifier blockId = Identifier.tryParse(blockIdStr);
        if (blockId == null) {
            return null;
        }

        return Registries.BLOCK.get(blockId);
    }

    /**
     * Check if this is an infinite block item
     */
    public static boolean isInfiniteBlock(ItemStack stack) {
        return stack.getItem() instanceof InfiniteBlockItem && getStoredBlock(stack) != null;
    }
}